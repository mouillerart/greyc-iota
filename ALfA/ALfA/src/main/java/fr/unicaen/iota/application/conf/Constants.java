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
package fr.unicaen.iota.application.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;

/**
 * Constantes globales initialisées depuis le fichier PROPERTIES_CONFIG_FILE au
 * chargement.
 */
public final class Constants {

    private Constants() {
    }
    public static final String PROPERTIES_CONFIG_FILE = "/application.properties";
    public static String[] ONS_HOSTS;
    public static String ONS_EPCIS_ENTRY;
    public static String ONS_DS_ENTRY;
    public static String ONS_SPEC_ENTRY;
    public static String ONS_ENTRY_REGEX;
    public static final String DEFAULT_SESSION = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
    public static String RMI_SERVER_NAME;
    public static String RMI_SERVER_HOST;
    public static int RMI_SERVER_PORT;
    public static String ONS_DOMAIN_PREFIX;
    public static double ONS_SPEC_LEVEL;
    public static String DS_LOGIN;
    public static String DS_PASSWORD;
    public static boolean DEBUG = true;

    static {
        try {
            Properties props = loadProperties();
            ONS_HOSTS = props.getProperty("ons").split(",");
            for (int i = 0; i < ONS_HOSTS.length; i++) {
                ONS_HOSTS[i] = ONS_HOSTS[i].trim();
            }
            RMI_SERVER_NAME = props.getProperty("rmi-server-name", "alfa");
            RMI_SERVER_HOST = props.getProperty("rmi-server-host", "localhost");
            RMI_SERVER_PORT = Integer.parseInt(props.getProperty("rmi-server-port", "1099"));
            DS_LOGIN = props.getProperty("ds-login", "anonymous");
            DS_PASSWORD = props.getProperty("ds-password", "anonymous");
            ONS_DOMAIN_PREFIX = props.getProperty("ons-domain-prefix", "ons-peer.com.");
            if (!ONS_DOMAIN_PREFIX.endsWith(".")) {
                ONS_DOMAIN_PREFIX += ".";
            }
            ONS_SPEC_LEVEL = Double.parseDouble(props.getProperty("ons-spec-level", "2.0"));
            ONS_EPCIS_ENTRY = props.getProperty("ons-epcis-entry", "epc\\+epcis");
            ONS_DS_ENTRY = props.getProperty("ons-ds-entry", "epc\\+ds");
            ONS_SPEC_ENTRY = props.getProperty("ons-spec-entry", "epc\\+spec");
            ONS_ENTRY_REGEX = props.getProperty("ons-entry-regex", "\\!\\^\\.\\*\\$\\!|\\!");
        } catch (IOException ex) {
            LogFactory.getLog(Constants.class).fatal(null, ex);
        }
    }

    public static Properties loadProperties() throws IOException {
        // create and load default properties
        Properties props = new Properties();
        InputStream in = Constants.class.getResourceAsStream(PROPERTIES_CONFIG_FILE);
        props.load(in);
        in.close();
        return props;
    }
}
