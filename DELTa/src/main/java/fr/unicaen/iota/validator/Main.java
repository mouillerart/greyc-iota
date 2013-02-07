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

import fr.unicaen.iota.tau.model.Identity;
import fr.unicaen.iota.validator.gui.GUI;
import java.io.File;
import java.io.IOException;
import javax.swing.UIManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 *
 */
public class Main {

    private static final Log log = LogFactory.getLog(Main.class);

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String policyFile = Main.class.getClassLoader().getResource("java.policy").toString();
        System.setProperty("java.security.policy", policyFile);
        File f = new File(Configuration.XML_EVENT_FOLDER);
        if (!f.exists()) {
            log.fatal(Configuration.XML_EVENT_FOLDER + " does not exist!");
            System.exit(-1);
        }
        if (!f.isDirectory()) {
            log.fatal(Configuration.XML_EVENT_FOLDER + " is not a folder!");
            System.exit(-1);
        }
        IOTA iota = new IOTA();
        try {
            iota.loadFromXML();
        } catch (SAXException ex) {
            log.fatal(null, ex);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
        } catch (Exception e) {
            log.error(null, e);
        }
        AnalyserResult analyserResult = new AnalyserResult();
        Identity identity = new Identity();
        identity.setAsString(Configuration.IDENTITY);
        Controler controler = new Controler(identity, f, iota);
        GUI gui = new GUI(controler);
        controler.getAnalyserStatus().addListener(gui);
        controler.getAnalyserStatus().addListener(analyserResult);
        gui.setResults(analyserResult);
        gui.setVisible(true);
    }
}
