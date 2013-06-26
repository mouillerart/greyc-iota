/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dseta.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    public static final boolean XACML_USE_TLS_ID;
    public static final String XACML_DEFAULT_USER;
    public static final String XACML_ANONYMOUS_USER;
    public static final boolean MULTI_DSETA_ARCHITECTURE;
    public static final String SERVICE_ID;
    public static final String PKS_FILENAME;
    public static final String PKS_PASSWORD;
    public static final String TRUST_PKS_FILENAME;
    public static final String TRUST_PKS_PASSWORD;
    public static final String[] ONS_HOSTS;
    public static final String JMS_URL;
    public static final String JMS_LOGIN;
    public static final String JMS_PASSWORD;
    public static final String JMS_QUEUE_NAME;
    public static final String JMS_MESSAGE_TIME_PROPERTY;
    public static final long PUBLISHER_DELAY;
    public static final long PUBLISHER_PERIOD;
    public static final long PUBLISHER_TIMEOUT;
    private static final Log LOG = LogFactory.getLog(Constants.class);

    public Constants() {
    }

    static {
        Properties properties = new Properties();
        InputStream is = Constants.class.getClassLoader().getResourceAsStream("application.properties");
        LOG.debug("Chargement des propriétés de l'application");
        try {
            properties.load(is);
        } catch (IOException ex) {
            LOG.fatal(null, ex);
        }
        PKS_FILENAME = properties.getProperty("pks-filename", "privatekeys.jks");
        PKS_PASSWORD = properties.getProperty("pks-password", "changeit");
        TRUST_PKS_FILENAME = properties.getProperty("trust-pks-filename", "publickeys.jks");
        TRUST_PKS_PASSWORD = properties.getProperty("trust-pks-password", "changeit");
        XACML_USE_TLS_ID = Boolean.parseBoolean(properties.getProperty("xacml-use-tls-id"));
        XACML_ANONYMOUS_USER = properties.getProperty("xacml-anonymous-user", "anonymous");
        XACML_DEFAULT_USER = properties.getProperty("xacml-default-user", "default");
        MULTI_DSETA_ARCHITECTURE = Boolean.parseBoolean(properties.getProperty("multi-dseta-architecture"));
        if (MULTI_DSETA_ARCHITECTURE) {
            SERVICE_ID = properties.getProperty("service-id");
            ONS_HOSTS = fr.unicaen.iota.nu.Constants.ONS_HOSTS;
            JMS_URL = properties.getProperty("jms-url");
            JMS_LOGIN = properties.getProperty("jms-login");
            JMS_PASSWORD = properties.getProperty("jms-password");
            JMS_QUEUE_NAME = properties.getProperty("jms-queue-name");
            JMS_MESSAGE_TIME_PROPERTY = properties.getProperty("jms-message-time-property");
            PUBLISHER_DELAY = Long.valueOf(properties.getProperty("publisher-delay"));
            PUBLISHER_PERIOD = Long.valueOf(properties.getProperty("publisher-period"));
            PUBLISHER_TIMEOUT = Long.valueOf(properties.getProperty("publisher-timeout"));
        }
        else {
            SERVICE_ID = "127.0.0.1";
            ONS_HOSTS = null;
            JMS_URL = null;
            JMS_LOGIN = null;
            JMS_PASSWORD = null;
            JMS_QUEUE_NAME = null;
            JMS_MESSAGE_TIME_PROPERTY = null;
            PUBLISHER_DELAY = 0;
            PUBLISHER_PERIOD = 0;
            PUBLISHER_TIMEOUT = 0;
        }
    }
}
