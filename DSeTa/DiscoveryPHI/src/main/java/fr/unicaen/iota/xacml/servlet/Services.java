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
package fr.unicaen.iota.xacml.servlet;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.auth.User;
import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.Service;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.utils.InterfaceHelper;
import fr.unicaen.iota.utils.MapSessions;
import fr.unicaen.iota.utils.PEPRequester;
import fr.unicaen.iota.xacml.conf.Configuration;
import fr.unicaen.iota.xacml.ihm.Module;
import fr.unicaen.iota.xacml.pep.MethodNamesAdmin;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
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

    public String createPartnerGroup(String sessionId, User user, Module module, String value)
            throws ServiceException {
        String method = module == Module.adminModule ? "createAdminPartnerGroup" : "createPartnerGroup";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        GroupPolicy gpol = new GroupPolicy(value, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryGroupPolicy(partner, gpol);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureGroupPolicy(partner, gpol);
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.addAdminGroupPolicy(partner, gpol);
                break;
        }
        if (!resp) {
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public void deletePartnerGroup(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "deleteAdminPartnerGroup" : "deletePartnerGroup";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void addPartnerToGroup(String sessionId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "addAdminPartnerToGroup" : "addPartnerToGroup";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void removePartnerFromGroup(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "removeAdminPartnerFromGroup" : "removePartnerFromGroup";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException(method + ": internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addBizStepRestriction(String sessionId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addBizStepRestriction");
        String partner = user.getPartner().getPartnerID();
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

    public void removeBizStepRestriction(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeBizStepRestriction");
        String partner = user.getPartner().getPartnerID();
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

    public void addEPCRestriction(String sessionId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addRestriction");
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException("addEPCRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEPCRestriction(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEPCRestriction");
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException("removeEPCRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addEPCClassRestriction(String sessionId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
        checkAccess(user, module, "addEPCClassRestriction");
        String partner = user.getPartner().getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryEpcClassFilter(partner, groupId, value);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureEpcClassFilter(partner, groupId, value);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addEPCClassRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeEPCClassRestriction(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeEPCClassRestriction");
        String partner = user.getPartner().getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryEpcClassFilter(partner, groupId, objectId);
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureEpcClassFilter(partner, groupId, objectId);
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeEPCClassRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void addTimeRestriction(String sessionId, User user, Module module,
            String objectId, String groupId, String valueMin, String valueMax) throws ServiceException {
        checkAccess(user, module, "addTimeRestriction");
        String partner = user.getPartner().getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.addQueryEventTimeFilter(partner,
                        groupId, convertStringToDate(valueMin, valueMax));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.addCaptureEventTimeFilter(partner,
                        groupId, convertStringToDate(valueMin, valueMax));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("addTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public void removeTimeRestriction(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "removeTimeRestriction");
        String partner = user.getPartner().getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.removeQueryEventTimeFilter(partner, groupId, convertStringToDate((String) objectId));
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.removeCaptureEventTimeFilter(partner, groupId, convertStringToDate((String) objectId));
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("removeTimeRestriction: internal error in module: " + module, ServiceErrorType.xacml);
        }
    }

    public String switchBizStepPolicy(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchBizStepPolicy");
        String partner = user.getPartner().getPartnerID();
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

    public String switchEPCPolicy(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEPCPolicy");
        String partner = user.getPartner().getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEpcs(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getEpcsFilterFunction().getValue();
                break;
            case captureModule:
                break;
            case adminModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEpcs(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getEpcsFilterFunction().getValue();
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEPCPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchEPCClassPolicy(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchEPCClassPolicy");
        String partner = user.getPartner().getPartnerID();
        InterfaceHelper interfaceHelper = MapSessions.getAPMSession(sessionId, partner);
        boolean resp = false;
        String value = "";
        switch (module) {
            case queryModule:
                resp = interfaceHelper.APMSession.switchQueryPermissionEpcClasses(partner, groupId);
                value = (interfaceHelper.APMSession.getQueryPolicy(partner)).getGroupPolicy(groupId).getEpcClassesFilterFunction().getValue();
                break;
            case captureModule:
                resp = interfaceHelper.APMSession.switchCapturePermissionEpcClasses(partner, groupId);
                value = (interfaceHelper.APMSession.getCapturePolicy(partner)).getGroupPolicy(groupId).getEpcClassesFilterFunction().getValue();
                break;
            case adminModule:
                break;
        }
        if (!resp) {
            throw new ServiceException("switchEPCClassPolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchTimePolicy(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        checkAccess(user, module, "switchTimePolicy");
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException("switchTimePolicy: internal error in module: " + module, ServiceErrorType.xacml);
        }
        return value;
    }

    public String switchUserPermissionPolicy(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "switchAdminUserPermissionPolicy" : "switchUserPermissionPolicy";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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

    public void removeUserPermission(String sessionId, User user, Module module,
            String objectId, String groupId) throws ServiceException {
        String method = module == Module.adminModule ? "removeAdminUserPermission" : "removeUserPermission";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void addUserPermission(String sessionId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "addAdminUserPermission" : "addUserPermission";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void updateGroupName(String sessionId, User user, Module module,
            String objectId, String groupId, String value) throws ServiceException {
        String method = module == Module.adminModule ? "updateAdminGroupName" : "updateGroupName";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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
            default:
                break;
        }
        if (!resp) {
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void savePolicyPartner(String sessionId, User user, Module module) throws ServiceException {
        String method = module == Module.adminModule ? "saveAdminPolicyPartner" : "savePolicyPartner";
        checkAccess(user, module, method);
        String partner = user.getPartner().getPartnerID();
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
            default:
                break;
        }
        if (!resp) {
            throw new ServiceException(method + ": internal error in module " + module, ServiceErrorType.xacml);
        }
    }

    public void updatePartner(String sessionId, User user, int partnerUID, String partnerID,
            String serviceID, String serviceAddress, String serviceType) throws ServiceException {
        checkAccess(user, Module.adminModule, "partnerUpdate");
        DsClient dsClient = new DsClient(Configuration.DS_ADDRESS);
        Service service;
        try {
            service = new Service(serviceID, serviceType, new URI(serviceAddress));
        } catch (MalformedURIException ex) {
            throw new ServiceException("Service URL malformed!", ServiceErrorType.unknown);
        }
        List<Service> lService = new ArrayList<Service>();
        lService.add(service);
        try {
            dsClient.partnerUpdate(sessionId, partnerUID, partnerID, lService);
        } catch (RemoteException ex) {
            throw new ServiceException("DS Communication Failure: internal protocol error!", ServiceErrorType.unknown);
        } catch (EnhancedProtocolException ex) {
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        }
    }

    public void createUser(String sessionId, User user, String login, String pass) throws ServiceException {
        checkAccess(user, Module.adminModule, "userCreate");
        DsClient dsClient = new DsClient(Configuration.DS_ADDRESS);
        String partner = user.getPartner().getPartnerID();
        try {
            dsClient.userCreate(sessionId, partner, login, pass, 30);
        } catch (RemoteException ex) {
            throw new ServiceException("DS Communication Failure: internal protocol error!", ServiceErrorType.unknown);
        } catch (EnhancedProtocolException ex) {
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        }
    }

    public void updateUser(String sessionId, User user, String login, String pass) throws ServiceException {
        checkAccess(user, Module.adminModule, "userUpdate");
        DsClient dsClient = new DsClient(Configuration.DS_ADDRESS);
        String partner = user.getPartner().getPartnerID();
        try {
            dsClient.userUpdate(sessionId, user.getId(), partner, login, pass, 30);
        } catch (RemoteException ex) {
            throw new ServiceException("DS Communication Failure: internal protocol error!", ServiceErrorType.unknown);
        } catch (EnhancedProtocolException ex) {
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        }
    }

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

    public boolean createAccount(String sessionId, User user, String partnerId, String serviceId,
            String serviceType, String serviceAddress, String login, String pass) throws ServiceException {
        checkAccess(user, Module.adminModule, "superadmin");
        DsClient dsClient = new DsClient(Configuration.DS_ADDRESS);
        try {
            Service service = new Service(serviceId, serviceType, new URI(serviceAddress));
            List<Service> sList = new ArrayList<Service>();
            sList.add(service);
            boolean found = false;
            try {
                dsClient.partnerInfo(sessionId, partnerId);
                found = true;
            } catch (EnhancedProtocolException ex) {
            }
            try {
                dsClient.userInfo(sessionId, login);
                found = true;
            } catch (EnhancedProtocolException ex) {
            }
            if (found) {
                throw new ServiceException("User or Partner exists", ServiceErrorType.unknown);
            }
            dsClient.partnerCreate(sessionId, partnerId, sList);
            dsClient.userCreate(sessionId, partnerId, login, pass, 30);
            createRootPartnerPolicy(sessionId, login, partnerId);
        } catch (MalformedURIException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            throw new ServiceException("DS Communication Failure: internal protocol error!", ServiceErrorType.unknown);
        } catch (EnhancedProtocolException ex) {
            throw new ServiceException(ex.getMessage(), ServiceErrorType.unknown);
        }
        return true;
    }

    public void loadPolicyTree(User user, Module module) {
    }

    public void cancelPartnerPolicy(User user, Module module) {
    }

    private List<Date> convertStringToDate(String dateInString) {
        List<Date> dates = new ArrayList<Date>();
        String[] datesStringTab = dateInString.split(" -> ");
        for (int i = 0; i < datesStringTab.length; i++) {
            String[] dateValue = datesStringTab[i].split("/");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(dateValue[2]), Integer.parseInt(dateValue[0]), Integer.parseInt(dateValue[1]), 0, 0, 0);

            long time = cal.getTimeInMillis() - cal.get(Calendar.MILLISECOND);
            Date date = new Date(time);
            dates.add(date);
        }
        return dates;
    }

    private List<Date> convertStringToDate(String dateMinInString, String dateMaxInString) {
        List<Date> dates = new ArrayList<Date>();
        String[] datesStringTab = {dateMinInString, dateMaxInString};
        for (int i = 0; i < datesStringTab.length; i++) {
            String[] dateValue = datesStringTab[i].split("/");
            Calendar cal = Calendar.getInstance();
            cal.set(Integer.parseInt(dateValue[2]), Integer.parseInt(dateValue[0]), Integer.parseInt(dateValue[1]), 0, 0, 0);

            long time = cal.getTimeInMillis() - cal.get(Calendar.MILLISECOND);
            Date date = new Date(time);
            dates.add(date);
        }
        return dates;
    }
}
