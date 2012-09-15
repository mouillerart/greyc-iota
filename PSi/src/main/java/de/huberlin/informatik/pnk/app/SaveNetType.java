package de.huberlin.informatik.pnk.app;

import de.huberlin.informatik.pnk.app.base.*;
import de.huberlin.informatik.pnk.appControl.*;
import de.huberlin.informatik.pnk.appControl.base.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import javax.swing.*;
import javax.xml.parsers.*;
import org.apache.crimson.tree.XmlDocument;
import org.w3c.dom.*;

/**
        The application SaveNetType saves the net type specification of the active
        net into an XML file. This is very useful, especially for adapting dynamic
        generated net type specifications on your personal PNK struture.
        Therefor the application menu contains a Save and a SaveAs entry.
 */

public class SaveNetType extends MetaApplication {
    public static String staticAppName = "Save net type";

    /**
          Constructor only calls the parent constructor.
     */
    public SaveNetType(ApplicationControl ac) {
        super(ac);
    }

    /**
          A special menu is used for this application. It contains a Save
          and an SaveAs menu entry.
     */
    public JMenu[] getMenus() {
        Object[] e1 = {ApplicationControlMenu.MENU_ENTRY, ApplicationControlMenu.MENU_ACTIVE, "Save", this, "saveNT"};
        Object[] e2 = {ApplicationControlMenu.MENU_ENTRY, ApplicationControlMenu.MENU_ACTIVE, "SaveAs...", this, "saveNTas"};
        Object[] m1 = {"SaveNetType", ApplicationControlMenu.MENU_ACTIVE, e1, e2};
        return this.applicationControl.setMenu(this, new Object[] {m1});
    }

    /**
          This function is called when the menu entry SaveAs in the
          application menu is chosen. It invokes a dialog box. So
          the user can choose the file to save the net type.
     */
    public void saveNTas() {
        /*the URL of the chosen file*/
        URL chosenURL = null;
        /*the file filter allowes only '.xml'-files*/
        javax.swing.filechooser.FileFilter XMLFileFilter = new PnkFileFilter("net type description", "xml");
        /*a JFileChooser dialog with "netTypeSpecifications" as default directory*/
        JFileChooser fc = new JFileChooser("netTypeSpecifications");
        /*given answer to the overwrite question (if chosen file exists)*/
        int overwriteFile = 0;

        fc.addChoosableFileFilter(XMLFileFilter); //use the file filter with the dialog
        fc.setFileFilter(XMLFileFilter);
        fc.removeChoosableFileFilter(fc.getAcceptAllFileFilter());
        fc.setDialogTitle("Save net type");
        /*default file name is "netType.xml" where netType is the name of the net type to save*/
        fc.setSelectedFile(new File(applicationControl.getNetType(net) + ".xml"));

        /*repeat while the answer to the overwrite question is "no"*/
        do {
            /*show the saveAs dialog*/
            int buttonPressed = fc.showSaveDialog(null);
            if (buttonPressed == JFileChooser.APPROVE_OPTION) { // if ok button pressed
                try {
                    chosenURL = fc.getSelectedFile().toURL(); // get chosen URL
                    if (chosenURL != null) {
                        if (!chosenURL.toString().endsWith(".xml")) // guarantee xml-extension
                            chosenURL = new URL(chosenURL.toString() + ".xml");
                        /*can the file be written? (either it does not exist or it exists and can be overwritte)*/
                        overwriteFile = canWrite(chosenURL);
                        if (overwriteFile == JOptionPane.YES_OPTION) { // if yes
                            writeNetTypeXML(chosenURL); // write the net type into chosenURL
                            System.out.println("Net type was written into " + chosenURL.toString() + " !");
                        }
                    }
                } catch (MalformedURLException e) {
                    System.err.println(e);
                }
            } else { // if ok button not pressed (cancel)
                /*set overwriteFile to CANCEL to leave the loop*/
                overwriteFile = JOptionPane.CANCEL_OPTION;
            }
        } while (overwriteFile == JOptionPane.NO_OPTION);
    }

