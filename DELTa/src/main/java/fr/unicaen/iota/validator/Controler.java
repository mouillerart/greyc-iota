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
import fr.unicaen.iota.validator.listener.AnalyserStatus;
import fr.unicaen.iota.validator.operations.Analyser;
import fr.unicaen.iota.validator.operations.ThreadManager;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class Controler extends Thread implements Runnable {

    private static final Log log = LogFactory.getLog(Controler.class);
    private File xmlEventFolder;
    private final IOTA iota;
    private final Identity identity;
    private AnalyserStatus analyserStatus;
    private boolean proceedAnalyse = true;
    public static List<String> ACTIVE_FILE_LIST = new ArrayList<String>();

    Controler(Identity identity, File f, IOTA iota) {
        this.xmlEventFolder = f;
        this.iota = iota;
        this.identity = identity;
        this.analyserStatus = new AnalyserStatus();
    }

    @Override
    public void run() {
        log.trace("Initialising Thread manager... ");
        ThreadManager threadManager = new ThreadManager();
        log.trace("Analysing files... ");
        while ((xmlEventFolder = new File(Configuration.XML_EVENT_FOLDER)).list().length > 0) {
            String[] files = getXmlEventFolder().list();
            for (String file : files) {
                if (ACTIVE_FILE_LIST.contains(Configuration.XML_EVENT_FOLDER + "/" + file)) {
                    continue;
                }
                ACTIVE_FILE_LIST.add(Configuration.XML_EVENT_FOLDER + "/" + file);
                Analyser analyser = new Analyser(Configuration.XML_EVENT_FOLDER + "/" + file, getIdentity(), getIota(), getAnalyserStatus());
                try {
                    threadManager.startThread(analyser);
                } catch (InterruptedException ex) {
                    log.fatal(null, ex);
                }
                try {
                    synchronized (this) {
                        while (!proceedAnalyse) {
                            wait();
                        }
                    }
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    /**
     * @return the xmlEventFolder
     */
    public File getXmlEventFolder() {
        return xmlEventFolder;
    }

    /**
     * @param xmlEventFolder the xmlEventFolder to set
     */
    public void setXmlEventFolder(File xmlEventFolder) {
        this.xmlEventFolder = xmlEventFolder;
    }

    /**
     * @return the iota
     */
    public IOTA getIota() {
        return iota;
    }

    /**
     * @return the identity
     */
    public Identity getIdentity() {
        return identity;
    }

    /**
     * @return the analyserStatus
     */
    public AnalyserStatus getAnalyserStatus() {
        return analyserStatus;
    }

    /**
     * @param analyserStatus the analyserStatus to set
     */
    public void setAnalyserStatus(AnalyserStatus analyserStatus) {
        this.analyserStatus = analyserStatus;
    }

    public synchronized void suspendAnalyse() {
        proceedAnalyse = false;
    }

    public synchronized void resumeAnalyse() {
        proceedAnalyse = true;
        notify();
    }
}
