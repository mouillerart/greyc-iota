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
package fr.unicaen.iota.application;

import fr.unicaen.iota.application.operations.DiscoveryOperation;
import fr.unicaen.iota.application.operations.EpcisOperation;
import fr.unicaen.iota.application.operations.TraceEPC;
import fr.unicaen.iota.application.operations.TraceEPCAsync;
import fr.unicaen.iota.application.rmi.CallbackClient;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.nu.ONSOperation;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISEventType;

public class ALfA implements AccessInterface {

    private final String pksFilename;
    private final String pksPassword;
    private final String trustPksFilename;
    private final String trustPksPassword;
    private static final Log LOG = LogFactory.getLog(ALfA.class);

    public ALfA(String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        this.pksFilename = pksFilename;
        this.pksPassword = pksPassword;
        this.trustPksFilename = trustPksFilename;
        this.trustPksPassword = trustPksPassword;
    }

    @Override
    public Map<ONSEntryType, String> queryONS(String EPC) {
        LOG.trace("[COMMAND]--[QUERY ONS]");
        LOG.trace(EPC);
        return new ONSOperation().queryONS(EPC);
    }

    @Override
    public String getReferentDS(String EPC) {
        LOG.trace("[COMMAND]--[GET REFERENT DS]");
        LOG.trace("EPC = " + EPC);
        Map<ONSEntryType, String> res = queryONS(EPC);
        return res.get(ONSEntryType.ided_ds);
    }

    @Override
    public List<EPCISEventType> traceEPC(Identity identity, String EPC) throws RemoteException {
        LOG.trace("[COMMAND]--[TRACE EPC]");
        LOG.trace(EPC);
        return new TraceEPC(identity, pksFilename, pksPassword, trustPksFilename, trustPksPassword).traceEPC(EPC);
    }

    @Override
    public Map<String, List<EPCISEventType>> traceEPCByEPCIS(Identity identity, String EPC) throws RemoteException {
        LOG.trace("[COMMAND]--[TRACE EPC BY EPCIS]");
        LOG.trace(EPC);
        return new TraceEPC(identity, pksFilename, pksPassword, trustPksFilename, trustPksPassword).traceEPCbyEPCIS(EPC);
    }

    @Override
    public List<EPCISEventType> traceEPC(Identity identity, String EPC, Map<String, String> filters) throws RemoteException {
        LOG.trace("[COMMAND]--[FILTERED TRACE]");
        LOG.trace(EPC);
        return new TraceEPC(identity, pksFilename, pksPassword, trustPksFilename, trustPksPassword).filteredTrace(EPC, filters);
    }

    @Override
    public Map<String, List<EPCISEventType>> traceEPCByEPCIS(Identity identity, String EPC, Map<String, String> filters) throws RemoteException {
        LOG.trace("[COMMAND]--[FILTERED TRACE BY EPCIS]");
        LOG.trace(EPC);
        return new TraceEPC(identity, pksFilename, pksPassword, trustPksFilename, trustPksPassword).filteredTracebyEPCIS(EPC, filters);
    }

    @Override
    public String getEPCDocURL(String EPC) {
        LOG.trace("[COMMAND]--[GET EPC DOC]");
        Map<ONSEntryType, String> res = queryONS(EPC);
        return res.get(ONSEntryType.html);
    }

    @Override
    public void traceEPCAsync(Identity identity, String sessionID, CallbackClient client, String EPC) throws RemoteException {
        LOG.trace("[COMMAND]--[TRACE EPC ASYNC]");
        new TraceEPCAsync(EPC, sessionID, client, identity, pksFilename, pksPassword, trustPksFilename, trustPksPassword).start();
    }

    @Override
    public List<EPCISEventType> queryEPCIS(Identity identity, String EPC, String EPCISAddress) throws RemoteException {
        LOG.trace("[COMMAND]--[QUERY EPCIS]");
        EpcisOperation epcisOperation = null;
        try {
            epcisOperation = new EpcisOperation(identity, EPCISAddress, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        } catch (Exception e) {
            epcisOperation = null;
            String msg = "Unable to create service proxy port";
            LOG.warn(msg, e);
            throw new RemoteException(msg, e);
        }
        return epcisOperation.getEventFromEPC(EPC);
    }

    @Override
    public List<EPCISEventType> queryEPCIS(Identity identity, Map<String, String> filters, String EPCISAddress) throws RemoteException {
        LOG.trace("[COMMAND]--[QUERY EPCIS FILTERS]");
        EpcisOperation epcisOperation = null;
        try {
            epcisOperation = new EpcisOperation(identity, EPCISAddress, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        } catch (Exception e) {
            epcisOperation = null;
            String msg = "Unable to create service proxy port";
            LOG.warn(msg, e);
            throw new RemoteException(msg, e);
        }
        List<EPCISEventType> res = epcisOperation.getFilteredEvent(filters);
        return res;
    }

    @Override
    public List<DSEvent> queryDS(Identity identity, String EPC, String DSAddress) throws RemoteException {
        LOG.trace("[COMMAND]--[QUERY DS]");
        DiscoveryOperation dsOperation = new DiscoveryOperation(identity, DSAddress, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
        List<DSEvent> list = dsOperation.getDSEvents(EPC);
        return list;
    }

    @Override
    public List<DSEvent> queryDS(Identity identity, String EPC, String DSAddress, ONSEntryType serviceType) throws RemoteException {
        LOG.trace("[COMMAND]--[QUERY DS]");
        try {
            DiscoveryOperation dsOperation = new DiscoveryOperation(identity, DSAddress, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
            List<DSEvent> list = dsOperation.getDSEvents(EPC, serviceType);
            return list;
        } catch (Exception e) {
            LOG.error(null, e);
            return null;
        }
    }
}
