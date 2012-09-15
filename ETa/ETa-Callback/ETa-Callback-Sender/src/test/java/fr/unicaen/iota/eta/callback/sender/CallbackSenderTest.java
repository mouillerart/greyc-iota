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
package fr.unicaen.iota.eta.callback.sender;

import java.io.IOException;
import java.net.MalformedURLException;
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
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class CallbackSenderTest extends TestCase {

    private static final Log LOG = LogFactory.getLog(CallbackSenderTest.class);

    private String testQueueName = "receiverTestQueueName";

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
    
    private final int port = 60000;

    private String testURL = "http://localhost:"+port;
    
    private QueryCallbackListener listener;

    public CallbackSenderTest() {
        super();
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
        
    }

    @Before
    /**
     * Creates one message before to test.
     */
    public void setUp() {
        sendTestEvent();
        try {
            listener = QueryCallbackListener.getInstance(port);
            if (!listener.isRunning()) {
                listener.start();
            }
        }
        catch (IOException ex) {
            LOG.error(null, ex);
        }
    }

    @After
    public void tearDown() {
        listener.stopRunning();
    }

    @Test
    public void testSend() {
        boolean succes = false;
        try {
            CallbackOperationsModule c = new CallbackOperationsModule();
            ActiveMQConnectionFactory factory;
            if (c.getMsgUser() != null && c.getMsgPassword() != null
                    && !c.getMsgUser().isEmpty() && !c.getMsgPassword().isEmpty()) {
                factory = new ActiveMQConnectionFactory(c.getMsgUser(), c.getMsgPassword(), c.getMsgUrl());
            }
            else {
                factory = new ActiveMQConnectionFactory(c.getMsgUrl());
            }
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(testQueueName);
            MessageConsumer consumer = session.createConsumer(destination);
            connection.start();

            try {
                Message message = consumer.receive(200);
                if (message != null && message instanceof TextMessage) {
                    TextMessage text = (TextMessage)message;
                    String event = text.getText();
                    c.send(event, testURL);
                    succes = true;
                }
                message.acknowledge();
            }
            finally {
                connection.close();
            }
        }
        catch (MalformedURLException e) {
            LOG.error("Destination URL is malformed", e);
        }
        catch (IOException e) {
            LOG.error("An I/O error occurred", e);
        }
        catch (JMSException e) {
            LOG.error("An error deleting test event occured", e);
        }
        catch (Exception e) {
            LOG.error("An unexpected error occured", e);
        }
        assertTrue(succes);
    }

    private void sendTestEvent() {
        try {
            CallbackOperationsModule c = new CallbackOperationsModule();
            ActiveMQConnectionFactory factory;
            if (c.getMsgUser() != null && c.getMsgPassword() != null
                    && !c.getMsgUser().isEmpty() && !c.getMsgPassword().isEmpty()) {
                factory = new ActiveMQConnectionFactory(c.getMsgUser(), c.getMsgPassword(), c.getMsgUrl());
            }
            else {
                factory = new ActiveMQConnectionFactory(c.getMsgUrl());
            }
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(testQueueName);
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

}
