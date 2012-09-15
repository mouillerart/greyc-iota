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

public class NaturalNumber1 extends NaturalNumber {
    /**
     * Constructor specifying the place. <BR>
     * The inscription is "1".
     */
    public NaturalNumber1(Extendable place) {
        super(place);
        internState = 1;
    }

    /**
     * Constructor specifying the place and the marking. <BR>
     */
    public NaturalNumber1(Extendable place, String marking) {
        super(place, marking);
    }

    /**
     * Gives the extern representation of default state: "1"
     */
    public String defaultToString() {
        return "1";
    } // public String defaultToString( )
} //  public class NaturalNumber