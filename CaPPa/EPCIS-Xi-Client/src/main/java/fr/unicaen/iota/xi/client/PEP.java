/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xi.client;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PEP {

    private String url;

    private static final Log log = LogFactory.getLog(PEP.class);

    public PEP(String url) {
        this.url = url;
    }

    /**
     * Processes an event request and returns XACML response code.
     *
     * @param eventRequest The event request.
     * @return The XACML response code.
     */
    public int processXACMLRequest(EventRequest eventRequest) {
        int response = Result.DECISION_DENY;
        try {
            String respInString = sendXACMLRequest(eventRequest.createRequest());
            response = Integer.parseInt(respInString);
        } catch (Exception ex) {
            log.error("", ex);
        }
        return response;
    }

    /**
     * Sends XACML request to the XACML module and gets the response.
     *
     * @param xacmlReq The XACML request to send.
     * @return The XACML response.
     * @throws IOException If an I/O error occurred.
     */
    private String sendXACMLRequest(RequestCtx xacmlReq) throws IOException {
        HttpURLConnection httpConnection = getConnection("text/plain", url);
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
            return String.valueOf(Result.DECISION_DENY);
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
    private HttpURLConnection getConnection(final String contentType, String url) throws IOException {
        URL serviceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
        connection.setRequestProperty("content-type", contentType);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

}
