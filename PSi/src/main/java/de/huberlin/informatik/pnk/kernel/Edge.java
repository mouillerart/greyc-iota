package de.huberlin.informatik.pnk.kernel;

import de.huberlin.informatik.pnk.exceptions.*;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Edge.java is part of the
   Petri Net Kernel Java reimplementation.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Edge.java,v $
   Revision 1.13  2001/10/11 16:57:50  oschmann
   Neue Release

   Revision 1.11  2001/06/12 07:03:06  oschmann
   Neueste Variante...

   Revision 1.10  2001/05/11 17:21:45  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.8  2001/02/27 21:29:02  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.7  2001/01/16 17:36:48  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:51  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:31  oschmann
   Neue Version...

   Revision 1.9  2000/10/09 13:51:32  gruenewa
 *** empty log message ***

   Revision 1.8  2000/09/22 08:43:39  gruenewa
 *** empty log message ***

   Revision 1.7  2000/08/30 14:22:45  hohberg
   Update of comments

   Revision 1.6  2000/08/11 09:23:01  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:21  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:26:25  hohberg
        New comments

   Revision 1.1  2000/04/06 10:36:17  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:25  rschulz
   import of paradigm java sources

 */

import de.huberlin.informatik.pnk.kernel.base.*;
import java.util.Observer;

/**
 * Defines an <em>edge</em> of a {@link Graph directed graph}.
 * No standard {@link Extension extensions} are defined for
 *  <code>Edge</code>.
 * @version 1.0
 */
public class Edge extends Member {
    /**
     * Refers to the {@link Node node}, the edge leaves from.
     */
    private Node source;

    /**
     * Refers to the {@link Node node}, the edge leads to.
     */
    private Node target;

    /**
     * Initializes a new edge from node <code>source</code> to node
     * <code>target</code> with each of its local extensions set to its
     * {@link Extension#isDefault default} state.
     */
    public Edge(Graph graph, Node source, Node target) {
        super(graph);
        if (this.checkNodes(source, target)) {
            setSource(source);
            setTarget(target);
            graph.registerEdge(this);
            // register as incoming and outgoing edge
            source.addOutgoingEdge(this);
            target.addIncomingEdge(this);
        } else {
            throw new KernelUseException(this.getClass().getName() + " is not allowed for this net type");
        }
    } // public  Edge( Graph graph, Node source, Node target)

    /**
     * Initializes a new edge from node <code>source</code> to node
     * <code>target</code> with each of its local extensions set to its
     * {@link Extension#isDefault default} state.
     */
    public Edge(Graph graph, String sourceId, String targetId, Object initiator, String arcId) {
        super(graph);
        setSource(graph.getNodeById(sourceId));
        setTarget(graph.getNodeById(targetId));
        graph.registerEdge(this);
        // register as incoming and outgoing edge
        source.addOutgoingEdge(this);
        target.addIncomingEdge(this);
        setId(arcId);
    } // public  Arc( Graph graph, Node source, Node target)

    /**
     * Checks if Edge is allowed...
     */
    public boolean checkNodes(Node source, Node target) {
        if ((source instanceof Node) && (target instanceof Node))
            if (getExtIdToObject() != null)
                return true;
        return false;
    }

    /**
     * Deletes the edge.
     * Informs the observer of the <code>graph</code>.
     */
    public void delete(Object initiator) { // Unregister this edge in source and target
        source.deleteOutgoingEdge(this);
        target.deleteIncomingEdge(this);
        // Inform the observer
        Observer o = ((Net)getGraph()).getObserver();
        if (o != null) {
            DeleteAction d = new DeleteAction(initiator);
            o.update(this, d);
        }
        // Unregister this edge in graph
        getGraph().unregisterEdge(this);
    }

    /**
     * Returns a reference to the {@link Node node}, the edge
     * leaves from.
     */
    public Node getSource() {
        return source;
    } // public Node getSource( )

    /**
     * Returns a reference to the {@link Node node}, the edge
     * leads to.
     */
    public Node getTarget() {
        return target;
    } // public Node getTarget( )

    /**
     * Sets the {@link Node node}, the edge
     * leaves from, to <code>source</code>.
     */
    protected void setSource(Node source) {
        this.source = source;
    } // protected void setSource( Node source)

    /**
     * Sets the {@link Node node}, the edge
     * leads to, to <code>target</code>.
     */
    protected void setTarget(Node target) {
        this.target = target;
    } // protected void setTarget( Node target)
} // class Edge extends Member
