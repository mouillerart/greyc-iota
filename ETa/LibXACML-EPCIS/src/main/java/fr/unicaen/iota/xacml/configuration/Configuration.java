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
package fr.unicaen.iota.xacml.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Configuration {

    public static final String QUERY_POLICIES_DIRECTORY;
    public static final String CAPTURE_POLICIES_DIRECTORY;
    public static final String ADMIN_POLICIES_DIRECTORY;
    private static final Log log = LogFactory.getLog(Configuration.class);

    static {
        InputStream in = null;
        Properties prop = new Properties();
        try {
            in = Configuration.class.getClassLoader().getResourceAsStream("xacml_configuration.properties");
            prop.load(in);
        } catch (IOException ex) {
            log.error("error during parsing properties", ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                log.error("error during closing input stream from properties", ex);
            }
        }
        QUERY_POLICIES_DIRECTORY = prop.getProperty("query-policy-directory");
        CAPTURE_POLICIES_DIRECTORY = prop.getProperty("capture-policy-directory");
        ADMIN_POLICIES_DIRECTORY = prop.getProperty("admin-policy-directory");
        log.info("Query policies directory: " + QUERY_POLICIES_DIRECTORY);
        log.info("Capture policies directory: " + CAPTURE_POLICIES_DIRECTORY);
        log.info("Admin policies directory: " + ADMIN_POLICIES_DIRECTORY);
    }
}
