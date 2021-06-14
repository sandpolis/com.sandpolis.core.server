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

import com.sandpolis.core.instance.TaskOutcome;
import com.sandpolis.core.instance.init.InstanceLoadConfiguration;
import com.sandpolis.core.server.config.CfgServer;

public class ServerLoadConfiguration extends InstanceLoadConfiguration {

	@Override
	public TaskOutcome run(TaskOutcome outcome) throws Exception {

		CfgServer.DB_PROVIDER.register("hibernate");
		CfgServer.DB_URL.register();
		CfgServer.DB_USERNAME.register();
		CfgServer.DB_PASSWORD.register();

		CfgServer.BANNER_TEXT.register("Welcome to a Sandpolis Server");
		CfgServer.BANNER_IMAGE.register();

		CfgServer.GEOLOCATION_SERVICE.register("ip-api.com");

		return super.run(outcome);
	}
}
