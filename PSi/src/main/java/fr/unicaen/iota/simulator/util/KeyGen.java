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

import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class KeyGen {

    private KeyGen() {
    }
    private static final Log log = LogFactory.getLog(KeyGen.class);
    private final static String epcFile = "epcs.lst";

    public static String generateEPC(String barCode) {
        FileWriter fw = null;
        try {
            StringBuilder result = new StringBuilder();
            if (barCode.startsWith("urn:epc:id:sscc:")) {
                result.append(barCode);
                result.append(".");
                result.append(generateSsccID());
            } else {
                result.append(barCode);
                result.append(".");
                result.append(generateID());
            }
            fw = new FileWriter(epcFile, true);
            fw.write(result.toString());
            fw.write("\n");
            return result.toString();
        } catch (IOException ex) {
            log.fatal(null, ex);
            return null;
        } finally {
            try {
                fw.close();
            } catch (IOException ex) {
                log.fatal(null, ex);
                return null;
            }
        }
    }

    private static String generateID() {
        return String.valueOf(GregorianCalendar.getInstance().getTimeInMillis());
    }

    private static String generateSsccID() {
        return String.valueOf((new Long(GregorianCalendar.getInstance().getTimeInMillis() / 1000)).intValue());
    }
}
