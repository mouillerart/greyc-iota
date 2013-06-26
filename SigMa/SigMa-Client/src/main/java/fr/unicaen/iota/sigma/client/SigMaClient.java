/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.sigma.client;

import fr.unicaen.iota.sigma.model.Principal;
import fr.unicaen.iota.sigma.model.Verification;
import fr.unicaen.iota.sigma.soap.SigMaService;
import fr.unicaen.iota.sigma.soap.SigMaServicePortType;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 * This <code>SigMAClient</code> performs the requests to the SigMA web service. This
 * class receives the address of the SigMA web service and sends the signature
 * verification resquests.
 */
public class SigMaClient {

    private static final Log log = LogFactory.getLog(SigMaClient.class);
    private SigMaServicePortType port;

    public SigMaClient(String address) {
        this(address, null, null, null, null);
    }

    public SigMaClient(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        try {
            this.configureService(address, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        } catch (MalformedURLException ex) {
            log.error(ex.getMessage(), ex);
        } catch (Exception e) {
            throw new RuntimeException("Can’t configure service: " + e.getMessage(), e);
        }
    }

    public void configureService(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) throws MalformedURLException, Exception {
        if (pksFilename != null && pksPassword != null && trustPksFilename != null && trustPksPassword != null) {
            System.setProperty("javax.net.ssl.keyStore", pksFilename);
            System.setProperty("javax.net.ssl.keyStorePassword", pksPassword);
            System.setProperty("javax.net.ssl.trustStore", trustPksFilename);
            System.setProperty("javax.net.ssl.trustStorePassword", trustPksPassword);
        }
        URL wsdlUrl = new URL(address + "?wsdl");
        SigMaService service = new SigMaService(wsdlUrl);
        port = service.getPort(SigMaServicePortType.class);

        // turn off chunked transfer encoding
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpConduit.setClient(httpClientPolicy);

        if (pksFilename != null) {
            log.debug("Authenticating with certificate in file: " + pksFilename);

            if (!wsdlUrl.getProtocol().equalsIgnoreCase("https")) {
                throw new Exception("Authentication method requires the use of HTTPS");
            }

            KeyStore keyStore = KeyStore.getInstance(pksFilename.endsWith(".p12") ? "PKCS12" : "JKS");
            keyStore.load(new FileInputStream(new File(pksFilename)), pksPassword.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, pksPassword.toCharArray());

            KeyStore trustStore = KeyStore.getInstance(trustPksFilename.endsWith(".p12") ? "PKCS12" : "JKS");
            trustStore.load(new FileInputStream(new File(trustPksFilename)), trustPksPassword.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);

            TLSClientParameters tlscp = new TLSClientParameters();
            tlscp.setSecureRandom(new SecureRandom());
            tlscp.setKeyManagers(keyManagerFactory.getKeyManagers());
            tlscp.setTrustManagers(trustManagerFactory.getTrustManagers());

            httpConduit.setTlsClientParameters(tlscp);
        }
    }

    public Verification verify(Principal principal) {
        Verification verification = port.verify(principal);
        return verification;
    }

    public Verification verify(EPCISEventType event) {
        Principal principal = new Principal();
        principal.setEvent(event);
        return verify(principal);
    }
}
