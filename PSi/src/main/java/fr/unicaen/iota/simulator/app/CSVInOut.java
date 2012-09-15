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
package fr.unicaen.iota.simulator.app;

import de.huberlin.informatik.pnk.appControl.PnmlInOut;
import de.huberlin.informatik.pnk.kernel.Net;
import de.huberlin.informatik.pnk.kernel.Transition;
import fr.unicaen.iota.simulator.model.LatLonLocation;
import fr.unicaen.iota.simulator.pnk.Event;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class CSVInOut extends PnmlInOut {

    private static final Log log = LogFactory.getLog(CSVInOut.class);
    public static String inOutName = "PNML and CSV 1.0";
    public static String stdFileExt = "pnml";
    public static Boolean multipleAllowed = new Boolean(true);

    public CSVInOut(de.huberlin.informatik.pnk.appControl.ApplicationControl ap) {
        super(ap);
    }

    @Override
    public void save(Vector theNets, URL theURL) {
        super.save(theNets, theURL);
        save((Collection<Net>) theNets, theURL);
    }

    public void save(Collection<Net> theNets, URL theURL) {
        List<String> rps = new ArrayList<String>();
        for (Net theNet : theNets) {
            StringBuilder csvContent = new StringBuilder();
            csvContent.append("reader ; latitude ; longitude \n");
            for (Object o : theNet.getTransitions()) {
                Transition t = (Transition) o;
                Event event = (Event) t.getExtension("event");
                String rp = event.getEvent().getReadPoint();
                if (rps.contains(rp)) {
                    continue;
                }
                rps.add(rp);
                LatLonLocation latLonLocation = event.getEvent().getLatLonLocation();
                csvContent.append(rp);
                csvContent.append(" ; ");
                csvContent.append(latLonLocation.getLatitude());
                csvContent.append(" ; ");
                csvContent.append(latLonLocation.getLongitude());
                csvContent.append("\n");
            }
            try {
                FileWriter fw = new FileWriter(new File(theURL.getFile() + ".csv"));
                fw.write(csvContent.toString());
                fw.close();
            } catch (IOException ex) {
                log.fatal(null, ex);
            }
        }
    }
}
