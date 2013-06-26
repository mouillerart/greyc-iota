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
package fr.unicaen.iota.application.operations;

import fr.unicaen.iota.nu.ONSOperation;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.*;
import java.util.Map.Entry;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.AggregationEventType;
import org.fosstrak.epcis.model.EPC;
import org.fosstrak.epcis.model.EPCISEventType;
import org.fosstrak.epcis.model.TransactionEventType;

/**
 *
 */
public class TraceEPC {

    private final Identity identity;
    private final String pksFilename;
    private final String pksPassword;
    private final String trustPksFilename;
    private final String trustPksPassword;
    private final ONSOperation onsOperation;
    private static final Log log = LogFactory.getLog(TraceEPC.class);

    public TraceEPC(Identity identity, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        this.identity = identity;
        this.pksFilename = pksFilename;
        this.pksPassword = pksPassword;
        this.trustPksFilename = trustPksFilename;
        this.trustPksPassword = trustPksPassword;
        this.onsOperation = new ONSOperation();
    }

    public List<EPCISEventType> traceEPC(String EPC) throws RemoteException {
        log.trace("EPC = " + EPC);
        return traceEPCAux(EPC, new HashMap<String, String>());
    }

    public Map<String, List<EPCISEventType>> traceEPCbyEPCIS(String EPC) throws RemoteException {
        log.trace("EPC = " + EPC);
        return traceEPCAuxByEPCIS(EPC, new HashMap<String, String>());
    }

    public List<EPCISEventType> filteredTrace(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("Filters = " + filters);
        return traceEPCAux(EPC, filters);
    }

    public Map<String, List<EPCISEventType>> filteredTracebyEPCIS(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("Filters = " + filters);
        return traceEPCAuxByEPCIS(EPC, filters);
    }

