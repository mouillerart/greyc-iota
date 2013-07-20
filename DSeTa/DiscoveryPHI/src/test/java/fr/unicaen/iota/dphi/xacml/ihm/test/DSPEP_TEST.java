/*
 *  This program is a part of the IoTa project.
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
package fr.unicaen.iota.dphi.xacml.ihm.test;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.pep.XACMLDSEvent;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to manage the DS access control policy
 */
public class DSPEP_TEST {

    private static String url;

    private static final Log log = LogFactory.getLog(DSPEP_TEST.class);

    public static void main(String[] args) {
        url = args[0];
        XACMLDSEvent dSEvent = new XACMLDSEvent("epcistest", "bizstep", "urn:epc:id:sgtin:1.3.325", "object", new Date());
        int result = eventLookup("epcistest", dSEvent, "Query");
        log.trace(result);
    }

    /**
     * process access control policy for the Hello method.
     * @param userId      connected user
     * @param ownerId   corresponding ownerId
     * @param module      Query, Capture or Admin
     * @return
     */
    public static int hello(String userId, String ownerId, String module) {
        log.trace("process hello policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", ownerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventLookup method for each retrieved event retriefed.
     * @param userId    connected user
     * @param dsEvent   the event
     * @param module    Query, Capture or Admin
     * @return
     */
    public static int eventLookup(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventInfo method.
     * @param userId    connected user
     * @param dsEvent
     * @param module
     * @return
     */
    public static int eventInfo(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventInfo", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int eventCreate(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventCreate", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int multipleEventCreate(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process multipleEventCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userLookup(String userId, String owner, String module) {
        log.trace("process userLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userCreate(String userId, String owner, String module) {
        log.trace("process userCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userInfo(String userId, String owner, String module) {
        log.trace("process userInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userUpdate(String userId, String owner, String module) {
        log.trace("process userUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userDelete(String userId, String owner, String module) {
        log.trace("process userDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerUpdate(String userId, String owner, String module) {
        log.trace("process ownerUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerUpdate", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerLookup(String userId, String owner, String module) {
        log.trace("process ownerLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerLookup", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerDelete(String userId, String owner, String module) {
        log.trace("process ownerDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerDelete", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerCreate(String userId, String owner, String module) {
        log.trace("process ownerCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerCreate", owner, module);
        return processXACMLRequest(eventRequest);
    }

    private static int processXACMLRequest(EventRequest eventRequest) {
        int response = Result.DECISION_DENY;
        try {
            String respInString = sendXACMLRequest(eventRequest.createRequest());
            response = Integer.parseInt(respInString);
        } catch (Exception ex) {
            log.error("", ex);
        }
        return response;
    }

    private static String sendXACMLRequest(RequestCtx xacmlReq) throws IOException {
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

    private static HttpURLConnection getConnection(final String contentType) throws IOException {
        URL serviceUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) serviceUrl.openConnection();
        connection.setRequestProperty("content-type", contentType);
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }
}
