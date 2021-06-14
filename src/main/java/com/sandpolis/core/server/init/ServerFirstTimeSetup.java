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

import static com.sandpolis.core.server.group.GroupStore.GroupStore;
import static com.sandpolis.core.server.listener.ListenerStore.ListenerStore;
import static com.sandpolis.core.server.user.UserStore.UserStore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.instance.Group.GroupConfig;
import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.Listener.ListenerConfig;
import com.sandpolis.core.instance.TaskOutcome;
import com.sandpolis.core.instance.User.UserConfig;

public class ServerFirstTimeSetup extends InitTask {

	private static final Logger log = LoggerFactory.getLogger(ServerFirstTimeSetup.class);

	@Override
	public String description() {
		return "First time initialization";
	}

	@Override
	public TaskOutcome run(TaskOutcome outcome) throws Exception {
		boolean skipped = true;

		// Setup default users
		if (UserStore.getMetadata().getInitCount() == 1) {
			log.debug("Creating default users");
			UserStore.create(UserConfig.newBuilder().setUsername("admin").setPassword("password").build());
			skipped = false;
		}

		// Setup default listeners
		if (ListenerStore.getMetadata().getInitCount() == 1) {
			log.debug("Creating default listeners");
			ListenerStore.create(ListenerConfig.newBuilder().setPort(8768).setAddress("0.0.0.0").setOwner("admin")
					.setName("Default Listener").setEnabled(true).build());
			skipped = false;
		}

		// Setup default groups
		if (GroupStore.getMetadata().getInitCount() == 1) {
			log.debug("Creating default groups");
			GroupStore
					.create(GroupConfig.newBuilder().setName("Default Authentication Group").setOwner("admin").build());
			skipped = false;
		}

		if (skipped)
			return outcome.skipped();

		return outcome.success();
	}

}
