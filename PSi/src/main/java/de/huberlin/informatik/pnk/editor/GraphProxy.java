package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.util.*;

import de.huberlin.informatik.pnk.kernel.*;

/**
 * GraphProxy.java
 *
 * The editor uses this class to access kernel-net
 * or kernel-graph. Therefor it calls the methods
 * of this class.
 *
 * Created: Sat Dec 23 13:06:03 2000
 *
 * @author Alexander Gruenewald
 * @version 0.1
 */

class GraphProxy  {
    private Editor editor;

    Graph graph;

    /*
     * Class constructor.
     * @param editor the editor that uses this proxy.
     * @param graph the graph/net this is the proxy for.
     */
    protected GraphProxy(Editor editor, Object graph) {
        this.setEditor(editor);
        this.setGraph(graph);
    }

    /*
     * Set extension of netobject in net.
     * @param id name of extension
     * @param value value of extension
     */
    protected void changeExtension(Object netobject, String id, String value) {
        if (netobject instanceof Extendable) {
            Extendable e = (Extendable)netobject;
            e.setExtension(this.editor, id, value);
            if (id.equals("name") && (netobject instanceof Graph)) {
                /*if net name was changed inform the application control -> label update*/
                this.editor.applicationControl.netNameChanged();
            }
        }
    }

    protected void closeGraph() {
        this.graph = null;
    }

    /*
     * Delets an object in kernel.Net.
     * @param netobject the object should be deleted
     */
    protected void delete(Object netobject) {
        Editor editor = this.getEditor();
        if (netobject instanceof de.huberlin.informatik.pnk.kernel.Node) {
            ((de.huberlin.informatik.pnk.kernel.Node)netobject).delete(editor);
            return;
        }
        if (netobject instanceof de.huberlin.informatik.pnk.kernel.Edge) {
            ((de.huberlin.informatik.pnk.kernel.Edge)netobject).delete(editor);
            return;
        }
        if (netobject instanceof de.huberlin.informatik.pnk.kernel.Arc) {
            ((de.huberlin.informatik.pnk.kernel.Arc)netobject).delete(editor);
            return;
        }
        if (netobject instanceof de.huberlin.informatik.pnk.kernel.Place) {
            ((de.huberlin.informatik.pnk.kernel.Place)netobject).delete(editor);
            return;
        }
        if (netobject instanceof de.huberlin.informatik.pnk.kernel.Transition) {
            ((de.huberlin.informatik.pnk.kernel.Transition)netobject).delete(editor);
            return;
        }
    }

    /**
     * @return a vector containing all netobjects of kernel.Net / kernel.Graph
     */
    protected Vector getAllNetobjects() {
        // create a collection of all netobjects
        Vector netobjects = new Vector();
        /*if(this.graph instanceof Net) {
           Net net = (Net) this.graph;
           netobjects.addAll(net.getPlaces());
           netobjects.addAll(net.getTransitions());
           netobjects.addAll(net.getArcs());
           return netobjects;
           }*/
        //else {
        netobjects.addAll(graph.getNodes());
        netobjects.addAll(graph.getEdges());
        return netobjects;
        //}
    }

    /**
     * Get the value of editor.
     * @return Value of editor.
     */
    protected Editor getEditor() {
        return editor;
    }

    /*
     * Get extension of an extendable
     * @param extendable owner of extension
     * @param id id of wanted extension
     */
    protected Object getExtension(Object extendable, String id) {
        if (extendable instanceof Extendable) {
            Extendable e = (Extendable)extendable;
            return e.getExtension(id);
        }
        return null;
    }

    protected Enumeration getExtensionIds(String classIdentifier) {
        return this.graph.getSpecification().getSpecTab().getExtIds(classIdentifier);
    }

    /*
     * Get a hashtable with all extensions of a netobject
     * Entrys have the form: String id -> String value
     */
    protected Hashtable getExtensionIdToValue(Object ext) {
        if (ext instanceof
            de.huberlin.informatik.pnk.kernel.Extendable) {
            de.huberlin.informatik.pnk.kernel.Extendable extendable =
                (de.huberlin.informatik.pnk.kernel.Extendable)ext;
            Hashtable extIdToVal = new Hashtable();
            Hashtable extIdToObj = extendable.getExtIdToObject();
            if (extIdToObj != null) {
                Enumeration keys = extIdToObj.keys();
                while (keys.hasMoreElements()) {
                    String extId = (String)keys.nextElement();
                    String extVa = ((de.huberlin.informatik.pnk.kernel.Extension)
                                    extIdToObj.get(extId)).toString();
                    extIdToVal.put(extId, extVa);
                }
                return extIdToVal;
            }
        } else {
            editor.error(" ### object is no extendable");
        }
        return null;
    }

    /**
     * Get the value of graph.
     * @return Value of graph.
     */
    protected Graph getGraph() {
        return graph;
    }

    protected String getMemberId(Object member) {
        return ((de.huberlin.informatik.pnk.kernel.Member)member).getId();
    }

    protected String getName() {
        return this.graph.getName();
    }

    /*
     * Gets position of netobject on Page pageId
     */
    protected Point getPosition(Object netobject, int pageId) {
        Point p = new Point();
        if (netobject instanceof Extendable) {
            Extendable e = (Extendable)netobject;
            p = e.getPosition(pageId);
            return p;
        }
        return p;
    }

    /*
     * @return kernels sourcenode of edge.
     */
    protected Object getSource(Object edge) {
        return ((de.huberlin.informatik.pnk.kernel.Edge)edge).getSource();
    }

