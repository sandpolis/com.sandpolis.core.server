//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//

plugins {
	id("java-library")
	id("sandpolis-java")
	id("sandpolis-module")
	id("sandpolis-publish")
	id("de.jjohannes.extra-java-module-info") version "0.9"
}

dependencies {
	testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.1")
	testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.1")

	// https://github.com/FasterXML/jackson-databind
	implementation("com.fasterxml.jackson.core:jackson-databind:2.12.4")

	// https://github.com/cilki/zipset
	implementation("com.github.cilki:zipset:1.2.1")

	// https://github.com/jchambers/java-otp
	//implementation("com.eatthepath:java-otp:0.2.0")
	
	// https://github.com/hierynomus/sshj
	implementation("com.hierynomus:sshj:0.30.0")
	
	implementation("org.mongodb:mongodb-driver-sync:4.3.0-beta1")

	if (project.getParent() == null) {
		api("com.sandpolis:core.clientserver:+")
		api("com.sandpolis:core.serveragent:+")
		implementation("com.sandpolis:core.net:+")
		implementation("com.sandpolis:core.instance:+")
	} else {
		api(project(":module:com.sandpolis.core.clientserver"))
		api(project(":module:com.sandpolis.core.serveragent"))
		implementation(project(":module:com.sandpolis.core.net"))
		implementation(project(":module:com.sandpolis.core.instance"))
	}
}

extraJavaModuleInfo {
	automaticModule("sshj-0.30.0.jar", "sshj")
	automaticModule("failureaccess-1.0.1.jar", "failureaccess")
}

sourceSets {
	main {
		java {
			srcDirs("gen/main/java")
		}
	}
}
