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
package fr.unicaen.iota.eta.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    private static final Log log = LogFactory.getLog(Constants.class);
    public static final String PROP_SESSION_TIME_LEASE = "session-time-lease";
    public static final String PROP_SESSION_FAILED_ID = "session-failed-id";
    public static final String PROP_XACML_URL = "xacml-url";
    public static final String PROP_XACML_IHM_URL = "xacml-ihm-url";
    public static final String PROP_EPCIS_QUERY_URL = "epcis-query-url";
    public static final String PROP_EPCIS_CAPTURE_URL = "epcis-capture-url";
    public static final String PROP_CALLBACK_URL = "eta-callback-url";
    public static final String PROP_EPCIS_SCHEMA_FILE = "epcisSchemaFile";
    public static final String PROP_EPCIS_MASTER_DATA_SCHEMA_FILE = "epcisMasterDataSchemaFile";
    public static final String PROP_LDAP_URL = "ldap-url";
    public static final String PROP_LDAP_BASE_DN = "ldap-basedn";
    public static final String PROP_LDAP_USER = "ldap-user";
    public static final String PROP_LDAP_PASSWORD = "ldap-password";
    public static final int SESSION_TIME_LEASE;
    public static final String SESSION_FAILED_ID;
    public static final String XACML_URL;
    public static final String XACML_IHM_URL;
    public static final String CALLBACK_URL;
    public static final String EPCIS_QUERY_URL;
    public static final String EPCIS_CAPTURE_URL;
    public static final String EPCIS_SCHEMA_FILE;
    public static final String EPCIS_MASTER_DATA_SCHEMA_FILE;
    public static final String LDAP_URL;
    public static final String LDAP_BASE_DN;
    public static final String LDAP_USER;
    public static final String LDAP_PASSWORD;

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
        SESSION_FAILED_ID = properties.getProperty(PROP_SESSION_FAILED_ID );
        XACML_URL = properties.getProperty(PROP_XACML_URL);
        XACML_IHM_URL = properties.getProperty(PROP_XACML_IHM_URL);
        EPCIS_QUERY_URL = properties.getProperty(PROP_EPCIS_QUERY_URL);
        EPCIS_CAPTURE_URL = properties.getProperty(PROP_EPCIS_CAPTURE_URL);
        CALLBACK_URL = properties.getProperty(PROP_CALLBACK_URL);
        EPCIS_SCHEMA_FILE = properties.getProperty(PROP_EPCIS_SCHEMA_FILE);
        EPCIS_MASTER_DATA_SCHEMA_FILE = properties.getProperty(PROP_EPCIS_MASTER_DATA_SCHEMA_FILE);
        LDAP_URL = properties.getProperty(PROP_LDAP_URL);
        LDAP_BASE_DN = properties.getProperty(PROP_LDAP_BASE_DN);
        LDAP_USER = properties.getProperty(PROP_LDAP_USER);
        LDAP_PASSWORD = properties.getProperty(PROP_LDAP_PASSWORD);
    }
}
