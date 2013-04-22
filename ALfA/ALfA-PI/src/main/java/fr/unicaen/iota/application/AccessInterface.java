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

import fr.unicaen.iota.application.rmi.CallbackClient;
import fr.unicaen.iota.ds.model.TEventItem;
import fr.unicaen.iota.ds.model.TServiceType;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 * RMI interface for the IoTa platform.
 */
public interface AccessInterface {

    /**
     * Queries the ONS for all NAPTR entries related to the given EPC code.
     *
     * @param EPC the EPC code
     * @return a mapping of all entries by service type
     * @throws RemoteException
     */
    public Map<ONSEntryType, String> queryONS(String EPC) throws RemoteException;

    /**
     * Queries the ONS for the product information URL for the givent EPC code.
     *
     * @param EPC the EPC code
     * @return the URL for the product documentation
     * @throws RemoteException
     */
    public String getEPCDocURL(String EPC) throws RemoteException;

    /**
     * Queries the ONS for the URL of the referent Discovery Service for the
     * given EPC code.
     *
     * @param EPC the EPC code
     * @return the URL of the referent Discovery Service
     * @throws RemoteException
     */
    public String getReferentDS(String EPC) throws RemoteException;

    /**
     * Queries a given Discovery Service for all events concerning a given EPC
     * code.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @param DSAddress the DS URL
     * @return a list of DS events
     * @throws RemoteException
     */
    public List<TEventItem> queryDS(Identity identity, String EPC, String DSAddress) throws RemoteException;

    /**
     * Queries a given Discovery Service for all events concerning a given EPC
     * code.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @param DSAddress the DS URL
     * @param serviceType the service type
     * @return a list of DS events
     * @throws RemoteException
     */
    public List<TEventItem> queryDS(Identity identity, String EPC, String DSAddress, TServiceType serviceType) throws RemoteException;

    /**
     * Gets all the EPCIS events concerning a given EPC code.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @return a list of EPCIS events
     * @throws RemoteException
     */
    public List<EPCISEventType> traceEPC(Identity identity, String EPC) throws RemoteException;

    /**
     * Gets all the EPCIS events concerning a given EPC code and matching the
     * given filters.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @param filters the filters
     * @return a list of EPCIS events
     * @throws RemoteException
     */
    public List<EPCISEventType> traceEPC(Identity identity, String EPC, Map<String, String> filters) throws RemoteException;

    /**
     * Gets all the EPCIS events sorted by EPCIS concerning a given EPC code.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @return a list of EPCIS events by EPCIS
     * @throws RemoteException
     */
    public Map<String, List<EPCISEventType>> traceEPCByEPCIS(Identity identity, String EPC) throws RemoteException;

    /**
     * Gets all the EPCIS events sorted by EPCIS concerning a given EPC code and matching the
     * given filters.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @param filters the filters
     * @return a list of EPCIS events by EPCIS
     * @throws RemoteException
     */
    public Map<String, List<EPCISEventType>> traceEPCByEPCIS(Identity identity, String EPC, Map<String, String> filters) throws RemoteException;

    /**
     * Asynchronously gets all the EPCIS events concerning a given EPC code.
     *
     * @param identity the client identification
     * @param sessionID the session for the request
     * @param client the callback client to whom the events will be sent
     * @param EPC the EPC code
     * @throws RemoteException
     */
    public void traceEPCAsync(Identity identity, String sessionID, CallbackClient client, String EPC) throws RemoteException;

    /**
     * Gets all the EPCIS events concerning a given EPC code from a given ECPIS
     * repository.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @param EPCISAddress the URL of the EPCIS repository
     * @return a list of EPCIS events
     * @throws RemoteException
     */
    public List<EPCISEventType> queryEPCIS(Identity identity, String EPC, String EPCISAddress) throws RemoteException;

    /**
     * Gets all the EPCIS events matching the given filters from a given ECPIS
     * repository.
     *
     * @param identity the client identification
     * @param filters the EPC code
     * @param EPCISAddress the URL of the EPCIS repository
     * @return a list of EPCIS events
     * @throws RemoteException
     */
    public List<EPCISEventType> queryEPCIS(Identity identity, Map<String, String> filters, String EPCISAddress) throws RemoteException;
}
