package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Inscription.java is part of the
   Petri Net Kernel Java reimplementation.
   Inscription.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: HLRule.java,v $
   Revision 1.7  2001/10/11 16:59:00  oschmann
   Neue Release

   Revision 1.5  2001/06/12 07:03:53  oschmann
   Neueste Variante...

   Revision 1.4  2001/05/11 17:23:05  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.3  2001/03/28 08:05:43  hohberg
   Implementation of Inscription variables of subrange type

   Revision 1.2  2001/02/27 13:34:30  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:09:56  hohberg
   New package structure

   Revision 1.4  2001/02/19 14:30:24  hohberg
   New structure of GHS simulator

   Revision 1.3  2001/02/15 13:03:57  hohberg
   New method checkContextAndParse()

   Revision 1.2  2001/02/02 08:11:52  hohberg
   New representation of token type (Hohberg)

   Revision 1.1  2001/01/30 14:32:22  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)

 */

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;
import de.huberlin.informatik.pnk.netElementExtensions.llNet.SimpleRule;
import java.util.*;

/**
 * The {@link FiringRule firing rule} of a {@link Net Petri Net} is a
 * standard {@link Extension extension}. <br>
 * The HLRule requires
 * an {@link Arc arc} inscription of type {@link HLInscription}
 * and a net extension "signature" of type Signature.
 * (No special type of {@link Marking} is required.)
 */
public class HLRule extends SimpleRule {
    /**
     * Constructor specifying the extended extendable (a net).
     */
    public HLRule(Extendable ext) {
        super(ext);
    }

    /**
     * Second step to fire <code>transition</code>:
     * Add markings to the post-set of <code>transition</code>.
     */
    public void addMarkings(Transition transition) {
        // Marken auf Nachplaetzen hinzufuegen
        Vector edges = transition.getOutgoingEdges();
        Enumeration e = edges.elements();
        Inscription inscription = null;
        int i = 1;
        while (e.hasMoreElements()) {
            try {
                Edge edge = (Edge)e.nextElement();
                Place place = (Place)edge.getTarget();
                Marking mPlace = place.getMarking();
                //System.out.println("Arc to " + place.getName()+ mPlace);
                inscription =
                    (Inscription)edge.getExtension("inscription");
                if (inscription == null) {
                    throw(new NetSpecificationException
                              ("Error: No inscription defined"));
                }
                mPlace.add(inscription.evaluate());
            } catch (ClassCastException exept) {
                throw(new NetSpecificationException
                          ("Inscription " + inscription + " is not the name of an Inscription  class"));
            }
        }
    }

    /**
     * Parses all Extensions with an internal value depending on
     * (possibly edited) other extensions.
     * For one transition are dependences of inscription variables
     * (on incomming edges) and inscription expressions.
     * Semantic is determined by the signature of the net.
     */
    public void checkContextAndParseExtensions() {
        super.checkContextAndParseMarkings();
        Net net = (Net)getGraph();
        Signature sig = (Signature)net.getExtension("signature");
        sig.parseAllInscriptions();
    }

    /**
     * Variables get its first value. <br>
     */
    protected boolean initVariables(Vector variables) {
        int length = variables.size();
        for (int i = 0; i < length; i++) {
            System.out.println("initVariables " + i);
            InscriptionVariable tv = (InscriptionVariable)variables.elementAt(i);
            if (!tv.initValue()) return false;
        }
        return true;
    }

    /**
     * Given is a set of inscriptions which know its source node, a place.
     * Returns true if for all inscriptions the source node contains the marking
     * computed by the inscription. <br>
     * Requires: Variables are instantiated.
     */
    protected boolean isActivated(Vector functions) {
        int length = functions.size();
        for (int i = 0; i < length; i++) {
            //System.out.println("isActivated "+i);
            HLInscription inscr =
                (HLInscription)functions.elementAt(i);
            Place place = ((Arc)inscr.getExtendable()).getPlace();
            Marking m = (Marking)place.getExtension("marking");
            System.out.println("Marking on Place: " + place.getName() + m);
            Marking inscrMarking = inscr.evaluate();
            System.out.println("Marking of inscription function: "
                               + inscrMarking);
            if (!m.contains(inscrMarking)) return false;
        }
        return true;
    }

    /**
     * Returns whether the given
     * <code>transition</code> is concessioned. <BR>
     */
    public boolean isConcessioned(Transition transition) {
        Vector edges = transition.getIncomingEdges();
        Vector variables = new Vector(3); // InscriptionVariables
        Vector functions = new Vector(3); // Inscription (not variables)
        HLInscription hlIns = null;
        Enumeration e = edges.elements();
        // Select variables and functions
        System.out.println("isConcessioned transition? " + transition.getName());
        while (e.hasMoreElements()) {
            Edge edge = (Edge)e.nextElement();
            try {
                hlIns = (HLInscription)edge.getExtension("inscription");
                if (hlIns == null) {
                    throw(new NetSpecificationException
                              ("Error: No inscription defined on arc to " + transition.getName()));
                }
                if (hlIns.isVariable()) { // select variable only once!
                    Object var = hlIns.getVariable();
                    if (variables.contains(var))
                        functions.addElement(hlIns);
                    else
                        variables.addElement(var);
                } else // not variable
                    functions.addElement(hlIns);
            } catch (ClassCastException exept) {
                throw(new NetSpecificationException("Inscription" + hlIns + " is not a HLInscription: Arc to " +
                                                    transition.getName()));
            }
        }
        if (!initVariables(variables)) return false;  //empty marking
        if (functions.size() == 0) return true;
        do {
            if (isActivated(functions)) return true;
        } while (variablesNextValue(variables, 0));
        return false;
    } // boolean isConcessioned

    /**
     * First step to fire <code>transition</code>:
     * Subtract markings from the pre-set of <code>transition</code>.
     */
    public void subMarkings(Transition transition) {
        int i = 0;
        Inscription inscription = null;
        Vector edges = transition.getIncomingEdges();
        // Marken von Vorplaetzen abziehen
        Enumeration e = edges.elements();
        while (e.hasMoreElements()) {
            Edge edge = (Edge)e.nextElement();
            try {
                //System.out.println("Kante: " + i++);
                inscription = (Inscription)edge.getExtension("inscription");
                if (inscription == null) {
                    throw(new NetSpecificationException
                              ("Error: No inscription defined"));
                }
                Place place = (Place)edge.getSource();
                Marking mPlace = place.getMarking();
                mPlace.sub(inscription.evaluate());
            } catch (ClassCastException exept) {
                throw(new NetSpecificationException
                          ("Inscription " + inscription + " is not a Inscription  class"));
            }
        }
    } // public void subMarkings(

    /**
     * All variables are initialised.
     * Assign the next value to the i-th variable, if exist esle init
     * and next value to variable i+1 if possible and so on
     * variable at 0 is like the digits (Einer)
     */
    protected boolean variablesNextValue(Vector variables, int i) {
        System.out.println("variablesNextValue");
        InscriptionVariable tv = (InscriptionVariable)variables.elementAt(i);
        if (tv.setNextValue()) return true;
        // No other variables?
        if (variables.size() == i + 1) return false;
        // Start with first value and next value of the others, if exist
        tv.initValue(); // all variables have at least one value
        return variablesNextValue(variables, i + 1);
    }
} // public class SimpleRule
