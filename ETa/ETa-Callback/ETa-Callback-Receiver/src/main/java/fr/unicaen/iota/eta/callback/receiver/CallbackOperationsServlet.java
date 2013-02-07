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
package fr.unicaen.iota.eta.callback.receiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import javax.jms.JMSException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * This
 * <code>CallbackOperationsServlet</code> accepts and analyzes HTTP POST
 * requests and delegates them to the
 * <code>CallbackOperationsModule</code>. This servlet also initializes the
 * <code>CallbackOperationsModule</code> properly and returns a simple
 * information page upon GET requests.
 */
public class CallbackOperationsServlet extends HttpServlet {

    /**
     * The
     * <code>CallbackOperationsModule</code> object.
     */
    private CallbackOperationsModule callbackOperationsModule;
    private static final Log LOG = LogFactory.getLog(CallbackOperationsServlet.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        LOG.debug("Init the Callback context");
        callbackOperationsModule = new CallbackOperationsModule();
    }

    /**
     * Returns a simple information page.
     *
     * @see
     * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest,
     * javax.servlet.http.HttpServletResponse)
     * @param req The HttpServletRequest.
     * @param rsp The HttpServletResponse.
     * @throws IOException If an error occurred while writing the response.
     */
    @Override
    public void doGet(final HttpServletRequest req, final HttpServletResponse rsp) throws IOException {
        final PrintWriter out = rsp.getWriter();

        // return an HTML info page
        rsp.setContentType("text/html");

        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
        out.println("   \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html>");
        out.println("<head><title>Gateway Callback Service</title></head>");
        out.println("<body>");
        out.println("<p>This service receives and sends EPCIS Callbacks using HTTP POST requests.<br />");
        out.println("The payload of the HTTP POST request is expected to be an XML document conforming to the EPCISDocument schema.</p>");
        out.println("<p>For further information refer to the xml schema files or check the Example <br />");
        out.println("in 'EPC Information Services (EPCIS) Version 1.0 Specification', Section 9.6.</p>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    /**
     * Implements the callback operation. Takes HTTP POST request, extracts the
     * payload into an XML document, validates the document against the EPCIS
     * schema, and sends the events to the corresponding user. Errors are caught
     * and returned as simple plaintext messages via HTTP.
     *
     * @param req The HttpServletRequest.
     * @param rsp The HttpServletResponse.
     * @throws IOException If an error occurred while validating the request or
     * writing the response.
     */
    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse rsp) throws IOException {
        LOG.debug("Gateway Callback Interface invoked.");
        rsp.setContentType("text/plain");
        final PrintWriter out = rsp.getWriter();
        InputStream is;
        is = req.getInputStream();

        try {
            Document doc = callbackOperationsModule.getDocumentFromInputStream(is);

            DOMSource source = new DOMSource(doc);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            StreamResult result = new StreamResult(new StringWriter());
            transformer.transform(source, result);
            String msg = result.getWriter().toString();
            callbackOperationsModule.send(msg);
            rsp.setStatus(HttpServletResponse.SC_OK);
            out.println("Event received by Callback Interface module");
            LOG.info("Event received.");
        } catch (SAXException e) {
            String response = "An error processing the XML document occurred";
            LOG.error(response, e);
            out.println(response);
            rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (TransformerConfigurationException e) {
            String response = "Transformer creation error occured";
            LOG.error(response, e);
            out.println(response);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (TransformerException e) {
            String response = "Transforming error occured";
            LOG.error(response, e);
            out.println(response);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (JMSException e) {
            String response = "An error sending events occured ";
            LOG.error(response, e);
            out.println(response);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            String response = "An unexpected error occured";
            LOG.error(response, e);
            out.println(response);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
