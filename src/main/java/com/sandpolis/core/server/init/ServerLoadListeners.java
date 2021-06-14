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

import static com.sandpolis.core.server.listener.ListenerStore.ListenerStore;

import com.sandpolis.core.instance.InitTask;
import com.sandpolis.core.instance.TaskOutcome;

public class ServerLoadListeners extends InitTask {

	@Override
	public TaskOutcome run(TaskOutcome outcome) throws Exception {
		ListenerStore.start();

		return outcome.success();
	}

	@Override
	public String description() {
		return "Load socket listeners";
	}

}
