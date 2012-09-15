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
import fr.unicaen.iota.eta.user.userservice.ImplementationException;
import fr.unicaen.iota.eta.user.userservice.ImplementationExceptionSeverity;
import fr.unicaen.iota.eta.user.userservice_wsdl.ImplementationExceptionResponse;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Binding;
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
     * Fetchs list of
     * <code>User</code> corresponding to login and password from the LDAP base.
     *
     * @param login The user login.
     * @param password The user password.
     * @return The list of users corresponding to the login and password.
     * @throws ImplementationExceptionResponse If an error involving the LDAP
     * base occurred.
     */
    public List<User> userLogin(String login, String password)
            throws ImplementationExceptionResponse {
        try {
            /*
             * Context newCtx = new InitialContext(); Context envCtx = (Context)
             * newCtx.lookup("java:comp/env"); DirContext dirCtxt = (DirContext)
             * envCtx.lookup("ldap/gatewayldap");
             */
            String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
            String LDAP_CNX_POOL = "com.sun.jndi.ldap.connect.pool";
            String LDAP_AUTHENTICATION_MODE = "simple";
            String LDAP_REFERRAL_MODE = "follow";
            String LDAP_USER = "cn=" + Constants.LDAP_USER + "," + Constants.LDAP_BASE_DN;
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, Constants.LDAP_URL + "/" + Constants.LDAP_BASE_DN);
            env.put(Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE);
            env.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
            env.put(Context.SECURITY_CREDENTIALS, Constants.LDAP_PASSWORD);
            env.put(Context.REFERRAL, LDAP_REFERRAL_MODE);
            DirContext dirCtxt = new InitialDirContext(env);

            try {
                SearchControls ContrainteRecherche = new SearchControls();
                ContrainteRecherche.setSearchScope(SearchControls.SUBTREE_SCOPE);
                String critere = "uid=" + login;
                NamingEnumeration answer = dirCtxt.search("", critere, ContrainteRecherche);
                List<User> userList = new ArrayList<User>();
                while (answer.hasMore()) {
                    Binding currentElement = (Binding) answer.next();
                    Attributes attrs = dirCtxt.getAttributes(currentElement.getName());
                    byte[] bb = (byte[]) attrs.get("userpassword").get();
                    String pwd = new String(bb);
                    if (pwd.compareTo("{SHA}" + password) == 0) {
                        User user = new User();
                        user.setUserID(login);
                        String partner = (attrs.get("partner") != null) ? attrs.get("partner").get().toString() : "";
                        user.setPartnerID(partner);
                        userList.add(user);
                    }
                }
                return userList;
            } finally {
                dirCtxt.close();
            }
        } catch (NamingException ex) {
            String msg = "An error occurred while LDAP connection.";
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
     * Fetchs list of
     * <code>User</code> corresponding to user ID from the LDAP base.
     *
     * @param userId The user ID.
     * @return The list of users corresponding to the user ID.
     * @throws ImplementationExceptionResponse If an error involving the LDAP
     * base occurred.
     */
    public List<User> userLookup(String userId)
            throws ImplementationExceptionResponse {
        try {
            List<User> userList = new ArrayList<User>();
            String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
            String LDAP_CNX_POOL = "com.sun.jndi.ldap.connect.pool";
            String LDAP_SERVER_URL = "ldap://localhost:389"; // connexion openLDAP
            String LDAP_BASE_DN = "dc=gateway,dc=iota,dc=greyc,dc=fr";
            String LDAP_AUTHENTICATION_MODE = "simple";
            String LDAP_REFERRAL_MODE = "follow";
            String LDAP_USER = "cn=admin," + LDAP_BASE_DN;
            String LDAP_PASSWORD = "gatewayldap";
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, LDAP_SERVER_URL + "/" + LDAP_BASE_DN);
            env.put(Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE);
            env.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
            env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);
            env.put(Context.REFERRAL, LDAP_REFERRAL_MODE);
            DirContext dirCtxt = new InitialDirContext(env);

            try {
                SearchControls ContrainteRecherche = new SearchControls();
                ContrainteRecherche.setSearchScope(SearchControls.SUBTREE_SCOPE);
                String critere = "uid=" + userId;
                NamingEnumeration answer = dirCtxt.search("", critere, ContrainteRecherche);
                while (answer.hasMore()) {
                    Binding currentElement = (Binding) answer.next();
                    String uid = currentElement.getName();
                    Attributes attrs = dirCtxt.getAttributes(uid);
                    String partner = (attrs.get("partner") != null) ? attrs.get("partner").get().toString() : "";
                    User user = new User();
                    user.setUserID(userId);
                    user.setPartnerID(partner);
                    userList.add(user);
                }
                return userList;
            } finally {
                dirCtxt.close();
            }
        } catch (NamingException ex) {
            String msg = "An error occurred while LDAP connection.";
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
     * @param password The password of the new user.
     * @param partner The partner of the new user.
     * @throws ImplementationExceptionResponse If an error involving the LDAP
     * base occurred.
     */
    public void userCreate(String login, String password, String partner)
            throws ImplementationExceptionResponse {
        try {
            String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
            String LDAP_CNX_POOL = "com.sun.jndi.ldap.connect.pool";
            String LDAP_SERVER_URL = "ldap://localhost:389"; // connexion openLDAP
            String LDAP_BASE_DN = "dc=gateway,dc=iota,dc=greyc,dc=fr";
            String LDAP_AUTHENTICATION_MODE = "simple";
            String LDAP_REFERRAL_MODE = "follow";
            String LDAP_USER = "cn=admin," + LDAP_BASE_DN;
            String LDAP_PASSWORD = "gatewayldap";
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, LDAP_SERVER_URL + "/" + LDAP_BASE_DN);
            env.put(Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE);
            env.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
            env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);
            env.put(Context.REFERRAL, LDAP_REFERRAL_MODE);
            DirContext dirCtxt = new InitialDirContext(env);

            try {
                Attributes attributes = new BasicAttributes(true);
                Attribute oc = new BasicAttribute("objectclass");
                oc.add("top");
                oc.add("user");
                attributes.put(oc);
                attributes.put(new BasicAttribute("uid", login));
                attributes.put(new BasicAttribute("partner", partner));
                attributes.put(new BasicAttribute("userpassword", "{SHA}" + password));
                dirCtxt.createSubcontext("uid=" + login + ",ou=Users", attributes);
            } finally {
                dirCtxt.close();
            }
        } catch (NamingException ex) {
            String msg = "An error occurred while LDAP connection.";
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
     * @throws ImplementationExceptionResponse If an error involving the LDAP
     * base occurred.
     */
    public void userDelete(String user)
            throws ImplementationExceptionResponse {
        try {
            String LDAP_CONTEXT_FACTORY = "com.sun.jndi.ldap.LdapCtxFactory";
            String LDAP_CNX_POOL = "com.sun.jndi.ldap.connect.pool";
            String LDAP_SERVER_URL = "ldap://localhost:389"; // connexion openLDAP
            String LDAP_BASE_DN = "dc=gateway,dc=iota,dc=greyc,dc=fr";
            String LDAP_AUTHENTICATION_MODE = "simple";
            String LDAP_REFERRAL_MODE = "follow";
            String LDAP_USER = "cn=admin," + LDAP_BASE_DN;
            String LDAP_PASSWORD = "gatewayldap";
            Hashtable env = new Hashtable();
            env.put(Context.INITIAL_CONTEXT_FACTORY, LDAP_CONTEXT_FACTORY);
            env.put(Context.PROVIDER_URL, LDAP_SERVER_URL + "/" + LDAP_BASE_DN);
            env.put(Context.SECURITY_AUTHENTICATION, LDAP_AUTHENTICATION_MODE);
            env.put(Context.SECURITY_PRINCIPAL, LDAP_USER);
            env.put(Context.SECURITY_CREDENTIALS, LDAP_PASSWORD);
            env.put(Context.REFERRAL, LDAP_REFERRAL_MODE);
            DirContext dirCtxt = new InitialDirContext(env);

            try {
                dirCtxt.destroySubcontext("uid=" + user + ",ou=Users");
            } finally {
                dirCtxt.close();
            }
        } catch (NamingException ex) {
            String msg = "An error occurred while LDAP connection.";
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
}
