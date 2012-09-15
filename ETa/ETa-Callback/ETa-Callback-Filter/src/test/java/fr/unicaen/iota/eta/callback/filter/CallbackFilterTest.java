/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.repository.InvalidFormatException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Filter test
 */
public class CallbackFilterTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(CallbackFilterTest.class);

    private String consummerQueueName = "filterConsummerTest";
    
    private String senderQueueName = "filterSenderTest";

    private String testEvents = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns3:EPCISQueryDocument "
            + "xmlns:ns3=\"urn:epcglobal:epcis-query:xsd:1\" xmlns:ns2=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\" "
            + "xmlns:ns4=\"urn:epcglobal:epcis:xsd:1\" xmlns:ns5=\"urn:epcglobal:epcis-masterdata:xsd:1\" "
            + "creationDate=\"1900-01-01T00:00:00.000+02:00\" schemaVersion=\"1.0\"> "
            + "<EPCISBody> <ns3:QueryResults><queryName>qName</queryName> <subscriptionID>subidTest</subscriptionID>"
            + "<resultsBody> <EventList> <ObjectEvent> <eventTime>1900-01-01T00:00:00.000+01:00</eventTime> "
            + "<eventTimeZoneOffset>+01:00</eventTimeZoneOffset> <epcList> <epc>urn:epc:id:sgtin:0614141.107346.2017</epc> "
            + "</epcList> <action>OBSERVE</action> <bizStep>urn:epcglobal:epcis:bizstep:fmcg:shipped</bizStep> "
            + "<disposition>urn:epcglobal:epcis:disp:fmcg:unknown</disposition> <readPoint> <id>urn:epc:id:sgln:0614141.07346.1234</id> "
            + "</readPoint> <bizLocation> <id>urn:epcglobal:fmcg:loc:0614141073467.A23-49</id> </bizLocation> "
            + "<bizTransactionList> <bizTransaction type=\"urn:epcglobal:fmcg:btt:po\"> http://transaction.acme.com/po/12345678 </bizTransaction> "
            + "</bizTransactionList> </ObjectEvent> </EventList> </resultsBody> </ns3:QueryResults> "
            + "</EPCISBody> </ns3:EPCISQueryDocument>";
    
    private CallbackOperationsModule callbackM;
    
    /**
     * Create the test case
     * @param testName name of the test case
     */
    public CallbackFilterTest() {
        super();
            callbackM = new CallbackOperationsModule();
            callbackM.setConsummerQueueName(consummerQueueName);
            callbackM.setSenderQueueName(senderQueueName);
    }
    
    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        // clean test queue
        receiveMessages();
        // send test message
        sendTestEvent();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testFilter() {
        try {
            // Gets and filter JMS message.
            callbackM.receiveFilterSend();
            // The queue must be empty:
            assertFalse(receiveMessages());
        }
        catch (JMSException ex) {
            Logger.getLogger(CallbackFilterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex) {
            Logger.getLogger(CallbackFilterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SAXException ex) {
            Logger.getLogger(CallbackFilterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InvalidFormatException ex) {
            Logger.getLogger(CallbackFilterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(CallbackFilterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex) {
            Logger.getLogger(CallbackFilterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void sendTestEvent() {
        try {
            ActiveMQConnectionFactory factory;
            if (callbackM.getMsgUser() != null && callbackM.getMsgPassword() != null
                    && !callbackM.getMsgUser().isEmpty() && !callbackM.getMsgPassword().isEmpty()) {
                factory = new ActiveMQConnectionFactory(callbackM.getMsgUser(), callbackM.getMsgPassword(), callbackM.getMsgUrl());
            }
            else {
                factory = new ActiveMQConnectionFactory(callbackM.getMsgUrl());
            }
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(consummerQueueName);
            MessageProducer producer = session.createProducer(destination);
            connection.start();

            try {
                TextMessage message = session.createTextMessage();
                message.setText(testEvents);
                producer.send(message);
            }
            finally {
                connection.close();
            }
        }
        catch (JMSException e) {
            LOG.error("An error sending test event occured", e);
        }
    }
    
    /**
     * Receives JMS messages.
     * @return <code>true</code> if a message is received.
     */
    private boolean receiveMessages() {
        boolean msgReceived = false;
        try {
            ActiveMQConnectionFactory factory;
                if (callbackM.getMsgUser() != null && callbackM.getMsgPassword() != null
                        && !callbackM.getMsgUser().isEmpty() && !callbackM.getMsgPassword().isEmpty()) {
                    factory = new ActiveMQConnectionFactory(callbackM.getMsgUser(), callbackM.getMsgPassword(), callbackM.getMsgUrl());
                }
                else {
                    factory = new ActiveMQConnectionFactory(callbackM.getMsgUrl());
                }
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(consummerQueueName);
            MessageConsumer consumer = session.createConsumer(destination);
            connection.start();

            try {
                while (true) {
                    Message message = consumer.receive(200);
                    if (message != null)
                        msgReceived = true;
                    else
                        break;
                }
            }
            finally {
                connection.close();
            }
        }
        catch (JMSException ex) {
            Logger.getLogger(CallbackFilterTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        return msgReceived;
    }
}
