package de.huberlin.informatik.pnk.kernel;

import de.huberlin.informatik.pnk.kernel.base.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;
import java.util.*;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Net.java,v $
   Revision 1.15  2001/10/11 16:57:58  oschmann
   Neue Release

   Revision 1.13  2001/06/12 07:03:13  oschmann
   Neueste Variante...

   Revision 1.12  2001/05/11 17:21:53  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.11  2001/03/30 12:55:16  hohberg
   Error in informObserver() removed

   Revision 1.10  2001/03/26 08:49:17  hohberg
   New Method: informObserver()

   Revision 1.9  2001/02/27 21:29:10  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.8  2001/02/15 15:07:17  hohberg
   New method resetMarkings()

   Revision 1.7  2001/01/16 17:36:56  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:57  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:38  oschmann
   Neue Version...

   Revision 1.10  2000/09/22 08:43:52  gruenewa
 *** empty log message ***

   Revision 1.8  2000/08/30 14:22:48  hohberg
   Update of comments

   Revision 1.7  2000/08/11 09:23:10  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:25  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:47:38  hohberg
   New comments

 */

import java.util.Vector;

/**
 * A bipartitioned, directed graph with some standard {@link Extension
 * extensions} called <em> Petri Net </em>. <br>
 */
public final class Net extends Graph {
    /*
     * A vector containing a reference to each place of the net. <BR>
     */
    private Vector places = new Vector(10);

    /**
     * A vector containing a reference to each transition of the net. <BR>
     */
    private Vector transitions = new Vector(10);

    /**
     * Constructor specifying the net type. <BR>
     * @see Specification
     */
    public Net(Specification netSpec) {
        super(netSpec);
    } // public  Net( Specification netSpec)

    /**
     * Constructor specifying the net type and name. <BR>
     * @see Specification
     */
    public Net(Specification netSpec, String name) {
        super(netSpec, name);
    } // public  Net( NetSpecification netSpec, String name)

    /**
     * Gets a Vector of all {@link Arc arcs} of this net.
     */
    public Vector getArcs() {
        return getEdges();
    } // public Vector getArcs( )

    /**
     * Gets the  firing rule of this net.
     */
    public FiringRule getFiringRule() {
        return (FiringRule)getExtension("firingRule");
    } // public FiringRule getFiringRule( )

    /**
     * Gets a Vector of all {@link Place places} of this net.
     */
    public Vector getPlaces() {
        return places;
    } // public Vector getPlaces( )

    /**
     * Gets a Vector of all {@link Transition transitions} of this net.
     */
    public Vector getTransitions() {
        return transitions;
    } // public Vector getTransitions( )

    /**
     * Informs the observer of this net if something changes
     */
    public void informObserver(Object object, ActionObject a) {
        Observer o = getObserver();
        if (o != null) {
            o.update((Observable)object, a);
            // de.huberlin.informatik.pnk.appControl.base.D.d("Net: observer informed");
        }
    }

    /**
     * Registers a new {@link Place place} of this net.
     **/
    public void registerPlace(Place p) {
        places.addElement(p);
        //getNodes().addElement((Node)p);
    }

    /**
     * Registers a new {@link Transition transition} of this net.
     **/
    public void registerTransition(Transition t) {
        transitions.addElement(t);
        //getNodes().addElement((Node)t);
    }

    /**
     * Sets imarkings to initional markings.<br>
     */
    public void resetMarkings(Object initiator) {
        Vector places = getPlaces();
        Enumeration e = places.elements();
        while (e.hasMoreElements()) {
            Place p = (Place)(e.nextElement());
            Extension im = p.getExtension("initialMarking");
            p.setExtension(initiator, "marking", im.toString());
        }
    }

    /**
     * Sets the {@link Place places} of this net to <code>places</code>.
     */
    private void setPlaces(Vector places) {
        this.places = places;
    } // private void setPlaces( Vector places)

    /**
     * Sets the {@link Transition transitions} of this net to
     * <code>transitions</code>.
     */
    private void setTransitions(Vector transitions) {
        this.transitions = transitions;
    } // private void setTransitions( Vector transitions)

    /**
     * Unregisters a place of this net.
     **/
    public void unregisterPlace(Place p) {
        places.removeElement(p);
    }

    /**
     * Unregisters a transition of this net.
     **/
    public void unregisterTransition(Transition t) {
        transitions.removeElement(t);
    }
} // public final class Net extends Graph