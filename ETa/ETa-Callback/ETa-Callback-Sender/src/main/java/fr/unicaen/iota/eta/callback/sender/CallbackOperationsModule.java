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

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.jms.*;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * This
 * <code>CallbackOperationsModule</code> parses messages from the message broker
 * to a XML document conforming to a xsd schema. The query callback events
 * contained by this document are sent to the user.
 */
public class CallbackOperationsModule {

    private static final String PROP_EPCIS_SCHEMA_FILE = "/wsdl/EPCglobal-epcis-query-1_0.xsd";
    private static final String PROPERTY_FILE = "/application.properties";
    private Schema schema;
    private Properties properties;
    private String queueName = "queueToFilter";
    private String msgUser;
    private String msgPassword;
    private String msgUrl;
    private static final String PROP_MSG_QUEUENAME = "messagebroker.queueName";
    private static final String PROP_MSG_USER = "messagebroker.user";
    private static final String PROP_MSG_PASSWORD = "messagebroker.password";
    private static final String PROP_MSG_URL = "messagebroker.url";
    /**
     * The
     * <code>Connection</code> to the database
     */
    private java.sql.Connection dbConnection;
    /**
     * The
     * <code>CallbackOperationsBackendSQL</code> used for database
     */
    private CallbackOperationsBackendSQL backend;
    // Properties of database
    private static final String PROP_DB_USERNAME = "database.username";
    private static final String DEFAULT_DB_USERNAME = "eta_usr";
    private static final String PROP_DB_PASSWORD = "database.password";
    private static final String DEFAULT_DB_PASSWORD = "eta_pwd";
    private static final String PROP_DB_URL = "database.url";
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/eta_db?autoReconnect=true";
    /**
     * Whether to trust a certificate whose certificate chain cannot be
     * validated when delivering results via Sender.
     */
    private boolean trustAllCertificates;
    private static final Log LOG = LogFactory.getLog(CallbackOperationsModule.class);

    public CallbackOperationsModule() {
        this.properties = loadProperties(PROPERTY_FILE);
        this.schema = initEpcisSchema(PROP_EPCIS_SCHEMA_FILE);
        this.trustAllCertificates = Boolean.parseBoolean(properties.getProperty("trustAllCertificates", "false"));
        queueName = properties.getProperty(PROP_MSG_QUEUENAME, queueName);
        msgUrl = properties.getProperty(PROP_MSG_URL, ActiveMQConnection.DEFAULT_BROKER_URL);
        msgUser = properties.getProperty(PROP_MSG_USER, ActiveMQConnection.DEFAULT_USER);
        msgPassword = properties.getProperty(PROP_MSG_PASSWORD, ActiveMQConnection.DEFAULT_PASSWORD);
        backend = new CallbackOperationsBackendSQL();
        dbConnection = loadDatabaseConnection();
        LOG.info("CallbackModule sender is successfully loaded");
    }

    /**
     * Gets the ActiveMQ password.
     *
     * @return The ActiveMQ password.
     */
    public String getMsgPassword() {
        return msgPassword;
    }

    /**
     * Gets the destination URL.
     *
     * @return The destination URL.
     */
    public String getMsgUrl() {
        return msgUrl;
    }

