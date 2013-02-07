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
package fr.unicaen.iota.application.rmi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;

/**
 * Global constants initialized from the file PROPERTIES_CONFIG_FILE on loading.
 */
public final class Constants {

    private Constants() {
    }
    public static final String PROPERTIES_CONFIG_FILE = "/application.properties";
    public static final String PKS_FILENAME;
    public static final String PKS_PASSWORD;
    public static final String TRUST_PKS_FILENAME;
    public static final String TRUST_PKS_PASSWORD;
    public static final String DEFAULT_SESSION = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    public static final String RMI_SERVER_NAME;
    public static final String RMI_SERVER_HOST;
    public static final int RMI_SERVER_PORT;
    public static final boolean DEBUG = true;

    static {
        Properties props = new Properties();
        try {
            InputStream in = Constants.class.getResourceAsStream(PROPERTIES_CONFIG_FILE);
            props.load(in);
            in.close();
        } catch (IOException ex) {
            LogFactory.getLog(Constants.class).fatal(null, ex);
        }
        PKS_FILENAME = props.getProperty("pks-filename", "privatekeys.jks");
        PKS_PASSWORD = props.getProperty("pks-password", "changeit");
        TRUST_PKS_FILENAME = props.getProperty("trustpks-filename", "publickeys.jks");
        TRUST_PKS_PASSWORD = props.getProperty("trustpks-password", "changeit");
        RMI_SERVER_NAME = props.getProperty("rmi-server-name", "alfa");
        RMI_SERVER_HOST = props.getProperty("rmi-server-host", "localhost");
        RMI_SERVER_PORT = Integer.parseInt(props.getProperty("rmi-server-port", "1099"));
        String ons_domain_prefix = props.getProperty("ons-domain-prefix", "ons-peer.com.");
        if (!ons_domain_prefix.endsWith(".")) {
            ons_domain_prefix += ".";
        }
    }
}
