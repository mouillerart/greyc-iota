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
package fr.unicaen.iota.xacml.policy;

import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.cond.*;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.MyTargetFactory;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class SCEventTypeRule extends SCRule {

    private static final Log log = LogFactory.getLog(SCEventTypeRule.class);
    public static String RULEFILTER = "EventType";

    @Override
    protected String getId() {
        return "urn:oasis:names:tc:xacml:1.0:resource:eventType-id";
    }

    public SCEventTypeRule(String ruleId, List values) {
        super(ruleId, values);
    }

    public SCEventTypeRule(String ruleId, List values, String actions) {
        super(ruleId, values, actions);
    }

    SCEventTypeRule(String name, List eventTypes, OneOrGlobalFunction eventTypesFilterFunction) {
        super(name, eventTypes, eventTypesFilterFunction);
    }

    @Override
    public Rule createRule() {
        // Step 1: Define the identifier for the rule
        URI ruleId = null;
        try {
            ruleId = new URI(RULEFILTER);
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        // Step 2: Define the effect of the rule
        int effect = Result.DECISION_PERMIT;
        // Step 3: Get the target for the rule
        Target target = createTarget();
        // Step 4: Get the condition for the rule
        Condition condition = createCondition();
        // Step 5: Create the rule
        Rule openRule = new Rule(ruleId, effect, description, target, condition);
        return openRule;
    }

    @Override
    protected Target createTarget() {
        return MyTargetFactory.getTargetInstance(null, null, null);
    }

    @Override
    protected Condition createCondition() {
        // Define the name and type of the attribute
        // to be used in the condition
        URI designatorType = null;
        URI designatorId = null;
        try {
            designatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
            designatorId = new URI(getId());
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        // Pick the function that the condition uses
        FunctionFactory factory = FunctionFactory.getConditionInstance();
        Function conditionFunction = null;
        try {
            conditionFunction = factory.createFunction(globalFunction.getFunctionName());
        } catch (UnknownIdentifierException ex) {
            log.fatal(null, ex);
        } catch (FunctionTypeException ex) {
            log.fatal(null, ex);
        }

        if (this.values.isEmpty()) {
            List tmp = new ArrayList();
            tmp.add(BooleanAttribute.getInstance(false));
            return new Condition(conditionFunction, tmp);
        }

        Function conditionFunction2 = null;
        try {
            conditionFunction2 = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:" + "string-equal");
        } catch (UnknownIdentifierException ex) {
            log.fatal(null, ex);
        } catch (FunctionTypeException ex) {
            log.fatal(null, ex);
        }

        factory = FunctionFactory.getGeneralInstance();
        Function applyFunction;
        try {
            applyFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:" + "string-one-and-only");
        } catch (Exception e) {
            return null;
        }
        List conditionArgs = new ArrayList();
        for (Object value : values) {
            List applyArgs = new ArrayList();
            List applyArgsUser = new ArrayList();

            AttributeDesignator designator = new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET, designatorType, designatorId, true, null);
            applyArgsUser.add(designator);
            Apply applyUser = new Apply(applyFunction, applyArgsUser);

            applyArgs.add(applyUser);
            StringAttribute stringAttribute = new StringAttribute((String) value);
            applyArgs.add(stringAttribute);
            Apply applyCondition = new Apply(conditionFunction2, applyArgs);
            conditionArgs.add(applyCondition);
        }
        Condition condition = new Condition(conditionFunction, conditionArgs);
        return condition;
    }
}
