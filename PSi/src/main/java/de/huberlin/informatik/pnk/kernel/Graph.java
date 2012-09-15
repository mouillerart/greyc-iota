package de.huberlin.informatik.pnk.kernel;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Graph.java is part of the
   Petri Net Kernel Java reimplementation.
   Graph.java has been created by the
   PNK JAVA code generator script.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Graph.java,v $
   Revision 1.15  2001/12/18 13:05:29  efischer
   name als Extension

   Revision 1.14  2001/10/11 16:57:54  oschmann
   Neue Release

   Revision 1.11  2001/06/12 07:03:09  oschmann
   Neueste Variante...

   Revision 1.10  2001/05/11 17:21:48  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.9  2001/02/27 21:29:06  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.8  2001/02/04 17:45:34  juengel
 *** empty log message ***

   Revision 1.7  2001/01/16 17:36:51  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:54  juengel
   fast fertig

   Revision 1.5  2000/12/14 00:42:34  oschmann
   Neue Version...

   Revision 1.8  2000/09/22 08:43:43  gruenewa
 *** empty log message ***

   Revision 1.7  2000/08/30 14:22:46  hohberg
   Update of comments

   Revision 1.6  2000/08/11 09:23:05  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:23  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:32:40  hohberg
   New comments

   Revision 1.1  2000/04/06 10:36:19  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:25  rschulz
   import of paradigm java sources

 */
import java.util.Enumeration;
import java.util.Vector;

import de.huberlin.informatik.pnk.kernel.base.*;
import java.util.Observer;

public class Graph extends Extendable {
    /**
     * Contains the {@link Edge edges} of this graph.
     */
    private Vector edges;
    /**
     * Contains the {@link Node nodes} of this graph.
     */
    private Vector nodes;

    /**
     * Initializes an unnamed graph with its {@link Extension extensions}
     * set to {@link Extension#isDefault default} states. <br>
     * The 'type' of the graph, that is its and its {@link Member members}
     * {@link Extension extensions}, is determined by
     * <code>specification</code>. <br>
     * For a detailed description of the treatment of the
     * extensions during instantiation refer to {@link
     * Extendable#Extendable( Specification specification)}. <br>
     */
    public Graph(Specification specification) {
        super(specification);
        this.specification = specification;
        setName("unnamed");
        setId("n1");
        setNodes(new Vector());
        setEdges(new Vector());
    } // protected  Graph(Specification specification)

    /**
     * Initializes a named graph with its {@link Extension extensions} set
     * to  {@link Extension#isDefault default} states. <BR>
     * Except the explicit naming the instantiation procedure is the same as
     * for {@link Graph#Graph( Specification specification)}. <br>
     * @param name is going to be the name of the graph.
     */
    protected Graph(Specification specification, String name) {
        super(specification);
        this.specification = specification;
        setName(name);
        setId("n1");
        setNodes(new Vector());
        setEdges(new Vector());
    } // protected  Graph( ....)

    /**
     * Gives the edge of this graph identified by <code>edgeId</code>.
     */
    public Edge getEdgeById(String edgeId) {
        Edge actEdge;
        for (Enumeration e = getEdges().elements(); e.hasMoreElements(); ) {
            actEdge = (Edge)(e.nextElement());
            if (actEdge.getId().equals(edgeId)) {
                return actEdge;
            }
        }
        return null;
    } // public Node getPlaceById(String placeId)

    /**
     * Gives a vector containing the {@link Edge edges} of this graph.
     */
    public Vector getEdges() {
        return edges;
    } // public Vector getEdges( )

    /**
     * Gives the name of this graph.
     */
    public String getName() {
        return getExtension("name").toString();
    } // public String getName( )

    /**
     * Gives the node of this graph identified by <code>nodeId</code>.
     */
    public Node getNodeById(String nodeId) {
        Node actNode;
        for (Enumeration e = getNodes().elements(); e.hasMoreElements(); ) {
            actNode = (Node)(e.nextElement());
            if (actNode.getId().equals(nodeId)) {
                return actNode;
            }
        }
        return null;
    } // public Node getPlaceById(String placeId)

