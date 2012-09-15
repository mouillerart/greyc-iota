package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.appControl.base.D;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 * Insert the type's description here.
 * Creation date: (29.07.00 21:16:11)
 * @author: Matthias Jörgel
 */
/**
 * management of the tool specification
 */
public class ToolSpecification {
    private URL theURL;
    // für ACRecources

    private Hashtable nettypeId2nettypeURL = new Hashtable();
    // für ACRecources

    private Hashtable apptypeId2apptypeinfo = new Hashtable();
    // für ACRecources

    private Hashtable ioFormatId2ioformatinfo = new Hashtable();

    // für ACRecources

    private String idOfStandardApplication;
    // für ACRecources

    private String idOfStandardIOFormat;
    // für ACRecources

    private String idOfStandardNettype;
/**
 * 'theURL' specifies the location of the tool specification.
 * The private load method is called, that parses the tool specification
 */
    public ToolSpecification(URL theURL) {
        this.theURL = theURL;
        load();
    }

/**
 * returns a hashtable that contains all application types and
 * special infos, e.g. allowed net types, max instances, corresponding class...
 */
    public Hashtable getApptypes() {
        return apptypeId2apptypeinfo;
    }

/**
 * returns ID of standard application
 */
    public String getIdOfStandardApplication() {
        return idOfStandardApplication;
    }

/**
 * returns ID of standard file format
 */
    public String getIdOfStandardIOFormat() {
        return idOfStandardIOFormat;
    }

/**
 * returns ID of standard net type
 */
    public String getIdOfStandardNettype() {
        return idOfStandardNettype;
    }

/**
 * returns a hashtable that contains all file formats and
 * additional infos, e.g. allowed net types, corresponding class...
 */
    public Hashtable getIOFormattypes() {
        return ioFormatId2ioformatinfo;
    }

/**
 * returns a hashtable that contains all net types and
 * additional infos, e.g. corresponding net type specification...
 */
    public Hashtable getNettypes() {
        return nettypeId2nettypeURL;
    }

/**
 * Insert the method's description here.
 * Creation date: (30.07.00 11:30:34)
 */
    private final void load() {
        try {
            //D.d("---> ToolSpecification: " + theURL.toString());
            // Step 1: create a DocumentBuilderFactory and configure it
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // Optional: set various configuration options
            dbf.setValidating(false);
            dbf.setIgnoringComments(true);
            dbf.setIgnoringElementContentWhitespace(true);
//		dbf.setCoalescing(putCDATAIntoText);
            // The opposite of creating entity ref nodes is expanding them inline
//		dbf.setExpandEntityReferences(!createEntityRefs);

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
                System.exit(1);
            }

//		// Set an ErrorHandler before parsing
//		db.setErrorHandler(new MyErrorHandler(System.err));

            // Step 3: parse the input file
            Document doc = null;
            try {
                doc = db.parse(theURL.toString());
            } catch (SAXException se) {
                System.err.println(se.getMessage());
                System.exit(1);
            } catch (IOException ioe) {
                System.err.println(ioe);
                System.exit(1);
            }

            NodeList theTagList;
            NodeList allowedNettypesList;
            org.w3c.dom.Node actTag, actNettypeTag;
            org.w3c.dom.Node allowedNettypesTag;
            Element root;

            root = doc.getDocumentElement();
            theTagList = root.getChildNodes();
            for (int i = 0; i < theTagList.getLength(); i++) {
                actTag = theTagList.item(i);
                String actTagName = actTag.getNodeName();
                //D.d(actTagName + " " + " " );
                ////////////////////////////
                if (actTagName == "nettype") {
                    URL typeSpecificationURL = new URL(((Element)actTag).getAttribute("typeSpecification"));
                    nettypeId2nettypeURL.put(((Element)actTag).getAttribute("id"),
                                             typeSpecificationURL);

/***********
                                // Step 3: parse the input file
                                Document actNettypeSpecificationDoc = null;
                                try {
                                        actNettypeSpecificationDoc = db.parse(typeSpecificationURL.toString());
                                } catch (SAXException se) {
                                        System.err.println("   Error: " + se.getMessage());
                                        // System.exit(1);
                                } catch (IOException ioe) {
                                        System.err.println("   Error: " + ioe);
                                        // System.exit(1);
                                }
                                if (actNettypeSpecificationDoc != null) {
                                    Element ntSpecRoot = actNettypeSpecificationDoc.getDocumentElement();
                                    String nettypeName = ntSpecRoot.getAttribute("name");
                                    Hashtable specificationTable = new Hashtable();
                                    NodeList extendableList = ntSpecRoot.getElementsByTagName("extendable");
                                    for (int j = 0; j < extendableList.getLength(); j++) {
                                        org.w3c.dom.Node actExtendableTag = extendableList.item(j);
                                        String extendableClassName = ((Element)actExtendableTag).getAttribute("class");
                                        Hashtable extensionName2ExtensionClassName = new Hashtable();
                                        NodeList extensionList = ((Element)actExtendableTag).getElementsByTagName("extension");
                                        for (int k = 0; k < extensionList.getLength(); k++) {
                                            org.w3c.dom.Node actExtensionTag = extensionList.item(k);
                                            extensionName2ExtensionClassName.put( ((Element)actExtensionTag).getAttribute("name"),
                                                                                  ((Element)actExtensionTag).getAttribute("class") );
                                        }
                                        specificationTable.put(extendableClassName, extensionName2ExtensionClassName);
                                    }
                                }
***********/
                }
                /////////////////////////////
                if (actTagName == "application") {
                    String maxinstancesString = ((Element)actTag).getAttribute("maxinstances");
                    int maxinstances;
                    if (maxinstancesString.equals("inf")) maxinstances = Integer.MAX_VALUE;
                    else maxinstances = Integer.parseInt(maxinstancesString);

                    allowedNettypesTag = ((Element)actTag).getElementsByTagName("allowedNettypes").item(0);
                    Vector allowedNettypes = null;
                    if (allowedNettypesTag != null) {
                        allowedNettypes = new Vector();
                        allowedNettypesList = ((Element)allowedNettypesTag).getElementsByTagName("ntref");
                        for (int j = 0; j < allowedNettypesList.getLength(); j++) {
                            actNettypeTag = allowedNettypesList.item(j);
                            allowedNettypes.addElement(((Element)actNettypeTag).getAttribute("ref"));
                        }
                        //D.d("+-+-+-+-+-+-+-+-+ App: " + allowedNettypes + " " + allowedNettypesList);
                    }
                    Vector apptypeinfo = new Vector();
                    apptypeinfo.add(((Element)actTag).getAttribute("mainClass"));
                    apptypeinfo.add(new Integer(maxinstances));
                    apptypeinfo.add(allowedNettypes);

                    apptypeId2apptypeinfo.put(((Element)actTag).getAttribute("id"), apptypeinfo);
                }
                if (actTagName == "format") {
                    allowedNettypesTag = ((Element)actTag).getElementsByTagName("allowedNettypes").item(0);
                    Vector allowedNettypes = null;
                    if (allowedNettypesTag != null) {
                        allowedNettypes = new Vector();
                        allowedNettypesList = ((Element)allowedNettypesTag).getElementsByTagName("ntref");
                        for (int j = 0; j < allowedNettypesList.getLength(); j++) {
                            actNettypeTag = allowedNettypesList.item(j);
                            allowedNettypes.addElement(((Element)actNettypeTag).getAttribute("ref"));
                        }
                        //D.d("+-+-+-+-+-+-+-+-+ IO: " + allowedNettypes + " " + allowedNettypesList);
                    }
                    Vector ioformatinfo = new Vector();
                    ioformatinfo.add(((Element)actTag).getAttribute("ioClass"));
                    ioformatinfo.add(allowedNettypes);

                    ioFormatId2ioformatinfo.put(((Element)actTag).getAttribute("id"), ioformatinfo);
                }
                if (actTagName == "standardNettype") {
                    this.idOfStandardNettype = ((Element)actTag).getAttribute("ref");
                }
                if (actTagName == "standardApplication") {
                    this.idOfStandardApplication = ((Element)actTag).getAttribute("ref");
                }
                if (actTagName == "standardFormat") {
                    this.idOfStandardIOFormat = ((Element)actTag).getAttribute("ref");
                }
            }
        } catch (Throwable t) {
            D.d("Error in ToolSpecification:");
            t.printStackTrace();
        }
    }

