/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dphi.xacml.servlet;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.dphi.auth.User;
import fr.unicaen.iota.dphi.utils.Constants;
import fr.unicaen.iota.dphi.utils.InterfaceHelper;
import fr.unicaen.iota.dphi.utils.MapSessions;
import fr.unicaen.iota.dphi.utils.PEPRequester;
import fr.unicaen.iota.dphi.xacml.ihm.Module;
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

/**
 *
 */
public class Services {

    private static final Log log = LogFactory.getLog(Services.class.getName());

    private void checkAccess(User user, Module module, String method) throws ServiceException {
        if (PEPRequester.checkAccess(user, method) == Result.DECISION_DENY) {
            throw new ServiceException(method + ": not allowed for user" + user.getUserID() + " in module " + module, ServiceErrorType.xacml);
        }
    }

    public String createOwnerGroup(String userId, User user, Module module, String value)
            throws ServiceException {
        String method = module == Module.adminModule ? "createAdminOwnerGroup" : "createOwnerGroup";
        checkAccess(user, module, method);
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        GroupPolicy gpol = new GroupPolicy(value, owner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryGroupPolicy(owner, gpol);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureGroupPolicy(owner, gpol);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminGroupPolicy(owner, gpol);
                break;
        }
        if (!resp) {
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public void deleteOwnerGroup(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void addOwnerToGroup(String userId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void removeOwnerFromGroup(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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
            throw new ServiceException(method + ": internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addBizStepRestriction(String userId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
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

    public void removeBizStepRestriction(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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

    public void addEPCRestriction(String userId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addRestriction");
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
            throw new ServiceException("addEPCRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEPCRestriction(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEPCRestriction");
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
            throw new ServiceException("removeEPCRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEventTypeRestriction(String userId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
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

    public void removeEventTypeRestriction(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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

    public void addTimeRestriction(String userId, User user, Module module,
            String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addTimeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        try {
            switch (module) {
                case queryModule:
                    resp = interfaceHelper.APMSession.addQueryEventTimeFilter(owner,
                            groupId, convertStringToDate(valueMin, valueMax));
                    break;
                case captureModule:
                    resp = interfaceHelper.APMSession.addCaptureEventTimeFilter(owner,
                            groupId, convertStringToDate(valueMin, valueMax));
                    break;
                case adminModule:
                    break;
            }
        } catch (ParseException ex) {
            throw new ServiceException("addTimeRestriction: parsing error: " + ex.getMessage(), ServiceErrorType.xacml);
        }
        if (!resp) {
            throw new ServiceException("addTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeTimeRestriction(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeTimeRestriction");
        String owner = user.getOwnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(userId, owner);
        boolean resp = false;
        try {
            switch (module) {
                case queryModule:
                    resp = interfaceHelper.APMSession.removeQueryEventTimeFilter(owner, groupId, convertStringToDate((String) objectId));
                    break;
                case captureModule:
                    resp = interfaceHelper.APMSession.removeCaptureEventTimeFilter(owner, groupId, convertStringToDate((String) objectId));
                    break;
                case adminModule:
                    break;
            }
        } catch (ParseException ex) {
            throw new ServiceException("addTimeRestriction: parsing error: " + ex.getMessage(), ServiceErrorType.xacml);
        }
        if (!resp) {
            throw new ServiceException("removeTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public String switchBizStepPolicy(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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

    public String switchEPCPolicy(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEPCPolicy");
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
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEpcs(owner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(owner)).getGroupPolicy(groupId).getEpcsFilterFunction().getValue();
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEPCPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEventTypePolicy(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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

    public String switchTimePolicy(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchTimePolicy");
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
            throw new ServiceException("switchTimePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchUserPermissionPolicy(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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

    public void removeUserPermission(String userId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void addUserPermission(String userId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void updateGroupName(String userId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
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
            default:
                break;
        }
        if (!resp) {
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
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
            default:
                break;
        }
        if (!resp) {
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
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
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
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
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        }
    }

    /*public void updateUser(String userId, User user, String login, String pass) throws ServiceException {
        checkAccess(user, Module.adminModule, "userUpdate");
        YPSilonClient client = new YPSilonClient(Constants.YPSILON_ADDRESS, Constants.PKS_FILENAME,
                            Constants.PKS_PASSWORD, Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
        String owner = user.getOwnerID();
        try {
            client.userUpdate(userId, user.getId(), owner, login, pass, 30);
        } catch (ImplementationExceptionResponse ex) {
            log.error("Internal error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        } catch (SecurityExceptionResponse ex) {
            log.error("Security error", ex);
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        }
    }*/

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
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        }
        return true;
    }

    public void loadPolicyTree(User user, Module module) {
    }

    public void cancelOwnerPolicy(User user, Module module) {
    }

    private List<Date> convertStringToDate(String dateInString) throws ParseException {
        List<Date> dates = new ArrayList<Date>();
        String[] datesStringTab = dateInString.split(" -> ");
        for (int i = 0; i < datesStringTab.length; i++) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            dates.add(formatter.parse(datesStringTab[i]));
        }
        return dates;
    }

    private List<Date> convertStringToDate(String dateMinInString, String dateMaxInString) throws ParseException {
        List<Date> dates = new ArrayList<Date>();
        String[] datesStringTab = {dateMinInString, dateMaxInString};
        for (int i = 0; i < datesStringTab.length; i++) {
            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            dates.add(formatter.parse(datesStringTab[i]));
        }
        return dates;
    }
}
