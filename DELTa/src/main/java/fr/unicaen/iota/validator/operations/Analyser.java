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
import fr.unicaen.iota.validator.Controler;
import fr.unicaen.iota.validator.IOTA;
import fr.unicaen.iota.validator.listener.AnalyserStatus;
import fr.unicaen.iota.validator.model.*;
import java.io.File;
import java.rmi.Naming;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Document;

/**
 *
 */
public class Analyser extends Thread implements Runnable {

    private final String XMLPath;
    private AccessInterface server;
    private final IOTA iota;
    private final Identity identity;
    private ThreadManager threadManager;
    private final AnalyserStatus analyserStatus;
    
    private static final Log log = LogFactory.getLog(Analyser.class);

    public Analyser(String XMLPath, Identity identity, IOTA iota, AnalyserStatus analyserStatus) {
        this.iota = iota;
        this.XMLPath = XMLPath;
        this.analyserStatus = analyserStatus;
        this.identity = identity;
        try {
            this.server = (AccessInterface) Naming.lookup(Configuration.RMI_SERVER_URL);
        } catch (Exception e) {
            log.fatal("Failed to setup for RMI", e);
        }
    }

    @Override
    public void run() {
        try {
            File xmlFile = new File(XMLPath);
            if (!xmlFile.exists()) {
                threadManager.stopThread(this);
                return;
            }
            DOMParser parser = new DOMParser();
            parser.parse(XMLPath);
            Document documentDOM = parser.getDocument();
            DOMBuilder builder = new DOMBuilder();
            org.jdom.Document documentJDOM = builder.build(documentDOM);
            if (Configuration.DEBUG) {
                log.debug("Parsing file " + XMLPath + "... ");
            }
            List<EPC> containerList = parseEPCSimulated(documentJDOM.getRootElement());
            Map<EPC, List<BaseEvent>> epcisResults = null;
            if (Configuration.ANALYSE_EPCIS_EVENTS) {
                EPCISEntryComparator epcisComparator = new EPCISEntryComparator(identity, server, iota);
                epcisResults = epcisComparator.getEventNotVerified(containerList);
                if (Configuration.DEBUG) {
                    log.debug("\n\n");
                }
            }
            DSEntryComparator dsComparator = null;
            if (Configuration.ANALYSE_DS_TO_DS_EVENTS || Configuration.ANALYSE_EPCIS_TO_DS_EVENTS) {
                dsComparator = new DSEntryComparator(identity, server, iota);
            }
            Map<EPC, List<DSEvent>> dsResults = null;
            if (Configuration.ANALYSE_EPCIS_TO_DS_EVENTS) {
                dsResults = dsComparator.getEventNotVerified(containerList);
                if (Configuration.DEBUG) {
                    log.debug("\n\n");
                }
            }
            Map<EPC, List<DSEvent>> dsToDsResults = null;
            if (Configuration.ANALYSE_DS_TO_DS_EVENTS) {
                dsToDsResults = dsComparator.verifyDSToDSReferences(containerList);
            }
            if (report(containerList, epcisResults, dsResults, dsToDsResults)) {
                log.trace("Analysing file " + xmlFile.getName() + " ------------> [ DONE ]");
            } else {
                log.error("Analysing file " + xmlFile.getName() + " ------------> [ ERROR ]");
            }
        } catch (Exception ex) {
            log.error("[ a problem occurred " + ex.getMessage() + " ]", ex);
            Controler.ACTIVE_FILE_LIST.remove(XMLPath);
        }
        threadManager.stopThread(this);
    }

    public List<EPC> parseEPCSimulated(Element elem) {
        List<EPC> res = new ArrayList<EPC>();
        for (Object o : elem.getChildren("EPCSimulated")) {
            Element elem2 = (Element) o;
            String epc = elem2.getAttributeValue("epc");
            EPC container = new EPC(epc);
            Element parent = elem2.getChild("parent");
            if (parent != null) {
                container.setParent(parent.getValue());
            }
            for (Object o2 : elem2.getChildren("node")) {
                BaseEvent base = parseElement((Element) o2);
                container.addEvent(base);
            }
            res.add(container);
        }
        for (Object o : elem.getChildren("childs")) {
            Element elem2 = (Element) o;
            res.addAll(parseEPCSimulated(elem2));
        }
        return res;
    }

    public BaseEvent parseElement(Element elem) {
        String type = elem.getAttributeValue("type");
        if ("object".equals(type)) {
            ObjectEvent objectNode = new ObjectEvent();
            objectNode.loadFromXML(elem);
            return objectNode;
        } else if ("aggregation".equals(type)) {
            AggregationEvent aggregationNode = new AggregationEvent();
            aggregationNode.loadFromXML(elem);
            return aggregationNode;
        } else if ("transition".equals(type)) {
            TransactionEvent transactionNode = new TransactionEvent();
            transactionNode.loadFromXML(elem);
            return transactionNode;
        } else if ("quantity".equals(type)) {
            QuantityEvent quantityNode = new QuantityEvent();
            quantityNode.loadFromXML(elem);
            return quantityNode;
        }
        return null;
    }

    void setThreadManager(ThreadManager threadManager) {
        this.threadManager = threadManager;
    }

    private boolean report(List<EPC> containerList, Map<EPC, List<BaseEvent>> epcisResults,
            Map<EPC, List<DSEvent>> dsResults, Map<EPC, List<DSEvent>> dsToDsResults) {
        boolean result = false;
        int nbUnverifiedEvent = 0;
        if (Configuration.ANALYSE_EPCIS_EVENTS) {
            for (EPC container : epcisResults.keySet()) {
                List<BaseEvent> list = epcisResults.get(container);
                nbUnverifiedEvent += list.size();
            }
        }
        if (Configuration.ANALYSE_EPCIS_TO_DS_EVENTS) {
            for (EPC container : dsResults.keySet()) {
                List<DSEvent> list = dsResults.get(container);
                nbUnverifiedEvent += list.size();
            }
        }
        if (Configuration.ANALYSE_DS_TO_DS_EVENTS) {
            for (EPC container : dsToDsResults.keySet()) {
                List<DSEvent> list = dsToDsResults.get(container);
                nbUnverifiedEvent += list.size();
            }
        }
        File f = new File(XMLPath);
        String fileName = f.getName();
        if (nbUnverifiedEvent == 0) {
            f.renameTo(new File(Configuration.VERIFIED_DIRECTORY + "/" + fileName));
            result = true;
        } else {
            analyserStatus.errorFound();
            f.renameTo(new File(Configuration.UNVERIFIED_DIRECTORY + "/" + fileName));
        }
        analyserStatus.publishResults(containerList, epcisResults, dsResults, dsToDsResults);
        analyserStatus.analysedObject();
        Controler.ACTIVE_FILE_LIST.remove(XMLPath);
        return result;
    }
}
