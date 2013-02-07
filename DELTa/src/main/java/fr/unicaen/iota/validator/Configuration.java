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
package fr.unicaen.iota.validator;

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
    public static String DS_SERVICE_TYPE_FOR_EPCIS;
    public static String DS_SERVICE_TYPE_FOR_DS;
    public static String EPCIS_CAPTURE_INTERFACE;
    public static String EPCIS_QUERY_INTERFACE;
    public static String IOTA_XML_SCHEMA;
    public static String VERIFIED_DIRECTORY;
    public static String UNVERIFIED_DIRECTORY;
    public static String LOG_DIRECTORY;
    public static String RMI_SERVER_URL;
    public static boolean DEBUG = false;
    public static int NUMBER_OF_ACTIVE_THREAD = 10;
    public static String EPCIS_LOG_TYPE;
    public static String DS_LOG_TYPE;
    public static String DS_TO_DS_LOG_TYPE;
    public static boolean ANALYSE_EPCIS_EVENTS;
    public static boolean ANALYSE_EPCIS_TO_DS_EVENTS;
    public static boolean ANALYSE_DS_TO_DS_EVENTS;
    public static String PSI_REPOSITORY;
    public static String XML_EVENT_FOLDER;
    public static String STATS_FOLDER;
    public static String IDENTITY;

    static {
        try {
            Properties props = loadProperties();
            DS_SERVICE_TYPE_FOR_EPCIS = props.getProperty("ds-service-type-for-epcis", "epcis");
            DS_SERVICE_TYPE_FOR_DS = props.getProperty("ds-service-type-for-ds", "ds");
            EPCIS_CAPTURE_INTERFACE = props.getProperty("epcis-capture-interface", "/capture");
            EPCIS_QUERY_INTERFACE = props.getProperty("epcis-query-interface", "/query");
            IOTA_XML_SCHEMA = props.getProperty("iota-xml-schema", "./resources/iota.xml");
            VERIFIED_DIRECTORY = props.getProperty("verified-directory", "./repository/verified");
            UNVERIFIED_DIRECTORY = props.getProperty("unverified-directory", "./repository/unverified");
            LOG_DIRECTORY = props.getProperty("log-directory", "./repository/logs");
            RMI_SERVER_URL = props.getProperty("rmi-server-url", "//localhost:1099/ALfA");
            DEBUG = Boolean.parseBoolean(props.getProperty("debug", "false"));
            NUMBER_OF_ACTIVE_THREAD = Integer.parseInt(props.getProperty("thread-number", "10"));
            EPCIS_LOG_TYPE = props.getProperty("epcis-log-type", "epcis");
            DS_LOG_TYPE = props.getProperty("ds-log-type", "ds");
            DS_TO_DS_LOG_TYPE = props.getProperty("ds-to-ds-log-type", "dstods");
            ANALYSE_EPCIS_EVENTS = Boolean.parseBoolean(props.getProperty("analyse-epcis-events", "false"));
            ANALYSE_EPCIS_TO_DS_EVENTS = Boolean.parseBoolean(props.getProperty("analyse-epcis-to-ds-events", "true"));
            ANALYSE_DS_TO_DS_EVENTS = Boolean.parseBoolean(props.getProperty("analyse-ds-to-ds-events", "true"));
            PSI_REPOSITORY = props.getProperty("psi-repository", "psi/repository");
            XML_EVENT_FOLDER = PSI_REPOSITORY + "/events/";
            STATS_FOLDER = PSI_REPOSITORY + "/stats/";
            IDENTITY = props.getProperty("identity", "anonymous");
        } catch (IOException ex) {
            LogFactory.getLog(Configuration.class).fatal(null, ex);
        }
    }

    private static Properties loadProperties() throws IOException {
        // create and load default properties
        Properties props = new Properties();
        InputStream in = Configuration.class.getResourceAsStream(PROPERTIES_CONFIG_FILE);
        props.load(in);
        in.close();
        return props;
    }
}
