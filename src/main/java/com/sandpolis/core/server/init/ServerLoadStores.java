//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.server.init;

import static com.sandpolis.core.instance.plugin.PluginStore.PluginStore;
import static com.sandpolis.core.instance.pref.PrefStore.PrefStore;
import static com.sandpolis.core.instance.profile.ProfileStore.ProfileStore;
import static com.sandpolis.core.instance.state.STStore.STStore;
import static com.sandpolis.core.instance.thread.ThreadStore.ThreadStore;
import static com.sandpolis.core.net.connection.ConnectionStore.ConnectionStore;
import static com.sandpolis.core.net.exelet.ExeletStore.ExeletStore;
import static com.sandpolis.core.net.network.NetworkStore.NetworkStore;
import static com.sandpolis.core.net.stream.StreamStore.StreamStore;
import static com.sandpolis.core.server.banner.BannerStore.BannerStore;
import static com.sandpolis.core.server.group.GroupStore.GroupStore;
import static com.sandpolis.core.server.listener.ListenerStore.ListenerStore;
import static com.sandpolis.core.server.location.LocationStore.LocationStore;
import static com.sandpolis.core.server.trust.TrustStore.TrustStore;
import static com.sandpolis.core.server.user.UserStore.UserStore;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Executors;

import com.sandpolis.core.instance.Entrypoint;
import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.Metatypes.InstanceFlavor;
import com.sandpolis.core.instance.Metatypes.InstanceType;
import com.sandpolis.core.instance.TaskOutcome;
import com.sandpolis.core.instance.state.oid.Oid;
import com.sandpolis.core.instance.state.st.EphemeralDocument;
import com.sandpolis.core.net.util.CvidUtil;
import com.sandpolis.core.server.auth.AuthExe;
import com.sandpolis.core.server.auth.LoginExe;
import com.sandpolis.core.server.banner.BannerExe;
import com.sandpolis.core.server.config.CfgServer;
import com.sandpolis.core.server.group.GroupExe;
import com.sandpolis.core.server.listener.ListenerExe;
import com.sandpolis.core.server.plugin.PluginExe;
import com.sandpolis.core.server.state.STExe;
import com.sandpolis.core.server.stream.StreamExe;
import com.sandpolis.core.server.user.UserExe;

import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.util.concurrent.UnorderedThreadPoolEventExecutor;

public class ServerLoadStores extends InitTask {

	@Override
	public TaskOutcome run(TaskOutcome outcome) throws Exception {
		switch (CfgServer.STORAGE_PROVIDER.value().orElse("mongodb")) {
		case "mongodb":
			// TODO
			CfgServer.MONGODB_DATABASE.value().orElse("Sandpolis");
			CfgServer.MONGODB_HOST.value().orElse("127.0.0.1");
			CfgServer.MONGODB_USER.value().orElse("");
			CfgServer.MONGODB_PASSWORD.value().orElse("");
			break;
		case "embedded":
			// TODO
			break;
		case "ephemeral":
			STStore.init(config -> {
				config.concurrency = 2;
				config.root = new EphemeralDocument(null, null);
			});
			break;
		default:
			break;
		}

		ProfileStore.init(config -> {
			config.collection = Oid.of("/profile").get();
		});

		ThreadStore.init(config -> {
			config.defaults.put("net.exelet", new NioEventLoopGroup(2));
			config.defaults.put("net.connection.outgoing", new NioEventLoopGroup(2));
			config.defaults.put("net.message.incoming", new UnorderedThreadPoolEventExecutor(2));
			config.defaults.put("server.generator", Executors.newCachedThreadPool());
			config.defaults.put("store.event_bus", Executors.newSingleThreadExecutor());
		});

		NetworkStore.init(config -> {
			config.cvid = CvidUtil.cvid(InstanceType.SERVER);
			config.collection = Oid.of("/network_connection").get();
		});

		ConnectionStore.init(config -> {
			config.collection = Oid.of("/connection").get();
		});

		ExeletStore.init(config -> {
			config.exelets = List.of(AuthExe.class, GroupExe.class, ListenerExe.class, LoginExe.class, BannerExe.class,
					UserExe.class, PluginExe.class, StreamExe.class, STExe.class);
		});

		StreamStore.init(config -> {
		});

		PrefStore.init(config -> {
			config.instance = InstanceType.SERVER;
			config.flavor = InstanceFlavor.VANILLA;
		});

		BannerStore.init(config -> {
		});

		UserStore.init(config -> {
			config.collection = Oid.of("/user").get();
		});

		ListenerStore.init(config -> {
			config.collection = Oid.of("/profile//server/listener", Entrypoint.data().uuid()).get();
		});

		GroupStore.init(config -> {
			config.collection = Oid.of("/group").get();
		});

		TrustStore.init(config -> {
			config.collection = Oid.of("/trust_anchor").get();
		});

		PluginStore.init(config -> {
			config.verifier = TrustStore::verifyPluginCertificate;
			config.collection = Oid.of("/profile//plugin", Entrypoint.data().uuid()).get();
		});

		LocationStore.init(config -> {
			config.service = CfgServer.GEOLOCATION_SERVICE.value().orElse(null);
			config.key = CfgServer.GEOLOCATION_SERVICE_KEY.value().orElse(null);
			config.cacheExpiration = Duration.ofDays(10);
		});

		return outcome.success();
	}

	@Override
	public String description() {
		return "Load stores";
	}

}
