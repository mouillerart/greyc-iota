/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class Configuration {

    private Configuration() {
    }
    private static final Log log = LogFactory.getLog(Configuration.class);
    private static final String CONFIG_FILE = "application.properties";
    public static final String PKS_FILENAME;
    public static final String PKS_PASSWORD;
    public static final String TRUST_PKS_FILENAME;
    public static final String TRUST_PKS_PASSWORD;
    public static final String RMI_URL;
    public static final String XI_URL;
    public static final String DEFAULT_IDENTITY;

    static {
        log.trace("Loading configuration file");
        Properties properties = new Properties();
        try {
            InputStream is = Configuration.class.getClassLoader().getResourceAsStream(CONFIG_FILE);
            properties.load(is);
            is.close();
        } catch (IOException ex) {
            log.error("Unable to load " + CONFIG_FILE);
        }
        PKS_FILENAME = properties.getProperty("pks-filename", "privatekeys.jks");
        PKS_PASSWORD = properties.getProperty("pks-password", "changeit");
        TRUST_PKS_FILENAME = properties.getProperty("trust-pks-filename", "publickeys.jks");
        TRUST_PKS_PASSWORD = properties.getProperty("trust-pks-password", "changeit");
        RMI_URL = properties.getProperty("alfa-rmi-url", "//localhost:1099/alfa");
        XI_URL = properties.getProperty("xi-url", "//localhost:1099/ephi/xi");
        DEFAULT_IDENTITY = properties.getProperty("default-identity", "anonymous");
    }
}
