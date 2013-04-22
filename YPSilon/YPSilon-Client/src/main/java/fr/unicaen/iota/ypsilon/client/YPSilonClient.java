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

import fr.unicaen.iota.ypsilon.client.model.UserCertLoginIn;
import fr.unicaen.iota.ypsilon.client.model.UserCreateIn;
import fr.unicaen.iota.ypsilon.client.model.UserCreateOut;
import fr.unicaen.iota.ypsilon.client.model.UserDeleteIn;
import fr.unicaen.iota.ypsilon.client.model.UserDeleteOut;
import fr.unicaen.iota.ypsilon.client.model.UserInfoIn;
import fr.unicaen.iota.ypsilon.client.model.UserInfoOut;
import fr.unicaen.iota.ypsilon.client.model.UserLoginOut;
import fr.unicaen.iota.ypsilon.client.model.UserLogoutIn;
import fr.unicaen.iota.ypsilon.client.model.UserLogoutOut;
import fr.unicaen.iota.ypsilon.client.model.UserLookupIn;
import fr.unicaen.iota.ypsilon.client.model.UserLookupOut;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ypsilon.client.soap.SecurityExceptionResponse;
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
     * Logs out the user corresponding to the session ID.
     * @param sessionID The user's session ID.
     * @return The result of the log out.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If an access error occurred.
     */
    public UserLogoutOut userLogout(String sessionID) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserLogoutIn userLogoutIn = new UserLogoutIn();
        userLogoutIn.setSid(sessionID);
        return port.userLogout(userLogoutIn);
    }

    /**
     * Logs a user by his DN or his user name.
     *
     * @param user The user id (DN).
     * @return The login object created for the user.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If an access error occurred.
     */
    public UserLoginOut userCertLogin(String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserCertLoginIn userCertLoginIn = new UserCertLoginIn();
        userCertLoginIn.setUserID(user);
        return port.userCertLogin(userCertLoginIn);
    }

    /**
     * Fetchs user corresponding to user ID, if the session ID is root access
     * or userInfo is permitted to the user associated to the session.
     *
     * @param sessionId The session ID.
     * @param user The user ID to fetch.
     * @return The user corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public UserInfoOut userInfo(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserInfoIn userInfoIn = new UserInfoIn();
        userInfoIn.setUserID(user);
        userInfoIn.setSid(sid);
        return port.userInfo(userInfoIn);
    }

    /**
     * Fetchs list of users corresponding to user ID from the base, if the session
     * ID is root access or userLookup is permitted to the user associated to the session.
     *
     * @param sessionId The session ID.
     * @param userId The user ID to fetch.
     * @return The list of users corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public UserLookupOut userLookup(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserLookupIn userLookupIn = new UserLookupIn();
        userLookupIn.setUserID(user);
        userLookupIn.setSid(sid);
        return port.userLookup(userLookupIn);
    }

    /**
     * Creates a new user in the base.
     *
     * @param sessionId The session ID corresponding to the connection.
     * @param user The user id of the new user.
     * @param owner The owner of the new user.
     * @param alias The alias DN of the new user. Can be null.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public UserCreateOut userCreate(String sid, String user, String owner, String alias, int time)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserCreateIn userCreateIn = new UserCreateIn();
        userCreateIn.setUserID(user);
        userCreateIn.setSid(sid);
        userCreateIn.setOwnerID(owner);
        userCreateIn.setAlias(alias);
        userCreateIn.setSessionLease(time);
        return port.userCreate(userCreateIn);
    }

    /**
     * Creates a new user in the base. It is equivalent to supplying null as the alias parameter to
     * the method <code>userCreate(String, String, String, String, int)</code>.
     * See {@link #userCreate(String, String, String, String, int)} for a full description.
     *
     * @param sessionId The session ID corresponding to the connection.
     * @param user The user id of the new user.
     * @param owner The owner of the new user.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public UserCreateOut userCreate(String sid, String user, String owner, int time)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserCreateIn userCreateIn = new UserCreateIn();
        userCreateIn.setUserID(user);
        userCreateIn.setSid(sid);
        userCreateIn.setOwnerID(owner);
        userCreateIn.setAlias(null);
        userCreateIn.setSessionLease(time);
        return port.userCreate(userCreateIn);
    }

    /**
     * Deletes user from the base.
     *
     * @param sessionId The session ID corresponding to the connection.
     * @param user The user to delete.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public UserDeleteOut userDelete(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserDeleteIn userDeleteIn = new UserDeleteIn();
        userDeleteIn.setSid(sid);
        userDeleteIn.setUserID(user);
        return port.userDelete(userDeleteIn);
    }
}
