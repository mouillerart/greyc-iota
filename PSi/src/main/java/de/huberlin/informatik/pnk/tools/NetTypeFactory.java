package de.huberlin.informatik.pnk.tools;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;

import de.huberlin.informatik.pnk.appControl.base.D;
/**
 * The NetTypeFactory can create a dynamic net type, that is only specified
 * by certain parameters, such as "BlackToken, MultiSet". The meaning of
 * each parameter is declared in an extern XML configuration file. The URL
 * of this file must be passed when calling the constructor.
 */
import de.huberlin.informatik.pnk.tools.base.MetaNetType;

public class NetTypeFactory extends MetaNetType implements ActionListener {

    /**
     * Keeps the URL of the configuration file.
     */
    private URL configFile;
    /**
     * The DOM of the configuration file
     */
    private Document doc;
    /**
     * This dialog is used, when getSpecification is called without any net type
     * parameters.
     */
    private JDialog dialog;
    /**
     * The dialog used in getSpecification stores its result in this variable.
     */
    private Hashtable specification;
    /**
     * The Vector contains all RadioButtonGroups of the dialog. This makes it
     * easier to evaluate the user's choice.
     */
    private Vector buttonGroups;

    /**
     * Returns a hashtable, that represents a specification,
     * defined by the passed argument vector.
     */
    public Hashtable getSpecification(Vector parameters) {
        Hashtable specification = new Hashtable();
        this.parameters = parameters;

        if (parameters == null || parameters.size() == 0 || doc == null) {
            return specification;
        }
        // get dimension node list
        NodeList dimNodeList = doc.getElementsByTagName("dimension");
        for (int i = 0; i < dimNodeList.getLength(); i++) {
            //get dimension node
            org.w3c.dom.Node dimNode = dimNodeList.item(i);
            // the count attribute of a dimension node specifies the position
            // of the connected argument
            int dimCount = Integer.parseInt(((Element) dimNode).getAttribute("count"));
            if (dimCount <= parameters.size()) {
                // get the passed argument for the current dimension
                String currentArgument = (String) parameters.get(dimCount - 1);
                // get a list of all possible parameters for this dimension
                NodeList paramList = ((Element) dimNode).getElementsByTagName("param");
                int j = 0;
                String paramName = "";
                org.w3c.dom.Node paramNode = null;
                // Find the parameter definition for the current argument.
                while (j < paramList.getLength() && !currentArgument.equals(paramName)) {
                    paramNode = paramList.item(j++);
                    paramName = ((Element) paramNode).getAttribute("name");
                }
                if (currentArgument.equals(paramName)) {
                    // If a parameter definition was found, add it to the specification.
                    addSpecification(paramNode, specification);
                } else {
                    System.err.println("No definition for '" + currentArgument + "' found!");
                }
            }
        }
        return specification;
    }

    /**
    Implementation of the ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("OK")) {
            ok();
        }
        if (cmd.equals("Cancel")) {
            cancel();
        }
    }

    /*
    Parse the specNode and add the resulting specification to the
    hastable passed.
     */
    private void addSpecification(org.w3c.dom.Node specNode, Hashtable specification) {
        /*get extendable node list*/
        NodeList extendableList = ((Element) specNode).getElementsByTagName("extendable");
        for (int j = 0; j < extendableList.getLength(); j++) {
            /*get extendable node*/
            org.w3c.dom.Node actExtendableTag = extendableList.item(j);
            String extendableClassName = ((Element) actExtendableTag).getAttribute("class");
            Hashtable extensionName2ExtensionClassName = (Hashtable) specification.get(extendableClassName);
            /*create a new extension hashtable for this extendable if it does not exist*/
            if (extensionName2ExtensionClassName == null) {
                extensionName2ExtensionClassName = new Hashtable();
            }
            /*get extension node list*/
            NodeList extensionList = ((Element) actExtendableTag).getElementsByTagName("extension");
            for (int k = 0; k < extensionList.getLength(); k++) {
                /*get extension node*/
                org.w3c.dom.Node actExtensionTag = extensionList.item(k);
                /*add extension to the extension hashtable*/
                extensionName2ExtensionClassName.put(((Element) actExtensionTag).getAttribute("name"),
                        ((Element) actExtensionTag).getAttribute("class"));
            }
            /*add extension hashtable to the extendable hashtable*/
            specification.put(extendableClassName, extensionName2ExtensionClassName);
        }
    }

