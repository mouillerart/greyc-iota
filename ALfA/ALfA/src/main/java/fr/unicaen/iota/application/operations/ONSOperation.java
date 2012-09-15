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
package fr.unicaen.iota.application.operations;

import fr.unicaen.iota.application.conf.Constants;
import fr.unicaen.iota.application.model.ONSEntryType;
import fr.unicaen.iota.application.util.EPCUtilities;
import fr.unicaen.iota.application.util.EPCUtilities.InvalidFormatException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.EnumMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xbill.DNS.*;

/**
 *
 */
public class ONSOperation {
    
    private final String[] ONS_ADDRESSES;
    private static final Log log = LogFactory.getLog(ONSOperation.class);

    public ONSOperation(String[] ONS_ADDRESSES) {
        this.ONS_ADDRESSES = ONS_ADDRESSES;
    }

    public String getReferentDS(String epc) throws RemoteException {
        Map<ONSEntryType, String> res = queryONS(epc);
        return res.get(ONSEntryType.ds);
    }

    public Map<ONSEntryType, String> queryONS(String epc) {
        log.trace("queryONS: " + epc);
        Map<ONSEntryType, String> result = new EnumMap<ONSEntryType, String>(ONSEntryType.class);
        Record[] records;
        try {
            String formatedEPC = Constants.ONS_SPEC_LEVEL >= 2 ? EPCUtilities.formatRevertEpc(epc) : formatEPC(epc);
            log.trace(formatedEPC);
            records = reverseDns(formatedEPC);
        } catch (InvalidFormatException ex) {
            log.error(null, ex);
            return null;
        } catch (IOException e) {
            log.error(null, e);
            return null;
        }
        if (records == null) {
            return null;
        }
        for (Record record : records) {
            String entry = record.rdataToString();
            log.trace(entry);
            if (entry.split(Constants.ONS_EPCIS_ENTRY).length > 1) {
                String[] tab = entry.split(Constants.ONS_ENTRY_REGEX);
                result.put(ONSEntryType.epcis, tab[1]);
            }
            if (entry.split(Constants.ONS_SPEC_ENTRY).length > 1) {
                String[] tab = entry.split(Constants.ONS_ENTRY_REGEX);
                result.put(ONSEntryType.spec, tab[1]);
            }
            if (entry.split(Constants.ONS_DS_ENTRY).length > 1) {
                String[] tab = entry.split(Constants.ONS_ENTRY_REGEX);
                result.put(ONSEntryType.ds, tab[1]);
            }
        }
        return result;
    }

    private String formatEPC(String epc) {
        String[] tab = epc.split("\\.|:");
        if (tab.length != 7) {
            return "0.0.0.sgtin.onsepc1.eu.";
        }
        StringBuilder res = new StringBuilder();
        for (int i = 5; i >= 2; i--) {
            res.append(tab[i]);
            res.append(".");
        }
        res.append(Constants.ONS_DOMAIN_PREFIX);
        return res.toString();
    }

    private Record[] reverseDns(String hostIp) throws IOException {
        log.trace("reverseDns: " + hostIp);
        Resolver res = new ExtendedResolver(ONS_ADDRESSES);
        Name name = new Name(hostIp);
        int type = Type.NAPTR;
        int dclass = DClass.IN;
        Record rec = Record.newRecord(name, type, dclass);
        Message query = Message.newQuery(rec);
        Message response = res.send(query);
        Record[] answers = response.getSectionArray(Section.ANSWER);
        return answers.length == 0 ? null : answers;
    }
}
