package de.huberlin.informatik.pnk.netElementExtensions.llNet;

/*
   Petri Net Kernel,
   Copyright 1996-1999 Petri Net Kernel Team,

   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: TokenSet.java,v $
   Revision 1.10  2001/10/11 16:59:23  oschmann
   Neue Release

   Revision 1.7  2001/06/12 07:04:13  oschmann
   Neueste Variante...

   Revision 1.6  2001/05/11 17:23:26  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.5  2001/03/29 13:32:00  hohberg
   More detailed error messages

   Revision 1.4  2001/03/26 08:51:38  hohberg
   Token class name if no name extension

   Revision 1.3  2001/02/27 13:30:47  hohberg
   New exceptions

   Revision 1.2  2001/02/23 15:32:25  hohberg
   Implementation initial marking given by a function

   Revision 1.1  2001/02/22 16:11:05  hohberg
   New package structure

   Revision 1.8  2001/02/20 15:43:14  hohberg
   Implementation of TokenBag

   Revision 1.7  2001/02/20 09:29:15  hohberg
   Better implementation

   Revision 1.6  2001/02/19 14:30:24  hohberg
   New structure of GHS simulator

   Revision 1.5  2001/02/16 15:35:31  hohberg
   New acces to the graph

   Revision 1.4  2001/02/15 12:57:14  hohberg
   Local parse and parse with context

   Revision 1.3  2001/02/13 09:55:42  hohberg
   Determines token type using the signature

   Revision 1.2  2001/02/02 08:11:53  hohberg
   New representation of token type (Hohberg)

   Revision 1.1  2001/01/30 14:32:27  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)

   Revision 1.6  2000/09/01 08:07:28  hohberg
   Code revision

   Revision 1.5  2000/08/30 14:22:50  hohberg
   Update of comments

 */

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;
import de.huberlin.informatik.pnk.netElementExtensions.hlNet.Signature;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Defines a marking as a set of {@TokenType token}.
 * The tokens type is String or defined by an
 * {@Extension extension} of the extendable with id "tokenClass".
 * <br>
 *
   <pre>Internal representation: vector of token objects.

   External representation: [token delimiter]*
        delimiter: space | comma | nl
   </pre>
 * Examples:<br>
 * token1, token2, token3 <br>
 * oneToken
 */
public class TokenSet extends Marking {
    final protected static String tokenClassId = "tokenClass";
    protected Vector internState = new Vector(5); // set of token
    protected String tokenClassName; // name of token class
    protected Class tokenClass; // to construct a token
    protected static Signature signature = null; // to translate token class name

    /**
     * Constructor specifying the extendable, not the marking. <br>
     * The extendable must be a place.
     */
    public TokenSet(Extendable place) {
        super(place); // sets token type and internState calling parse
        //System.out.println("Start TokenSet:");
    }

    /**
     * Constructor specifying the extendableand the signature of the net. <br>
     * The extendable must be a place.
     */
    /**
     *  Constructor specifying the extendable, a place,
     *  and the <code>marking</code>. <br>
     */
    public TokenSet(Extendable place, String marking) {
        super(place, marking); // sets token type and internState calling parse
    }

    /**
     * Adds a token to internal and extenal state. <br>
     * Used to construct a token set for given token.
     * (Token class and representation of token is unknown.)
     */
    public void addToken(Object o) {
        setExternState(toString() + " " + o.toString());
        if (!internState.contains(o)) internState.addElement(o);
    }

    /**
     * The (internal) state gets the set of token
     * representend by the external representation -
     * a sequence of token or a parameter free token function.<br>
     * The external representation changes in the case of token function to the sequence of token.
     * Token class name is determined using the net extensions.
     */
    public void checkContextAndParse() {
        tokenClassName = getTokenClassName(); // from context
        String repr = toString();
        // function call? ("name()")
        int l = repr.length();
        if (((l - 2) >= 0) && (repr.charAt(l - 2) == '(') && (repr.charAt(l - 1) == ')')) {
            internState = evaluateFunction(repr);
            setExternState(externRepresentation());
            updateValue();     // send new representation to observer
            return;
        }
        internState = new Vector(5);
        Vector v = selectSubstrings(repr, " ,;\n");
        if (v.isEmpty()) return;
        else {
            Enumeration e = v.elements();
            String str;
            while (e.hasMoreElements()) {
                str = (String)e.nextElement();
                Object o = toToken(str);
                if (o == null)
                    System.out.println(str + " is not a token!");
                else internStateAdd(o);
            }
        }
    }

