/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.lambda.Utils;

import fr.unicaen.iota.application.soap.IoTaException;
import fr.unicaen.iota.application.soap.client.OmICron;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.lambda.Configuration;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.sigma.client.SigMaClient;
import fr.unicaen.iota.sigma.model.Verification;
import fr.unicaen.iota.tau.model.Identity;
import java.util.List;
import java.util.Map;
import org.fosstrak.epcis.model.EPCISEventType;

public class EventsHandler {

    private OmICron omicron;

    private SigMaClient sigmaClient;

    public EventsHandler(String identity, String omegaUrl, String pksFilename, String pksPassword,
            String trustPksFilename, String trustPksPassword) {
        Identity id = new Identity();
        id.setAsString(identity);
        omicron = new OmICron(id, omegaUrl, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
    }

    /**
     * Gets a list of events from EPC code.
     * @param epc The EPC code to trace.
     * @return List of events associated to the EPC code.
     * @throws IoTaException
     */
    public List<EPCISEventType> traceEPC(String epc) throws IoTaException {
        return omicron.traceEPC(epc);
    }

    /**
     * Gets the referent DS of an EPC code.
     * @param epc The EPC code to handle.
     * @return The referent DS of the EPC code.
     * @throws IoTaException
     */
    public String getReferentDS(String epc) throws IoTaException {
        return omicron.getReferentDS(epc);
    }

    /**
     * Queries an ONS to get a list of events of an EPC code.
     * @param epc The EPC code to handle.
     * @return a mapping of all entries by service type.
     * @throws IoTaException
     */
    public Map<ONSEntryType, String> queryONS(String epc) throws IoTaException {
        return omicron.queryONS(epc);
    }

    /**
     * Queries a DS to get a list of events of an EPC code.
     * @param epc The EPC code to handle.
     * @param dsUrl The DS's URL to query.
     * @return List of events associated to an EPC code contained by a DS.
     * @throws IoTaException
     */
    public List<DSEvent> queryDS(String epc, String dsUrl) throws IoTaException {
        return omicron.queryDS(epc, dsUrl);
    }

    /**
     * Queries an EPCIS to get a list of events of an EPC code.
     * @param epc The EPC code to handle.
     * @param dsUrl The EPCIS's URL to query.
     * @return List of events associated to an EPC code contained by a EPCIS.
     * @throws IoTaException
     */
    public List<EPCISEventType> queryEPCIS (String epc, String epcisUrl) throws IoTaException {
        return omicron.queryEPCIS(epc, epcisUrl);
    }

    /**
     * Gets all the EPCIS events sorted by EPCIS concerning a given EPC code.
     *
     * @param identity the client identification
     * @param EPC the EPC code
     * @return a list of EPCIS events by EPCIS
     * @throws RemoteException
     */
    public Map<String, List<EPCISEventType>> traceEPCByEPCIS(String epc) throws IoTaException {
        return omicron.traceEPCByEPCIS(epc);
    }

    /**
     * Inits the Sigma Client.
     */
    public void initSigmaClient() {
        sigmaClient = new SigMaClient(Configuration.SIGMA_URL, Configuration.PKS_FILENAME,
                Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
    }

    /**
     * Verify the signature of the event.
     * @param event The event which the signature is to be signed.
     * @return The <code>Verification</code>
     */
    public Verification verifySignature(EPCISEventType event) {
        return sigmaClient.verify(event);
    }

}
