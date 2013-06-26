/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.ds.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ds.soap.InternalExceptionResponse;
import fr.unicaen.iota.dseta.client.DSeTaClient;
import fr.unicaen.iota.dseta.soap.SecurityExceptionResponse;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DiscoveryOperation {

    private static final Log log = LogFactory.getLog(DiscoveryOperation.class);
    private final Identity identity;
    private String DS_SERVICE_ADDRESS;
    private final String pksFilename;
    private final String pksPassword;
    private final String trustPksFilename;
    private final String trustPksPassword;
    private final CallbackClient client;
    private final String sessionID;
    private final Set<String> visitedSet = new HashSet<String>();
    private DSeTaClient dsetaClient;

    public DiscoveryOperation(Identity identity, String ds_service_address, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        super();
        this.identity = identity;
        this.DS_SERVICE_ADDRESS = ds_service_address;
        this.pksFilename = pksFilename;
        this.pksPassword = pksPassword;
        this.trustPksFilename = trustPksFilename;
        this.trustPksPassword = trustPksPassword;
        this.client = null;
        this.sessionID = null;
        this.dsetaClient = new DSeTaClient(identity, DS_SERVICE_ADDRESS, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
    }

    public DiscoveryOperation(Identity identity, String dsAddress, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword, String sessionID, CallbackClient client) {
        super();
        this.identity = identity;
        this.DS_SERVICE_ADDRESS = dsAddress;
        this.pksFilename = pksFilename;
        this.pksPassword = pksPassword;
        this.trustPksFilename = trustPksFilename;
        this.trustPksPassword = trustPksPassword;
        this.client = client;
        this.sessionID = sessionID;
        this.dsetaClient = new DSeTaClient(identity, DS_SERVICE_ADDRESS, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
    }

    private List<DSEvent> getEvents(String epc) throws RemoteException {
        List<DSEvent> dsClientEventList;
        try {
            dsClientEventList = dsetaClient.eventLookup(epc, null, null, null, null, null);
        } catch (ImplementationExceptionResponse ex) {
            log.error("Unable to process eventLookup", ex);
            throw new RemoteException("Unable to process eventLookup");
        } catch (InternalExceptionResponse ex) {
            log.error("Unable to process eventLookup", ex);
            throw new RemoteException("Unable to process eventLookup");
        } catch (SecurityExceptionResponse ex) {
            log.error("Unable to process eventLookup", ex);
            throw new RemoteException("Unable to process eventLookup");
        }
        log.debug(epc + " -> dsEvents in repository: " + dsClientEventList.size());
        return dsClientEventList;
    }

    public Set<String> discover(String epc) {
        Set<String> result = discover(epc, DS_SERVICE_ADDRESS, DS_SERVICE_ADDRESS);
        this.dsetaClient = new DSeTaClient(identity, DS_SERVICE_ADDRESS, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        return result;
    }

    private Set<String> discover(String epc, String oldDSAddress, String dsAddress) {
        Set<String> result = new HashSet<String>();
        Collection<DSEvent> evtList;
        if (dsAddress == null || dsAddress.isEmpty()) {
            return result;
        }
        else if (!dsAddress.equals(oldDSAddress) || dsetaClient == null) {
            try {
                dsetaClient = new DSeTaClient(identity, dsAddress, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
            } catch (Exception e) {
                log.error(null, e);
                return result;
            }
        }
        try {
            evtList = getEvents(epc);
        } catch (RemoteException e) {
            log.error(null, e);
            return result;
        }
        for (DSEvent evt : evtList) {
            // TODO: also handle TServiceType.DS (?)
            if (ONSEntryType.ided_ds.name().equals(evt.getServiceType())) {
                // TODO: Quick'n'dirty correction
                result.addAll(discover(epc, dsAddress, evt.getServiceAddress()));
            }
            else if (ONSEntryType.ided_epcis.name().equals(evt.getServiceType())) {
                result.add(evt.getServiceAddress());
                if (client != null && !visitedSet.contains(evt.getServiceAddress())) {
                    new EpcisRequest(evt.getServiceAddress(), pksFilename, pksPassword, trustPksFilename, trustPksPassword, epc, identity, sessionID, client).start();
                    visitedSet.add(evt.getServiceAddress());
                }
            } // else: do nothing
        }
        return result;
    }

    public List<DSEvent> getDSEvents(String epc) throws RemoteException {
        List<DSEvent> result = new ArrayList<DSEvent>();
        for (DSEvent event : getEvents(epc)) {
            result.add(event);
        }
        log.debug(epc + " -> dsEvents with corresponding type: " + result.size());
        return result;
    }

    public List<DSEvent> getDSEvents(String epc, ONSEntryType serviceType) throws RemoteException {
        List<DSEvent> result = new ArrayList<DSEvent>();
        for (DSEvent event : getEvents(epc)) {
            if (event.getServiceType() != null && serviceType.name().equals(event.getServiceType())) {
                result.add(event);
            }
        }
        log.debug(epc + " -> dsEvents with corresponding type: " + result.size());
        return result;
    }
}
