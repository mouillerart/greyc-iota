package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Java source Inscription.java is part of the
   Petri Net Kernel Java reimplementation.
   Inscription.java has been created by the
   PNK JAVA code generator script.

   Date of last code generator run: Jul 08, 1999

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: HLInscription.java,v $
   Revision 1.8  2001/10/11 16:58:59  oschmann
   Neue Release

   Revision 1.6  2001/06/12 07:03:52  oschmann
   Neueste Variante...

   Revision 1.5  2001/05/11 17:23:04  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.4  2001/03/29 13:28:42  hohberg
   Implementations of constants in inscription expression

   Revision 1.3  2001/03/28 08:05:43  hohberg
   Implementation of Inscription variables of subrange type

   Revision 1.2  2001/02/27 13:34:30  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:09:55  hohberg
   New package structure

   Revision 1.3  2001/02/15 12:57:13  hohberg
   Local parse and parse with context

   Revision 1.2  2001/02/05 13:25:24  hohberg
 *** empty log message ***

   Revision 1.1  2001/01/30 14:32:22  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)

 */
import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;
import de.huberlin.informatik.pnk.netElementExtensions.llNet.TokenBag;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * The {@link FiringRule firing rule} of a {@link Net Petri Net} is a
 * standard {@link Extension extension}. <br>
 * The HLRule requires
 * an {@link Arc arc} inscription of
 * type {@link HLInscription}.
 */
public class HLInscription extends Extension implements Inscription {
    private InscriptionVariable variable = null;
    private InscriptionExpression expression = null;
    private Arc arc;
    private Signature signature;
    private Place place;
    private String inscr;
    private int length;
    private int index;

    /**
     * Constructor specifying the extended extendable (an arc). <br>
     */
    public HLInscription(Extendable arc) {
        super(arc);
        setArc(arc);
    }

    /**
     *  Constructor specifying the extendable, an arc,
     *  and the <code>inscription</code> parsed by the super class. <br>
     */
    public HLInscription(Extendable arc, String inscription) {
        super(arc, inscription);
        setArc(arc);
    }

    /**
     * Computes the inscription expression and gets one
     * or a sequence of tokens or a Marking
     * Tokens are packed in a token bag (that is more general then TokenSet).
     */
    public Marking evaluate() {
        TokenBag ts = new TokenBag(place);
        if (variable != null) {
            ts.addToken(variable.evaluate());
            return ts;
        }
        if (expression != null) { // insert one or a sequence of token in ts
            Object o = expression.evaluate();
            if (o instanceof Marking) {
                return (Marking)o;
            }
            if (o == null) {
                return ts;
            }
            try {
                Vector v = (Vector)o;
                Enumeration e = v.elements();
                while (e.hasMoreElements()) {
                    ts.addToken(e.nextElement());
                }
            } catch (ClassCastException e) {
                ts.addToken(o);
            }
            return ts;
        }
        throw (new KernelUseException("HLInscription.evaluate(): Undefined inscription"));
    }

    /**
     * The i-th parameter Object of function fct given by str is generated
     */
    private Object generateParObject(Signature sig, StringBuffer fctIndex, int i, String str) {
        // get the parameter type of parameter i from signature sig
        String className = sig.getParameterType(fctIndex, i);
        if (className == null) {
            throw (new ExtensionValueException("HLInscription: Undefined parameter type of " + i +
                                               "-th parameter at index " + index + " of " + inscr, null, null));
            //String className = "de.huberlin.informatik.pnk.netElementExtensions.hlNet.ScalarTypeInt";
        }
        Class parClass;
        Class[] paramTypes = {String.class };
        Object[] params = {str};
        if (className == null) {
            className = "java.lang.String";
        }
        System.out.println("** generateParObject: Klasse: " + className + "(" + str + ")");
        try {
            parClass = Class.forName(className);
            Constructor parClassConstructor =
                parClass.getConstructor(paramTypes);
            //System.out.println("Konstruktor gefunden");
            return parClassConstructor.newInstance(params);
        } catch (ClassNotFoundException cE) {
            System.out.println("generateParObject:" + cE.toString());
        } catch (NoSuchMethodException cE) {
            System.out.println("generateParObject: No constructor with String parameter" + cE.toString());
        } catch (InstantiationException cE) {
            System.out.println("generateParObject:" + cE.toString());
        } catch (InvocationTargetException cE) {
            System.out.println("generateParObject:" + cE.toString());
            Throwable e = cE.getTargetException();
            System.out.println("generateParObject:" + e.toString());
        } catch (IllegalAccessException cE) {
            System.out.println("Klasse nicht gefunden: " + cE.toString());
        }
        throw (new ExtensionValueException("generateParObject(): Exception while constructing constant parameter " +
                                           str + " of class " + className + "!", null, null));
    }