    private List<EPCISEventType> traceEPCAux(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("[TRACE EPC]: " + EPC);
        log.trace("Get Referent ds address");
        String dsAddress = onsOperation.getReferentIDedDS(EPC);
        if (dsAddress == null) {
            log.warn("Unable to retreive referent ds address for this epc code");
            return new ArrayList<EPCISEventType>();
        } else {
            log.trace("referent ds address found: " + dsAddress);
        }
        log.trace("Start discover");
        DiscoveryOperation dsOp = new DiscoveryOperation(identity, dsAddress, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        return traceEPC(dsOp, EPC, filters);
    }

    private Map<String, List<EPCISEventType>> traceEPCAuxByEPCIS(String EPC, Map<String, String> filters) throws RemoteException {
        log.trace("[TRACE EPC BY EPCIS]: " + EPC);
        log.trace("Get Referent ds address");
        String dsAddress = onsOperation.getReferentIDedDS(EPC);
        if (dsAddress == null) {
            log.warn("Unable to retreive referent ds address for this epc code");
            return new HashMap<String, List<EPCISEventType>>();
        } else {
            log.trace("referent ds address found: " + dsAddress);
        }
        log.trace("Start discover");
        DiscoveryOperation dsOp = new DiscoveryOperation(identity, dsAddress, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        return traceEPCByEPCIS(dsOp, EPC, filters);
    }

    private List<EPCISEventType> traceEPC(DiscoveryOperation dsOp, String EPC, Map<String, String> filters) throws RemoteException {
        List<EPCISEventType> eventList = new ArrayList<EPCISEventType>();
        for (String EPCIS_SERVICE_ADDRESS : dsOp.discover(EPC)) {
            EpcisOperation epcisOperation = null;
            try {
                epcisOperation = new EpcisOperation(identity, EPCIS_SERVICE_ADDRESS, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
            } catch (Exception ex) {
                epcisOperation = null;
                String msg = "Unable to create service proxy port";
                log.warn(msg, ex);
                throw new RemoteException(msg, ex);
            }
            Collection<EPCISEventType> list = epcisOperation.getObjectEventFromEPC(EPC, filters);
            eventList.addAll(list);
            list = epcisOperation.getQuantityEventFromEPC(EPC, filters);
            eventList.addAll(list);
            log.trace("nbr epc events: " + list.size());
            Collection<EPCISEventType> children = epcisOperation.getAggregationEventFromEPC(EPC, filters);
            eventList.addAll(children);
            /*log.trace("nbr child events: " + children.size());
            for (EPCISEventType o : children) {
                AggregationEventType event = (AggregationEventType) o;
                for (EPC childEpc : event.getChildEPCs().getEpc()) {
                    log.trace("new traceEPC: " + childEpc.getValue());
                    eventList.addAll(traceEPCAux(childEpc.getValue(), filters));
                }
            }*/
            Collection<EPCISEventType> trans = epcisOperation.getTransactionEventFromEPC(EPC, filters);
            eventList.addAll(trans);
            /*for (EPCISEventType o : trans) {
                TransactionEventType event = (TransactionEventType) o;
                for (EPC childEpc : event.getEpcList().getEpc()) {
                    log.trace("new traceEPC: " + childEpc.getValue());
                    eventList.addAll(traceEPCAux(childEpc.getValue(), filters));
                }
            }*/
        }
        return eventList;
    }

    private Map<String, List<EPCISEventType>> traceEPCByEPCIS(DiscoveryOperation dsOp, String EPC, Map<String, String> filters) throws RemoteException {
        Map<String, List<EPCISEventType>> eventListByEPCIS = new HashMap<String, List<EPCISEventType>>();
        for (String EPCIS_SERVICE_ADDRESS : dsOp.discover(EPC)) {
            EpcisOperation epcisOperation = null;
            try {
                epcisOperation = new EpcisOperation(identity, EPCIS_SERVICE_ADDRESS, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
            } catch (Exception ex) {
                String msg = "Unable to create service proxy port";
                log.warn(msg, ex);
                throw new RemoteException(msg, ex);
            }
            List<EPCISEventType> list = epcisOperation.getObjectEventFromEPC(EPC, filters);
            addToMap(eventListByEPCIS, EPCIS_SERVICE_ADDRESS, list);
            list = epcisOperation.getQuantityEventFromEPC(EPC, filters);
            addToMap(eventListByEPCIS, EPCIS_SERVICE_ADDRESS, list);
            log.trace("nbr epc events: " + list.size());
            List<EPCISEventType> children = epcisOperation.getAggregationEventFromEPC(EPC, filters);
            addToMap(eventListByEPCIS, EPCIS_SERVICE_ADDRESS, children);
            log.trace("nbr child events: " + children.size());
            /*for (EPCISEventType o : children) {
                AggregationEventType event = (AggregationEventType) o;
                for (EPC childEpc : event.getChildEPCs().getEpc()) {
                    log.trace("new traceEPC: " + childEpc.getValue());
                    Map<String, List<EPCISEventType>> agEventMap = traceEPCAuxByEPCIS(childEpc.getValue(), filters);
                    addToMap(eventListByEPCIS, agEventMap);
                }
            }*/
            List<EPCISEventType> trans = epcisOperation.getTransactionEventFromEPC(EPC, filters);
            addToMap(eventListByEPCIS, EPCIS_SERVICE_ADDRESS, trans);
            /*for (EPCISEventType o : trans) {
                TransactionEventType event = (TransactionEventType) o;
                for (EPC childEpc : event.getEpcList().getEpc()) {
                    log.trace("new traceEPC: " + childEpc.getValue());
                    Map<String, List<EPCISEventType>> transEventMap = traceEPCAuxByEPCIS(childEpc.getValue(), filters);
                    addToMap(eventListByEPCIS, transEventMap);
                }
            }*/
        }
        return eventListByEPCIS;
    }

    private void addToMap(Map<String, List<EPCISEventType>> map, String epcisAddress, List<EPCISEventType> listToAdd) {
        if (map.containsKey(epcisAddress)) {
            map.get(epcisAddress).addAll(listToAdd);
        }
        else {
            map.put(epcisAddress, listToAdd);
        }
    }

    private void addToMap(Map<String, List<EPCISEventType>> map, Map<String, List<EPCISEventType>> mapToAdd) {
        for (Entry<String, List<EPCISEventType>> mapToAddEntry : mapToAdd.entrySet()) {
            String epcisAddress = mapToAddEntry.getKey();
            List<EPCISEventType> eventList = mapToAddEntry.getValue();
            if (map.containsKey(epcisAddress)) {
                map.get(epcisAddress).addAll(eventList);
            } else {
                map.put(epcisAddress, eventList);
            }
        }
    }

}
