package de.huberlin.informatik.pnk.appControl.base;

import java.util.*;
/**
 * Insert the type's description here.
 * Creation date: (21.9.2001 00:09:59)
 * @author: Administrator
 */
public class D {
    /**
     * Contains debuglevel of application
     */
    public static int debug;

/**
 * D constructor comment. Never used. Only static Methods and Fields.
 */
    public D() {
        super();
    }

/**
 * print text if debuglevel >= 2.
 */
    public static void d(String text) {
        if (2 <= debug) {
            System.out.println(text);
        }
    }

/**
 * print text if debuglevel >= 2.
 */
    public static void d() {
        if (2 <= debug) {
            System.out.println();
        }
    }

/**
 * print text if debuglevel >= i.
 */
    public static void d(String text, int i) {
        if (i <= debug) {
            System.out.println(text);
        }
    }

/**
 * print object if debuglevel >= 2.
 */
    public static void d(Object o) {
        d(o.toString());
    }

/**
 * print object if debuglevel >= i.
 */
    public static void d(Object o, int i) {
        d(o.toString(), i);
    }

    /**
       *Dump a specification hashtable.
     */
    public static void dumpSpecificationTable(Hashtable hash) {
        dumpSpecificationTable(hash, 2);
    }

/**
 * print text to stderr if debug >= 2.
 */
    public static void e(String text) {
        if (2 <= debug) {
            System.err.println(text);
        }
    }

/**
 * print text to stderr if debug >= 1.
 */
    public static void e(String text, int i) {
        if (i <= debug) {
            System.err.println(text);
        }
    }

/**
 * print text to stderr.
 */
    public static void err(String text) {
        System.err.println(text);
    }

    /**
       *Dump a specification hashtable.
     */
    public static void dumpSpecificationTable(Hashtable hash, int i) {
        for (Enumeration keys = hash.keys(); keys.hasMoreElements(); ) {
            String actKey = (String)keys.nextElement();
            d(actKey + ":", i);
            Hashtable extensionHash = (Hashtable)hash.get(actKey);
            if (extensionHash != null) {
                for (Enumeration extensions = extensionHash.keys(); extensions.hasMoreElements(); ) {
                    String actExt = (String)extensions.nextElement();
                    d(actExt + ": " + extensionHash.get(actExt), i);
                }
            }
        }
    }

    public static void dumpHashtable(Hashtable hash) {
        for (Enumeration keys = hash.keys(); keys.hasMoreElements(); ) {
            Object actKey = keys.nextElement();
            Object hashObj = hash.get(actKey);
            if (hashObj != null) {
                d(actKey + ": " + hashObj);
            } else {
                d(actKey + ": null");
            }
        }
    }

    public static void dumpVector(Vector v) {
        String s = "{";
        for (Enumeration keys = v.elements(); keys.hasMoreElements(); ) {
            Object actKey = keys.nextElement();
            if (s != "{") {
                s = s + ", ";
            }
            s = s + actKey.toString();
        }
        D.d(s);
    }
}

