/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.callback.sender;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.activemq.ActiveMQConnection;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    public static final String EPCIS_SCHEMA_PATH;
    public static final String TRUST_ALL_CERTIFICATES;
    public static final String ACTIVEMQ_QUEUE_NAME;
    public static final String ACTIVEMQ_URL;
    public static final String ACTIVEMQ_LOGIN;
    public static final String ACTIVEMQ_PASSWORD;
    public static final String DATABASE_URL;
    public static final String DATABASE_LOGIN;
    public static final String DATABASE_PASSWORD;
    public static final long STARTUP_DELAY;
    public static final long POLLING_DELAY;
    private static final Log LOG = LogFactory.getLog(Constants.class);

    private Constants() {
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
        EPCIS_SCHEMA_PATH = properties.getProperty("epcisSchemaFile", "/xsd/EPCglobal-epcis-query-1_0.xsd");
        TRUST_ALL_CERTIFICATES = properties.getProperty("trustAllCertificates", "false");
        ACTIVEMQ_URL = properties.getProperty("activemq-url");
        ACTIVEMQ_LOGIN = properties.getProperty("activemq-login", ActiveMQConnection.DEFAULT_USER);
        ACTIVEMQ_PASSWORD = properties.getProperty("activemq-password", ActiveMQConnection.DEFAULT_PASSWORD);
        ACTIVEMQ_QUEUE_NAME = properties.getProperty("activemq-queueName");
        DATABASE_URL = properties.getProperty("database-url");
        DATABASE_LOGIN = properties.getProperty("database-login");
        DATABASE_PASSWORD = properties.getProperty("database-password");
        STARTUP_DELAY = Long.parseLong(properties.getProperty("startup-delay", "10000"));
        POLLING_DELAY = Long.parseLong(properties.getProperty("polling-delay", "60000"));
    }
}
