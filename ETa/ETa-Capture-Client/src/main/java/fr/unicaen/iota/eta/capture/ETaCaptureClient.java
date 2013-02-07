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
package fr.unicaen.iota.eta.capture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.captureclient.CaptureClient;
import org.fosstrak.epcis.captureclient.CaptureClientException;
import org.fosstrak.epcis.model.Document;
import org.fosstrak.epcis.model.EPCISDocumentType;
import org.fosstrak.epcis.model.EPCISMasterDataDocumentType;
import org.fosstrak.epcis.model.ObjectFactory;

public class ETaCaptureClient extends CaptureClient {

    private final String pksFilename;
    private final String pksPassword;
    private final String trustPksFilename;
    private final String trustPksPassword;
    private static final Log log = LogFactory.getLog(ETaCaptureClient.class);

    public ETaCaptureClient(String url, Object[] authOptions,
            String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        super(url, authOptions);
        this.pksFilename = pksFilename;
        this.pksPassword = pksPassword;
        this.trustPksFilename = trustPksFilename;
        this.trustPksPassword = trustPksPassword;
    }

    public ETaCaptureClient(String url, String pksFilename, String pksPassword,
            String trustPksFilename, String trustPksPassword) {
        super(url);
        this.pksFilename = pksFilename;
        this.pksPassword = pksPassword;
        this.trustPksFilename = trustPksFilename;
        this.trustPksPassword = trustPksPassword;
    }
    
    /**
     * {@inheritDoc 
     */
    @Override
    public int dbReset() throws CaptureClientException {
        String formParam = "dbReset=true";
        try {
            return doPost(formParam, "application/x-www-form-urlencoded");
        } catch (IOException e) {
            throw new CaptureClientException("error communicating with EPCIS cpature interface: " + e.getMessage(), e);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int capture(final InputStream xmlStream) throws CaptureClientException {
        try {
            return doPost(xmlStream, "text/xml");
        } catch (IOException e) {
            throw new CaptureClientException("error communicating with EPCIS cpature interface: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int capture(final String eventXml) throws CaptureClientException {
        try {
            return doPost(eventXml, "text/xml");
        } catch (IOException e) {
            throw new CaptureClientException("error communicating with EPCIS cpature interface: " + e.getMessage(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int capture(final Document epcisDoc) throws CaptureClientException {
        StringWriter writer = new StringWriter();
        ObjectFactory objectFactory = new ObjectFactory();
        try {
            JAXBContext context = JAXBContext.newInstance("org.fosstrak.epcis.model");
            JAXBElement<? extends Document> item;
            if (epcisDoc instanceof EPCISDocumentType) {
                item = objectFactory.createEPCISDocument((EPCISDocumentType) epcisDoc);
            } else {
                item = objectFactory.createEPCISMasterDataDocument((EPCISMasterDataDocumentType) epcisDoc);
            }
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(item, writer);
        } catch (JAXBException e) {
            throw new CaptureClientException("error serializing EPCIS Document: " + e.getMessage(), e);
        }
        return capture(writer.toString());
    }
    
    /**
     * Send data to the repository's capture operation using HTTP POST. The data
     * will be sent using the given content-type.
     * @param data The data to send.
     * @return The HTTP response message from the repository.
     * @throws IOException If an error on the HTTP layer occurred.
     * @throws CaptureClientException
     */
    private int doPost(final InputStream data, final String contentType) throws IOException, CaptureClientException {
        HttpURLConnection connection = getConnection(contentType);
        // read from input and write to output
        OutputStream os = connection.getOutputStream();
        int b;
        while ((b = data.read()) != -1) {
            os.write(b);
        }
        os.flush();
        os.close();
        return connection.getResponseCode();
    }
    
    /**
     * Send data to the repository's capture operation using HTTP POST. The data
     * will be sent using the given content-type.
     * @param data The data to send.
     * @return The HTTP response message
     * @throws IOException If an error on the HTTP layer occurred.
     */
    private int doPost(final String data, final String contentType) throws CaptureClientException, IOException {
        HttpURLConnection connection = getConnection(contentType);
        // write the data
        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream());
        wr.write(data);
        wr.flush();
        wr.close();
        return connection.getResponseCode();
    }
    
    /**
     * Opens a connection to the EPCIS capture interface.
     * @param contentType The HTTP content-type, e.g., <code>text/xml</code>
     * @return The HTTP connection object.
     */
    private HttpURLConnection getConnection(final String contentType) throws CaptureClientException, IOException {
        System.setProperty("javax.net.ssl.keyStore", pksFilename);
        System.setProperty("javax.net.ssl.keyStorePassword", pksPassword);
        System.setProperty("javax.net.ssl.trustStore", trustPksFilename);
        System.setProperty("javax.net.ssl.trustStorePassword", trustPksPassword);
        URL serviceUrl = new URL(getCaptureUrl());
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
        connection.setRequestProperty("content-type", contentType);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

}