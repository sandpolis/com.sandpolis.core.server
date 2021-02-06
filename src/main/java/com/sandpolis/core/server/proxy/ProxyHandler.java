//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.server.proxy;

import static com.sandpolis.core.net.connection.ConnectionStore.ConnectionStore;

import com.sandpolis.core.instance.state.ConnectionOid;
import com.sandpolis.core.net.Message.MSG;
import com.sandpolis.core.net.channel.ChannelConstant;
import com.sandpolis.core.net.channel.HandlerKey;
import com.sandpolis.core.net.msg.MsgNetwork.EV_EndpointClosed;
import com.sandpolis.core.net.util.MsgUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.util.ReferenceCountUtil;

/**
 * {@link ProxyHandler} reads the first two fields of an incoming {@link MSG} to
 * determine its destination. If the destination is another instance, the
 * {@link ByteBuf} will be efficiently forwarded without decoding the entire
 * message. Otherwise the {@link ByteBuf} will be decoded and executed for this
 * instance.
 *
 * <p>
 * This handler MUST be placed after a {@link ProtobufVarint32FrameDecoder}!
 * Otherwise a malicious instance could send messages to unauthorized instances
 * by sending two messages in rapid succession. Since this handler only verifies
 * the first few bytes and then routes the entire buffer accordingly, sending
 * one small valid message followed by an invalid message would lead to the
 * invalid message being delivered to the receiver.
 *
 * @since 5.0.0
 */
@Sharable
public class ProxyHandler extends SimpleChannelInboundHandler<ByteBuf> {

	/**
	 * The server's CVID which is used in determining when to route messages.
	 */
	private final int cvid;

	public ProxyHandler(int cvid) {
		this.cvid = cvid;
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
		msg.markReaderIndex();

		// Read field: "to"
		if (msg.isReadable() && msg.readByte() == 0x08) {
			int to = readCvid(msg);

			// Perform redirection if necessary
			if (to != cvid) {

				// Read field: "from"
				if (msg.isReadable() && msg.readByte() == 0x10) {
					int from = readCvid(msg);

					// Verify the from field to prevent spoofing
					if (ctx.channel().attr(ChannelConstant.SOCK).get().get(ConnectionOid.REMOTE_CVID) != from) {
						throw new ChannelException("Message 'from' does not match channel's CVID");
					}
				} else {
					throw new ChannelException("Message specifies 'to' but not 'from'");
				}

				// Route the message
				var sock = ConnectionStore.getByCvid(to);
				if (sock.isPresent()) {
					msg.resetReaderIndex();
					msg.retain();

					// Skip to the middle of the pipeline
					sock.get().getHandler(HandlerKey.FRAME_ENCODER).get().shortcut(msg);
				} else {
					ctx.channel().writeAndFlush(MsgUtil.ev(0, EV_EndpointClosed.newBuilder().setCvid(to).build()));
				}

				return;
			}
		}

		msg.resetReaderIndex();
		ReferenceCountUtil.retain(msg);
		ctx.fireChannelRead(msg);
	}

	/**
	 * Read a CVID varint from the given {@link ByteBuf}.
	 *
	 * @param buffer The buffer to read (read pointer will be modified)
	 * @return The decoded CVID
	 */
	private static int readCvid(ByteBuf buffer) {
		byte tmp = buffer.readByte();
		if (tmp >= 0) {
			return tmp;
		} else {
			int result = tmp & 127;
			if ((tmp = buffer.readByte()) >= 0) {
				result |= tmp << 7;
			} else {
				result |= (tmp & 127) << 7;
				if ((tmp = buffer.readByte()) >= 0) {
					result |= tmp << 14;
				} else {
					result |= (tmp & 127) << 14;
					if ((tmp = buffer.readByte()) >= 0) {
						result |= tmp << 21;
					} else {
						result |= (tmp & 127) << 21;
						result |= (tmp = buffer.readByte()) << 28;
						if (tmp < 0) {
							// The CVID is negative (negative varints are always 10 bytes)
							throw new CorruptedFrameException("Invalid CVID");
						}
					}
				}
			}
			if (result < 0)
				throw new CorruptedFrameException("Invalid CVID");

			return result;
		}
	}
}
