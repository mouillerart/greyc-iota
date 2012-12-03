/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.tau.xi;

//import fr.unicaen.iota.tau.model.Principal;
import fr.unicaen.iota.tau.model.Identity;
import fr.unicaen.iota.tau.soap.TAuXiService;
import fr.unicaen.iota.tau.soap.TAuXiServicePortType;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;

public final class TAuXiClient implements X509TrustManager {

    private static final Log log = LogFactory.getLog(TAuXiClient.class);
    private TAuXiServicePortType port;

    public TAuXiClient(String address) {
        this(address, null, null);
    }

    public TAuXiClient(String address, String pksFilename, String pksPassword) {
        // TODO: fake Xi
        try {
            //configureService(address, pksFilename, pksPassword);
        } catch (Exception e) {
            throw new RuntimeException("Can’t configure service: " + e.getMessage(), e);
        }
    }

    // TODO: TLS
    public void configureService(String address, String pksFilename, String pksPassword) throws Exception {
        URL wsdlUrl = new URL(address + "?wsdl");
        TAuXiService service = new TAuXiService(wsdlUrl);
        port = service.getPort(TAuXiServicePortType.class);

        // turn off chunked transfer encoding
        Client client = ClientProxy.getClient(port);
        HTTPConduit httpConduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
        httpClientPolicy.setAllowChunking(false);
        httpConduit.setClient(httpClientPolicy);

        // TODO: TLS
        if (pksFilename != null) {
            //log.debug("Authenticating with certificate in file: " + pksFilename);

            if (!wsdlUrl.getProtocol().equalsIgnoreCase("https")) {
                throw new Exception("Authentication method requires the use of HTTPS");
            }

            KeyStore keyStore = KeyStore.getInstance(pksFilename.endsWith(".p12") ? "PKCS12" : "JKS");
            keyStore.load(new FileInputStream(new File(pksFilename)), pksPassword.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, pksPassword.toCharArray());

            TLSClientParameters tlscp = new TLSClientParameters();
            tlscp.setKeyManagers(keyManagerFactory.getKeyManagers());
            tlscp.setSecureRandom(new SecureRandom());
            tlscp.setDisableCNCheck(true);
            tlscp.setTrustManagers(new TrustManager[]{this});

            httpConduit.setTlsClientParameters(tlscp);
        }
    }

    public boolean canBe(java.security.Principal principal, Identity identity) {
        if (principal == null || identity == null) {
            return false;
        }
        /* TODO: fake Xi
         * Principal tauPrincipal = new Principal();
         * tauPrincipal.setName(principal.getName());
         * tauPrincipal.setHashCode(principal.hashCode());
         * tauPrincipal.setAsString(principal.toString()); return
         * port.canBe(tauPrincipal, identity);
         */
        return principal.toString().equals(identity.getAsString());
    }

    @Override
    public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
