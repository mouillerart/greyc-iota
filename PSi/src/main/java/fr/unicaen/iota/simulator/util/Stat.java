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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class Stat {

    private final String bizLoc;
    private final String folder;
    private int beforeSave = Config.publicationWindowForAverage;
    private int generatedObjects = 0;
    private int publicationCount = 0;
    private int publicationTimeoutCount = 0;
    private int publicationErrorCount = 0;
    private List<Integer> publicationInstantResponseTime = new ArrayList<Integer>();
    private int publicationInstantResponseTimeSum = 0;
    private int publicationInstantResponseTimeID = 0;
    private int publicationResponseTimeSum = 0;
    private int publicationResponseTimeID = 0;
    private int maxValue = Integer.MIN_VALUE;
    private int minValue = Integer.MAX_VALUE;

    public Stat(String bizLoc, String folder) {
        this.bizLoc = bizLoc;
        this.folder = folder;
    }

    public void addGeneratedObject() {
        generatedObjects++;
    }

    public void addPublication() {
        publicationCount++;
    }

    public void addPublicationTimeout() {
        publicationTimeoutCount++;
    }

    public void addPublicationError() {
        publicationErrorCount++;
    }

    public void addPublicationTimestamp(int l) {
        if (publicationInstantResponseTimeID > Config.publicationWindowForAverage) {
            int elem = publicationInstantResponseTime.remove(0);
            publicationInstantResponseTimeSum -= elem;
            publicationInstantResponseTimeID--;
        }
        if (l > getMaxValue()) {
            setMaxValue(l);
        }
        if (l < getMinValue()) {
            setMinValue(l);
        }
        publicationInstantResponseTime.add(l);
        publicationInstantResponseTimeSum += l;
        publicationInstantResponseTimeID++;
        publicationResponseTimeSum += l;
        publicationResponseTimeID++;
        beforeSave--;
        if (beforeSave == 0) {
            try {
                saveStats();
            } catch (IOException ex) {
                LogFactory.getLog(Stat.class).error(null, ex);
            }
            beforeSave = Config.publicationWindowForAverage;
        }
    }

    /**
     * @return the generatedObjects
     */
    public int getGeneratedObjects() {
        return generatedObjects;
    }

    /**
     * @return the publicationCount
     */
    public int getPublicationCount() {
        return publicationCount;
    }

    /**
     * @return the publicationTimeoutCount
     */
    public int getPublicationTimeoutCount() {
        return publicationTimeoutCount;
    }

    /**
     * @return the publicationErrorCount
     */
    public int getPublicationErrorCount() {
        return publicationErrorCount;
    }

    public double getPublicationInstantResponseTimeAverage() {
        if (publicationInstantResponseTimeID == 0) {
            return 0;
        }
        return publicationInstantResponseTimeSum / publicationInstantResponseTimeID;
    }

    public double getPublicationResponseTimeAverage() {
        if (publicationResponseTimeID == 0) {
            return 0;
        }
        return publicationResponseTimeSum / publicationResponseTimeID;
    }

    private void saveStats() throws IOException {
        if (!Config.saveStats) {
            return;
        }
        StringBuilder lString = new StringBuilder();
        for (int i : publicationInstantResponseTime) {
            lString.append(" ");
            lString.append(i);
        }
        File f_avg = new File(folder + "/" + getBizLoc());
        if (!f_avg.exists()) {
            f_avg.createNewFile();
        }
        lString.append("\n");
        FileWriter fstream = new FileWriter(f_avg, true);
        BufferedWriter out = new BufferedWriter(fstream);
        out.write(lString.toString());
        out.close();
    }

    /**
     * @return the maxValue
     */
    public int getMaxValue() {
        return maxValue;
    }

    /**
     * @param aMaxValue the maxValue to set
     */
    public void setMaxValue(int aMaxValue) {
        maxValue = aMaxValue;
    }

    /**
     * @return the minValue
     */
    public int getMinValue() {
        return minValue;
    }

    /**
     * @param aMinValue the minValue to set
     */
    public void setMinValue(int aMinValue) {
        minValue = aMinValue;
    }

    /**
     * @return the bizLoc
     */
    public String getBizLoc() {
        return bizLoc;
    }
}
