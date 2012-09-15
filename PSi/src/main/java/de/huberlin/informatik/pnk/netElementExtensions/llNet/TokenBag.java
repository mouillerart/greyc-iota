package de.huberlin.informatik.pnk.netElementExtensions.llNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,

   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: TokenBag.java,v $
   Revision 1.5  2001/10/11 16:59:20  oschmann
   Neue Release

   Revision 1.3  2001/06/12 07:04:10  oschmann
   Neueste Variante...

   Revision 1.2  2001/05/11 17:23:23  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.1  2001/02/22 16:11:04  hohberg
   New package structure

   Revision 1.1  2001/02/20 15:41:00  hohberg
 *** empty log message ***

 */

import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Defines a marking as a bag of {@TokenType token}. <br>
 * The tokens type is String or defined by an
 * {@Extension extension} of the extendable with id "tokenClass".
 * <br>
 * It is permitted to add or subtract a TokenSet.
   <pre>Internal representation: vector of token objects.

   External representation: [token delimiter]*
        delimiter: space | comma | nl
   </pre>
 * Example: a,a,c,c,b,c,a <br>
 * Generated representation: a,a,a,b,c,c,c
 */
public class TokenBag extends TokenSet {
    final protected static String tokenClassId = "tokenClass";

    /**
     * Constructor specifying the extendable, not the marking. <br>
     * The extendable must be a place.
     */
    public TokenBag(Extendable place) {
        super(place); // sets token type and internState calling parse
        // System.out.println("Start TokenBag:");
    }

    /**
     * Constructor specifying the extendableand the signature of the net. <br>
     * The extendable must be a place.
     */
    /**
     *  Constructor specifying the extendable, a place,
     *  and the <code>marking</code>. <br>
     */
    public TokenBag(Extendable place, String marking) {
        super(place, marking); // sets token type and internState calling parse
        // System.out.println("Start TokenSet, marking");
    }

    /**
     * Adds a token to internal and extenal state. <br>
     * Used to construct a token set for given token.
     * (Token class and representation of token is unknown.)
     */
    public void addToken(Object o) {
        setExternState(toString() + " " + o.toString());
        internState.addElement(o);
    }

    public void checkContextAndParse() {
        super.checkContextAndParse();
        sortInternState();
        // neue externe Repraesentation
        setExternState(externRepresentation());
    }

    protected void internStateAdd(Object o) {
        internState.addElement(o);
        // System.out.println("Add token " + o.toString());
    }

////////////////  Implementation of interface Marking: //////////
//                 add, sub, contains
/**
 * Add all tokens and sort
 */
    public void localAdd(Marking m) {
        TokenSet marking = toTokenSet(m);
        // load internal representation
        if (marking.internState == null)
            System.out.println("InternSt: NullPointer");
        Enumeration e = marking.internState.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            internState.addElement(o);
        }
        // System.out.println("InternSt: " +externRepresentation());
        sortInternState();
        // neue externe Repraesentation
        setExternState(externRepresentation());
    } // void localAdd(Marking m)

    /**
     * Requires: contains(marking).
     * No error message if not.
     */
    public void localSub(Marking m) {
        TokenSet marking = toTokenSet(m);
        if (marking.isEmpty()) return;
        // else subtract the internal representations
        Enumeration e = marking.internState.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            internState.removeElement(o);
        }
        sortInternState();
        setExternState(externRepresentation());
    }

    private void sortInternState() {
        Vector newRepr = new Vector(internState.size());

        Enumeration e = internState.elements();
        if (e.hasMoreElements())
            newRepr.addElement(e.nextElement());
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            for (int i = newRepr.size() - 1; i >= 0; i--) { // compare String representation
                if (o.toString().compareTo(
                        newRepr.elementAt(i).toString()) >= 0)
                {newRepr.insertElementAt(o, i + 1); o = null; break; }
            }
            if (o != null) newRepr.insertElementAt(o, 0);  // kleinstes Element
        }
        internState = newRepr;
        // setExternState (externRepresentation());
        // System.out.println("Sortierte Form: "+externRepresentation());
    }
} // class TokenBag