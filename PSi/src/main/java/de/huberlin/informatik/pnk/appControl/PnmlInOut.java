package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.appControl.base.D;

import de.huberlin.informatik.pnk.kernel.*;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.crimson.tree.XmlDocument;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Insert the type's description here.
 * Creation date: (20.06.00 13:35:50)
 * @author: Matthias Juengel
 */
public class PnmlInOut extends InOut {
    public static String inOutName = "Pnml 1.0";
    public static String stdFileExt = "pnml";
    public static Boolean multipleAllowed = new Boolean(true);

    /**Writes the Structure of theStructure as XML Element Structure in actNode
     */
    public PnmlInOut(ApplicationControl ac) {
        super(ac);
    }

    public void dump(Hashtable hash) {
        for (Enumeration keys = hash.keys(); keys.hasMoreElements(); ) {
            String actKey = (String)keys.nextElement();
            D.d(actKey + ":");
            Hashtable extensionHash = (Hashtable)hash.get(actKey);
            if (extensionHash != null) {
                for (Enumeration extensions = extensionHash.keys(); extensions.hasMoreElements(); ) {
                    String actExt = (String)extensions.nextElement();
                    D.d(actExt + ": " + extensionHash.get(actExt));
                }
            }
        }
    }

    private Graph getDefaultNetWithDynamicSpecification(org.w3c.dom.Node netNode) {
        String STRING_EXTENSION = "de.huberlin.informatik.pnk.netElementExtensions.base.StringExtension";
        NodeList netElementList = ((Element)netNode).getChildNodes();
        org.w3c.dom.Node actNetElement;
        String actNetElementName;
        String className = "";
        Hashtable specificationTable = new Hashtable();
        for (int i = 0; i < netElementList.getLength(); i++) {
            actNetElement = netElementList.item(i);
            actNetElementName = actNetElement.getNodeName();
            if (actNetElementName.equals("place")) {
                className = "de.huberlin.informatik.pnk.kernel.Place";
            } else if (actNetElementName.equals("transition")) {
                className = "de.huberlin.informatik.pnk.kernel.Transition";
            } else if (actNetElementName.equals("node")) {
                className = "de.huberlin.informatik.pnk.kernel.Node";
            } else if (actNetElementName.equals("arc")) {
                className = "de.huberlin.informatik.pnk.kernel.Arc";
            } else if (actNetElementName.equals("edge")) {
                className = "de.huberlin.informatik.pnk.kernel.Edge";
            } else {
                className = "de.huberlin.informatik.pnk.kernel.Net";
            }
            Hashtable extensionList = (Hashtable)specificationTable.get(className);
            if (extensionList == null) {
                extensionList = new Hashtable();
            }
            if ((actNetElementName.equals("place") ||
                 actNetElementName.equals("transition") ||
                 actNetElementName.equals("node") ||
                 actNetElementName.equals("edge") ||
                 actNetElementName.equals("arc"))) {
                if (actNetElement.hasChildNodes()) {
                    NodeList extensionChildList = ((Element)actNetElement).getChildNodes();
                    org.w3c.dom.Node actExtension;
                    String actExtensionName;
                    for (int j = 0; j < extensionChildList.getLength(); j++) {
                        actExtension = extensionChildList.item(j);
                        actExtensionName = actExtension.getNodeName();
                        if (!(actExtensionName.equals("#text") || actExtensionName.equals("graphics"))) {
                            extensionList.put(actExtensionName, STRING_EXTENSION);
                        }
                    }
                }
            } else if (!(actNetElementName.equals("#text") || actNetElementName.equals("graphics"))) {
                extensionList.put(actNetElementName, STRING_EXTENSION);
            }
            specificationTable.put(className, extensionList);
        }
        //dump(specificationTable);
        Graph newNet = ac.getNewNet(specificationTable, ((Element)netNode).getAttribute("type"));
        return newNet;
    }

