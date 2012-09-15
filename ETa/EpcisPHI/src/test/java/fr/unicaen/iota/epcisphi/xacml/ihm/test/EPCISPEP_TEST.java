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
package fr.unicaen.iota.epcisphi.xacml.ihm.test;

import com.sun.xacml.ctx.RequestCtx;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to manage the epcis access control policy
 */
public class EPCISPEP_TEST {

    private static final Log log = LogFactory.getLog(EPCISPEP_TEST.class);

    public static void main(String[] args) {
        XACMLEPCISEvent epcisEvent = new XACMLEPCISEvent("anonym", "bizstep", "urn:epc:id:sgtin:1.3.325", new Date(), new Date(), "add", "object", "parent", "child", new Long(2), "readpoint", "bizLoc", "bizTrans", "disposition", null);
        int result = eventLookup("anonym", epcisEvent, "Query");
        log.trace(result);
    }

    /**
     * process access control policy for the Hello method.
     * @param userId      connected user
     * @param partnerId   corresponding partnerId
     * @param module      Query, Capture or Admin
     * @return
     */
    public static int hello(String userId, String partnerId, String module) {
        log.info("process hello policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the partnerInfo method.
     * @param userId      connected user
     * @param partnerId   partner concerned by the request
     * @param module      Query, Capture or Admin
     * @return
     */
    public static int partnerInfo(String userId, String partnerId, String module) {
        log.info("process partnerInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerInfo", partnerId, module);
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

    public static int userLookup(String userId, String partner, String module) {
        log.info("process userLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userCreate(String userId, String partner, String module) {
        log.info("process userCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userInfo(String userId, String partner, String module) {
        log.info("process userInfo policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userUpdate(String userId, String partner, String module) {
        log.info("process userUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int userDelete(String userId, String partner, String module) {
        log.info("process userDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerUpdate(String userId, String partner, String module) {
        log.info("process partnerUpdate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerLookup(String userId, String partner, String module) {
        log.info("process partnerLookup policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerDelete(String userId, String partner, String module) {
        log.info("process partnerDelete policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public static int partnerCreate(String userId, String partner, String module) {
        log.info("process partnerCreate policy for user : " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    private static String sendXACMLRequest(RequestCtx xacmlReq) throws IOException {
        Socket socket = new Socket("localhost", 9998);
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        xacmlReq.encode(socket.getOutputStream());
        String response = socketReader.readLine();
        socket.close();
        return response;
    }

    private static int processXACMLRequest(EventRequest eventRequest) {
        String response = "DENY";
        try {
            response = sendXACMLRequest(eventRequest.createRequest());
        } catch (IOException ex) {
            log.error(null, ex);
        }
        return XACMLUtils.createXACMLResponse(response);
    }
}
