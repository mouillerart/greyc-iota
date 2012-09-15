package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: HLGuard.java,v $
   Revision 1.7  2001/10/11 16:58:58  oschmann
   Neue Release

   Revision 1.5  2001/06/12 07:03:51  oschmann
   Neueste Variante...

   Revision 1.4  2001/05/11 17:23:03  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.3  2001/03/29 13:28:42  hohberg
   Implementations of constants in inscription expression

   Revision 1.2  2001/02/27 13:34:29  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:09:55  hohberg
   New package structure

   Revision 1.2  2001/02/05 13:22:11  hohberg
 *** empty log message ***

   Revision 1.1  2001/01/30 14:32:21  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)

 */

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;

/**
 * A HLGuard specified a Boolean-function
 * parameters are inscription variables
 */

public class HLGuard extends Extension {
    private Signature signature;
    private InscriptionExpression expression = null;

    private String inscr;
    private int length;
    private int index;

    /**
     * Constructor specifying the extended extendable (an arc). <br>
     */
    public HLGuard(Extendable transition) {
        super(transition);
        setSignature(transition);
    }

    /**
     *  Constructor specifying the extendable, an arc,
     *  and the <code>inscription</code> parsed by the super class. <br>
     */
    public HLGuard(Extendable transition, String guard) {
        super(transition, guard);
        // System.out.println("Start HLGuard(transition, guard)");
        setSignature(transition);
    }

    /**
     * Computes the inscription expression and gets
     * an Boolean object.
     */
    public Boolean evaluate() {
        if (expression != null)
            return (Boolean)expression.evaluate();
        else
            throw(new ExtensionValueException
                      ("HLGuard.evaluate(): Undefined inscription", "inscription", getExtendable()));
    }

    protected boolean isValid() {
        String str = toString();
        return isValid(str);
    }

    /**
     * Only valid for type Transition!
     */
    protected boolean isValid(Extendable ext) {
        return ext.getClass().getName().
               equals("de.huberlin.informatik.pnk.kernel.Transition");
    }

    /**
     * Returns <code>true</code> if the extern representation is
     * a HL-Inscription. <br>
     */
    protected boolean isValid(String str) {
        if (str == defaultToString()) return true;
        return true;
    }

    private String nextName() {
        skipBlank();
        int start = index;
        while ((index < length) &&
               Character.isLetterOrDigit(inscr.charAt(index))) {
            index++;
        }
        if (start == index) return null;
        return inscr.substring(start, index);
    }

    public void parse() { // nothing to do
        // parsed before simulation of a net!
    }

    /**
     * fctName( par1, ...)
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
        InscriptionFunction fct = (InscriptionFunction)
                                  signature.getFunction(fctName, null);
        index++;
        String varName = nextName();
//System.out.println("Index: "+index+" of "+inscr);
        skipBlank();
        if (varName == null) { // no parameter
            if (inscr.charAt(index) == ')') {
                index++;
                return fct;
            } else {
                System.out.println("Missing ')' " + inscr);
                return fct;
            }
        }
        // list of parameters for fct
        parseParlistForFct(fct, varName);

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
     * Parses the inscription and generates a functioncall object.
     * When the signature of the net is updated
     * the inscription is to parse again.
     * !! Parse first variables using parseInscriptionVar(..)!!
     * guard = variable.fct
     * expr = variable | expr.fct
     * fct = name(parlist)
     * parlist = expr | parlist, expr
     */
    public void parseGuard(Signature signature) {
        inscr = toString();
        System.out.println("parseGuard " + inscr + " of Transition " +
                           ((Transition)getExtendable()).getName());
        length = inscr.length();
        index = 0;
        String name = nextName();
        skipBlank();
        Object obj = (InscriptionVariable)signature.getVariable(name);
        // Obj may be qualified
        if ((index >= length - 1) // end of string
            || (inscr.charAt(index) != '.')) {  // Variable?
            System.out.println(inscr + " is a variable?");
            throw(new ExtensionValueException(
                      "Guard must be an function, got: " + inscr, "inscription", getExtendable()));
        } else { // follows a function call
            //System.out.println(inscr + " is a function");
            InscriptionFunction fct;
            do {
                index++; System.out.println("Index: " + index);
                fct = parseExpression();
                if (fct == null)
                    throw(new ExtensionValueException("HLGuard: Undefined fuction: " + name, "inscription", getExtendable()));
                fct.setParam(1, obj); // Method of this object
                obj = fct; // expression computes an object
            } while ((index < length - 1) // end of string
                     && (inscr.charAt(index) == '.'));
            expression = fct;
        }
    }

    /**
     * Parse parameter list of fct -
     * first parameter beginns with varName
     */
    private void parseParlistForFct(InscriptionFunction fct, String varName) {
        int parIndex = 2;         // 0: MethodCall; 1: object of this method
        System.out.println("1. parameter");
        while (varName != null) { // parameter starts with variable varname
            InscriptionExpression par =
                (InscriptionVariable)signature.getVariable(varName);
            // Error in signature detected!
            if ((index >= length - 1) // end of string
                || (inscr.charAt(index) != '.')) { // parameter is variable
                fct.setParam(parIndex++, par);
                System.out.println(" parameter is variable");
            } else { // parameter is function call
                    // parse function call
                InscriptionFunction parFct;
                do {
                    index++;
                    parFct = parseExpression();
                    if (parFct == null)
                        throw(new ExtensionValueException("HLGuard: Undefined fuction for object: " + varName, null, null));
                    parFct.setParam(1, par); // Method of this par
                    par = parFct; // expression computes an object
                } while ((index < length - 1) // end of string
                         && (inscr.charAt(index) == '.'));
                System.out.println(" parameter is function");
                fct.setParam(parIndex, par); parIndex++;
            } // end of parameter
            skipBlank();
            if ((index < length - 1) && (inscr.charAt(index) == ',')) {
                // more parameters
                skipBlank();
                varName = nextName();
            } else {varName = null; }
        }
    }

/* --------------------------  private   ------------------------*/

    private void setSignature(Extendable transition) {
        if (transition == null) {
            throw(new NetSpecificationException
                      ("HLGuard must be an Extension of a transition but is null"));
        }
        try {Transition tr = (Transition)transition; } catch (ClassCastException e) {
            throw(new NetSpecificationException
                      ("HLGuard must be an Extension of a transition:  "
                      + transition.getClass().getName()));
        }
        try {
            //System.out.println("Sig HLGuard");
            signature = (Signature)
                        (transition.getNet()).getExtension("signature");
        } catch (ClassCastException e) {
            System.out.println("Extension \"signature\" of the net is not of type Signature ");
            throw(new NetSpecificationException
                      ("Extension \"signature\" of this net is not of type Signature "));
        }
        if (signature == null) {
            System.out.println("Extension \"signature\" not defined for this net ");
            throw(new NetSpecificationException
                      ("Extension \"signature\" not defined for this net "));
        }
        //System.out.println("End HLGuard");
    }

    private void skipBlank() {
        while ((index < length) && (inscr.charAt(index) == ' ')) {
            index++;
        }
    }
} // public class HLGuard