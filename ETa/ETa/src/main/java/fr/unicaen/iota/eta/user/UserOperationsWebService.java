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
package fr.unicaen.iota.eta.user;

import fr.unicaen.iota.eta.constants.Constants;
import fr.unicaen.iota.eta.user.userservice.*;
import fr.unicaen.iota.eta.user.userservice_wsdl.ImplementationExceptionResponse;
import fr.unicaen.iota.eta.user.userservice_wsdl.SecurityExceptionResponse;
import fr.unicaen.iota.eta.user.userservice_wsdl.UserServicePortType;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserOperationsWebService implements UserServicePortType {

    private static final Log LOG = LogFactory.getLog(UserOperationsWebService.class);
    private UserOperationsModule userModule;

    public UserOperationsWebService() {
        this.userModule = new UserOperationsModule();
    }

    @Override
    public HelloOut hello(HelloIn parms) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserLookupOut userLookup(UserLookupIn parms)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        if (parms.getUserID() == null || parms.getSid() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLookup");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        UserLookupOut out = new UserLookupOut();
        if (!Session.isValidSession(parms.getSid())) {
            String msg = "It is not a valid session.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLookup");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        List<User> userList = userModule.userLookup(parms.getSid(), parms.getUserID());
        TUserItemList userItemList = new TUserItemList();
        for (User u : userList) {
            TUserItem uItem = new TUserItem();
            uItem.setId(u.getUserID());
            userItemList.getUser().add(uItem);
        }
        out.setUserList(userItemList);
        TResult tresult = new TResult();
        tresult.setDesc("userLookup command successfull.");
        out.setResult(tresult);
        return out;
    }

    @Override
    public UserCreateOut userCreate(UserCreateIn parms)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        if (parms.getUserID() == null || parms.getPartnerID() == null || parms.getPassword() == null
                || parms.getLoginMode() == null || parms.getSid() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userCreate");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        String sessionId = parms.getSid();
        if (!Session.isValidSession(sessionId)) {
            String msg = "It is not a valid session.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userCreate");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        UserCreateOut out = new UserCreateOut();
        userModule.userCreate(sessionId, parms.getUserID(), parms.getPassword(), parms.getPartnerID());
        TResult tresult = new TResult();
        tresult.setDesc("userCreate command successfull.");
        out.setResult(tresult);
        return out;
    }

    @Override
    public UserDeleteOut userDelete(UserDeleteIn parms)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        if (parms.getUserID() == null || parms.getSid() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userDelete");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        String sessionId = parms.getSid();
        if (!Session.isValidSession(sessionId)) {
            String msg = "It is not a valid session.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userDelete");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        UserDeleteOut out = new UserDeleteOut();
        userModule.userDelete(sessionId, parms.getUserID());
        TResult tresult = new TResult();
        tresult.setDesc("userDelete command successfull.");
        out.setResult(tresult);
        return out;
    }

    @Override
    public UserUpdateOut userUpdate(UserUpdateIn parms)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserLogoutOut userLogout(UserLogoutIn parms)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        if (parms.getSid() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLogout");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        try {
            UserLogoutOut out = new UserLogoutOut();
            if (!Session.isValidSession(parms.getSid())) {
                String msg = "It is not a valid session.";
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg);
                ie.setQueryName("userLogout");
                ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
                LOG.error(msg, ier);
                throw ier;
            }
            Session.closeSession(parms.getSid());
            TResult tresult = new TResult();
            tresult.setDesc("Session closed.");
            out.setResult(tresult);
            return out;
        } catch (Exception e) {
            String msg = "An unexpected error occurred while closing session.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLogout");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, e);
            LOG.error(msg, ier);
            throw ier;
        }
    }

    @Override
    public UserLoginOut userLogin(UserLoginIn parms)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        if (parms.getUserID() == null || parms.getPassword() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLogin");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        UserLoginOut out = new UserLoginOut();
        User user = userModule.userLogin(parms.getUserID(), parms.getPassword());
        out.setSessionLease(Constants.SESSION_TIME_LEASE);
        String sessionId = Session.openSession(user);
        out.setSid(sessionId);
        return out;
    }

    @Override
    public UserInfoOut userInfo(UserInfoIn parms)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        if (parms.getUserID() == null || parms.getSid() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userInfo");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        UserInfoOut out = new UserInfoOut();
        try {
            String sessionId = parms.getSid();
            if (!Session.isValidSession(sessionId)) {
                String msg = "It is not a valid session.";
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg);
                ie.setQueryName("userInfo");
                ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
                LOG.error(msg, ier);
                throw ier;
            }
            User user = userModule.userInfo(sessionId, parms.getUserID());
            out.setSessionLease(Constants.SESSION_TIME_LEASE);
            out.setLoginMode(TLoginMode.KEY_AND_PASSWORD);
            out.setPartnerID(user.getPartnerID());
            out.setUserID(user.getUserID());
            TResult tresult = new TResult();
            tresult.setDesc("Command userInfo successfull.");
            out.setResult(tresult);
            return out;
        } catch (ImplementationExceptionResponse ier) {
            throw ier;
        } catch (SecurityExceptionResponse ser) {
            throw ser;
        } catch (Exception ex) {
            String msg = "An unexpected error occurred while closing session.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userInfo");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
            throw ier;
        }
    }
}
