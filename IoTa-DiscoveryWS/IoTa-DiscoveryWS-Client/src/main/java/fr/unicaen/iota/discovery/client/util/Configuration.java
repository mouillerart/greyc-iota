/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.discovery.client.util;

import java.io.FileInputStream;
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
    public static final int WS_CONNECTION_POOL_SIZE;
    public static String DEFAULT_SESSION = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    static {
        log.debug("Loading publisher properties");
        Properties properties = new Properties();
        InputStream is = Configuration.class.getClassLoader().getResourceAsStream("ds-client.properties");
        try {
            if (is == null) {
                is = new FileInputStream("resources/ds-client.properties");
            }
            properties.load(is);
        } catch (IOException ex) {
            log.error(null, ex);
        }
        WS_CONNECTION_POOL_SIZE = Integer.parseInt(properties.getProperty("ws-connection-pool-size", "10"));
    }
}
