/*
 *  This program is a part of the IoTa project.
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
package fr.unicaen.iota.eta.callback.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    public static final String EPCIS_SCHEMA_PATH;
    public static final String JMS_URL;
    public static final String JMS_LOGIN;
    public static final String JMS_PASSWORD;
    public static final String JMS_QUEUE_NAME;
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
        JMS_URL = properties.getProperty("jms-url");
        JMS_LOGIN = properties.getProperty("jms-login");
        JMS_PASSWORD = properties.getProperty("jms-password");
        JMS_QUEUE_NAME = properties.getProperty("jms-queueName", "queueToFilter");
    }
}
