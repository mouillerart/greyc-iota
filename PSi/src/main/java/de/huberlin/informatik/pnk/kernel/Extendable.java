package de.huberlin.informatik.pnk.kernel;

import de.huberlin.informatik.pnk.kernel.base.*;
import java.awt.Point;
import java.util.Enumeration;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Extendable.java is part of the
   Petri Net Kernel Java reimplementation.
   Extendable.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Extendable.java,v $
   Revision 1.21  2001/12/20 11:59:38  efischer
 *** empty log message ***

   Revision 1.20  2001/12/06 13:37:36  gruenewa
   setDynamicExtensionAstString hinzugefuegt

   Revision 1.19  2001/10/16 10:56:46  gruenewa
   hasExtension() WIEDER !!! eingefuegt

   Revision 1.18  2001/10/11 16:57:51  oschmann
   Neue Release

   Revision 1.15  2001/06/04 15:44:02  efischer
 *** empty log message ***

   Revision 1.14  2001/05/11 17:21:46  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.13  2001/04/17 05:35:31  gruenewa
 *** empty log message ***

   Revision 1.12  2001/02/27 21:29:03  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.10  2001/02/13 10:41:42  hohberg
 *** empty log message ***

   Revision 1.9  2001/02/04 17:45:32  juengel
 *** empty log message ***

   Revision 1.8  2001/02/03 12:33:13  juengel
 *** empty log message ***

   Revision 1.7  2001/01/16 17:36:49  oschmann
   Neu! Jetzt mit ActionObjekt fuer Kommunikation!

   Revision 1.6  2000/12/15 17:20:52  juengel
   fast fertig

   Revision 1.12  2000/10/09 13:51:33  gruenewa
 *** empty log message ***

   Revision 1.11  2000/09/22 08:43:40  gruenewa
 *** empty log message ***

   Revision 1.10  2000/09/01 08:07:26  hohberg
   Code revision

   Revision 1.9  2000/08/30 14:22:45  hohberg
   Update of comments

   Revision 1.8  2000/08/11 09:23:02  gruenewa
 *** empty log message ***

   Revision 1.3  2000/05/17 14:11:22  juengel
   vorbereitung xml laden / speichern

   Revision 1.2  2000/05/10 12:28:28  hohberg
   New attribute id and method getId

   Revision 1.1  2000/04/06 10:36:18  gruenewa
 *** empty log message ***

   Revision 1.1.1.1  1999/07/08 17:51:25  rschulz
   import of paradigm java sources

 */

import java.util.Hashtable;
import java.util.Observer;
import java.util.Vector;

/**
 * The root class for all <em> extendable </em> classes of the package
 * <code> de.huberlin.informatik.pnk.kernel </code>.
 * Extendable are the classes of the {@link Member
 * member} class hierarchy, {@link Graph} and {@link Net}.<BR>
 * <code> Extendable </code> provides the infrastructure to administer
 * {@link Extension extensions} and solves the problem of instantiation
 * of the right {@link Extension extensions} for all classes of the
 * <code> Extendable </code> class hierarchy. This is possible, since the
 * instantiation procedure is fully independent of the actual {@link
 * Extension extensions} and the actual class. <br>
 *
 * @version 1.0
 */
public abstract class Extendable extends NetObservable {
    /**
     * The mapping of {@link Extension extension} identifiers to the
     * extension objects of this extendable specified by the current
     * {@link Specification specification}.
     */
    private Hashtable extIdToObject = new Hashtable();

    /**
     * The mapping of the Application identifiers to the
     * dynamic extensions.<BR>
     */
    private Hashtable applicationKeyToInstanceList = new Hashtable();

    /**
     * Gives the graph the member has registered with.
       <BR>    */
    private Graph graph = null;

    /**
     * The unique identification number of the extendable <BR>
     */
    private String id = "";

    private org.w3c.dom.Document dynExtDocument;
    /**
     * Constructor specifying the graph ({@link Net net}). <BR>
     * Initializes a new extendable object with each of its {@link Extension
     * extensions} set to its {@link Extension#isDefault() default} state.
     * <BR>
     * During runtime the class of the extendable object is retrieved. Then
     * the class hierarchy up to <code>Extendable</code> is followed
     * successively. For each class on the way up the {@link Extension
     * extension} identifiers and associated extension
     * class names mapped to it are retrieved from
     * <code>specification</code>. For each extension
     * class name an object of the class is instantiated. The
     * extension identifier and object are stored in the extIdToObject
     * table. <br>
     */
    protected Extendable(Graph graph) {
        setGraph(graph);
        setExtIdToObject(getSpecification().genExtIdToObject(this));
    }

