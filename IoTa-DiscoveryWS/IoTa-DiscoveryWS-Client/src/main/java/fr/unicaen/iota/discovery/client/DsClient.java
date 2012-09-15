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
package fr.unicaen.iota.discovery.client;

import fr.unicaen.iota.discovery.client.model.*;
import fr.unicaen.iota.discovery.client.soap.ESDS_ServiceStub;
import fr.unicaen.iota.discovery.client.soap.ESDS_ServiceStub.TEventTypeChoice;
import fr.unicaen.iota.discovery.client.soap.ESDS_ServiceStub.TServiceType;
import fr.unicaen.iota.discovery.client.soap.ServicePool;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.discovery.client.util.StatusCodeHelper;
import java.rmi.RemoteException;
import java.util.*;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axis2.databinding.ADBBean;
import org.apache.axis2.databinding.types.PositiveInteger;
import org.apache.axis2.databinding.types.Token;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DsClient {

    private static final Log LOG = LogFactory.getLog(DsClient.class);
    private ServicePool servicePool = ServicePool.getInstance();
    private String dsAddress;

    public DsClient(String dsAddress) {
        this.dsAddress = dsAddress;
    }

    public String hello(String sessionId) throws EnhancedProtocolException, RemoteException {
        ESDS_ServiceStub.Hello operation = new ESDS_ServiceStub.Hello();
        ESDS_ServiceStub.HelloIn in = new ESDS_ServiceStub.HelloIn();
        in.setSid(createSessionId(sessionId));
        operation.setHello(in);
        ESDS_ServiceStub.HelloResult res = (ESDS_ServiceStub.HelloResult) execOperation(operation);
        ESDS_ServiceStub.HelloOut out = res.getHelloResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        LOG.info("Hello result: " + out.getServerIdentity());
        return out.getServerIdentity().toString();
    }

    public List<UserId> userLookup(String sessionId, String userID) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.UserLookup operation = new ESDS_ServiceStub.UserLookup();
        ESDS_ServiceStub.UserLookupIn in = new ESDS_ServiceStub.UserLookupIn();
        in.setSid(createSessionId(sessionId));
        ESDS_ServiceStub.TUserID tUserID = new ESDS_ServiceStub.TUserID();
        tUserID.setTUserID(new Token(userID));
        in.setUserID(tUserID);
        operation.setUserLookup(in);
        ESDS_ServiceStub.UserLookupResult res = (ESDS_ServiceStub.UserLookupResult) execOperation(operation);
        ESDS_ServiceStub.UserLookupOut out = res.getUserLookupResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        List<UserId> result = new ArrayList<UserId>();
        ESDS_ServiceStub.TUserItemList list = res.getUserLookupResult().getUserList();
        if (list.getUser() == null) {
            return result;
        }
        for (ESDS_ServiceStub.TUserItem userItem : list.getUser()) {
            UserId userId = new UserId(userItem.getUid().getTSmallUID(), userItem.getId().getTUserID().toString());
            result.add(userId);
        }
        return result;
    }

    public int userCreate(String sessionId, String partnerId, String userId, String passwd, int ttl) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.UserCreate userCreate = new ESDS_ServiceStub.UserCreate();
        ESDS_ServiceStub.UserCreateIn in = new ESDS_ServiceStub.UserCreateIn();
        in.setLoginMode(ESDS_ServiceStub.TLoginMode.value1);
        in.setPartnerID(createPartnerId(partnerId));
        ESDS_ServiceStub.TLeaseSeconds tLeaseSeconds = new ESDS_ServiceStub.TLeaseSeconds();
        tLeaseSeconds.setTLeaseSeconds(ttl);
        in.setSessionLease(tLeaseSeconds);
        ESDS_ServiceStub.TPassword tPassword = new ESDS_ServiceStub.TPassword();
        tPassword.setTPassword(new Token(passwd));
        in.setPassword(tPassword);
        ESDS_ServiceStub.TRoleID tRoleID = new ESDS_ServiceStub.TRoleID();
        tRoleID.setTRoleID(new Token("r001"));
        in.setRoleID(tRoleID);
        in.setSid(createSessionId(sessionId));
        in.setUserID(createUserId(userId));
        userCreate.setUserCreate(in);
        ESDS_ServiceStub.UserCreateResult res = (ESDS_ServiceStub.UserCreateResult) execOperation(userCreate);
        ESDS_ServiceStub.UserCreateOut out = res.getUserCreateResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        return out.getUserUID().getTSmallUID();
    }

    public UserInfo userInfo(String sessionId, String userId) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.UserInfo userInfo = new ESDS_ServiceStub.UserInfo();
        ESDS_ServiceStub.UserInfoIn in = new ESDS_ServiceStub.UserInfoIn();
        in.setSid(createSessionId(sessionId));
        in.setUserID(createUserId(userId));
        userInfo.setUserInfo(in);
        ESDS_ServiceStub.UserInfoResult res = (ESDS_ServiceStub.UserInfoResult) execOperation(userInfo);
        ESDS_ServiceStub.UserInfoOut out = res.getUserInfoResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        return new UserInfo(out.getUserUID().getTSmallUID(),
                out.getUserID().getTUserID().toString(),
                out.getPartnerID().getTPartnerID().toString());
    }

    public void userUpdate(String sessionId, int userUID, String partnerId, String userId, String passwd, int ttl) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.UserUpdate userUpdate = new ESDS_ServiceStub.UserUpdate();
        ESDS_ServiceStub.UserUpdateIn in = new ESDS_ServiceStub.UserUpdateIn();
        in.setLoginMode(ESDS_ServiceStub.TLoginMode.value1);
        in.setPartnerID(createPartnerId(partnerId));
        ESDS_ServiceStub.TLeaseSeconds tLeaseSeconds = new ESDS_ServiceStub.TLeaseSeconds();
        tLeaseSeconds.setTLeaseSeconds(ttl);
        in.setSessionLease(tLeaseSeconds);
        ESDS_ServiceStub.TPassword tPassword = new ESDS_ServiceStub.TPassword();
        tPassword.setTPassword(new Token(passwd));
        in.setPassword(tPassword);
        ESDS_ServiceStub.TRoleID tRoleID = new ESDS_ServiceStub.TRoleID();
        tRoleID.setTRoleID(new Token("r001"));
        in.setRoleID(tRoleID);
        in.setSid(createSessionId(sessionId));
        in.setUserID(createUserId(userId));
        ESDS_ServiceStub.TSmallUID uid = new ESDS_ServiceStub.TSmallUID();
        uid.setTSmallUID(userUID);
        in.setUserUID(uid);
        userUpdate.setUserUpdate(in);
        ESDS_ServiceStub.UserUpdateResult res = (ESDS_ServiceStub.UserUpdateResult) execOperation(userUpdate);
        ESDS_ServiceStub.UserUpdateOut out = res.getUserUpdateResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
    }

    public void userDelete(String sessionId, String userId) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.UserDelete userDelete = new ESDS_ServiceStub.UserDelete();
        ESDS_ServiceStub.UserDeleteIn in = new ESDS_ServiceStub.UserDeleteIn();
        in.setSid(createSessionId(sessionId));
        in.setUserID(createUserId(userId));
        userDelete.setUserDelete(in);
        ESDS_ServiceStub.UserDeleteResult res = (ESDS_ServiceStub.UserDeleteResult) execOperation(userDelete);
        ESDS_ServiceStub.UserDeleteOut out = res.getUserDeleteResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
    }

    public Session userLogin(String sessionId, String userId, String passwd) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.UserLogin userLogin = new ESDS_ServiceStub.UserLogin();
        ESDS_ServiceStub.UserLoginIn in = new ESDS_ServiceStub.UserLoginIn();
        in.setSid(createSessionId(sessionId));
        in.setUserID(createUserId(userId));
        ESDS_ServiceStub.TPassword tPassword = new ESDS_ServiceStub.TPassword();
        tPassword.setTPassword(new Token(passwd));
        in.setPassword(tPassword);
        userLogin.setUserLogin(in);
        ESDS_ServiceStub.UserLoginResult res = (ESDS_ServiceStub.UserLoginResult) execOperation(userLogin);
        ESDS_ServiceStub.UserLoginOut out = res.getUserLoginResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        return new Session(out.getSid().getTSessionID().toString(), out.getSessionLease().getTLeaseSeconds());
    }

    public void userLogout(String sessionId) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.UserLogout userLogout = new ESDS_ServiceStub.UserLogout();
        ESDS_ServiceStub.UserLogoutIn in = new ESDS_ServiceStub.UserLogoutIn();
        in.setSid(createSessionId(sessionId));
        userLogout.setUserLogout(in);
        ESDS_ServiceStub.UserLogoutResult res = (ESDS_ServiceStub.UserLogoutResult) execOperation(userLogout);
        ESDS_ServiceStub.UserLogoutOut out = res.getUserLogoutResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
    }

    public List<PartnerId> partnerLookup(String sessionId, String partnerId) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.PartnerLookup partnerLookup = new ESDS_ServiceStub.PartnerLookup();
        ESDS_ServiceStub.PartnerLookupIn in = new ESDS_ServiceStub.PartnerLookupIn();
        in.setPartnerID(createPartnerId(partnerId));
        in.setSid(createSessionId(sessionId));
        partnerLookup.setPartnerLookup(in);
        ESDS_ServiceStub.PartnerLookupResult res = (ESDS_ServiceStub.PartnerLookupResult) execOperation(partnerLookup);
        ESDS_ServiceStub.PartnerLookupOut out = res.getPartnerLookupResult();
        LOG.info("PartnerLookup result -> code: " + out.getResult().getValue() + " desc: " + out.getResult().getDesc());
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        List<PartnerId> result = new ArrayList<PartnerId>();
        if (out.getPartnerList().getPartner() == null) {
            return result;
        }
        for (ESDS_ServiceStub.TPartnerItem tPartnerItem : out.getPartnerList().getPartner()) {
            result.add(new PartnerId(tPartnerItem.getUid().getTSmallUID(),
                    tPartnerItem.getId().getTPartnerID().toString()));
        }
        return result;
    }

    public int partnerCreate(String sessionId, String partnerId, Collection<Service> services) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.PartnerCreate partnerCreate = new ESDS_ServiceStub.PartnerCreate();
        ESDS_ServiceStub.PartnerCreateIn in = new ESDS_ServiceStub.PartnerCreateIn();

        in.setPartnerID(createPartnerId(partnerId));
        ESDS_ServiceStub.TServiceItemList tServiceItemList = new ESDS_ServiceStub.TServiceItemList();
        for (Service service : services) {

            ESDS_ServiceStub.TServiceItem tServiceItem = new ESDS_ServiceStub.TServiceItem();
            ESDS_ServiceStub.TServiceID tServiceID = new ESDS_ServiceStub.TServiceID();
            tServiceID.setTServiceID(new Token(service.getId()));
            tServiceItem.setId(tServiceID);
            tServiceItem.setType(createServiceType(service.getType()));
            ESDS_ServiceStub.TServiceURI tServiceURI = new ESDS_ServiceStub.TServiceURI();
            tServiceURI.setTServiceURI(new URI(service.getUri()));
            tServiceItem.setUri(tServiceURI);
            tServiceItemList.setAction(ESDS_ServiceStub.TListAction.add);
            tServiceItemList.addService(tServiceItem);
        }
        in.setServiceList(tServiceItemList);
        in.setSid(createSessionId(sessionId));
        partnerCreate.setPartnerCreate(in);
        ESDS_ServiceStub.PartnerCreateResult res = (ESDS_ServiceStub.PartnerCreateResult) execOperation(partnerCreate);
        ESDS_ServiceStub.PartnerCreateOut out = res.getPartnerCreateResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        return out.getPartnerUID().getTSmallUID();
    }

    public PartnerInfo partnerInfo(String sessionId, String partnerId) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.PartnerInfo partnerInfo = new ESDS_ServiceStub.PartnerInfo();
        ESDS_ServiceStub.PartnerInfoIn in = new ESDS_ServiceStub.PartnerInfoIn();
        in.setPartnerID(createPartnerId(partnerId));
        in.setSid(createSessionId(sessionId));
        partnerInfo.setPartnerInfo(in);
        ESDS_ServiceStub.PartnerInfoResult res = (ESDS_ServiceStub.PartnerInfoResult) execOperation(partnerInfo);
        ESDS_ServiceStub.PartnerInfoOut out = res.getPartnerInfoResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }

        ESDS_ServiceStub.TServiceItemList tServiceItemList = out.getServiceList();
        PartnerInfo result = new PartnerInfo(out.getPartnerUID().getTSmallUID(), out.getPartnerID().getTPartnerID().toString());
        for (ESDS_ServiceStub.TServiceItem item : tServiceItemList.getService()) {
            Service serviceModel = new Service(item.getId().getTServiceID().toString(), item.getType().getValue().toString(), item.getUri().getTServiceURI());
            result.addService(serviceModel);
        }
        return result;
    }

    public void partnerUpdate(String sessionId, int partnerUID, String partnerId, Collection<Service> serviceList) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.PartnerUpdate partnerUpdate = new ESDS_ServiceStub.PartnerUpdate();
        ESDS_ServiceStub.PartnerUpdateIn in = new ESDS_ServiceStub.PartnerUpdateIn();

        in.setPartnerID(createPartnerId(partnerId));
        ESDS_ServiceStub.TSmallUID tSmallUID = new ESDS_ServiceStub.TSmallUID();
        tSmallUID.setTSmallUID(partnerUID);
        in.setPartnerUID(tSmallUID);
        ESDS_ServiceStub.TServiceItemList tServiceItemList = new ESDS_ServiceStub.TServiceItemList();
        for (Service service : serviceList) {
            ESDS_ServiceStub.TServiceItem tServiceItem = new ESDS_ServiceStub.TServiceItem();
            ESDS_ServiceStub.TServiceID tServiceID = new ESDS_ServiceStub.TServiceID();
            tServiceID.setTServiceID(new Token(service.getId()));
            tServiceItem.setId(tServiceID);
            tServiceItem.setType(createServiceType(service.getType()));
            ESDS_ServiceStub.TServiceURI tServiceURI = new ESDS_ServiceStub.TServiceURI();
            tServiceURI.setTServiceURI(new URI(service.getUri()));
            tServiceItem.setUri(tServiceURI);
            tServiceItemList.addService(tServiceItem);
        }
        in.setServiceList(tServiceItemList);
        in.setSid(createSessionId(sessionId));
        partnerUpdate.setPartnerUpdate(in);
        ESDS_ServiceStub.PartnerUpdateResult res = (ESDS_ServiceStub.PartnerUpdateResult) execOperation(partnerUpdate);
        ESDS_ServiceStub.PartnerUpdateOut out = res.getPartnerUpdateResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
    }

    public void partnerDelete(String sessionId, String partnerId) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.PartnerDelete operation = new ESDS_ServiceStub.PartnerDelete();
        ESDS_ServiceStub.PartnerDeleteIn in = new ESDS_ServiceStub.PartnerDeleteIn();

        in.setPartnerID(createPartnerId(partnerId));
        in.setSid(createSessionId(sessionId));

        operation.setPartnerDelete(in);
        ESDS_ServiceStub.PartnerDeleteResult res = (ESDS_ServiceStub.PartnerDeleteResult) execOperation(operation);
        ESDS_ServiceStub.PartnerDeleteOut out = res.getPartnerDeleteResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
    }

    public List<Event> eventLookup(String sessionId, String objectId, Calendar start, Calendar end, String BizStep) throws MalformedURIException, RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.EventLookup eventLookup = new ESDS_ServiceStub.EventLookup();
        ESDS_ServiceStub.EventLookupIn in = new ESDS_ServiceStub.EventLookupIn();
        in.setObjectID(createObjectId(objectId));
        in.setSid(createSessionId(sessionId));
        if (BizStep != null) {
            ESDS_ServiceStub.TLifeCycleStepID tLifeCycleStepID = new ESDS_ServiceStub.TLifeCycleStepID();
            tLifeCycleStepID.setTLifeCycleStepID(new URI(BizStep));
            in.setLifeCycleStepID(tLifeCycleStepID);
        }
        if (start != null) {
            in.setStartingAt(start);
        }
        if (end != null) {
            in.setEndingAt(end);
        }
        eventLookup.setEventLookup(in);
        ESDS_ServiceStub.EventLookupResult res = (ESDS_ServiceStub.EventLookupResult) execOperation(eventLookup);
        ESDS_ServiceStub.EventLookupOut out = res.getEventLookupResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        ESDS_ServiceStub.TEventItemList tEventList = out.getEventList();
        List<Event> list = new ArrayList<Event>();
        if (tEventList.getEvent() == null) {
            return list;
        }
        for (ESDS_ServiceStub.TEventItem event : tEventList.getEvent()) {
            Event e = new Event(event.getE().getTEventID().intValue(),
                    event.getO().getTObjectID().toString(),
                    event.getP().getTPartnerID().toString(),
                    event.getU().getTUserID().toString(),
                    event.getLcs().getTLifeCycleStepID().toString(),
                    event.getT().getValue().toString(),
                    event.getC().getTEventClass().toString(),
                    event.getEts(),
                    event.getSts(),
                    null);
            if (event.getServiceList().getService() != null) {
                for (ESDS_ServiceStub.TServiceItem item : event.getServiceList().getService()) {
                    e.addService(new Service(item.getId().getTServiceID().toString(), item.getType().getValue().toString(), item.getUri().getTServiceURI()));
                }
            }
            list.add(e);
        }
        return list;
    }

    public int eventCreate(String sessionId, String partnerId, String objectId, String bizStep, String eventClass,
            Calendar sourceTimeStamp, int ttl, Collection<String> serviceIds, int priority, Map<String, String> extensions)
            throws MalformedURIException, EnhancedProtocolException, RemoteException {
        ESDS_ServiceStub.EventCreate operation = new ESDS_ServiceStub.EventCreate();
        ESDS_ServiceStub.EventCreateIn in = new ESDS_ServiceStub.EventCreateIn();
        in.setEvent(createTObjectEventTypeChoice(objectId, bizStep, eventClass,
                sourceTimeStamp, ttl, serviceIds, priority, extensions));
        ESDS_ServiceStub.TSupplyChainID tSupplyChainID = new ESDS_ServiceStub.TSupplyChainID();
        tSupplyChainID.setTSupplyChainID(new Token("not_used"));
        in.setSupplyChainID(tSupplyChainID);
        in.setProxyPartnerID(createPartnerId(partnerId));
        in.setSid(createSessionId(sessionId));
        operation.setEventCreate(in);
        ESDS_ServiceStub.EventCreateResult res = (ESDS_ServiceStub.EventCreateResult) execOperation(operation);
        ESDS_ServiceStub.EventCreateOut out = res.getEventCreateResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        return out.getEventID().getTEventID().intValue();
    }

    public EventInfo eventInfo(String sessionId, int eventUID) throws RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.EventInfo eventInfo = new ESDS_ServiceStub.EventInfo();
        ESDS_ServiceStub.EventInfoIn in = new ESDS_ServiceStub.EventInfoIn();

        ESDS_ServiceStub.TEventID tEventID = new ESDS_ServiceStub.TEventID();
        tEventID.setTEventID(new PositiveInteger(eventUID + ""));
        in.setEventID(tEventID);
        in.setSid(createSessionId(sessionId));

        eventInfo.setEventInfo(in);
        ESDS_ServiceStub.EventInfoResult res = (ESDS_ServiceStub.EventInfoResult) execOperation(eventInfo);
        ESDS_ServiceStub.EventInfoOut out = res.getEventInfoResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }
        ESDS_ServiceStub.TInfoEvent tInfoEvent = out.getEvent();
        Event e = new Event(tInfoEvent.getEventID().getTEventID().intValue(),
                tInfoEvent.getObjectID().getTObjectID().toString(),
                tInfoEvent.getPartnerID().getTPartnerID().toString(),
                tInfoEvent.getUserID().getTUserID().toString(),
                tInfoEvent.getLifeCycleStepID().getTLifeCycleStepID().toString(),
                tInfoEvent.getEventType().getValue().toString(),
                tInfoEvent.getEventClass().getTEventClass().toString(),
                tInfoEvent.getEventTS(),
                tInfoEvent.getSourceTS(),
                parseExtentions(out.getExtension()));
        if (tInfoEvent.getServiceList().getService() != null) {
            for (ESDS_ServiceStub.TServiceItem item : tInfoEvent.getServiceList().getService()) {
                e.addService(new Service(item.getId().getTServiceID().toString(), item.getType().getValue().toString(), item.getUri().getTServiceURI()));
            }
        }
        EventInfo result = new EventInfo(e, tInfoEvent.getPriority().getTEventPriority(), tInfoEvent.getTtl().getTEventTTL().intValue());
        return result;
    }

    public List<Integer> multipleEventCreate(String sessionId, String partnerId, Collection<EventInfo> eventList)
            throws MalformedURIException, RemoteException, EnhancedProtocolException {
        ESDS_ServiceStub.MultipleEventCreate multipleEventCreate = new ESDS_ServiceStub.MultipleEventCreate();
        ESDS_ServiceStub.MultipleEventCreateIn in = new ESDS_ServiceStub.MultipleEventCreateIn();
        in.setProxyPartnerID(createPartnerId(partnerId));
        in.setSid(createSessionId(sessionId));
        ESDS_ServiceStub.TSupplyChainID tSupplyChainID = new ESDS_ServiceStub.TSupplyChainID();
        tSupplyChainID.setTSupplyChainID(new Token("not_used"));  // HACK
        in.setSupplyChainID(tSupplyChainID);
        ESDS_ServiceStub.TObjectEventList objectEventList = new ESDS_ServiceStub.TObjectEventList();
        for (EventInfo event : eventList) {
            ESDS_ServiceStub.TObjectEvent tObjectEvent =
                    createTObjectEventTypeChoice(event.getEvent().getObjectId(),
                    event.getEvent().getBizStep(),
                    event.getEvent().getEventClass(),
                    event.getEvent().getSourceTimeStamp(),
                    event.getTtl(),
                    createServiceIds(event.getEvent().getServiceList()),
                    event.getPriority(),
                    event.getEvent().getExtensions()).getObjectEvent();
            objectEventList.addObjectEvent(tObjectEvent);
        }
        in.setEvents(objectEventList);
        multipleEventCreate.setMultipleEventCreate(in);
        ESDS_ServiceStub.MultipleEventCreateResult res = (ESDS_ServiceStub.MultipleEventCreateResult) execOperation(multipleEventCreate);
        ESDS_ServiceStub.MultipleEventCreateOut out = res.getMultipleEventCreateResult();
        int statusCode = out.getResult().getCode().getValue();
        if (StatusCodeHelper.isErrorCode(statusCode)) {
            throw new EnhancedProtocolException(statusCode, out.getResult().getDesc());
        }

        List<Integer> result = new ArrayList<Integer>();
        if (out.getEventIDList().getEventID() == null) {
            return result;
        }
        for (ESDS_ServiceStub.TEventID tEventID : out.getEventIDList().getEventID()) {
            result.add(tEventID.getTEventID().intValue());
        }
        return result;
    }
  
    private ADBBean execOperation(ADBBean operation) throws RemoteException {
        ESDS_ServiceStub service;
        ADBBean result = null;
        try {
            service = servicePool.getServiceInstance(dsAddress);
            try {
                if (operation instanceof ESDS_ServiceStub.Hello) {
                    result = service.hello((ESDS_ServiceStub.Hello) operation);
                } else if (operation instanceof ESDS_ServiceStub.UserLookup) {
                    result = service.userLookup((ESDS_ServiceStub.UserLookup) operation);
                } else if (operation instanceof ESDS_ServiceStub.UserCreate) {
                    result = service.userCreate((ESDS_ServiceStub.UserCreate) operation);
                } else if (operation instanceof ESDS_ServiceStub.UserInfo) {
                    result = service.userInfo((ESDS_ServiceStub.UserInfo) operation);
                } else if (operation instanceof ESDS_ServiceStub.UserUpdate) {
                    result = service.userUpdate((ESDS_ServiceStub.UserUpdate) operation);
                } else if (operation instanceof ESDS_ServiceStub.UserDelete) {
                    result = service.userDelete((ESDS_ServiceStub.UserDelete) operation);
                } else if (operation instanceof ESDS_ServiceStub.UserLogin) {
                    result = service.userLogin((ESDS_ServiceStub.UserLogin) operation);
                } else if (operation instanceof ESDS_ServiceStub.UserLogout) {
                    result = service.userLogout((ESDS_ServiceStub.UserLogout) operation);
                } else if (operation instanceof ESDS_ServiceStub.PartnerLookup) {
                    result = service.partnerLookup((ESDS_ServiceStub.PartnerLookup) operation);
                } else if (operation instanceof ESDS_ServiceStub.PartnerCreate) {
                    result = service.partnerCreate((ESDS_ServiceStub.PartnerCreate) operation);
                } else if (operation instanceof ESDS_ServiceStub.PartnerInfo) {
                    result = service.partnerInfo((ESDS_ServiceStub.PartnerInfo) operation);
                } else if (operation instanceof ESDS_ServiceStub.PartnerUpdate) {
                    result = service.partnerUpdate((ESDS_ServiceStub.PartnerUpdate) operation);
                } else if (operation instanceof ESDS_ServiceStub.PartnerDelete) {
                    result = service.partnerDelete((ESDS_ServiceStub.PartnerDelete) operation);
                } else if (operation instanceof ESDS_ServiceStub.EventLookup) {
                    result = service.eventLookup((ESDS_ServiceStub.EventLookup) operation);
                } else if (operation instanceof ESDS_ServiceStub.EventCreate) {
                    result = service.eventCreate((ESDS_ServiceStub.EventCreate) operation);
                } else if (operation instanceof ESDS_ServiceStub.EventInfo) {
                    result = service.eventInfo((ESDS_ServiceStub.EventInfo) operation);
                } else if (operation instanceof ESDS_ServiceStub.MultipleEventCreate) {
                    result = service.multipleEventCreate((ESDS_ServiceStub.MultipleEventCreate) operation);
                }
            } catch (RemoteException rex) {
                servicePool.releaseInstance(service);
                LOG.error(null, rex);
                throw new RemoteException(rex.getMessage(), rex.getCause());
            }
            servicePool.releaseInstance(service);
            return result;
        } catch (InterruptedException ex) {
            LOG.error(null, ex);
            throw new RemoteException(ex.getMessage(), ex.getCause());
        }
    }

    private ESDS_ServiceStub.TSessionID createSessionId(String sessionId) {
        ESDS_ServiceStub.TSessionID sessionID = new ESDS_ServiceStub.TSessionID();
        sessionID.setTSessionID(new Token(sessionId));
        LOG.debug("creating session: " + sessionID);
        return sessionID;
    }

    private ESDS_ServiceStub.TUserID createUserId(String userId) {
        ESDS_ServiceStub.TUserID userID = new ESDS_ServiceStub.TUserID();
        userID.setTUserID(new Token(userId));
        LOG.debug("create user: " + userID);
        return userID;
    }

    private ESDS_ServiceStub.TPartnerID createPartnerId(String partnerId) {
        ESDS_ServiceStub.TPartnerID tPartnerID = new ESDS_ServiceStub.TPartnerID();
        tPartnerID.setTPartnerID(new Token(partnerId));
        LOG.debug("create partner: " + tPartnerID);
        return tPartnerID;
    }

    private ESDS_ServiceStub.TObjectID createObjectId(String objectId) throws MalformedURIException {
        ESDS_ServiceStub.TObjectID tObjectID = new ESDS_ServiceStub.TObjectID();
        tObjectID.setTObjectID(new URI(objectId));
        LOG.debug("create object: " + tObjectID);
        return tObjectID;
    }
    private static final Map<String, TServiceType> SERVICE_TYPES = new HashMap<String, TServiceType>();

    static {
        SERVICE_TYPES.put("ds", TServiceType.ds);
        SERVICE_TYPES.put("epcis", TServiceType.epcis);
        SERVICE_TYPES.put("ws", TServiceType.ws);
        SERVICE_TYPES.put("html", TServiceType.html);
        SERVICE_TYPES.put("xmlrpc", TServiceType.xmlrpc);
    }

    private TServiceType createServiceType(String type) {
        return SERVICE_TYPES.get(type.toLowerCase());
    }

    private TEventTypeChoice createTObjectEventTypeChoice(String objectId, String bizStep, String eventClass,
            Calendar sourceTimeStamp, int ttl, Collection<String> serviceIds, int priority, Map<String, String> extensions)
            throws MalformedURIException {
        TEventTypeChoice tEventTypeChoice = new TEventTypeChoice();
        ESDS_ServiceStub.TObjectEvent tObjectEvent = new ESDS_ServiceStub.TObjectEvent();

        ESDS_ServiceStub.TServiceIDList tServiceIDList = new ESDS_ServiceStub.TServiceIDList();
        for (String serviceId : serviceIds) {
            ESDS_ServiceStub.TServiceID tServiceID = new ESDS_ServiceStub.TServiceID();
            tServiceID.setTServiceID(new Token(serviceId));
            tServiceIDList.addId(tServiceID);
        }
        tObjectEvent.setServiceList(tServiceIDList);

        ESDS_ServiceStub.TEventTTL tEventTTL = new ESDS_ServiceStub.TEventTTL();
        tEventTTL.setTEventTTL(new PositiveInteger(ttl + ""));
        tObjectEvent.setTtl(tEventTTL);

        tObjectEvent.setSourceTS(sourceTimeStamp);

        ESDS_ServiceStub.TEventPriority tEventPriority = new ESDS_ServiceStub.TEventPriority();
        tEventPriority.setTEventPriority(priority);
        tObjectEvent.setPriority(tEventPriority);

        ESDS_ServiceStub.TObjectID tObjectID = new ESDS_ServiceStub.TObjectID();
        tObjectID.setTObjectID(new URI(objectId));
        tObjectEvent.setObjectID(tObjectID);

        ESDS_ServiceStub.TLifeCycleStepID tLifeCycleStepID = new ESDS_ServiceStub.TLifeCycleStepID();
        tLifeCycleStepID.setTLifeCycleStepID(new URI(bizStep));
        tObjectEvent.setLifeCycleStepID(tLifeCycleStepID);

        ESDS_ServiceStub.TEventClass tEventClass = new ESDS_ServiceStub.TEventClass();
        tEventClass.setTEventClass(new Token(eventClass));
        tObjectEvent.setEventClass(tEventClass);

        // extensions:
        if (extensions != null) {
            for (Map.Entry<String, String> idval : extensions.entrySet()) {
                ESDS_ServiceStub.TExtension tExtension = new ESDS_ServiceStub.TExtension();
                OMFactory factory = OMAbstractFactory.getOMFactory();
                OMElement elemExtension = factory.createOMElement(new QName("fr:unicaen:extension"));
                OMElement key = factory.createOMElement(new QName("fr:unicaen:key"));
                key.setText(idval.getKey());
                OMElement value = factory.createOMElement(new QName("fr:unicaen:value"));
                value.setText(idval.getValue());
                elemExtension.addChild(key);
                elemExtension.addChild(value);
                tExtension.addExtraElement(elemExtension);
            }
        }
        tEventTypeChoice.setObjectEvent(tObjectEvent);
        return tEventTypeChoice;
    }

    private Map<String, String> parseExtentions(ESDS_ServiceStub.TExtension tExtension) {
        if (tExtension == null) {
            return null;
        }
        Map<String, String> result = new HashMap<String, String>();
        for (OMElement elem : tExtension.getExtraElement()) {
            Iterator<OMElement> it = elem.getChildElements();
            String k = null;
            String v = null;
            while (it.hasNext()) {
                OMElement elem2 = it.next();
                if (elem2.getQName().toString().equals("fr:unicaen:key")) {
                    k = elem2.getText();
                }
                if (elem2.getQName().toString().equals("fr:unicaen:value")) {
                    v = elem2.getText();
                }
            }
            if (k == null || v == null) {
                continue;
            }
            result.put(k, v);
        }
        return result;
    }

    private TEventTypeChoice createVoidEventTypeChoice(int eventId, String ttl, int priority) {
        TEventTypeChoice tEventTypeChoice = new TEventTypeChoice();
        ESDS_ServiceStub.TVoidEvent tVoidEvent = new ESDS_ServiceStub.TVoidEvent();

        ESDS_ServiceStub.TEventID tEventID = new ESDS_ServiceStub.TEventID();
        tEventID.setTEventID(new PositiveInteger(eventId + ""));
        tVoidEvent.setEventID(tEventID);

        ESDS_ServiceStub.TEventTTL tEventTTL = new ESDS_ServiceStub.TEventTTL();
        tEventTTL.setTEventTTL(new PositiveInteger(ttl));
        tVoidEvent.setTtl(tEventTTL);

        ESDS_ServiceStub.TEventPriority tEventPriority = new ESDS_ServiceStub.TEventPriority();
        tEventPriority.setTEventPriority(priority);
        tVoidEvent.setPriority(tEventPriority);

        tEventTypeChoice.setVoidEvent(tVoidEvent);
        return tEventTypeChoice;
    }

    private List<String> createServiceIds(Collection<Service> serviceList) {
        List<String> serviceIds = new ArrayList<String>();
        for (Service s : serviceList) {
            serviceIds.add(s.getId());
        }
        return serviceIds;
    }
}
