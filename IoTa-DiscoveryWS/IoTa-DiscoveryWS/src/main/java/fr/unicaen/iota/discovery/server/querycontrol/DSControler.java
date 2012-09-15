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
package fr.unicaen.iota.discovery.server.querycontrol;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.discovery.server.hibernate.Event;
import fr.unicaen.iota.discovery.server.hibernate.EventToPublish;
import fr.unicaen.iota.discovery.server.hibernate.Partner;
import fr.unicaen.iota.discovery.server.hibernate.User;
import fr.unicaen.iota.discovery.server.query.DSPEP;
import fr.unicaen.iota.discovery.server.query.QueryOperationsModule;
import fr.unicaen.iota.discovery.server.util.EPCUtilities.InvalidFormatException;
import fr.unicaen.iota.discovery.server.util.*;
import fr.unicaen.iota.xacml.policy.Module;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DSControler {

    private static final Log log = LogFactory.getLog(DSControler.class);
    private QueryOperationsModule queryOperationsModule = new QueryOperationsModule();
    private DSPEP dspep = new DSPEP();

    public String hello() {
        return Constants.SERVICE_ID;
    }

    public List<Event> eventLookup(String sessionId, String epc) throws ProtocolException {
        log.trace("eventLookup method called.");
        try {
            new EPCUtilities().checkEpcOrUri(epc);
        } catch (InvalidFormatException ex) {
            throw new ProtocolException(2000, ex.getMessage());
        }
        List<Event> eventListTmp = queryOperationsModule.eventLookup(epc);
        List<Event> eventList = new ArrayList<Event>();
        User u = Session.getUser(sessionId);
        for (Event e : eventListTmp) {
            int resp = dspep.eventLookup(u.getUserID(), XACMLUtils.createXACMLEvent(e), Module.queryModule.getValue());
            if (resp == Result.DECISION_PERMIT) {
                eventList.add(e);
            }
        }
        return eventList;
    }

    public String userLogin(String login, String passwd) throws ProtocolException {
        log.trace("userLogin method called.");
        List<User> users = queryOperationsModule.userLookup(login, passwd);
        if (users.isEmpty()) {
            throw new ProtocolException(2002, "user not found");
        }
        String sessionId = Session.openSession(users.iterator().next());
        return sessionId;
    }

    public void userLogout(String sessionId) {
        log.trace("userLogut method called.");
        Session.closeSession(sessionId);
    }

    public List<User> userLookup(String sessionId, String userID) throws ProtocolException {
        log.trace("userLookup method called.");
        User u = Session.getUser(sessionId);
        List<User> uList = queryOperationsModule.userLookup(userID);
        for (User u2 : uList) {
            int resp = dspep.userLookup(u.getUserID(), u2.getPartner().getPartnerID(), Module.administrationModule.getValue());
            if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionId, Module.administrationModule.getValue())) {
                throw new ProtocolException(2100, "Access denied for user " + userID);
            }
        }
        return uList;
    }

    public User userInfo(String sessionID, String userId) throws ProtocolException {
        log.trace("userInfo method called: " + userId);
        List<User> uList = queryOperationsModule.userLookup(userId);
        if (uList.isEmpty()) {
            throw new ProtocolException(2002, "user not found");
        }
        User u = Session.getUser(sessionID);
        int resp = dspep.userInfo(u.getUserID(), uList.get(0).getPartner().getPartnerID(), Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionID, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + userId);
        }
        if (uList.isEmpty()) {
            throw new ProtocolException(2002, "user not found");
        }
        return uList.get(0);
    }

    public int userCreate(String sessionID, String partnerId, String password, String login) throws ProtocolException {
        log.trace("userCreate method called.");
        User u = Session.getUser(sessionID);
        int resp = dspep.userCreate(u.getUserID(), partnerId, Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionID, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        if (!queryOperationsModule.userLookup(login).isEmpty()) {
            throw new ProtocolException(2002, "user already exists");
        }
        List<Partner> pList = null;
        if ((pList = queryOperationsModule.partnerLookup(partnerId)).isEmpty()) {
            throw new ProtocolException(2002, "partner not found");
        }
        User cUser = new User();
        cUser.setId(0);
        cUser.setPasswd(password);
        cUser.setUserID(login);
        cUser.setLogin(login);
        cUser.setDate(new Timestamp(new Date().getTime()));
        cUser.setPartner(pList.get(0));
        queryOperationsModule.userCreate(cUser);
        int id = queryOperationsModule.userLookup(login).get(0).getId();
        return id;
    }

    public void userUpdate(String sessionID, String partnerId, int uid, String userId, String password) throws ProtocolException {
        log.trace("userUpdate method called.");
        User u = Session.getUser(sessionID);
        int resp = dspep.userInfo(u.getUserID(), partnerId, Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionID, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + userId);
        }
        User u2 = queryOperationsModule.userLookup(uid);
        if (u2 == null) {
            throw new ProtocolException(2002, "user not found");
        }
        u2.setLogin(userId);
        u2.setUserID(userId);
        u2.setPasswd(password);
        boolean result = queryOperationsModule.userUpdate(u2);
        if (!result) {
            throw new ProtocolException(2002, "internal error");
        }
    }

    public void userDelete(String sessionID, String userId) throws ProtocolException {
        log.trace("userDelete method called.");
        List<User> uList = queryOperationsModule.userLookup(userId);
        if (uList == null || uList.isEmpty()) {
            throw new ProtocolException(2002, "user not found");
        }
        for (User u2 : uList) {
            User u = Session.getUser(sessionID);
            int resp = dspep.userDelete(u.getUserID(), u2.getPartner().getPartnerID(), Module.administrationModule.getValue());
            if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionID, Module.administrationModule.getValue())) {
                throw new ProtocolException(2100, "Access denied for user " + userId);
            }
            queryOperationsModule.userDelete(u2);
        }
    }

    public List<Partner> partnerLookup(String sessionId, String partnerId) throws ProtocolException {
        log.trace("partnerLookup method called.");
        User u = Session.getUser(sessionId);
        int resp = dspep.partnerLookup(u.getUserID(), partnerId, Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionId, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        List<Partner> pList = queryOperationsModule.partnerLookup(partnerId);
        return pList;
    }

    public Partner partnerInfo(String sessionId, String partnerId) throws ProtocolException {
        log.trace("partnerLookup method called.");
        User u = Session.getUser(sessionId);
        int resp = dspep.partnerInfo(u.getUserID(), partnerId, Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionId, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        List<Partner> pList = queryOperationsModule.partnerLookup(partnerId);
        if (pList == null || pList.isEmpty()) {
            throw new ProtocolException(2002, "partner not found");
        }
        return pList.get(0);
    }

    public int partnerCreate(String sessionId, String partnerId, String partnerServiceType, String partnerServiceURL) throws ProtocolException {
        log.trace("partnerCreate method called: " + partnerId);
        User u = Session.getUser(sessionId);
        int resp = dspep.partnerCreate(u.getUserID(), partnerId, Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionId, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        if (!queryOperationsModule.partnerLookup(partnerId).isEmpty()) {
            throw new ProtocolException(2002, "partner already exists");
        }
        Partner partner = new Partner();
        partner.setId(0);
        partner.setActive(true);
        partner.setPartnerID(partnerId);
        partner.setDate(new Timestamp(new Date().getTime()));
        partner.setServiceType(partnerServiceType);
        partner.setServiceAddress(partnerServiceURL);
        queryOperationsModule.partnerCreate(partner);
        Partner p2 = queryOperationsModule.partnerLookup(partnerId).get(0);
        log.trace("partnerCreate method finished: " + partnerId);
        return p2.getId();
    }

    public int partnerUpdate(String sessionId, int partnerUID, String partnerId, String serviceType, String serviceUri) throws ProtocolException {
        log.trace("partnerUpdate method called.");
        User u = Session.getUser(sessionId);
        int resp = dspep.partnerUpdate(u.getUserID(), partnerId, Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionId, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        Partner p = queryOperationsModule.partnerLookup(partnerUID);
        if (p == null) {
            throw new ProtocolException(2002, "partner not found");
        }
        p.setServiceType(serviceType);
        p.setServiceAddress(serviceUri);
        boolean result = queryOperationsModule.partnerUpdate(p);
        if (!result) {
            throw new ProtocolException(2002, "internal error!");
        }
        return p.getId();
    }

    public void partnerDelete(String sessionId, String partnerId) throws ProtocolException {
        log.trace("partnerDelete method called.");
        User u = Session.getUser(sessionId);
        int resp = dspep.partnerCreate(u.getUserID(), partnerId, Module.administrationModule.getValue());
        if (resp != Result.DECISION_PERMIT && !dspep.isRootAccess(sessionId, Module.administrationModule.getValue())) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        List<Partner> pList = queryOperationsModule.partnerLookup(partnerId);
        if (pList == null || pList.isEmpty()) {
            throw new ProtocolException(2002, "partner not found");
        }
        for (Partner partner : pList) {
            queryOperationsModule.partnerDelete(partner);
        }
    }

    public Event eventInfo(String sessionID, double eventId) throws ProtocolException {
        log.trace("eventInfo method called.");
        Event e = queryOperationsModule.eventLookup(eventId);
        User u = Session.getUser(sessionID);
        int resp = dspep.eventInfo(u.getUserID(), XACMLUtils.createXACMLEvent(e), Module.queryModule.getValue());
        if (resp != Result.DECISION_PERMIT) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        return e;
    }

    public int eventCreate(String sessionId, String partnerId, String epc, String eventClass, Timestamp eventTimeStamp,
            Timestamp sourceTimeStamp, String eventType, String bizStep) throws ProtocolException {
        log.trace("eventCreate method called.");
        EPCUtilities ePCUtilities = new EPCUtilities();
        try {
            ePCUtilities.checkEpcOrUri(epc);
        } catch (InvalidFormatException ex) {
            throw new ProtocolException(2000, ex.getMessage());
        }
        List<Partner> p = queryOperationsModule.partnerLookup(partnerId);
        if (p.isEmpty()) {
            throw new ProtocolException(2002, "partner not found");
        }
        Event event = new Event();
        event.setId(0);
        event.setSourceTimeStamp(sourceTimeStamp);
        event.setEpc(epc);
        event.setEPCClass(eventClass);
        event.setEventType(eventType);
        event.setEventTimeStamp(eventTimeStamp);
        event.setBizStep(bizStep);
        event.setPartner(p.get(0));
        User u = Session.getUser(sessionId);
        int resp = dspep.eventCreate(u.getUserID(), XACMLUtils.createXACMLEvent(event), Module.captureModule.getValue());
        if (resp != Result.DECISION_PERMIT) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        if (ePCUtilities.isReferencable(epc)) {
            EventToPublish etp = new EventToPublish();
            etp.setEvent(event);
            etp.setLastupdate(Constants.DEFAULT_EVENT_TOPUBLISH_TIMESTAMP);
            event.setEventToPublish(etp);
        }
        boolean result = queryOperationsModule.eventCreate(event);
        if (!result) {
            throw new ProtocolException(2002, "internal error");
        }
        return event.getId();
    }

    public int voidEvent(String sessionId, double eventId) throws ProtocolException {
        log.trace("voidEvent method called.");
        Event event = queryOperationsModule.eventLookup(eventId);
        User u = Session.getUser(sessionId);
        int resp = dspep.voidEvent(u.getUserID(), XACMLUtils.createXACMLEvent(event), Module.captureModule.getValue());
        if (resp != Result.DECISION_PERMIT) {
            throw new ProtocolException(2100, "Access denied for user " + u.getUserID());
        }
        event.setEventType("void");
        event.setEventTimeStamp(new Timestamp(new Date().getTime()));
        boolean result = queryOperationsModule.eventUpdate(event);
        if (!result) {
            throw new ProtocolException(2002, "internal error");
        }
        return event.getId();
    }

    public void createAccount(String sessionId, String partnerId, String partnerServiceType, String partnerServiceURL, String rootUserLogin, String rootUserPass) throws ProtocolException {
        log.trace("createAccount method called.");
        if (!queryOperationsModule.partnerLookup(partnerId).isEmpty()) {
            throw new ProtocolException(2002, "partner already exists");
        }
        if (!queryOperationsModule.userLookup(rootUserLogin).isEmpty()) {
            throw new ProtocolException(2002, "user already exists");
        }
        partnerCreate(sessionId, partnerId, partnerServiceType, partnerServiceURL);
        userCreate("root", partnerId, rootUserPass, rootUserLogin);
    }
}
