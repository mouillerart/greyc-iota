/*
 *  This program is a part of the IoTa Project.
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
package fr.unicaen.iota.discovery.server.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    private Constants() {
    }
    private static final Log log = LogFactory.getLog(Constants.class);
    public static final String SERVICE_ID;
    public static final int SESSION_TIME_LEASE;
    public static final String SESSION_FAILED_ID;
    public static final String DS_SERVICE_TYPE;
    public static final String HTML_SERVICE_TYPE;
    public static final String EPCIS_SERVICE_TYPE;
    public static final String DS_ONS_TYPE;
    public static final String HTML_ONS_TYPE;
    public static final String EPCIS_ONS_TYPE;
    public static final String DS_TO_DS_CLASS;
    public static final String DS_TO_DS_BIZ_STEP;
    public static final String DS_TO_DS_EVENT_TYPE;
    public static final int DS_TO_DS_POOL_EVENT;
    public static final Timestamp DEFAULT_EVENT_TOPUBLISH_TIMESTAMP;
    public static final long PUBLISHER_TIMEOUT;
    public static final int PUBLISHER_MONITOR_FREQUENCY;
    public static final int PUBLISHER_FREQUENCY;
    public static final String[] ONS_HOSTS;
    public static final String DS_LOGIN;
    public static final String DS_PASSWORD;
    public static final long PUBLISHER_EVENT_REPUBLISH_GAP; // temps laissé après un echec de publication avant de rééssayer à nouveau
    public static final boolean MULTI_DS_ARCHITECTURE;
    public static final int SIMULTANEOUS_PUBLISH_LIMIT;
    public static final String DEFAULT_POLICY = "ACCEPT";
    public static final long DEFAULT_TIME_STAMP = 10000000;
    public static final String ONS_DOMAIN_PREFIX;
    public static final String XACML_URL;
    public static final String XACML_IHM_URL;
    public static final boolean USE_XACML;

    static {
        log.info("Publisher properties configuration");
        Properties properties = new Properties();
        InputStream is = Constants.class.getClassLoader().getResourceAsStream("publisher.properties");
        try {
            properties.load(is);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        if (Boolean.parseBoolean(properties.getProperty("multi-ds-architecture"))) {
            MULTI_DS_ARCHITECTURE = true;
            ONS_HOSTS = fr.unicaen.iota.nu.Constants.ONS_HOSTS;
            SIMULTANEOUS_PUBLISH_LIMIT = Integer.parseInt(properties.getProperty("simultaneous-publish-limit"));
            DS_LOGIN = properties.getProperty("ds-login");
            DS_PASSWORD = properties.getProperty("ds-password");
            PUBLISHER_EVENT_REPUBLISH_GAP = Long.valueOf(properties.getProperty("publisher-event-republish-gap"));
            DS_TO_DS_CLASS = properties.getProperty("ds-to-ds-class");
            DS_TO_DS_BIZ_STEP = properties.getProperty("ds-to-ds-biz-step");
            DS_TO_DS_EVENT_TYPE = properties.getProperty("ds-to-ds-event-type");
            DS_TO_DS_POOL_EVENT = Integer.parseInt(properties.getProperty("ds-to-ds-pool-event"));
            DEFAULT_EVENT_TOPUBLISH_TIMESTAMP = new Timestamp(Integer.parseInt(properties.getProperty("default-event-topublish-timestamp")));
            PUBLISHER_TIMEOUT = Integer.parseInt(properties.getProperty("publisher-timeout"));
            PUBLISHER_MONITOR_FREQUENCY = Integer.parseInt(properties.getProperty("publisher-monitor-frequency"));
            PUBLISHER_FREQUENCY = Integer.parseInt(properties.getProperty("publisher-frequency"));
            ONS_DOMAIN_PREFIX = fr.unicaen.iota.nu.Constants.ONS_DOMAIN_PREFIX;
        } else {
            MULTI_DS_ARCHITECTURE = false;
            ONS_HOSTS = null;
            DS_LOGIN = null;
            DS_PASSWORD = null;
            SIMULTANEOUS_PUBLISH_LIMIT = 0;
            PUBLISHER_EVENT_REPUBLISH_GAP = 0;
            DS_TO_DS_CLASS = "";
            DS_TO_DS_BIZ_STEP = "";
            DS_TO_DS_EVENT_TYPE = "";
            DS_TO_DS_POOL_EVENT = 0;
            DEFAULT_EVENT_TOPUBLISH_TIMESTAMP = new Timestamp(Integer.parseInt(properties.getProperty("default-event-topublish-timestamp")));
            PUBLISHER_TIMEOUT = 0;
            PUBLISHER_MONITOR_FREQUENCY = 0;
            PUBLISHER_FREQUENCY = 0;
            ONS_DOMAIN_PREFIX = "";
        }
        log.info("Application properties configuration");

        properties = new Properties();
        is = Constants.class.getClassLoader().getResourceAsStream("application.properties");
        try {
            properties.load(is);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        SESSION_TIME_LEASE = Integer.parseInt(properties.getProperty("session-time-lease"));
        SESSION_FAILED_ID = properties.getProperty("session-failed-id");
        DS_SERVICE_TYPE = properties.getProperty("ds-service-type");
        HTML_SERVICE_TYPE = properties.getProperty("html-service-type");
        EPCIS_SERVICE_TYPE = properties.getProperty("epcis-service-type");
        DS_ONS_TYPE = properties.getProperty("ds-ons-type");
        HTML_ONS_TYPE = properties.getProperty("html-ons-type");
        EPCIS_ONS_TYPE = properties.getProperty("epcis-ons-type");
        SERVICE_ID = properties.getProperty("service-id");
        XACML_URL = properties.getProperty("xacml-url");
        XACML_IHM_URL = properties.getProperty("xacml-ihm-url");
        USE_XACML = Boolean.parseBoolean(properties.getProperty("use-xacml"));
    }
}