    public InscriptionExpression getFunction() {
        return expression;
    }

    public Place getPlace() {
        return place;
    }

    public InscriptionVariable getVariable() {
        return variable;
    }

    protected boolean isValid() {
        String str = toString();
        return isValid(str);
    }

    /**
     * Only valid for type Arc!
     */
    protected boolean isValid(Extendable ext) {
        return ext.getClass().getName().
               equals("de.huberlin.informatik.pnk.kernel.Arc");
    }

    /**
     * Returns <code>true</code> if the extern representation is
     * a HL-Inscription. <br>
     */
    protected boolean isValid(String str) {
        if (str == defaultToString()) {
            return true;
        }
        return true; // @SLS WTF!
    }

    public boolean isVariable() {
        return variable != null;
    }

    private String nextName() {
        skipBlank();
        int start = index;
        while ((index < length) &&
               Character.isLetterOrDigit(inscr.charAt(index))) {
            index++;
        }
        if (start == index) {
            return null;
        }
        return inscr.substring(start, index);
    }

    private String nextNumber() {
        skipBlank();
        int start = index;
        while ((index < length) && Character.isDigit(inscr.charAt(index))) {
            index++;
        }
        if (start == index) {
            return null;
        }
        return inscr.substring(start, index);
    }

    private String nextPar() {
        String par = nextName();
        if (par == null) {
            par = nextString();
        }
        if (par == null) {
            par = nextNumber();
        }
        return par;
    }

    private String nextString() {
        skipBlank();
        if (inscr.charAt(index) != '"') {
            return null;
        }
        int start = index;
        index++; // '"' is part of string
        while ((index < length) && (inscr.charAt(index) != '"')) {
            index++;
        }
        index++; // closing '"' is not part of string
        if (start == index - 1) {
            return null;
        }
        return inscr.substring(start, index - 1);
    }

    public void parse() { // nothing to do
    }

    /**
     * fctName( par1, ...) or number or String
     */
    private InscriptionFunction parseExpression() {
        String fctName = nextName(); // method name
        System.out.println("FctName: " + fctName);
        skipBlank();
        if ((index >= length - 1) // end of string
            || (inscr.charAt(index) != '(')) {
            System.out.println("Index: " + index + " of " + inscr);
            System.out.println("Missing '(' " + inscr.substring(index));
            return null;
            // syntactical error: missing '('
        }
        StringBuffer indexBuffer = new StringBuffer(5);
        InscriptionFunction fct = (InscriptionFunction)signature.getFunction(fctName, indexBuffer);
        index++;
        String parameter = nextPar();
        //System.out.println("Index: "+index+" of "+inscr);
        skipBlank();
        if (parameter == null) { // no parameter
            if (inscr.charAt(index) == ')') {
                index++;
                return fct;
            } else {
                System.out.println("Missing ')' " + inscr);
                return fct;
            }
        }
        // list of parameters for fct
        parseParlistForFct(fct, indexBuffer, parameter);

        // end parameter list: ')' expected
        if (inscr.charAt(index) == ')') {
            index++;
            return fct;
        } else {
            System.out.println("Missing ')' " + inscr);
            return fct;
        }
    } // end parse fct

    /**
     * Parses the inscription and generates a functioncall
     * or variable object. <br>
     * When the signature of the net is updated
     * the inscription is to parse again.
     * !! Parse first variables using parseInscriptionVar(..)!!
     * expr = var | expr.fct | fct
     * fct = name(parlist)
     * parlist = expr | exprlist, expr
     */
    public void parseInscr(Signature signature) {
        place = arc.getPlace(); // used from variables
        inscr = toString();
        length = inscr.length();
        index = 0;
        String name = nextName();
        skipBlank();
        if (name == null) {
            throw (new ExtensionValueException("Parse HLInscription.: Wrong inscription " + inscr + " of arc" +
                                               " from node " + arc.getSource().getName() + " to node " +
                                               arc.getTarget().getName(), "inscription", arc));
            // no inscription defined
        }
        Object obj;
        if ((index < length - 1) && (inscr.charAt(index) == '(')) { // static function
            obj = null;
            index = -1; // reset index - name is function name
        } else {
            obj = (InscriptionVariable)signature.getVariable(name);
            // Obj may be qualified
            if ((index >= length - 1) // end of string
                || (inscr.charAt(index) != '.')) {   // Variable
                variable = (InscriptionVariable)obj;
                System.out.println("Inscription " + inscr + " is a variable");
                return;
            }
        }
        //System.out.println("Inscription " + inscr + " is a function");
        InscriptionFunction fct;
        do {
            index++;
            //System.out.println("Index: "+index);
            fct = parseExpression();
            if (fct == null) {
                throw (new ExtensionValueException("Undefined fuction: " + name, "inscription", arc));
            }
            fct.setParam(1, obj); // Method of this object
            obj = fct; // expression computes an object
        } while ((index < length - 1) // end of string
                 && (inscr.charAt(index) == '.'));
        expression = fct;
    }

