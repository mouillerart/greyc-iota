/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013 Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.ds.commons;

import fr.unicaen.iota.ds.client.DSClient;
import fr.unicaen.iota.ds.model.CreateResponseType;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.ds.model.EventCreateResp;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.nu.ONSOperation;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class Publish {

    private String myAddress;
    private String jmsUrl;
    private String jmsLogin;
    private String jmsPassword;
    private String timeProperty;
    private Connection connection;
    private ONSOperation ons;
    private Map<String, String> dsAddressesByEPC;
    private static final Log LOG = LogFactory.getLog(Publish.class);

    public Publish(String myAddress, String[] onsHosts, String jmsUrl, String jmsLogin, String jmsPassword, String timeProperty) {
        this.myAddress = myAddress;
        ons = new ONSOperation(onsHosts);
        this.jmsUrl = jmsUrl;
        this.jmsLogin = jmsLogin;
        this.jmsPassword = jmsPassword;
        this.timeProperty = timeProperty;
        this.dsAddressesByEPC = new HashMap<String,String>();
    }

    public String getTimeProperty() {
        return timeProperty;
    }

    /**
     * Creates a JMS connection.
     * @throws JMSException If the connection could not be established.
     */
    public void createsJMSConnection() throws JMSException {
        ActiveMQConnectionFactory factory;
        if (jmsLogin != null && jmsPassword != null && !jmsLogin.isEmpty() && !jmsPassword.isEmpty()) {
            factory = new ActiveMQConnectionFactory(jmsLogin, jmsPassword, jmsUrl);
        } else {
            factory = new ActiveMQConnectionFactory(jmsUrl);
        }
        this.connection = factory.createConnection();
    }

    /**
     * Starts the JMS connection.
     * @throws JMSException If the JMS provider fails to start message delivery due to some internal error.
     */
    public void startsJMSConnection() throws JMSException {
        connection.start();
    }

    /**
     * Closes the JMS connection.
     * @throws JMSException If the JMS provider fails to start message delivery due to some internal error.
     */
    public void closesJMSConnection() throws JMSException {
        connection.close();
    }

    /**
     * Creates a <code>Session</code> object.
     * See {@link javax.jms.Connection#createSession(boolean, int)}
     */
    public Session createsJMSSession(boolean transacted, int acknowledge) throws JMSException {
        return connection.createSession(transacted, acknowledge);
    }

    /**
     * Creates a <code>Queue</code> object.
     * See {@link javax.jms.Session#createQueue(java.lang.String)}
     */
    public Queue createsJMSQueue(Session session, String queueName) throws JMSException {
        return session.createQueue(queueName);
    }

    /**
     * Creates a <code>MessageConsumer</code> object.
     * See {@link javax.jms.Session#createConsumer(javax.jms.Destination) }
     */
    public MessageConsumer createsJMSConsumer(Session session, Destination dest) throws JMSException {
        return session.createConsumer(dest);
    }

    /**
     * Creates a <code>MessageProducer</code> object.
     * See {@link javax.jms.Session#createProducer(javax.jms.Destination) }
     */
    public MessageProducer createJMSProducer(Session session, Destination dest) throws JMSException {
        return session.createProducer(dest);
    }

    /**
     * Queries ONS to fetch the address associated to the EPC.
     * @param epc The EPC to query.
     * @param type The type of the address.
     * @return The DS address associated to the EPC.
     */
    public String getDSAdress(String epc, ONSEntryType type) {
        if (dsAddressesByEPC.containsKey(epc)) {
            return dsAddressesByEPC.get(epc);
        }
        else {
            Map<ONSEntryType, String> addresses = ons.queryONS(epc);
            String dsAddress = (addresses != null)? addresses.get(type) : null;
            if (dsAddress == null) {
                return null;
            }
            dsAddressesByEPC.put(epc, dsAddress);
            return dsAddress;
        }
    }

    /**
     * Publishes the event. If the event can not be published, the event is resend to the queue.
     * @param consumer The JMS consumer which retrieves the event to publish.
     * @param timeout Waits until this time expires (in milliseconds).
     * @param producerSession The JMS session associated to the producer.
     * @param producer The JMS producer which sends the event to the next queue if the event could not be published.
     * @param runtime The runtime.
     * @return True if the publishing can continue.
     * @throws JMSException If an error occurred with the JMS provider.
     * @throws JAXBException If an error occurred during the conversion from the string to <code>DSEvent</code>.
     * @throws ParserConfigurationException If an error occurred during the conversion from the JMS message to string.
     * @throws SAXException If an error occurred during the conversion from the JMS message to string.
     * @throws IOException If an I/O error occurred.
     */
    public boolean publishEvent(MessageConsumer consumer, long timeout, Session producerSession, MessageProducer producer,
            long runtime) throws JMSException, JAXBException, ParserConfigurationException, SAXException, IOException {
        boolean continuePublishing = true;
        boolean republish = true;
        Message message = consumer.receive(timeout);
        if (message == null) {
            return false;
        }
        TextMessage text = (TextMessage) message;
        String docText = text.getText();
        if (text.getLongProperty(timeProperty) <= runtime) {
            DSEvent event = Utils.extractsDSEvent(docText);
            String epc = event.getEpc();
            String dsAddress = getDSAdress(epc, ONSEntryType.ds);
            if (dsAddress == null) {
                LOG.error("Could not get the referent DS address for " + epc);
            }
            else {
                try {
                    if (notMe(dsAddress)) {
                        DSClient dsClient = new DSClient(dsAddress);
                        EventCreateResp result = dsClient.eventCreate(event);
                        if (!CreateResponseType.NOT_ADDED.equals(result.getValue())) {
                            republish = false;
                            LOG.info("Event published to " + dsAddress);
                        }
                    }
                    else {
                        republish = false;
                        LOG.info("Event not published: already in the corresponding DS.");
                    }
                } catch (Exception ex) {
                    String msg = "Fails to send event to " + dsAddress;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(msg, ex);
                    }
                    else {
                        LOG.info(msg);
                    }
                }
            }
        }
        else {
            continuePublishing = false;
        }

        if (republish) {
            sendsEvent(producerSession, producer, docText);
        }
        message.acknowledge();
        return continuePublishing;
    }

    /**
     * Sends event with the specified message producer.
     * @param session The JMS session used to send the message.
     * @param producer The JMS message producer used to send the message.
     * @param eventToReturn The event to send in String format.
     * @throws JMSException If an error occurred with the JMS provider.
     */
    public void sendsEvent(Session session, MessageProducer producer, String eventToReturn) throws JMSException {
        TextMessage tMsg = session.createTextMessage(eventToReturn);
        tMsg.setLongProperty(timeProperty, System.currentTimeMillis());
        producer.send(tMsg);
    }

    /**
     * Sends event with the specified message producer.
     * @param session The JMS session used to send the message.
     * @param producer The JMS message producer used to send the message.
     * @param event The event to send.
     * @throws JAXBException If the event could not be parse as String.
     * @throws JMSException If an error occurred with the JMS provider.
     */
    public void sendsEvent(Session session, MessageProducer producer, DSEvent event) throws JAXBException, JMSException {
        String stringEvent = Utils.convertsDSEventToString(event);
        TextMessage tMsg = session.createTextMessage(stringEvent);
        tMsg.setLongProperty(timeProperty, System.currentTimeMillis());
        producer.send(tMsg);
    }

    /**
     * Returns <code>True</code> if the specified address is not the publisher's address.
     * @param address The address to compare.
     * @return True if the specified address is not the publisher's address.
     */
    public boolean notMe(String address) {
        return !myAddress.equals(address);
    }

}
