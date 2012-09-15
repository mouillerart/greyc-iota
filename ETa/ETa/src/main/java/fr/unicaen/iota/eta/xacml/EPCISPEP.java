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
package fr.unicaen.iota.eta.xacml;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.eta.constants.Constants;
import fr.unicaen.iota.eta.user.User;
import fr.unicaen.iota.xacml.pep.MethodNamesCapture;
import fr.unicaen.iota.xacml.pep.MethodNamesQuery;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISMasterData;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EPCISPEP implements MethodNamesCapture, MethodNamesQuery {

    private static final Log log = LogFactory.getLog(EPCISPEP.class);

    @Override
    public int hello(String userId, String partnerId, String module) {
        log.debug("process hello policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int queryEvent(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.debug("process queryEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "queryEvent", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int queryMasterData(String userId, XACMLEPCISMasterData epcisMasterData, String module) {
        log.debug("process queryMasterData policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "queryMasterData", epcisMasterData, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int subscribe(String userId, String partnerId, String module) {
        log.debug("process subscribe policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "subscribe", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int unsubscribe(String userId, String partnerId, String module) {
        log.debug("process unsubscribe policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "unsubscribe", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int captureEvent(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.debug("process captureEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "captureEvent", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int captureMasterData(String userId, XACMLEPCISMasterData epcisMasterData, String module) {
        log.debug("process captureMasterDataEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "captureMasterDataEvent", epcisMasterData, module);
        return processXACMLRequest(eventRequest);
    }

    public int userLookup(String userId, String partner, String module) {
        log.debug("process userLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userCreate(String userId, String partner, String module) {
        log.debug("process userCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userInfo(String userId, String partner, String module) {
        log.debug("process userInfo policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userUpdate(String userId, String partner, String module) {
        log.debug("process userUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userDelete(String userId, String partner, String module) {
        log.debug("process userDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerUpdate(String userId, String partner, String module) {
        log.debug("process partnerUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerLookup(String userId, String partner, String module) {
        log.debug("process partnerLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerDelete(String userId, String partner, String module) {
        log.debug("process partnerDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerCreate(String userId, String partner, String module) {
        log.debug("process partnerCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public boolean isRootAccess(String sessionId, String module) {
        User user = fr.unicaen.iota.eta.user.Session.getUser(sessionId);
        log.debug("process checkRootAccess policy for user: " + user);
        EventRequest eventRequest = new EventRequest(user.getUserID(), "superadmin", user.getPartnerID(), module);
        return processXACMLRequest(eventRequest) == Result.DECISION_PERMIT;
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
            log.error("", ex);
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
