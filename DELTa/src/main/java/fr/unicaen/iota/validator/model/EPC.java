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
package fr.unicaen.iota.validator.model;

import fr.unicaen.iota.ds.model.*;
import org.fosstrak.epcis.model.EPCISEventType;
import fr.unicaen.iota.validator.Configuration;
import fr.unicaen.iota.validator.IOTA;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class EPC {

    private static final Log log = LogFactory.getLog(EPC.class);
    private String epc;
    private List<BaseEvent> eventList;
    private List<TEventItem> dsToDsReferentList;
    private String parentId;

    public EPC(String epc) {
        this.epc = epc;
        eventList = new ArrayList<BaseEvent>();
        parentId = null;
    }

    public void addEvent(BaseEvent e) {
        getEventList().add(e);
    }

    public List<BaseEvent> getActiveEventList(IOTA iota) {
        List<BaseEvent> res = new ArrayList<BaseEvent>();
        for (BaseEvent be : eventList) {
            if (!iota.get(be.getInfrastructure().getBizLoc()).isActiveAnalyse()) {
                continue;
            }
            res.add(be);
        }
        return res;
    }

    public List<TEventItem> getDSEvents(List<EPC> containerList) throws Exception {
        List<BaseEvent> eventListClone = new ArrayList<BaseEvent>();
        eventListClone.addAll(this.eventList);
        if (this.parentId != null) {
            BaseEvent parentEvent = getParentEvent(parentId, containerList);
            eventListClone.add(parentEvent);
        }
        List<TEventItem> events = new ArrayList<TEventItem>();
        for (BaseEvent be : eventListClone) {
            TEventItem evt = new TEventItem();
            TServiceItemList serviceList = new TServiceItemList();
            TServiceItem service = new TServiceItem();
            service.setUri(be.getInfrastructure().getServiceAddress());
            serviceList.getService().add(service);
            evt.setServiceList(serviceList);
            evt.setC(epc);
            evt.setLcs(be.getBizStep());
            events.add(evt);
            //new DSEvent(this.epc,
            //        be.getInfrastructure().getServiceAddress(), 
             //       be.getBizStep(), 
               //     null));
        }
        return events;
    }

    public Iterable<TEventItem> getDSToDSEvents(List<EPC> containerList) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * @return the epc
     */
    public String getEpc() {
        return epc;
    }

    public List<Infrastructure> getInfrastructures() {
        List<Infrastructure> list = new ArrayList<Infrastructure>();
        for (BaseEvent be : eventList) {
            if (!list.contains(be.getInfrastructure())) {
                list.add(be.getInfrastructure());
            }
        }
        return list;
    }

    /**
     * @param epc the epc to set
     */
    public void setEpc(String epc) {
        this.epc = epc;
    }

    /**
     * @return the eventList
     */
    public List<BaseEvent> getEventList() {
        return eventList;
    }

    /**
     * @param eventList the eventList to set
     */
    public void setEventList(List<BaseEvent> eventList) {
        this.eventList = eventList;
    }

    public List<DSEvent> verifyDSEvents(List<DSEvent> list, List<EPC> containerList, IOTA iota) throws Exception {
        List<BaseEvent> eventListClone = new ArrayList<BaseEvent>();
        eventListClone.addAll(this.eventList);
        if (this.parentId != null) {
            BaseEvent parentEvent = getParentEvent(parentId, containerList);
            eventListClone.add(parentEvent);
        }
        List<DSEvent> events = new ArrayList<DSEvent>();
        for (BaseEvent be : eventListClone) {
            if (!iota.get(be.getInfrastructure().getBizLoc()).isActiveAnalyse()) {
                continue;
            }
            boolean found = false;
            for (DSEvent dsEvent : list) {
                if (dsEvent.getBizStep().equals(be.getBizStep())
                        && dsEvent.getEPC().equals(this.epc)
                        && dsEvent.getReferenceAddress().equals(formatAddress(be.getInfrastructure().getServiceAddress()))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                events.add(new DSEvent(this.epc, be.getInfrastructure().getServiceAddress(), be.getBizStep(), null));
            }
        }
        if (Configuration.DEBUG) {
            debug(eventListClone, list);
        }
        return events;
    }

    public List<BaseEvent> verifyEPCISEvents(List<EPCISEventType> list, IOTA iota) {
        List<BaseEvent> res = new ArrayList<BaseEvent>();
        for (BaseEvent be : this.eventList) {
            if (!iota.get(be.getInfrastructure().getBizLoc()).isActiveAnalyse()) {
                continue;
            }
            if (!be.isContainedIn(list)) {
                res.add(be);
            }
        }
        return res;
    }

    public List<BaseEvent> reverseVerifyEPCISEvents(List<EPCISEvent> list) {
        List<BaseEvent> res = new ArrayList<BaseEvent>();
        for (BaseEvent be : this.eventList) {
            if (!be.isContainedIn(list)) {
                res.add(be);
            }
        }
        return res;
    }

    public String formatAddress(String address) {
        String[] sub = address.split(Configuration.EPCIS_CAPTURE_INTERFACE);
        return sub[0] + Configuration.EPCIS_QUERY_INTERFACE;
    }

    /**
     * @return the parent
     */
    public String getParent() {
        return parentId;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(String parent) {
        this.parentId = parent;
    }

    private void debug(List<BaseEvent> eventListClone, List<DSEvent> list) {
        log.debug("");
        log.debug("# # # # # Event found in remote repositories # # # # #");
        log.debug("#");
        for (DSEvent dsEvent : list) {
            log.debug("#  " + dsEvent.getBizStep());
            log.debug("#  " + dsEvent.getEPC());
            log.debug("#  " + dsEvent.getReferenceAddress());
            log.debug("#");
        }
        log.debug("# # # # # # # Event found in the XML file # # # # # # #");
        log.debug("#");
        for (BaseEvent be : eventListClone) {
            log.debug("#  " + be.getBizStep());
            log.debug("#  " + this.epc);
            log.debug("#  " + formatAddress(be.getInfrastructure().getServiceAddress()));
            log.debug("#");
        }
        log.debug("# # # # # # # # # # # # # # # # # # # # # # # # # # # #");
        log.debug("");
    }

    private BaseEvent getParentEvent(String parentId, List<EPC> containerList) throws Exception {
        EPC container = null;
        for (EPC c : containerList) {
            if (c.getEpc().equals(parentId)) {
                container = c;
                break;
            }
        }
        if (container == null) {
            throw new Exception("Aggregated container has no parent in xml file");
        }
        for (BaseEvent be : container.getEventList()) {
            if (be instanceof AggregationEvent && ((AggregationEvent) be).getChildEpcs().contains(this.epc)) {
                return be;
            }
        }
        throw new Exception("Aggregated container has parent but no corresponding event found in parent!");
    }

    /**
     * @return the dsToEPCISReferentList
     */
    public List<DSEvent> getDsToEPCISReferentList(List<EPC> containerList) {
        List<DSEvent> result = new ArrayList<DSEvent>();
        for (BaseEvent event : eventList) {
            result.add(new DSEvent(epc, event.getInfrastructure().getServiceAddress(), event.getBizStep(), null));
        }
        if (this.parentId != null) {
            BaseEvent parentEvent = null;
            try {
                parentEvent = getParentEvent(parentId, containerList);
            } catch (Exception ex) {
                log.fatal(null, ex);
            }
            result.add(new DSEvent(epc, parentEvent.getInfrastructure().getServiceAddress(), parentEvent.getBizStep(), null));
        }
        return result;
    }

    /**
     * @return the dsToDsReferentList
     */
    public List<DSEvent> getDsToDsReferentList() {
        return dsToDsReferentList;
    }

    /**
     * @param dsToDsReferentList the dsToDsReferentList to set
     */
    public void setDsToDsReferentList(List<DSEvent> dsToDsReferentList) {
        this.dsToDsReferentList = dsToDsReferentList;
    }
}