    private void loadDynamicExtension(Extendable netElement, org.w3c.dom.Node node) {
        NodeList appNodeList = ((Element)node).getChildNodes();
        for (int i1 = 0; i1 < appNodeList.getLength(); i1++) {
            org.w3c.dom.Node appNode = appNodeList.item(i1);
            String appKey = appNode.getNodeName();
            if (appKey != null && !(appKey.equals("#text"))) {
                String instKey = ((Element)appNode).getAttribute("instance");
                if (instKey != null) {
                    NodeList dynExtNodeList = ((Element)appNode).getChildNodes();
                    for (int i2 = 0; i2 < dynExtNodeList.getLength(); i2++) {
                        org.w3c.dom.Node dynExtNode = dynExtNodeList.item(i2);
                        String extKey = dynExtNode.getNodeName();
                        if (extKey != null && !(extKey.equals("#text"))) {
                            org.w3c.dom.Node extValue = dynExtNode.getFirstChild();
                            if (extValue != null) {
                                netElement.setDynamicExtension(appKey, instKey, extKey, extValue);
                            }
                        }
                    }
                }
            }
        }
    }

    /**Returns the Net specified by theURL as a de.hu-berlin.informatik.pnk.kernel.Net
     */
    public Vector load(URL theURL) {
        Vector v = new Vector();
        InputSource input;
        Graph theNet = null;

        // Step 1: create a DocumentBuilderFactory and configure it
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        // Optional: set various configuration options
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);
        //    dbf.setCoalescing(putCDATAIntoText);
        // The opposite of creating entity ref nodes is expanding them inline
        //    dbf.setExpandEntityReferences(!createEntityRefs);
        // At this point the DocumentBuilderFactory instance can be saved
        // and reused to create any number of DocumentBuilder instances
        // with the same configuration options.
        // Step 2: create a DocumentBuilder that satisfies the constraints
        // specified by the DocumentBuilderFactory
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
            return v;
        }

        // Set an ErrorHandler before parsing
        //   db.setErrorHandler(new MyErrorHandler(System.err));
        // Step 3: parse the input file
        Document doc = null;
        try {
            doc = db.parse(theURL.toString());
        } catch (SAXException se) {
            System.err.println(se.getMessage());
            return v;
        } catch (IOException ioe) {
            System.err.println(ioe);
            return v;
        }
        try {
            D.d(theURL.toString() + " ist URL des Files " + " ");
            NodeList netElementList;
            NodeList childList;
            NodeList applicationTagList;
            NodeList dynExtList;
            Element root;
            org.w3c.dom.Node theNetNode;
            org.w3c.dom.Node theNode;
            String name, id, type;
            Member actMember;
            root = doc.getDocumentElement();
            //root.normalize();
            NodeList netNodes = root.getElementsByTagName("net");

            // ########## Pending... mehrere Netze... und Fähigkeit zu Graph!!!
            // Abhängig davon, was die nächste Zeile zurückliefert...
            // oder Unterschieden nach Place Transition Arc bzw. Node Edge in der Netzdatei???
            for (int netCount = 0; netCount < netNodes.getLength(); netCount++) {
                theNetNode = netNodes.item(netCount);
                theNet = ac.getNewNet(((Element)theNetNode).getAttribute("type"));
                if (theNet != null) {
                    D.d("================= Netz erhalten!!! " + theNet);
                } else {
                    D.d("################# Netz nicht erhalten!!! Typ unbekannt?");
                    D.d("################# Default-Net mit dynamischer Spezifikation erzeugt!!");
                    theNet = getDefaultNetWithDynamicSpecification(theNetNode);
                }
                if (theNet != null) {
                    //          D.d("Netz id (wozu???): " + ((Element)theNetNode).getAttribute("id"));
                    theNet.setId(((Element)theNetNode).getAttribute("id"));
                    org.w3c.dom.Node actExtensionNode, actExtensionSubNode, nameNode, actNetElement, actApplicationNode, actDynExtNode;
                    netElementList = ((Element)theNetNode).getChildNodes();
                    for (int i = 0; i < netElementList.getLength(); i++) {
                        type = "";
                        name = "";
                        id = "";
                        actNetElement = netElementList.item(i);
                        String actNetElementName = actNetElement.getNodeName();

                        ///                     D.d(actNetElementName + " " + " " );
                        actMember = null;
                        if (actNetElementName.equals("place")) {
                            id = ((Element)actNetElement).getAttribute("id");
                            actMember = new Place((Net)theNet, "", this, id);
                        }
                        if (actNetElementName.equals("transition")) {
                            id = ((Element)actNetElement).getAttribute("id");
                            actMember = new Transition((Net)theNet, "", this, id);
                        }
                        if (actNetElementName.equals("node")) {
                            id = ((Element)actNetElement).getAttribute("id");
                            actMember = new de.huberlin.informatik.pnk.kernel.Node(theNet, id);
                        }
                        if (actNetElementName.equals("arc")) {
                            String source, target;
                            id = ((Element)actNetElement).getAttribute("id");
                            type = ((Element)actNetElement).getAttribute("type");
                            source = ((Element)actNetElement).getAttribute("source");
                            target = ((Element)actNetElement).getAttribute("target");
                            if (type.equals("PlaceArc")) {
                                actMember = new PlaceArc((Net)theNet, source, target, this, id);
                            } else if (type.equals("TransitionArc")) {
                                actMember = new TransitionArc((Net)theNet, source, target, this, id);
                            } else {
                                actMember = new Arc((Net)theNet, source, target, this, id);
                            }
                        }
                        if (actNetElementName.equals("edge")) {
                            String source, target;
                            id = ((Element)actNetElement).getAttribute("id");
                            type = ((Element)actNetElement).getAttribute("type");
                            source = ((Element)actNetElement).getAttribute("source");
                            target = ((Element)actNetElement).getAttribute("target");
                            if (type.equals("PlaceArc")) {
                                actMember = new PlaceArc((Net)theNet, source, target, this, id);
                            } else if (type.equals("TransitionArc")) {
                                actMember = new TransitionArc((Net)theNet, source, target, this, id);
                            } else {
                                actMember = new Arc((Net)theNet, source, target, this, id);
                            }
                        }
                        if (actNetElementName.equals("place") ||
                            actNetElementName.equals("transition") ||
                            actNetElementName.equals("node") ||
                            actNetElementName.equals("edge") ||
                            actNetElementName.equals("arc")) {
                            //actNetElement is a Member of the Net
                            if (actNetElement.hasChildNodes()) {
                                childList = ((Element)actNetElement).getChildNodes();
                                for (int j = 0; j < childList.getLength(); j++) {
                                    actExtensionNode = childList.item(j);
                                    String nodeName = actExtensionNode.getNodeName().trim();
                                    //                                          if ( nodeName.equals("pnkAppInfo") ) {
                                    /*if (nodeName.equals("name")) {
                                       actMember.setExtension(this, "name", "Hallo");
                                       }*/
                                    if (nodeName.equals("graphics")) {
                                        actMember.setDynamicExtension("graphics", "graphics", "graphics", actExtensionNode);
                                    } else if (nodeName.equals("pnkAppInfo")) {
                                        loadDynamicExtension(actMember, actExtensionNode);
                                    } else if (!(nodeName.equals("#text") || nodeName.equals("graphics"))) {
                                        Extension e = (Extension)actMember.getExtIdToObject().get(nodeName);
                                        if (e != null) {
                                            e.load(actExtensionNode);
                                        }
                                    } //if ( !( nodeName.equals("#text") ) )
                                } //for (int j=0; j<childList.getLength(); j++) {
                            } //if (actNetElement.hasChildNodes())
                        } else if (actNetElementName.equals("pnkAppInfo")) {
                            loadDynamicExtension(theNet, actNetElement);
                        } else {
                            Extension e = (Extension)theNet.getExtIdToObject().get(actNetElementName);
                            /*load global extension*/
                            if (e != null) {
                                e.load(actNetElement);
                            }
                        }
                    }
                    v.addElement(theNet);
                } else {
                    D.d("dynamic Specification doesn't work!");
                } // theNet != null
            } //for(netCount)
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return v;
    } //loadNet()

    /**Writes theNet to theURL
     */
    public void save(Vector theNets, URL theURL) {
        FileWriter theFileWriter;
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
        Document doc = db.newDocument();
        org.w3c.dom.Node pnmlNode, netNode, actArcNode, actExtensionNode, pnkAppInfoNode, pnkAppInstanceNode, actDynExtensionNode;
        Extension actExtension;
        String actApplicationKey = "";
        String actInstanceKey = "";
        String actDynExtKey = "";

        pnmlNode = doc.appendChild(doc.createElement("pnml"));

        // ########## Pending... mehrere Netze... und Fähigkeit zu Graph!!!
        for (int netCount = 0; netCount < theNets.size(); netCount++) {
            Net theNet = (Net)theNets.elementAt(netCount);
            netNode = pnmlNode.appendChild(doc.createElement("net"));
            ((Element)netNode).setAttribute("id", theNet.getId());
            ((Element)netNode).setAttribute("type", ac.getNetType(theNet));

            Vector NodeList = theNet.getNodes();
            de.huberlin.informatik.pnk.kernel.Node actNetNode;
            org.w3c.dom.Node actDocNode;
            for (Enumeration e = NodeList.elements(); e.hasMoreElements(); ) {
                actNetNode = (de.huberlin.informatik.pnk.kernel.Node)(e.nextElement());
                if (((Object)actNetNode).getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Place")) {
                    actDocNode = doc.createElement("place");
                } else if (((Object)actNetNode).getClass().getName().equals("de.huberlin.informatik.pnk.kernel.Transition")) {
                    actDocNode = doc.createElement("transition");
                } else {
                    actDocNode = doc.createElement("node");
                }
                ((Element)actDocNode).setAttribute("id", actNetNode.getId());
                //extensions
                Enumeration f = actNetNode.getExtIdToObject().keys();
                for (; f.hasMoreElements(); ) {
                    String extensionName = (String)f.nextElement();
                    actExtension = actNetNode.getExtension(extensionName);
                    actExtensionNode = doc.createElement(extensionName);
                    actExtension.save(doc, actExtensionNode);
                    actDocNode.appendChild(actExtensionNode);
                }
                //
                //graphics
                if (actNetNode.hasGraphicsInfo()) {
                    actDynExtensionNode = doc.createElement("graphics");
                    actDynExtensionNode = doc.importNode(actNetNode.getGraphicsInfo(), true);
                    actDocNode.appendChild(actDynExtensionNode);
                }
                if (actNetNode.hasApplicationInfo()) {
                    pnkAppInfoNode = doc.createElement("pnkAppInfo");
                    Enumeration g = actNetNode.getApplicationKeys();
                    for (; g.hasMoreElements(); ) {
                        actApplicationKey = (String)g.nextElement();
                        Enumeration h = actNetNode.getInstanceKeys(actApplicationKey);
                        for (; h.hasMoreElements(); ) {
                            actInstanceKey = (String)h.nextElement();
                            pnkAppInstanceNode = doc.createElement(actApplicationKey);
                            ((Element)pnkAppInstanceNode).setAttribute("instance", actInstanceKey);
                            Enumeration i = actNetNode.getDynExtensionKeys(actApplicationKey, actInstanceKey);
                            for (; i.hasMoreElements(); ) {
                                actDynExtKey = (String)i.nextElement();
                                actDynExtensionNode = doc.createElement(actDynExtKey);
                                actDynExtensionNode.appendChild(doc.importNode(actNetNode.getDynamicExtension(actApplicationKey, actInstanceKey, actDynExtKey), true));
                                pnkAppInstanceNode.appendChild(actDynExtensionNode);
                            }
                            pnkAppInfoNode.appendChild(pnkAppInstanceNode);
                        }
                    }
                    if (actApplicationKey != "graphics" && actInstanceKey != "graphics" && actDynExtKey != "graphics") {
                        actDocNode.appendChild(pnkAppInfoNode);
                    }
                }
                //
                netNode.appendChild(actDocNode);
            }
            Vector arcList = theNet.getArcs();
            Edge actArc;
            for (Enumeration e = arcList.elements(); e.hasMoreElements(); ) {
                actArc = (Edge)(e.nextElement());
                if (actArc instanceof Arc) {
                    actArcNode = doc.createElement("arc");
                } else {
                    actArcNode = doc.createElement("edge");
                }
                ((Element)actArcNode).setAttribute("id", actArc.getId());
                ((Element)actArcNode).setAttribute("source", actArc.getSource().getId());
                ((Element)actArcNode).setAttribute("target", actArc.getTarget().getId());
                //store type of arc if it is placeArc or transitionArc
                String type;
                if (actArc instanceof PlaceArc) {
                    type = "PlaceArc";
                    ((Element)actArcNode).setAttribute("type", type);
                } else if (actArc instanceof TransitionArc) {
                    type = "TransitionArc";
                    ((Element)actArcNode).setAttribute("type", type);
                }
                //extensions
                Enumeration f = actArc.getExtIdToObject().keys();
                for (; f.hasMoreElements(); ) {
                    String extensionName = (String)f.nextElement();
                    actExtension = actArc.getExtension(extensionName);
                    actExtensionNode = doc.createElement(extensionName);
                    actExtension.save(doc, actExtensionNode);
                    actArcNode.appendChild(actExtensionNode);
                }
                // /extensions
                //graphics
                if (actArc.hasGraphicsInfo()) {
                    actDynExtensionNode = doc.createElement("graphics");
                    actDynExtensionNode = doc.importNode(actArc.getGraphicsInfo(), true);
                    actArcNode.appendChild(actDynExtensionNode);
                }
                //dynamic extensions
                if (actArc.hasApplicationInfo()) {
                    pnkAppInfoNode = doc.createElement("pnkAppInfo");
                    Enumeration g = actArc.getApplicationKeys();
                    for (; g.hasMoreElements(); ) {
                        actApplicationKey = (String)g.nextElement();
                        Enumeration h = actArc.getInstanceKeys(actApplicationKey);
                        for (; h.hasMoreElements(); ) {
                            actInstanceKey = (String)h.nextElement();
                            pnkAppInstanceNode = doc.createElement(actApplicationKey);
                            ((Element)pnkAppInstanceNode).setAttribute("instance", actInstanceKey);
                            Enumeration i = actArc.getDynExtensionKeys(actApplicationKey, actInstanceKey);
                            for (; i.hasMoreElements(); ) {
                                actDynExtKey = (String)i.nextElement();
                                actDynExtensionNode = doc.createElement(actDynExtKey);
                                actDynExtensionNode.appendChild(doc.importNode(actArc.getDynamicExtension(actApplicationKey, actInstanceKey, actDynExtKey), true));
                                pnkAppInstanceNode.appendChild(actDynExtensionNode);
                            }
                            pnkAppInfoNode.appendChild(pnkAppInstanceNode);
                        }
                    }
                    if (actApplicationKey != "graphics" && actInstanceKey != "graphics" && actDynExtKey != "graphics") {
                        actArcNode.appendChild(pnkAppInfoNode);
                    }
                }
                //
                netNode.appendChild(actArcNode);
            }
            //extensions of the net
            Enumeration f = theNet.getExtIdToObject().keys();
            for (; f.hasMoreElements(); ) {
                String extensionName = (String)f.nextElement();
                actExtension = theNet.getExtension(extensionName);
                actExtensionNode = doc.createElement(extensionName);
                actExtension.save(doc, actExtensionNode);
                netNode.appendChild(actExtensionNode);
            }
            //
            //dynamic extensions of the Net
            if (theNet.hasApplicationInfo()) {
                pnkAppInfoNode = doc.createElement("pnkAppInfo");
                Enumeration g = theNet.getApplicationKeys();
                for (; g.hasMoreElements(); ) {
                    actApplicationKey = (String)g.nextElement();
                    Enumeration h = theNet.getInstanceKeys(actApplicationKey);
                    for (; h.hasMoreElements(); ) {
                        actInstanceKey = (String)h.nextElement();
                        pnkAppInstanceNode = doc.createElement(actApplicationKey);
                        ((Element)pnkAppInstanceNode).setAttribute("instance", actInstanceKey);
                        Enumeration i = theNet.getDynExtensionKeys(actApplicationKey, actInstanceKey);
                        for (; i.hasMoreElements(); ) {
                            actDynExtKey = (String)i.nextElement();
                            actDynExtensionNode = doc.createElement(actDynExtKey);
                            actDynExtensionNode.appendChild(doc.importNode(theNet.getDynamicExtension(actApplicationKey, actInstanceKey, actDynExtKey), true));
                            pnkAppInstanceNode.appendChild(actDynExtensionNode);
                        }
                        pnkAppInfoNode.appendChild(pnkAppInstanceNode);
                    }
                }
                if (actApplicationKey != "graphics" && actInstanceKey != "graphics" && actDynExtKey != "graphics") {
                    netNode.appendChild(pnkAppInfoNode);
                }
            }
            //
        } //for
        /*cast into XmlDocument (a class of the crimson parser)*/
        XmlDocument xmlDoc = (XmlDocument)doc;
        try {
            /*set a file writer to the output file*/
            theFileWriter = new FileWriter(theURL.getFile());
            /*write the document into the file using ISO-8859-1 encoding*/
            xmlDoc.write(theFileWriter, "ISO-8859-1");
        } catch (IOException e) {
            System.err.println("  " + e.getMessage());
        }
    } //save()
}
