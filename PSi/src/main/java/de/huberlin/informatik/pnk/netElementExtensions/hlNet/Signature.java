package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

import java.io.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/*
   Petri Net Kernel,
   Copyright 1996-2000 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: Signature.java,v $
   Revision 1.13  2001/10/11 16:59:10  oschmann
   Neue Release

   Revision 1.11  2001/06/17 16:16:56  efischer
 *** empty log message ***

   Revision 1.10  2001/06/13 10:48:39  gruenewa
 *** empty log message ***

   Revision 1.9  2001/06/12 07:04:02  oschmann
   Neueste Variante...

   Revision 1.8  2001/06/04 15:46:52  efischer
 *** empty log message ***

   Revision 1.7  2001/05/11 17:23:15  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.6  2001/04/11 09:59:26  efischer
 *** empty log message ***

   Revision 1.5  2001/03/29 16:58:22  efischer
 *** empty log message ***

   Revision 1.4  2001/03/29 13:28:42  hohberg
   Implementations of constants in inscription expression

   Revision 1.3  2001/03/28 08:00:12  hohberg
   Implementation of Inscription variables of subrange type

   Revision 1.2  2001/02/27 13:34:30  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:09:59  hohberg
   New package structure

   Revision 1.2  2001/02/02 08:11:52  hohberg
   New representation of token type (Hohberg)

   Revision 1.1  2001/01/30 14:32:26  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)


 */

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Defines variables, functions and constants to construct
 * inscription expressions. <br>
 * Translates for one transition
 * the inscription expression of incomming and outgoing edges. <br>
 * The same variable name on edges identifies
 * the same variable.<br>
 * The place of arc must be assigned to the global variable place.
 * This place is a parameter of inscription variables
 * and inscription functions.
 */
public class Signature extends Extension {
    // variable id in net and corresponding variable for simulation
    public Object[][] varField;
    public Object[][] fktField; // short and complete function names
    public String[][] typeField; // short and complete token type names

    protected Transition transition;
    private Place place;
    private final String SIGNTABLE = "file:netTypeSpecifications/signatureTable.xml";

    /**
     *  Constructor specifying the extendable. <br>
     */
    public Signature(Extendable extendable) {
        super(extendable);
        if (!(extendable instanceof Net))
            throw (new NetSpecificationException("Signature must be an extension of Net"));
    }

    /**
     *  Constructor specifying the extendable and the name
     * of a token class. <br>
     */
    public Signature(Extendable extendable, String signature) {
        super(extendable, signature);
    }

    private void appendStateNode(Document doc, org.w3c.dom.Node node) {
        int i;
        for (i = 0; i < varField.length; i++) {
            org.w3c.dom.Node varNode = node.appendChild(doc.createElement("variable"));
            ((Element)varNode).setAttribute("name", (String)varField[i][0]);
        }
        for (i = 0; i < fktField.length; i++) {
            org.w3c.dom.Node fktNode = node.appendChild(doc.createElement("function"));
            ((Element)fktNode).setAttribute("name", (String)fktField[i][0]);
            ((Element)fktNode).setAttribute("class", (String)fktField[i][1]);
            ((Element)fktNode).setAttribute("signature", (String)fktField[i][2]);
        }
        for (i = 0; i < typeField.length; i++) {
            org.w3c.dom.Node typeNode = node.appendChild(doc.createElement("tokenType"));
            ((Element)typeNode).setAttribute("name", (String)typeField[i][0]);
            ((Element)typeNode).setAttribute("class", (String)typeField[i][1]);
        }
    }

//////////////////////////////////////////////////
// Implementation of abstract class Extension  //
/**
 * Gives the extern representation of default state: "String". <br>
 */
    public String defaultToString() {
        return "";
    } // public String defaultToString( )