/**
 * Insert the method's description here.
 * Creation date: (22.09.00 19:26:49)
 * (not implemented)
 */
    public final void save() {
        D.d("ToolSpecification: Save not implemented yet (TODO...)");
        // ########## Ich glaub, da fehlt noch was...
    }

/**
 * Insert the method's description here.
 * Creation date: (22.09.00 19:26:49)
 */
    public final void save(URL theUrl) {
        this.theURL = theURL;
        save();
    }

/**
 * Insert the method's description here.
 * Creation date: (22.09.00 19:26:49)
 */
    public final void load(URL theUrl) {
        this.theURL = theURL;
        load();
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:17:50)
       * @param newApptypeId2apptypeinfo java.util.Hashtable
       */

    public void setApptypes(java.util.Hashtable newApptypeId2apptypeinfo) {
        apptypeId2apptypeinfo = newApptypeId2apptypeinfo;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:17:26)
       * @param newIdOfStandardApplication java.lang.String
       */

    public void setIdOfStandardApplication(java.lang.String newIdOfStandardApplication) {
        idOfStandardApplication = newIdOfStandardApplication;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:17:26)
       * @param newIdOfStandardIOFormat java.lang.String
       */

    public void setIdOfStandardIOFormat(java.lang.String newIdOfStandardIOFormat) {
        idOfStandardIOFormat = newIdOfStandardIOFormat;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:17:26)
       * @param newIdOfStandardNettype java.lang.String
       */

    public void setIdOfStandardNettype(java.lang.String newIdOfStandardNettype) {
        idOfStandardNettype = newIdOfStandardNettype;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:17:50)
       * @param newIoFormatId2ioformatinfo java.util.Hashtable
       */

    public void setIoFormats(java.util.Hashtable newIoFormatId2ioformatinfo) {
        ioFormatId2ioformatinfo = newIoFormatId2ioformatinfo;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:17:50)
       * @param newNettypeId2nettypeURL java.util.Hashtable
       */

    public void setNettypes(java.util.Hashtable newNettypeId2nettypeURL) {
        nettypeId2nettypeURL = newNettypeId2nettypeURL;
    }
}
