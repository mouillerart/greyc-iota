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
package fr.unicaen.iota.ypsilon.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    private static final Log log = LogFactory.getLog(Constants.class);
    public static final String PROP_SESSION_TIME_LEASE = "session-time-lease";
    public static final String PROP_XACML_URL = "xacml-url";
    public static final String PROP_XACML_IHM_URL = "xacml-ihm-url";
    public static final String PROP_LDAP_URL = "ldap-url";
    public static final String PROP_LDAP_BASE_DN = "ldap-basedn";
    public static final String PROP_LDAP_USER = "ldap-user";
    public static final String PROP_LDAP_PASSWORD = "ldap-password";
    public static final int SESSION_TIME_LEASE;
    public static final String XACML_URL;
    public static final String XACML_IHM_URL;
    public static final String LDAP_URL;
    public static final String LDAP_BASE_DN;
    public static final String LDAP_USER;
    public static final String LDAP_PASSWORD;
    public static final String LDAP_USER_GROUP;
    public static final String LDAP_USER_ID;
    public static final String LDAP_ATTRIBUTE_ALIAS;
    public static final String LDAP_ATTRIBUTE_OWNER;
    public static final String PKS_FILENAME;
    public static final String PKS_PASSWORD;
    public static final String TRUST_PKS_FILENAME;
    public static final String TRUST_PKS_PASSWORD;

    private Constants() {
    }

    static {
        Properties properties = new Properties();
        InputStream is = Constants.class.getClassLoader().getResourceAsStream("application.properties");
        log.info("Chargement des propriétés de l'application");

        try {
            properties.load(is);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        SESSION_TIME_LEASE = Integer.parseInt(properties.getProperty(PROP_SESSION_TIME_LEASE));
        XACML_URL = properties.getProperty(PROP_XACML_URL);
        XACML_IHM_URL = properties.getProperty(PROP_XACML_IHM_URL);
        LDAP_URL = properties.getProperty(PROP_LDAP_URL);
        LDAP_BASE_DN = properties.getProperty(PROP_LDAP_BASE_DN);
        LDAP_USER = properties.getProperty(PROP_LDAP_USER);
        LDAP_PASSWORD = properties.getProperty(PROP_LDAP_PASSWORD);
        LDAP_USER_GROUP = properties.getProperty("ldap-user-group");
        LDAP_USER_ID = properties.getProperty("ldap-user-id");
        LDAP_ATTRIBUTE_ALIAS = properties.getProperty("ldap-attribute-alias", "aliasdn");
        LDAP_ATTRIBUTE_OWNER = properties.getProperty("ldap-attribute-owner", "owner");
        PKS_FILENAME = properties.getProperty("pks-filename", "privatekeys.jks");
        PKS_PASSWORD = properties.getProperty("pks-password", "changeit");
        TRUST_PKS_FILENAME = properties.getProperty("trust-pks-filename", "publickeys.jks");
        TRUST_PKS_PASSWORD = properties.getProperty("trust-pks-password", "changeit");
    }
}