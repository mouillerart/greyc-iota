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
   $Log: HLRulePlusGuard.java,v $
   Revision 1.7  2001/10/11 16:59:01  oschmann
   Neue Release

   Revision 1.5  2001/06/12 07:03:54  oschmann
   Neueste Variante...

   Revision 1.4  2001/06/04 15:29:44  efischer
 *** empty log message ***

   Revision 1.3  2001/05/11 17:23:06  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.2  2001/02/27 13:34:30  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:09:56  hohberg
   New package structure

   Revision 1.4  2001/02/19 14:30:24  hohberg
   New structure of GHS simulator

   Revision 1.3  2001/02/15 13:03:57  hohberg
   New method checkContextAndParse()

   Revision 1.2  2001/02/05 13:25:24  hohberg
 *** empty log message ***

   Revision 1.1  2001/01/30 14:32:23  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)

 */

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;
import java.util.*;

/**
 * The {@link de.huberlin.informatik.pnk.netElementExtensions.base.FiringRule firing rule} of a {@link Net Petri Net} is a
 * standard {@link Extension extension}. <br>
 * The HLRulePlusGuard requires the extension from HLRule and
 * an {@link Transition transition} extension "guard" of type {@link HLGuard}.
 */
public class HLRulePlusGuard extends HLRule {
    /**
     * Constructor specifying the extended extendable (a net).
     */
    public HLRulePlusGuard(Extendable ext) {
        super(ext);
    }

    /**
     * Parses all Extensions with an internal value depending on
     * (possibly edited) other extensions.
     * For one transition are dependences of inscription variables
     * (on incomming edges) and inscription expressions and guards!
     * Transitions have guards.
     * Semantic is determined by the signature of the net.
     */
    public void checkContextAndParseExtensions() {
        Transition tr;
        HLGuard guard = null;
        super.checkContextAndParseMarkings();
        Net net = (Net)getGraph();
        Signature sig = (Signature)net.getExtension("signature");
        Vector transitions = net.getTransitions();
        Enumeration e = transitions.elements();
        while (e.hasMoreElements()) {
            tr = (Transition)e.nextElement();
            System.out.println("Parse Inscr. for transition " + tr.getName());
            sig.parseInscriptions(tr);
            // parse guard
            try {
                guard = (HLGuard)tr.getExtension("guard");
                if (guard == null)
                    throw(new NetSpecificationException("HLRulePlusGuard: No guard defined for Transitions "));
            } catch (ClassCastException exept) {
                throw(new NetSpecificationException("HLRulePlusGuard: Guard " + guard + " is not a HLGuard class"));
            }
            guard.parseGuard(sig);
        }
    }

    /**
     * Returns whether the given
     * <code>transition</code> is concessioned. <BR>
     */
    public boolean isConcessioned(Transition transition) {
        HLGuard guard;
        try {
            guard = (HLGuard)transition.getExtension("guard");
            if (guard == null)
                throw(new NetSpecificationException("HLRulePlusGuard: No guard defined for Transitions "));
        } catch (ClassCastException exept) {
            throw(new NetSpecificationException
                      ("HLRulePlusGuard: Guard of transitions  " +
                      "defines not a HLGuard class"));
        }
        Vector edges = transition.getIncomingEdges();
        Vector variables = new Vector(3); // InscriptionVariables
        Vector functions = new Vector(3); // Inscription (not variables)

        Enumeration e = edges.elements();
        // Select variables and functions
        //System.out.println("isConcessioned transition? "+
        // transition.getName());
        while (e.hasMoreElements()) {
            Edge edge = (Edge)e.nextElement();
            try {
                HLInscription hlIns =
                    (HLInscription)edge.getExtension("inscription");
                if (hlIns == null)
                    throw(new NetSpecificationException(
                              "HLRulePlusGuard: No inscription defined for arcs "));
                if (hlIns.isVariable()) { // select variable only once!
                    Object var = hlIns.getVariable();
                    if (variables.contains(var))
                        functions.addElement(hlIns);
                    else
                        variables.addElement(var);
                } else // not variable
                    functions.addElement(hlIns);
            } catch (ClassCastException exept) {
                throw(new NetSpecificationException("HLRulePlusGuard:" +
                                                    "Inscription is not a HLInscription class"));
            }
        }
        if (!initVariables(variables)) return false;  //empty marking
        if (functions.size() == 0) {
            do {
                if (guard.evaluate().booleanValue())
                    return true;
            } while (variablesNextValue(variables, 0));
        } else
            do {
                if (guard.evaluate().booleanValue() && isActivated(functions))
                    return true;
            } while (variablesNextValue(variables, 0));
        return false;
    } // boolean isConcessioned
} // public class HLRulePlusGuard
