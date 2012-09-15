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
package fr.unicaen.iota.simulator.editor;

import de.huberlin.informatik.pnk.kernel.Extendable;
import de.huberlin.informatik.pnk.kernel.Graph;
import de.huberlin.informatik.pnk.kernel.Net;
import java.awt.Point;
import java.util.*;

/**
 * GraphProxy
 *
 * The editor uses this class to access kernel-net
 * or kernel-graph. Therefor it calls the methods
 * of this class.
 *
 * Created after de.huberlin.informatik.pnk.editor.GraphProxy
 */
class GraphProxy {

    private Editor editor;
    private Graph graph;

    /**
     * Class constructor.
     * @param editor the editor that uses this proxy.
     * @param graph the graph/net this is the proxy for.
     */
    protected GraphProxy(Editor editor, Object graph) {
        this.setEditor(editor);
        this.setGraph(graph);
    }

    /**
     * Set extension of netobject in net.
     * @param id name of extension
     * @param value value of extension
     */
    protected void changeExtension(Object netobject, String id, String value) {
        if (netobject instanceof Extendable) {
            Extendable e = (Extendable) netobject;
            e.setExtension(this.editor, id, value);
            if ("name".equals(id) && (netobject instanceof Graph)) {
                /*if net name was changed inform the application control -> label update*/
                this.editor.applicationControl.netNameChanged();
            }
        }
    }

    protected void closeGraph() {
        this.graph = null;
    }

    /**
     * Deletes an object in kernel.Net.
     * @param netobject the object should be deleted
     */
    protected void delete(de.huberlin.informatik.pnk.kernel.Node netobject) {
        netobject.delete(editor);
    }

    protected void delete(de.huberlin.informatik.pnk.kernel.Edge netobject) {
        netobject.delete(editor);
    }

    protected void delete(Object netobject) {
        if (netobject instanceof de.huberlin.informatik.pnk.kernel.Node) {
            ((de.huberlin.informatik.pnk.kernel.Node) netobject).delete(editor);
        } else if (netobject instanceof de.huberlin.informatik.pnk.kernel.Edge) {
            ((de.huberlin.informatik.pnk.kernel.Edge) netobject).delete(editor);
        }
    }

    /**
     * @return a vector containing all netobjects of kernel.Net / kernel.Graph
     */
    protected java.util.List<Object> getAllNetobjects() {
        // create a list of all netobjects
        java.util.List<Object> netobjects = new ArrayList<Object>();
        netobjects.addAll(graph.getNodes());
        netobjects.addAll(graph.getEdges());
        return netobjects;
    }

    /**
     * Get the value of editor.
     * @return Value of editor.
     */
    protected Editor getEditor() {
        return editor;
    }

    /**
     * Get extension of an extendable
     * @param extendable owner of extension
     * @param id id of wanted extension
     */
    protected Object getExtension(Object extendable, String id) {
        if (extendable instanceof Extendable) {
            return ((Extendable) extendable).getExtension(id);
        }
        return null;
    }

    protected Enumeration getExtensionIds(String classIdentifier) {
        return this.graph.getSpecification().getSpecTab().getExtIds(classIdentifier);
    }