    /**
     * Constructor specifying the <code>specification</code>.
     * (ONLY used by class {@link Graph Graph}).
     */
    protected Extendable(Specification specification) {
        setGraph((Graph) this);
        ((Graph) this).setSpecification(specification);
        setExtIdToObject(getSpecification().genExtIdToObject(this));
    } // protected  Extendable( Specification specification)

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    public Enumeration getApplicationKeys() {
        return applicationKeyToInstanceList.keys();
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Get Pnml-Node of the dynamic extension specified by
     * applicationKey, instanceKey and extensionName.
     */
    public org.w3c.dom.Node getDynamicExtension(String applicationKey, String instanceKey, String extensionName) {
        Hashtable instanceList = (Hashtable)applicationKeyToInstanceList.get(applicationKey);
        if (instanceList != null) {
            Hashtable dynamicExtensionList = (Hashtable)instanceList.get(instanceKey);
            if (dynamicExtensionList != null) {
                return (org.w3c.dom.Node)dynamicExtensionList.get(extensionName);
            }
        }
        if (dynExtDocument == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                System.err.println(pce);
                System.exit(1);
            }
            dynExtDocument = db.newDocument();
        }
        return null;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Returns a list (Enumeration) consisting of dynamic extension names
     * specified by applicationKey, instanceKey.
     */
    public Enumeration getDynExtensionKeys(String applicationKey, String instanceKey) {
        Hashtable instanceList = (Hashtable)applicationKeyToInstanceList.get(applicationKey);
        return ((Hashtable)instanceList.get(instanceKey)).keys();
    }

    public boolean hasExtension(String id) {
        return getExtension(id) != null;
    }

    /**
     * Gives the extension of this extendable identified by
     * <code>extId</code>.
     */
    public Extension getExtension(String extId) {
        Hashtable extTab = getExtIdToObject();
        if (extTab == null) return null;
        return (Extension)extTab.get(extId);
    } // public Extension getExtension( String extId)

    /**
     * Gives the table of all extensions of this extendable.
     */
    public Hashtable getExtIdToObject() {
        return extIdToObject;
    } // public Hashtable getExtIdToObject( )

    /**
     * Gives the graph of this extendable.
     */
    public Graph getGraph() {
        return graph;
    } // public Graph getGraph( )

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Returns the Pnml graphic node. -> dynamic extension
     * applicationKey = instanceKey = extensionName = "graphics"
     */
    public org.w3c.dom.Node getGraphicsInfo() {
        return getDynamicExtension("graphics", "graphics", "graphics");
    }

    /**
     * Insert the method's description here.
     * Creation date: (03.02.2001 14:49:41)
     */
    /**
     * Returns the ID of this net object.
     */
    public String getId() {
        return id;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Get an instance key list (enumeration) of an application
     * specified by applicationKey.
     */
    public Enumeration getInstanceKeys(String applicationKey) {
        return ((Hashtable)applicationKeyToInstanceList.get(applicationKey)).keys();
    }

    /**
     * Gives the net of this extendable.
     *
     * @exception ClassCastException if the {@link Graph graph}
     *  of this extendable is not a net.
     */
    public Net getNet() {
        try {
            return (Net)getGraph(); // exception if not a net
        } catch (ClassCastException e) {
            return null; // no net
        }
    } // public Net getNet( )

    /**
     * Gets the observer of this extendable, that is
     * the observer of the {@link Graph graph}.
     */
    protected Observer getObserver() {
        Net net = getNet();
        if (net == null)
            return null;
        else
            return net.getObserver();
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Get Offset of this net object on a page specified by pageId.
     * (This is mainly used for relative positioning, e.g. for
     * the graphical representation of extensions.)
     */
    public Point getOffset(int pageId) {
        Point thePoint = new Point();
        thePoint.x = 0;
        thePoint.y = 0;
        org.w3c.dom.Node graphicsNode = getDynamicExtension("graphics", "graphics", "graphics");
        if (graphicsNode != null) {
            org.w3c.dom.NodeList positionNodeList = ((Element)graphicsNode).getElementsByTagName("offset");
            for (int i = 0; i < positionNodeList.getLength(); i++) {
                org.w3c.dom.Node positionNode = positionNodeList.item(i);
                if (Integer.parseInt(((Element)positionNode).getAttribute("page")) == pageId) {
                    thePoint.x = Integer.parseInt(((Element)positionNode).getAttribute("x"));
                    thePoint.y = Integer.parseInt(((Element)positionNode).getAttribute("y"));
                }
            }
        }
        return thePoint;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Returns a page vector for this net object. (multiple
     * graphical represetaions of this object on several pages)
     */
    public Vector getPages() {
        Vector theVector = new Vector();
        org.w3c.dom.Node graphicsNode = getDynamicExtension("graphics", "graphics", "graphics");
        if (graphicsNode != null) {
            org.w3c.dom.NodeList positionNodeList = ((Element)graphicsNode).getElementsByTagName("position");
            for (int i = 0; i < positionNodeList.getLength(); i++) {
                org.w3c.dom.Node positionNode = positionNodeList.item(i);
                theVector.add(new Integer(Integer.parseInt(((Element)positionNode).getAttribute("page"))));
            }
        }
        return theVector;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Get absolute position of this net object on page 'pageId', e.g.
     * the position of a transition.
     */
    public Point getPosition(int pageId) {
        Point thePoint = new Point();
        thePoint.x = 0;
        thePoint.y = 0;
        org.w3c.dom.Node graphicsNode = getDynamicExtension("graphics", "graphics", "graphics");
        if (graphicsNode != null) {
            org.w3c.dom.NodeList positionNodeList = ((Element)graphicsNode).getElementsByTagName("position");
            for (int i = 0; i < positionNodeList.getLength(); i++) {
                org.w3c.dom.Node positionNode = positionNodeList.item(i);
                if (Integer.parseInt(((Element)positionNode).getAttribute("page")) == pageId) {
                    thePoint.x = Integer.parseInt(((Element)positionNode).getAttribute("x"));
                    thePoint.y = Integer.parseInt(((Element)positionNode).getAttribute("y"));
                }
            }
        }
        return thePoint;
    }

    /**
     * Gets the specification of this extendable.
     */
    public Specification getSpecification() {
        return graph.getSpecification();
    } // public Specification getSpecification( )

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    public boolean hasApplicationInfo() {
        return getApplicationKeys().hasMoreElements();
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Returns true, if a Pnml grphic node was specified for this
     * extendable.
     */
    public boolean hasGraphicsInfo() {
        if (getDynamicExtension("graphics", "graphics", "graphics") != null)
            return true;
        else
            return false;
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */

    public void setDynamicExtensionAsString(String applicationKey,
                                            String instanceKey,
                                            String extensionName,
                                            String extensionValue) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            org.w3c.dom.Node extensionNode =
                doc.createTextNode(extensionValue);

            setDynamicExtension(applicationKey,
                                instanceKey,
                                extensionName,
                                extensionNode);
        } catch (Exception e) {
            de.huberlin.informatik.pnk.appControl.base.D.d(e.toString());
        }
    }

    public String getDynamicExtensionAsString(String applicationKey,
                                              String instanceKey,
                                              String extensionName) {
        return getDynamicExtension(applicationKey,
                                   instanceKey,
                                   extensionName).toString();
    }

    /**
     * Set Pnml-Node 'theNode' for the dynamic extension specified by
     * applicationKey, instanceKey and extensionName.
     */
    public void setDynamicExtension(String applicationKey, String instanceKey, String extensionName, org.w3c.dom.Node theNode) {
        applicationKey.replace(' ', '_');
        Hashtable instanceList = (Hashtable)applicationKeyToInstanceList.get(applicationKey);
        if (instanceList == null) {
            instanceList = new Hashtable();
            applicationKeyToInstanceList.put(applicationKey, instanceList);
        }
        Hashtable dynamicExtensionList = (Hashtable)instanceList.get(instanceKey);
        if (dynamicExtensionList == null) {
            dynamicExtensionList = new Hashtable();
            instanceList.put(instanceKey, dynamicExtensionList);
        }
        if (dynExtDocument == null) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setValidating(false);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
            DocumentBuilder db = null;
            try {
                db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                System.err.println(pce);
                System.exit(1);
            }
            dynExtDocument = db.newDocument();
        }
        extensionName.replace(' ', '_');
        dynamicExtensionList.put(extensionName, dynExtDocument.importNode(theNode, true));
    }

    /**
     * Sets the {@link Extension extension} of this extendable identified
     * by <code>extId</code> to <code>newValue</code>.
     */
    public void setExtension(Object initiator, String extId, String newValue) {
        Extension e = getExtension(extId);
        //get  Extension with this extid
        if (e == null) {
            throw(new IllegalArgumentException("No Extension for " + extId));
        }
        e.valueOf(newValue);
        //set the new value
        Observer o = getObserver();
        if (o == null) return;
        ChangeExtension ue = new ChangeExtension(initiator, extId, newValue);
        o.update(this, ue);
    }

    /**
     * Sets all extensions of this extendable.
     * The extensions are given by <code>extIdToObject</code>.
     */
    protected void setExtIdToObject(Hashtable extIdToObject) {
        this.extIdToObject = extIdToObject;
    } // protected void setExtIdToObject( Hashtable extIdToObject)

    /**
     * Sets the graph of this extendable to <code>graph</code>.
     */
    protected void setGraph(Graph graph) {
        this.graph = graph;
    } // protected void setGraph( Graph graph)

    /**
     * Sets the id of this extendable to <code>id</code>.
     */
    public void setId(String id) {
        this.id = id;
    } // protected void setId( String id)

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Set Offset of this net object on a page specified by
     * pageId to 'thePoint'. (This is mainly used for relative
     * positioning, e.g. for the graphical representation of extensions.)
     */
    public void setOffset(Point thePoint, int pageId) {
        org.w3c.dom.Node graphicsNode = getDynamicExtension("graphics", "graphics", "graphics");
        if (graphicsNode == null)
            graphicsNode = dynExtDocument.createElement("graphics");
        org.w3c.dom.NodeList positionNodeList = ((Element)graphicsNode).getElementsByTagName("offset");
        org.w3c.dom.Node positionNode = null;
        for (int i = 0; i < positionNodeList.getLength(); i++) {
            org.w3c.dom.Node actNode = positionNodeList.item(i);
            if (Integer.parseInt(((Element)actNode).getAttribute("page")) == pageId)
                positionNode = actNode;
        }
        if (positionNode == null) {
            positionNode = dynExtDocument.createElement("offset");
            graphicsNode.appendChild(positionNode);
        }
        ((Element)positionNode).setAttribute("page", "" + pageId);
        ((Element)positionNode).setAttribute("x", "" + thePoint.x);
        ((Element)positionNode).setAttribute("y", "" + thePoint.y);
        setDynamicExtension("graphics", "graphics", "graphics", graphicsNode);
    }

    /**
     * Insert the method's description here.
     * Creation date: (04.07.00 10:04:17)
     */
    /**
     * Set absolute position of this extendable on page 'pageId', e.g.
     * the position of a transition.
     */
    public void setPosition(Point thePoint, int pageId) {
        org.w3c.dom.Node graphicsNode = getDynamicExtension("graphics", "graphics", "graphics");
        if (graphicsNode == null)
            graphicsNode = dynExtDocument.createElement("graphics");
        org.w3c.dom.NodeList positionNodeList = ((Element)graphicsNode).getElementsByTagName("position");
        org.w3c.dom.Node positionNode = null;
        for (int i = 0; i < positionNodeList.getLength(); i++) {
            org.w3c.dom.Node actNode = positionNodeList.item(i);
            if (Integer.parseInt(((Element)actNode).getAttribute("page")) == pageId)
                positionNode = actNode;
        }
        if (positionNode == null) {
            positionNode = dynExtDocument.createElement("position");
            graphicsNode.appendChild(positionNode);
        }
        ((Element)positionNode).setAttribute("page", "" + pageId);
        ((Element)positionNode).setAttribute("x", "" + thePoint.x);
        ((Element)positionNode).setAttribute("y", "" + thePoint.y);
        setDynamicExtension("graphics", "graphics", "graphics", graphicsNode);
    }

    /**
     * Sends the new value of extension  identified by  <code>extId</code>
     * to the observer of this extendable.
     */
    public void updateExtension(Object initiator, String extId,
                                String newValue) {
        Observer o = getObserver();
        if (o != null) {
            ChangeExtension ue = new ChangeExtension(initiator, extId, newValue);
            o.update(this, ue);
        }
    }
} // abstract class Extendable extends Observable
