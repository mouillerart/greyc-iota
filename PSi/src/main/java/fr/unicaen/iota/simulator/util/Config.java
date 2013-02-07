/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class Config {

    private Config() {
    }
    public static final String PROPERTIES_CONFIG_FILE = "/config/PTNet.properties";
    public static final String StatFolder = "./repository/stats/";
    public static final String EVENT_FILE_SAVER_FOLDER = "./repository/events/";
    public static boolean PRINT_MESSAGE;
    public static boolean attachPoint;
    public static boolean animated;
    public static boolean publish;
    public static boolean grid;
    public static int animation_speed;
    public static String themamapAddress;
    public static int themamapPort;
    public static int publicationWindowForAverage;
    public static boolean saveEvents;
    public static boolean saveStats;
    public static String ONS_ADDRESS;
    public static String ONS_TLD_DOMAIN;
    public static boolean sign;
    public static String sigma_keystore;
    public static String sigma_keystore_password;
    public static String pks_filename;
    public static String pks_password;
    public static String trust_pks_filename;
    public static String trust_pks_password;
    public static String eventOwner;

    static {
        try {
            Properties props = loadProperties();
            animated = Boolean.valueOf(props.getProperty("animated", "false"));
            grid = Boolean.valueOf(props.getProperty("grid", "false"));
            publish = Boolean.valueOf(props.getProperty("publish", "false"));
            attachPoint = Boolean.valueOf(props.getProperty("attach-point", "false"));
            animation_speed = Integer.valueOf(props.getProperty("animation-speed", "1000"));
            publicationWindowForAverage = Integer.valueOf(props.getProperty("publication-window-for-average", "50"));
            PRINT_MESSAGE = Boolean.valueOf(props.getProperty("print-message", "false"));
            themamapAddress = props.getProperty("themamap-address", "localhost");
            themamapPort = Integer.valueOf(props.getProperty("themamap-port", "9999"));
            saveEvents = Boolean.valueOf(props.getProperty("save-events", "false"));
            saveStats = Boolean.valueOf(props.getProperty("save-stats", "false"));
            ONS_ADDRESS = props.getProperty("ons-address", "localhost");
            ONS_TLD_DOMAIN = props.getProperty("ons-tld-domain", "ons-peer.com.");
            sign = Boolean.valueOf(props.getProperty("sign"));
            sigma_keystore = props.getProperty("sigma-keystore");
            sigma_keystore_password = props.getProperty("sigma-keystore-password");
            pks_filename = props.getProperty("pks-filename", "privatekeys.jks");
            pks_password = props.getProperty("pks-password", "changeit");
            trust_pks_filename = props.getProperty("trust-pks-filename", "publickeys.jks");
            trust_pks_password = props.getProperty("trust-pks-password", "changeit");
            eventOwner = props.getProperty("event-owner", "anonymous");
        } catch (Throwable e) {
            LogFactory.getLog(Config.class).fatal("Problem while reading 'config/PTNet.properties'", e);
        }
    }

    public static Properties loadProperties() throws IOException {
        // create and load default properties
        Properties props = new Properties();
        InputStream in = Config.class.getResourceAsStream(PROPERTIES_CONFIG_FILE);
        props.load(in);
        in.close();
        return props;
    }
}
