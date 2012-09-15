package fr.unicaen.iota.application.util;

import fr.unicaen.iota.application.conf.Constants;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public class EPCUtilities {

    private EPCUtilities() {
    }
    private static final Map<String, Pattern> PATTERNS = new HashMap<String, Pattern>();

    static {
        PATTERNS.put("gid", Pattern.compile("((0|[1-9][0-9]*)\\.){2}(0|[1-9][0-9]*)"));
        Pattern p = Pattern.compile("([0-9]+\\.){2}([0-9]|[A-Z]|[a-z]|[\\!\\(\\)\\*\\+\\-',:;=_]|(%(([0-9]|[A-F])|[a-f]){2}))+");
        PATTERNS.put("sgtin", p);
        PATTERNS.put("sgln", p);
        PATTERNS.put("grai", p);
        PATTERNS.put("sscc", Pattern.compile("[0-9]+\\.[0-9]+"));
        PATTERNS.put("giai", Pattern.compile("[0-9]+\\.([0-9]|[A-Z]|[a-z]|[\\!\\(\\)\\*\\+\\-',:;=_]|(%(([0-9]|[A-F])|[a-f]){2}))+"));
    }

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

    protected static void checkEpc(String textContent) throws InvalidFormatException {
        String uri = textContent;
        if (!uri.startsWith("urn:epc:id:")) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: must start with \"urn:epc:id:\"");
        }
        uri = uri.substring("urn:epc:id:".length());

        // check the patterns for the different EPC types
        String epcType = uri.substring(0, uri.indexOf(":"));
        uri = uri.substring(epcType.length() + 1);
        //log.debug("Checking pattern for EPC type " + epcType + ": " + uri);
        if (!PATTERNS.containsKey(epcType)) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: unknown EPC type: " + epcType);
        }
        Pattern p = PATTERNS.get(epcType);
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
            if (count1 + count2 > 13) {
                exceeded = true;
            }
        } else if ("sgln".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            if (count1 + count2 > 12) {
                exceeded = true;
            }
        } else if ("grai".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1) - (count1 + 1);
            if (count1 + count2 > 12) {
                exceeded = true;
            }
        } else if ("sscc".equals(epcType)) {
            int count2 = uri.length() - (count1 + 1);
            if (count1 + count2 > 17) {
                exceeded = true;
            }
        } else if ("giai".equals(epcType)) {
            int count2 = uri.length() - (count1 + 1);
            if (count1 + count2 > 30) {
                exceeded = true;
            }
        } else {
            // nothing to count
        }
        if (exceeded) {
            throw new InvalidFormatException("Invalid 'pure identity' EPC format: check allowed number of characters for EPC type '" + epcType + "'");
        }
    }

    private static String revertString(String str) {
        StringBuilder res = new StringBuilder();
        for (int i = str.length() - 1; i >= 0; i--) {
            if (str.charAt(i) == '.') {
                continue;
            }
            res.append(str.charAt(i));
            res.append('.');
        }
        return res.toString();
    }

    public static String formatRevertEpc(String textContent) throws InvalidFormatException {
        checkEpc(textContent);
        String uri = textContent;
        uri = uri.substring("urn:epc:id:".length());
        String epcType = uri.substring(0, uri.indexOf(":"));
        uri = uri.substring(epcType.length() + 1);

        int count1 = uri.indexOf(".");
        if ("sgtin".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1);
            return revertString(uri.substring(0, count2)) + "sgtin.id." + Constants.ONS_DOMAIN_PREFIX;
        } else if ("sgln".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1);
            return revertString(uri.substring(0, count2)) + "sgln.id." + Constants.ONS_DOMAIN_PREFIX;
        } else if ("grai".equals(epcType)) {
            int count2 = uri.indexOf(".", count1 + 1);
            return revertString(uri.substring(0, count2)) + "grai.id." + Constants.ONS_DOMAIN_PREFIX;
        } else if ("sscc".equals(epcType)) {
            return revertString(uri.substring(0, count1)) + "sscc.id." + Constants.ONS_DOMAIN_PREFIX;
        } else if ("giai".equals(epcType)) {
            return revertString(uri.substring(0, count1)) + "giai.id." + Constants.ONS_DOMAIN_PREFIX;
        }
        return null;
    }

    private static boolean checkUri(String textContent) throws InvalidFormatException {
        try {
            new URI(textContent);
        } catch (URISyntaxException e) {
            throw new InvalidFormatException(e.getMessage());
        }
        return true;
    }

    public static boolean isReferencable(String epcOrUri) {
        try {
            checkEpc(epcOrUri);
            return true;
        } catch (InvalidFormatException ex) {
            return false;
        }
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
