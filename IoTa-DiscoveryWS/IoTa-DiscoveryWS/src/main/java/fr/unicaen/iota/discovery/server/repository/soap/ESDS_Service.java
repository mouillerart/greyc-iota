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
package fr.unicaen.iota.discovery.server.repository.soap;

import fr.unicaen.iota.discovery.server.hibernate.Event;
import fr.unicaen.iota.discovery.server.hibernate.Partner;
import fr.unicaen.iota.discovery.server.hibernate.User;
import fr.unicaen.iota.discovery.server.querycontrol.DSControler;
import fr.unicaen.iota.discovery.server.repository.soap.model.*;
import fr.unicaen.iota.discovery.server.util.Constants;
import fr.unicaen.iota.discovery.server.util.ProtocolException;
import fr.unicaen.iota.discovery.server.util.Session;
import fr.unicaen.iota.discovery.server.util.Util;
import java.util.Calendar;
import java.util.List;
import org.apache.axis2.databinding.types.PositiveInteger;
import org.apache.axis2.databinding.types.Token;
import org.apache.axis2.databinding.types.URI;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/*
 * Based on ESDS_ServiceSkeleton java skeleton for the axisService
 */
public class ESDS_Service implements ESDS_ServiceSkeletonInterface {

    private static final Log log = LogFactory.getLog(ESDS_Service.class);
    private DSControler dsControler = new DSControler();

