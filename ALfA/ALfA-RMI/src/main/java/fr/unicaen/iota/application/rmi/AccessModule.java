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
package fr.unicaen.iota.application.rmi;

import fr.unicaen.iota.application.ALfA;
import fr.unicaen.iota.ds.model.TEventItem;
import fr.unicaen.iota.ds.model.TServiceType;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import org.fosstrak.epcis.model.EPCISEventType;

public class AccessModule implements RMIAccessInterface {

    private ALfA controler;

    public AccessModule(String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) throws RemoteException {
        controler = new ALfA(pksFilename, pksPassword, trustPksFilename, trustPksPassword);
    }

    @Override
    public synchronized Map<ONSEntryType, String> queryONS(String EPC) {
        return controler.queryONS(EPC);
    }

    @Override
    public synchronized String getReferentDS(String EPC) throws RemoteException {
        return controler.getReferentDS(EPC);
    }

    @Override
    public synchronized List<EPCISEventType> traceEPC(Identity identity, String EPC) throws RemoteException {
        return controler.traceEPC(identity, EPC);
    }

    @Override
    public synchronized List<EPCISEventType> traceEPC(Identity identity, String EPC, Map<String, String> filters) throws RemoteException {
        return controler.traceEPC(identity, EPC, filters);
    }

    @Override
    public synchronized String getEPCDocURL(String EPC) throws RemoteException {
        return controler.getEPCDocURL(EPC);
    }

    @Override
    public synchronized void traceEPCAsync(Identity identity, String sessionID, CallbackClient client, String EPC) throws RemoteException {
        controler.traceEPCAsync(identity, sessionID, client, EPC);
    }

    @Override
    public synchronized List<EPCISEventType> queryEPCIS(Identity identity, String EPC, String EPCISAddress) throws RemoteException {
        return controler.queryEPCIS(identity, EPC, EPCISAddress);
    }

    @Override
    public synchronized List<EPCISEventType> queryEPCIS(Identity identity, Map<String, String> filters, String EPCISAddress) throws RemoteException {
        return controler.queryEPCIS(identity, filters, EPCISAddress);
    }

    @Override
    public synchronized List<TEventItem> queryDS(Identity identity, String EPC, String DSAddress) throws RemoteException {
        return controler.queryDS(identity, EPC, DSAddress);
    }

    @Override
    public synchronized List<TEventItem> queryDS(Identity identity, String EPC, String DSAddress, TServiceType serviceType) throws RemoteException {
        return controler.queryDS(identity, EPC, DSAddress, serviceType);
    }
}
