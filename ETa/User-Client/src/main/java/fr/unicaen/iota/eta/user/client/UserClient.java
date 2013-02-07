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
package fr.unicaen.iota.eta.user.client;

import fr.unicaen.iota.eta.user.userservice.*;
import fr.unicaen.iota.eta.user.userservice_wsdl.ImplementationExceptionResponse;
import fr.unicaen.iota.eta.user.userservice_wsdl.SecurityExceptionResponse;
import fr.unicaen.iota.eta.user.userservice_wsdl.UserService;
import fr.unicaen.iota.eta.user.userservice_wsdl.UserServicePortType;
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
public class UserClient {

    private UserServicePortType port;

    private static final Log log = LogFactory.getLog(UserClient.class);

    public UserClient(String userAddress) {
        this(userAddress, null, null, null, null);
    }

    public UserClient(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        log.trace("new UserClient: " + address);
        try {
            configureService(address, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        } catch (Exception e) {
            throw new RuntimeException("Can’t configure service: " + e.getMessage(), e);
        }
    }

    public void configureService(String address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", pksFilename);
        System.setProperty("javax.net.ssl.keyStorePassword", pksPassword);
        System.setProperty("javax.net.ssl.trustStore", trustPksFilename);
        System.setProperty("javax.net.ssl.trustStorePassword", trustPksPassword);
        URL wsdlUrl = new URL(address + "?wsdl");
        UserService service = new UserService(wsdlUrl);
        port = service.getPort(UserServicePortType.class);

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

    public UserLogoutOut userLogout(String sessionID) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserLogoutIn userLogoutIn = new UserLogoutIn();
        userLogoutIn.setSid(sessionID);
        return port.userLogout(userLogoutIn);
    }

    public UserLoginOut userBasicLogin(String user, String password) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserBasicLoginIn userBasicLoginIn = new UserBasicLoginIn();
        userBasicLoginIn.setUserID(user);
        userBasicLoginIn.setPassword(password);
        return port.userBasicLogin(userBasicLoginIn);
    }

    public UserLoginOut userCertLogin(String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserCertLoginIn userCertLoginIn = new UserCertLoginIn();
        userCertLoginIn.setUserID(user);
        return port.userCertLogin(userCertLoginIn);
    }

    public UserInfoOut userInfo(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserInfoIn userInfoIn = new UserInfoIn();
        userInfoIn.setUserID(user);
        userInfoIn.setSid(sid);
        return port.userInfo(userInfoIn);
    }

    public UserLookupOut userLookup(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserLookupIn userLookupIn = new UserLookupIn();
        userLookupIn.setUserID(user);
        userLookupIn.setSid(sid);
        return port.userLookup(userLookupIn);
    }

    public UserCreateOut userCreate(String sid, String user, String password, String partner, int time)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserCreateIn userCreateIn = new UserCreateIn();
        userCreateIn.setUserID(user);
        userCreateIn.setSid(sid);
        userCreateIn.setPassword(password);
        userCreateIn.setPartnerID(partner);
        userCreateIn.setLoginMode(TLoginMode.KEY_AND_PASSWORD);
        userCreateIn.setSessionLease(time);
        return port.userCreate(userCreateIn);
    }

    public UserDeleteOut userDelete(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserDeleteIn userDeleteIn = new UserDeleteIn();
        userDeleteIn.setSid(sid);
        userDeleteIn.setUserID(user);
        return port.userDelete(userDeleteIn);
    }
}