    /*
    called if ok button was pressed
     */
    private void ok() {
        Vector args = new Vector();
        /*evaluate the user choice building an argument vector*/
        for (int i = 0; i < buttonGroups.size(); i++) {
            /*get ButtonGroup for dimension i*/
            ButtonGroup bg = (ButtonGroup) buttonGroups.get(i);
            /*get selected button*/
            ButtonModel button = bg.getSelection();
            /*add parameter(=ActionCommand) to the vector*/
            if (button != null) {
                args.add(button.getActionCommand());
            }
        }
        /*get the corresponding specification for the chosen parameters*/
        specification = getSpecification(args);
        /*dispose the dialog; now the method getSpecification() continues*/
        dialog.dispose();
    }

    /*
    called if cancel button was pressed
     */
    private void cancel() {
        /*dispose the dialog; now the method getSpecification() continues*/
        specification = null;
        dialog.dispose();
    }

    /*
    Gives a vector representation of the available arguments for every
    dimension. Therefore the vector contains a vectors of the following
    format: [dimension name, available parameter, ..., default parameter]
     */
    private Vector getDialogItems() {
        Vector dimensions = new Vector();
        if (doc == null) {
            return dimensions;
        }
        /*get dimension node list*/
        NodeList dimNodeList = doc.getElementsByTagName("dimension");
        for (int i = 0; i < dimNodeList.getLength(); i++) {
            Vector dimItemList = new Vector();
            /*get dimension node*/
            org.w3c.dom.Node dimNode = dimNodeList.item(i);
            /*the dimension name is the first comonent of the dimension vector*/
            dimItemList.add(((Element) dimNode).getAttribute("name"));
            int dimCount = Integer.parseInt(((Element) dimNode).getAttribute("count"));
            /*get parameter node list*/
            NodeList paramList = ((Element) dimNode).getElementsByTagName("param");
            for (int j = 0; j < paramList.getLength(); j++) {
                /*get parameter node*/
                org.w3c.dom.Node paramNode = paramList.item(j);
                /*add parameter name to the vector*/
                dimItemList.add(((Element) paramNode).getAttribute("name"));
            }
            /*get default node "list" (should contain only one element)*/
            NodeList defaultList = ((Element) dimNode).getElementsByTagName("default");
            if (defaultList.getLength() > 0) {
                /*get default node*/
                org.w3c.dom.Node defaultNode = defaultList.item(0);
                /*add default parameter at the end of the vector*/
                dimItemList.add(((Element) defaultNode).getAttribute("name"));
            } else {
                /*default="" if not defined*/
                dimItemList.add("");
            }
            if (dimCount > dimensions.size()) {
                dimensions.setSize(dimCount);
            }
            /*add the vector for the current dimension at the correct position*/
            dimensions.setElementAt(dimItemList, dimCount - 1);
        }
        return dimensions;
    }

    /*
     * Returns the maximum number of arguments available for
     * a dimension. Expects a vector representation of the
     * format, that is returned by getDialogItems().
     */
    private int getMaxItems(Vector dimVector) {
        /*at least 2 items (dimension name+default => 0 parameters)*/
        int max = 2;
        /*for every dimension*/
        for (int i = 0; i < dimVector.size(); i++) {
            /*get itemVector*/
            Vector itemVector = (Vector) dimVector.get(i);
            /*if it has more elements max=size of this itemVector*/
            if (itemVector != null && itemVector.size() > max) {
                max = itemVector.size();
            }
        }
        /*only the number of available parameters are interesting*/
        return max - 2;
    }
    /**
     * The Vector contains all RadioButtonGroups of the dialog. This makes it
     * easier to evaluate the user's choice.
     */
    private Vector parameters;

