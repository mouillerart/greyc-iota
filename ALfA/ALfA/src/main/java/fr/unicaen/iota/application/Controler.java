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
package fr.unicaen.iota.application;

import fr.unicaen.iota.application.conf.Constants;
import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.model.Spec;
import fr.unicaen.iota.application.operations.*;
import fr.unicaen.iota.application.rmi.CallBackClient;
import fr.unicaen.iota.application.model.ONSEntryType;
import fr.unicaen.iota.application.util.EpcisUtil;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISEventType;

public class Controler {

    private static final Log LOG = LogFactory.getLog(Controler.class);

    public Map<ONSEntryType, String> queryONS(String epc) {
        LOG.trace("[COMMAND]--[QUERY ONS]");
        LOG.trace(epc);
        return new ONSOperation(Constants.ONS_HOSTS).queryONS(epc);
    }

    public String getReferenteDS(String epc) throws RemoteException {
        LOG.trace("[COMMAND]--[GET REFERENTE DS]");
        LOG.trace("EPC = " + epc);
        Map<ONSEntryType, String> res = queryONS(epc);
        return res.get(ONSEntryType.ds);
    }

    public List<EPCISEvent> traceEPC(String epc) throws RemoteException {
        LOG.trace("[COMMAND]--[TRACE EPC]");
        LOG.trace(epc);
        return new TraceEPC(Constants.DS_LOGIN, Constants.DS_PASSWORD).traceEPC(epc);
    }

    public List<String> getEPCDoc(String epc) {
        LOG.trace("[COMMAND]--[GET EPC DOC]");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#getEPCDoc");
    }

    public List<EPCISEvent> getEPCEPCIS(String epc) {
        LOG.trace("[COMMAND]--[GET EPC EPCIS]");
        throw new java.lang.UnsupportedOperationException("Please implement " + this.getClass().getName() + "#getEPCEPCIS");
    }

    public Spec getSpecs(String address) throws RemoteException {
        LOG.trace("[COMMAND]--[GET SPEC]");
        return GetSpec.getSpecs(address);
    }

    public void traceEPCAsync(String sessionId, CallBackClient client, String EPC) throws RemoteException {
        LOG.trace("[COMMAND]--[TRACE EPC ASYNC]");
        new TraceEPCAsync(EPC, client, Constants.DS_LOGIN, Constants.DS_PASSWORD, sessionId).start();
    }

    public List<EPCISEvent> queryEPCIS(String epc, String EPCISAddress) throws RemoteException {
        LOG.trace("[COMMAND]--[QUERY EPCIS]");
        List<EPCISEvent> list = new ArrayList<EPCISEvent>();
        EpcisOperation epcisOperation = null;
        while (epcisOperation == null) {
            try {
                epcisOperation = new EpcisOperation(EPCISAddress);
            } catch (Exception e) {
                epcisOperation = null;
                LOG.warn("Unable to create service proxy port! [RETRY]");
            }
            try {
                Thread.sleep(1);
            } catch (InterruptedException ex) {
            }
        }
        List<EPCISEventType> tmp = new ArrayList<EPCISEventType>();
        List<EPCISEventType> l;
        if ((l = epcisOperation.getAggregationEventFromEPC(epc)) != null) {
            tmp.addAll(l);
        }
        if ((l = epcisOperation.getObjectEventFromEPC(epc)) != null) {
            tmp.addAll(l);
        }
        for (EPCISEventType o : tmp) {
            list.add(EpcisUtil.processEvent(o));
        }
        return list;
    }

    public List<DSEvent> queryDS(String epc, String DSAddress, String login, String password) throws RemoteException {
        LOG.trace("[COMMAND]--[QUERY DS]");
        DiscoveryOperation dsOperation = new DiscoveryOperation(login, password, DSAddress);
        List<DSEvent> list = dsOperation.getDSEvents(epc);
        return list;
    }

    public List<DSEvent> queryDS(String epc, String DSAddress, String login, String password, String serviceType) throws RemoteException {
        LOG.trace("[COMMAND]--[QUERY DS]");
        try {
            DiscoveryOperation dsOperation = new DiscoveryOperation(login, password, DSAddress);
            List<DSEvent> list = dsOperation.getDSEvents(epc, serviceType);
            return list;
        } catch (Exception e) {
            LOG.error(null, e);
            return null;
        }
    }
}