    /**
     * Identifies the name of the variable
     */
    public Object defineVariable(String str) {
        Class[] paramTypes = {};
        Object[] params = {};  // no parameters
        InscriptionVariable var;
        int length = varField.length; // number of variables
        //int varParameters = varField[0].length;
        if (place == null) System.out.println("Place == null");
        for (int i = 0; i < length; i++) {
            if (str.equals(varField[i][0])) { // index 0: name of variables
                var = (InscriptionVariable)varField[i][1]; // index 1: variable object if defined
                if (var != null) return var;  // defined!
                // else first use of variable
                if (varField[i].length == 2) { // variable gets its values from place
                    var = new InscriptionVariable(place);
                    varField[i][1] = var;
                    return var;
                }
                // subrange variable
                else if (varField[i].length == 4) {
                    // variable gets its values from subrange and place
                    var = new InscriptionVariable(this, i, place);
                    varField[i][1] = var;
                    return var;
                } else {
                    throw(new KernelUseException
                              ("Error in Signature: Not possible to create variable: " + str));
                }
            } // name identified
        }
        throw(new ExtensionValueException("Signature: Undefined variable: " + str, "inscription", null));
    }

    private Object[][] dim2VectorToArray(Vector v) {
        Object[][] retArr;

        retArr = new Object[v.size()][];
        for (int i = 0; i < v.size(); i++) {
            retArr[i] = ((Vector)(v.elementAt(i))).toArray();
        }
        return retArr;
    }

    public Object getFunction(String str, StringBuffer fctIndex) {
        Method m;
        int length = fktField.length;
        for (int i = 0; i < length; i++) {
            if (((String)fktField[i][0]).equals(str)) {
                if (fctIndex != null) fctIndex.append(i);  // to return the index of function
                if (fktField[i][3] == null) {
                    // construct new InscriptionFunction
                    String fktClassName = (String)fktField[i][1];
                    if (fktClassName == null) { // error in signature
                        System.out.println("Error in signature: Undefined class for: " + str);
                        throw(new NetSpecificationException("Error in signature: Undefined class for: " + str));
                    }
                    String functionDef = (String)fktField[i][2];
                    if (functionDef == null) { // error in inscription
                        System.out.println("Error in signature: Undefined function of: " + str);
                        throw(new NetSpecificationException("Error in signature: Undefined function of: " + str));
                    }
                    //System.out.println("getFunction: "+fktClassName+ " - "+functionDef);
                    MethodDef method = new MethodDef(fktClassName, functionDef);
                    fktField[i][3] = method.generateMethod();
                } // == null
                  // method defined, generate MethodCall object
                return new MethodCall((Method)fktField[i][3], str);
            } // function name defined
        }  // for
        throw(new ExtensionValueException("Inscription function: Undefined function name: " + str, "inscription", null));
    }

    /**
     * Returns parameter type of the parIndex parameter of fct. <br>
     * Requires: StringBuffer  fctIndex contains an index of a function in fktField and
     * parIndex >= 0
     */
    public String getParameterType(StringBuffer fctIndex, int parIndex) {
        int i = Integer.parseInt(fctIndex.toString());
        System.out.println("fct on index " + i);
        String function = (String)fktField[i][2];
        System.out.println("fct def " + function);
        int indexE = function.indexOf('(');
        int indexB = 0;
        // parIndex >= 0!
        for (int j = 0; j <= parIndex; j++) { // next begin
            indexB = indexE + 1;
            indexE = function.indexOf(',', indexB);
        }
        if (indexE == -1) {
            indexE = function.indexOf(')', indexB);
        }
        return function.substring(indexB, indexE);
    }

    public String getSubrangeOfVariable(int i) {
        return (String)varField[i][2];
    }

    public String getTypeOfVariable(int i) {
        return (String)varField[i][3];
    }

