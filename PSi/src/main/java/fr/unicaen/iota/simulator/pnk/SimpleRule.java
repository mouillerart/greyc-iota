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
package fr.unicaen.iota.simulator.pnk;

/*
 * Petri Net Kernel,
 * Copyright 1996-1999 Petri Net Kernel Team, Petri Net Technology Group,
 */
import de.huberlin.informatik.pnk.app.base.EmphasizeObjectsAction;
import de.huberlin.informatik.pnk.app.base.MetaApplication;
import de.huberlin.informatik.pnk.app.base.ResetEmphasizeAction;
import de.huberlin.informatik.pnk.app.base.SelectObjectAction;
import de.huberlin.informatik.pnk.exceptions.NetSpecificationException;
import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.FiringRule;
import de.huberlin.informatik.pnk.netElementExtensions.base.Inscription;
import de.huberlin.informatik.pnk.netElementExtensions.base.Marking;
import fr.unicaen.iota.simulator.app.EPC;
import fr.unicaen.iota.simulator.util.Config;
import fr.unicaen.iota.simulator.util.StatControler;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SimpleRule extends Extension implements FiringRule {

    private static final Log log = LogFactory.getLog(SimpleRule.class);

    /**
     * Constructor specifying the extended extendable (a net).
     */
    public SimpleRule(Extendable ext) {
        super(ext);
    }

    /**
     * Second step to fire <code>transition</code>: Add markings to the post-set of <code>transition</code>.
     */
    public void addMarkings(Transition transition) {
        // Marken auf Nachplaetzen hinzufuegen
        Collection<Edge> edges = transition.getOutgoingEdges();
        for (Edge edge : edges) {
            try {
                Inscription inscription = (Inscription) edge.getExtension("inscription");
                Marking mInscription = inscription.evaluate();
                Place place = (Place) edge.getTarget();
                Marking mPlace = place.getMarking();
                EventFileSaver efs = (EventFileSaver) edge.getTarget().getExtension("eventfilesaver");
                EPCDeactivator ed = (EPCDeactivator) edge.getTarget().getExtension("epcdeactivator");
                if (efs.isOn()) { // is there any event to save ?
                    if (Config.saveEvents) {
                        efs.save(mInscription); // TODO BEFORE mplace.add !!!!
                    }
                }
                if (ed.isDeactivator()) {
                    EPCInscription epcInscription = (EPCInscription) inscription;
                    ed.sendToTrash(epcInscription.getEpcList());
                    epcInscription.clearEpcList();
                } else {
                    Pipe pipe = (Pipe) edge.getTarget().getExtension("pipe");
                    if (pipe.isPipe() && pipe.getType() == Pipe.Type.EXPEDITION) {
                        pipe.send(mInscription);
                    } else {
                        mPlace.add(mInscription);
                    }
                }
            } catch (ClassCastException exept) {
                throw (new NetSpecificationException("Simple Rule: Inscription  is not an (EPC)Inscription"));
            }
        }
    }

    /**
     * Parses all Extensions with an internal value depending on (possibly edited) other extensions.
     */
    @Override
    public void checkContextAndParseExtensions() {
        checkContextAndParseMarkings();
        // parse inscription of arcs
        Net net = (Net) getGraph();
        Collection<Arc> vpl = net.getArcs();
        for (Arc a : vpl) {
            try {
                Marking m = (Marking) (a.getExtension("inscription"));
                if (m != null) {
                    m.checkContextAndParse();
                }
                m = (Marking) (a.getExtension("subscription"));
                if (m != null) {
                    m.checkContextAndParse();
                }
            } catch (ClassCastException exept) {
                throw (new NetSpecificationException("Simple Rule: Inscription  is not a Marking"));
            }
        }
    }

    /**
     * Parses markings using the extension of places and this net.
     */
    public void checkContextAndParseMarkings() {
        Net net = (Net) getGraph();
        Collection<Place> vpl = net.getPlaces();
        for (Place pl : vpl) {
            Marking m = (Marking) pl.getExtension("marking");
            m.checkContextAndParse();
            m = (Marking) pl.getExtension("initialMarking");
            m.checkContextAndParse();
            m = (Marking) pl.getExtension("epcgenerator");
            m.checkContextAndParse();
            m = (Marking) pl.getExtension("epcdeactivator");
            m.checkContextAndParse();
            m = (Marking) pl.getExtension("limitor");
            m.checkContextAndParse();
            m = (Marking) pl.getExtension("pipe");
            m.checkContextAndParse();
            m = (Marking) pl.getExtension("eventfilesaver");
            m.checkContextAndParse();
        }
    }

    /**
     * Fires the given <code>transition</code>.
     */
    public void fire(Transition transition) {
        // Marken von den Vorplaetzen abziehen
        subMarkings(transition);

        // petrinet EPC transfert arc to arc
        arcToArcEPCTransfert(transition);

        // EPC Network messages
        addMarkings(transition);
    } // public void fire

    /**
     * Fires a set ({@link #isStep step}) of {@link Transition transitions}.
     */
    @Override
    public void fire(Vector transitions) {
        Enumeration e = transitions.elements();
        while (e.hasMoreElements()) {
            Transition tr = (Transition) e.nextElement();
            fire(tr);
        }
    }

    /**
     * Gets the set of all {@link #isConcessioned concessioned} {@link Transition transitions}.
     */
    @Override
    public Vector getAllConcessioned() {
        Vector<Transition> activTransitions = new Vector<Transition>(5);
        Net net = (Net) getExtendable();
        for (Transition tr : (Collection<Transition>) net.getTransitions()) {
            if (isConcessioned(tr)) {
                // transitions.removeElement(tr);
                activTransitions.addElement(tr);
            }
        }
        return activTransitions;
    }

    /**
     * Gets the set of all {@link #isConcessioned concessioned} {@link Transition transitions},
     * which are in the set <code>inclTrans</code> and not in <code>exclTrans</code>.
     */
    @Override
    public Vector getAllConcessioned(Vector inclTrans, Vector exclTrans) {
        Vector<Transition> transitions = new Vector<Transition>(5);
        for (Transition tr : (Collection<Transition>) inclTrans) {
            if (!exclTrans.contains(tr) && isConcessioned(tr)) {
                transitions.addElement(tr);
            }
        }
        return transitions;
    }

    /**
     * Gets the set of all simultaneously fireable sets ({@link #isStep
     * steps}) of {@link Transition transitions}.
     * @see #getStep()
     */
    @Override
    public Vector getAllSteps() {
        return null;
    }

    /**
     * Gets the set of all sets ({@link #isStep steps}) of simultaneously
     * fireable {@link Transition transitions}.
     */
    @Override
    public Vector getAllSteps(Vector inclTrans, Vector exclTrans) {
        return null;
    }

    /**
     * Gets a {@link #isConcessioned concessioned} transition.
     */
    @Override
    public Transition getConcessioned() {
        Net net = (Net) getExtendable();
        Collection<Transition> transitions = net.getTransitions();
        for (Transition tr : transitions) {
            if (isConcessioned(tr)) {
                return tr;
            }
        }
        return null;
    }

    /**
     * Gets a {@link #isConcessioned concessioned} transition, which is in set
     * <code>inclTrans</code> and not in set <code>exclTrans</code>. <BR>
     */
    @Override
    public Transition getConcessioned(Vector inclTrans, Vector exclTrans) {
        return null;
    }

    /**
     * Gets a simultaneously fireable set ({@link #isStep step}) of {@link Transition transitions}.
     */
    @Override
    public Vector getStep() {
        return null;
    }

    /**
     * Gets a simultaneously fireable set {@link #isStep step} of {@link Transition transitions}.
     */
    @Override
    public Vector getStep(Vector transitions) {
        return null;
    }

    /**
     * Returns whether the given <code>transition</code> is concessioned.
     */
    @Override
    public boolean isConcessioned(Transition transition) {
        Collection<Edge> edgesIn = transition.getIncomingEdges();
        for (Edge edge : edgesIn) {
            try {
                Marking mInscription = (Marking) edge.getExtension("inscription");
                Place place = (Place) edge.getSource();
                Marking mPlace = place.getMarking();
                Pipe pipe = (Pipe) place.getExtension("pipe");
                if (pipe.isPipe() && pipe.getType() == Pipe.Type.RECEPTION) {
                    ((EPCList) mPlace).loadPipe(pipe);
                }
                if (!mPlace.contains(mInscription) && !((Marking) place.getExtension("epcgenerator")).contains(mInscription)) {
                    return false;
                }
            } catch (ClassCastException exept) {
                throw (new NetSpecificationException("Simple Rule: Inscription  is not a Marking"));
            }
        }
        Collection<Edge> edgesOut = transition.getOutgoingEdges();
        for (Edge edge : edgesOut) {
            try {
                EPCInscription mInscription = (EPCInscription) edge.getExtension("inscription");
                Place place = (Place) edge.getTarget();
                EPCList mPlace = (EPCList) place.getMarking();
                int epcNumber = mPlace.getEpcList().size();
                Limitor limitor = (Limitor) place.getExtension("limitor");
                if (epcNumber + mInscription.getCanalSize() > limitor.getValue()) {
                    return false;
                }
                Pipe pipe = (Pipe) place.getExtension("pipe");
                if ((pipe.isPipe() && pipe.getType() == Pipe.Type.EXPEDITION && !pipe.reserve(mInscription.getCanalSize()))) {
                    return false;
                }
            } catch (ClassCastException exept) {
                throw (new NetSpecificationException("Simple Rule: Inscription  is not an EPCInscription"));
            }
        }
        return true;
    } // boolean isConcessioned

    /**
     * Returns whether a set of {@link Transition transitions} is simultaneously fireable.
     */
    @Override
    public boolean isStep(Vector transitions) {
        return false;
    }

    @Override
    protected boolean isValid() {
        return false;
    }

    @Override
    protected boolean isValid(Extendable e) {
        return false;
    }

    @Override
    protected boolean isValid(String str) {
        return false;
    }

    protected void parse() {
    }

    @Override
    public void simulateWithUserInteraction(MetaApplication app) {
        checkContextAndParseExtensions();
        de.huberlin.informatik.pnk.appControl.ApplicationControl ac = app.getApplicationControl();
        Net net = (Net) getGraph();
        Vector conc = getAllConcessioned(); //all concessioned transitions

        if (conc == null || conc.isEmpty()) {
            return;
        }
        Transition tr = (Transition) (new SelectObjectAction(ac, net, app, conc)).invokeAction();

        while (tr != null && app.letrun) {
            fire(tr);
            conc = getAllConcessioned();
            if (conc == null || conc.isEmpty()) {
                return;
            }
            tr = (Transition) (new SelectObjectAction(ac, net, app, conc)).invokeAction();
        }
    }

    @Override
    public void simulateWithoutUserInteraction(MetaApplication app) {
        checkContextAndParseExtensions();
        de.huberlin.informatik.pnk.appControl.ApplicationControl ac = app.getApplicationControl();
        Random integerGenerator = new java.util.Random();
        Net net = (Net) getGraph();
        while (app.letrun) {
            Vector conc = getAllConcessioned();
            if (conc == null) {
                return;
            } else if (!conc.isEmpty()) {
                Transition tr = (Transition) conc.get(integerGenerator.nextInt(conc.size()));
                synchronized (this) {
                    (new EmphasizeObjectsAction(ac, net, app, tr)).invokeAction();
                    fire(tr);
                    try {
                        wait(Config.animation_speed);
                    } catch (Exception e) {
                        /* */
                    }
                    (new ResetEmphasizeAction(ac, net, app)).invokeAction();
                }
            } else {
                try {
                    synchronized (this) {
                        this.wait(Config.animation_speed);
                    }
                } catch (Exception e) {
                    /* */
                }
            }
        }
    }

    /**
     * First step to fire <code>transition</code>: Subtract markings from the pre-set of <code>transition</code>.
     */
    public void subMarkings(Transition transition) {
        Collection<Edge> edges = transition.getIncomingEdges();
        // Marken von Vorplaetzen abziehen
        for (Edge edge : edges) {
            try {
                Marking mInscription = (Marking) edge.getExtension("inscription");
                Place place = (Place) edge.getSource();
                EPCList mPlace = (EPCList) place.getMarking();
                EPCGenerator epcGenerator = (EPCGenerator) place.getExtension("epcgenerator");
                if (epcGenerator.isGenerator()) {
                    if (epcGenerator.useKeyGen()) {
                        epcGenerator.generate(mPlace);
                    }
                    StatControler.addGeneratedObject();
                }
                mPlace.sub(mInscription);
                //place.updateExtension(null, "marking", mPlace.toString());
            } catch (ClassCastException exept) {
                throw (new NetSpecificationException("Simple Rule: Inscription is not a Marking"));
            }
        }
    }

    private void arcToArcEPCTransfert(Transition transition) {
        List<Edge> edgesIn = transition.getIncomingEdges();
        List<Edge> edgesOut = transition.getOutgoingEdges();

        if (edgesIn.size() > 1 && edgesOut.size() == 1) { // Agregation !
            List<EPC> epcGenerated = new ArrayList<EPC>();
            List<EPC> epcList = new ArrayList<EPC>();
            for (Edge edgeIn : edgesIn) {
                try {
                    EPCGenerator epcGenerator = (EPCGenerator) edgeIn.getSource().getExtension("epcgenerator");
                    EPCInscription mInscriptionIn = (EPCInscription) edgeIn.getExtension("inscription");
                    if (epcGenerator.isGenerator()) {
                        if (mInscriptionIn.getEpcList().size() > 1) {
                            log.warn("la génération de code EPC sur un aggregation event ne peut engendrer qu'un code EPC => WARNING");
                        }
                        epcGenerated.addAll(mInscriptionIn.getEpcList());
                    } else {
                        epcList.addAll(mInscriptionIn.getEpcList());
                    }
                    mInscriptionIn.clearEpcList();
                } catch (ClassCastException exept) {
                    throw (new NetSpecificationException("Simple Rule: Inscription is not an EPCInscription"));
                }
            }
            if (epcGenerated.isEmpty()) {
                log.error("pas de génération d'epc pour cette action d'aggregation => ERROR");
                return;
            }
            for (Edge edgeOut : edgesOut) {
                try {
                    EPCInscription mInscriptionOut = (EPCInscription) edgeOut.getExtension("inscription");
                    mInscriptionOut.setEpcList(epcGenerated);
                } catch (ClassCastException exept) {
                    throw (new NetSpecificationException("Simple Rule: Inscription is not an EPCInscription"));
                }
            }
            sendEvent(transition, epcList, epcGenerated.get(0));
        } else if (edgesOut.size() == 2 && edgesIn.size() == 1) { // DisAgregation !
            List<EPC> epcList = new ArrayList<EPC>();
            Edge edgeIn = edgesIn.get(0);
            EPCInscription mInscriptionIn = (EPCInscription) edgeIn.getExtension("inscription");
            epcList.addAll(mInscriptionIn.getEpcList());
            mInscriptionIn.clearEpcList();

            EPCInscription childWay = null;
            EPCInscription parentWay = null;
            for (Edge edgeOut : edgesOut) {
                EPCInscription mInscriptionOut = (EPCInscription) edgeOut.getExtension("inscription");
                EPCSubscription mSubscriptionOut = (EPCSubscription) edgeOut.getExtension("subscription");
                if (mSubscriptionOut.isUnderSubscription()) {
                    parentWay = mInscriptionOut;
                } else {
                    childWay = mInscriptionOut;
                }
            }
            if (parentWay == null) {
                log.error("no subscription found for parentId during disagreggation -> ERROR");
                return;
            }
            for (EPC epc : epcList) {
                childWay.addEpcList(epc.getChildren());
                epc.clearChildren();
            }
            parentWay.setEpcList(epcList);
            for (EPC epc : epcList) {
                sendEvent(transition, epc.getChildren(), epc);
            }
        } else {
            Edge edgeIn = edgesIn.get(0);
            EPCInscription mInscriptionIn = (EPCInscription) edgeIn.getExtension("inscription");
            Collection<EPC> epcList = new ArrayList<EPC>(mInscriptionIn.getEpcList());
            mInscriptionIn.clearEpcList();
            for (Edge edgeOut : edgesOut) {
                try {
                    EPCInscription mInscriptionOut = (EPCInscription) edgeOut.getExtension("inscription");
                    mInscriptionOut.setEpcList(epcList);
                    sendEvent(transition, epcList);
                } catch (ClassCastException exept) {
                    throw (new NetSpecificationException("Simple Rule: Inscription is not an EPCInscription"));
                }
            }
        }
    }

    private void sendEvent(Transition transition, Collection<EPC> epcList) {
        Map<String, Object> extIdToObj = transition.getExtIdToObject();
        Event event = (Event) extIdToObj.get("event");
        if (event != null) {
            event.publish(epcList);
        }
    }

    private void sendEvent(Transition transition, Collection<EPC> epcList, EPC parent) {
        Map<String, Object> extIdToObj = transition.getExtIdToObject();
        Event event = (Event) extIdToObj.get("event");
        if (event != null) {
            event.publish(epcList, parent);
        }
    }
} // public class SimpleRule
