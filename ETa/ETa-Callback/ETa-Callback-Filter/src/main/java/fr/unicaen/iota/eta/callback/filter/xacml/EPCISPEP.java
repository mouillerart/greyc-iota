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
package fr.unicaen.iota.eta.callback.filter.xacml;

import com.sun.xacml.ctx.RequestCtx;
import fr.unicaen.iota.eta.callback.filter.constants.Constants;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The XACML PEP.
 */
public class EPCISPEP {

    private static final Log log = LogFactory.getLog(EPCISPEP.class);

    public int hello(String userId, String partnerId, String module) {
        log.debug("process hello policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    public int queryEvent(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.debug("process queryEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "queryEvent", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public int queryMetaData(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.debug("process queryMetaData policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "queryMetaData", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * Processes an event request and returns XACML response code.
     *
     * @param eventRequest The event request.
     * @return The XACML response code.
     */
    private int processXACMLRequest(EventRequest eventRequest) {
        String response = "DENY";
        try {
            response = sendXACMLRequest(eventRequest.createRequest());
        } catch (IOException ex) {
            log.error(ex);
        }
        return XACMLUtils.createXACMLResponse(response);
    }

    /**
     * Sends XACML request to the XACML module and gets the response.
     *
     * @param xacmlReq The XACML request to send.
     * @return The XACML response.
     * @throws IOException If an I/O error occurred.
     */
    private String sendXACMLRequest(RequestCtx xacmlReq) throws IOException {
        HttpURLConnection httpConnection = getConnection("text/plain");
        log.debug("Sending XACML request...");
        xacmlReq.encode(httpConnection.getOutputStream());
        log.debug("Getting XACML response...");
        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpServletResponse.SC_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        } else {
            log.error("XACML module servlet response: " + responseCode);
            return "DENY";
        }
    }

    /**
     * Opens a connection to the xacml module.
     *
     * @param contentType The HTTP content-type, e.g.,
     * <code>text/xml</code>
     * @return The HTTP connection object.
     * @throws IOException If an error occurred connecting to the XACML module.
     */
    private HttpURLConnection getConnection(final String contentType) throws IOException {
        URL serviceUrl = new URL(Constants.XACML_URL);
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
        connection.setRequestProperty("content-type", contentType);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
}
