/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.ds.service;

import fr.unicaen.iota.ds.commons.Publish;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 *
 */
public class Publisher  {

    private final Timer timer;
    private final Publish publish;
    private final long delay;
    private final long period;
    private final long timeout;
    private static final Log LOG = LogFactory.getLog(Publisher.class);

    public Publisher(long delay, long period, long timeout) {
        this.timer = new Timer();
        this.publish = new Publish(Constants.SERVICE_ID, Constants.ONS_HOSTS, Constants.JMS_URL,
                Constants.JMS_LOGIN, Constants.JMS_PASSWORD, Constants.JMS_MESSAGE_TIME_PROPERTY);
        this.delay = delay;
        this.period = period;
        this.timeout = timeout;
    }

    public void start() {
        timer.schedule(new PublisherTask(), delay, period);
    }

    public void stop() {
        timer.cancel();
    }

    private class PublisherTask extends TimerTask {

        @Override
        public void run() {
            try {
                publish.createsJMSConnection();
                Session consumerSession = publish.createsJMSSession(false, Session.CLIENT_ACKNOWLEDGE);
                Queue consumerQueue = publish.createsJMSQueue(consumerSession, Constants.JMS_QUEUE_NAME);
                MessageConsumer consumer = publish.createsJMSConsumer(consumerSession, consumerQueue);
                Session producerSession = publish.createsJMSSession(false, Session.CLIENT_ACKNOWLEDGE);
                Queue producerQueue = publish.createsJMSQueue(producerSession, Constants.JMS_QUEUE_NAME);
                MessageProducer producer = publish.createJMSProducer(producerSession, producerQueue);
                publish.startsJMSConnection();
                try {
                    long runtime = System.currentTimeMillis();
                    while (true) {
                        boolean result = publish.publishEvent(consumer, timeout, producerSession, producer, runtime);
                        if (!result) {
                            break;
                        }
                    }
                } finally {
                    consumerSession.close();
                    producerSession.close();
                    publish.closesJMSConnection();
                }
            } catch (IOException ex) {
                LOG.error("An I/O error occurred");
                LOG.debug("", ex);
            } catch (JMSException ex) {
                LOG.error("Error during receiving or sending message");
                LOG.debug("", ex);
            } catch (JAXBException ex) {
                LOG.error("Error during the conversion from the string to DSEvent");
                LOG.debug("", ex);
            } catch (ParserConfigurationException ex) {
                LOG.error("Error during the conversion from the JMS message to string.");
                LOG.debug("", ex);
            } catch (SAXException ex) {
                LOG.error("Error during the conversion from the JMS message to string.");
                LOG.debug("", ex);
            } catch (Exception ex) {
                LOG.error("An unexpected error occurred", ex);
            }
        }
    }

}
