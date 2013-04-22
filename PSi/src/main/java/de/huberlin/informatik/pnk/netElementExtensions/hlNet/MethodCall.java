package de.huberlin.informatik.pnk.netElementExtensions.hlNet;

import de.huberlin.informatik.pnk.exceptions.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MethodCall extends InscriptionFunction {
/*
   Interpretation eines Methodenrufs
 */

    Method interpretedMethod;
    Object givenObject;
    Object callParameters[]; // Parameterliste der Methode
    int numberOfParams = 0; // number of parameters
    String methodName;

    /**
     * A parameter object is given by a function call (MethodCall object)
     * or a constant (object not of type MethodCall)
     */
    public MethodCall(Method interpretedMethod, String str) {
        this.interpretedMethod = interpretedMethod;
        methodName = str;
    }

    /**
     * Berechnung des Funktionswertes
     * callParameters: 0: method 1: object, 2, .. parameters
     *  kind of parameters: method call / inscription variable / constant
     *
     */
    public Object evaluate() {
        System.out.println("Evaluate method " + methodName + " with " + numberOfParams);
        if (callParameters == null) return null;
        if (numberOfParams == 0) return null;  // No object
        Object calledObject;
        // first parameter is the object which method is called
        if (callParameters[1] == null) {
            calledObject = null; // static function
        } else if (callParameters[1].getClass().getName().
                   equals("de.huberlin.informatik.pnk.netElementExtensions.hlNet.MethodCall")) { //MethodCall-object
                                                                                                //System.out.println("Object is method call");
            calledObject = ((MethodCall)callParameters[1]).evaluate();
        } else {
            if (callParameters[1].getClass().getName().
                equals("de.huberlin.informatik.pnk.netElementExtensions.hlNet.InscriptionVariable")) { // System.out.println("Object is variable");
                calledObject = ((InscriptionVariable)callParameters[1]).evaluate();
            } else {
                System.out.println("MethodCall.evaluate: Object is given");
                calledObject = callParameters[1]; // constant
            }
        }
        Object[] methParms = null;
        if (numberOfParams > 1) {
            methParms = new Object[numberOfParams - 1];
            for (int i = 2; i <= numberOfParams; i++) {
                if (callParameters[i].getClass().getName().
                    equals("de.huberlin.informatik.pnk.netElementExtensions.hlNet.MethodCall")) { //MethodCall-object
                                                                                                 //System.out.println("Parameter is method call");
                    methParms[i - 2] = ((MethodCall)callParameters[i]).evaluate(); // to compute
                } else {
                    if (callParameters[i].getClass().getName().
                        equals("de.huberlin.informatik.pnk.netElementExtensions.hlNet.InscriptionVariable")) {
                        //System.out.println("Parameter is variable");
                        methParms[i - 2] =
                            ((InscriptionVariable)callParameters[i]).evaluate();
                    } else {
                        System.out.println("MethodCall.evaluate: Parameter is given");
                        methParms[i - 2] = callParameters[i]; // constant
                    }
                }
            }
        } // if (numberOfParams >1)
        try {
            System.out.println("MethodCall.evaluate: invoke method " + methodName);
            return interpretedMethod.invoke(calledObject, methParms);
        } catch (InvocationTargetException e) {
            System.out.println("MethodCall.evaluate: invoke method " + methodName);
            throw new ExtensionValueException("MethodCall.evaluate: Not possible to interpret method", null, null);
        } catch (IllegalAccessException e) {
            System.out.println("MethodCall.evaluate: invoke method " + methodName);
            throw new ExtensionValueException("MethodCall.evaluate: Not possible to interpret method", null, null);
        }
    }

    public void setParam(int index, Object parameter) {
        if (callParameters == null) {
            callParameters = new Object[5];
        } else {
            if (callParameters.length <= index) { // too short
                Object[] list = new Object[index + 2]; // space for 2 parameters
                // copy parameters to list
                for (int j = 0; j < numberOfParams; j++) {
                    list[j] = callParameters[j];
                }
                callParameters = list;
            }
        }
        callParameters[index] = parameter;
        numberOfParams++; // one more parameter
    }
} // class MethodCall