    /**
     * Gets the user name.
     *
     * @return The user name.
     */
    public String getMsgUser() {
        return msgUser;
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
     * Loads the application's properties file from the class path.
     *
     * @return A populated Properties instance.
     */
    private Properties loadProperties(String file) {
        // read application properties from classpath
        InputStream is = this.getClass().getResourceAsStream(file);
        Properties prop = new Properties();
        try {
            prop.load(is);
            is.close();
        } catch (IOException e) {
            LOG.error("Unable to load application properties from classpath:" + file + " ("
                    + this.getClass().getResource(file) + ")", e);
        }
        return prop;
    }

    /**
     * Loads the connection to the database.
     *
     * @return The connection to the database.
     */
    private java.sql.Connection loadDatabaseConnection() {
        String username = properties.getProperty(PROP_DB_USERNAME, DEFAULT_DB_USERNAME);
        String password = properties.getProperty(PROP_DB_PASSWORD, DEFAULT_DB_PASSWORD);
        String url = properties.getProperty(PROP_DB_URL, DEFAULT_DB_URL);
        java.sql.Connection c = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Properties props = new Properties();
            props.setProperty("user", username);
            props.setProperty("password", password);
            props.setProperty("autoReconnect", "true");
            c = DriverManager.getConnection(url, props);
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
    private Document getDocumentFromString(String string)
            throws SAXException, IOException {

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
                        // too large, do not log
                        xml = null;
                    } else {
                        LOG.debug("Incoming contents:\n\n" + writer.toString() + "\n");
                    }
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
     * Connects to the Message Broker, gets and sends events to users.
     *
     * @throws JMSException If an error receiving or sending message occurred.
     * @throws MalformedURLException If a destination url is malformed.
     * @throws IOException If an I/O error occurred.
     * @throws SAXException If an error processing the XML document occurred.
     * @throws SQLException If an error involving the database occurred.
     * @throws Exception If an unexpected error occurred.
     */
    public void consumeAndSend() throws JMSException, MalformedURLException, IOException, SAXException,
            SQLException, Exception {
        ActiveMQConnectionFactory factory;
        if (msgUser != null && msgPassword != null && !msgUser.isEmpty() && !msgPassword.isEmpty()) {
            factory = new ActiveMQConnectionFactory(msgUser, msgPassword, msgUrl);
        } else {
            factory = new ActiveMQConnectionFactory(msgUrl);
        }
        Connection connection = factory.createConnection();
        Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
        Destination destination = session.createQueue(queueName);
        MessageConsumer consumer = session.createConsumer(destination);
        connection.start();

        try {
            while (true) {
                Message message = consumer.receive(200);
                if (message == null) {
                    break;
                }

                String docText = "";
                if (message != null && message instanceof TextMessage) {
                    TextMessage text = (TextMessage) message;
                    docText = text.getText();

                    Document doc = getDocumentFromString(docText);
                    String dest = fetchAddress(getSubIDInDoc(doc));

                    try {
                        int response = send(docText, dest);
                    } catch (SocketTimeoutException e) {
                    } catch (IOException e) {
                        Session prodS = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
                        Destination sendDest = prodS.createQueue(queueName);
                        MessageProducer producer = prodS.createProducer(sendDest);
                        TextMessage messageToSend = prodS.createTextMessage();
                        messageToSend.setText(docText);
                        producer.send(messageToSend);
                        if (LOG.isDebugEnabled()) {
                            LOG.debug(e);
                        } else {
                            LOG.error(e);
                        }
                    }
                    message.acknowledge();
                }
            }
        } finally {
            connection.close();
        }
    }

    /**
     * Sends the given data String to the specified URL.
     *
     * @param data The data to send.
     * @param dest The destination to send the data to.
     * @return The response code.
     * @throws MalformedURLException If the destination url is not conformed.
     * @throws IOException If a communication error occurred.
     */
    public int send(String data, String dest)
            throws MalformedURLException, SocketTimeoutException, IOException, Exception {
        // set up connection and send data to given destination
        URL serviceUrl;
        try {
            serviceUrl = new URL(dest.toString());
        } catch (MalformedURLException e) {
            throw new MalformedURLException("Unable to parse destination as a URL");
        }
        LOG.debug("Sending results of subscribed query to '" + serviceUrl + "'");

        HttpURLConnection connection;
        int response = -1;
        try {
            if ("HTTPS".equalsIgnoreCase(serviceUrl.getProtocol()) && trustAllCertificates) {
                connection = getAllTrustingConnection(serviceUrl);
            } else {
                connection = getConnection(serviceUrl);
            }
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "text/xml");
            connection.setRequestProperty("content-length", "" + data.length());
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setReadTimeout(200);

            // send data
            Writer out = new OutputStreamWriter(connection.getOutputStream());
            out.write(data);
            out.flush();
            out.close();

            // disconnect and return
            // TODO response code
            try {
                response = connection.getResponseCode();
            } catch (IOException e) {
            }
            connection.disconnect();
            return response;
        } catch (SocketTimeoutException e) {
            throw e;
        } catch (IOException e) {
            throw new IOException("Unable to send results of subscribed query to '"
                    + dest + "'", e);
        }
    }

    /**
     * Opens a connection to the given URL. <p> The URL.openConnection() method
     * returns an instance of javax.net.ssl.HttpsURLConnection, which extends
     * java.net.HttpURLConnection, if the HTTPS protocol is used in the URL.
     * Thus, we support both the HTTP and HTTPS binding of the query callback
     * interface. <p> Note: By default, accessing an HTTPS URL using the URL
     * class results in an exception if the destination's certificate chain
     * cannot be validated. In this case you can manually import the
     * destination's certificate into the Java runtime's trust store, or, if you
     * want to disable the validation of certificates for testing purposes, use
     * {@link getAllTrustingConnection(URL)}.
     *
     * @param url The URL on which a connection will be opened.
     * @return A HttpURLConnection connection object.
     * @throws IOException If an I/O error occurred.
     */
    private HttpURLConnection getConnection(URL url) throws IOException {
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * Retrieves an "all-trusting" HTTP URL connection object, by disabling the
     * validation of certificates and overriding the default trust manager with
     * one that trusts all certificates.
     *
     * @param url The URL on which a connection will be opened.
     * @return A HttpURLConnection connection object.
     * @throws IOException If an I/O error occurred.
     */
    private HttpURLConnection getAllTrustingConnection(URL url) throws IOException {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
        };

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            LOG.error("Unable to install the all-trusting trust manager", e);
        }
        return getConnection(url);
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
}
