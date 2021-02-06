//============================================================================//
//                                                                            //
//                         Copyright © 2015 Sandpolis                         //
//                                                                            //
//  This source file is subject to the terms of the Mozilla Public License    //
//  version 2. You may not use this file except in compliance with the MPL    //
//  as published by the Mozilla Foundation.                                   //
//                                                                            //
//============================================================================//
package com.sandpolis.core.server.trust;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.PKIXParameters;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sandpolis.core.foundation.ConfigStruct;
import com.sandpolis.core.foundation.util.CertUtil;
import com.sandpolis.core.instance.state.TrustAnchorOid;
import com.sandpolis.core.instance.state.st.STDocument;
import com.sandpolis.core.instance.store.ConfigurableStore;
import com.sandpolis.core.instance.store.STCollectionStore;
import com.sandpolis.core.server.trust.TrustStore.TrustStoreConfig;

/**
 * The {@link TrustStore} contains trust anchors for plugin certificate
 * authorities.
 *
 * @author cilki
 * @since 5.0.0
 */
public final class TrustStore extends STCollectionStore<TrustAnchor> implements ConfigurableStore<TrustStoreConfig> {

	@ConfigStruct
	public static final class TrustStoreConfig {

		public STDocument collection;
	}

	private static final Logger log = LoggerFactory.getLogger(TrustStore.class);

	/**
	 * The global context {@link TrustStore}.
	 */
	public static final TrustStore TrustStore = new TrustStore();

	public TrustStore() {
		super(log, TrustAnchor::new);
	}

	@Override
	public void init(Consumer<TrustStoreConfig> configurator) {
		var config = new TrustStoreConfig();
		configurator.accept(config);

		setDocument(config.collection);

		// Install root CA if required
		if (getMetadata().getInitCount() == 1) {
			create(anchor -> {
				anchor.set(TrustAnchorOid.NAME, "PLUGIN CA");
				anchor.set(TrustAnchorOid.CERTIFICATE, CertUtil.getPluginRoot());
			});
		}
	}

	/**
	 * Verify a plugin certificate against the trust anchors in the store.
	 *
	 * @param cert The plugin's certificate
	 * @return Whether the certificate could be validated
	 */
	public boolean verifyPluginCertificate(X509Certificate cert) {
		Objects.requireNonNull(cert);

		PKIXParameters params;
		try (Stream<TrustAnchor> stream = values().stream()) {
			params = new PKIXParameters(
					stream.map(t -> new java.security.cert.TrustAnchor(t.get(TrustAnchorOid.CERTIFICATE), null))
							.collect(Collectors.toSet()));
			params.setRevocationEnabled(false);
		} catch (InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}

		try {
			CertPathValidator.getInstance("PKIX")
					.validate(CertificateFactory.getInstance("X.509").generateCertPath(List.of(cert)), params);
		} catch (CertPathValidatorException | CertificateException e) {
			return false;
		} catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}

		log.debug("Successfully verified certificate: {}", cert.getSerialNumber());
		return true;
	}
}
