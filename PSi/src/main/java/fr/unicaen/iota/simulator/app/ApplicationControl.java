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

import de.huberlin.informatik.pnk.appControl.base.D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ApplicationControl extends de.huberlin.informatik.pnk.appControl.ApplicationControl {

    private static final Log log = LogFactory.getLog(ApplicationControl.class);

    public ApplicationControl(String toolspec) {
        super(toolspec);
    } // ApplicationControl

    // #################### Application Schnittstelle...
    // #################### ACApplicationInterface
    public ApplicationControl(String toolspec, String netFile) {
        this(toolspec);
        if (netFile == null) {
            return;
        }
        try {
            loadNet(new File(netFile).toURL());
        } catch (IOException ex) {
            log.fatal("File not found !", ex);
            System.exit(-1);
        }
    }

    public static void main(String args[]) {
        // debuglevel setzen... hier später Kommandozeile auswerten.
        //D.debug = 3;

        //main_test3();

        log.info("IoTa Simulator Started ...");

        String netFile = null;

        List<String> myargs = new ArrayList<String>();
        boolean options = true;
        boolean debugset = false;
        for (int i = 0; i < args.length; i++) {
            if ((args[i].charAt(0) == '-') && (options == true)) {
                String s = args[i];
                for (int j = 1; j < s.length(); j++) {
                    switch (s.charAt(j)) {
                        case 'd':
                            i++;
                            try {
                                D.debug = Integer.parseInt(args[i]);
                                debugset = true;
                            } catch (NumberFormatException e) {
                                log.fatal("wrong option, integer expected:" + args[i]);
                                usage();
                            }
                            break;
                        case 'n':     // -n netFile -> open net directly
                            i++;
                            if (args.length >= i && new File(args[i]).exists()) {
                                netFile = args[i];
                            } else {
                                log.fatal("Bad net file ofter -n option !");
                                System.exit(-1);
                            }
                            break;
                        case '-':
                            // Optionen zuende...
                            options = false;
                            break;
                        default:
                            log.fatal("unknown option:" + s.charAt(j));
                            usage();
                    }
                }
            } else {
                myargs.add(args[i]);
            }
        }
        if (!debugset) {
            D.debug = 0;
        }

        String toolspec = null;
        if (myargs.isEmpty()) {
            // Standard-toolSpecification
            toolspec = "file:toolSpecifications/toolSpecification.xml";
        } else {
            if (myargs.size() == 1) {
                toolspec = "file:" + myargs.get(0);
            } else {
                log.fatal("too many arguments");
                usage();
            }
        }
        ApplicationControl m = new ApplicationControl(toolspec, netFile);
    } // main
} // class ApplicationControl
