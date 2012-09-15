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
package fr.unicaen.iota.xacml.conf;

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
    private static final String PROPERTIES_CONFIG_FILE = "/application.properties";
    public static String DS_ADDRESS;
    public static int XACML_SERVICE_PORT;

    static {
        try {
            Properties props = loadProperties();
            DS_ADDRESS = props.getProperty("ds-address");
            XACML_SERVICE_PORT = Integer.parseInt(props.getProperty("xacml-service-port"));
        } catch (IOException ex) {
            LogFactory.getLog(Configuration.class).fatal(null, ex);
        }
    }

    public static Properties loadProperties() throws IOException {
        Properties props = new Properties();
        InputStream in = Configuration.class.getClassLoader().getResourceAsStream(PROPERTIES_CONFIG_FILE);
        props.load(in);
        in.close();
        return props;
    }
}
