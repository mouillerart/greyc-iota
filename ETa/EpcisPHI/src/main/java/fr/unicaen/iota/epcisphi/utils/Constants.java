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
package fr.unicaen.iota.epcisphi.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    private Constants() {
    }
    private static Log log = LogFactory.getLog(Constants.class);
    public static final String USERSERVICE_ADDRESS;
    public static final String PROP_USERSERVICE_ADDRESS = "eta.userservice.url";
    public static final String PKS_FILENAME;
    public static final String PKS_PASSWORD;
    public static final String TRUST_PKS_FILENAME;
    public static final String TRUST_PKS_PASSWORD;


    static {
        Properties properties = new Properties();
        InputStream is = Constants.class.getClassLoader().getResourceAsStream("application.properties");
        log.info("Chargement des propriétés de l'application");

        try {
            properties.load(is);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        USERSERVICE_ADDRESS = properties.getProperty("eta.userservice.url");
        PKS_FILENAME = properties.getProperty("pks-filename", "privatekeys.jks");
        PKS_PASSWORD = properties.getProperty("pks-password", "changeit");
        TRUST_PKS_FILENAME = properties.getProperty("trust-pks-filename", "publickeys.jks");
        TRUST_PKS_PASSWORD = properties.getProperty("trust-pks-password", "changeit");
    }

}
