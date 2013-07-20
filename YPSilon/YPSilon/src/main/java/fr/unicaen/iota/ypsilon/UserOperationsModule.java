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

import fr.unicaen.iota.ypsilon.client.model.ImplementationException;
import fr.unicaen.iota.ypsilon.client.model.ImplementationExceptionSeverity;
import fr.unicaen.iota.ypsilon.client.model.User;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserOperationsModule {

    private static final Log LOG = LogFactory.getLog(UserOperationsModule.class);
    private UserOperations backend;

    public UserOperationsModule() {
        this.backend = new UserOperations();
    }

    /**
     * Creates a new user in the base.
     *
     * @param login The login of the new user.
     * @param owner The owner of the new user.
     * @param alias The alias DN of the new user. Can be null.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public void userCreate(String login, String owner, String alias)
            throws ImplementationExceptionResponse {
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
     * @param user The user to delete.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public void userDelete(String user) throws ImplementationExceptionResponse {
        List<User> userList = userLookup(user);
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
        backend.userDelete(user);
    }

    /**
     * Fetchs list of <code>User</code> corresponding to user ID from the base.
     *
     * @param userId The user ID to fetch.
     * @return The list of users corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public List<User> userLookup(String userId) throws ImplementationExceptionResponse {
        try {
            return backend.userLookup(userId);
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
     * Fetchs <code>User</code> corresponding to user DN.
     *
     * @param userDN The user DN to fetch.
     * @return The <code>User</code> corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the base occurred.
     */
    public User userInfo(String userDN) throws ImplementationExceptionResponse {
        try {
            return backend.userInfo(userDN);
        } catch (ImplementationExceptionResponse ier) {
            throw ier;
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

}
