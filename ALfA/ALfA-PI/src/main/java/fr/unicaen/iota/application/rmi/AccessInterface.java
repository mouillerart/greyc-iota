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

import fr.unicaen.iota.application.model.ONSEntryType;
import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.model.Spec;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface AccessInterface extends Remote {

    /**
     * Asynchronous tracing
     */
    public void traceEPCAsync(String sessionId, CallBackClient client, String EPC) throws RemoteException;

    public List<EPCISEvent> traceEPC(String epc) throws RemoteException;

    public Map<ONSEntryType, String> queryONS(String ECP) throws RemoteException;

    public List<String> getEPCDoc(String epc) throws RemoteException;

    public List<EPCISEvent> getEPCEPCIS(String epc) throws RemoteException;

    public List<EPCISEvent> queryEPCIS(String epc, String EPCISAddress) throws RemoteException;

    public List<DSEvent> queryDS(String epc, String DSAddress, String login, String password) throws RemoteException;

    public List<DSEvent> queryDS(String epc, String DSAddress, String login, String password, String serviceType) throws RemoteException;

    public Spec getSpecs(String address) throws RemoteException;

    public String getReferenteDS(String epc) throws RemoteException;
}
