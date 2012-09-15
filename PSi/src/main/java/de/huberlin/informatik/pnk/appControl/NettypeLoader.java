package de.huberlin.informatik.pnk.appControl;

/*
   Petri Net Kernel,
   Copyright 1996-2001 Petri Net Kernel Team,

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: NettypeLoader.java,v $
   Revision 1.1  2001/10/11 16:36:29  oschmann
   Neue Release

   Revision 1.9  2001/06/04 15:40:24  efischer
 *** empty log message ***

   Revision 1.8  2001/05/11 17:21:03  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.7  2001/02/27 21:28:44  oschmann
   Version mit viel neu Effekt: Menu bleibt sichtbar, Apps sind
   nummeriert, und es gibt Radiobuttons;-)

   Revision 1.6  2001/02/13 08:57:50  hohberg
   Class names beginning with '.' are expanded by de.huberlin.informatik.pnk

 */

import java.io.*;
import java.util.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.*;
import org.xml.sax.*;

import java.net.*;

import de.huberlin.informatik.pnk.appControl.base.*;

public class NettypeLoader extends Object {
    // ##### Verwaltet Nettype...

    final String STD_PATH = "de.huberlin.informatik.pnk";

    private URL url = null;
    private String name = "";

    /**
     * returns name of this net type
     */
    public String getNettypeName() {
        return this.name;
    }

    public boolean isReady() {
        return isReady;
    }

    private String classname = null;        private boolean isParametric = false;   private boolean isReady = false;        private Hashtable parameters;   private Hashtable specificationTable;       /**
                                                                                                                                                                                                         * Constructs a new net type object, specified by the URL of its net type specification.
                                                                                                                                                                                                         */
    NettypeLoader(URL url) {
        this.url = url;
        load();
    }    /**
          * Insert the method's description here.
          * Creation date: (19.9.2001 19:08:02)
          * @return java.lang.String
          */

    public java.lang.String getClassname() {
        return classname;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 20:18:15)
       * @return java.lang.String
       */

    public java.lang.String getName() {
        return name;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:53:04)
       * @return java.util.Hashtable
       */

    public java.util.Hashtable getParameters() {
        return parameters;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:54:51)
       * @return java.util.Hashtable
       */

    public java.util.Hashtable getSpecificationTable() {
        return specificationTable;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:08:02)
       * @return boolean
       */

    public boolean isIsParametric() {
        return isParametric;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:08:02)
       * @return boolean
       */

    public boolean isParametric() {
        return isParametric;
    }   /**
         * get a new net object (Graph) of this net type
         */

    public void load() {
        // Step 1: create a DocumentBuilderFactory and configure it
        DocumentBuilderFactory dbf =
            DocumentBuilderFactory.newInstance();

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
            return;
        }

        // Step 3: parse the input file
        Document nettypeSpecificationDoc = null;
//		NettypeSpecificationDoc = XmlDocument.createXmlDocument (url.toString());
        try {
            nettypeSpecificationDoc = db.parse(url.toString());
        } catch (SAXException se) {
            System.err.println(se.getMessage());
            return;
        } catch (IOException ioe) {
            System.err.println(ioe);
            return;
        }

        Element ntSpecRoot = nettypeSpecificationDoc.getDocumentElement();
        this.name = ntSpecRoot.getAttribute("name");
        //D.d("Netztyp name: " + this.name, 3);
        NodeList extendableList = ntSpecRoot.getElementsByTagName("executable");
        if (extendableList.getLength() > 0) {
            if (extendableList.getLength() > 1) {
                D.d("Warning: More than one executable defined. Using first.");
            }
            org.w3c.dom.Node actExtendableTag = extendableList.item(0);

            classname = ((Element)actExtendableTag).getAttribute("class");

            if (classname.charAt(0) == '.')
                classname = STD_PATH + classname;

            parameters = new Hashtable();

            NodeList extensionList = ((Element)actExtendableTag).getElementsByTagName("param");
            for (int k = 0; k < extensionList.getLength(); k++) {
                org.w3c.dom.Node actExtensionTag = extensionList.item(k);
                String value = ((Element)actExtensionTag).getAttribute("value");
                parameters.put(((Element)actExtensionTag).getAttribute("name"), value);
            }

            isParametric = true;
        }

        extendableList = ntSpecRoot.getElementsByTagName("extendable");
        if ((extendableList.getLength() > 0) && isParametric) {
            D.d("NetLoader: Extendables in Parametric Nettype will be ignored.");
        } else {
            specificationTable = new Hashtable();
            for (int j = 0; j < extendableList.getLength(); j++) {
                org.w3c.dom.Node actExtendableTag = extendableList.item(j);
                String extendableClassName = ((Element)actExtendableTag).getAttribute("class");
                if (extendableClassName.charAt(0) == '.')
                    extendableClassName = STD_PATH + extendableClassName;

                Hashtable extensionName2ExtensionClassName = new Hashtable();
                NodeList extensionList = ((Element)actExtendableTag).getElementsByTagName("extension");
                for (int k = 0; k < extensionList.getLength(); k++) {
                    org.w3c.dom.Node actExtensionTag = extensionList.item(k);
                    String extensionClassName = ((Element)actExtensionTag).getAttribute("class");
                    if (extensionClassName.charAt(0) == '.')
                        extensionClassName = STD_PATH + extensionClassName;
                    extensionName2ExtensionClassName.put(((Element)actExtensionTag).getAttribute("name"),
                                                         extensionClassName);
                }
                specificationTable.put(extendableClassName, extensionName2ExtensionClassName);
            }
        }
        //D.d("NetLoader: Name: " + this.name);
        isReady = true;
    }    /**
          * Insert the method's description here.
          * Creation date: (19.9.2001 19:08:02)
          * @param newIsParametric boolean
          */

    public void setIsParametric(boolean newIsParametric) {
        isParametric = newIsParametric;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:08:02)
       * @param newMainclass java.lang.String
       */

    public void setMainclass(java.lang.String newClassname) {
        classname = newClassname;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 20:18:15)
       * @param newName java.lang.String
       */

    public void setName(java.lang.String newName) {
        name = newName;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:53:04)
       * @param newParameters java.util.Hashtable
       */

    public void setParameters(java.util.Hashtable newParameters) {
        parameters = newParameters;
    } /**
       * Insert the method's description here.
       * Creation date: (19.9.2001 19:54:51)
       * @param newSpecificationTable java.util.Hashtable
       */

    public void setSpecificationTable(java.util.Hashtable newSpecificationTable) {
        specificationTable = newSpecificationTable;
    }
}