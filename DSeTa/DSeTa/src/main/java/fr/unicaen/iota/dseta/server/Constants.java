/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dseta.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    private Constants() {
    }
    private static final Log log = LogFactory.getLog(Constants.class);
    public static final String SERVICE_ID;
    public static final String WINGS_URL;
    public static final String WINGS_LOGIN;
    public static final String WINGS_PASSWORD;
    public static final String DEFAULT_SESSION = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    public static final String XACML_DEFAULT_USER;

    static {
        log.info("Publisher properties configuration");
        Properties properties = new Properties();
        InputStream is = Constants.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(is);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        SERVICE_ID = properties.getProperty("service-id");
        WINGS_LOGIN = properties.getProperty("wings-login", "anonymous");
        WINGS_PASSWORD = properties.getProperty("wings-password", "anonymous");
        WINGS_URL = properties.getProperty("wings-url");
        XACML_DEFAULT_USER = properties.getProperty("xacml-default-user");
    }
}
