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
package fr.unicaen.iota.validator.operations;

import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.application.rmi.AccessInterface;
import fr.unicaen.iota.tau.model.Identity;
import fr.unicaen.iota.validator.Configuration;
import fr.unicaen.iota.validator.IOTA;
import fr.unicaen.iota.validator.model.DSLink;
import fr.unicaen.iota.validator.model.EPC;
import fr.unicaen.iota.validator.model.Infrastructure;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DSEntryComparator {

    private final AccessInterface applicationLevelInterface;
    private final IOTA iota;
    private final Identity identity;
    private static final Log log = LogFactory.getLog(DSEntryComparator.class);

    public DSEntryComparator(Identity identity, AccessInterface applicationLevelInterface, IOTA iota) {
        this.applicationLevelInterface = applicationLevelInterface;
        this.iota = iota;
        this.identity = identity;
    }

    public Map<EPC, List<DSEvent>> getEventNotVerified(EPC container, List<EPC> list) {
        if (Configuration.DEBUG) {
            log.debug("analysing DS to EPCIS entries ...");
        }
        Map<EPC, List<DSEvent>> map = new HashMap<EPC, List<DSEvent>>();
        try {
            map.put(container, verifyDSEntry(container, list));
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        }
        return map;
    }

    public Map<EPC, List<DSEvent>> getEventNotVerified(List<EPC> list) {
        if (Configuration.DEBUG) {
            log.debug("Analysing DS to EPCIS entries...");
        }
        Map<EPC, List<DSEvent>> map = new HashMap<EPC, List<DSEvent>>();
        for (EPC container : list) {
            try {
                map.put(container, verifyDSEntry(container, list));
            } catch (RemoteException ex) {
                log.fatal(null, ex);
            }
        }
        return map;
    }

    private List<DSEvent> verifyDSEntry(EPC container, List<EPC> containerList) throws RemoteException {
        List<DSEvent> eventList = new ArrayList<DSEvent>();
        List<String> tmp = new ArrayList<String>();
        for (Infrastructure infra : container.getInfrastructures()) {
            DSLink link = iota.get(infra.getBizLoc()).getDSLink();
            if (!link.isActiveAnalyse() || tmp.contains(link.getDsAddress())) {
                continue;
            }
            tmp.add(link.getDsAddress());
            Date d1 = new Date();
            eventList.addAll(applicationLevelInterface.queryDS(container.getEpc(),
                    link.getDsAddress(), identity, Configuration.DS_SERVICE_TYPE_FOR_EPCIS));
            Date d2 = new Date();
            link.addTimeResponse(d2.getTime() - d1.getTime());
        }
        List<DSEvent> res = null;
        try {
            res = container.verifyDSEvents(eventList, containerList, iota);
        } catch (Exception ex) {
            log.fatal(null, ex);
        }
        int requiredEventNumber = container.getEventList().size() + (container.getParent() != null ? 1 : 0);
        String report = (requiredEventNumber - res.size()) + " / " + requiredEventNumber;
        boolean match = res.isEmpty();
        if (Configuration.DEBUG) {
            log.debug(container.getEpc() + " => " + report + " events match" + (match ? " " : "!"));
            log.debug("         | " + eventList.size() + " events found in remote repositories");
        }
        log.trace(String.format("%s %s %d %d", Configuration.DS_LOG_TYPE, container.getEpc(), requiredEventNumber - res.size(), requiredEventNumber));
        return res;
    }

    public Map<EPC, List<DSEvent>> verifyDSToDSReferences(List<EPC> list) throws RemoteException {
        if (Configuration.DEBUG) {
            log.debug("Analysing DS to DS entries...");
        }
        Map<EPC, List<DSEvent>> map = new HashMap<EPC, List<DSEvent>>();
        for (EPC container : list) {
            map.put(container, verifyDSToDSEntry(container));
        }
        return map;
    }

    private List<DSEvent> verifyDSToDSEntry(EPC container) throws RemoteException {
        List<DSEvent> dsEventList = new ArrayList<DSEvent>();
        List<Infrastructure> infrastructures = container.getInfrastructures();
        List<String> dsLinks = new ArrayList<String>();
        String referentDS = applicationLevelInterface.getReferentDS(container.getEpc());
        for (Infrastructure infra : infrastructures) {
            if (!iota.get(infra.getBizLoc()).getDSLink().isActiveAnalyse()) {
                continue;
            }
            DSLink link = iota.getDSLink(infra);
            if (!dsLinks.contains(link.getDsAddress()) && !link.getDsAddress().equals(referentDS)) {
                dsLinks.add(link.getDsAddress());
            }
            if (link.getDsAddress().equals(referentDS)) {
                Date d1 = new Date();
                List<DSEvent> list = applicationLevelInterface.queryDS(container.getEpc(), link.getDsAddress(), identity, Configuration.DS_SERVICE_TYPE_FOR_DS);
                Date d2 = new Date();
                link.addTimeResponse(d2.getTime() - d1.getTime());
                for (DSEvent dSEvent : list) {
                    if (Configuration.DEBUG) {
                        log.debug("found address: " + dSEvent.getReferenceAddress());
                    }
                    if (dsEventList.contains(dSEvent)) {
                        continue;
                    }
                    dsEventList.add(dSEvent);
                }
            }
        }
        container.setDsToDsReferentList(dsEventList);
        List<DSEvent> res = new ArrayList<DSEvent>();
        if (Configuration.DEBUG) {
            log.debug("IOTA schema infrastructure list: " + dsLinks);
            log.debug("found reference addresses: " + dsEventList);
        }

        for (DSEvent dSEvent : dsEventList) {
            if (!dsLinks.contains(dSEvent.getReferenceAddress())) {
                res.add(dSEvent);
            }
        }
        String report = (dsLinks.size() - res.size()) + " / " + (dsLinks.size());
        boolean match = res.isEmpty();
        if (Configuration.DEBUG) {
            log.debug(container.getEpc() + " => " + report + " events match" + (match ? " " : "!"));
        }
        log.trace(String.format("%s %s %d %d", Configuration.DS_TO_DS_LOG_TYPE, container.getEpc(), dsLinks.size() - res.size(), dsLinks.size()));
        return res;
    }
}
