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
   $Log: Arc.java,v $
   Revision 1.16  2001/10/11 16:57:47  oschmann
   Neue Release

   Revision 1.14  2001/06/12 07:03:04  oschmann
   Neueste Variante...

   Revision 1.13  2001/05/11 17:21:43  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.12  2001/03/30 12:55:17  hohberg
   Error in informObserver() removed

   Revision 1.11  2001/03/30 08:28:38  hohberg
   New exception handling

   Revision 1.10  2001/03/26 07:47:40  hohberg
   Code improved

   Revision 1.9  2001/02/27 21:29:01  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:36:46  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:50  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:28  oschmann
   Neue Version...

   Revision 1.7  2000/09/22 08:43:35  gruenewa
 *** empty log message ***

   Revision 1.6  2000/08/30 14:22:43  hohberg
   Update of comments

   Revision 1.2  2000/05/10 12:26:25  hohberg
        New comments

   Revision 1.1  2000/04/06 10:36:17  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:25  rschulz
   import of paradigm java sources

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
public final class Arc extends Edge {
    /**
     * Initializes a new arc from node <code>source</code> to node
     * <code> target </code> with each of its {@link Extension extensions}
     * set to its {@link Extension#isDefault() default}state.
     */
    public Arc(Net net, Node source, Node target, Object initiator) {
        super(net, source, target);
        setAttributes(net, source, target, initiator);
    } // public  Arc( Net net, Node source, Node target)

    /**
     * Initializes a new arc from node <code>source</code> to node
     * <code>target</code> with each of its {@link Extension extensions}
     * set to its {@link Extension#isDefault() default} state and with id
     * <code>arcId</code>.
     */
    public Arc(Net net, String sourceId, String targetId, Object initiator, String arcId) {
        super(net, net.getNodeById(sourceId), net.getNodeById(targetId));
        setId(arcId);
        setAttributes(net, net.getNodeById(sourceId), net.getNodeById(targetId), initiator);
    } // public  Arc( Net net, Node source, Node target)

    public boolean checkNodes(Node source, Node target) {
        if (((source instanceof Place) && (target instanceof Transition))
            || ((source instanceof Transition) && (target instanceof Place)))
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

    /**
     * Gets the {@link Place place} of this arc. <BR>
     */
    public Place getPlace() {
        if (getSource().getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Place")) {
            return (Place)getSource();
        } else {
            if (getTarget().getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Place")) {
                return (Place)getTarget();
            }
        }
        return null;
    } // public Place getPlace( )

    /**
     * Gets the {@link Transition transition} of this arc.
     */
    public Transition getTransition() {
        if (getSource().getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Transition")) {
            return (Transition)getSource();
        } else {
            if (getTarget().getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Transition")) {
                return (Transition)getTarget();
            }
        }
        return null;
    } // public Transition getTransition( )

    private void setAttributes(Net net, Node source, Node target, Object initiator) {
        // get table of extension names and values of extensions
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
} // public final class Arc extends Edge
