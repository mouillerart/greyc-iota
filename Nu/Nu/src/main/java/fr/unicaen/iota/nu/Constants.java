/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.nu;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;

/**
 * Global constants initialized at loading time from PROPERTIES_CONFIG_FILE.
 */
public final class Constants {

    private Constants() {
    }
    public static final String PROPERTIES_CONFIG_FILE = "/application.properties";
    public static String[] ONS_HOSTS;
    public static String ONS_EPCIS_ENTRY;
    public static String ONS_IDED_EPCIS_ENTRY;
    public static String ONS_DS_ENTRY;
    public static String ONS_IDED_DS_ENTRY;
    public static String ONS_HTML_ENTRY;
    public static String ONS_ENTRY_REGEX;
    public static String ONS_DOMAIN_PREFIX;
    public static double ONS_SPEC_LEVEL;

    static {
        try {
            Properties props = loadProperties();
            ONS_HOSTS = props.getProperty("ons-hosts").split(",");
            for (int i = 0; i < ONS_HOSTS.length; i++) {
                ONS_HOSTS[i] = ONS_HOSTS[i].trim();
            }
            ONS_DOMAIN_PREFIX = props.getProperty("ons-domain-prefix", "ons-peer.com.");
            if (!ONS_DOMAIN_PREFIX.endsWith(".")) {
                ONS_DOMAIN_PREFIX += ".";
            }
            ONS_SPEC_LEVEL = Double.parseDouble(props.getProperty("ons-spec-level", "2.0"));
            ONS_EPCIS_ENTRY = props.getProperty("ons-epcis-entry", "epc\\+epcis");
            ONS_IDED_EPCIS_ENTRY = props.getProperty("ons-ided-epcis-entry", "epc\\+ided_epcis");
            ONS_DS_ENTRY = props.getProperty("ons-ds-entry", "epc\\+ds");
            ONS_IDED_DS_ENTRY = props.getProperty("ons-ided-ds-entry", "epc\\+ided_ds");
            ONS_HTML_ENTRY = props.getProperty("ons-html-entry", "epc\\+html");
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
