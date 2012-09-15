/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

/**
 * SOAP Client to interrogate the user directory.
 *
 */
public class GatewayClient {

    private UserServicePortType servicePort;

    public GatewayClient(String endPointAddress) {
        Service service = Service.create(UserService.SERVICE);
        service.addPort(UserService.UserServicePort, SOAPBinding.SOAP11HTTP_BINDING, endPointAddress);
        servicePort = service.getPort(UserService.UserServicePort, UserServicePortType.class);
    }

    public UserLogoutOut userLogout(String sessionID) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserLogoutIn userLogoutIn = new UserLogoutIn();
        userLogoutIn.setSid(sessionID);
        return servicePort.userLogout(userLogoutIn);
    }

    public UserLoginOut userLogin(String user, String password) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserLoginIn userLoginIn = new UserLoginIn();
        userLoginIn.setUserID(user);
        userLoginIn.setPassword(password);
        return servicePort.userLogin(userLoginIn);
    }

    public UserInfoOut userInfo(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserInfoIn userInfoIn = new UserInfoIn();
        userInfoIn.setUserID(user);
        userInfoIn.setSid(sid);
        return servicePort.userInfo(userInfoIn);
    }

    public UserLookupOut userLookup(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserLookupIn userLookupIn = new UserLookupIn();
        userLookupIn.setUserID(user);
        userLookupIn.setSid(sid);
        return servicePort.userLookup(userLookupIn);
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
        return servicePort.userCreate(userCreateIn);
    }

    public UserDeleteOut userDelete(String sid, String user) throws ImplementationExceptionResponse, SecurityExceptionResponse {
        UserDeleteIn userDeleteIn = new UserDeleteIn();
        userDeleteIn.setSid(sid);
        userDeleteIn.setUserID(user);
        return servicePort.userDelete(userDeleteIn);
    }

}
