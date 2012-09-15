package de.huberlin.informatik.pnk.netElementExtensions.llNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
 */

import de.huberlin.informatik.pnk.app.base.*;
import java.util.*;

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;

import de.huberlin.informatik.pnk.appControl.ApplicationControl;

public class SimpleRule extends Extension implements FiringRule {
    /**
     * Constructor specifying the extended extendable (a net).
     */
    public SimpleRule(Extendable ext) {
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
        int i = 1;
        while (e.hasMoreElements()) {
            Edge edge = (Edge)e.nextElement();
            try {
                //System.out.println("Kante: " + i++);
                Inscription inscription = (Inscription)edge.getExtension("inscription");
                Marking mInscription = inscription.evaluate();
                Place place = (Place)edge.getTarget();
                Marking mPlace = place.getMarking();
                mPlace.add(mInscription);
                //place.updateExtension(null, "marking", mPlace.toString());
            } catch (ClassCastException exept) {
                System.out.println("Simple Rule: Inscription  is not a Marking");
                throw (new NetSpecificationException("Simple Rule: Inscription  is not a Marking"));
            }
        }
    }

    /**
     * Parses all Extensions with an internal value depending on
     * (possibly edited) other extensions.
     */
    public void checkContextAndParseExtensions() {
        checkContextAndParseMarkings();
        // parse inscription of arcs
        Net net = (Net)getGraph();
        Vector vpl = net.getArcs();
        Enumeration e = vpl.elements();
        while (e.hasMoreElements()) {
            Arc a = (Arc)(e.nextElement());
            try {
                Marking m = (Marking)(a.getExtension("inscription"));
                if (m != null) m.checkContextAndParse();
            } catch (ClassCastException exept) {
                System.out.println("Inscription is not a Marking");
                throw (new NetSpecificationException("Simple Rule: Inscription  is not a Marking"));
            }
        }
    }

    /**
     * Parses markings using the extension of places and this net.
     */
    public void checkContextAndParseMarkings() {
        Net net = (Net)getGraph();
        Vector vpl = net.getPlaces();
        Enumeration e = vpl.elements();
        while (e.hasMoreElements()) {
            Place pl = (Place)(e.nextElement());
            Marking m = (Marking)(pl.getExtension("marking"));
            m.checkContextAndParse();
            m = (Marking)(pl.getExtension("initialMarking"));
            m.checkContextAndParse();
        }
    }

    /**
     * Fires the given <code>transition</code>.
     */
    public void fire(Transition transition) {
        // Marken von den Vorplaetzen abziehen
        subMarkings(transition);
        // Marken auf Nachplaetzen hinzufuegen
        addMarkings(transition);
    } // public void fire

    /**
     * Fires a set ({@link #isStep step}) of {@link Transition
     * transitions}. <BR>

     */
    public void fire(Vector transitions) {
        Enumeration e = transitions.elements();
        while (e.hasMoreElements()) {
            Transition tr = (Transition)e.nextElement();
            fire(tr);
        }
    }

    /**
     * Gets the set of all {@link #isConcessioned concessioned} {@link
     * Transition transitions}. <BR>
     */
    public Vector getAllConcessioned() {
        Vector activTransitions = new Vector(5);
        Net net = (Net)getExtendable();
        Vector transitions = net.getTransitions();
        Enumeration e = transitions.elements();
        while (e.hasMoreElements()) {
            Transition tr = (Transition)e.nextElement();
            if (isConcessioned(tr)) {
                // transitions.removeElement(tr);
                activTransitions.addElement(tr);
            }
        }
        return activTransitions;
    }

    /**
     * Gets the set of all {@link #isConcessioned concessioned} {@link
     * Transition transitions}, which are in the set
     * <code>inclTrans</code> and not in <code>exclTrans</code>.

     */
    public Vector getAllConcessioned(Vector inclTrans, Vector exclTrans) {
        Vector transitions = new Vector(5);
        Enumeration e = inclTrans.elements();
        while (e.hasMoreElements()) {
            Transition tr = (Transition)e.nextElement();
            if (!exclTrans.contains(tr))
                if (isConcessioned(tr)) transitions.addElement(tr);
        }
        return transitions;
    }

    /**
     * Gets the set of all simultaneously fireable sets ({@link #isStep
     * steps}) of {@link Transition transitions}. <BR>
     * @see #getStep()
     */
    public Vector getAllSteps()
    {return null; }
    /**
     * Gets the set of all sets ({@link #isStep steps}) of
     * simultaneously fireable {@link Transition transitions}. <br>

     */
    public Vector getAllSteps(Vector inclTrans, Vector exclTrans) {return null; }
    /**
     * Gets  a {@link #isConcessioned concessioned}  transition.
     */
    public Transition getConcessioned() {
        Net net = (Net)getExtendable();
        Vector transitions = net.getTransitions();
        Enumeration e = transitions.elements();
        while (e.hasMoreElements()) {
            Transition tr = (Transition)e.nextElement();
            if (isConcessioned(tr)) return tr;
        }
        return null;
    }

