package de.huberlin.informatik.pnk.kernel;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Node.java is part of the
   Petri Net Kernel Java reimplementation.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Node.java,v $
   Revision 1.17  2001/10/11 16:57:59  oschmann
   Neue Release

   Revision 1.16  2001/05/11 17:21:54  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.14  2001/04/24 16:11:55  efischer
 *** empty log message ***

   Revision 1.13  2001/04/17 05:35:32  gruenewa
 *** empty log message ***

   Revision 1.12  2001/03/30 12:57:57  hohberg
   New error handling
   r handling

   Revision 1.11  2001/02/27 21:29:12  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.9  2001/02/12 14:52:41  hohberg
 *** empty log message ***

   Revision 1.8  2001/02/12 12:51:35  hohberg
   RuntimeError if Place or Transition not allowed in net type

   Revision 1.7  2001/01/16 17:36:57  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:58  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:39  oschmann
   Neue Version...

   Revision 1.12  2000/09/22 08:43:53  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:25  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:48:40  hohberg
   New comments

   Revision 1.1  2000/04/06 10:36:24  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:26  rschulz
   import of paradigm java sources

 */

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.base.*;
import java.util.Enumeration;
import java.util.Observer;
import java.util.Vector;

/**
 * Defines a <em>node</em> of a {@link Graph graph}.
 * <br>
 * Standard {@link Extension extension} is the name of a node. <br>
 */
public class Node extends Member {
    /**
     * A vector containing a reference to each edge leading to this node.
     */
    private Vector incomingEdges = new Vector(10);

    /**
     * A vector containing a reference to each outgoing edge of this node.
     */
    private Vector outgoingEdges = new Vector(10);

    ///////////////////////////////////////////////////////////////////////
    ///////         Interface of node //////////////////////////////////
    /**
       Block containing this Node
     */
    BlockStructure block = null;

    /**
       Vector of interface nodes n joined to this node.
       (n.getBlock() gives the corresponding interface block)
     */
    Vector blockInterface = new Vector(2);

    /**
     * Creates a new node of the graph with each of its local extensions
     * set to its default state. <BR>
     * Specifyes the <code>name</code> of this graph.<BR>
     * @param initiator The object, creating this node.
     */
    public Node(Graph graph, String name) {
        /* body source: src\de.huberlin.informatik.pnk.kernel\Node\Node_default.java */
        super(graph);
        if (getExtIdToObject() == null) {
            throw new KernelUseException(this.getClass().getName() +
                                         " is not allowed for this net type");
        }
        graph.registerNode(this);
        if (name == null) setName(getId());
        else setName(name);
    }     // protected  Node( Graph graph, String name)

    /**
     * Register a new incoming edge of this node. <BR>
     */
    public void addIncomingEdge(Edge edge) {
        incomingEdges.addElement(edge);
    }     // addIncomingEdge( Edge edge)

    /**
     * Register a new outgoing edge of this node. <BR>
     */
    public void addOutgoingEdge(Edge edge) {
        outgoingEdges.addElement(edge);
    }     // addOutgoingEdge( Edge edge)

    /**
     * Deletes this node and its incomming and outgoing edges. <BR>
     * Informs the Observer of the graph.
     */
    public void delete(Object initiator) {
        // delete incoming and outgoing edges
        Enumeration inEdges = incomingEdges.elements();
        while (inEdges.hasMoreElements()) {
            ((Edge)inEdges.nextElement()).delete(initiator);
        }
        Enumeration outEdges = outgoingEdges.elements();
        while (outEdges.hasMoreElements()) {
            ((Edge)outEdges.nextElement()).delete(initiator);
        }
        // Unregister this node in graph
        getGraph().unregisterNode(this);
        // inform the Observer
        Observer o = ((Net)getGraph()).getObserver();
        if (o != null) {
            DeleteAction d = new DeleteAction(initiator);
            o.update(this, d);
        }
    }

    /**
     * Unregisters an incoming edge of this node. <BR>
     */
    public void deleteIncomingEdge(Edge edge) {
        incomingEdges.removeElement(edge);
    }     // deleteIncomingEdge( Edge edge)