    /**
     * Returns <code>true</code> if this marking contains
     * all token contained in marking m, otherwise <code>false</code>. <br>
     */
    public boolean contains(Marking m) {
        TokenSet marking = toTokenSet(m);
        if (marking.isEmpty()) return true;
        if (isEmpty()) return false;
        Enumeration e = marking.internState.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (!internState.contains(o)) return false;
        }
        return true;
    }

    /**
     * Evaluate static function evaluateInitial(String) of token class.
     * Implementation: Construct token without parameters and evaluate method
     */
    private Vector evaluateFunction(String str) {
        TokenFunction tokenFct;
        Class[] paramTypes = {};
        Object[] params = {};
        // System.out.println("** Tokenklasse:  "+tokenClassName+" Function: "+str);
        try {
            tokenClass = Class.forName(tokenClassName);
            Constructor tokenClassConstructor =
                tokenClass.getConstructor(paramTypes);
            // System.out.println("Konstruktor gefunden");
            tokenFct = (TokenFunction)tokenClassConstructor.newInstance(params);
            return tokenFct.evaluateInitial(str);
        } catch (ClassNotFoundException cE) {
            System.out.println("toToken:" + cE.toString());
        } catch (NoSuchMethodException cE) {
            System.out.println("toToken:" + cE.toString());
        } catch (InstantiationException cE) {
            System.out.println(cE.toString());
        } catch (InvocationTargetException cE) {
            System.out.println("toToken:" + cE.toString());
            Throwable e = cE.getTargetException();
            System.out.println("toToken:" + e.toString());
        } catch (IllegalAccessException cE) {
            System.out.println("Klasse nicht gefunden: " + cE.toString());
        }
        throw(new ExtensionValueException("ToToken(): Exception while constructing token function " +
                                          str + " of class " + tokenClassName + "!", null, null));
    }

    /**
     * Translates the internal state in its String representation.
     */
    protected String externRepresentation() {
        // neue externe Repraesentation
        String externState = "";
        int l = 0;
        int actL = 0;
        Enumeration e = internState.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (externState.length() - l > 15) {
                l = externState.length();
                externState = externState + " \n" + o.toString();
            } else {
                externState = externState + " " + o.toString();
            }
        }
        return externState;
    }

    /**
     * Gets the class name of this object
     */
    public String getClassName() {return getClass().getName(); }
    /**
     * Gets the first element of this set. <br>
     */
    public Object getFirstToken() {
        return internState.firstElement();
    }

    /**
     * Givs an Enumeration of the token of this marking
     */
    public Vector getToken() {
        return internState;
    }

/*------------------------------- private  -------------------------------*/

    private String getTokenClassName() {
        Extension e = (Extension)getExtendable().getExtension(tokenClassId);
        if (e == null) return "java.lang.String";  // no token class defined
        String name = e.toString();
        if (name == null) return "java.lang.String";
        if (signature == null) {
            Net net = (Net)getGraph();
            signature = (Signature)net.getExtension("signature");
        }
        if (signature == null) return name;
        return signature.translateType(name);
    }

    protected void internStateAdd(Object o) {
        if (!internState.contains(o)) internState.addElement(o);
    }

    protected boolean isValid() {
        String str = toString();
        return isValid(str);
    }

    protected boolean isValid(Extendable extendable) {
        /**
                Nur fuer Stellen zulaessig!
         */
        return false;
    }

    /**
     * Returns <code>true</code> if the extern representation is
     * a set of token. <br>
     */
    protected boolean isValid(String str) {
        if (str == defaultToString()) return true;  // leere Markierung
        // Zerlegung des Strings in die Markenrepraesentationen
        // und Test der Markenrepraesentationen
        Vector v = selectSubstrings(toString(), " ,;\n");
        Enumeration e = v.elements();
        while (e.hasMoreElements()) {
            if (toToken((String)e.nextElement()) == null) return false; }
        return true;
    }