    protected Hashtable getSpecificationTable() {
        return this.graph.getSpecification().getSpecTab().getSpecificationTable();
    }

    /*
     * @return kernels targetnode of edge.
     */
    protected Object getTarget(Object edge) {
        return ((de.huberlin.informatik.pnk.kernel.Edge)edge).getTarget();
    }

    /*
     * @return true, if object is kernel.Arc
     */
    boolean isArc(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Arc;
    }

    /*
     * @return true, if object is kernel.Node
     */
    boolean isNode(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Node;
    }

    /*
     * @return true, if object is kernel.Place
     */
    boolean isPlace(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Place;
    }

    /*
     * @return true, if object is kernel.Transition
     */
    boolean isTransition(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Transition;
    }

    /*
     * Joins these kernel-nodes in net.
     */
    protected void join(Object netobject1, Object netobject2) {
        de.huberlin.informatik.pnk.kernel.Node n1;
        de.huberlin.informatik.pnk.kernel.Node n2;
        n1 = (de.huberlin.informatik.pnk.kernel.Node)netobject1;
        n2 = (de.huberlin.informatik.pnk.kernel.Node)netobject2;
        n1.join(n2, this.editor);
    }

    /*
     * Creates a new Arc in kernel.Net.
     */
    protected Object newArc(Object source, Object target) {
        Graph graph = this.getGraph();
        Editor editor = this.getEditor();
        if (graph instanceof Net) {
            Net n = (Net)graph;
            de.huberlin.informatik.pnk.kernel.Node s;
            de.huberlin.informatik.pnk.kernel.Node t;
            s = (de.huberlin.informatik.pnk.kernel.Node)source;
            t = (de.huberlin.informatik.pnk.kernel.Node)target;
            try {
                if (s instanceof de.huberlin.informatik.pnk.kernel.Transition && t instanceof de.huberlin.informatik.pnk.kernel.Transition)
                    return new de.huberlin.informatik.pnk.kernel.TransitionArc(n, s, t, editor);
                if (s instanceof de.huberlin.informatik.pnk.kernel.Place && t instanceof de.huberlin.informatik.pnk.kernel.Place)
                    return new de.huberlin.informatik.pnk.kernel.PlaceArc(n, s, t, editor);
                return new de.huberlin.informatik.pnk.kernel.Arc(n, s, t, editor);
            } catch (RuntimeException r) {
                editor.error(" ###### wrong arc type");
                return null;
            }
        } else {
            editor.error(" ### wrong net type");
            return null;
        }
    }

    /*
     * Creates a new Edge in kernel-net.
     */
    protected Object newEdge(Object source, Object target) {
        Graph graph = this.getGraph();
        de.huberlin.informatik.pnk.kernel.Node s;
        de.huberlin.informatik.pnk.kernel.Node t;
        s = (de.huberlin.informatik.pnk.kernel.Node)source;
        t = (de.huberlin.informatik.pnk.kernel.Node)target;
        return new de.huberlin.informatik.pnk.kernel.Edge(graph, s, t);
    }

    /*
     * Creates a node in kernel.Graph.
     * @param name name of the new node
     * @return new node in kernel.Graph
     */
    protected Object newNode(String name) {
        Graph graph = this.getGraph();
        Editor editor = this.getEditor();
        if (graph instanceof Graph)
            return new de.huberlin.informatik.pnk.kernel.Node(graph, name);
        else {
            editor.error(" ### wrong graph type");
            return null;
        }
    }

    /*
     * Creates a place in kernel.Net.
     * @param name name of place
     * @return a place of kernel.Net or null.
     */
    protected Object newPlace(String name) {
        Graph graph = this.getGraph();
        Editor editor = this.getEditor();
        if (graph instanceof Net) {
            try {
                return new de.huberlin.informatik.pnk.kernel.Place((Net)graph, name, editor);
            } catch (RuntimeException e) {
                editor.error(" ### place not allowed for this net type");
                return null;
            }
        } else {
            editor.error(" ### wrong graph type");
            return null;
        }
    }

    /*
     * Creates a transition in kernel.Net.
     * @param name transitionname
     * @return transition of kernel.Net or null.
     */
    protected Object newTransition(String name) {
        Graph graph = this.getGraph();
        Editor editor = this.getEditor();
        if (graph instanceof Net) {
            try {
                return new de.huberlin.informatik.pnk.kernel.Transition((Net)graph, name, editor);
            } catch (RuntimeException e) {
                editor.error(" ### transition not allowed for this net type");
                return null;
            }
        } else {
            editor.error(" ### wrong graph type");
            return null;
        }
    }

    /**
     * Set the value of editor.
     * @param v  Value to assign to editor.
     */
    protected void setEditor(Editor v) {
        this.editor = v;
    }

    /**
     * Set the value of graph.
     * @param v  Value to assign to graph.
     */
    protected void setGraph(Object v) {
        this.graph = (Graph)v;
    }

    protected void setName(String name) {
        graph.setName(name);
    }

    /*
     * Sets the position-value of netobject.
     */
    protected void setPosition(Object netobject, Point pos, int pageId) {
        if (netobject instanceof Extendable) {
            Extendable e = (Extendable)netobject;
            e.setPosition(pos, pageId);
        }
    }

    /*
     * Spits a kernel-node into two nodes.
     */
    protected Object split(Vector edges, Object netobject) {
        de.huberlin.informatik.pnk.kernel.Node n;
        n = (de.huberlin.informatik.pnk.kernel.Node)netobject;
        return n.split(edges, this.editor);
    }
} // GraphProxy
