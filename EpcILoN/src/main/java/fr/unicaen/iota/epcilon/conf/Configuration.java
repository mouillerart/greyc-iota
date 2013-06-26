/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.epcilon.conf;

import fr.unicaen.iota.nu.ONSEntryType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Date;
import java.util.Properties;
import java.util.Random;
import org.apache.commons.logging.LogFactory;

public final class Configuration {

    private Configuration() {
    }
    public static long PUBLISHER_MAX_WAIT;
    public static int PUBLISHER_MONITOR_FREQUENCY;
    public static boolean IOTA_IDED;
    public static int EPCIS_TO_DS_POOL_EVENT;
    public static String IDENTITY;
    public static String DISCOVERY_SERVICE_ADDRESS;
    public static int PUBLISHER_FREQUENCY;
    public static String QUERY_CLIENT_TYPE;
    public static String DEFAULT_QUERY_CLIENT_ADDRESS;
    public static String DEFAULT_QUERY_CALLBACK_ADDRESS;
    public static String SUBSCRIPTION_KEY;
    public static int DEFAULT_EVENT_TO_PUBLISH_TIMESTAMP;
    public static int PUBLISHER_PENDING_REPUBLISH;
    public static int SIMULTANEOUS_PUBLISH_LIMIT;
    public static int SUBSCRIPTION_TYPE;
    public static String SUBSCRIPTION_VALUE;
    public static boolean PUBLISH;
    public static String PKS_FILENAME;
    public static String PKS_PASSWORD;
    public static String TRUST_PKS_FILENAME;
    public static String TRUST_PKS_PASSWORD;
    // read application properties from classpath
    private static final String PROPERTIES_CONFIG_FILE = "/application.properties";

    static {
        try {
            String resource = "subscription_key.properties";
            InputStream is = Configuration.class.getClassLoader().getResourceAsStream(resource);
            Properties properties = new Properties();
            properties.load(is);
            is.close();
            SUBSCRIPTION_KEY = properties.getProperty("subscription_key", "");
            if (SUBSCRIPTION_KEY.isEmpty()) {
                long key = new Date().getTime();
                Random random = new Random();
                int rInt = random.nextInt(100);
                String text = String.valueOf(key).concat(String.valueOf(rInt));
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte[] digest = md.digest(text.getBytes("UTF-8"));
                String subKey = new BigInteger(1, digest).toString(16);
                File f = new File(Configuration.class.getClassLoader().getResource(resource).getFile());
                FileWriter op = new FileWriter(f);
                op.write("subscription_key = " + subKey);
                op.close();
                SUBSCRIPTION_KEY = subKey;
            }
        } catch (Exception e) {
            LogFactory.getLog(Configuration.class).fatal(null, e);
            System.exit(-1);
        }
        try {
            InputStream is = Configuration.class.getClassLoader().getResourceAsStream(PROPERTIES_CONFIG_FILE);
            Properties properties = new Properties();
            properties.load(is);
            SIMULTANEOUS_PUBLISH_LIMIT = Integer.parseInt(properties.getProperty("simultaneous-publish-limit"));
            PUBLISHER_FREQUENCY = Integer.parseInt(properties.getProperty("publisher-frequency"));
            PUBLISHER_MAX_WAIT = Long.parseLong(properties.getProperty("publisher-max-wait"));
            PUBLISHER_MONITOR_FREQUENCY = Integer.parseInt(properties.getProperty("publisher-monitor-frequency"));
            IOTA_IDED = Boolean.parseBoolean(properties.getProperty("iota-ided"));
            EPCIS_TO_DS_POOL_EVENT = Integer.parseInt(properties.getProperty("epcis-to-ds-pool-event"));
            IDENTITY = properties.getProperty("identity");
            DISCOVERY_SERVICE_ADDRESS = properties.getProperty("discovery-service-address");
            QUERY_CLIENT_TYPE = (IOTA_IDED)? ONSEntryType.ided_epcis.name() : ONSEntryType.epcis.name();
            DEFAULT_QUERY_CLIENT_ADDRESS = properties.getProperty("query-client-address");
            DEFAULT_QUERY_CALLBACK_ADDRESS = properties.getProperty("query-callback-address");
            DEFAULT_EVENT_TO_PUBLISH_TIMESTAMP = Integer.parseInt(properties.getProperty("default-event-to-publish-timestamp"));
            PUBLISHER_PENDING_REPUBLISH = Integer.parseInt(properties.getProperty("publisher-pending-republish"));
            SUBSCRIPTION_TYPE = Integer.parseInt(properties.getProperty("subscription-type"));
            SUBSCRIPTION_VALUE = properties.getProperty("subscription-value");
            PUBLISH = Boolean.parseBoolean(properties.getProperty("publish"));
            PKS_FILENAME = properties.getProperty("pks-filename", "privatekeys.jks");
            PKS_PASSWORD = properties.getProperty("pks-password", "changeit");
            TRUST_PKS_FILENAME = properties.getProperty("trust-pks-filename", "publickeys.jks");
            TRUST_PKS_PASSWORD = properties.getProperty("trust-pks-password", "changeit");
            is.close();
        } catch (IOException e) {
            LogFactory.getLog(Configuration.class).fatal(null, e);
            System.exit(-1);
        }
    }
}
