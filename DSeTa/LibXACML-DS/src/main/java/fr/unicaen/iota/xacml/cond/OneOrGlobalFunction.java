/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *                     		
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.xacml.cond;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.cond.Evaluatable;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;
import java.util.Iterator;
import java.util.List;

/**
 *
 */
public class OneOrGlobalFunction extends FunctionBase {

    public static final String NAME_GLOBAL_DENY = FUNCTION_NS + "global-deny-one-permit";
    public static final String NAME_GLOBAL_PERMIT = FUNCTION_NS + "global-permit-one-deny";
    public static final int ID_GLOBAL_DENY = 0;
    public static final int ID_GLOBAL_PERMIT = 1;

    public OneOrGlobalFunction(String functionName) {
        super(functionName, getId(functionName), BooleanAttribute.identifier,
                false, -1, -1, BooleanAttribute.identifier, false);
    }

    public String getValue() {
        return (NAME_GLOBAL_PERMIT.equals(getFunctionName())) ? "ACCEPT" : "DENY";
    }

    public static int getId(String functionName) {
        if (functionName.equals(NAME_GLOBAL_DENY)) {
            return ID_GLOBAL_DENY;
        } else if (functionName.equals(NAME_GLOBAL_PERMIT)) {
            return ID_GLOBAL_PERMIT;
        } else {
            throw new IllegalArgumentException("unknown one or global function: "
                    + functionName);
        }
    }

    @Override
    public String toString() {
        return "Function = " + this.getFunctionName();
    }

    @Override
    public EvaluationResult evaluate(List inputs, EvaluationCtx context) {

        // Evaluate the arguments one by one. As soon as we can
        // return a result, do so. Return Indeterminate if any argument
        // evaluated is indeterminate.
        Iterator it = inputs.iterator();

        while (it.hasNext()) {
            Evaluatable eval = (Evaluatable) (it.next());

            // Evaluate the argument
            EvaluationResult result = eval.evaluate(context);

            if (result.indeterminate()) {
                return result;
            }

            AttributeValue value = result.getAttributeValue();
            boolean argBooleanValue = ((BooleanAttribute) value).getValue();

            switch (getFunctionId()) {
                case ID_GLOBAL_DENY:
                    if (argBooleanValue) {
                        return EvaluationResult.getTrueInstance();
                    }
                    break;
                case ID_GLOBAL_PERMIT:
                    if (argBooleanValue) {
                        return EvaluationResult.getFalseInstance();
                    }
                    break;
            }
        }

        if (getFunctionId() == ID_GLOBAL_DENY) {
            return EvaluationResult.getFalseInstance();
        } else {
            return EvaluationResult.getTrueInstance();
        }
    }
}
