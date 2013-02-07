/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.eta.callback.filter;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class Filter {

    private final Timer timer;
    private final CallbackOperationsModule callback;
    private final long delay;
    private final long period;
    private static final Log LOG = LogFactory.getLog(Filter.class);

    public Filter(long delay, long period) {
        this.timer = new Timer();
        this.callback = new CallbackOperationsModule();
        this.delay = delay;
        this.period = period;
    }

    public void start() {
        timer.schedule(new FilterTask(), delay, period);
    }

    public void stop() {
        timer.cancel();
    }

    private class FilterTask extends TimerTask {

        @Override
        public void run() {
            try {
                callback.receiveFilterSend();
            } catch (SAXException ex) {
                LOG.error("An error processing the XML document occurred", ex);
            } catch (SQLException ex) {
                LOG.error("An error involving the database occurred", ex);
            } catch (JAXBException ex) {
                LOG.error("An error parsing the XML contents occurred", ex);
            } catch (IOException ex) {
                LOG.error("An I/O error occurred", ex);
            } catch (JMSException e) {
                LOG.error("Error during receiving or sending message", e);
            } catch (Exception e) {
                LOG.error("An unexpected error occurred", e);
            }
        }
    }
}
