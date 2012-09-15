package de.huberlin.informatik.pnk.kernel;

import de.huberlin.informatik.pnk.netElementExtensions.base.*;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Place.java,v $
   Revision 1.15  2001/10/11 16:58:00  oschmann
   Neue Release

   Revision 1.13  2001/06/12 07:03:14  oschmann
   Neueste Variante...

   Revision 1.12  2001/05/11 17:21:55  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.11  2001/04/17 05:35:33  gruenewa
 *** empty log message ***

   Revision 1.10  2001/03/30 12:55:16  hohberg
   Error in informObserver() removed

   Revision 1.9  2001/03/26 07:47:41  hohberg
   Code improved

   Revision 1.8  2001/02/27 21:29:13  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:36:58  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:59  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:40  oschmann
   Neue Version...

   Revision 1.10  2000/09/01 08:07:27  hohberg
   Code revision

   Revision 1.9  2000/08/30 14:22:49  hohberg
   Update of comments

   Revision 1.8  2000/08/11 09:23:12  gruenewa
   =======
   Revision 1.12  2000/09/22 08:43:54  gruenewa
   >>>>>>> 1.12
 *** empty log message ***
   <<<<<<< Place.java

   Revision 1.3  2000/05/17 14:11:25  juengel
   vorbereitung xml laden / speichern


   Revision 1.2  2000/05/10 12:49:49  hohberg

   New comments

 */

import de.huberlin.informatik.pnk.kernel.base.*;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

/**
 * Describes a <em> place </em> of a {@link Net Petri Net}
 */
public final class Place extends Node {
    /**
     * Initializes a new place with each of its local
     * {@link Extension extensions} set to its default state. <BR>
     * @param net    the net of this place
     * @param name   the name of this place
     * @param initiator the object, creating this place
     */
    public Place(Net net, String name, Object initiator) {
        super(net, name);
        net.registerPlace(this);
        if (name == null) setName(getId());

        Hashtable extIdToValue = getExtIdToValue();
        NewPlaceAction p = new NewPlaceAction(initiator, name, extIdToValue);
        net.informObserver(this, p);
    } // public  Place( Net net, String name)

/**
 * Initializes a new place with each of its local {@link Extension
 * extensions} set to its default state. <br>
 * This place is contained in the specified <code>block</code>
 * @param net    the net of this place
 * @param name   the name of this place
 * @param initiator the object, creating this place
 * @param block the block containing this place
 */
    public Place(Net net, String name, Object initiator, BlockStructure block) {
        this(net, name, initiator);
        setBlock(block);
        block.registerPlace(this);
        if (name == null) setName(getId());
    }

    /**
     * Initializes a new place with each of its local
     * {@link Extension extensions} set to its default state. <BR>
     * @param net    the net of this place
     * @param name   the name of this place
     * @param initiator the object, creating this place
     * @param placeId   the id of this place
     */
    public Place(Net net, String name, Object initiator, String placeId) {
        super(net, name);
        net.registerPlace(this);
        setId(placeId);
        if (name == null) setName(getId());

        Hashtable extIdToValue = getExtIdToValue();
        NewPlaceAction p = new NewPlaceAction(initiator, name, extIdToValue);
        net.informObserver(this, p);
    } // public  Place( Net net, String name)

    /**
     * Deletes this place and its incoming and outgoing edges. <BR>
     * Informs the Observer of the graph.
     */
    public void delete(Object initiator) {
        // delete incoming and outgoing edges
        getNet().unregisterPlace(this);
        super.delete(initiator);
    }

    /**
     * Gets the {@link Marking initial marking} of this place given by the
     * {@link Extension extension} with identifier "initialMarking".
     */
    public Marking getInitialMarking() {
        return (Marking)getExtension("initialMarking");
    } // public Marking getInitialMarking( )

    /**
     * Gets the {@link Marking  marking} of this place given by the
     * {@link Extension extension} with identifier "marking".
     */
    public Marking getMarking() {
        /* body source: src\de.huberlin.informatik.pnk.kernel\Place\getMarking.java */
        return (Marking)getExtension("marking");
    } // public Marking getMarking( )

    /**
       Join an interface node with this node and return true.
       The joined node gets the extension of this node.
       Returns false if this node or <code>p</code>
       is not an interface node.
     */
    public boolean interfaceJoin(Place p, Object initiator) {
        de.huberlin.informatik.pnk.appControl.base.D.d("Start: Place.interfaceJoin()");
        if (!block.isInterfaceNode(this)) return false;
        if (!p.getBlock().isInterfaceNode(p)) return false;
        // de.huberlin.informatik.pnk.appControl.base.D.d("Place.interfaceJoin()");
        p.registerInterfaceNode(this);
        // p gets the extensions of this node
        p.setExtIdToObject(getExtIdToObject());
        return true;
    }

    /**
     * {@link Extension Extension} "marking" is assigned the value
     * of extension "initialMarking".
     */
    public void setMarkingAsInitial() {
        getExtension("initialMarking").
        valueOf(getExtension("marking").toString());
    } // public void setMarkingAsInitial( )

    /**
     * Gets a new place with {@link Edge edges} given in
     * <code>splitEdges</code>. <br>
     * That is, in the <code>splitEdges</code> this place is substituted by
       the      * new place. <br>
     * {@link Extension Extensions} of all edges stay unchanged.
     * The <code>initiator</code> calls this method.
     */
    public Node split(Vector splitEdges, Object initiator) {
        Net net = (Net)getGraph();
        Place splitPlace = new Place(net, "undefined name", initiator);

        Enumeration edges = splitEdges.elements();
        while (edges.hasMoreElements()) {
            Arc a = ((Arc)edges.nextElement());
            if (a.getTarget() == this) {
                a.setTargetNode(splitPlace, initiator);
                deleteIncomingEdge(a);
            } else if (a.getSource() == this) {
                a.setSourceNode(splitPlace, initiator);
                deleteIncomingEdge(a);
            } else {
                de.huberlin.informatik.pnk.appControl.base.D.d("ERROR: No arc of this node.");
            }
        }
        return splitPlace;
    } // split()
} // public final class Place extends Node