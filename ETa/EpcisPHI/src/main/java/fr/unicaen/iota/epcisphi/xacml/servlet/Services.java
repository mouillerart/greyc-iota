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
import fr.unicaen.iota.xacml.pep.MethodNamesAdmin;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import fr.unicaen.iota.ypsilon.client.YPSilonClient;
import fr.unicaen.iota.ypsilon.client.model.UserInfoOut;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    public String createOwnerGroup(String userId, User user, Module module, String value) throws ServiceException {
        String method = module == Module.adminModule ? "createAdminOwnerGroup" : "createOwnerGroup";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryGroupPolicy(owner, new GroupPolicy(value, owner));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureGroupPolicy(owner, new GroupPolicy(value, owner));
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminGroupPolicy(owner, new GroupPolicy(value, owner));
                break;
        }
        if (!resp) {
            throw new ServiceException("createOwnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public void deleteOwnerGroup(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "deleteAdminOwnerGroup" : "deleteOwnerGroup";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.deleteQueryGroupPolicy(owner, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.deleteCaptureGroupPolicy(owner, objectId);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.deleteAdminGroupPolicy(owner, objectId);
                break;
        }
        if (!resp) {
            throw new ServiceException("deleteOwnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addOwnerToGroup(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "addAdminOwnerToGroup" : "addOwnerToGroup";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryUserFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureUserFilter(owner, groupId, value);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminUserFilter(owner, groupId, value);
                break;
        }
        if (!resp) {
            throw new ServiceException("deleteOwnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeOwnerFromGroup(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "removeAdminOwnerFromGroup" : "removeOwnerFromGroup";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryUserFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureUserFilter(owner, groupId, objectId);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.removeAdminUserFilter(owner, groupId, objectId);
                break;
        }
        if (!resp) {
            throw new ServiceException("deleteOwnerGroup: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addBizStepRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addBizStepRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryBizStepFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureBizStepFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addBizStepRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeBizStepRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeBizStepRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryBizStepFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureBizStepFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeBizStepRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEpcRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addEpcRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryEpcFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureEpcFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEpcRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEpcRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryEpcFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureEpcFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEventTimeRestriction(String userId, User user, Module module, String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addEventTimeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        try {
            switch (module) {
                case queryModule:
                    resp = interfaceHelper.APMSession.addQueryEventTimeFilter(owner, groupId, convertStringToDate(valueMin, valueMax));
                    break;
                case captureModule:
                    resp = interfaceHelper.APMSession.addCaptureEventTimeFilter(owner, groupId, convertStringToDate(valueMin, valueMax));
                    break;
                case adminModule:
                    break;
            }
        } catch (ParseException ex) {
            throw new ServiceException("addRecordTimeRestriction: parsing error: " + ex.getMessage(), ServiceErrorType.xacml);
        }
        if (!resp) {
            throw new ServiceException("addEventTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEventTimeRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEventTimeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        try {
            switch (module) {
                case queryModule:
                    resp = interfaceHelper.APMSession.removeQueryEventTimeFilter(owner, groupId, convertStringToDate(objectId));
                    break;
                case captureModule:
                    resp = interfaceHelper.APMSession.removeCaptureEventTimeFilter(owner, groupId, convertStringToDate(objectId));
                    break;
                case adminModule:
                    break;
            }
        } catch (ParseException ex) {
            throw new ServiceException("addRecordTimeRestriction: parsing error: " + ex.getMessage(), ServiceErrorType.xacml);
        }
        if (!resp) {
            throw new ServiceException("removeEventTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addRecordTimeRestriction(String userId, User user, Module module, String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addRecordTimeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        try {
            switch (module) {
                case queryModule:
                    resp = interfaceHelper.APMSession.addQueryRecordTimeFilter(owner, groupId, convertStringToDate(valueMin, valueMax));
                    break;
                case captureModule:
                    resp = interfaceHelper.APMSession.addCaptureRecordTimeFilter(owner, groupId, convertStringToDate(valueMin, valueMax));
                    break;
                case adminModule:
                    break;
            }
        } catch (ParseException ex) {
            throw new ServiceException("addRecordTimeRestriction: parsing error: " + ex.getMessage(), ServiceErrorType.xacml);
        }
        if (!resp) {
            throw new ServiceException("addRecordTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeRecordTimeRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeRecordTimeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        try {
            switch (module) {
                case queryModule:
                    resp = interfaceHelper.APMSession.removeQueryRecordTimeFilter(owner, groupId, convertStringToDate(objectId));
                    break;
                case captureModule:
                    resp = interfaceHelper.APMSession.removeCaptureRecordTimeFilter(owner, groupId, convertStringToDate(objectId));
                    break;
                case adminModule:
                    break;
            }
        } catch (ParseException ex) {
            throw new ServiceException("addRecordTimeRestriction: parsing error: " + ex.getMessage(), ServiceErrorType.xacml);
        }
        if (!resp) {
            throw new ServiceException("removeRecordTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addOperationRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addOperationRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryOperationFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureOperationFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addOperationRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeOperationRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeOperationRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryOperationFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureOperationFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeOperationRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEventTypeRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addEventTypeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryEventTypeFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureEventTypeFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addEventTypeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEventTypeRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEventTypeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryEventTypeFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureEventTypeFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeEventTypeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addParentIdRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addParentIdRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryParentIdFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureParentIdFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addParentIdRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeParentIdRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeParentIdRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryParentIdFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureParentIdFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeParentIdRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addChildEpcRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addChildEpcRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryChildEpcFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureChildEpcFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addChildEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeChildEpcRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeChildEpcRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryChildEpcFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureChildEpcFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeChildEpcRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addQuantityRestriction(String userId, User user, Module module, String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addQuantityRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        List quantities = new ArrayList();
        quantities.add(Long.valueOf(valueMin));
        quantities.add(Long.valueOf(valueMax));
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryQuantityFilter(owner, groupId, quantities);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureQuantityFilter(owner, groupId, quantities);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addQuantityRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeQuantityRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeQuantityRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryQuantityFilter(owner, groupId, convertStringToQuantity(objectId));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureQuantityFilter(owner, groupId, convertStringToQuantity(objectId));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeQuantityRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addReadPointRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addReadPointRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryReadPointFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureReadPointFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addReadPointRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeReadPointRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeReadPointRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryReadPointFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureReadPointFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeReadPointRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addBizLocRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addBizLocRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryBizLocFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureBizLocFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addBizLocRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeBizLocRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeBizLocRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryBizLocFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureBizLocFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeBizLocRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addDispositionRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addDispositionRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryDispositionFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureDispositionFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addDispositionRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeDispositionRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeDispositionRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryDispositionFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureDispositionFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeDispositionRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addMasterDataIdRestriction(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addMasterDataIdRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryMasterDataIdFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureMasterDataIdFilter(owner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addMasterDataIdRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeMasterDataIdRestriction(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeMasterDataIdRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryMasterDataIdFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureMasterDataIdFilter(owner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeMasterDataIdRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public String switchBizStepPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchBizStepPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionBizSteps(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getBizStepsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionBizSteps(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getBizStepsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchBizStepPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEpcPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEpcPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEpcs(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getEpcsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEpcs(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getEpcsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEpcPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEventTimePolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEventTimePolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEventTimes(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getEventTimesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEventTimes(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getEventTimesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEventTimePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchRecordTimePolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchRecordTimePolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionRecordTimes(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getRecordTimesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionRecordTimes(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getRecordTimesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchRecordTimePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchOperationPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchOperationPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionOperations(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getOperationsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionOperations(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getOperationsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchOperationPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEventTypePolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEventTypePolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEventTypes(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getEventTypesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEventTypes(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getEventTypesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEventTypePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchParentIdPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchParentIdPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionParentIds(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getParentIdsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionParentIds(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getParentIdsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchParentIdPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchChildEpcPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchChildEpcPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionChildEpcs(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getChildEpcsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionChildEpcs(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getChildEpcsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchChildEpcPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchQuantityPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchQuantityPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionQuantities(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getQuantitiesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionQuantities(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getQuantitiesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchQuantityPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchReadPointPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchReadPointPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionReadPoints(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getReadPointsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionReadPoints(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getReadPointsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchReadPointPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchBizLocPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchBizLocPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionBizLocs(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getBizLocsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionBizLocs(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getBizLocsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchBizLocPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchDispositionPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchDispositionPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionDispositions(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getDispositionsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionDispositions(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getDispositionsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchDispositionPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchMasterDataIdPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchMasterDataIdPolicy");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionMasterDataIds(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getMasterDataIdsFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionMasterDataIds(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getMasterDataIdsFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchMasterDataIdPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchUserPermissionPolicy(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "switchAdminUserPermissionPolicy" : "switchUserPermissionPolicy";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionUsers(owner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(owner)).getGroupPolicy(groupId).getUsersFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionUsers(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getUsersFilterFunction().getValue();
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.switchAdminPermissionUsers(owner, groupId);
                value = (interfaceHelper.APMSession.getAdminPolicy(owner)).getGroupPolicy(groupId).getUsersFilterFunction().getValue();
                break;
        }
        if (!resp) {
            throw new ServiceException("switchUserPermissionPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public void removeUserPermission(String userId, User user, Module module, String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "removeAdminUserPermission" : "removeUserPermission";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryActionFilter(owner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureActionFilter(owner, groupId, objectId);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.removeAdminActionFilter(owner, groupId, objectId);
                break;
        }
        if (!resp) {
            throw new ServiceException("removeUserPermission: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addUserPermission(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "addAdminUserPermission" : "addUserPermission";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryActionFilter(owner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureActionFilter(owner, groupId, value);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminActionFilter(owner, groupId, value);
                break;
        }
        if (!resp) {
            throw new ServiceException("addUserPermission: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void updateGroupName(String userId, User user, Module module, String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "updateAdminGroupName" : "updateGroupName";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.updateQueryGroupName(owner, objectId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.updateCaptureGroupName(owner, objectId, value);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.updateAdminGroupName(owner, objectId, value);
                break;
        }
        if (!resp) {
            throw new ServiceException("updateGroupName: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void savePolicyOwner(String userId, User user, Module module) throws ServiceException {
        String method = module == Module.adminModule ? "saveAdminPolicyOwner" : "savePolicyOwner";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.saveQueryPolicies(owner);
                interfaceHelper.updateQueryAPM();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.saveCapturePolicies(owner);
                interfaceHelper.updateCaptureAPM();
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.saveAdminPolicies(owner);
                interfaceHelper.updateAdminAPM();
                break;
        }
        if (!resp) {
            throw new ServiceException("savePolicyOwner: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void createUser(User user, String userDN, String userName) throws ServiceException {
        checkAccess(user, Module.adminModule, "userCreate");
        try {
            String owner = user.getOwnerID();
            YPSilonClient client = new YPSilonClient(Constants.YPSILON_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
            if (userName != null && !userName.isEmpty()) {
                client.userCreate(userDN, owner, userName);
            }
            else {
                client.userCreate(userDN, owner);
            }
        } catch (ImplementationExceptionResponse ex) {
            log.error("Internal error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        }
    }

    public void deleteUser(User user, String userId) throws ServiceException {
        checkAccess(user, Module.adminModule, "userDelete");
        try {
            YPSilonClient client = new YPSilonClient(Constants.YPSILON_ADDRESS, Constants.PKS_FILENAME,
                    Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
            UserInfoOut userInfo = client.userInfo(userId);
            if (userInfo.getUser() != null && userInfo.getUser().getOwner() != null
                    && !userInfo.getUser().getOwner().isEmpty() && userInfo.getUser().getOwner().equals(user.getUserID())) {
                client.userDelete(userId);
            }
            else {
                throw new ServiceException("You can't delete this user.", ServiceErrorType.xacml);
            }
        } catch (ImplementationExceptionResponse ex) {
            log.error("Internal error", ex);
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
     * gatewayClient.userUpdate(sessionId, user.getId(), owner, login, pass,
     * 30); } catch (RemoteException ex) { throw new ServiceException("DS
     * Communication Failure: internal protocol error !",
     * ServiceErrorType.Unknown); } catch (EnancedProtocolException ex) { throw
     * new ServiceException(ex.getMessage(), ServiceErrorType.Unknown); } }
     */
    public void createRootOwnerPolicy(String userId, String ownerId) {
        String gpName = "admin";
        InterfaceHelper ih = new InterfaceHelper(ownerId);
        OwnerPolicies ownerPolicies = new OwnerPolicies(ownerId, fr.unicaen.iota.xacml.policy.Module.administrationModule);
        ih.APMSession.addAdminPolicy(ownerPolicies);
        GroupPolicy gp = new GroupPolicy(gpName, ownerId);
        ih.APMSession.addAdminGroupPolicy(ownerId, gp);
        ih.APMSession.addAdminUserFilter(ownerId, gpName, userId);
        for (Method m : MethodNamesAdmin.class.getMethods()) {
            String n = m.getName();
            ih.APMSession.addAdminActionFilter(ownerId, gpName, n);
        }
        ih.APMSession.saveAdminPolicies(ownerId);
        ih.updateAPM();
        log.debug(MapSessions.AdminAPMtoString());
    }

    public boolean createAccount(User user, String ownerId, String userDN, String userName) throws ServiceException {
        checkAccess(user, Module.adminModule, "superadmin");
        try {
            YPSilonClient client = new YPSilonClient(Constants.YPSILON_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
            if (userName != null && !userName.isEmpty()) {
                if (client.userInfo(userDN).getUser() == null) {
                    client.userCreate(userDN, ownerId, userName);
                }
                createRootOwnerPolicy(userName, ownerId);
            }
            else {
                if (client.userInfo(userDN).getUser() == null) {
                    client.userCreate(userDN, ownerId);
                }
                createRootOwnerPolicy(userDN, ownerId);
            }
        } catch (ImplementationExceptionResponse ex) {
            log.error("Internal error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.Unknown);
        }
        return true;
    }

    public void loadPolicyTree(User user, Module module) {
    }

    public void cancelOwnerPolicy(User user, Module module) {
    }

    private List<Date> convertStringToDate(String dateInString) throws ParseException {
        List dates = new ArrayList();
        String[] datesStringTab = dateInString.split(" -> ");

        for (int i = 0; i < datesStringTab.length; i++) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            dates.add(formatter.parse(datesStringTab[i]));
        }
        return dates;
    }

    private List<Date> convertStringToDate(String dateMinInString, String dateMaxInString) throws ParseException {
        List dates = new ArrayList();
        String[] datesStringTab = {dateMinInString, dateMaxInString};

        for (int i = 0; i < datesStringTab.length; i++) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            dates.add(formatter.parse(datesStringTab[i]));
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
