/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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

import java.io.File;
import java.util.Date;

/**
 *
 */
public class StatControler {

    private StatControler() {
    }
    private static String folder;
    private static Stat generalStats;
    private static StatMap stats;
    private static final String separator = System.getProperty("file.separator");

    static {
        folder = new Date().toString();
        File f = new File(createFolderPath());
        f.mkdir();
        generalStats = new Stat("all", Config.StatFolder + separator + folder);
        stats = new StatMap();
    }

    public static void addGeneratedObject() {
        getGeneralStats().addGeneratedObject();
    }

    public static void addPublication(String bizLoc) {
        getGeneralStats().addPublication();
        getStats().get(bizLoc, createFolderPath()).addPublication();
    }

    public static void addPublicationTimeout(String bizLoc) {
        getGeneralStats().addPublicationTimeout();
        getStats().get(bizLoc, createFolderPath()).addPublicationTimeout();
    }

    public static void addPublicationError(String bizLoc) {
        getGeneralStats().addPublicationError();
        getStats().get(bizLoc, createFolderPath()).addPublicationError();
    }

    public static void addPublicationTimestamp(int l, String bizLoc) {
        getGeneralStats().addPublicationTimestamp(l);
        getStats().get(bizLoc, createFolderPath()).addPublicationTimestamp(l);
    }

    /**
     * @return the generatedObjects
     */
    public static int getGeneratedObjects() {
        return getGeneralStats().getGeneratedObjects();
    }

    /**
     * @return the publicationCount
     */
    public static int getPublicationCount() {
        return getGeneralStats().getPublicationCount();
    }

    /**
     * @return the publicationTimeoutCount
     */
    public static int getPublicationTimeoutCount() {
        return getGeneralStats().getPublicationTimeoutCount();
    }

    /**
     * @return the publicationErrorCount
     */
    public static int getPublicationErrorCount() {
        return getGeneralStats().getPublicationErrorCount();
    }

    public static double getPublicationInstantResponseTimeAverage() {
        return getGeneralStats().getPublicationInstantResponseTimeAverage();
    }

    public static double getPublicationResponseTimeAverage() {
        return getGeneralStats().getPublicationResponseTimeAverage();
    }

    /**
     * @return the maxValue
     */
    public static int getMaxValue() {
        return getGeneralStats().getMaxValue();
    }

    /**
     * @param aMaxValue the maxValue to set
     */
    public static void setMaxValue(int aMaxValue) {
        getGeneralStats().setMaxValue(aMaxValue);
    }

    /**
     * @return the minValue
     */
    public static int getMinValue() {
        return getGeneralStats().getMinValue();
    }

    /**
     * @param aMinValue the minValue to set
     */
    public static void setMinValue(int aMinValue) {
        getGeneralStats().setMinValue(aMinValue);
    }

    private static String createFolderPath() {
        return Config.StatFolder + separator + folder;
    }

    /**
     * @return the generalStats
     */
    public static Stat getGeneralStats() {
        return generalStats;
    }

    /**
     * @return the stats
     */
    public static StatMap getStats() {
        return stats;
    }
}
