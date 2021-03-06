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
package fr.unicaen.iota.epcisphi.xacml.ihm.test;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
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
 * This class is used to manage the epcis access control policy
 */
public class EPCISPEP_TEST {

    private static final Log log = LogFactory.getLog(EPCISPEP_TEST.class);

    private static String url;

    public static void main(String[] args) {
        url = args[0];
        XACMLEPCISEvent epcisEvent = new XACMLEPCISEvent("anonym", "bizstep", "urn:epc:id:sgtin:1.3.325", new Date(), new Date(), "add", "object", "parent", "child", new Long(2), "readpoint", "bizLoc", "bizTrans", "disposition", null);
        int result = eventLookup("anonym", epcisEvent, "Query");
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
        log.info("process hello policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", ownerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the ownerInfo method.
     * @param userId      connected user
     * @param ownerId   owner concerned by the request
     * @param module      Query, Capture or Admin
     * @return
     */
    public static int ownerInfo(String userId, String ownerId, String module) {
        log.info("process ownerInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerInfo", ownerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventLookup method for each retrieved event retriefed.
     * @param userId    connected user
     * @param epcisEvent   the event
     * @param module    Query, Capture or Admin
     * @return
     */
    public static int eventLookup(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.info("process eventLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventInfo method.
     * @param userId    connected user
     * @param epcisEvent
     * @param module
     * @return
     */
    public static int eventInfo(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.info("process eventInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventInfo", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int eventCreate(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.info("process eventCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventCreate", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int voidEvent(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.info("process voidEvent policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "voidEvent", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int multipleEventCreate(String userId, XACMLEPCISEvent epcisEvent, String module) {
        log.info("process multipleEventCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", epcisEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userLookup(String userId, String owner, String module) {
        log.info("process userLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userCreate(String userId, String owner, String module) {
        log.info("process userCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userInfo(String userId, String owner, String module) {
        log.info("process userInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userUpdate(String userId, String owner, String module) {
        log.info("process userUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userDelete(String userId, String owner, String module) {
        log.info("process userDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerUpdate(String userId, String owner, String module) {
        log.info("process ownerUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerUpdate", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerLookup(String userId, String owner, String module) {
        log.info("process ownerLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerLookup", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerDelete(String userId, String owner, String module) {
        log.info("process ownerDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerDelete", owner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int ownerCreate(String userId, String owner, String module) {
        log.info("process ownerCreate policy for user : " + userId);
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
