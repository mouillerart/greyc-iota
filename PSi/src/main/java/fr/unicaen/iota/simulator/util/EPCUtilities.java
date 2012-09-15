/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class EPCUtilities {

    public static boolean checkEpcOrUri(String epcOrUri) throws InvalidFormatException {
        boolean isEpc = false;
        if (epcOrUri.startsWith("urn:epc:id:")) {
            // check if it is a valid EPC
            checkEpc(epcOrUri);
            isEpc = true;
        } else {
            // childEPCs in AggregationEvents, and epcList in
            // TransactionEvents might also be simple URIs
            checkUri(epcOrUri);
        }
        return isEpc;
    }

    private static void checkEpc(String textContent) throws InvalidFormatException {
        String uri = textContent;
        if (!uri.startsWith("urn:epc:id:")) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: must start with \"urn:epc:id:\"");
        }
        uri = uri.substring("urn:epc:id:".length());

        // check the patterns for the different EPC types
        String epcType = uri.substring(0, uri.indexOf(":"));
        uri = uri.substring(epcType.length() + 1);
        //log.debug("Checking pattern for EPC type " + epcType + ": " + uri);
        Pattern p;
        if ("gid".equals(epcType)) {
            p = Pattern.compile("((0|[1-9][0-9]*)\\.){2}(0|[1-9][0-9]*)");
        } else if ("sgtin".equals(epcType) || "sgln".equals(epcType) || "grai".equals(epcType)) {
            p = Pattern.compile("([0-9]+\\.){2}([0-9]|[A-Z]|[a-z]|[\\!\\(\\)\\*\\+\\-',:;=_]|(%(([0-9]|[A-F])|[a-f]){2}))+");
        } else if ("sscc".equals(epcType)) {
            p = Pattern.compile("[0-9]+\\.[0-9]+");
        } else if ("giai".equals(epcType)) {
            p = Pattern.compile("[0-9]+\\.([0-9]|[A-Z]|[a-z]|[\\!\\(\\)\\*\\+\\-',:;=_]|(%(([0-9]|[A-F])|[a-f]){2}))+");
        } else {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: unknown EPC type: " + epcType);
        }
        Matcher m = p.matcher(uri);
        if (!m.matches()) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: pattern \"" + uri
                    + "\" is invalid for EPC type \"" + epcType + "\" - check with Tag Data Standard");
        }

        // check the number of digits for the different EPC types
        boolean exceeded = false;
        int count1 = uri.indexOf(".");
        if ("sgtin".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            exceeded = (count1 + count2 > 13);
        } else if ("sgln".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            exceeded = (count1 + count2 > 12);
        } else if ("grai".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            exceeded = (count1 + count2 > 12);
        } else if ("sscc".equals(epcType)) {
            int count2 = uri.length() - (count1 + 1);
            exceeded = (count1 + count2 > 17);
        } else if ("giai".equals(epcType)) {
            int count2 = uri.length() - (count1 + 1);
            exceeded = (count1 + count2 > 30);
        } else {
            // nothing to count
        }
        if (exceeded) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: check allowed number of characters for EPC type '" + epcType + "'");
        }
    }

    private static boolean checkUri(String textContent) throws InvalidFormatException {
        try {
            URI tst = new URI(textContent);
        } catch (URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        return true;
    }

    public static class InvalidFormatException extends Exception {

        public InvalidFormatException(String msg) {
            super(msg);
        }

        public InvalidFormatException(String msg, Throwable e) {
            super(msg, e);
        }
    }
}
