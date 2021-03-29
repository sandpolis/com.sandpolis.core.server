//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.server.config;

import com.sandpolis.core.foundation.config.ConfigProperty;
import com.sandpolis.core.instance.config.DefaultConfigProperty;

public final class CfgServer {

	/**
	 * Whether a server banner will be sent to prospective connections.
	 */
	public static final ConfigProperty<Boolean> BANNER_ENABLED = new DefaultConfigProperty<>(Boolean.class,
			"s7s.banner");

	/**
	 * A path to an image to use in the server banner.
	 */
	public static final ConfigProperty<String> BANNER_IMAGE = new DefaultConfigProperty<>(String.class,
			"s7s.banner.image");

	/**
	 * A greeting message to use in the server banner.
	 */
	public static final ConfigProperty<String> BANNER_TEXT = new DefaultConfigProperty<>(String.class,
			"s7s.banner.text");

	/**
	 * The database user password.
	 */
	public static final ConfigProperty<String> DB_PASSWORD = new DefaultConfigProperty<>(String.class,
			"s7s.database.password");
	/**
	 * The database provider name.
	 */
	public static final ConfigProperty<String> DB_PROVIDER = new DefaultConfigProperty<>(String.class,
			"s7s.database.provider");

	/**
	 * The database URL.
	 */
	public static final ConfigProperty<String> DB_URL = new DefaultConfigProperty<>(String.class, "s7s.database.url");

	/**
	 * The database user username.
	 */
	public static final ConfigProperty<String> DB_USERNAME = new DefaultConfigProperty<>(String.class,
			"s7s.database.username");

	/**
	 * The service to use for geolocation requests.
	 */
	public static final ConfigProperty<String> GEOLOCATION_SERVICE = new DefaultConfigProperty<>(String.class,
			"s7s.geolocation.service");

	/**
	 * The geolocation API key.
	 */
	public static final ConfigProperty<String> GEOLOCATION_SERVICE_KEY = new DefaultConfigProperty<>(String.class,
			"s7s.geolocation.service_key");

	/**
	 * The geolocation request timeout.
	 */
	public static final ConfigProperty<Integer> GEOLOCATION_TIMEOUT = new DefaultConfigProperty<>(Integer.class,
			"s7s.geolocation.timeout");

	/**
	 * The MongoDB database name.
	 */
	public static final ConfigProperty<String> MONGODB_DATABASE = new DefaultConfigProperty<>(String.class,
			"s7s.storage.mongodb.database");

	/**
	 * The MongoDB endpoint address and port.
	 */
	public static final ConfigProperty<String> MONGODB_HOST = new DefaultConfigProperty<>(String.class,
			"s7s.storage.mongodb.host");

	/**
	 * The MongoDB database password.
	 */
	public static final ConfigProperty<String> MONGODB_PASSWORD = new DefaultConfigProperty<>(String.class,
			"s7s.storage.mongodb.password");

	/**
	 * The MongoDB database username.
	 */
	public static final ConfigProperty<String> MONGODB_USER = new DefaultConfigProperty<>(String.class,
			"s7s.storage.mongodb.user");

	/**
	 * The storage provider which may be: mongodb, infinispan_embedded, or
	 * ephemeral.
	 */
	public static final ConfigProperty<String> STORAGE_PROVIDER = new DefaultConfigProperty<>(String.class,
			"s7s.storage.provider");

	private CfgServer() {
	}
}
