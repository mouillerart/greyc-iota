/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dseta.server;

import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.Event;
import fr.unicaen.iota.discovery.client.model.EventInfo;
import fr.unicaen.iota.discovery.client.model.Service;
import fr.unicaen.iota.discovery.client.model.UserInfo;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.ds.model.*;
import fr.unicaen.iota.dseta.soap.IDedDSServicePortType;
import fr.unicaen.iota.tau.model.Identity;
import java.math.BigInteger;
import java.rmi.RemoteException;
import java.util.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DSeTaWebService implements IDedDSServicePortType {

    private static final Log log = LogFactory.getLog(DSeTaWebService.class);
    private final DsClient dsClient;
    private static DatatypeFactory DF;
    protected final Identity anonymous;

    static {
        try {
            DF = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException ex) {
            DF = null;
            log.fatal(null, ex);
        }
    }

    public DSeTaWebService() {
        anonymous = new Identity();
        anonymous.setAsString(Constants.XACML_DEFAULT_USER);
        dsClient = new DsClient(Constants.WINGS_URL);
    }

    private TEventType createEventType(String type) {
        return TEventType.valueOf(type.toUpperCase());
    }

    private TServiceType createServiceType(String type) {
        return TServiceType.valueOf(type.toUpperCase());
    }

    @Override
    public EventLookupOut iDedEventLookup(EventLookupIn parms, Identity id) {
        try {
            String sessionId = dsClient.userLogin(Constants.DEFAULT_SESSION, Constants.WINGS_LOGIN, Constants.WINGS_PASSWORD).getSessionId();
            EventLookupOut res = new EventLookupOut();
            // tres not used but required
            TResult tres = new TResult();
            tres.setCode(1000);
            tres.setDesc("command successfull");
            tres.setValue("10000");
            res.setResult(tres);
            TEventItemList evtList = new TEventItemList();
            res.setEventList(evtList);
            List<TEventItem> events = evtList.getEvent();
            GregorianCalendar start = parms.getStartingAt() == null ? null : parms.getStartingAt().toGregorianCalendar();
            GregorianCalendar end = parms.getEndingAt() == null ? null : parms.getEndingAt().toGregorianCalendar();
            List<Event> clientEvents = dsClient.eventLookup(sessionId, parms.getObjectID(), start, end, parms.getLifeCycleStepID());
            for (Event evt : clientEvents) {
                TEventItem tevt = new TEventItem();
                events.add(tevt);
                tevt.setC(evt.getEventClass());
                tevt.setE(BigInteger.valueOf(evt.getEventId()));
                GregorianCalendar gCal = new GregorianCalendar();
                gCal.setTime(evt.getEventTimeStamp().getTime());
                XMLGregorianCalendar xmlCal = DF.newXMLGregorianCalendar(gCal);
                tevt.setEts(xmlCal);
                gCal.setTime(evt.getSourceTimeStamp().getTime());
                xmlCal = DF.newXMLGregorianCalendar(gCal);
                tevt.setSts(xmlCal);
                tevt.setLcs(evt.getBizStep());
                tevt.setO(evt.getObjectId());
                tevt.setP(evt.getPartnerId());
                tevt.setT(createEventType(evt.getEventType()));
                tevt.setSc("not_used");
                tevt.setU(evt.getUserId());
                TServiceItemList srvs = new TServiceItemList();
                tevt.setServiceList(srvs);
                List<TServiceItem> srvlst = srvs.getService();
                for (Service srv : evt.getServiceList()) {
                    TServiceItem tsrv = new TServiceItem();
                    srvlst.add(tsrv);
                    tsrv.setId(srv.getId());
                    tsrv.setType(createServiceType(srv.getType()));
                    tsrv.setUri(srv.getUri().toString());
                }
            }
            dsClient.userLogout(sessionId);
            return res;
        } catch (MalformedURIException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        } catch (EnhancedProtocolException ex) {
            log.fatal(null, ex);
        }
        return null;
    }

    @Override
    public MultipleEventCreateOut iDedMultipleEventCreate(MultipleEventCreateIn parms, Identity id) {
        try {
            String sessionId = dsClient.userLogin(Constants.DEFAULT_SESSION, Constants.WINGS_LOGIN, Constants.WINGS_PASSWORD).getSessionId();

            UserInfo uInfo = dsClient.userInfo(sessionId, Constants.WINGS_LOGIN);
            String userId = uInfo.getUserId();
            String partnerId = uInfo.getPartnerId();

            List<EventInfo> eventList = new LinkedList<EventInfo>();
            for (TObjectEvent oe : parms.getEvents().getObjectEvent()) {
                Calendar now = Calendar.getInstance();
                now.setTime(new Date());
                String etype = TEventType.OBJECT.toString();
                Event event = new Event(0, oe.getObjectID(), partnerId, userId, oe.getLifeCycleStepID(),
                        etype, oe.getEventClass(), oe.getSourceTS().toGregorianCalendar(), now, new HashMap<String, String>());
                eventList.add(new EventInfo(event, oe.getPriority(), oe.getTtl().intValue()));
            }
            List<Integer> idsList = dsClient.multipleEventCreate(sessionId, partnerId, eventList);

            MultipleEventCreateOut res = new MultipleEventCreateOut();
            TEventIDList teidList = new TEventIDList();
            // tres not used but required
            TResult tres = new TResult();
            tres.setCode(1000);
            tres.setDesc("command successfull");
            tres.setValue("10000");
            res.setResult(tres);
            res.setEventIDList(teidList);
            List<BigInteger> resIds = teidList.getEventID();
            for (int eid : idsList) {
                resIds.add(BigInteger.valueOf(eid));
            }

            dsClient.userLogout(sessionId);
            return res;
        } catch (MalformedURIException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        } catch (EnhancedProtocolException ex) {
            log.fatal(null, ex);
        }
        return null;
    }

    @Override
    public EventCreateOut iDedEventCreate(EventCreateIn parms, Identity id) {
        try {
            String sessionId = dsClient.userLogin(Constants.DEFAULT_SESSION, Constants.WINGS_LOGIN, Constants.WINGS_PASSWORD).getSessionId();
            UserInfo uInfo = dsClient.userInfo(sessionId, Constants.WINGS_LOGIN);
            String partnerId = uInfo.getPartnerId();

            Calendar now = Calendar.getInstance();
            now.setTime(new Date());
            TObjectEvent oe = parms.getEvent().getObjectEvent();
            int eid = dsClient.eventCreate(sessionId, partnerId, oe.getObjectID(), oe.getLifeCycleStepID(), oe.getEventClass(),
                    oe.getSourceTS().toGregorianCalendar(), oe.getTtl().intValue(), null, oe.getPriority(), new HashMap<String, String>());

            EventCreateOut res = new EventCreateOut();
            // tres not used but required
            TResult tres = new TResult();
            tres.setCode(1000);
            tres.setDesc("command successfull");
            tres.setValue("10000");
            res.setResult(tres);
            res.setEventID(BigInteger.valueOf(eid));
            dsClient.userLogout(sessionId);
            return res;
        } catch (MalformedURIException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        } catch (EnhancedProtocolException ex) {
            log.fatal(null, ex);
        }
        return null;
    }
}