////////////////  Implementation of interface Marking: //////////
//                 add, sub, contains

    public void localAdd(Marking m) {
        TokenSet marking = toTokenSet(m);
        // load internal representation
        Enumeration e = marking.internState.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            addToken(o);
        }
    } // void add(TokenSet marking)

    /**
     * Requires: contains(marking).
     * No error message if not.
     */
    public void localSub(Marking m) {
        TokenSet marking = toTokenSet(m);
        if (marking.isEmpty()) return;
        // interne Repraesentation bestimmen
        Enumeration e = marking.internState.elements();
        while (e.hasMoreElements()) {
            Object o = e.nextElement();
            if (internState.contains(o)) internState.removeElement(o);
        }
        setExternState(externRepresentation());
    }

    /**
           Zerlegung des Strings str in die Markenrepraesentationen
           und Eintragen der Markenrepraesentationen in den Vektor.

           External representation: [token  delimiter]*
                 delimiter:: space | comma | nl

     */
    private Vector selectSubstrings(String str, String delimiters) {
        Vector v = new Vector(5);
        int l = str.length();
        //System.out.println("Select-Start "+str);
        int i = 0;
        int start = 0;
        while (i <= l - 1) { // noch Zeichen vorhanden
            // Delimiter uebergehen
            while (i < l) {
                if (delimiters.indexOf(str.charAt(i)) < 0)
                    break;  // Zeichen an pos i kein Delimiter
                i++;
            }
            // Markenende bestimmen
            if (i >= l) break;  //Ende erreicht
            start = i;
            // lock for tupels
            if (str.charAt(i) == '(') {
                i++;
                while (i < l - 1 && str.charAt(i) != ')') {i++; }
                i++;
                v.addElement(str.substring(start, i));
            } else { // lock for single token
                for (; i < l; i++) {
                    if (delimiters.indexOf(str.charAt(i)) >= 0)
                        break;  // char at i is delimiter
                }
                v.addElement(str.substring(start, i));
            }
            //System.out.println("Select"+str.substring(start,i));
        }
        return v;
    }

    public static void setSignature(Signature s)
    {signature = s; }
    /**
     * Constructs a new Token with representation <code>str</code>. <br>
     * Returns false if str is not a representation of a token.
     */
    private Object toToken(String str) {
        Class[] paramTypes = {String.class };
        Object[] params = {str};
        // token class name may be edited
        //(getExtendable().getExtension(tokenClassId)).toString();
        if (tokenClassName == null) tokenClassName = "java.lang.String";
        // System.out.println("**toToken Tokenklasse:  "+tokenClassName+" Token: "+str);
        try {
            tokenClass = Class.forName(tokenClassName);
            Constructor tokenClassConstructor =
                tokenClass.getConstructor(paramTypes);
            // System.out.println("Konstruktor gefunden");
            Object token = tokenClassConstructor.newInstance(params);
            return token;
        } catch (ClassNotFoundException cE) {
            System.out.println("toToken:" + cE.toString());
        } catch (NoSuchMethodException cE) {
            System.out.println("toToken: No constructor with String parameter" + cE.toString());
        } catch (InstantiationException cE) {
            System.out.println("toToken:" + cE.toString());
        } catch (InvocationTargetException cE) {
            System.out.println("toToken:" + cE.toString());
            Throwable e = cE.getTargetException();
            System.out.println("toToken:" + e.toString());
        } catch (IllegalAccessException cE) {
            System.out.println("toToken: Klasse nicht gefunden: " + cE.toString());
        }
        throw(new ExtensionValueException("ToToken(): Exception while constructing token " +
                                          str + " of class " + tokenClassName + "!", null, null));
    }

    protected TokenSet toTokenSet(Marking m) {
        try
        {return (TokenSet)m; } catch (ClassCastException e) {
            System.out.println("Incompatible marking type");
            return new TokenSet(null); // leere Menge von Token
        }
    }
} // class TokenSet