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
package fr.unicaen.iota.application.rmi;

import fr.unicaen.iota.application.Controler;
import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.model.ONSEntryType;
import fr.unicaen.iota.application.model.Spec;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public class AccessModule implements AccessInterface {

    private Controler controler;

    public AccessModule() throws RemoteException {
        controler = new Controler();
    }

    @Override
    public synchronized Map<ONSEntryType, String> queryONS(String epc) {
        return controler.queryONS(epc);
    }

    @Override
    public synchronized String getReferenteDS(String epc) throws RemoteException {
        return controler.getReferenteDS(epc);
    }

    @Override
    public synchronized List<EPCISEvent> traceEPC(String epc) throws RemoteException {
        return controler.traceEPC(epc);
    }

    @Override
    public synchronized List<String> getEPCDoc(String epc) {
        return controler.getEPCDoc(epc);
    }

    @Override
    public synchronized List<EPCISEvent> getEPCEPCIS(String epc) {
        return controler.getEPCEPCIS(epc);
    }

    @Override
    public synchronized Spec getSpecs(String address) throws RemoteException {
        return controler.getSpecs(address);
    }

    @Override
    public synchronized void traceEPCAsync(String sessionId, CallBackClient client, String EPC) throws RemoteException {
        controler.traceEPCAsync(sessionId, client, EPC);
    }

    @Override
    public synchronized List<EPCISEvent> queryEPCIS(String epc, String EPCISAddress) throws RemoteException {
        return controler.queryEPCIS(epc, EPCISAddress);
    }

    @Override
    public synchronized List<DSEvent> queryDS(String epc, String DSAddress, String login, String password) throws RemoteException {
        return controler.queryDS(epc, DSAddress, login, password);
    }

    @Override
    public synchronized List<DSEvent> queryDS(String epc, String DSAddress, String login, String password, String serviceType) throws RemoteException {
        return controler.queryDS(epc, DSAddress, login, password, serviceType);
    }
}
