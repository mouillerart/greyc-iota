/*
 *  This program is a part of the IoTa project.
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
package fr.unicaen.iota.sigma.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    private static final Log log = LogFactory.getLog(Constants.class);
    public static final String KEY_STORE_FILE_PATH;
    public static final String KEY_STORE_PASSWORD;
    public static final String PROP_KEY_STORE_FILE_PATH = "key-store-file-path";
    public static final String PROP_KEY_STORE_PASSWORD = "key-store-password";
    
    private Constants() {
    }

    static {
        Properties properties = new Properties();
        InputStream is = Constants.class.getClassLoader().getResourceAsStream("application.properties");
        log.info("Loading application properties");

        try {
            properties.load(is);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        KEY_STORE_FILE_PATH = properties.getProperty(PROP_KEY_STORE_FILE_PATH);
        KEY_STORE_PASSWORD = properties.getProperty(PROP_KEY_STORE_PASSWORD);
    }
}
