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
package fr.unicaen.iota.eta.callback.receiver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import junit.framework.TestCase;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class CallbackOperationsServletTest extends TestCase {

    private final Log LOG;

    String testQueueName = "receiverTestQueueName";

    String testEvents = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<ns3:EPCISQueryDocument schemaVersion=\"1.0\" creationDate=\"1900-01-01T00:00:00.000+02:00\" "
            + "xmlns:ns2=\"http://www.unece.org/cefact/namespaces/StandardBusinessDocumentHeader\" "
            + "xmlns:ns4=\"urn:epcglobal:epcis:xsd:1\" xmlns:ns3=\"urn:epcglobal:epcis-query:xsd:1\" "
            + "xmlns:ns5=\"urn:epcglobal:epcis-masterdata:xsd:1\"> <EPCISBody> <ns3:QueryResults>"
            + "<queryName>qName</queryName> <subscriptionID>subidTest</subscriptionID>"
            + "<resultsBody> <EventList> <ObjectEvent> <eventTime>1900-01-01T00:00:00.000+01:00</eventTime> "
            + "<eventTimeZoneOffset>+01:00</eventTimeZoneOffset> "
            + "<epcList> <epc>urn:epc:id:sgtin:0614141.107346.2017</epc> </epcList> "
            + "<action>OBSERVE</action> <bizStep>urn:epcglobal:epcis:bizstep:fmcg:shipped</bizStep> "
            + "<disposition>urn:epcglobal:epcis:disp:fmcg:unknown</disposition> "
            + "<readPoint> <id>urn:epc:id:sgln:0614141.07346.1234</id> </readPoint> "
            + "<bizLocation> <id>urn:epcglobal:fmcg:loc:0614141073467.A23-49</id> </bizLocation> "
            + "<bizTransactionList> <bizTransaction type=\"urn:epcglobal:fmcg:btt:po\"> "
            + "http://transaction.acme.com/po/12345678 </bizTransaction> </bizTransactionList> "
            + "</ObjectEvent> </EventList> </resultsBody> </ns3:QueryResults> </EPCISBody> </ns3:EPCISQueryDocument>";

    String testEventsToDelete = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ns3:EPCISQueryDocument "
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

    public CallbackOperationsServletTest() {
        super();
        System.setProperty("catalina.base", ".");
        LOG = LogFactory.getLog(CallbackOperationsServletTest.class);
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    /**
     * Deletes the test message from the queue after test.
     */
    @After
    public void tearDown() {
        deleteTestEvent();
    }

    @Test
    public void testSend() {
        boolean succes = false;
        try {
            InputStream inByte = inByte = new ByteArrayInputStream(testEvents.getBytes("UTF-8"));
            try {
                CallbackOperationsModule c = new CallbackOperationsModule();
                Document doc = c.getDocumentFromInputStream(inByte);
                DOMSource source = new DOMSource(doc);
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                StreamResult result = new StreamResult(new StringWriter());
                transformer.transform(source, result);
                String msg = result.getWriter().toString();

                //
                ActiveMQConnectionFactory factory;
                if (c.getUser() != null && c.getPassword() != null
                        && !c.getUser().isEmpty() && !c.getPassword().isEmpty()) {
                    factory = new ActiveMQConnectionFactory(c.getUser(), c.getPassword(), c.getUrl());
                }
                else {
                    factory = new ActiveMQConnectionFactory(c.getUrl());
                }
                Connection connection = factory.createConnection();
                Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                Destination destination = session.createQueue(testQueueName);
                MessageProducer producer = session.createProducer(destination);
                connection.start();

                try {
                    TextMessage message = session.createTextMessage();
                    message.setText(msg);
                    producer.send(message);
                    succes = true;
                }
                finally {
                    connection.close();
                }
                //
            }
            finally {
                inByte.close();
            }
        }
        catch (SAXException e) {
            LOG.error("Parsing error", e);
        }
        catch (TransformerException e) {
            LOG.error("Transformer creation error", e);
        }
        catch (JMSException e) {
            LOG.error("A JMS error occured ", e);
        }
        catch (UnsupportedEncodingException e) {
            LOG.error(e);
        }
        catch (IOException e) {
            LOG.error(e);
    }
        assertTrue(succes);
    }

    private void deleteTestEvent() {
        try {
            CallbackOperationsModule c = new CallbackOperationsModule();
            ActiveMQConnectionFactory factory;
            if (c.getUser() != null && c.getPassword() != null
                    && !c.getUser().isEmpty() && !c.getPassword().isEmpty()) {
                factory = new ActiveMQConnectionFactory(c.getUser(), c.getPassword(), c.getUrl());
            }
            else {
                factory = new ActiveMQConnectionFactory(c.getUrl());
            }
            Connection connection = factory.createConnection();
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            Destination destination = session.createQueue(testQueueName);
            MessageConsumer consumer = session.createConsumer(destination);
            connection.start();

            try {
                while (true) {
                    Message message = consumer.receive(200);
                    if (message == null) {
                        break;
                    }
                    message.acknowledge();
                }
            }
            finally {
                connection.close();
            }
        }
        catch (JMSException e) {
            LOG.error("An error deleting test event occured", e);
        }
    }

}
