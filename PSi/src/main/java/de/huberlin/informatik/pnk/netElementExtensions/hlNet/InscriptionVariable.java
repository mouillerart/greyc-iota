package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

/*
   Petri Net Kernel,
   Copyright 1996-2000 Petri Net Kernel Team,
   Petri Net Technology Group,
   Department of Computer Science,
   Humboldt-Universitaet zu Berlin, Germany
   All Rights Reserved.

   Do NOT modify the following lines!
   They contain CVS management information.
   $Log: InscriptionVariable.java,v $
   Revision 1.7  2001/10/11 16:59:05  oschmann
   Neue Release

   Revision 1.6  2001/06/12 07:03:57  oschmann
   Neueste Variante...

   Revision 1.5  2001/05/11 17:23:10  oschmann
   Alpha Version... hoffentlich komplett und unzerstvrt.

   Revision 1.4  2001/03/30 13:17:36  hohberg
   New error handling

   Revision 1.3  2001/03/28 08:02:53  hohberg
   Implementation of a subrange type

   Revision 1.2  2001/02/27 13:36:15  hohberg
   New exceptions

   Revision 1.1  2001/02/22 16:09:58  hohberg
   New package structure

   Revision 1.2  2001/02/05 13:22:11  hohberg
 *** empty log message ***

   Revision 1.1  2001/01/30 14:32:24  hohberg
   Implementation: Echo and GHS algorithms (Hohberg)


 */

import de.huberlin.informatik.pnk.exceptions.*;
import de.huberlin.informatik.pnk.kernel.*;
import de.huberlin.informatik.pnk.netElementExtensions.base.*;
import de.huberlin.informatik.pnk.netElementExtensions.llNet.TokenSet;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Vector;

/**
 * Tokenvariable is an inscription for an arc. <br>
 * By the constructer is the place of the arc specifyed.
 * Possible values of this Variable are the token of the place.
 */
public class InscriptionVariable implements InscriptionExpression {
    private Place place;
    private Signature signature;
    private int varIndex;
    private String subrange;
    private String subrangeType = null;
    private Object value = null; // Value of this variable
    private Enumeration tokenSequence = null; // Tokens to select
    private Marking marking = null; // last evaluated marking

    public InscriptionVariable(Place place) {
        this.place = place;
        signature = null;
    }

    public InscriptionVariable(Signature signature, int index, Place place) {
        this.signature = signature;
        varIndex = index;
        this.place = place;
    }

    /**
     * Returns  the value of this token variable as object. <br>
     */
    public Object evaluate() {
        if (value == null) {
            throw(new ExtensionValueException("evaluate(): No value of inscription variable",
                                              "inscription", null));
        }
        return value;
    }

    private ScalarType generateScalar(String representation, String extClassName) {
        if (representation == null) return null;
        Object[] params = {representation};
        // parameter is of type String:
        Class[] paramTypes = {String.class };
        try {
            //System.out.println("genScalar "+extClassName);
            Class extClass = Class.forName(extClassName);
            //System.out.println("Klasse gefunden");
            Constructor extClassConstructor = extClass.getConstructor(paramTypes);
            //System.out.println("Konstruktor gefunden");
            ScalarType scalarObject = (ScalarType)extClassConstructor.newInstance(params);
            //System.out.println("Objekt erzeugt");
            return scalarObject;
        } catch (ClassCastException cE) {
            System.out.println("nscription: Object of Class " + extClassName + " not a scalar type");
            throw(new RuntimeException("Inscription: Object of Class " + extClassName + " not a scalar type"));
        } catch (ClassNotFoundException cE) {
            System.out.println("Inscription: Object of Class " + extClassName + " not crated");
            throw(new RuntimeException("Inscription: Object of Class " + extClassName + " not crated"));
        } catch (NoSuchMethodException cE) {
            System.out.println("Inscription: No constructor with String parameter" + cE.toString());
        } catch (InstantiationException cE) {
            System.out.println("nscription: Object of Class " + extClassName + " not crated");
            throw(new RuntimeException("Inscription: Object of Class " + extClassName + " not crated"));
        } catch (InvocationTargetException cE) {
            System.out.println("Inscription: Klasse nicht gefunden: " + cE.toString());
            Throwable e = cE.getTargetException();
            System.out.println("Exception:" + e.toString());
        } catch (IllegalAccessException cE) {
            System.out.println("Klasse nicht gefunden: " + cE.toString());
        }
        return null;
    }