    /**
     * Identifies the name of the variable
     */
    public Object getVariable(String str) {
        Object[] params = {place};
        InscriptionVariable var;
        int length = varField.length;
        if (place == null) System.out.println("Place == null");
        for (int i = 0; i < length; i++) {
            if (str.equals(varField[i][0])) {
                var = (InscriptionVariable)varField[i][1];
                if (var != null) return var;
                if (varField[i].length == 4) { // variable gets its values from subrange
                    var = new InscriptionVariable(this, i, null);
                    varField[i][1] = var;
                    return var;
                }
                // else error: variable not defined
                throw(new ExtensionValueException
                          ("Signature: Variable " + str + " is has no value", "inscription", null));
            }
        }
        throw(new ExtensionValueException("Signature: Undefined variable: " + str, "inscription", null));
    }

    protected boolean isValid() {
        return true;
    }

    protected boolean isValid(Extendable extendable) {
        return true;
    }

    protected boolean isValid(String state) {
        return true;
    }

    /**
     * load signature extension node
     */
    public void load(org.w3c.dom.Node theNode) {
        org.w3c.dom.Node valueChild = ((Element)theNode).getElementsByTagName("value").item(0);
        if (valueChild != null) {
            String childListString = "";
            org.w3c.dom.NodeList childList = valueChild.getChildNodes();
            for (int i = 0; i < childList.getLength(); i++) {
                childListString += childList.item(i).toString();
            }
            valueOf(childListString);
        }
        org.w3c.dom.Node graphicsChild = ((Element)theNode).getElementsByTagName("graphics").item(0);
        if (graphicsChild != null) {
            setDynamicExtension("graphics", "graphics", "graphics", graphicsChild);
        }
    }

    /**
          Load the signature table specified by externState
     */
    protected void localParse() {
        String sigString = "<signature>" + toString() + "</signature>";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document doc = null;
        NodeList theTagList;
        org.w3c.dom.Node actTag;
        Element root;
        Vector varTable, fctTable, tokenTypeTable;

        dbf.setValidating(false);
        dbf.setIgnoringComments(true);
        dbf.setIgnoringElementContentWhitespace(true);

        try {
            db = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException pce) {
            System.err.println(pce);
            System.exit(1);
        }

        try {
            doc = db.parse(new org.xml.sax.InputSource(new StringReader(sigString)));
        } catch (java.io.IOException e) {
            System.err.println(e);
            System.exit(1);
        } catch (org.xml.sax.SAXException e) {
            System.err.println(e);
            System.exit(1);
        }

        root = doc.getDocumentElement();
        theTagList = root.getChildNodes();
        varTable = new Vector(5, 5);
        fctTable = new Vector(5, 5);
        tokenTypeTable = new Vector(5, 5);

        for (int i = 0; i < theTagList.getLength(); i++) {
            actTag = theTagList.item(i);
            String actTagName = actTag.getNodeName();
            if (actTagName.equals("variable")) {
                Vector varDef = new Vector(2);
                varDef.add(((Element)actTag).getAttribute("name"));
                varDef.add(null);
                varTable.add(varDef);
            }
            if (actTagName.equals("function")) {
                Vector fctDef = new Vector(4);
                fctDef.add(((Element)actTag).getAttribute("name"));
                fctDef.add(((Element)actTag).getAttribute("class"));
                fctDef.add(((Element)actTag).getAttribute("signature"));
                fctDef.add(null);
                fctTable.add(fctDef);
            }
            if (actTagName.equals("tokenType")) {
                Vector tokenTypeDef = new Vector(2);
                tokenTypeDef.add(((Element)actTag).getAttribute("name"));
                tokenTypeDef.add(((Element)actTag).getAttribute("class"));
                tokenTypeTable.add(tokenTypeDef);
            }
        }
        if (varField == null || varField.length == 0)
            varField = dim2VectorToArray(varTable);
        if (fktField == null || fktField.length == 0)
            fktField = dim2VectorToArray(fctTable);
        if (typeField == null || typeField.length == 0) {
            Object[][] tempTypeField = dim2VectorToArray(tokenTypeTable);
            typeField = new String[tempTypeField.length][];
            for (int i = 0; i < tempTypeField.length; i++) {
                typeField[i] = new String[tempTypeField[i].length];
                for (int j = 0; j < tempTypeField[i].length; j++) {
                    typeField[i][j] = (String)tempTypeField[i][j];
                }
            }
        }
    }

