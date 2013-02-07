/*
 *  This program is a part of the IoTa project.
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
package fr.unicaen.iota.epcilon.discovery;

import fr.unicaen.iota.epcilon.conf.Configuration;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import javax.swing.Timer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class PublisherMonitor {

    private PublisherMonitor() {
    }
    private static XPublisher thread = null;
    private static long lastNotification = 0;
    private static Timer timeOut = null;
    private static final Log log = LogFactory.getLog(PublisherMonitor.class);

    public static void init() {
        log.info("publisher initialization");
        XPublisher pub = new XPublisher();
        setPublisher(pub);
        pub.start();
        if (timeOut != null) {
            timeOut.restart();
        } else {
            timeOut = initTimeOut();
            timeOut.start();
        }
        notification();
    }

    public static void destroy() {
        if (timeOut != null) {
            timeOut.stop();
        }
        killPublisher();
    }

    private static Timer initTimeOut() {
        ActionListener action = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Calendar.getInstance().getTimeInMillis() - lastNotification) > Configuration.PUBLISHER_MAX_WAIT/*
                         * || thread==null
                         */) {
                    log.info("[[--publisher timeout--]]");
                    XPublisher pub = new XPublisher();
                    killPublisher();
                    setPublisher(pub);
                    pub.start();
                } else {
                    log.info("publisher is running ...");
                }
            }
        };
        return new Timer(Configuration.PUBLISHER_MONITOR_FREQUENCY, action);
    }

    public static synchronized boolean isRunning() {
        return thread != null && thread.isAlive();
    }

    public static synchronized void setPublisher(XPublisher p) {
        thread = p;
    }

    public static synchronized void notification() {
        lastNotification = Calendar.getInstance().getTimeInMillis();
    }

    public static synchronized void killPublisher() {
        if (thread == null) {
            return;
        }
        thread.interrupt();
        thread = null;
    }
}
