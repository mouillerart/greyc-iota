/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
 *  Copyright © 2007       ETH Zurich
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
/*
 * Derived from org.fosstrak.epcis.repository.capture.CaptureOperationsServlet
 */
package fr.unicaen.iota.eta.capture;

import fr.unicaen.iota.eta.constants.Constants;
import fr.unicaen.iota.sigma.client.SigMaClient;
import fr.unicaen.iota.xi.client.EPCISPEP;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

/**
 * This CaptureOperationsServlet accepts and analyzes HTTP POST requests and
 * delegates them to the appropriate handler methods in the
 * CaptureOperationsModule. This servlet also initializes the
 * CaptureOperationsModule properly and returns a simple information page upon
 * GET requests.
 */
public class CaptureOperationsServlet extends HttpServlet {

    private static final Log LOG = LogFactory.getLog(CaptureOperationsServlet.class);
    private CaptureOperationsModule captureOperationsModule;

    /**
     * {@inheritDoc}
     */
    @Override
    public void init() {
        // ================= SET CAPTURE MODULE ====================
        LOG.debug("Fetching capture operations module from servlet context ...");
        CaptureOperationsModule cm = (CaptureOperationsModule) getServletContext().getAttribute("captureOperationsModule");
        if (cm == null) {
            LOG.warn("Capture operations module not found - initializing manually");
            cm = new CaptureOperationsModule();
            cm.setEpcisSchemaFile(Constants.EPCIS_SCHEMA_FILE);
            cm.setEpcisMasterDataSchemaFile(Constants.EPCIS_MASTER_DATA_SCHEMA_FILE);
            EPCISPEP epcisPEP = new EPCISPEP(Constants.XACML_URL);
            cm.setCaptureCheck(new CaptureCheck(epcisPEP));
            getServletContext().setAttribute("captureOperationsModule", cm);
            SigMaClient sigMaClient = new SigMaClient(Constants.SIGMA_URL);
            cm.setSigMaClient(sigMaClient);
        } else {
            LOG.debug("Capture module found");
        }
        setCaptureOperationsModule(cm);
    }

    public void setCaptureOperationsModule(CaptureOperationsModule captureOperationsModule) {
        this.captureOperationsModule = captureOperationsModule;
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
        out.println("<head><title>EPCIS Capture Service</title></head>");
        out.println("<body>");
        out.println("<p>This service captures EPCIS events sent to it using HTTP POST requests.<br />");
        out.println("The payload of the HTTP POST request is expected to be an XML document conforming to the EPCISDocument schema.</p>");
        out.println("<p>For further information refer to the xml schema files or check the Example <br />");
        out.println("in 'EPC Information Services (EPCIS) Version 1.0 Specification', Section 9.6.</p>");
        out.println("</body>");
        out.println("</html>");
        out.flush();
        out.close();
    }

    /**
     * Implements the EPCIS capture operation. Takes HTTP POST request, extracts
     * the payload into an XML document, validates the document against the
     * EPCIS schema, and captures the EPCIS events given in the document. Errors
     * are caught and returned as simple plaintext messages via HTTP.
     *
     * @param req The HttpServletRequest.
     * @param rsp The HttpServletResponse.
     * @throws IOException If an error occurred while validating the request or
     * writing the response.
     */
    @Override
    public void doPost(final HttpServletRequest req, final HttpServletResponse rsp) throws IOException {
        LOG.debug("EPCIS Capture Interface invoked.");
        rsp.setContentType("text/plain");
        final PrintWriter out = rsp.getWriter();
        LOG.debug("EPCIS validating Event ...");
        // check if we have a POST request with form parameters
        if ("application/x-www-form-urlencoded".equalsIgnoreCase(req.getContentType())) {
            // check if the 'event' or 'dbReset' form parameter are given
            String event = req.getParameter("event");
            String dbReset = req.getParameter("dbReset");
            if (event != null) {
                LOG.debug("Found deprecated 'event=' parameter. Refusing to process request.");
                String msg = "Starting from version 0.2.2, the EPCIS repository does not accept the EPCISDocument "
                        + "in the HTTP POST form parameter 'event' anymore. Please provide the EPCISDocument as HTTP POST payload instead.";
                rsp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                out.println(msg);
            } else if (dbReset != null && dbReset.equalsIgnoreCase("true")) {
                LOG.debug("Found 'dbReset' parameter set to 'true'.");
                doDbReset(rsp);
            }
            out.flush();
            out.close();
        } else {
            LOG.debug("EPCIS saving and publishing event ...");
            try {
                captureOperationsModule.doCapture(req, rsp);
            } catch (SAXException e) {
                String msg = "Unable to parse incoming XML due to error: " + e.getMessage();
                LOG.debug(msg, e);
                rsp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            } catch (final Exception e) {
                String msg = "The repository is unable to handle the request due to an internal error.";
                LOG.error(msg, e);
                rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }

    /**
     * Performs database reset by querying EPCIS.
     *
     * @param rsp The HTTP response
     * @throws IOException
     */
    private void doDbReset(final HttpServletResponse rsp) throws IOException {
        rsp.setContentType("text/plain");
        final PrintWriter out = rsp.getWriter();
        try {
            captureOperationsModule.doDbReset(rsp, out);
        } catch (UnsupportedOperationException e) {
            String msg = "'dbReset' operation not allowed!";
            LOG.debug(msg);
            rsp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            out.println(msg);
        } catch (Exception e) {
            String msg = "An unexpected error occurred";
            LOG.error(msg, e);
            rsp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println(msg);
        }
    }
}
