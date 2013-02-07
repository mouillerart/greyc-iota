/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.epcisphi.xacml.servlet;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.epcisphi.utils.*;
import fr.unicaen.iota.epcisphi.xacml.ihm.Module;
import fr.unicaen.iota.eta.user.client.UserClient;
import fr.unicaen.iota.eta.user.userservice_wsdl.ImplementationExceptionResponse;
import fr.unicaen.iota.eta.user.userservice_wsdl.SecurityExceptionResponse;
import fr.unicaen.iota.xacml.pep.MethodNamesAdmin;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Services {

    private static final Log log = LogFactory.getLog(Services.class);

    private void checkAccess(User user, Module module, String method) throws ServiceException {
        if (PEPRequester.checkAccess(user, method) == Result.DECISION_DENY) {
            throw new ServiceException(method + ": not allowed for user " + user.getUserID() + " in module " + module, ServiceErrorType.xacml);
        }
    }

    public String createPartnerGroup(String sessionId, User user, Module module, String value) throws ServiceException {
        String method = module == Module.adminModule ? "createAdminPartnerGroup" : "createPartnerGroup";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryGroupPolicy(partner, new GroupPolicy(value, partner));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureGroupPolicy(partner, new GroupPolicy(value, partner));
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminGroupPolicy(partner, new GroupPolicy(value, partner));
                break;
        }
        if (!resp) {
            throw new ServiceException("createPartnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public void deletePartnerGroup(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "deleteAdminPartnerGroup" : "deletePartnerGroup";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.deleteQueryGroupPolicy(partner, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.deleteCaptureGroupPolicy(partner, objectId);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.deleteAdminGroupPolicy(partner, objectId);
                break;
        }
        if (!resp) {
            throw new ServiceException("deletePartnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addPartnerToGroup(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "addAdminPartnerToGroup" : "addPartnerToGroup";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryUserFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureUserFilter(partner, groupId, value);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminUserFilter(partner, groupId, value);
                break;
        }
        if (!resp) {
            throw new ServiceException("deletePartnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removePartnerFromGroup(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "removeAdminPartnerFromGroup" : "removePartnerFromGroup";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryUserFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureUserFilter(partner, groupId, objectId);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.removeAdminUserFilter(partner, groupId, objectId);
                break;
        }
        if (!resp) {
            throw new ServiceException("deletePartnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addBizStepRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addBizStepRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryBizStepFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureBizStepFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addBizStepRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeBizStepRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeBizStepRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryBizStepFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureBizStepFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeBizStepRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEpcRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addEpcRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryEpcFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureEpcFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEpcRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEpcRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryEpcFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureEpcFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEventTimeRestriction(String sessionId, User user, Module module, String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addEventTimeRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryEventTimeFilter(partner, groupId, convertStringToDate(valueMin, valueMax));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureEventTimeFilter(partner, groupId, convertStringToDate(valueMin, valueMax));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addEventTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEventTimeRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEventTimeRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryEventTimeFilter(partner, groupId, convertStringToDate(objectId));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureEventTimeFilter(partner, groupId, convertStringToDate(objectId));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeEventTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addRecordTimeRestriction(String sessionId, User user, Module module, String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addRecordTimeRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryRecordTimeFilter(partner, groupId, convertStringToDate(valueMin, valueMax));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureRecordTimeFilter(partner, groupId, convertStringToDate(valueMin, valueMax));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addRecordTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeRecordTimeRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeRecordTimeRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryRecordTimeFilter(partner, groupId, convertStringToDate(objectId));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureRecordTimeFilter(partner, groupId, convertStringToDate(objectId));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeRecordTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addOperationRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addOperationRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryOperationFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureOperationFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addOperationRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeOperationRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeOperationRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryOperationFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureOperationFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeOperationRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEventTypeRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addEventTypeRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryEventTypeFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureEventTypeFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addEventTypeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEventTypeRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEventTypeRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryEventTypeFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureEventTypeFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeEventTypeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addParentIdRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addParentIdRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryParentIdFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureParentIdFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addParentIdRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeParentIdRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeParentIdRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryParentIdFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureParentIdFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeParentIdRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addChildEpcRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addChildEpcRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryChildEpcFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureChildEpcFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addChildEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeChildEpcRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeChildEpcRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryChildEpcFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureChildEpcFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeChildEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addQuantityRestriction(String sessionId, User user, Module module, String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addQuantityRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        List quantities = new ArrayList();
        quantities.add(Long.valueOf(valueMin));
        quantities.add(Long.valueOf(valueMax));
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryQuantityFilter(partner, groupId, quantities);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureQuantityFilter(partner, groupId, quantities);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addQuantityRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeQuantityRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeQuantityRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryQuantityFilter(partner, groupId, convertStringToQuantity(objectId));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureQuantityFilter(partner, groupId, convertStringToQuantity(objectId));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeQuantityRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addReadPointRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addReadPointRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryReadPointFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureReadPointFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addReadPointRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeReadPointRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeReadPointRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryReadPointFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureReadPointFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeReadPointRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addBizLocRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addBizLocRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryBizLocFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureBizLocFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addBizLocRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeBizLocRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeBizLocRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryBizLocFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureBizLocFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeBizLocRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addDispositionRestriction(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addDispositionRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryDispositionFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureDispositionFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addDispositionRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeDispositionRestriction(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeDispositionRestriction");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryDispositionFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureDispositionFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeDispositionRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public String switchBizStepPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchBizStepPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionBizSteps(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getBizStepsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionBizSteps(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getBizStepsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchBizStepPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEpcPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEpcPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEpcs(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getEpcsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEpcs(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getEpcsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEpcPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEventTimePolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEventTimePolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEventTimes(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getEventTimesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEventTimes(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getEventTimesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEventTimePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchRecordTimePolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchRecordTimePolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionRecordTimes(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getRecordTimesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionRecordTimes(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getRecordTimesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchRecordTimePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchOperationPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchOperationPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionOperations(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getOperationsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionOperations(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getOperationsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchOperationPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEventTypePolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEventTypePolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEventTypes(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getEventTypesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEventTypes(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getEventTypesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEventTypePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchParentIdPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchParentIdPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionParentIds(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getParentIdsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionParentIds(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getParentIdsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchParentIdPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchChildEpcPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchChildEpcPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionChildEpcs(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getChildEpcsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionChildEpcs(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getChildEpcsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchChildEpcPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchQuantityPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchQuantityPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionQuantities(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getQuantitiesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionQuantities(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getQuantitiesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchQuantityPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchReadPointPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchReadPointPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionReadPoints(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getReadPointsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionReadPoints(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getReadPointsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchReadPointPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchBizLocPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchBizLocPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionBizLocs(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getBizLocsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionBizLocs(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getBizLocsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchBizLocPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchDispositionPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchDispositionPolicy");
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionDispositions(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getDispositionsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionDispositions(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getDispositionsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchDispositionPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchUserPermissionPolicy(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "switchAdminUserPermissionPolicy" : "switchUserPermissionPolicy";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionUsers(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getUsersFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionUsers(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getUsersFilterFunction().getValue();
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.switchAdminPermissionUsers(partner, groupId);
                value = (interfaceHelper.APMSession.getAdminPolicy(partner)).getGroupPolicy(groupId).getUsersFilterFunction().getValue();
                break;
        }
        if (!resp) {
            throw new ServiceException("switchUserPermissionPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public void removeUserPermission(String sessionId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "removeAdminUserPermission" : "removeUserPermission";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryActionFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureActionFilter(partner, groupId, objectId);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.removeAdminActionFilter(partner, groupId, objectId);
                break;
        }
        if (!resp) {
            throw new ServiceException("removeUserPermission: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addUserPermission(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "addAdminUserPermission" : "addUserPermission";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryActionFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureActionFilter(partner, groupId, value);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminActionFilter(partner, groupId, value);
                break;
        }
        if (!resp) {
            throw new ServiceException("addUserPermission: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void updateGroupName(String sessionId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "updateAdminGroupName" : "updateGroupName";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.updateQueryGroupName(partner, objectId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.updateCaptureGroupName(partner, objectId, value);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.updateAdminGroupName(partner, objectId, value);
                break;
        }
        if (!resp) {
            throw new ServiceException("updateGroupName: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void savePolicyPartner(String sessionId, User user, Module module) throws ServiceException {
        String method = module == Module.adminModule ? "saveAdminPolicyPartner" : "savePolicyPartner";
        checkAccess(user, module, method);
        String partner = user.getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.saveQueryPolicies(partner);
                interfaceHelper.updateQueryAPM();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.saveCapturePolicies(partner);
                interfaceHelper.updateCaptureAPM();
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.saveAdminPolicies(partner);
                interfaceHelper.updateAdminAPM();
                break;
        }
        if (!resp) {
            throw new ServiceException("savePolicyPartner: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    /*
     * TODO: methods implementing public void updatePartner(String sessionId,
     * User user, int partnerUID, String partnerID, String serviceID, String
     * serviceAddress, String serviceType, Session session) throws
     * ServiceException { if (PEPRequester.checkAccess(user, "partnerUpdate") ==
     * Result.DECISION_DENY) { throw new ServiceException("partnerUpdate: not
     * allowed for user " + user.getUserID() + " in module: " +
     * Module.adminModule, ServiceErrorType.xacml); } DsClient gatewayClient =
     * new DsClient(CONFIGURATION.DS_ADDRESS); Service service; try { service =
     * new Service(serviceID, serviceType, new URI(serviceAddress)); } catch
     * (MalformedURIException ex) { throw new ServiceException("service URL
     * malformed !", ServiceErrorType.Unknown); } List<Service> lService = new
     * ArrayList<Service>(); lService.add(service); try {
     * gatewayClient.partnerUpdate(sessionId, partnerUID, partnerID, lService);
     * } catch (RemoteException ex) { throw new ServiceException("DS
     * Communication Failure: internal protocol error !",
     * ServiceErrorType.Unknown); } catch (EnancedProtocolException ex) { throw
     * new ServiceException(ex.getMessage(), ServiceErrorType.Unknown); } }
     */
    public void createUser(String sessionId, User user, String login, String pass) throws ServiceException {
        checkAccess(user, Module.adminModule, "userCreate");
        try {
            String partner = user.getPartnerID();
            String hashPass = SHA1.makeSHA1Hash(pass);
            UserClient client = new UserClient(Constants.USERSERVICE_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
            client.userCreate(sessionId, login, hashPass, partner, 30);
        } catch (NoSuchAlgorithmException ex) {
            log.error("Algorithm error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.epcis);
        } catch (ImplementationExceptionResponse ex) {
            log.error("Internal error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        } catch (SecurityExceptionResponse ex) {
            log.error("Security error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        }
    }

    public void deleteUser(String sessionId, User user, String login) throws ServiceException {
        checkAccess(user, Module.adminModule, "userDelete");
        try {
            UserClient client = new UserClient(Constants.USERSERVICE_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
            client.userDelete(sessionId, login);
        } catch (ImplementationExceptionResponse ex) {
            log.error("Internal error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        } catch (SecurityExceptionResponse ex) {
            log.error("Security error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        }
    }

    /*
     * public void updateUser(String sessionId, User user, String login, String
     * pass) throws ServiceException { if (PEPRequester.checkAccess(user,
     * "userUpdate") == Result.DECISION_DENY) { throw new
     * ServiceException("createUser: not allowed for user " + user.getUserID()
     * + " in module: " + Module.adminModule, ServiceErrorType.xacml); }
     * DsClient gatewayClient = new DsClient(CONFIGURATION.DS_ADDRESS); try {
     * gatewayClient.userUpdate(sessionId, user.getId(), partner, login, pass,
     * 30); } catch (RemoteException ex) { throw new ServiceException("DS
     * Communication Failure: internal protocol error !",
     * ServiceErrorType.Unknown); } catch (EnancedProtocolException ex) { throw
     * new ServiceException(ex.getMessage(), ServiceErrorType.Unknown); } }
     */
    public void createRootPartnerPolicy(String sessionId, String userId, String partnerId) {
        String gpName = "admin";
        InterfaceHelper ih = new InterfaceHelper(partnerId);
        OwnerPolicies ownerPolicies = new OwnerPolicies(partnerId, fr.unicaen.iota.xacml.policy.Module.administrationModule);
        ih.APMSession.addAdminPolicy(ownerPolicies);
        GroupPolicy gp = new GroupPolicy(gpName, partnerId);
        ih.APMSession.addAdminGroupPolicy(partnerId, gp);
        ih.APMSession.addAdminUserFilter(partnerId, gpName, userId);
        for (Method m : MethodNamesAdmin.class.getMethods()) {
            String n = m.getName();
            ih.APMSession.addAdminActionFilter(partnerId, gpName, n);
        }
        ih.APMSession.saveAdminPolicies(partnerId);
        ih.updateAPM();
        log.debug(MapSessions.AdminAPMtoString());
    }

    public boolean createAccount(String sessionId, User user, String partnerId, String login, String pass) throws ServiceException {
        checkAccess(user, Module.adminModule, "superadmin");
        try {
            UserClient client = new UserClient(Constants.USERSERVICE_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
            boolean found = false;
            try {
                client.userInfo(sessionId, login);
                found = true;
            } catch (ImplementationExceptionResponse ex) {
                log.warn(null, ex);
            } catch (SecurityExceptionResponse ex) {
                log.warn(null, ex);
            }
            if (found) {
                throw new ServiceException("User exists", ServiceErrorType.Unknown);
            }
            String hashPass = SHA1.makeSHA1Hash(pass);
            client.userCreate(sessionId, login, hashPass, partnerId, 30);
            createRootPartnerPolicy(sessionId, login, partnerId);
        } catch (NoSuchAlgorithmException ex) {
            log.error("Algorithm error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        } catch (ImplementationExceptionResponse ex) {
            log.error("Internal error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        } catch (SecurityExceptionResponse ex) {
            log.error("Security error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        }
        return true;
    }

    public void loadPolicyTree(User user, Module module) {
    }

    public void cancelPartnerPolicy(User user, Module module) {
    }

    private List convertStringToDate(String dateInString) {
        List dates = new ArrayList();
        String[] datesStringTab = dateInString.split(" -> ");

        for (int i = 0; i < datesStringTab.length; i++) {
            String[] dateValue = datesStringTab[i].split("/");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(dateValue[2]), Integer.parseInt(dateValue[0]) - 1, Integer.parseInt(dateValue[1]), 0, 0, 0);

            long time = cal.getTimeInMillis() - cal.get(Calendar.MILLISECOND);
            Date date = new Date(time);
            dates.add(date);
        }
        return dates;
    }

    private List convertStringToDate(String dateMinInString, String dateMaxInString) {
        List dates = new ArrayList();
        String[] datesStringTab = {dateMinInString, dateMaxInString};

        for (int i = 0; i < datesStringTab.length; i++) {
            String[] dateValue = datesStringTab[i].split("/");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(dateValue[2]), Integer.parseInt(dateValue[0]) - 1, Integer.parseInt(dateValue[1]), 0, 0, 0);

            long time = cal.getTimeInMillis() - cal.get(Calendar.MILLISECOND);
            Date date = new Date(time);
            dates.add(date);
        }
        return dates;
    }

    private List convertStringToQuantity(String quantityInString) {
        List quantities = new ArrayList();
        String[] quantitiesStringTab = quantityInString.split(" -> ");

        try {
            if (quantitiesStringTab.length != 2) {
                throw new NumberFormatException("Quantity format conversion error");
            }
            quantities.add(Long.valueOf(quantitiesStringTab[0]));
            quantities.add(Long.valueOf(quantitiesStringTab[1]));
        } catch (NumberFormatException e) {
            log.error("Number format error", e);
        }
        return quantities;
    }
}
