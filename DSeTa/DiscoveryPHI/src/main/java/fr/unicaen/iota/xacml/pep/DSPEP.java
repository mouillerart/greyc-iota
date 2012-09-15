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
    public int hello(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "hello", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userLookup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "userLookup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userCreate(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "userCreate", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userInfo(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "userInfo", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userUpdate(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "userUpdate", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int userDelete(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "userDelete", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int partnerInfo(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "partnerInfo", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int partnerUpdate(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "partnerUpdate", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int partnerDelete(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "partnerDelete", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    //####################################################
    //############## Admin Module Section ################
    //####################################################
    public int superadmin(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "superadmin", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    public int allAdminMethods(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "allAdminMethods", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    public int allQueryMethods(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "allQueryMethods", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    public int allCaptureMethods(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "allCaptureMethods", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int createAdminPartnerGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "createAdminPartnerGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int deleteAdminPartnerGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "deleteAdminPartnerGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addAdminPartnerToGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addAdminPartnerToGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeAdminPartnerFromGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeAdminPartnerFromGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchAdminUserPermissionPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchAdminUserPermissionPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeAdminUserPermission(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeAdminUserPermission", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addAdminUserPermission(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addAdminUserPermission", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int updateAdminGroupName(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "updateAdminGroupName", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int saveAdminPolicyPartner(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "saveAdminPolicyPartner", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    //####################################################
    //################# Modules Section ##################
    //####################################################
    @Override
    public int createPartnerGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "createPartnerGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int deletePartnerGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "deletePartnerGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addPartnerToGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addPartnerToGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removePartnerFromGroup(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removePartnerFromGroup", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addBizStepRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addBizStepRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeBizStepRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeBizStepRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addEPCRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addEPCRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeEPCRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeEPCRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addEPCClassRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addEPCClassRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeEPCClassRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeEPCClassRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addTimeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addTimeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeTimeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeTimeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchBizStepPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchBizStepPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchEPCPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchEPCPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchEPCClassPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchEPCClassPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchTimePolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchTimePolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchUserPermissionPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchUserPermissionPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeUserPermission(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeUserPermission", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addUserPermission(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addUserPermission", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int updateGroupName(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "updateGroupName", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int savePolicyPartner(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "savePolicyPartner", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    private int processResult(ResponseCtx result) {
        Iterator it = result.getResults().iterator();
        while (it.hasNext()) {
            Result res = (Result) it.next();
            if (res != null) {
                return new XACMLResponse(res).getResponse();
            }
        }
        return Result.DECISION_DENY;
    }
}
