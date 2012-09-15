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
package fr.unicaen.iota.epcisphi.xacml.pep;

import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.epcisphi.utils.MapSessions;
import fr.unicaen.iota.xacml.pep.MethodNamesAdmin;
import fr.unicaen.iota.xacml.pep.XACMLResponse;
import fr.unicaen.iota.xacml.request.EventRequest;
import java.util.Iterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EPCISPEP implements MethodNamesAdmin {

    private static final Log log = LogFactory.getLog(EPCISPEP.class);

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
    public int addEpcRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addEpcRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeEpcRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeEpcRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addEventTimeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addEventTimeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeEventTimeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeEventTimeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addRecordTimeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addRecordTimeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeRecordTimeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeRecordTimeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addOperationRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addOperationRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeOperationRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeOperationRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addEventTypeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addEventTypeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeEventTypeRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeEventTypeRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addParentIdRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addParentIdRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeParentIdRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeParentIdRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addChildEpcRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addChildEpcRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeChildEpcRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeChildEpcRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addQuantityRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addQuantityRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeQuantityRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeQuantityRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addReadPointRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addReadPointRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeReadPointRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeReadPointRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addBizLocRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addBizLocRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeBizLocRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeBizLocRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addBizTransRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addBizTransRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeBizTransRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeBizTransRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addDispositionRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addDispositionRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeDispositionRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeDispositionRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addMasterDataIdRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addMasterDataIdRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeMasterDataIdRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeMasterDataIdRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int addExtensionRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "addExtensionRestriction", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int removeExtensionRestriction(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "removeExtensionRestriction", partner, module);
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
    public int switchEpcPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchEpcPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchEventTimePolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchEventTimePolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchRecordTimePolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchRecordTimePolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchOperationPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchOperationPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchEventTypePolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchEventTypePolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchParentIdPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchParentIdPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchChildEpcPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchChildEpcPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchQuantityPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchQuantityPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchReadPointPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchReadPointPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchBizLocPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchBizLocPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchBizTransPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchBizTransPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchDispositionPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchDispositionPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchMasterDataIdPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchMasterDataIdPolicy", partner, module);
        RequestCtx request = eventRequest.createRequest();
        ResponseCtx result = MapSessions.APM.evaluate(request);
        return processResult(result);
    }

    @Override
    public int switchExtensionPolicy(String user, String partner, String module) {
        EventRequest eventRequest = new EventRequest(user, "switchExtensionPolicy", partner, module);
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