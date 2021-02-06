//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.server.listener;

import static com.sandpolis.core.foundation.Result.ErrorCode.INVALID_ADDRESS;
import static com.sandpolis.core.foundation.Result.ErrorCode.INVALID_CERTIFICATE;
import static com.sandpolis.core.foundation.Result.ErrorCode.INVALID_KEY;
import static com.sandpolis.core.foundation.Result.ErrorCode.INVALID_PORT;
import static com.sandpolis.core.foundation.Result.ErrorCode.OK;
import static com.sandpolis.core.foundation.util.ValidationUtil.ipv4;
import static com.sandpolis.core.instance.state.ListenerOid.ACTIVE;
import static com.sandpolis.core.instance.state.ListenerOid.ADDRESS;
import static com.sandpolis.core.instance.state.ListenerOid.CERTIFICATE;
import static com.sandpolis.core.instance.state.ListenerOid.PORT;
import static com.sandpolis.core.instance.state.ListenerOid.PRIVATE_KEY;

import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.foundation.Result.ErrorCode;
import com.sandpolis.core.foundation.util.CertUtil;
import com.sandpolis.core.foundation.util.NetUtil;
import com.sandpolis.core.foundation.util.ValidationUtil;
import com.sandpolis.core.instance.Core;
import com.sandpolis.core.instance.state.ListenerOid;
import com.sandpolis.core.instance.state.st.STDocument;
import com.sandpolis.core.instance.state.vst.AbstractSTDomainObject;
import com.sandpolis.core.net.util.ChannelUtil;
import com.sandpolis.core.server.channel.ServerChannelInitializer;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ServerChannel;

/**
 * A network listener that binds to a port and handles new connections.
 *
 * @since 1.0.0
 */
public class Listener extends AbstractSTDomainObject {

	public static final Logger log = LoggerFactory.getLogger(Listener.class);

	/**
	 * The listening {@link Channel} that is bound to the listening network
	 * interface.
	 */
	private ServerChannel acceptor;

	/**
	 * The {@link EventLoopGroup} that handles the {@link ServerChannel}.
	 */
	private EventLoopGroup parentLoopGroup;

	/**
	 * The {@link EventLoopGroup} that handles all spawned {@link Channel}s.
	 */
	private EventLoopGroup childLoopGroup;

	Listener(STDocument document) {
		super(document);
	}

	public void start() {
		if (acceptor != null)
			throw new IllegalStateException("The listener is already running");

		NetUtil.serviceName(get(PORT)).ifPresentOrElse(name -> {
			log.debug("Starting listener on port: {} ({})", get(PORT), name);
		}, () -> {
			log.debug("Starting listener on port: {}", get(PORT));
		});

		// Build new loop groups to handle socket events
		parentLoopGroup = ChannelUtil.newEventLoopGroup();
		childLoopGroup = ChannelUtil.newEventLoopGroup();

		ServerBootstrap b = new ServerBootstrap()
				// Set the event loop groups
				.group(parentLoopGroup, childLoopGroup)
				// Set the channel class
				.channel(ChannelUtil.getServerChannelType())
				// Set the number of sockets in the backlog
				.option(ChannelOption.SO_BACKLOG, 128)
				// Set the keep-alive option
				.childOption(ChannelOption.SO_KEEPALIVE, true);

		b.childHandler(new ServerChannelInitializer(config -> {
			config.cvid = Core.cvid();

			if (attribute(ListenerOid.CERTIFICATE).isPresent() && attribute(ListenerOid.PRIVATE_KEY).isPresent()) {
				config.serverTlsWithCert(get(CERTIFICATE), get(PRIVATE_KEY));
			} else {
				config.serverTlsSelfSigned();
			}
		}));

		try {
			acceptor = (ServerChannel) b.bind(get(ADDRESS), get(PORT)).await().channel();
		} catch (InterruptedException e) {
			log.error("Failed to start the listener", e);
			acceptor = null;
			set(ACTIVE, false);
		}
		set(ACTIVE, true);
	}

	/**
	 * Stop the listener, leaving all spawned {@link Channel}s alive.
	 */
	public void stop() {
		if (acceptor == null)
			throw new IllegalStateException("The listener is not running");

		log.debug("Stopping listener on port: {}", get(ListenerOid.PORT));

		try {
			acceptor.close().sync();
		} catch (InterruptedException e) {
			// Ignore
		} finally {
			parentLoopGroup.shutdownGracefully();
			acceptor = null;
			set(ListenerOid.ACTIVE, false);
		}
	}

	@Override
	public ErrorCode valid() {

		if (attribute(ListenerOid.PORT).isPresent() && !ValidationUtil.port(get(ListenerOid.PORT)))
			return INVALID_PORT;
		if (attribute(ListenerOid.ADDRESS).isPresent() && !ipv4(get(ListenerOid.ADDRESS)))
			return INVALID_ADDRESS;
		if (!attribute(ListenerOid.CERTIFICATE).isPresent() && attribute(ListenerOid.PRIVATE_KEY).isPresent())
			return INVALID_CERTIFICATE;
		if (attribute(ListenerOid.CERTIFICATE).isPresent() && !attribute(ListenerOid.PRIVATE_KEY).isPresent())
			return INVALID_KEY;
		if (attribute(ListenerOid.CERTIFICATE).isPresent() && attribute(ListenerOid.PRIVATE_KEY).isPresent()) {
			// Check certificate and key formats
			try {
				CertUtil.parseCert(get(ListenerOid.CERTIFICATE));
			} catch (CertificateException e) {
				return INVALID_CERTIFICATE;
			}

			try {
				CertUtil.parseKey(get(ListenerOid.PRIVATE_KEY));
			} catch (InvalidKeySpecException e) {
				return INVALID_KEY;
			}
		}

		return OK;
	}

	@Override
	public ErrorCode complete() {

		if (!attribute(ListenerOid.PORT).isPresent())
			return INVALID_PORT;
		if (!attribute(ListenerOid.ADDRESS).isPresent())
			return INVALID_ADDRESS;

		return OK;
	}
}
