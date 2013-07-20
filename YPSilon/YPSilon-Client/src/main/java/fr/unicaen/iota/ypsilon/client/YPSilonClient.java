/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.ypsilon.client;

import fr.unicaen.iota.ypsilon.client.model.User;
import fr.unicaen.iota.ypsilon.client.model.UserCreateIn;
import fr.unicaen.iota.ypsilon.client.model.UserCreateOut;
import fr.unicaen.iota.ypsilon.client.model.UserDeleteIn;
import fr.unicaen.iota.ypsilon.client.model.UserDeleteOut;
import fr.unicaen.iota.ypsilon.client.model.UserInfoIn;
import fr.unicaen.iota.ypsilon.client.model.UserInfoOut;
import fr.unicaen.iota.ypsilon.client.model.UserLookupIn;
import fr.unicaen.iota.ypsilon.client.model.UserLookupOut;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ypsilon.client.soap.YPSilonService;
import fr.unicaen.iota.ypsilon.client.soap.YPSilonServicePortType;
import java.io.File;
import java.io.FileInputStream;
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

/**
 * SOAP Client to interrogate the user directory.
 *
 */
public class YPSilonClient {

    private YPSilonServicePortType port;

    private static final Log log = LogFactory.getLog(YPSilonClient.class);

    public YPSilonClient(String userAddress) {
        this(userAddress, null, null, null, null);
    }

    public YPSilonClient(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        log.trace("new UserClient: " + address);
        try {
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
        YPSilonService service = new YPSilonService(wsdlUrl);
        port = service.getPort(YPSilonServicePortType.class);

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

    /**
     * Fetchs user corresponding to user DN.
     *
     * @param userDN The user to fetch.
     * @return The user corresponding to the user DN.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public UserInfoOut userInfo(String userDN) throws ImplementationExceptionResponse {
        UserInfoIn userInfoIn = new UserInfoIn();
        userInfoIn.setUserDN(userDN);
        return port.userInfo(userInfoIn);
    }

    /**
     * Fetchs list of users corresponding to user ID from the base.
     *
     * @param userID The user to fetch.
     * @return The list of users corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public UserLookupOut userLookup(String userID) throws ImplementationExceptionResponse {
        UserLookupIn userLookupIn = new UserLookupIn();
        userLookupIn.setUserID(userID);
        return port.userLookup(userLookupIn);
    }

    /**
     * Creates a new user in the base.
     *
     * @param userID The user ID of the new user.
     * @param owner The owner of the new user.
     * @param alias The alias DN of the new user. Can be null.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public UserCreateOut userCreate(String userID, String owner, String alias)
            throws ImplementationExceptionResponse {
        UserCreateIn userCreateIn = new UserCreateIn();
        User user = new User();
        user.setUserDN(userID);
        user.setOwner(owner);
        user.setAlias(alias);
        userCreateIn.setUser(user);
        return port.userCreate(userCreateIn);
    }

    /**
     * Creates a new user in the base. It is equivalent to supplying null as the alias parameter to
     * the method <code>userCreate(String, String, String)</code>.
     * See {@link #userCreate(String, String, String)} for a full description.
     *
     * @param userID The user id of the new user.
     * @param owner The owner of the new user.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public UserCreateOut userCreate(String userID, String owner) throws ImplementationExceptionResponse {
        UserCreateIn userCreateIn = new UserCreateIn();
        User userToCreate = new User();
        userToCreate.setUserDN(userID);
        userToCreate.setOwner(owner);
        userToCreate.setAlias(null);
        userCreateIn.setUser(userToCreate);
        return port.userCreate(userCreateIn);
    }

    /**
     * Creates a new user in the base.
     *
     * @param user The user to add.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public UserCreateOut userCreate(User user)
            throws ImplementationExceptionResponse {
        UserCreateIn userCreateIn = new UserCreateIn();
        userCreateIn.setUser(user);
        return port.userCreate(userCreateIn);
    }

    /**
     * Deletes user from the base.
     *
     * @param user The user to delete.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public UserDeleteOut userDelete(String user) throws ImplementationExceptionResponse {
        UserDeleteIn userDeleteIn = new UserDeleteIn();
        userDeleteIn.setUserID(user);
        return port.userDelete(userDeleteIn);
    }
}
