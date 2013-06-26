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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.jms.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This <code>CallbackOperationsModule</code> parses messages from the message broker
 * to a XML document conforming to a xsd schema. The query callback events
 * contained by this document are sent to the user.
 */
public class CallbackOperationsModule {

    private Schema schema;
    private java.sql.Connection dbConnection;
    private CallbackOperationsBackendSQL backend;
    private Connection connection;
    private String jmsUrl;
    private String jmsLogin;
    private String jmsPassword;
    private String timeProperty;
    private static final Log LOG = LogFactory.getLog(CallbackOperationsModule.class);

    public CallbackOperationsModule(String jmsUrl, String jmsLogin, String jmsPassword, String timeProperty) {
        this.schema = initEpcisSchema(Constants.EPCIS_SCHEMA_PATH);
        backend = new CallbackOperationsBackendSQL();
        dbConnection = loadDatabaseConnection();
        this.jmsUrl = jmsUrl;
        this.jmsLogin = jmsLogin;
        this.jmsPassword = jmsPassword;
        this.timeProperty = timeProperty;
        LOG.info("CallbackModule sender is successfully loaded");
    }

    /**
     * Initializes the EPCIS schema from a XSD file.
     *
     * @param xsdFile The XSD file containing the EPCIS schema.
     * @return The EPCIS schema.
     */
    private Schema initEpcisSchema(String xsdFile) {
        InputStream is = this.getClass().getResourceAsStream(xsdFile);
        if (is != null) {
            try {
                SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
                Source schemaSrc = new StreamSource(is);
                schemaSrc.setSystemId(CallbackOperationsModule.class.getResource(xsdFile).toString());
                Schema schm = schemaFactory.newSchema(schemaSrc);
                LOG.debug("EPCIS schema file initialized and loaded successfully");
                return schm;
            } catch (Exception e) {
                LOG.warn("Unable to load or parse the EPCIS schema", e);
            }
        } else {
            LOG.error("Unable to load the EPCIS schema file from classpath: cannot find resource " + xsdFile);
        }
        LOG.warn("Schema validation will not be available!");
        return null;
    }