    /**
     * External representation is not defined. <br>
     */
    protected void parse() {}
    public void parseAllInscriptions() {
        Net net = (Net)getExtendable();
        Vector transitions = net.getTransitions();
        Enumeration e = transitions.elements();
        while (e.hasMoreElements()) {
            Transition tr = (Transition)e.nextElement();
            System.out.println("Parse Inscr. for transition " + tr.getName());
            parseInscriptions(tr);
        }
    }

    public void parseInscriptions(Transition tr) {
        // set variables null
        for (int i = 0; i < varField.length; i++) {
            varField[i][1] = null;
        }
        // for all incomming edges:
        Vector edges = tr.getIncomingEdges();
        Enumeration e = edges.elements();
        int i = 1;
        // at first parse and define variables
        // they must be defined before the functions are parsed
        while (e.hasMoreElements()) {
            Arc arc = (Arc)e.nextElement();
            place = arc.getPlace();
            if (place == null) {
                throw(new KernelUseException
                          ("parseInscriptions: No place of Arc to " + tr.getName()));
            }
            HLInscription inscr =
                (HLInscription)arc.getExtension("inscription");
            if (inscr == null) {
                throw(new NetSpecificationException
                          ("parseInscriptions: Each arc must have an inscription. Arc from " +
                          tr.getName() + "to " + arc.getPlace().getName()));
            }
            inscr.parseInscrVar(this);
        }
        // now parse expressions (functions or used variables)
        e = edges.elements();
        while (e.hasMoreElements()) {
            Arc arc = (Arc)e.nextElement();
            place = arc.getPlace();
            if (place == null) {
                throw(new KernelUseException
                          ("parseInscriptions: No place of arc to " + tr.getName()));
            }
            HLInscription inscr =
                (HLInscription)arc.getExtension("inscription");
            if (inscr == null) {
                throw(new NetSpecificationException
                          ("parseInscriptions: Each arc must have an inscription. Arc from " +
                          tr.getName() + "to " + arc.getPlace().getName()));
            }
            if (!inscr.isVariable())
                inscr.parseInscr(this);
        }
        // for all outgoing edges
        edges = tr.getOutgoingEdges();
        e = edges.elements();
        i = 1;
        while (e.hasMoreElements()) {
            Arc arc = (Arc)e.nextElement();
            place = arc.getPlace();
            if (place == null) {
                throw(new KernelUseException
                          ("parseInscriptions: No place of arc from " + tr.getName()));
            }
            HLInscription inscr =
                (HLInscription)arc.getExtension("inscription");
            if (inscr == null) {
                throw(new NetSpecificationException
                          ("parseInscriptions: Each arc must have an inscription. Arc from " +
                          arc.getPlace().getName() + "to " + tr.getName()));
            }
            inscr.parseInscr(this);
        }
    }

    /**
     * save the signature table into an extension node
     */
    public void save(Document doc, org.w3c.dom.Node extensionNode) {
        String externState = toString();
        if (hasGraphicsInfo()) {
            org.w3c.dom.Node graphicsNode = getGraphicsInfo();
            extensionNode.appendChild(doc.importNode(graphicsNode, true));
        }
        if (externState != null) {
            org.w3c.dom.Node valueNode = extensionNode.appendChild(doc.createElement("value"));
            appendStateNode(doc, valueNode);
            extensionNode.appendChild(valueNode);
        }
    }

    /**
     * get long name of a token type
     */
    public String translateType(String name) {
        int length = typeField.length;
        for (int i = 0; i < length; i++) {
            if (name.equals(typeField[i][0])) {
                return typeField[i][1];
            }
        }
        throw(new ExtensionValueException("Signature: Undefined token class name: " + name, null, null));
    }
} // Signature