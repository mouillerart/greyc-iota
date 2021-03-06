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
import fr.unicaen.iota.ypsilon.constants.Constants;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserOperations {

    private static final Log LOG = LogFactory.getLog(UserOperations.class);

    public UserOperations() {
    }

    /**
     * Gets the connection to the LDAP directory.
     *
     * @return The connection.
     * @throws NamingException If an error occurred during the LDAP connection.
     */
    private DirContext getContext() throws NamingException {
        /*
         * Context newCtx = new InitialContext(); Context envCtx = (Context)
         * newCtx.lookup("java:comp/env"); DirContext dirCtxt = (DirContext)
         * envCtx.lookup("ldap/gatewayldap");
         */
        String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
        String LDAP_AUTHENTICATION_MODE = "simple";
        String LDAP_REFERRAL_MODE = "follow";
        String LDAP_USER = "cn=" + Constants.LDAP_USER + "," + Constants.LDAP_BASE_DN;
        Properties p = new Properties();
        p.setProperty(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
        String providerUrl = Constants.LDAP_URL;
        p.setProperty(Context.PROVIDER_URL, providerUrl);
        p.setProperty(Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE);
        p.setProperty(Context.SECURITY_PRINCIPAL, LDAP_USER);
        p.setProperty(Context.SECURITY_CREDENTIALS, Constants.LDAP_PASSWORD);
        p.setProperty(Context.REFERRAL, LDAP_REFERRAL_MODE);
        DirContext dirCtxt = new InitialDirContext(p);
        return dirCtxt;
    }

    /**
     * Fetchs <code>User</code> corresponding to user DN from the LDAP base.
     *
     * @param userDN The user DN.
     * @return The user corresponding to the user DN.
     * @throws ImplementationExceptionResponse If an error involving the LDAP base occurred.
     */
    public User userInfo(String userDN) throws ImplementationExceptionResponse {
        String userId;
            if (userDN.contains("=")) {
                userId = userDN;
            }
            else {
                userId = Constants.LDAP_USER_ID + "=" + userDN;
                userId += (Constants.LDAP_USER_GROUP != null && !Constants.LDAP_USER_GROUP.isEmpty()) ? "," + Constants.LDAP_USER_GROUP : "";
                userId += "," + Constants.LDAP_BASE_DN;
            }
            try {
                return getUserByDN(userId);
            } catch (NamingException ex) {
                String msg = "An error occurred during the user info: " + ex.toString();
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg);
                ie.setQueryName("userInfo");
                ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
                LOG.info(msg, ier);
                throw ier;
        }
    }

    /**
     * Fetchs list of <code>User</code> corresponding to user ID from the LDAP base.
     *
     * @param userId The user ID.
     * @return The list of users corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the LDAP base occurred.
     */
    public List<User> userLookup(String userId) throws ImplementationExceptionResponse {
        try {
            String userDN;
            if (userId.contains("=")) {
                userDN = userId;
            }
            else {
                userDN = Constants.LDAP_USER_ID + "=" + userId;
                userDN += (Constants.LDAP_USER_GROUP != null && !Constants.LDAP_USER_GROUP.isEmpty()) ? "," + Constants.LDAP_USER_GROUP : "";
                userDN += "," + Constants.LDAP_BASE_DN;
            }
            List<User> userList = new ArrayList<User>();
            User user = getUserByDN(userDN);
            if (user != null) {
                userList.add(user);
            }
            else {
                userList.addAll(getUserByAlias(userDN));
            }
            return userList;
        } catch (NamingException ex) {
            String msg = "An error occurred during the user lookup: " + ex.toString();
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLookup");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.info(msg, ier);
            throw ier;
        }
    }

    /**
     * Adds new user in the LDAP base.
     *
     * @param login The login of the new user.
     * @param owner The owner ID of the new user.
     * @param alias The alias DN of the new user. Can be null.
     * @throws ImplementationExceptionResponse If an error involving the LDAP base occurred.
     */
    public void userCreate(String login, String owner, String alias)
            throws ImplementationExceptionResponse {
        try {
            DirContext dirCtxt = getContext();
            try {
                Attributes attributes = new BasicAttributes(true);
                Attribute oc = new BasicAttribute("objectclass");
                oc.add("top");
                oc.add("user");
                attributes.put(oc);
                attributes.put(new BasicAttribute(Constants.LDAP_ATTRIBUTE_OWNER, owner));
                if (alias != null && !alias.isEmpty()) {
                    attributes.put(new BasicAttribute(Constants.LDAP_ATTRIBUTE_ALIAS, formatDN(alias)));
                }
                if (!login.contains("=")) {
                    login = Constants.LDAP_USER_ID + "=" + login;
                    login += (Constants.LDAP_USER_GROUP != null && !Constants.LDAP_USER_GROUP.isEmpty()) ? "," + Constants.LDAP_USER_GROUP : "";
                }
                if (!login.toLowerCase().endsWith(Constants.LDAP_BASE_DN.toLowerCase())) {
                    login += "," + Constants.LDAP_BASE_DN;
                }
                String formatedDN = formatDN(login);
                dirCtxt.createSubcontext(formatedDN, attributes);
            } catch (NamingException ex) {
                String msg = "An error occurred during the creation of the user: ";
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg + ex.toString());
                ie.setQueryName("userCreate");
                ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg + ex.toString(), ie, ex);
                LOG.info(msg, ier);
                throw ier;
            } finally {
                dirCtxt.close();
            }
        } catch (NamingException ex) {
            String msg = "An error occurred during the LDAP connection.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userCreate");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
            throw ier;
        }
    }

    /**
     * Deletes user from the LDAP base.
     *
     * @param user The user to remove.
     * @throws ImplementationExceptionResponse If an error involving the LDAP base occurred.
     */
    public void userDelete(String user) throws ImplementationExceptionResponse {
        try {
            DirContext dirCtxt = getContext();
            try {
                String name;
                if (user.contains("=")) {
                    name = user;
                }
                else {
                    name = Constants.LDAP_USER_ID + "=" + user;
                    name += (Constants.LDAP_USER_GROUP != null && !Constants.LDAP_USER_GROUP.isEmpty()) ? "," + Constants.LDAP_USER_GROUP : "";
                    name += "," + Constants.LDAP_BASE_DN;
                }
                String formatedDN = formatDN(name);
                dirCtxt.destroySubcontext(formatedDN);
            } catch (NamingException ex) {
                String msg = "An error occurred during the delete of the user: ";
                ImplementationException ie = new ImplementationException();
                ie.setReason(msg + ex.toString());
                ie.setQueryName("userDelete");
                ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg + ex.toString(), ie, ex);
                LOG.info(msg, ier);
                throw ier;
            } finally {
                dirCtxt.close();
            }
        } catch (NamingException ex) {
            String msg = "An error occurred during the LDAP connection.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userDelete");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
            throw ier;
        }
    }

    /**
     * Gets <code>User</code> corresponding to a DN from the LDAP base.
     * @param userDN The user DN.
     * @return The user corresponding to the DN or null if the DN is not found.
     * @throws NamingException
     * @throws Exception
     */
    private User getUserByDN(String userDN) throws NamingException {
        DirContext dirCtxt = getContext();
        User user = null;
        try {
            String formatedDN = formatDN(userDN);
            LOG.debug("Tries to find " + formatedDN);
            try {
                Attributes attrs = dirCtxt.getAttributes(formatedDN);
                if (attrs != null && attrs.size() > 0) {
                    user = new User();
                    user.setUserDN(userDN);
                    String owner = (attrs.get(Constants.LDAP_ATTRIBUTE_OWNER) != null)?
                            attrs.get(Constants.LDAP_ATTRIBUTE_OWNER).get().toString() : null;
                    user.setOwner(owner);
                }
            } catch (NamingException ex) {
                //user not found.
            }
        } finally {
            dirCtxt.close();
        }
        return user;
    }

    /**
     * Gets a list of <code>User</code> corresponding to an alias from the LDAP base.
     * @param user The user alias.
     * @return The list of users corresponding to the alias.
     * @throws NamingException
     * @throws Exception
     */
    private List<User> getUserByAlias(String userAlias) throws NamingException {
        DirContext dirCtxt = getContext();
        try {
            List<User> userList = new ArrayList<User>();
            String formatedDN = formatDN(userAlias);
            LOG.debug("Tries to find [" + formatedDN + "] in the attribute: " + Constants.LDAP_ATTRIBUTE_ALIAS);
            String filter = Constants.LDAP_ATTRIBUTE_ALIAS + "=" + formatedDN;
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration answer = dirCtxt.search(Constants.LDAP_BASE_DN, filter, constraints);
            while (answer.hasMore()) {
                SearchResult result = (SearchResult) answer.next();
                Attributes attrsRes = result.getAttributes();
                User user = new User();
                user.setAlias((String) attrsRes.get(Constants.LDAP_ATTRIBUTE_ALIAS).get());
                String owner = (attrsRes.get(Constants.LDAP_ATTRIBUTE_OWNER) != null) ?
                        attrsRes.get(Constants.LDAP_ATTRIBUTE_OWNER).get().toString() : null;
                user.setOwner(owner);
                user.setUserDN(result.getName());
                userList.add(user);
            }
            for (User u : userList) {
                LOG.debug("User found:");
                LOG.debug("user DN: " + u.getUserDN());
                LOG.debug("owner ID: " + u.getOwner());
            }
            return userList;
        } finally {
            dirCtxt.close();
        }
    }

    /**
     * Formats the specified DN depending on the LDAP configuration: removes white spaces.
     * @param dn The DN to format.
     * @return The formated DN.
     */
    private String formatDN(String dn) {
        String formatedDN = dn.replaceAll(" ", "");
        return formatedDN;
    }

}
