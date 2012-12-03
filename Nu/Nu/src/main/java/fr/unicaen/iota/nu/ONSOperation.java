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
package fr.unicaen.iota.nu;

import java.io.IOException;
import java.net.UnknownHostException;
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

    private static final Log log = LogFactory.getLog(ONSOperation.class);
    private final String[] ONS_ADDRESSES;
    private final boolean useDNSSEC;

    public ONSOperation() {
        this(Constants.ONS_HOSTS);
    }

    public ONSOperation(String ONS_ADDRESS) {
        this(new String[]{ONS_ADDRESS}, false);
    }

    public ONSOperation(String[] ONS_ADDRESSES) {
        this(ONS_ADDRESSES, false);
    }

    public ONSOperation(String[] ONS_ADDRESSES, boolean useDNSSEC) {
        this.ONS_ADDRESSES = ONS_ADDRESSES;
        this.useDNSSEC = useDNSSEC;
    }

    public String getReferentDS(String epc) throws RemoteException {
        Map<ONSEntryType, String> res = queryONS(epc);
        return res.get(ONSEntryType.ds);
    }

    public String getReferentIDedDS(String epc) throws RemoteException {
        Map<ONSEntryType, String> res = queryONS(epc);
        return res.get(ONSEntryType.ided_ds);
    }

    public Map<ONSEntryType, String> queryONS(String epc) {
        log.trace("queryONS: " + epc);
        Map<ONSEntryType, String> result = new EnumMap<ONSEntryType, String>(ONSEntryType.class);
        Record[] records;
        try {
            String formatedEPC = Constants.ONS_SPEC_LEVEL >= 2 ? EPCUtilities.formatRevertEpc(epc) : formatEPC(epc);
            log.trace("        : " + formatedEPC);
            records = reverseDns(formatedEPC);
        } catch (EPCUtilities.InvalidFormatException ex) {
            log.error("Wrong format for epc: " + epc, ex);
            return null;
        } catch (IOException e) {
            log.error("", e);
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
            } else if (entry.split(Constants.ONS_IDED_EPCIS_ENTRY).length > 1) {
                String[] tab = entry.split(Constants.ONS_ENTRY_REGEX);
                result.put(ONSEntryType.ided_epcis, tab[1]);
            } else if (entry.split(Constants.ONS_HTML_ENTRY).length > 1) {
                String[] tab = entry.split(Constants.ONS_ENTRY_REGEX);
                result.put(ONSEntryType.html, tab[1]);
            } else if (entry.split(Constants.ONS_DS_ENTRY).length > 1) {
                String[] tab = entry.split(Constants.ONS_ENTRY_REGEX);
                result.put(ONSEntryType.ds, tab[1]);
            } else if (entry.split(Constants.ONS_IDED_DS_ENTRY).length > 1) {
                String[] tab = entry.split(Constants.ONS_ENTRY_REGEX);
                result.put(ONSEntryType.ided_ds, tab[1]);
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
        if (useDNSSEC) {
            /*
             * RRset[] rsets = response.getSectionRRsets(Section.ANSWER); for
             * (RRset set: rsets) { verifyZone(set.rrs().next(),
             * set.sigs().next()); }
             */
            return null;
        } else {
            Record[] answers = response.getSectionArray(Section.ANSWER);
            return answers.length == 0 ? null : answers;
        }
    }

    public void pingONS() throws TextParseException, UnknownHostException {
        for (String address : ONS_ADDRESSES) {
            Lookup l = new Lookup("version.bind.", Type.TXT, DClass.CH);
            l.setResolver(new SimpleResolver(address));
            l.run();
            if (l.getResult() == Lookup.SUCCESSFUL) {
                log.trace("PING ONS: " + l.getAnswers()[0].rdataToString());
            } else {
                throw new UnknownHostException("ONS addresse unreachable");
            }
        }
    }
}