    /**
     * Unregisters an outgoing edge of this node. <BR>
     */
    public void deleteOutgoingEdge(Edge edge) {
        outgoingEdges.removeElement(edge);
    }     // deleteOutgoingEdge( Edge edge)

    /**
     * Gets the {@link BlockStructure block} of this node. <br>
     */
    public BlockStructure getBlock() {return block; }
    /**
     * Gets the vector of all edges leading to this node. <BR>
     */
    public Vector getIncomingEdges() {
        return incomingEdges;
    }     // public Vector getIncomingEdges( )

    /**
     * Gets the name of this node. <br>
     */
    public String getName() {
        return getExtension("name").toString();
    }     // public String getName( )

    /**
     * Gets the vector of all incoming edges of this node. <BR>
     */
    public Vector getOutgoingEdges() {
        return outgoingEdges;
    }     // public Vector getOutgoingEdges( )

    /**
     * Joins this node with node <code>n</code>. <br>
     * {@link Edge Edges} of <code>n</code> turn to edges of this node. <br>
     * {@link Extension Extensions} of this node and
     * of all edges stay unchanged. <br>
     * The <code>initiator</code> calls this method.
     */
    public void join(Node n, Object initiator) {
        // de.huberlin.informatik.pnk.appControl.base.D.d("Node.join()");
        Vector inEdges = n.getIncomingEdges();
        while (!inEdges.isEmpty()) {
            /* de.huberlin.informatik.pnk.appControl.base.D.d(
               "Node.join()      while (inEdges.hasMoreElements())   ");
             */
            Arc a = ((Arc)inEdges.firstElement());
            a.setTargetNode(this, initiator);
            n.deleteIncomingEdge(a);
        }
        Vector outEdges = n.getOutgoingEdges();
        while (!outEdges.isEmpty()) {
            //de.huberlin.informatik.pnk.appControl.base.D.d("Node.join() while(outEdges.hasMoreElements())   ");
            Arc a = ((Arc)outEdges.firstElement());
            a.setSourceNode(this, initiator);
            n.deleteOutgoingEdge(a);
        }
        // after deleting incomming and outgoing arcs of n
        // delete node n
        n.delete(initiator);
    }     // join(Node n)

    /**
     * Unregisters code>inode</code> as interface node
     * of this node. <br>
       public void unregisterInterfaceNode( Node inode) {
       blockInterface.removeElement(inode);
       }

       /**
     * Gets all interface nodes of this node. <br>
       public Vector getInterfaceNodes() { return blockInterface; }

       /**
       Join with the interface nodes.
       Requires: block contains no block. <br>
     */
    public void joinWithInterface(Object initiator) {
        if (blockInterface.isEmpty()) return;
        // join recursively with interface nodes
        Enumeration joined = blockInterface.elements();
        while (joined.hasMoreElements()) {
            Node jn = (Node)joined.nextElement();
            jn.joinWithInterface(initiator);
            // join with this node
            // de.huberlin.informatik.pnk.appControl.base.D.d("join: " );
            join(jn, initiator);
            // delete jn from jn.block
            jn.block.unregisterNode(jn);
        }
    }

    /**
     * Registers <code>inode</code> as a new interface node
     * of this node (joined with this node). <br>
     */
    public void registerInterfaceNode(Node inode) {
        blockInterface.addElement(inode);
    }

    /**
     * Sets the block of this node to <code>block</code>. <br>
     */
    public void setBlock(BlockStructure block) {this.block = block; }
    /**
     * Sets the name of this node to <code>name</code>. <br>
     */
    protected void setName(String name) {
        if (name == null)
            name = this.getId();

        setExtension(null, "name", name);
        /*
           Extension e = (Extension)getExtIdToObject().get("name");
           if(e != null) e.valueOf(name);   //set the name
         */
    }     // public void setName( String name)

    /**
     * Gets a new node with {@link Edge edges} given in <code>edges</code>.
     * <br>
     * That is, in the <code>edges</code> this node is substituted by the
     * new node. <br>
     * The <code>initiator</code> calls this method.
     */
    public Node split(Vector edges, Object initiator) {return null; }
} // class Node extends Member