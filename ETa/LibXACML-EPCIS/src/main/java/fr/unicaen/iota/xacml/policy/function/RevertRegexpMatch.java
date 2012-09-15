/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.xacml.policy.function;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.EvaluationResult;
import com.sun.xacml.cond.FunctionBase;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class RevertRegexpMatch extends FunctionBase {

    // the name of the function, which will be used publicly
    public static final String NAME = "urn:unicaen:xacml:1.0:function:revert-regexp-string-match";
    // the parameter types, in order, and whether or not they're bags
    private static final String params[] = {StringAttribute.identifier, StringAttribute.identifier};
    private static final boolean bagParams[] = {false, false};
    private static final Log log = LogFactory.getLog(RevertRegexpMatch.class);

    public RevertRegexpMatch() {
        // use the constructor that handles mixed argument types
        super(NAME, 0, params, bagParams, BooleanAttribute.identifier, false);
    }

    @Override
    public EvaluationResult evaluate(List inputs, EvaluationCtx context) {
        // Evaluate the arguments using the helper method...this will
        // catch any errors, and return values that can be compared
        AttributeValue[] argValues = new AttributeValue[inputs.size()];
        EvaluationResult result = evalArgs(inputs, context, argValues);
        if (result != null) {
            return result;
        }
        // cast the resolved values into specific types
        StringAttribute query = (StringAttribute) (argValues[0]);
        StringAttribute policyValue = (StringAttribute) (argValues[1]);
        log.debug("QUERY: " + query);
        log.debug("POLICY VALUE: " + policyValue);
        boolean evalResult;
        // now compare the values
        evalResult = query.getValue().matches(policyValue.getValue());
        log.debug("result: " + evalResult);
        // boolean returns are common, so there's a getInstance() for that
        return EvaluationResult.getInstance(evalResult);
    }
}
