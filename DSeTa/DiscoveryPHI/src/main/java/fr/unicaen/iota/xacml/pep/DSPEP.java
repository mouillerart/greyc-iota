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
package fr.unicaen.iota.xacml.pep;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.utils.MapSessions;
import fr.unicaen.iota.xacml.policy.Module;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DSPEP implements MethodNamesAdmin {

    private static final Log log = LogFactory.getLog(DSPEP.class);

    //####################################################
    //################## Authentication ##################
    //####################################################
    @Override
    public int hello(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "hello", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userLookup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "userLookup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userCreate(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "userCreate", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userInfo(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "userInfo", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userUpdate(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "userUpdate", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userDelete(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "userDelete", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int ownerUpdate(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "ownerUpdate", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int ownerDelete(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "ownerDelete", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    //####################################################
    //############## Admin Module Section ################
    //####################################################
    public int superadmin(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "superadmin", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    public int allAdminMethods(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "allAdminMethods", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    public int allQueryMethods(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "allQueryMethods", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    public int allCaptureMethods(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "allCaptureMethods", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int createAdminOwnerGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "createAdminOwnerGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int deleteAdminOwnerGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "deleteAdminOwnerGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addAdminOwnerToGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addAdminOwnerToGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeAdminOwnerFromGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeAdminOwnerFromGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchAdminUserPermissionPolicy(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "switchAdminUserPermissionPolicy", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeAdminUserPermission(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeAdminUserPermission", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addAdminUserPermission(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addAdminUserPermission", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int updateAdminGroupName(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "updateAdminGroupName", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int saveAdminPolicyOwner(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "saveAdminPolicyOwner", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    //####################################################
    //################# Modules Section ##################
    //####################################################
    @Override
    public int createOwnerGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "createOwnerGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int deleteOwnerGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "deleteOwnerGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addOwnerToGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addOwnerToGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeOwnerFromGroup(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeOwnerFromGroup", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addBizStepRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addBizStepRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeBizStepRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeBizStepRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addEPCRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addEPCRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeEPCRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeEPCRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addEventTypeRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addEventTypeRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeEventTypeRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeEventTypeRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addTimeRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addTimeRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeTimeRestriction(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeTimeRestriction", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchBizStepPolicy(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "switchBizStepPolicy", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchEPCPolicy(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "switchEPCPolicy", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchEventTypePolicy(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "switchEventTypePolicy", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchTimePolicy(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "switchTimePolicy", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchUserPermissionPolicy(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "switchUserPermissionPolicy", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeUserPermission(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "removeUserPermission", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addUserPermission(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "addUserPermission", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int updateGroupName(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "updateGroupName", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int savePolicyOwner(String user, String owner) {
        EventRequest eventRequest = new EventRequest(user, "savePolicyOwner", owner, Module.administrationModule.getValue());
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    private int processResult(ResponseCtx result) {
        Iterator it = result.getResults().iterator();
        while (it.hasNext()) {
            Result res = (Result) it.next();
            if (res != null) {
                return res.getDecision();
            }
        }
        return Result.DECISION_DENY;
    }
}