    /**
     * Gets  a {@link #isConcessioned concessioned}
     * transition, which is in set
     * <code>inclTrans</code> and not in set <code>exclTrans</code>. <BR>
     */
    public Transition getConcessioned(Vector inclTrans, Vector exclTrans)
    {return null; }
    /**
     * Gets a simultaneously fireable set ({@link #isStep step}) of
     * {@link Transition transitions}. <BR>

     */
    public Vector getStep()
    {return null; }
    /**
     * Gets a simultaneously fireable set {@link #isStep step} of
     * {@link Transition transitions}. <br>

     */
    public Vector getStep(Vector transitions)
    {return null; }
    /**
     * Returns whether the given
     * <code>transition</code> is concessioned. <BR>
     */
    public boolean isConcessioned(Transition transition) {
        Vector edges = transition.getIncomingEdges();
        Enumeration e = edges.elements();
        while (e.hasMoreElements()) {
            Edge edge = (Edge)e.nextElement();
            try {
                Marking mInscription = (Marking)edge.getExtension("inscription");
                Place place = (Place)edge.getSource();
                Marking mPlace = place.getMarking();
                if (!mPlace.contains(mInscription)) {return false; }
            } catch (ClassCastException exept) {
                System.out.println("Inscription is not a Marking");
                throw (new NetSpecificationException("Simple Rule: Inscription  is not a Marking"));
            }
        }
        return true;
    } // boolean isConcessioned

    /**
     * Returns whether a set of {@link Transition transitions} is
     * simultaneously fireable. <BR>
     */
    public boolean isStep(Vector transitions)
    {return false; }
    protected boolean isValid() {return false; }
    protected boolean isValid(Extendable e) {return false; }
    protected boolean isValid(String str) {return false; }
    protected void parse() {}
    public void simulateWithUserInteraction(MetaApplication app) {
        checkContextAndParseExtensions();
        ApplicationControl ac = app.getApplicationControl();
        Net net = (Net)getGraph();
        Vector conc = getAllConcessioned(); //all concessioned transitions

        if (conc == null || conc.isEmpty())
            return;
        Transition tr = (Transition)
                        (new SelectObjectAction(ac, net, app, conc)).invokeAction();

        while (tr != null && app.letrun) {
            fire(tr);
            conc = getAllConcessioned();
            if (conc == null || conc.isEmpty())
                return;
            tr = (Transition)
                 (new SelectObjectAction(ac, net, app, conc)).invokeAction();
        }
    }

    public void simulateWithoutUserInteraction(MetaApplication app) {
        checkContextAndParseExtensions();

        ApplicationControl ac =
            app.getApplicationControl();

        Random integerGenerator =
            new java.util.Random();

        Net net = (Net)getGraph();

        Vector conc = getAllConcessioned();

        if (conc == null || conc.isEmpty()) {
            return;
        }
        Transition tr = (Transition)
                        conc.get(integerGenerator.nextInt(conc.size()));

        while (tr != null && app.letrun) {
            synchronized (this) {
                (new EmphasizeObjectsAction(ac, net, app, tr)).invokeAction();
                fire(tr);
                try {
                    wait(1000);
                } catch (Exception e) {}
                (new ResetEmphasizeAction(ac, net, app)).invokeAction();
            }

            conc = getAllConcessioned();
            if (conc == null || conc.isEmpty())
                return;
            tr = (Transition)
                 conc.get(integerGenerator.nextInt(conc.size()));
        }
    }

/**
 * First step to fire <code>transition</code>:
 * Subtract markings from the pre-set of <code>transition</code>.
 */
    public void subMarkings(Transition transition) {
        Vector edges = transition.getIncomingEdges();
        // Marken von Vorplaetzen abziehen
        Enumeration e = edges.elements();
        while (e.hasMoreElements()) {
            Edge edge = (Edge)e.nextElement();
            try {
                Marking mInscription = (Marking)edge.getExtension("inscription");
                Place place = (Place)edge.getSource();
                Marking mPlace = place.getMarking();
                mPlace.sub(mInscription);
                //place.updateExtension(null, "marking", mPlace.toString());
            } catch (ClassCastException exept) {
                System.out.println("Inscription is not a Marking");
                throw (new NetSpecificationException("Simple Rule: Inscription is not a Marking"));
            }
        }
    }
} // public class SimpleRule
