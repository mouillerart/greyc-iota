package de.huberlin.informatik.pnk.netElementExtensions.llNet;

import de.huberlin.informatik.pnk.kernel.*;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
 */


/**
 * Marking for Place/Transition-Nets. <br>
 * The marking is a natural number. <br>
 * Addition and subtraction of markings are addition and subtraction of
 * natural numbers.
 */

import de.huberlin.informatik.pnk.netElementExtensions.base.*;

public class NaturalNumber extends Marking {
    protected int internState = 0;

    /**
     * Constructor specifying the place. <BR>
     * The marking is "0".
     */
    public NaturalNumber(Extendable place) {
        super(place);
        if (!place.getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Place")) {
            if (!place.getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Arc"))
                System.out.println("Fehler: Place/Arc erwartet. Erhalten: " +
                                   place.getClass().getName());
        }
    }

    /**
     * Constructor specifying the place and the marking. <BR>
     */
    public NaturalNumber(Extendable place, String marking) {
        super(place, marking);
        if (!place.getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Place")) {
            if (!place.getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Arc"))
                System.out.println("Fehler: Place/Arc erwartet. Erhalten: " +
                                   place.getClass().getName());
        }
    }

    /**
     * Returns <code>true</code> if the value of this marking is
     * greater then marking <code>m</code>, otherwise false.
     */
    public boolean contains(Marking m) {
        if (m == null) {
            System.out.println("NaturalNumber: Marking null");
            return false;
        }
        NaturalNumber marking = toNaturalNumber(m);
        return (internState >= marking.internState) ? true : false;
    }

    /**
     * Gives the extern representation of default state: "0"
     */
    public String defaultToString() {
        return "0";
    } // public String defaultToString( )

    /**
     * Gives the extern representation of this marking. <br>
     */
    private String externRepresentation() {
        return String.valueOf(internState);
    }

    /**
           Represents string <code>str</code> a natural number?
     */
    public boolean isToken(String str) {
        try {
            new Integer(str); return true;
        } catch (NumberFormatException e)
        {return false; }
    }

    protected boolean isValid() {
        String str = toString();
        return isValid(str);
    }

    protected boolean isValid(Extendable extendable) {
        /**
                Nur fuer Stellen zulaessig!
         */
        return false;
    }

    protected boolean isValid(String str) {
        if (str == "0") return true;
        else if (isToken(str)) return true;
        return false;
    }

    /**
     * Adds the value of this marking to the value of markig
     * <code>m</code>.
     */
    public void localAdd(Marking m) {
        NaturalNumber marking = toNaturalNumber(m);
        // interne Repraesentation bestimmen
        internState += marking.internState;
        setExternState(externRepresentation());
    } // void add(NaturalNumber marking)

    /**
     * Internal state (value) gets the natural number represented
     * by the external representation of this marking. <BR>
     * If the external representation is not a number the
     * value is zero.
     */
    protected void localParse() {
        String str = toString(); // str externe Darstellung
        // System.out.println("NatNumb.parse: " + str);
        try {
            internState = (new Integer(str)).intValue();
        } catch (NumberFormatException e)
        {internState = 0; }
    }

    /**
     * Requires: This marking contains marking <code>m</code>.
     * No error message, if not!
     */
    public void localSub(Marking m) {
        NaturalNumber marking = toNaturalNumber(m);
        internState -= marking.internState;
        if (internState < 0) internState = 0;
        setExternState(externRepresentation());
    }

    /**
     * Converts marking <code>m</code> a marking of type
     * NaturalNumber.
     */
    private NaturalNumber toNaturalNumber(Marking m) {
        try
        {return (NaturalNumber)m; } catch (ClassCastException e) {
            System.out.println("Incompatible marking type");
            return new NaturalNumber(getExtendable()); // empty set of token
        }
    }
} //  public class NaturalNumber
