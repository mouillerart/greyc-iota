package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

import de.huberlin.informatik.pnk.exceptions.*;
import java.lang.reflect.Method;

public class MethodDef {
/*
   Definition of a method
 */
    String className = "Ostern";
    String methDef = "berechneDatum(java.lang.Integer)";
    int length = methDef.length();

    int index = 0; // index of next character in methDef
    int parms;

    /**
     * The definition is a string of the form:
     * classname and methodname( parameter-types)
     */
    public MethodDef(String className, String methDefinition) {
        this.className = className;
        methDef = methDefinition;
        length = methDef.length();
    }

    /**
     * Genetes a method object specified by className and methDef
     * for example: "classX" and "methodM( ParType1, ParType2)"
     */
    public Method generateMethod() {
        String methName = nextName();
        if (methDef.charAt(index) != '(') {
            System.out.println("MethodDef: '(' expectet after method name in " + methDef);
            throw new ExtensionValueException(
                      "Parse inscription: '(' expectet after method name in " + methDef, null, null);
        }
        index++;
        Class cl;
        Method m;
        parms = numberOfParms();
        Class[] parTypes = null;
        if (parms > 0) { // generate list of parameter types
            parTypes = new Class[parms];
            for (int i = 0; i < parms; i++) {
                String parTypeName = nextQualifiedName();
                System.out.println(parTypeName);
                try {
                    parTypes[i] = Class.forName(parTypeName);
                } catch (ClassNotFoundException e) {
                    System.out.println("In signature for parameter type " + parTypeName + " class not found");
                    throw new NetSpecificationException("In signature for parameter type " + parTypeName + " class not found");
                }
            }
        }
        try {
            cl = Class.forName(className);
            m = cl.getDeclaredMethod(methName, parTypes);
            return m;
        } catch (ClassNotFoundException e) {
            System.out.println("In signature for method " + methName + " class not found");
            throw new NetSpecificationException("In signature for method " + methName + " class not found");
        } catch (NoSuchMethodException e) {
            System.out.println("In signature for method " + methName + " class not found");
            throw new NetSpecificationException("In signature for method " + methName + " class not found");
        }
    }

    public int getNumberOfParms() {return parms; }
    private String nextName() {
        skipBlank();
        int start = index;
        while ((index < length) &&
               Character.isLetterOrDigit(methDef.charAt(index))) {
            index++;
        }
        if (start == index) return null;
        return methDef.substring(start, index);
    }

    private String nextQualifiedName() {
        skipBlank();
        if (methDef.charAt(index) == ',') {
            index++;
        }
        skipBlank();
        int start = index;
        nextName();
        while (methDef.charAt(index) == '.') {
            index++;
            nextName();
        }
        if (start == index) return null;
        return methDef.substring(start, index);
    }

    /**
     * returns the number of method parameters by counting the ','
     */
    private int numberOfParms() {
        skipBlank();
        if (methDef.charAt(index) == ')') return 0;
        int pars = 1;
        int i = index;
        while (i < length) {  // search ','
            if (methDef.charAt(i) == ',') pars++;
            i++;
        }
        return pars;
    }

    private void skipBlank() {
        while ((index < length) && (methDef.charAt(index) == ' ')) {
            index++;
        }
    }
} // class MethodDef