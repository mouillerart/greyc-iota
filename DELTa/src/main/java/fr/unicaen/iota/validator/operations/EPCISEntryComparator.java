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
package fr.unicaen.iota.validator.operations;

import fr.unicaen.iota.application.AccessInterface;
import fr.unicaen.iota.tau.model.Identity;
import fr.unicaen.iota.validator.Configuration;
import fr.unicaen.iota.validator.IOTA;
import fr.unicaen.iota.validator.model.BaseEvent;
import fr.unicaen.iota.validator.model.EPC;
import fr.unicaen.iota.validator.model.Infrastructure;
import fr.unicaen.iota.validator.model.Link;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 *
 */
public class EPCISEntryComparator {

    private static final Log log = LogFactory.getLog(EPCISEntryComparator.class);
    private final AccessInterface applicationLevelInterface;
    private final IOTA iota;
    private final Identity identity;

    public EPCISEntryComparator(Identity identity, AccessInterface applicationLevelInterface, IOTA iota) {
        this.applicationLevelInterface = applicationLevelInterface;
        this.iota = iota;
        this.identity = identity;
    }

    public Map<EPC, List<BaseEvent>> getEventNotVerified(List<EPC> list) throws RemoteException {
        if (Configuration.DEBUG) {
            log.debug("Analysing EPCIS entries...");
        }
        Map<EPC, List<BaseEvent>> map = new HashMap<EPC, List<BaseEvent>>();
        for (EPC container : list) {  //sort the list
            map.put(container, verifyEPCISEntry(container));
        }
        return map;
    }

    private List<BaseEvent> verifyEPCISEntry(EPC container) throws RemoteException {
        List<EPCISEventType> eventList = new ArrayList<EPCISEventType>();
        for (Infrastructure infra : container.getInfrastructures()) {
            Link link = iota.get(infra.getBizLoc());
            if (!link.isActiveAnalyse()) {
                continue;
            }
            Date d1 = new Date();
            eventList.addAll(applicationLevelInterface.queryEPCIS(identity, container.getEpc(), link.getServiceAddress()));
            Date d2 = new Date();
            link.addTimeResponse(d2.getTime() - d1.getTime());
        }
        List<BaseEvent> res = container.verifyEPCISEvents(eventList, iota);
        int fullListSize = container.getActiveEventList(iota).size();
        String report = (fullListSize - res.size()) + " / " + fullListSize;
        boolean succedded = res.isEmpty();
        if (Configuration.DEBUG) {
            log.debug(container.getEpc() + " => " + report + " events match " + (succedded ? "" : "!"));
        }
        log.trace(String.format("%s %s %d %d", Configuration.EPCIS_LOG_TYPE,  container.getEpc(), fullListSize - res.size(), fullListSize));
        return res;
    }

    public String formatAddress(String address) {
        String[] sub = address.split(Configuration.EPCIS_CAPTURE_INTERFACE);
        return sub[0] + Configuration.EPCIS_QUERY_INTERFACE;
    }
}