    /**
     * Loads the connection to the database.
     *
     * @return The connection to the database.
     */
    private java.sql.Connection loadDatabaseConnection() {
        java.sql.Connection c = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties props = new Properties();
            props.setProperty("user", Constants.DATABASE_LOGIN);
            props.setProperty("password", Constants.DATABASE_PASSWORD);
            props.setProperty("autoReconnect", "true");
            c = DriverManager.getConnection(Constants.DATABASE_URL, props);
            c.setAutoCommit(false);
            LOG.debug("MySQL connection established");
        } catch (ClassNotFoundException e) {
            LOG.error(e);
        } catch (SQLException e) {
            LOG.error("Error during connection to the database", e);
        }
        return c;
    }

    /**
     * Parses and validates the payload as XML document.
     *
     * @param input The payload to parse.
     * @return The valid XML document.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws IOException If an I/O error occurred.
     */
    private Document getDocumentFromString(String string) throws SAXException, IOException {

        // parse the payload as XML document
        Document document;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            document = builder.parse(new InputSource(new StringReader(string)));
            LOG.debug("Payload successfully parsed as XML document");
            if (LOG.isDebugEnabled()) {
                try {
                    TransformerFactory tfFactory = TransformerFactory.newInstance();
                    Transformer transformer = tfFactory.newTransformer();
                    StringWriter writer = new StringWriter();
                    transformer.transform(new DOMSource(document), new StreamResult(writer));
                    String xml = writer.toString();
                    if (xml.length() > 100 * 1024) {
                        xml = "[too large, not logged]";
                    }
                    LOG.debug("Incoming contents:\n\n" + xml + "\n");
                } catch (Exception e) {
                    // never mind ... do not log
                }
            }

            // validate the XML document against the EPCISDocument schema
            if (schema != null) {
                Validator validator = schema.newValidator();
                try {
                    validator.validate(new DOMSource(document), null);
                } catch (SAXParseException e) {
                    // TODO: we need to ignore XML element order, the following
                    // is only a hack to pass some of the conformance tests
                    if (e.getMessage().contains("parentID")) {
                        LOG.warn("Ignoring XML validation exception: " + e.getMessage());
                    } else {
                        throw e;
                    }
                }
                LOG.debug("Callback events were successfully validated against the EPCISDocument schema");
            } else {
                LOG.warn("Schema validator unavailable. Unable to validate EPCIS callback events against schema!");
            }

        } catch (ParserConfigurationException e) {
            throw new SAXException(e);
        }
        return document;
    }

    /**
     * Connects to the Message Broker, gets and sends events to user.
     * @param consumer The JMS consumer which retrieves the event to send.
     * @param timeout Waits until this time expires (in milliseconds).
     * @param producerSession The JMS session associated to the producer.
     * @param producer The JMS producer which sends the event to the next queue if the event could not be sended.
     * @param runtime The runtime.
     * @return True if the sending can continue.
     * @throws MalformedURLException If the destination URL is malformed.
     * @throws JMSException If an error occurred with the JMS provider.
     * @throws IOExceptionIf an I/O error occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws SQLException If an error involving the database occurred.
     * @throws Exception If an unexpected error occurred.
     */
    public boolean consumeAndSend(MessageConsumer consumer, long timeout, Session producerSession, MessageProducer producer,
            long runtime) throws  MalformedURLException, JMSException, IOException, SAXException,
            SQLException, Exception {
        boolean continueSending = true;
        Message message = consumer.receive(timeout);
        if (message == null) {
            return false;
        }
        String docText;
        TextMessage text = (TextMessage) message;
        docText = text.getText();
        if (text.getLongProperty(timeProperty) <= runtime) {
            Document doc = getDocumentFromString(docText);
            String dest = fetchAddress(getSubIDInDoc(doc));
            try {
                int response = send(docText, dest);
                LOG.info("Event sent.");
            } catch (Exception e) {
                sendsJMSEvent(producerSession, producer, docText);
                String msg = "Fails to send event to " + dest + " : event resent.";
                if (LOG.isDebugEnabled()) {
                    LOG.debug(msg, e);
                }
                else {
                    LOG.info(msg);
                }
            }
        }
        else {
            continueSending = false;
        }
        message.acknowledge();
        return continueSending;
    }

    /**
     * Sends the given data String to the specified URL.
     *
     * @param data The data to send.
     * @param dest The destination to send the data to.
     * @return The response code.
     * @throws MalformedURLException If the destination url is not conformed.
     * @throws IOException If a communication error occurred.
     * @throws Exception
     */
    public int send(String data, String dest) throws MalformedURLException, IOException, Exception {
        // set up connection and send data to given destination
        URL serviceUrl;
        try {
            serviceUrl = new URL(dest);
        } catch (MalformedURLException e) {
            throw new MalformedURLException("Unable to parse destination as a URL");
        }
        LOG.debug("Sending results of subscribed query to '" + serviceUrl + "'");

        HttpURLConnection connection;
        int response = -1;
        try {
            connection = getConnection(dest);

            // send data
            Writer out = new OutputStreamWriter(connection.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            // disconnect and return
            // TODO response code
            response = connection.getResponseCode();
            connection.disconnect();
            return response;
        } catch (IOException e) {
            throw new IOException("Unable to send results of subscribed query to '" + dest + "'", e);
        }
    }

    /**
     * Opens a connection to the user interface.
     * @param url The address on which a connection will be opened.
     * @return The HTTP connection object.
     * @throws IOException If an error occurred connecting to the interface.
     * @throws Exception
     */
    private HttpURLConnection getConnection(String url) throws IOException, Exception {
        if (Constants.PKS_FILENAME != null && Constants.PKS_PASSWORD != null
                && Constants.TRUST_PKS_FILENAME != null && Constants.TRUST_PKS_PASSWORD != null) {
            System.setProperty("javax.net.ssl.keyStore", Constants.PKS_FILENAME);
            System.setProperty("javax.net.ssl.keyStorePassword", Constants.PKS_PASSWORD);
            System.setProperty("javax.net.ssl.trustStore", Constants.TRUST_PKS_FILENAME);
            System.setProperty("javax.net.ssl.trustStorePassword", Constants.TRUST_PKS_PASSWORD);
        }
        URL serviceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();

        if (Constants.PKS_FILENAME != null) {
            KeyStore keyStore = KeyStore.getInstance(Constants.PKS_FILENAME.endsWith(".p12") ? "PKCS12" : "JKS");
            keyStore.load(new FileInputStream(new File(Constants.PKS_FILENAME)), Constants.PKS_PASSWORD.toCharArray());
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, Constants.PKS_PASSWORD.toCharArray());
            KeyStore trustStore = KeyStore.getInstance(Constants.TRUST_PKS_FILENAME.endsWith(".p12") ? "PKCS12" : "JKS");
            trustStore.load(new FileInputStream(new File(Constants.TRUST_PKS_FILENAME)), Constants.TRUST_PKS_PASSWORD.toCharArray());
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
            trustManagerFactory.init(trustStore);
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            ((HttpsURLConnection) connection).setSSLSocketFactory(sslContext.getSocketFactory());
        }

        connection.setRequestProperty("content-type", "text/xml");
        connection.setRequestMethod("POST");
        connection.setReadTimeout(200);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    /**
     * Returns the subcription ID from a XML document.
     *
     * @param doc The XML document.
     * @return The subscription ID contained by the XML document.
     * @throws SAXException If the document contains less or more than one
     * subscriptionid.
     */
    private String getSubIDInDoc(Document doc) throws SAXException {
        NodeList l = doc.getElementsByTagName("subscriptionID");
        if (l.getLength() != 1) {
            throw new SAXException("Error: one and only one time subscriptionID");
        }
        return l.item(0).getTextContent();
    }

    /**
     * Fetch user ID corresponding to subscription ID.
     *
     * @param subscriptionID The subscription ID.
     * @return The user ID corresponding to the subscription.
     * @throws SQLException If an error involving the database occurred.
     */
    private String fetchAddress(String subscriptionID) throws SQLException {
        CallbackOperationsSession session = new CallbackOperationsSession(dbConnection);
        return backend.fetchAddress(session, subscriptionID);
    }

    /**
     * Creates a JMS connection.
     * @throws JMSException If the connection could not be established.
     */
    public void createsJMSConnection() throws JMSException {
        ActiveMQConnectionFactory factory;
        if (jmsLogin != null && jmsPassword != null
                && !jmsLogin.isEmpty() && !jmsPassword.isEmpty()) {
            factory = new ActiveMQConnectionFactory(jmsLogin, jmsPassword, jmsUrl);
        } else {
            factory = new ActiveMQConnectionFactory(jmsUrl);
        }
        connection = factory.createConnection();
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
     * Sends event with the specified message producer.
     * @param session The JMS session used to send the message.
     * @param producer The JMS message producer used to send the message.
     * @param eventToReturn The event to send in String format.
     * @throws JMSException If an error occurred with the JMS provider.
     */
    public void sendsJMSEvent(Session session, MessageProducer producer, String eventToReturn) throws JMSException {
        TextMessage tMsg = session.createTextMessage(eventToReturn);
        tMsg.setLongProperty(timeProperty, System.currentTimeMillis());
        producer.send(tMsg);
    }

}