    /**
     * Auto generated method signature
     *
     *
     * @param hello
     */
    @Override
    public HelloResult hello(Hello hello) {
        log.trace("Hello method called");
        HelloResult helloResult = new HelloResult();
        HelloOut helloOut = new HelloOut();
        helloOut.setResult(createCommandSuccessfull());
        helloOut.setServerIdentity(new Token(dsControler.hello()));
        helloOut.setServerTS(Calendar.getInstance());
        helloResult.setHelloResult(helloOut);
        return helloResult;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param userLookup
     */
    @Override
    public UserLookupResult userLookup(UserLookup userLookup) {
        log.trace("userLookup method called");
        UserLookupResult result = new UserLookupResult();
        UserLookupOut out = new UserLookupOut();
        try {
            String sessionID = userLookup.getUserLookup().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            String userID = userLookup.getUserLookup().getUserID().getTUserID().toString();
            TUserItemList tUserItemList = new TUserItemList();
            List<User> users = dsControler.userLookup(sessionID, userID);
            for (User user : users) {
                TUserItem tUserItem = new TUserItem();
                TUserID tUserID = new TUserID();
                tUserID.setTUserID(new Token(user.getUserID()));
                tUserItem.setId(tUserID);
                TSmallUID uid = new TSmallUID();
                uid.setTSmallUID(user.getId());
                tUserItem.setUid(uid);
                tUserItemList.addUser(tUserItem);
            }
            out.setUserList(tUserItemList);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            out.setUserList(new TUserItemList());
        }
        result.setUserLookupResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param userCreate
     */
    @Override
    public UserCreateResult userCreate(UserCreate userCreate) {
        log.trace("userCreate method called");
        UserCreateResult result = new UserCreateResult();
        UserCreateOut out = new UserCreateOut();
        try {
            String sessionID = userCreate.getUserCreate().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            int uid = dsControler.userCreate(sessionID, userCreate.getUserCreate().getPartnerID().getTPartnerID().toString(),
                    userCreate.getUserCreate().getPassword().getTPassword().toString(),
                    userCreate.getUserCreate().getUserID().getTUserID().toString());
            TSmallUID tSmallUID = new TSmallUID();
            tSmallUID.setTSmallUID(uid);
            out.setUserUID(tSmallUID);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            result.setUserCreateResult(out);
            TSmallUID userUid = new TSmallUID();
            userUid.setTSmallUID(-1);
            out.setUserUID(userUid);
        }
        result.setUserCreateResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param userInfo
     */
    @Override
    public UserInfoResult userInfo(UserInfo userInfo) {
        log.trace("userInfo method called");
        UserInfoResult result = new UserInfoResult();
        UserInfoOut out = new UserInfoOut();
        try {
            String sessionID = userInfo.getUserInfo().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            User user = dsControler.userInfo(sessionID, userInfo.getUserInfo().getUserID().getTUserID().toString());
            TPartnerID tPartnerID = new TPartnerID();
            tPartnerID.setTPartnerID(new Token(user.getPartner().getPartnerID()));
            TSmallUID tSmallUID = new TSmallUID();
            tSmallUID.setTSmallUID(user.getId());
            TLeaseSeconds tLeaseSeconds = new TLeaseSeconds();
            tLeaseSeconds.setTLeaseSeconds(Constants.SESSION_TIME_LEASE);
            TRoleID tRoleID = new TRoleID();
            tRoleID.setTRoleID(new Token("not_used"));          // HACK
            out.setRoleID(tRoleID);
            out.setSessionLease(tLeaseSeconds);
            out.setLoginMode(TLoginMode.value1);
            out.setPartnerID(tPartnerID);
            out.setUserID(userInfo.getUserInfo().getUserID());
            out.setUserUID(tSmallUID);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            TPartnerID tPartnerID = new TPartnerID();
            tPartnerID.setTPartnerID(new Token("NULL"));
            TSmallUID tSmallUID = new TSmallUID();
            tSmallUID.setTSmallUID(1);
            TLeaseSeconds tLeaseSeconds = new TLeaseSeconds();
            tLeaseSeconds.setTLeaseSeconds(Constants.SESSION_TIME_LEASE);
            TRoleID tRoleID = new TRoleID();
            tRoleID.setTRoleID(new Token("not_used"));          // HACK
            out.setRoleID(tRoleID);
            out.setSessionLease(tLeaseSeconds);
            out.setLoginMode(TLoginMode.value1);
            out.setPartnerID(tPartnerID);
            out.setUserID(userInfo.getUserInfo().getUserID());
            out.setUserUID(tSmallUID);
        }
        result.setUserInfoResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param userUpdate
     */
    @Override
    public UserUpdateResult userUpdate(UserUpdate userUpdate) {
        log.trace("userUpdate method called");
        UserUpdateResult result = new UserUpdateResult();
        UserUpdateOut out = new UserUpdateOut();
        try {
            String sessionID = userUpdate.getUserUpdate().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            dsControler.userUpdate(sessionID,
                    userUpdate.getUserUpdate().getPartnerID().getTPartnerID().toString(),
                    userUpdate.getUserUpdate().getUserUID().getTSmallUID(),
                    userUpdate.getUserUpdate().getUserID().getTUserID().toString(),
                    userUpdate.getUserUpdate().getPassword().getTPassword().toString());
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
        }
        result.setUserUpdateResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param userDelete
     */
    @Override
    public UserDeleteResult userDelete(UserDelete userDelete) {
        log.trace("userDelete method called");
        UserDeleteResult result = new UserDeleteResult();
        UserDeleteOut out = new UserDeleteOut();
        try {
            String sessionID = userDelete.getUserDelete().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            dsControler.userDelete(sessionID, userDelete.getUserDelete().getUserID().getTUserID().toString());
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
        }
        result.setUserDeleteResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param userLogin
     */
    @Override
    public UserLoginResult userLogin(UserLogin userLogin) {
        log.trace("userLogin method called");
        UserLoginResult result = new UserLoginResult();
        UserLoginOut out = new UserLoginOut();
        try {
            String sessionId = dsControler.userLogin(userLogin.getUserLogin().getUserID().toString(), userLogin.getUserLogin().getPassword().toString());
            if (sessionId == null) {
                throw new ProtocolException(2200, "Authentication failed!");
            }
            TLeaseSeconds tLeaseSeconds = new TLeaseSeconds();
            tLeaseSeconds.setTLeaseSeconds(Constants.SESSION_TIME_LEASE);
            out.setSessionLease(tLeaseSeconds);
            TSessionID tSessionID = new TSessionID();
            tSessionID.setTSessionID(new Token(sessionId));
            out.setSid(tSessionID);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            result.setUserLoginResult(out);
            TLeaseSeconds tLeaseSeconds = new TLeaseSeconds();
            tLeaseSeconds.setTLeaseSeconds(0);
            out.setSessionLease(tLeaseSeconds);
            TSessionID tSessionID = new TSessionID();
            tSessionID.setTSessionID(new Token(Constants.SESSION_FAILED_ID));
            out.setSid(tSessionID);
            return result;
        }
        result.setUserLoginResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param userLogout
     */
    @Override
    public UserLogoutResult userLogout(UserLogout userLogout) {
        log.trace("userLogout method called");
        UserLogoutResult result = new UserLogoutResult();
        UserLogoutOut out = new UserLogoutOut();
        try {
            String sessionID = userLogout.getUserLogout().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            dsControler.userLogout(sessionID);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
        }
        result.setUserLogoutResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param partnerLookup
     */
    @Override
    public PartnerLookupResult partnerLookup(PartnerLookup partnerLookup) {
        log.trace("partnerLookup method called");
        PartnerLookupResult result = new PartnerLookupResult();
        PartnerLookupOut out = new PartnerLookupOut();
        try {
            String sessionID = partnerLookup.getPartnerLookup().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            List<Partner> partnerList = dsControler.partnerLookup(sessionID, partnerLookup.getPartnerLookup().getPartnerID().getTPartnerID().toString());
            TPartnerItemList tPartnerItemList = new TPartnerItemList();
            for (Partner partner : partnerList) {
                TPartnerItem tPartnerItem = new TPartnerItem();
                tPartnerItem.setAuthority(true);
                TPartnerID tPartnerID = new TPartnerID();
                tPartnerID.setTPartnerID(new Token(partner.getPartnerID()));
                tPartnerItem.setId(tPartnerID);
                TSmallUID tSmallUID = new TSmallUID();
                tSmallUID.setTSmallUID(partner.getId());
                tPartnerItem.setUid(tSmallUID);
                tPartnerItemList.addPartner(tPartnerItem);

            }
            out.setPartnerList(tPartnerItemList);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            TPartnerItemList tPartnerItemList = new TPartnerItemList();
            out.setPartnerList(tPartnerItemList);
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
        }
        result.setPartnerLookupResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param partnerCreate
     */
    @Override
    public PartnerCreateResult partnerCreate(PartnerCreate partnerCreate) {
        log.trace("partnerCreate method called");
        PartnerCreateResult result = new PartnerCreateResult();
        PartnerCreateOut out = new PartnerCreateOut();
        try {
            String sessionID = partnerCreate.getPartnerCreate().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            PartnerCreateIn partnerCreateIn = partnerCreate.getPartnerCreate();
            TServiceItem tServiceItem = partnerCreateIn.getServiceList().getService()[0];                    // HACK
            int uid = dsControler.partnerCreate(sessionID, partnerCreateIn.getPartnerID().getTPartnerID().toString(),
                    tServiceItem.getType().getValue().toString(), tServiceItem.getUri().getTServiceURI().toString());
            TSmallUID tSmallUID = new TSmallUID();
            tSmallUID.setTSmallUID(uid);
            out.setPartnerUID(tSmallUID);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            TSmallUID uid = new TSmallUID();
            uid.setTSmallUID(0);
            out.setPartnerUID(uid);
        }
        result.setPartnerCreateResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param partnerInfo
     */
    @Override
    public PartnerInfoResult partnerInfo(PartnerInfo partnerInfo) {
        log.trace("partnerInfo method called");
        PartnerInfoResult result = new PartnerInfoResult();
        PartnerInfoOut out = new PartnerInfoOut();
        try {
            String sessionID = partnerInfo.getPartnerInfo().getSid().getTSessionID().toString();
            out.setPartnerID(partnerInfo.getPartnerInfo().getPartnerID());
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            Partner partner = dsControler.partnerInfo(sessionID, partnerInfo.getPartnerInfo().getPartnerID().getTPartnerID().toString());
            TServiceItem tServiceItem = new TServiceItem();
            TServiceID tServiceID = new TServiceID();
            tServiceID.setTServiceID(new Token(partner.getPartnerID()));
            TServiceType tServiceType = convertServiceType(partner.getServiceType());
            TServiceURI tServiceURI = new TServiceURI();
            try {
                tServiceURI.setTServiceURI(new URI(partner.getServiceAddress()));
            } catch (MalformedURIException e) {
                log.error(null, e);
            }
            tServiceItem.setId(tServiceID);
            tServiceItem.setType(tServiceType);
            tServiceItem.setUri(tServiceURI);
            TSmallUID tSmallUID = new TSmallUID();
            tSmallUID.setTSmallUID(partner.getId());
            TServiceItemList tServiceItemList = new TServiceItemList();
            tServiceItemList.addService(tServiceItem);
            out.setPartnerUID(tSmallUID);
            out.setServiceList(tServiceItemList);
            out.setSupplyChainList(new TSupplyChainItemList());
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            out.setPartnerUID(new TSmallUID());
            out.setServiceList(new TServiceItemList());
            out.setSupplyChainList(new TSupplyChainItemList());
        }
        result.setPartnerInfoResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param partnerUpdate
     */
    @Override
    public PartnerUpdateResult partnerUpdate(PartnerUpdate partnerUpdate) {
        log.trace("partnerUpdate method called");
        PartnerUpdateResult result = new PartnerUpdateResult();
        PartnerUpdateOut out = new PartnerUpdateOut();
        try {
            String sessionID = partnerUpdate.getPartnerUpdate().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            PartnerUpdateIn in = partnerUpdate.getPartnerUpdate();
            TServiceItem tServiceItem = in.getServiceList().getService()[0];                    // HACK
            dsControler.partnerUpdate(sessionID, in.getPartnerUID().getTSmallUID(),
                    in.getPartnerID().getTPartnerID().toString(),
                    tServiceItem.getType().getValue().toString(),
                    tServiceItem.getUri().getTServiceURI().toString());
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
        }
        result.setPartnerUpdateResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param partnerDelete
     */
    @Override
    public PartnerDeleteResult partnerDelete(PartnerDelete partnerDelete) {
        log.trace("partnerDelete method called");
        PartnerDeleteResult result = new PartnerDeleteResult();
        PartnerDeleteOut out = new PartnerDeleteOut();
        try {
            String sessionID = partnerDelete.getPartnerDelete().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            dsControler.partnerDelete(sessionID, partnerDelete.getPartnerDelete().getPartnerID().getTPartnerID().toString());
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
        }
        result.setPartnerDeleteResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param eventLookup
     * @throws MalformedURIException
     */
    @Override
    public EventLookupResult eventLookup(EventLookup eventLookup) {
        log.trace("eventLookup method called");
        EventLookupResult result = new EventLookupResult();
        EventLookupOut out = new EventLookupOut();
        try {
            String sessionID = eventLookup.getEventLookup().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            TEventItemList tEventItemList = new TEventItemList();
            String epc = eventLookup.getEventLookup().getObjectID().getTObjectID().toString();
            List<Event> eventList = dsControler.eventLookup(sessionID, epc);
            for (fr.unicaen.iota.discovery.server.hibernate.Event event : eventList) {
                TEventItem tEventItem = new TEventItem();
                TEventClass tEventClass = new TEventClass();
                tEventClass.setTEventClass(new Token(event.getEPCClass()));
                TEventID tEventID = new TEventID();
                tEventID.setTEventID(new PositiveInteger(event.getId().toString()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(event.getEventTimeStamp());
                TLifeCycleStepID tLifeCycleStepID = new TLifeCycleStepID();
                tLifeCycleStepID.setTLifeCycleStepID(new URI(event.getBizStep().equals("") ? "default" : event.getBizStep()));
                TObjectID tObjectID = new TObjectID();
                tObjectID.setTObjectID(new URI(event.getEpc()));
                TPartnerID tPartnerID = new TPartnerID();
                tPartnerID.setTPartnerID(new Token(event.getPartner().getPartnerID()));
                TEventType tEventType;
                if (event.getEventType().equals(TEventType.value1.toString())) {
                    tEventType = TEventType.value1;
                } else {
                    tEventType = TEventType.value2;
                }
                TUserID tUserID = new TUserID();
                tUserID.setTUserID(new Token(Session.getUser(sessionID).getUserID()));
                TSupplyChainID tSupplyChainID = new TSupplyChainID();
                tSupplyChainID.setTSupplyChainID(new Token("sc1")); // TODO :
                tEventItem.setC(tEventClass);
                tEventItem.setE(tEventID);
                tEventItem.setEts(calendar);
                tEventItem.setLcs(tLifeCycleStepID);
                tEventItem.setO(tObjectID);
                tEventItem.setP(tPartnerID);
                tEventItem.setSc(tSupplyChainID);
                TServiceItem tServiceItem = new TServiceItem();
                TServiceID tServiceID = new TServiceID();
                tServiceID.setTServiceID(new Token(event.getPartner().getPartnerID()));
                TServiceType tServiceType = convertServiceType(event.getPartner().getServiceType());
                TServiceURI tServiceURI = new TServiceURI();
                tServiceURI.setTServiceURI(new URI(event.getPartner().getServiceAddress()));
                tServiceItem.setId(tServiceID);
                tServiceItem.setType(tServiceType);
                tServiceItem.setUri(tServiceURI);
                TServiceItemList tServiceItemList = new TServiceItemList();
                tServiceItemList.addService(tServiceItem);
                tEventItem.setServiceList(tServiceItemList);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(event.getSourceTimeStamp());
                tEventItem.setSts(calendar2);
                tEventItem.setT(tEventType);
                tEventItem.setU(tUserID);
                tEventItemList.addEvent(tEventItem);
            }
            out.setEventList(tEventItemList);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            TEventItemList tEventItemList = new TEventItemList();
            out.setEventList(tEventItemList);
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
        } catch (MalformedURIException e) {
            log.error(null, e);
        }
        result.setEventLookupResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param eventCreate
     */
    @Override
    public EventCreateResult eventCreate(EventCreate eventCreate) {
        log.trace("[ " + eventCreate.getEventCreate().getEvent().getObjectEvent().getObjectID() + " ] " + "eventCreate method called");
        EventCreateResult result = new EventCreateResult();
        EventCreateOut out = new EventCreateOut();
        try {
            String sessionID = eventCreate.getEventCreate().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            if (eventCreate.getEventCreate().getEvent().getVoidEvent() != null) {
                return voidEvent(sessionID, eventCreate);
            }
            TPartnerID pID = eventCreate.getEventCreate().getProxyPartnerID();
            Calendar sTS = eventCreate.getEventCreate().getEvent().getObjectEvent().getSourceTS();
            TObjectID epc = eventCreate.getEventCreate().getEvent().getObjectEvent().getObjectID();
            TEventClass eC = eventCreate.getEventCreate().getEvent().getObjectEvent().getEventClass();
            String eLcs = eventCreate.getEventCreate().getEvent().getObjectEvent().getLifeCycleStepID().getTLifeCycleStepID().toString();
            TEventType eT = TEventType.value2;
            Calendar eTS = Calendar.getInstance();
            int uid = dsControler.eventCreate(sessionID, pID.getTPartnerID().toString(), epc.getTObjectID().toString(), eC.getTEventClass().toString(), Util.convert(sTS), Util.convert(eTS), eT.getValue().toString(), eLcs);
            TEventID tEventID = new TEventID();
            tEventID.setTEventID(new PositiveInteger(uid + ""));
            out.setEventID(tEventID);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            TEventID tEventID = new TEventID();
            tEventID.setTEventID(new PositiveInteger("1"));
            out.setEventID(tEventID);
            result.setEventCreateResult(out);
            return result;
        }
        result.setEventCreateResult(out);
        return result;
    }

    private EventCreateResult voidEvent(String sessionId, EventCreate eventCreate) throws ProtocolException {
        log.trace("eventCreate method called");
        EventCreateResult result = new EventCreateResult();
        EventCreateOut out = new EventCreateOut();
        out.setResult(createCommandSuccessfull());
        result.setEventCreateResult(out);
        double eventId = eventCreate.getEventCreate().getEvent().getVoidEvent().getEventID().getTEventID().doubleValue();
        int id = dsControler.voidEvent(sessionId, eventId);
        TEventID tEventID = new TEventID();
        tEventID.setTEventID((PositiveInteger) (PositiveInteger.valueOf(id)));
        out.setEventID(tEventID);
        return result;
    }

    /**
     * Auto generated method signature
     *
     *
     * @param eventInfo
     */
    @Override
    public EventInfoResult eventInfo(EventInfo eventInfo) {
        log.trace("eventInfo method called");
        EventInfoResult result = new EventInfoResult();
        EventInfoOut out = new EventInfoOut();
        try {
            try {
                String sessionID = eventInfo.getEventInfo().getSid().getTSessionID().toString();
                if (!Session.isValidSession(sessionID)) {
                    throw new ProtocolException(2002, "not a valid session");
                }
                Event event = dsControler.eventInfo(sessionID, Integer.parseInt(eventInfo.getEventInfo().getEventID().getTEventID().toString()));
                if (event == null) {
                    throw new ProtocolException(2002, "event not found");
                }
                TInfoEvent tInfoEvent = new TInfoEvent();
                TEventClass tEventClass = new TEventClass();
                tEventClass.setTEventClass(new Token(event.getEPCClass()));
                TEventID tEventID = new TEventID();
                tEventID.setTEventID(new PositiveInteger(event.getId().toString()));
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(event.getEventTimeStamp());
                TLifeCycleStepID tLifeCycleStepID = new TLifeCycleStepID();
                tLifeCycleStepID.setTLifeCycleStepID(new URI(event.getBizStep()));
                TObjectID tObjectID = new TObjectID();
                tObjectID.setTObjectID(new URI(event.getEpc()));
                TPartnerID tPartnerID = new TPartnerID();
                tPartnerID.setTPartnerID(new Token(event.getPartner().getPartnerID()));
                TEventType tEventType;
                if (event.getEventType().equals(TEventType.value1.toString())) {
                    tEventType = TEventType.value1;
                } else {
                    tEventType = TEventType.value2;
                }
                TUserID tUserID = new TUserID();
                tUserID.setTUserID(new Token(Session.getUser(sessionID).getUserID()));
                TSupplyChainID tSupplyChainID = new TSupplyChainID();
                tSupplyChainID.setTSupplyChainID(new Token("not_defined"));     // HACK
                tInfoEvent.setEventClass(tEventClass);
                tInfoEvent.setEventID(tEventID);
                tInfoEvent.setEventTS(calendar);
                tInfoEvent.setLifeCycleStepID(tLifeCycleStepID);
                tInfoEvent.setObjectID(tObjectID);
                tInfoEvent.setPartnerID(tPartnerID);
                tInfoEvent.setSupplyChainID(tSupplyChainID);
                tInfoEvent.setServiceList(new TServiceItemList());
                TEventPriority tEventPriority = new TEventPriority();
                tEventPriority.setTEventPriority(0);                     // HACK
                tInfoEvent.setPriority(tEventPriority);
                TEventTTL ttl = new TEventTTL();
                ttl.setTEventTTL(new PositiveInteger(1 + ""));           // HACK
                tInfoEvent.setTtl(ttl);
                Calendar calendar2 = Calendar.getInstance();
                calendar2.setTime(event.getSourceTimeStamp());
                tInfoEvent.setSourceTS(calendar2);
                tInfoEvent.setEventType(tEventType);
                tInfoEvent.setUserID(tUserID);
                out.setEvent(tInfoEvent);
                out.setResult(createCommandSuccessfull());
            } catch (ProtocolException pe) {
                TInfoEvent tInfoEvent = new TInfoEvent();
                TEventClass tEventClass = new TEventClass();
                tEventClass.setTEventClass(new Token("NULL"));
                TEventID tEventID = new TEventID();
                tEventID.setTEventID(new PositiveInteger(1 + ""));
                Calendar calendar = Calendar.getInstance();
                TLifeCycleStepID tLifeCycleStepID = new TLifeCycleStepID();
                tLifeCycleStepID.setTLifeCycleStepID(new URI("NULL"));
                TObjectID tObjectID = new TObjectID();
                tObjectID.setTObjectID(new URI("NULL"));
                TPartnerID tPartnerID = new TPartnerID();
                tPartnerID.setTPartnerID(new Token("NULL"));
                TEventType tEventType;
                tEventType = TEventType.value1;
                TUserID tUserID = new TUserID();
                tUserID.setTUserID(new Token("NULL"));
                TSupplyChainID tSupplyChainID = new TSupplyChainID();
                tSupplyChainID.setTSupplyChainID(new Token("not_used")); // TODO :
                tInfoEvent.setEventClass(tEventClass);
                tInfoEvent.setEventID(tEventID);
                tInfoEvent.setEventTS(calendar);
                tInfoEvent.setLifeCycleStepID(tLifeCycleStepID);
                tInfoEvent.setObjectID(tObjectID);
                tInfoEvent.setPartnerID(tPartnerID);
                tInfoEvent.setSupplyChainID(tSupplyChainID);
                tInfoEvent.setServiceList(new TServiceItemList());
                Calendar calendar2 = Calendar.getInstance();
                tInfoEvent.setSourceTS(calendar2);
                tInfoEvent.setEventType(tEventType);
                tInfoEvent.setUserID(tUserID);
                out.setEvent(tInfoEvent);
                out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            }
        } catch (MalformedURIException e) {
            log.error(null, e);
        }
        result.setEventInfoResult(out);
        return result;
    }

    /**
     * Auto generated method signature
     *
     * @param multipleEventCreate
     */
    @Override
    public MultipleEventCreateResult multipleEventCreate(MultipleEventCreate multipleEventCreate) {
        log.trace("MultipleEventCreate method called");
        MultipleEventCreateResult result = new MultipleEventCreateResult();
        MultipleEventCreateOut out = new MultipleEventCreateOut();
        try {
            String sessionID = multipleEventCreate.getMultipleEventCreate().getSid().getTSessionID().toString();
            if (!Session.isValidSession(sessionID)) {
                throw new ProtocolException(2002, "not a valid session");
            }
            TEventIDList tEventIDList = new TEventIDList();
            for (TObjectEvent tObjectEvent : multipleEventCreate.getMultipleEventCreate().getEvents().getObjectEvent()) {
                Calendar sTS = tObjectEvent.getSourceTS();
                TObjectID epc = tObjectEvent.getObjectID();
                TEventClass eC = tObjectEvent.getEventClass();
                TEventType eT = TEventType.value2;
                TLifeCycleStepID tLifeCycleStepID = tObjectEvent.getLifeCycleStepID();
                Calendar eTS = Calendar.getInstance();
                Integer uid;
                try {
                    uid = dsControler.eventCreate(sessionID, multipleEventCreate.getMultipleEventCreate().getProxyPartnerID().getTPartnerID().toString(),
                            epc.getTObjectID().toString(), eC.getTEventClass().toString(), Util.convert(sTS), Util.convert(eTS),
                            eT.getValue().toString(), tLifeCycleStepID.getTLifeCycleStepID().toString());
                } catch (ProtocolException pe) {
                    continue;
                }
                TEventID tEventID = new TEventID();
                tEventID.setTEventID(new PositiveInteger(uid + ""));
                tEventIDList.addEventID(tEventID);
            }
            out.setEventIDList(tEventIDList);
            out.setResult(createCommandSuccessfull());
        } catch (ProtocolException pe) {
            out.setResult(createResultFailed(pe.getMessage(), pe.getResultCode()));
            TEventIDList tEventIDList = new TEventIDList();
            TEventID tEventID = new TEventID();
            tEventID.setTEventID(new PositiveInteger("1"));
            tEventIDList.addEventID(tEventID);
            out.setEventIDList(tEventIDList);
        }
        result.setMultipleEventCreateResult(out);
        return result;
    }

    private TResult createResultFailed(String message, int resultCode) {
        TResult tResult = new TResult();
        tResult.setCode(TResultCode.value6);
        tResult.setDesc(message);
        tResult.setValue(resultCode + "");
        return tResult;
    }

    private TResult createCommandSuccessfull() {
        TResult tResult = new TResult();
        tResult.setCode(TResultCode.value1);
        tResult.setDesc("command successfull");
        tResult.setValue("1000");
        return tResult;
    }

    private TServiceType convertServiceType(String type) {
        if ("ds".equalsIgnoreCase(type)) {
            return TServiceType.ds;
        } else if ("epcis".equalsIgnoreCase(type)) {
            return TServiceType.epcis;
        } else if ("ws".equalsIgnoreCase(type)) {
            return TServiceType.ws;
        } else if ("html".equalsIgnoreCase(type)) {
            return TServiceType.html;
        } else if ("xmlrpc".equalsIgnoreCase(type)) {
            return TServiceType.xmlrpc;
        } else if ("ided_epcis".equalsIgnoreCase(type)) {
            return TServiceType.ided_epcis;
        } else if ("ided_ds".equalsIgnoreCase(type)) {
            return TServiceType.ided_ds;
        }
        return null;
    }

    // ######################################################################### //
    //                                                                           //
    //                                                                           //
    //                            NOT IMPLEMENTED                                //
    //                                                                           //
    //                                                                           //
    // ######################################################################### //
    /**
     * Auto generated method signature
     *
     *
     * @param supplyChainLookup
     */
    @Override
    public SupplyChainLookupResult supplyChainLookup(SupplyChainLookup supplyChainLookup) {
        log.trace("supplyChainLookup method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#supplyChainLookup");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param supplyChainCreate
     */
    @Override
    public SupplyChainCreateResult supplyChainCreate(SupplyChainCreate supplyChainCreate) {
        log.trace("supplayChainCreate method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#supplyChainCreate");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param supplyChainInfo
     */
    @Override
    public SupplyChainInfoResult supplyChainInfo(SupplyChainInfo supplyChainInfo) {
        log.trace("supplyChainInfo method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#supplyChainInfo");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param supplyChainUpdate
     */
    @Override
    public SupplyChainUpdateResult supplyChainUpdate(SupplyChainUpdate supplyChainUpdate) {
        log.trace("supplyChainUpdate method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#supplyChainUpdate");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param supplyChainDelete
     */
    @Override
    public SupplyChainDeleteResult supplyChainDelete(SupplyChainDelete supplyChainDelete) {
        log.trace("supplyChainDelete method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#supplyChainDelete");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param lookup
     */
    @Override
    public LookupResult lookup(Lookup lookup) {
        log.trace("lookup method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#lookup");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param roleLookup
     */
    @Override
    public RoleLookupResult roleLookup(RoleLookup roleLookup) {
        log.trace("roleLookup method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#roleLookup");

    }

    /**
     * Auto generated method signature
     *
     *
     * @param roleCreate
     */
    @Override
    public RoleCreateResult roleCreate(RoleCreate roleCreate) {
        log.trace("roleCreate method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#roleCreate");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param roleInfo
     */
    @Override
    public RoleInfoResult roleInfo(RoleInfo roleInfo) {
        log.trace("roleInfo method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#roleInfo");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param roleUpdate
     */
    @Override
    public RoleUpdateResult roleUpdate(RoleUpdate roleUpdate) {
        log.trace("roleUpdate method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#roleUpdate");
    }

    /**
     * Auto generated method signature
     *
     *
     * @param roleDelete
     */
    @Override
    public RoleDeleteResult roleDelete(RoleDelete roleDelete) {
        log.trace("roleDelete method called");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#roleDelete");
    }
}