    /**
          This function is called when the menu entry Save in the
          application menu is chosen. It saves the net type of the
          active net under "netType.xml" where netType is the name
          of the net type.
     */
    public void saveNT() {
        try {
            /*default URL is "file:netTypeSpecifications/netType.xml" where netType is the name of the net type to save*/
            URL stdURL = new URL("file:netTypeSpecifications/" + applicationControl.getNetType(net) + ".xml");
            switch (canWrite(stdURL)) { // can file be written into this URL
            case JOptionPane.YES_OPTION: // if yes
                writeNetTypeXML(stdURL); // write the net type into stdURL
                System.out.println("Net type was written into " + stdURL.toString() + " !");
                break;
            case JOptionPane.NO_OPTION: // if no
                saveNTas(); // call saveNTas() to choose another file
                break;
            } /*else (if overwrite question was canceled) leave*/
        } catch (MalformedURLException e) {
            System.err.println(e);
        }
    }

    /*Returns, if the file can be written. That means, that the given URL is
       valid (not null) and the file does not exist or it exists and can be
       overwritten (-> user dialog). The method returns the JOptionPane dialog
       constants for Yes/No/Cancel.*/
    private int canWrite(URL url) {
        try {
            if (url == null) // if URL is null
                return JOptionPane.NO_OPTION;  // the file can not be written
            /*check if the file already exists (IOException will be thrown else)*/
            url.getContent();
            /*if file exists return the answer given to the dialog box*/
            return JOptionPane.showConfirmDialog(null,
                                                 "The File " + url.toString() + " does already exist! Overwrite?",
                                                 "Overwrite existing file",
                                                 JOptionPane.YES_NO_CANCEL_OPTION,
                                                 JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            /*if file not exists return YES (file can be written)*/
            return JOptionPane.YES_OPTION;
        }
    }

    /*Write the net type into the file specified by NetTypeURL.*/
    private void writeNetTypeXML(URL NetTypeURL) {
        /*get a DocumentBuilderFactory*/
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        /*get a new DocumentBuilder*/
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
            System.exit(1);
        }

        /*and create a new plain xml document*/
        Document doc = db.newDocument();
        org.w3c.dom.Node NetTypeSpecNode, ExtendableNode, ExtensionNode;
        Hashtable ExtensionHash, SpecTab;
        Enumeration ExtensionList, ExtendableList;

        /*Now we have to built a DOM tree representation for our net type
           xml document. At first we create the root elemnt "netTypeSpecification"*/
        NetTypeSpecNode = doc.appendChild(doc.createElement("netTypeSpecification"));
        /*the attribute name gets the net type name*/
        ((Element)NetTypeSpecNode).setAttribute("name", applicationControl.getNetType(net));
        /*Now we've to write the specification table. So get it from the net*/
        SpecTab = net.getSpecification().getSpecTab().getSpecificationTable();
        /*the SpecTab contains a list of extendables*/
        for (ExtendableList = SpecTab.keys(); ExtendableList.hasMoreElements(); ) {
            /*actual extendable name (=corresponding class)*/
            String ExtendableName = (String)ExtendableList.nextElement();
            /*create an extendable node in our DOM tree with a class attribute*/
            ExtendableNode = NetTypeSpecNode.appendChild(doc.createElement("extendable"));
            ((Element)ExtendableNode).setAttribute("class", ExtendableName);
            /*for every extendable there is an extension list in the SpecTab*/
            ExtensionHash = (Hashtable)SpecTab.get(ExtendableName); // get it
            for (ExtensionList = ExtensionHash.keys(); ExtensionList.hasMoreElements(); ) {
                /*actual extension name*/
                String actExtensionName = (String)ExtensionList.nextElement();
                if (!actExtensionName.equals("name")) { // name extension is default extension
                    /*create an extension node in our DOM tree with a name- and a class attribute*/
                    ExtensionNode = ExtendableNode.appendChild(doc.createElement("extension"));
                    ((Element)ExtensionNode).setAttribute("name", actExtensionName);
                    ((Element)ExtensionNode).setAttribute("class", (String)ExtensionHash.get(actExtensionName));
                }
            }
        }
        /*Write the DOM tree. At first cast the document into
           an XmlDocument (a class of the crimson parser)*/
        XmlDocument xmlDoc = (XmlDocument)doc;
        try {
            /*Now write the document into the file using UTF-8 encoding*/
            xmlDoc.write(new FileWriter(NetTypeURL.getFile()), "UTF-8");
        } catch (IOException e) {
            System.err.println("  " + e.getMessage());
        }
    }
}
