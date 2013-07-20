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
package fr.unicaen.iota.ypsilon;

import fr.unicaen.iota.ypsilon.client.model.*;
import fr.unicaen.iota.ypsilon.client.soap.*;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class YPSilonWebService implements YPSilonServicePortType {

    private static final Log LOG = LogFactory.getLog(YPSilonWebService.class);
    private UserOperationsModule userModule;

    public YPSilonWebService() {
        this.userModule = new UserOperationsModule();
    }

    @Override
    public UserLookupOut userLookup(UserLookupIn parms) throws ImplementationExceptionResponse {
        if (parms.getUserID() == null) {
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
        List<User> userList = userModule.userLookup(parms.getUserID());
        out.getUserList().addAll(userList);
        return out;
    }

    @Override
    public UserCreateOut userCreate(UserCreateIn parms) throws ImplementationExceptionResponse {
        if (parms.getUser() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userCreate");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        UserCreateOut out = new UserCreateOut();
        userModule.userCreate(parms.getUser().getUserDN(), parms.getUser().getOwner(), parms.getUser().getAlias());
        return out;
    }

    @Override
    public UserDeleteOut userDelete(UserDeleteIn parms) throws ImplementationExceptionResponse {
        if (parms.getUserID() == null) {
            String msg = "A parameter is missing.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userDelete");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        UserDeleteOut out = new UserDeleteOut();
        userModule.userDelete(parms.getUserID());
        return out;
    }

    @Override
    public UserUpdateOut userUpdate(UserUpdateIn parms) throws ImplementationExceptionResponse {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public UserInfoOut userInfo(UserInfoIn parms) throws ImplementationExceptionResponse {
        if (parms.getUserDN() == null) {
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
            User user = userModule.userInfo(parms.getUserDN());
            out.setUser(user);
            return out;
        } catch (ImplementationExceptionResponse ier) {
            throw ier;
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