    private String getFirstScalar(String subrange) {
        int index = subrange.indexOf('.');
        if (index == -1) return subrange;
        return subrange.substring(0, index);
    }

    /**
     * Gets the place of this variable. <br>
     */
    public Place getPlace() {return place; }
    private String getSecondScalar(String subrange) {
        int index = subrange.indexOf("..");
        if (index == -1) return null;
        return subrange.substring(index + 2);
    }

    /**
     * More token to select?
     */
    public boolean hasMoreTokens() {
        return tokenSequence.hasMoreElements();
    }

    /**
     * Sets the possible value of this variable to
     * the sequence of tokens of the  place.
     * Returns true if place is marked else false. <br>
     */
    public boolean initValue() {
        Vector tokens;
        if (place != null) {
            TokenSet m;
            try {
                m = (TokenSet)place.getExtension("marking");
            } catch (ClassCastException e) {
                throw(new NetSpecificationException
                          ("Init InscriptionVariable: No TokenSet on place " + place.getName()));
            }
            if (m == null)
                throw(new NetSpecificationException
                          ("Init InscriptionVariable: No marking on place " + place.getName() + " defined"));
            tokens = m.getToken();
            System.out.println("Init variable on place " + place.getName() + " marking " + m);
            if (tokens == null)
                System.out.println("Init variable on place " + place.getName());
            System.out.println("Init variable: Number of token: " + tokens.size() + " on place " + place.getName());
            tokenSequence = tokens.elements();
            if (!isSubrange()) return setNextValue();
        } // isSubrange(): determine subrange in Vector objects
        String subrange = signature.getSubrangeOfVariable(varIndex);
        String subrangeType = signature.getTypeOfVariable(varIndex);
        String first = getFirstScalar(subrange);
        String second = getSecondScalar(subrange);
        ScalarType firstObject = generateScalar(first, subrangeType);
        ScalarType secondObject = generateScalar(second, subrangeType);
        Vector subrangeObjects = new Vector(10);
        subrangeObjects.addElement(firstObject);
        int counter = 0; // counts the objects in subrange
        ScalarType nextObj = firstObject;
        while (counter++ < 10 && nextObj != secondObject) {
            nextObj = nextObj.next();
            subrangeObjects.addElement(nextObj);
            System.out.println("Init variable: next object: " + nextObj);
        }
        if (place == null) {
            tokenSequence = subrangeObjects.elements();
            return setNextValue();
        }
        // else compute intersection of objects and tokenSequence
        Vector intersection = new Vector(5);
        while (tokenSequence.hasMoreElements()) {
            Object o = tokenSequence.nextElement();
            if (subrangeObjects.contains(o)) {
                intersection.addElement(o);
                System.out.println("Init variable with object " + o);
            }
        }
        tokenSequence = intersection.elements();
        return setNextValue();
    }

    private boolean isSubrange() {
        return signature != null;
    }

    /**
     * Sets the new value and marking if possible and returns true,
     * otherwhise false. <br>
     * Reqires call of  {@link #initValue()}.
     */
    public boolean setNextValue() {
        if ((tokenSequence != null) && tokenSequence.hasMoreElements()) {
            value = tokenSequence.nextElement();
            System.out.println("setNextValue " + value);
            return true;
        }
        return false;
    }

    /**
     * Sets the place of this variable. <br>
     */
    public void setPlace(Place p) {place = p; }
} //public  class TokenVariable