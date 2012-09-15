/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.discovery.server.query;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.discovery.server.hibernate.User;
import fr.unicaen.iota.discovery.server.util.Constants;
import fr.unicaen.iota.discovery.server.util.XACMLUtils;
import fr.unicaen.iota.xacml.pep.MethodNamesCapture;
import fr.unicaen.iota.xacml.pep.MethodNamesQuery;
import fr.unicaen.iota.xacml.pep.XACMLDSEvent;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to manage the DS access control policy
 */
public class DSPEP implements MethodNamesQuery, MethodNamesCapture {

    private static final Log log = LogFactory.getLog(DSPEP.class);

    /**
     * process access control policy for the Hello method.
     * @param userId      connected user
     * @param partnerId   corresponding partnerId
     * @param module      Query, Capture or Admin
     * @return
     */
    @Override
    public int hello(String userId, String partnerId, String module) {
        log.trace("process hello policy for user: " + userId);
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
    @Override
    public int partnerInfo(String userId, String partnerId, String module) {
        log.trace("process partnerInfo policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerInfo", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventLookup method for each retrieved event retriefed.
     * @param userId    connected user
     * @param dsEvent   the event
     * @param module    Query, Capture or Admin
     * @return
     */
    @Override
    public int eventLookup(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventLookup policy for user: " + userId);
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
    @Override
    public int eventInfo(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventInfo policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventInfo", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int eventCreate(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process eventCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventCreate", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int voidEvent(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process voidEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "voidEvent", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int multipleEventCreate(String userId, XACMLDSEvent dsEvent, String module) {
        log.trace("process multipleEventCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", dsEvent, module);
        return processXACMLRequest(eventRequest);
    }

    public int userLookup(String userId, String partner, String module) {
        log.trace("process userLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userCreate(String userId, String partner, String module) {
        log.trace("process userCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userInfo(String userId, String partner, String module) {
        log.trace("process userInfo policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userUpdate(String userId, String partner, String module) {
        log.trace("process userUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int userDelete(String userId, String partner, String module) {
        log.trace("process userDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerUpdate(String userId, String partner, String module) {
        log.trace("process partnerUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerUpdate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerLookup(String userId, String partner, String module) {
        log.trace("process partnerLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerLookup", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerDelete(String userId, String partner, String module) {
        log.trace("process partnerDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerDelete", partner, module);
        return processXACMLRequest(eventRequest);
    }

    public int partnerCreate(String userId, String partner, String module) {
        log.trace("process partnerCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerCreate", partner, module);
        return processXACMLRequest(eventRequest);
    }

    private String sendXACMLRequest(RequestCtx xacmlReq) throws IOException {
        Socket socket = new Socket(Constants.XACML_ADDRESS, Constants.XACML_PORT);
        BufferedReader socketReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        xacmlReq.encode(socket.getOutputStream());
        String response = socketReader.readLine();
        socket.close();
        return response;
    }

    private int processXACMLRequest(EventRequest eventRequest) {
        String response = "DENY";
        try {
            if (!Constants.USE_XACML) {
                return Result.DECISION_PERMIT;
            }
            response = sendXACMLRequest(eventRequest.createRequest());
        } catch (IOException ex) {
            log.error(null, ex);
        }
        return XACMLUtils.createXACMLResponse(response);
    }

    public boolean isRootAccess(String sessionId, String module) {
        User u = fr.unicaen.iota.discovery.server.util.Session.getUser(sessionId);
        log.trace("process checkRootAccess policy for user: " + u.getUserID());
        EventRequest eventRequest = new EventRequest(u.getUserID(), "superadmin", u.getPartner().getPartnerID(), module);
        return processXACMLRequest(eventRequest) == Result.DECISION_PERMIT;
    }
}
