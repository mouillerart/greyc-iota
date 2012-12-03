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

import fr.unicaen.iota.application.rmi.CallbackClient;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.ds.model.TEventItem;
import fr.unicaen.iota.ds.model.TServiceItem;
import fr.unicaen.iota.ds.model.TServiceType;
import fr.unicaen.iota.dseta.client.DSeTaClient;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoveryOperation {

    private static final Log log = LogFactory.getLog(DiscoveryOperation.class);
    private final Identity identity;
    private String DS_SERVICE_ADDRESS;
    private final CallbackClient client;
    private final String sessionID;
    private final Set<String> visitedSet = new HashSet<String>();
    private final DSeTaClient dSClient;

    public DiscoveryOperation(Identity identity, String ds_service_address) {
        super();
        this.identity = identity;
        this.DS_SERVICE_ADDRESS = ds_service_address;
        this.client = null;
        this.sessionID = null;
        this.dSClient = new DSeTaClient(identity, DS_SERVICE_ADDRESS);
    }

    public DiscoveryOperation(Identity identity, String dsAddress, String sessionID, CallbackClient client) {
        super();
        this.identity = identity;
        this.DS_SERVICE_ADDRESS = dsAddress;
        this.client = client;
        this.sessionID = sessionID;
        this.dSClient = new DSeTaClient(identity, DS_SERVICE_ADDRESS);
    }

    private List<TEventItem> getEvents(String EPC) throws RemoteException {
        List<TEventItem> dsClientEventList;
        try {
            dsClientEventList = dSClient.eventLookup(EPC, null, null, null);
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
        Collection<TEventItem> evtList;
        try {
            evtList = getEvents(EPC);
        } catch (RemoteException e) {
            log.error(null, e);
            return new HashSet<String>();
        }
        for (TEventItem evt : evtList) {
            log.trace("Source found: " + evt.getP());
            Collection<TServiceItem> serviceList = evt.getServiceList().getService();
            log.trace(serviceList.size());

            for (TServiceItem s : serviceList) {
                log.trace("  PartnerID: " + evt.getP());
                log.trace("    | service type: " + s.getType());
                log.trace("    | service address: " + s.getUri());
                // TODO: also handle TServiceType.DS (?)
                if (s.getType() == TServiceType.IDED_DS) {
                    // TODO: Quick'n'dirty correction
                    String old_addr = DS_SERVICE_ADDRESS;
                    DS_SERVICE_ADDRESS = s.getUri().toString();
                    result.addAll(discover(EPC));
                    DS_SERVICE_ADDRESS = old_addr;
                } else if (s.getType() == TServiceType.IDED_EPCIS) {
                    result.add(s.getUri().toString());
                    if (client != null && !visitedSet.contains(s.getUri().toString())) {
                        new EpcisRequest(s.getUri().toString(), EPC, identity, sessionID, client).start();
                        visitedSet.add(s.getUri().toString());
                    }
                } // else: do nothing
            }
        }
        return result;
    }

    public List<TEventItem> getDSEvents(String EPC) throws RemoteException {
        List<TEventItem> result = new ArrayList<TEventItem>();
        for (TEventItem dsClientEvent : getEvents(EPC)) {
            TServiceItem first = dsClientEvent.getServiceList().getService().get(0);
            log.debug(EPC + "      | partnerID: " + first.getId());
            log.debug(EPC + "             | partner info size: " + dsClientEvent.getServiceList().getService().size());
            log.debug(EPC + "             | partner type: " + first.getType());
            result.add(dsClientEvent);
        }
        return result;
    }

    public List<TEventItem> getDSEvents(String EPC, TServiceType serviceType) throws RemoteException {
        List<TEventItem> result = new ArrayList<TEventItem>();
        for (TEventItem dsClientEvent : getEvents(EPC)) {
            if (!dsClientEvent.getServiceList().getService().isEmpty()) {
                TServiceItem firstService = dsClientEvent.getServiceList().getService().get(0);
                log.debug(EPC + "      | partnerID:" + firstService.getId());
                log.debug(EPC + "             | partner info size:" + dsClientEvent.getServiceList().getService().size());
                log.debug(EPC + "             | partner type: " + firstService.getType());
                if (firstService.getType() == serviceType) {
                    result.add(dsClientEvent);
                }
            }
        }
        log.debug(EPC + " -> dsEvents with corresponding type: " + result.size());
        return result;
    }
}