    /**
     * Gives a vector containing the {@link Node nodes} of this graph.
     */
    public Vector getNodes() {
        return nodes;
    } // public Vector getNodes( )

    /**
     * Registers a new edge of this graph.
     **/
    public void registerEdge(Edge theEdge) {
        String id = theEdge.getId();
        if (id == "" || getEdgeById(id) != null) {
            String prefix;
            if (theEdge instanceof PlaceArc) {
                prefix = "pa";
            } else if (theEdge instanceof TransitionArc) {
                prefix = "ta";
            } else if (theEdge instanceof Arc) {
                prefix = "a";
            } else {
                prefix = "e";
            }
            boolean goOn = true;
            int i = 1;
            String newId;
            while (goOn) {
                newId = prefix + i;
                if (getEdgeById(newId) == null) {
                    theEdge.setId(newId);
                    goOn = false;
                }
                i++;
            }
        }
        edges.addElement(theEdge);
    }

    /**
     * Registers a new node of this graph.
     **/
    public void registerNode(Node theNode) {
        String id = theNode.getId();
        if (id == "" || getNodeById(id) != null) {
            String prefix = "n";
            if (theNode.getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Place")) {
                prefix = "p";
            }
            if (theNode.getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Transition")) {
                prefix = "t";
            }
            boolean goOn = true;
            int i = 1;
            String newId;
            while (goOn) {
                newId = prefix + i;
                if (getNodeById(newId) == null) {
                    theNode.setId(newId);
                    goOn = false;
                }
                i++;
            }
        }
        nodes.addElement(theNode);
    }

    /**
     * Sets the edges of this graph to <code>edges</code>.
     */
    private void setEdges(Vector edges) {
        this.edges = edges;
    } // protected void setEdges( Vector edges)

    /**
     * Sets the name of this graph to <code>name</code>.
     */
    public void setName(String name) {
        setExtension(this, "name", name);
    } // public void setName( String name)

    /**

     * Sets nodes of this graph to <code>nodes</code>.<BR>
     */
    private void setNodes(Vector nodes) {
        this.nodes = nodes;
    } // protected void setNodes( Vector nodes)

    /**
     * Unregisters the edge <code>e</code> of this graph.
     **/
    public void unregisterEdge(Edge e) {
        edges.removeElement(e);
    }

    /**
     * Unregisters the node <code>n</code> of this graph.
     **/
    public void unregisterNode(Node n) {
        nodes.removeElement(n);
    }

    /**
       *A reference to the observer of this net (initialy null).
     */
    private Observer observer = null;
    /**
     * Refers to the current {@link Specification specification}. <BR>
     */
    private Specification specification;

    /**
     * Sets the observer of this net to <code>o</code>.
     * The oserver gets the specification table containing a
     * description of all {@link Extension extensions} of this net.
     */
    public synchronized void addObserver(Observer o) {
        // de.huberlin.informatik.pnk.appControl.base.D.d("Extendable1 addObserver");
        if (observer == null) {
            // Der Observer darf nur einmal gesetzt werden...
            observer = o;
        }
        Specification spec = getSpecification();
        NewNetAction netAction = null;
        if (spec != null) {
            netAction = new NewNetAction(observer, spec.getSpecTab());
        } else {
            netAction = new NewNetAction(observer, null);
        }
        observer.update(this, netAction);
    }

    /**
     * Gets the observer of this net.
     */
    public Observer getObserver() {
        return observer;
    }

    /**
     * Gets the specification of this extendable.
     */
    public Specification getSpecification() {
        return specification;
    } // public Specification getSpecification()

    /**
     * Sets the specification of this extendable.
     */
    protected void setSpecification(Specification specification) {
        this.specification = specification;
    } // public Specification setSpecification(Specification specification)
} // class Graph extends Extendable
