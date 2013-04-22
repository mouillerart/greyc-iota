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
import fr.unicaen.iota.ypsilon.client.model.ImplementationException;
import fr.unicaen.iota.ypsilon.client.model.ImplementationExceptionSeverity;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
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
     * @throws Exception If an unexpected error occured.
     */
    private DirContext getContext() throws NamingException, Exception {
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
        if (!providerUrl.endsWith("/")) {
            providerUrl += "/";
        }
        providerUrl += Constants.LDAP_BASE_DN;
        p.setProperty(Context.PROVIDER_URL, providerUrl);
        p.setProperty(Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE);
        p.setProperty(Context.SECURITY_PRINCIPAL, LDAP_USER);
        p.setProperty(Context.SECURITY_CREDENTIALS, Constants.LDAP_PASSWORD);
        p.setProperty(Context.REFERRAL, LDAP_REFERRAL_MODE);
        DirContext dirCtxt = new InitialDirContext(p);
        return dirCtxt;
    }

    /**
     * Fetchs a list of <code>User</code> corresponding to user certificate DN from the LDAP base.
     *
     * @param userDN The user certificate DN.
     * @return The list of users corresponding to the DN.
     * @throws ImplementationExceptionResponse If an error involving the LDAP base occurred.
     */
    public List<User> userCertLogin(String userDN) throws ImplementationExceptionResponse {
        try {
            return getUserByDN(userDN);
        } catch (NamingException ex) {
            String msg = "An error occurred during the LDAP connection.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLogin");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
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
            return getUserByDN(userDN);
        } catch (NamingException ex) {
            String msg = "An error occurred during the LDAP connection.";
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setQueryName("userLookup");
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            ImplementationExceptionResponse ier = new ImplementationExceptionResponse(msg, ie, ex);
            LOG.error(msg, ier);
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
                String name = login;
                if (alias != null && !alias.isEmpty()) {
                    attributes.put(new BasicAttribute(Constants.LDAP_ATTRIBUTE_ALIAS, login));
                    name = alias;
                }
                if (!name.contains("=")) {
                    name = Constants.LDAP_USER_ID + "=" + name;
                    name += (Constants.LDAP_USER_GROUP != null && !Constants.LDAP_USER_GROUP.isEmpty()) ? "," + Constants.LDAP_USER_GROUP : "";
                }
                String formatedDN = formatDN(name);
                dirCtxt.createSubcontext(formatedDN, attributes);
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
        } catch (Exception ex) {
            String msg = "An unexpected error occurred.";
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
        } catch (Exception ex) {
            String msg = "An unexpected error occurred.";
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
     * Gets a list of <code>User</code> corresponding to a DN from the LDAP base.
     * @param userDN The user DN. Can be an "alias".
     * @return The list of users corresponding to the DN.
     * @throws NamingException
     * @throws Exception
     */
    private List<User> getUserByDN(String userDN) throws NamingException, Exception {
        DirContext dirCtxt = getContext();
        try {
            List<User> userList = new ArrayList<User>();
            String formatedDN = formatDN(userDN);
            boolean found = false;
            try {
                Attributes attrs = dirCtxt.getAttributes(formatedDN);
                if (attrs != null && attrs.size() > 0) {
                    found = true;
                    User user = new User();
                    user.setUserID(userDN);
                    String owner = (attrs.get(Constants.LDAP_ATTRIBUTE_OWNER) != null) ? attrs.get(Constants.LDAP_ATTRIBUTE_OWNER).get().toString() : null;
                    user.setOwnerID(owner);
                    userList.add(user);
                }
            } catch (NamingException ex) {
                // DN not found.
            }
            if (!found) {
                Attributes matchAttribs = new BasicAttributes(true);
                matchAttribs.put(new BasicAttribute(Constants.LDAP_ATTRIBUTE_ALIAS, userDN));
                NamingEnumeration answer = dirCtxt.search("", matchAttribs);
                while (answer.hasMore()) {
                    SearchResult result = (SearchResult) answer.next();
                    Attributes attrsRes = result.getAttributes();
                    User user = new User();
                    user.setUserID((String) attrsRes.get(Constants.LDAP_ATTRIBUTE_ALIAS).get());
                    String owner = (attrsRes.get(Constants.LDAP_ATTRIBUTE_OWNER) != null) ? attrsRes.get(Constants.LDAP_ATTRIBUTE_OWNER).get().toString() : null;
                    user.setOwnerID(owner);
                    userList.add(user);
                }
            }
            return userList;
        } finally {
            dirCtxt.close();
        }
    }

    /**
     * Formats the specified DN depending on the LDAP configuration: removes white spaces and base DN.
     * @param dn The DN to format.
     * @return The formated DN.
     */
    private String formatDN(String dn) {
        String formatedDN = dn.replaceAll(" ", "");
        if (formatedDN.toLowerCase().endsWith(Constants.LDAP_BASE_DN.toLowerCase())) {
            int index = formatedDN.toLowerCase().lastIndexOf(Constants.LDAP_BASE_DN.toLowerCase());
            formatedDN = formatedDN.substring(0, index);
            if (formatedDN.endsWith(",")) {
                formatedDN = formatedDN.substring(0, formatedDN.length() - 1);
            }
        }
        return formatedDN;
    }

}
