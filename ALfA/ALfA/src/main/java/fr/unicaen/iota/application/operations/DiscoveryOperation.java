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
package fr.unicaen.iota.application.operations;

import fr.unicaen.iota.application.conf.Constants;
import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.application.rmi.CallBackClient;
import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.Event;
import fr.unicaen.iota.discovery.client.model.Service;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import java.rmi.RemoteException;
import java.sql.Timestamp;
import java.util.*;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoveryOperation {

    private static final Log log = LogFactory.getLog(DiscoveryOperation.class);
    private final String LOGIN;
    private final String PASS;
    private String DS_SERVICE_ADDRESS;
    private final CallBackClient client;
    private final String sessionId;
    private final Set<String> visitedSet = new HashSet<String>();

    public DiscoveryOperation(String login, String pass, String ds_service_address) {
        super();
        this.LOGIN = login;
        this.PASS = pass;
        this.DS_SERVICE_ADDRESS = ds_service_address;
        this.client = null;
        this.sessionId = null;
    }

    public DiscoveryOperation(String login, String pass, String dsAddress, CallBackClient client, String sessionId) {
        super();
        this.LOGIN = login;
        this.PASS = pass;
        this.DS_SERVICE_ADDRESS = dsAddress;
        this.client = client;
        this.sessionId = sessionId;
    }
    private transient DsClient dSClient = null;
    private transient String dSSessionID = null;

    private void login() throws RemoteException {
        log.debug("[DS SESSION START]");
        log.debug(" -> " + DS_SERVICE_ADDRESS);
        dSClient = new DsClient(DS_SERVICE_ADDRESS);
        try {
            dSSessionID = dSClient.userLogin(Constants.DEFAULT_SESSION, LOGIN, PASS).getSessionId();
        } catch (RemoteException ex) {
            log.error("Unable to connect to the DS: login or password error!", ex);
            throw new RemoteException("Unable to connect to the DS: login or password error!");
        } catch (EnhancedProtocolException ex) {
            log.error("Unable to connect to the DS: login or password error!", ex);
            throw new RemoteException("Unable to connect to the DS: login or password error!");
        }
        log.debug(" -> " + dSSessionID);
        if (dSSessionID == null) {
            throw new RemoteException("Unable to connect to the DS: login or password error!");
        }
    }

    private void logout() throws RemoteException {
        try {
            dSClient.userLogout(dSSessionID);
            dSSessionID = null;
            dSClient = null;
        } catch (RemoteException ex) {
            log.error("Unable to logout", ex);
            throw new RemoteException("Unable to logout");
        } catch (EnhancedProtocolException ex) {
            log.error("Unable to logout", ex);
            throw new RemoteException("Unable to logout");
        }
        log.debug("[DS SESSION END]");
    }

    private List<Event> getEvents(String EPC) throws RemoteException {
        List<Event> dsClientEventList;
        try {
            dsClientEventList = dSClient.eventLookup(dSSessionID, EPC, null, null, null);
        } catch (MalformedURIException ex) {
            log.error("Unable to process eventLookup", ex);
            throw new RemoteException("Unable to process eventLookup");
        } catch (EnhancedProtocolException ex) {
            log.error("Unable to process eventLookup", ex);
            throw new RemoteException("Unable to process eventLookup");
        }
        log.debug(EPC + " -> dsEvents in repository: " + dsClientEventList.size());
        return dsClientEventList;
    }

    public Set<String> discover(String EPC) {
        Set<String> result = new HashSet<String>();
        Collection<Event> evtList;
        try {
            login();
            evtList = getEvents(EPC);
        } catch (RemoteException e) {
            log.error(null, e);
            return new HashSet<String>();
        }
        for (Event evt : evtList) {
            log.trace("Source found: " + evt.getPartnerId());
            Collection<Service> serviceList = evt.getServiceList();
            log.trace(serviceList.size());

            for (Service s : serviceList) {
                log.trace("  PartnerID: " + evt.getPartnerId());
                log.trace("    | service type: " + s.getType());
                log.trace("    | service address: " + s.getUri());
                if ("ds".equals(s.getType())) {
                    DS_SERVICE_ADDRESS = s.getUri().toString();
                    result.addAll(discover(EPC));
                } else {
                    result.add(s.getUri().toString());
                    if (client != null && !visitedSet.contains(s.getUri().toString())) {
                        new EpcisRequest(s.getUri().toString(), EPC, LOGIN, PASS, client, sessionId).start();
                        visitedSet.add(s.getUri().toString());
                    }
                }
            }
        }
        try {
            logout();
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        }
        return result;
    }

    public List<DSEvent> getDSEvents(String EPC) throws RemoteException {
        List<DSEvent> result = new ArrayList<DSEvent>();
        login();
        for (Event dsClientEvent : getEvents(EPC)) {
            Service first = dsClientEvent.getServiceList().get(0);
            log.debug(EPC + "      | partnerID: " + first.getId());
            log.debug(EPC + "             | partner info size: " + dsClientEvent.getServiceList().size());
            log.debug(EPC + "             | partner type: " + first.getType());
            result.add(new DSEvent(EPC, first.getUri().toString(), dsClientEvent.getBizStep(),
                    new Timestamp(dsClientEvent.getSourceTimeStamp().getTimeInMillis())));
        }
        logout();
        return result;
    }

    public List<DSEvent> getDSEvents(String EPC, String serviceType) throws RemoteException {
        List<DSEvent> result = new ArrayList<DSEvent>();
        login();
        for (Event dsClientEvent : getEvents(EPC)) {
            if (!dsClientEvent.getServiceList().isEmpty()) {
                Service firstService = dsClientEvent.getServiceList().get(0);
                log.debug(EPC + "      | partnerID:" + firstService.getId());
                log.debug(EPC + "             | partner info size:" + dsClientEvent.getServiceList().size());
                log.debug(EPC + "             | partner type: " + firstService.getType());
                if (firstService.getType().equals(serviceType)) {
                    result.add(new DSEvent(EPC, firstService.getUri().toString(),
                            dsClientEvent.getBizStep(), new Timestamp(dsClientEvent.getSourceTimeStamp().getTimeInMillis())));
                }
            }
        }
        log.debug(EPC + " -> dsEvents with corresponding type: " + result.size());
        logout();
        return result;
    }
}
