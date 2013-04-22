package de.huberlin.informatik.pnk.kernel;

import de.huberlin.informatik.pnk.kernel.base.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;
import java.util.*;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   Java source Transition.java is part of the
   Petri Net Kernel Java reimplementation.

   $Log: Transition.java,v $
   Revision 1.15  2001/10/11 16:58:05  oschmann
   Neue Release

   Revision 1.13  2001/06/12 07:03:18  oschmann
   Neueste Variante...

   Revision 1.12  2001/05/11 17:21:59  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.11  2001/04/17 05:35:34  gruenewa
 *** empty log message ***

   Revision 1.10  2001/03/30 12:55:17  hohberg
   Error in informObserver() removed

   Revision 1.9  2001/03/26 07:47:41  hohberg
   Code improved

   Revision 1.8  2001/02/27 21:29:17  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:37:02  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:21:03  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:44  oschmann
   Neue Version...

   Revision 1.13  2000/09/22 08:44:04  gruenewa
 *** empty log message ***

   Revision 1.12  2000/09/11 07:48:04  hohberg
   Implementation of blockstructure

   Revision 1.11  2000/09/01 08:07:28  hohberg
   Code revision

   Revision 1.10  2000/08/30 14:22:51  hohberg
   Update of comments

   Revision 1.9  2000/08/11 09:23:17  gruenewa
 *** empty log message ***

   Revision 1.4  2000/05/17 14:11:28  juengel
   vorbereitung xml laden / speichern

   Revision 1.3  2000/05/11 06:44:24  hohberg
   New comments

 */

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Describes a <em> transition </em> of a {@link Net Petri Net}.

 */
public final class Transition extends Node {
    /**
     * Initializes a new transition with each of its local
     * {@link Extension extensions} set to its default state. <BR>
     * @param net    the net of this transition
     * @param name   the name of this transition
     * @param initiator the object, creating this transition
     */
    public Transition(Net net, String name, Object initiator) {
        super(net, name);
        net.registerTransition(this);
        if (name == null) setName(getId());
        Hashtable extIdToValue = getExtIdToValue();
        NewTransitionAction p =
            new NewTransitionAction(initiator, name, extIdToValue);
        net.informObserver(this, p);
    } // public  Transition( Net net, String name)

    /**
     * Initializes a new transition with each of its local {@link Extension
     * extensions} set to its default state. <br>
     * This transition is contained in the specified <code>block</code>
     * @param net    the net of this transition
     * @param name   the name of this transition
     * @param initiator the object, creating this transition
     * @param block the block containing this transition
     */
    public Transition(Net net, String name, Object initiator, BlockStructure block) {
        this(net, name, initiator);
        setBlock(block);
        block.registerTransition(this);
        if (name == null) setName(getId());
    }

    /**
     * Initializes a new transition with each of its local
     * {@link Extension extensions} set to its default state. <BR>
     * @param net    the net of this transition
     * @param name   the name of this transition
     * @param initiator the object, creating this transition
     * @param transitionId   the id of this transition
     */
    public Transition(Net net, String name, Object initiator, String transitionId) {
        super(net, name);
        net.registerTransition(this);
        setId(transitionId);
        if (name == null) setName(getId());
        Hashtable extIdToValue = getExtIdToValue();
        NewTransitionAction p =
            new NewTransitionAction(initiator, name, extIdToValue);
        net.informObserver(this, p);
    } // public  Transition( Net net, String name)

    /**
     * Deletes this transition and its incomming and outgoing edges. <BR>
     * Informs the Observer of the graph.<BR>
     */
    public void delete(Object initiator) {
        ((Net)getGraph()).unregisterTransition(this);
        super.delete(initiator);
    }

    /**
     * Gets the {@link Mode mode} of this transition given by the
     * {@link Extension extension} with identifier "mode".
     */
    public Mode getMode() {
        return (Mode)getExtension("mode");
    } // public Mode getMode( )

    /**
     * Gets a new transition with {@link Edge edges} given in
     * <code>splitEdges</code>. <br>
     * That is, in all eges of the set
     * <code>splitEdges</code> this transition is
     * substituted by the new transition. <br>
     * {@link Extension Extensions} of all edges stay unchanged.
     * The <code>initiator</code> calls this method.
     */
    public Node split(Vector splitEdges, Object initiator) {
        Net net = (Net)getGraph();
        Transition splitTransition =
            new Transition(net, "undefined name", initiator);

        Enumeration edges = splitEdges.elements();
        while (edges.hasMoreElements()) {
            Arc a = ((Arc)edges.nextElement());
            if (a.getTarget() == this) {
                a.setTargetNode(splitTransition, initiator);
                deleteIncomingEdge(a);
            } else if (a.getSource() == this) {
                a.setSourceNode(splitTransition, initiator);
                deleteIncomingEdge(a);
            } else {
                System.out.println("ERROR: No arc of this node.");
            }
        }
        return splitTransition;
    } // split()

    public Vector getPreSet() {
        Net net = (Net)getGraph();
        Vector iEdges = getIncomingEdges();
        HashSet places = new HashSet();
        for (int i = 0; i < iEdges.size(); i++) {
            Edge iEdge = (Edge)iEdges.get(i);
            places.add(iEdge.getSource());
        }
        return new Vector(places);
    }

    public Vector getPostSet() {
        Net net = (Net)getGraph();
        Vector oEdges = getOutgoingEdges();
        HashSet places = new HashSet();
        for (int i = 0; i < oEdges.size(); i++) {
            Edge oEdge = (Edge)oEdges.get(i);
            places.add(oEdge.getTarget());
        }
        return new Vector(places);
    }
} // public final class Transition extends Node
