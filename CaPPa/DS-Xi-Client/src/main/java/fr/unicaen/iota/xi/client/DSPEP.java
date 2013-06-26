/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xi.client;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.pep.MethodNamesCapture;
import fr.unicaen.iota.xacml.pep.MethodNamesQuery;
import fr.unicaen.iota.xacml.pep.XACMLDSEvent;
import fr.unicaen.iota.xacml.policy.Module;
import fr.unicaen.iota.xacml.request.EventRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is used to manage the DS access control policy
 */
public class DSPEP extends PEP implements MethodNamesQuery, MethodNamesCapture {

    private static final Log log = LogFactory.getLog(DSPEP.class);

    public DSPEP(String url, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        super(url, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
    }

    /**
     * process access control policy for the Hello method.
     *
     * @param userId connected user
     * @param ownerId corresponding ownerId
     * @param module Query, Capture or Admin
     * @return
     */
    @Override
    public int hello(String userId, String ownerId, String module) {
        log.trace("process hello policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", ownerId, module);
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventLookup method for each
     * retrieved event retriefed.
     *
     * @param userId connected user
     * @param dsEvent the event
     * @return
     */
    @Override
    public int eventLookup(String userId, XACMLDSEvent dsEvent) {
        log.trace("process eventLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", dsEvent, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    /**
     * process access control policy for the eventInfo method.
     *
     * @param userId connected user
     * @param dsEvent
     * @return
     */
    @Override
    public int eventInfo(String userId, XACMLDSEvent dsEvent) {
        log.trace("process eventInfo policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventInfo", dsEvent, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int canBe(String userId, String ownerId) {
        log.trace("process canBe policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "canBe", ownerId, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int eventCreate(String userId, XACMLDSEvent dsEvent) {
        log.trace("process eventCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventCreate", dsEvent, Module.captureModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int multipleEventCreate(String userId, XACMLDSEvent dsEvent) {
        log.trace("process multipleEventCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "eventLookup", dsEvent, Module.captureModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userLookup(String userId, String owner) {
        log.trace("process userLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userCreate(String userId, String owner) {
        log.trace("process userCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userInfo(String userId, String owner) {
        log.trace("process userInfo policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userUpdate(String userId, String owner) {
        log.trace("process userUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userDelete(String userId, String owner) {
        log.trace("process userDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int ownerUpdate(String userId, String owner) {
        log.trace("process ownerUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerUpdate", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int ownerLookup(String userId, String owner) {
        log.trace("process ownerLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerLookup", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int ownerDelete(String userId, String owner) {
        log.trace("process ownerDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerDelete", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int ownerCreate(String userId, String owner) {
        log.trace("process ownerCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "ownerCreate", owner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public boolean isRootAccess(String userId, String ownerId) {
        log.trace("process checkRootAccess policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "superadmin", ownerId, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest) == Result.DECISION_PERMIT;
    }
}
