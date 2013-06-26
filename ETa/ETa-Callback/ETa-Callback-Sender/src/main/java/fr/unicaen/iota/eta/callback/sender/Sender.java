/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.callback.sender;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.Timer;
import java.util.TimerTask;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class Sender {

    private static final Log LOG = LogFactory.getLog(Sender.class);
    private Timer timer;
    private CallbackOperationsModule callback;
    private long delay;
    private long period;

    public Sender(long delay, long period) {
        this.timer = new Timer();
        // TODO reload CallbackOperationsModule each launch?
        this.callback = new CallbackOperationsModule(Constants.JMS_URL,
                Constants.JMS_LOGIN, Constants.JMS_PASSWORD, Constants.JMS_MESSAGE_TIME_PROPERTY);
        this.period = period;
        this.delay = delay;
    }

    public void start() {
        timer.schedule(new SendTask(), delay, period);
    }

    public void stop() {
        timer.cancel();
    }

    private class SendTask extends TimerTask {

        @Override
        public void run() {
            try {
                callback.createsJMSConnection();
                Session consumerSession = callback.createsJMSSession(false, Session.CLIENT_ACKNOWLEDGE);
                Queue consumerQueue = callback.createsJMSQueue(consumerSession, Constants.JMS_QUEUE_NAME);
                MessageConsumer consumer = callback.createsJMSConsumer(consumerSession, consumerQueue);
                Session producerSession = callback.createsJMSSession(false, Session.CLIENT_ACKNOWLEDGE);
                Queue producerQueue = callback.createsJMSQueue(producerSession, Constants.JMS_QUEUE_NAME);
                MessageProducer producer = callback.createJMSProducer(producerSession, producerQueue);
                callback.startsJMSConnection();
                try {
                    long runtime = System.currentTimeMillis();
                    while (true) {
                        boolean result = callback.consumeAndSend(consumer, Constants.JMS_TIMEOUT, producerSession, producer, runtime);
                        if (!result) {
                            break;
                        }
                    }
                } finally {
                    consumerSession.close();
                    producerSession.close();
                    callback.closesJMSConnection();
                }
            } catch (SAXException ex) {
                LOG.error("An error processing the XML document occurred");
                LOG.debug("", ex);
            } catch (SQLException ex) {
                LOG.error("An error involving the database occurred", ex);
            } catch (MalformedURLException ex) {
                LOG.error("A destination url is malformed");
                LOG.debug("", ex);
            } catch (IOException ex) {
                LOG.error("An I/O error occurred");
                LOG.debug("", ex);
            } catch (JMSException ex) {
                LOG.error("Error during receiving or sending message");
                LOG.debug("", ex);
            } catch (Exception e) {
                LOG.error("An unexpected error occurred", e);
            }
        }
    }
}
