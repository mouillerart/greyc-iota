package de.huberlin.informatik.pnk.kernel;

import de.huberlin.informatik.pnk.kernel.base.*;
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
   $Log: PlaceArc.java,v $
   Revision 1.11  2001/10/11 16:58:02  oschmann
   Neue Release

   Revision 1.8  2001/06/12 07:03:15  oschmann
   Neueste Variante...

   Revision 1.7  2001/05/11 17:21:56  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.6  2001/03/30 12:55:17  hohberg
   Error in informObserver() removed

   Revision 1.5  2001/03/30 08:28:37  hohberg
   New exception handling

   Revision 1.4  2001/03/26 07:47:42  hohberg
   Code improved

   Revision 1.3  2001/02/27 21:29:14  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.1  2001/02/13 10:41:03  hohberg
 *** empty log message ***

 */

import java.util.*;
import java.util.Hashtable;

/**
 * Describes an <em> arc </em> of a {@link Net Petri Net}.
 * <br>
 * A class for the standard {@link Extension extension} refered to by the
 * indentifier <code> "inscription" </code> must be implemented. This is
 * achieved by deriving a class from {@link Extension} and implementing
 * the interface {@link Inscription}. <br>
   @version 1.0
 */
public final class PlaceArc extends Edge {
    /**
     * Initializes a new arc from node <code>source</code> to node
     * <code> target </code> with each of its {@link Extension extensions}
     * set to its {@link Extension#isDefault() default}state.
     */
    public PlaceArc(Net net, Node source, Node target, Object initiator) {
        super(net, source, target);
        setAttributes(net, source, target, initiator);
    } // public  PlaceArc( Net net, Node source, Node target)

    /**
     * Initializes a new arc from node <code>source</code> to node
     * <code>target</code> with each of its {@link Extension extensions}
     * set to its {@link Extension#isDefault() default} state and with id
     * <code>arcId</code>.
     */
    public PlaceArc(Net net, String sourceId, String targetId, Object initiator, String arcId) {
        super(net, net.getNodeById(sourceId), net.getNodeById(targetId));
        setId(arcId);
        setAttributes(net, net.getNodeById(sourceId), net.getNodeById(targetId), initiator);
    } // public  PlaceArc( Net net, Node source, Node target)

    public boolean checkNodes(Node source, Node target) {
        if ((source instanceof Place) && (target instanceof Place))
            if (getExtIdToObject() != null)
                return true;
        return false;
    }

    /**
     * Gets the  {@link Extension standard extension}
     * {@link Inscription inscription} of this arc. <br>
     * @see Extendable#getExtension
     */
    public Inscription getInscription() {
        return (Inscription)getExtension("inscription");
    } // public Inscription getInscription( )

    private void setAttributes(Net net, Node source, Node target, Object initiator) {
        Hashtable extIdToValue = getExtIdToValue();
        NewArcAction a = new NewArcAction(initiator, source, target, extIdToValue);
        net.informObserver(this, a);
    }

    /**
     * Sets source node of this arc to <code>newNode</code>
     * and informs the observer of the net.
     */
    public void setSourceNode(Node newNode, Object initiator) {
//de.huberlin.informatik.pnk.appControl.base.D.d("de.huberlin.informatik.pnk.kernel.Arc.setSourceNode");

        setSource(newNode);
        newNode.addOutgoingEdge(this);
        Observer o = ((Net)getGraph()).getObserver();
        if (o != null) {
            /* de.huberlin.informatik.pnk.appControl.base.D.d(
               "de.huberlin.informatik.pnk.kernel.Arc.setSourceNode... sending
               ChangeSourceAction(initiator, newNode) ");
             */

            ChangeSourceAction p = new ChangeSourceAction(initiator, newNode);
            o.update(this, p);
        }
        setSource(newNode);
    }

    /**
     * Sets the target node of this arc to <code>newNode</code>
     * and informs the observer of the net.
     */
    public void setTargetNode(Node newNode, Object initiator) {
        // de.huberlin.informatik.pnk.appControl.base.D.d("de.huberlin.informatik.pnk.kernel.Arc.setTargetNode");
        setTarget(newNode);
        newNode.addIncomingEdge(this);
        Observer o = ((Net)getGraph()).getObserver();
        if (o != null) {
            /* de.huberlin.informatik.pnk.appControl.base.D.d(
               "de.huberlin.informatik.pnk.kernel.Arc.setTargetNode....sending \
                    ChangeTargetAction");
             */
            ChangeTargetAction p = new ChangeTargetAction(initiator, newNode);
            o.update(this, p);
        }
        setTarget(newNode);
    }
} // public final class PlaceArc extends Edge