    /**
     * Get a hashtable with all extensions of a netobject
     * Entrys have the form: String id -> String value
     */
    protected Map<String, String> getExtensionIdToValue(Object ext) {
        if (ext instanceof de.huberlin.informatik.pnk.kernel.Extendable) {
            de.huberlin.informatik.pnk.kernel.Extendable extendable = (de.huberlin.informatik.pnk.kernel.Extendable) ext;
            Map<String, String> extIdToVal = new HashMap<String, String>();
            Map<String, de.huberlin.informatik.pnk.kernel.Extension> extIdToObj = extendable.getExtIdToObject();
            if (extIdToObj != null) {
                for (Map.Entry<String, de.huberlin.informatik.pnk.kernel.Extension> idval : extIdToObj.entrySet()) {
                    extIdToVal.put(idval.getKey(), idval.getValue().toString());
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
        return ((de.huberlin.informatik.pnk.kernel.Member) member).getId();
    }

    protected String getName() {
        return this.graph.getName();
    }

    /**
     * Gets position of netobject on Page pageId
     */
    protected Point getPosition(Object netobject, int pageId) {
        if (netobject instanceof Extendable) {
            Extendable e = (Extendable) netobject;
            return e.getPosition(pageId);
        }
        return new Point();
    }

    /**
     * @return kernels sourcenode of edge.
     */
    protected Object getSource(Object edge) {
        return ((de.huberlin.informatik.pnk.kernel.Edge) edge).getSource();
    }

    protected Hashtable getSpecificationTable() {
        return this.graph.getSpecification().getSpecTab().getSpecificationTable();
    }

    /**
     * @return kernels targetnode of edge.
     */
    protected Object getTarget(Object edge) {
        return ((de.huberlin.informatik.pnk.kernel.Edge) edge).getTarget();
    }

    /**
     * @return true, if object is kernel.Arc
     */
    boolean isArc(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Arc;
    }

    /**
     * @return true, if object is kernel.Node
     */
    boolean isNode(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Node;
    }

    /**
     * @return true, if object is kernel.Place
     */
    boolean isPlace(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Place;
    }

    /**
     * @return true, if object is kernel.Transition
     */
    boolean isTransition(Object netobject) {
        return netobject instanceof de.huberlin.informatik.pnk.kernel.Transition;
    }

    /**
     * Joins these kernel-nodes in net.
     */
    protected void join(Object netobject1, Object netobject2) {
        de.huberlin.informatik.pnk.kernel.Node n1;
        de.huberlin.informatik.pnk.kernel.Node n2;
        n1 = (de.huberlin.informatik.pnk.kernel.Node) netobject1;
        n2 = (de.huberlin.informatik.pnk.kernel.Node) netobject2;
        n1.join(n2, this.editor);
    }

    /**
     * Creates a new Arc in kernel.Net.
     */
    protected Object newArc(Object source, Object target) {
        if (graph instanceof Net) {
            Net n = (Net) graph;
            de.huberlin.informatik.pnk.kernel.Node s;
            de.huberlin.informatik.pnk.kernel.Node t;
            s = (de.huberlin.informatik.pnk.kernel.Node) source;
            t = (de.huberlin.informatik.pnk.kernel.Node) target;
            try {
                if (s instanceof de.huberlin.informatik.pnk.kernel.Transition && t instanceof de.huberlin.informatik.pnk.kernel.Transition) {
                    return new de.huberlin.informatik.pnk.kernel.TransitionArc(n, s, t, editor);
                } else if (s instanceof de.huberlin.informatik.pnk.kernel.Place && t instanceof de.huberlin.informatik.pnk.kernel.Place) {
                    return new de.huberlin.informatik.pnk.kernel.PlaceArc(n, s, t, editor);
                } else {
                    return new de.huberlin.informatik.pnk.kernel.Arc(n, s, t, editor);
                }
            } catch (RuntimeException r) {
                editor.error(" ###### wrong arc type");
                return null;
            }
        } else {
            editor.error(" ### wrong net type");
            return null;
        }
    }

    /**
     * Creates a new Edge in kernel-net.
     */
    protected Object newEdge(Object source, Object target) {
        de.huberlin.informatik.pnk.kernel.Node s;
        de.huberlin.informatik.pnk.kernel.Node t;
        s = (de.huberlin.informatik.pnk.kernel.Node) source;
        t = (de.huberlin.informatik.pnk.kernel.Node) target;
        return new de.huberlin.informatik.pnk.kernel.Edge(graph, s, t);
    }

    /**
     * Creates a node in kernel.Graph.
     * @param name name of the new node
     * @return new node in kernel.Graph
     */
    protected Object newNode(String name) {
        if (graph instanceof Graph) {
            return new de.huberlin.informatik.pnk.kernel.Node(graph, name);
        } else {
            editor.error(" ### wrong graph type");
            return null;
        }
    }

    /**
     * Creates a place in kernel.Net.
     * @param name name of place
     * @return a place of kernel.Net or null.
     */
    protected Object newPlace(String name) {
        if (graph instanceof Net) {
            try {
                return new de.huberlin.informatik.pnk.kernel.Place((Net) graph, name, editor);
            } catch (RuntimeException e) {
                editor.error(" ### place not allowed for this net type");
                return null;
            }
        } else {
            editor.error(" ### wrong graph type");
            return null;
        }
    }

    /**
     * Creates a transition in kernel.Net.
     * @param name transitionname
     * @return transition of kernel.Net or null.
     */
    protected Object newTransition(String name) {
        if (graph instanceof Net) {
            try {
                return new de.huberlin.informatik.pnk.kernel.Transition((Net) graph, name, editor);
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
        this.graph = (Graph) v;
    }

    protected void setName(String name) {
        graph.setName(name);
    }

    /**
     * Sets the position-value of netobject.
     */
    protected void setPosition(Object netobject, Point pos, int pageId) {
        if (netobject instanceof Extendable) {
            ((Extendable) netobject).setPosition(pos, pageId);
        }
    }

    /**
     * Spits a kernel-node into two nodes.
     */
    protected Object split(Vector edges, Object netobject) {
        return ((de.huberlin.informatik.pnk.kernel.Node) netobject).split(edges, this.editor);
    }
} // GraphProxy