    /**
     * Parses the inscription and generates an InscriptionVariable
     * if possible or set variable=null and return. <br>
     * When the signature of the net is updated
     * parse the inscription again.
     */
    public void parseInscrVar(Signature signature) {
        place = arc.getPlace(); // used by variables

        System.out.println("Place of arc: " + place.getName());
        variable = null; // assume inscription is not a variable
        inscr = toString();
        length = inscr.length();
        index = 0;
        String name = nextName();
        skipBlank();
        if (name == null) {
            variable = null; // no variable found
            return;
        }
        if ((index >= length - 1) // end of string
            || (inscr.charAt(index) != '.')) {   // Variable
            // signature generates the variable
            variable = (InscriptionVariable)signature.defineVariable(name);
            // Error in signature detected!
        }
        // if variable == null then no variable found
    }

    /**
     * Parse parameter list of fct -
     * first parameter beginns with a name or is a string or number
     */
    private void parseParlistForFct(InscriptionFunction fct, StringBuffer fctIndex, String parameter) {
        int parIndex = 2; // 0: MethodCall; 1: object of this method
        System.out.println("1. parameter");
        Object par = null;
        while (parameter != null) { // parameter starts with variable string or number
            if (Character.isLetter(parameter.charAt(0))) { // parameter is a name: Variable or name of static function
                if ((index >= length - 1) // end of string
                    || ((inscr.charAt(index) != '.') && (inscr.charAt(index) != '('))) {   // parameter is variable
                    par = (InscriptionVariable)signature.getVariable(parameter);
                    // Error in signature detected!
                    fct.setParam(parIndex++, par);
                    System.out.println(" parameter is variable");
                } else { // parameter is function call
                    if ((index < length - 1) && (inscr.charAt(index) == '(')) { // static function
                        par = null;
                        // reset index of the begin of parameter
                        index = index - (parameter.length() + 1);
                    } else { // dynamic function of the form: var.fct( parameters )
                        par = (InscriptionVariable)signature.getVariable(parameter);
                        // Error in signature detected!
                    }
                    // parse function call
                    InscriptionFunction parFct;
                    do {
                        index++;
                        parFct = parseExpression();
                        if (parFct == null) {
                            throw (new ExtensionValueException("Undefined fuction for object: " + parameter, "inscription", arc));
                        }
                        parFct.setParam(1, par); // Method of this par
                        par = parFct; // expression computes an object
                    } while ((index < length - 1) && (inscr.charAt(index) == '.'));
                    System.out.println(" parameter is function");
                    fct.setParam(parIndex, par);
                    parIndex++;
                } // end parameter is function call
            } // end of parameter is a name
            if (Character.isDigit(parameter.charAt(0))) { // parameter is a number
                System.out.println(" parameter is number " + parameter);
                // parIndex-2 is index of this parameter of the function
                par = generateParObject(signature, fctIndex, parIndex - 2, parameter);
                fct.setParam(parIndex, par);
                parIndex++;
            } else if (parameter.charAt(0) == '"') { // parameter is String
                System.out.println(" parameter is string " + parameter.substring(1));
                // parIndex-2 is index of this parameter of the function
                par = generateParObject(signature, fctIndex, parIndex - 2, parameter.substring(1));
                fct.setParam(parIndex, par);
                parIndex++;
            }
            skipBlank();
            if ((index < length - 1) && (inscr.charAt(index) == ',')) {
                // more parameters
                index++; //skip ','
                skipBlank();
                parameter = nextPar();
            } else {
                parameter = null;
            }
        } // while(parameter != null)
    }

    /* --------------------------  private   ------------------------*/

    /**
     * Initiates this.arc and inscription.
     * It is not possible to initiate place because creation of arc is not finished.
     */
    private void setArc(Extendable arc) {
        //System.out.println("Sig HLInscription-Start");
        if (arc == null) {
            throw (new NetSpecificationException("HLInscription must be an Extension of an arc: " + arc.getClass().getName()));
        }
        try {
            this.arc = (Arc)arc;
        } catch (ClassCastException e) {
            throw (new NetSpecificationException("HLInscription must be an Extension of an arc: " + arc.getClass().getName()));
        }
        try {
            signature = (Signature)(this.arc.getNet()).getExtension("signature");
        } catch (ClassCastException e) {
            throw (new NetSpecificationException("Extension \"signature\" is not of type Signature "));
        }
        if (signature == null) {
            throw (new NetSpecificationException("Extension \"signature\" not defined "));
        }
    }

    private void skipBlank() {
        while ((index < length) && (inscr.charAt(index) == ' ')) {
            index++;
        }
    }
} // public class HLInscription
