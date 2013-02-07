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
import fr.unicaen.iota.xacml.policy.Module;
import fr.unicaen.iota.xacml.request.EventRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class UserPEP extends PEP {

    private static final Log log = LogFactory.getLog(UserPEP.class);

    public UserPEP(String url, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        super(url, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
    }

    public int userLookup(String userId, String partner) {
        log.debug("process userLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userLookup", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userCreate(String userId, String partner) {
        log.debug("process userCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userCreate", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userInfo(String userId, String partner) {
        log.debug("process userInfo policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userInfo", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userUpdate(String userId, String partner) {
        log.debug("process userUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userUpdate", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int userDelete(String userId, String partner) {
        log.debug("process userDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "userDelete", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int partnerUpdate(String userId, String partner) {
        log.debug("process partnerUpdate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerUpdate", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int partnerLookup(String userId, String partner) {
        log.debug("process partnerLookup policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerLookup", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int partnerDelete(String userId, String partner) {
        log.debug("process partnerDelete policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerDelete", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public int partnerCreate(String userId, String partner) {
        log.debug("process partnerCreate policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "partnerCreate", partner, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    public boolean isRootAccess(String userId, String partnerId) {
        log.trace("process checkRootAccess policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "superadmin", partnerId, Module.administrationModule.getValue());
        return processXACMLRequest(eventRequest) == Result.DECISION_PERMIT;
    }

}
