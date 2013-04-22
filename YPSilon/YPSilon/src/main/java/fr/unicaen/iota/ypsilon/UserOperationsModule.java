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

import fr.unicaen.iota.ypsilon.constants.Constants;
import fr.unicaen.iota.xi.client.UserPEP;
import fr.unicaen.iota.xi.utils.Utils;
import fr.unicaen.iota.ypsilon.client.model.ImplementationException;
import fr.unicaen.iota.ypsilon.client.model.SecurityException;
import fr.unicaen.iota.ypsilon.client.model.ImplementationExceptionSeverity;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ypsilon.client.soap.SecurityExceptionResponse;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserOperationsModule {

    private UserPEP userPep;
    private static final Log LOG = LogFactory.getLog(UserOperationsModule.class);
    private UserOperations backend;

    public UserOperationsModule() {
        this.backend = new UserOperations();
        this.userPep = new UserPEP(Constants.XACML_URL, Constants.PKS_FILENAME, Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
    }

    /**
     * Creates a new user in the base.
     *
     * @param sessionId The session ID corresponding to the connection.
     * @param login The login of the new user.
     * @param owner The owner of the new user.
     * @param alias The alias DN of the new user. Can be null.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public void userCreate(String sessionId, String login, String owner, String alias)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        String userId = Session.getUser(sessionId).getUserID();
        int resp = userPep.userCreate(userId, owner);
        if (!Utils.responseIsPermit(resp) && !isRootAccess(sessionId)) {
            String msg = "Acces denied.";
            SecurityException se = new SecurityException();
            se.setReason(msg);
            se.setQueryName("userCreate");
            SecurityExceptionResponse ser = new SecurityExceptionResponse(msg, se);
            LOG.error(msg, ser);
            throw ser;
        }
        String userToCheck = (alias != null && !alias.isEmpty())? alias : login;
        if (!backend.userLookup(userToCheck).isEmpty()) {
            String msg = "User already exists.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userCreate");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        backend.userCreate(login, owner, alias);
    }

    /**
     * Deletes user from the base.
     *
     * @param sessionId The session ID corresponding to the connection.
     * @param user The user to delete.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public void userDelete(String sessionId, String user)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        List<User> userList = userLookup(sessionId, user);
        if (userList == null || userList.isEmpty()) {
            String msg = "User is not found.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userDelete");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
            LOG.error(msg, ier);
            throw ier;
        }
        String userId = Session.getUser(sessionId).getUserID();
        for (User u : userList) {
            int resp = userPep.userDelete(userId, u.getOwnerID());
            if (!Utils.responseIsPermit(resp) && !isRootAccess(sessionId)) {
                String msg = "Acces denied.";
                SecurityException se = new SecurityException();
                se.setReason(msg);
                se.setQueryName("userDelete");
                SecurityExceptionResponse ser = new SecurityExceptionResponse(msg, se);
                LOG.error(msg, ser);
                throw ser;
            }
            backend.userDelete(user);
        }
    }

    /**
     * Fetchs <code>User</code> corresponding to login.
     *
     * @param login The user login.
     * @return The user corresponding to the login.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If login or password is incorrect.
     */
    public User userCertLogin(String login)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        try {
            /*
             * Context newCtx = new InitialContext(); Context envCtx = (Context)
             * newCtx.lookup("java:comp/env"); DirContext dirCtxt = (DirContext)
             * envCtx.lookup("ldap/gatewayldap");
             */
            List<User> userList = backend.userCertLogin(login);
            if (userList.isEmpty()) {
                String msg = "A LDAP error occurred: login is incorrect.";
                SecurityException se = new SecurityException();
                se.setReason(msg);
                se.setQueryName("userLogin");
                SecurityExceptionResponse ser = new SecurityExceptionResponse(msg, se);
                LOG.error(msg, ser);
                throw ser;
            }
            return userList.get(0);
        } catch (SecurityExceptionResponse ser) {
            throw ser;
        } catch (ImplementationExceptionResponse ier) {
            throw ier;
        } catch (Exception ex) {
            String msg = "An unexpected error occurred.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLogin");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
            throw ier;
        }
    }

    /**
     * Fetchs list of <code>User</code> corresponding to user ID from the base, if the session
     * ID is root access or userLookup is permitted to the user associated to the session.
     *
     * @param sessionId The session ID.
     * @param userId The user ID to fetch.
     * @return The list of users corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public List<User> userLookup(String sessionId, String userId)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        try {
            User u = Session.getUser(sessionId);
            List<User> userList = backend.userLookup(userId);
            Iterator<User> iterUser = userList.iterator();
            while (iterUser.hasNext()) {
                User user = iterUser.next();
                int resp = userPep.userLookup(u.getUserID(), user.getOwnerID());
                if (!Utils.responseIsPermit(resp) && !isRootAccess(sessionId)) {
                    String msg = "Acces denied.";
                    SecurityException se = new SecurityException();
                    se.setReason(msg);
                    se.setQueryName("userLookup");
                    SecurityExceptionResponse ser = new SecurityExceptionResponse(msg, se);
                    LOG.error(msg, ser);
                    throw ser;
                }
            }
            return userList;
        } catch (SecurityExceptionResponse ser) {
            throw ser;
        } catch (ImplementationExceptionResponse ier) {
            throw ier;
        } catch (Exception ex) {
            String msg = "An unexpected error occurred.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLookup");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
            throw ier;
        }
    }

    /**
     * Fetchs <code>User</code> corresponding to user ID, if the session ID is root access
     * or userInfo is permitted to the user associated to the session.
     *
     * @param sessionId The session ID.
     * @param userId The user ID to fetch.
     * @return The <code>User</code> corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     * @throws SecurityExceptionResponse If the access is denied to the user.
     */
    public User userInfo(String sessionId, String userId)
            throws ImplementationExceptionResponse, SecurityExceptionResponse {
        try {
            List<User> uList = backend.userLookup(userId);
            if (uList.isEmpty()) {
                String msg = "User not found.";
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg);
                ie.setQueryName("userInfo");
                ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie);
                LOG.error(msg, ier);
                throw ier;
            }
            User u = Session.getUser(sessionId);
            int resp = userPep.userInfo(u.getUserID(), uList.get(0).getOwnerID());
            if (!Utils.responseIsPermit(resp) && !isRootAccess(sessionId)) {
                String msg = "Acces denied.";
                SecurityException se = new SecurityException();
                se.setReason(msg);
                se.setQueryName("userInfo");
                SecurityExceptionResponse ser = new SecurityExceptionResponse(msg, se);
                LOG.error(msg, ser);
                throw ser;
            }
            return uList.get(0);
        } catch (ImplementationExceptionResponse ier) {
            throw ier;
        } catch (SecurityExceptionResponse ser) {
            throw ser;
        } catch (Exception ex) {
            String msg = "An unexpected error occurred.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userInfo");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
            throw ier;
        }
    }

    public boolean isRootAccess(String sessionId) {
        User u = Session.getUser(sessionId);
        return userPep.isRootAccess(u.getUserID(), u.getOwnerID());
    }
}
