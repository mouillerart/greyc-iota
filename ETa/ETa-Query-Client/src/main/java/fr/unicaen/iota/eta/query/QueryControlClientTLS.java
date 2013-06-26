/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.eta.query;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.utils.AuthenticationType;

/**
 *
 */
public class QueryControlClientTLS extends QueryControlClient {

    private static final Log log = LogFactory.getLog(QueryControlClientTLS.class);
    private final X509TrustManager x509TrustManager;

    public QueryControlClientTLS(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) throws KeyStoreException {
        super(address, new Object[]{AuthenticationType.HTTPS_WITH_CLIENT_CERT, pksFilename, pksPassword});
        KeyStore trustStore = KeyStore.getInstance(trustPksFilename.endsWith(".p12") ? "PKCS12" : "JKS");
        try {
            trustStore.load(new FileInputStream(new File(trustPksFilename)), trustPksPassword.toCharArray());
        } catch (Exception ex) {
            log.fatal("Couldn’t load the TrustStore " + trustPksFilename, ex);
        }
        TrustManagerFactory trustManagerFactory = null;
        try {
            trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        } catch (NoSuchAlgorithmException ex) {
            log.fatal("Couldn’t find SunX509", ex);
        }
        trustManagerFactory.init(trustStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

        X509TrustManager found = null;
        for (TrustManager trustManager : trustManagers) {
            if (trustManager instanceof X509TrustManager) {
                found = (X509TrustManager) trustManager;
                break;
            }
        }
        if (found == null) {
            log.fatal("Couldn’t find a X509TrustManager");
        }
        x509TrustManager = found;
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        x509TrustManager.checkServerTrusted(chain, authType);
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        x509TrustManager.checkServerTrusted(chain, authType);
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return x509TrustManager.getAcceptedIssuers();
    }
}
