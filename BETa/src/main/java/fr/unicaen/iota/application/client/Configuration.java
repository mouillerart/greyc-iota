/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.application.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class Configuration {

    private Configuration() {
    }
    public static final String PROPERTIES_CONFIG_FILE = "/application.properties";
    public static String SOAP_SERVICE_URL;
    public static String RMI_SERVICE_URL;
    public static String RMI_CALLBACK_HOST;
    public static int RMI_CALLBACK_PORT;
    public static String DEFAULT_IDENTITY;

    static {
        try {
            Properties props = loadProperties();
            SOAP_SERVICE_URL = props.getProperty("soap-service-url", "http://localhost:8080/omega/services/IOTA_Service/");
            RMI_SERVICE_URL = props.getProperty("rmi-service-url", "//localhost:1099/ALfA");
            RMI_CALLBACK_HOST = props.getProperty("rmi-callback-host", "localhost");
            RMI_CALLBACK_PORT = Integer.parseInt(props.getProperty("rmi-callback-port", "1099"));
            DEFAULT_IDENTITY = props.getProperty("default-identity", "anonymous");
        } catch (IOException ex) {
            LogFactory.getLog(Configuration.class).fatal(null, ex);
        }
    }

    public static Properties loadProperties() throws IOException {
        // create and load default properties
        Properties props = new Properties();
        InputStream in = Configuration.class.getResourceAsStream(PROPERTIES_CONFIG_FILE);
        props.load(in);
        in.close();
        return props;
    }
}