    public NetTypeFactory(Hashtable parameters) {
        super(parameters);
        try {
            this.configFile = new URL((String) parameters.get("configFile"));
        } catch (MalformedURLException e) {
            System.out.println("MalformedURLException: " + e.toString());
            return;
        }

        /*create a DocumentBuilderFactory and configure it*/
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        /* create a DocumentBuilder that satisfies the constraints
        specified by the DocumentBuilderFactory*/
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            D.err(pce.toString());
            doc = null;
            return;
        }

        /*parse the configuration file*/
        try {
            doc = db.parse(configFile.toString());
        } catch (SAXException se) {
            D.err(se.getMessage());
            doc = null;
            return;
        } catch (IOException ioe) {
            D.err(ioe.toString());
            doc = null;
            return;
        }
    }

    public Vector getParameters() {
        return parameters;
    }

    /**
    This method evokes a dialog to request the net type parameters.
    The resulting specification is returned as Hashtable.
    The parent parameter is the parent frame of the dialog.
     */
    public Hashtable getSpecification() {
        // get vector representation of available parameters
        Vector dimVector = getDialogItems();
        // get max parameters available for a dimension
        int maxItems = getMaxItems(dimVector);
        specification = new Hashtable();
        buttonGroups = new Vector();

        // create a new dialog
        dialog = new JDialog(new JFrame(), "NetTypeFactory", true);
        // dialog = new JDialog((Frame)null, "NetTypeFactory", true);
        dialog.setSize(400, 250);
        // create TabbedPane; every dimension gets an own tab
        JTabbedPane dimensions = new JTabbedPane();
        dimensions.setBorder(new TitledBorder("Select one item for each dimension"));
        dialog.getContentPane().add(dimensions);

        // For every dimension create a panel with a RadioButton for
        // every parameter
        for (int i = 0; i < dimVector.size(); i++) {
            // the vector contains all possible parameters for this dimension
            Vector itemVector = (Vector) dimVector.get(i);
            if (itemVector != null) {
                String dimName = (String) itemVector.get(0);
                // create a new panel for this dimension and add it to the
                // TabbedPane
                JPanel dimPanel = new JPanel(new GridLayout(maxItems, 1));
                dimPanel.setBorder(new TitledBorder(dimName));
                dimensions.add(dimName, dimPanel);
                // one ButtonGroup for every dimension
                ButtonGroup bg = new ButtonGroup();
                // add a button for every parameter
                for (int j = 1; j < itemVector.size() - 1; j++) {
                    String choiceText = (String) itemVector.get(j);
                    JRadioButton choice = new JRadioButton(choiceText);
                    choice.setActionCommand(choiceText);
                    // default parameter of this dimension is selected
                    if (choiceText.equals((String) itemVector.get(itemVector.size() - 1))) {
                        choice.setSelected(true);
                    }
                    dimPanel.add(choice);
                    bg.add(choice);
                }
                // add ButtonGroup to the global ButtonGroupVector for later
                // choice evaluation
                buttonGroups.add(bg);
            }
        }

        // add an ok and a cancel button and show the dialog
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        buttons.add(okButton);
        JButton cancelButton = new JButton("Cancel");
        buttons.add(cancelButton);
        cancelButton.addActionListener(this);
        dialog.getContentPane().add("South", buttons);
        // The dialog is modal, that means the program will wait after
        // the call of the show method, until the dialog is disposed.
        dialog.show();

        // After pressing the ok button, the specification hashtable
        // will be created and is now available.
        return specification;
    }
}
