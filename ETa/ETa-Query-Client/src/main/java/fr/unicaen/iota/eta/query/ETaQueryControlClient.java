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
package fr.unicaen.iota.eta.query;

import fr.unicaen.iota.eta.soap.IDedEPCISServicePortType;
import fr.unicaen.iota.eta.soap.IDedEPCglobalEPCISService;
import fr.unicaen.iota.tau.model.Identity;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.List;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManagerFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.configuration.jsse.TLSClientParameters;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.queryclient.QueryControlInterface;
import org.fosstrak.epcis.soap.*;

/**
 *
 */
public class ETaQueryControlClient implements QueryControlInterface {

    private Identity identity;
    private IDedEPCISServicePortType port;
    private QueryControlClient epcisClient;
    private static final Log log = LogFactory.getLog(ETaQueryControlClient.class);

    public ETaQueryControlClient(Identity id, String address) {
        this(id, address, null, null, null, null);
    }

    public ETaQueryControlClient(Identity id, String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        log.trace("new ETaClient: " + id + " @ " + address);
        this.identity = id;
        try {
            this.epcisClient = new QueryControlClientTLS(address, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
            configureService(address, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        } catch (Exception e) {
            throw new RuntimeException("Can’t configure service: " + e.getMessage(), e);
        }
    }

    public void configureService(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) throws Exception {
        if (pksFilename != null && pksPassword != null && trustPksFilename != null && trustPksPassword != null) {
            System.setProperty("javax.net.ssl.keyStore", pksFilename);
            System.setProperty("javax.net.ssl.keyStorePassword", pksPassword);
            System.setProperty("javax.net.ssl.trustStore", trustPksFilename);
            System.setProperty("javax.net.ssl.trustStorePassword", trustPksPassword);
        }
        URL wsdlUrl = new URL(address + "?wsdl");
        IDedEPCglobalEPCISService service = new IDedEPCglobalEPCISService(wsdlUrl);
        port = service.getPort(IDedEPCISServicePortType.class);

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

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity id) {
        this.identity = id;
    }

    @Override
    public QueryResults poll(Poll poll) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        return port.iDedPoll(poll, identity);
    }

    @Override
    public void subscribe(Subscribe subscribe) throws DuplicateSubscriptionExceptionResponse, ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse, InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse, NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        port.iDedSubscribe(subscribe, identity);
    }

    @Override
    public void unsubscribe(String subscriptionId) throws ImplementationExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        Unsubscribe unsubscribe = new Unsubscribe();
        unsubscribe.setSubscriptionID(subscriptionId);
        port.iDedUnsubscribe(unsubscribe, identity);
    }

    @Override
    public List<String> getQueryNames() throws ImplementationExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse {
        return epcisClient.getQueryNames();
    }

    @Override
    public List<String> getSubscriptionIds(String queryName) throws ImplementationExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        GetSubscriptionIDs gsubids = new GetSubscriptionIDs();
        gsubids.setQueryName(queryName);
        return port.iDedGetSubscriptionIDs(gsubids, identity).getString();
    }

    @Override
    public String getStandardVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse {
        return epcisClient.getStandardVersion();
    }

    @Override
    public String getVendorVersion() throws ImplementationExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse {
        return epcisClient.getVendorVersion();
    }
}
