/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xacml.policy;

import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.cond.*;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.MyTargetFactory;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCRecordTimeRule extends SCRule {

    private static final Log log = LogFactory.getLog(SCRecordTimeRule.class);
    public static final String RULEFILTER = "RecordTime";

    @Override
    protected String getId() {
        return "urn:oasis:names:tc:xacml:1.0:resource:recordTime-id";
    }

    public SCRecordTimeRule(String ruleId, List values) throws Exception {
        super(ruleId, values);
        description = "RecordTime filter for SC: " + this.name;
        if (values.size() % 2 != 0) {
            throw new Exception("values have to be added in couple");
        }
    }

    public SCRecordTimeRule(String ruleId, List values, String actions) throws Exception {
        super(ruleId, values, actions);
        description = "RecordTime filter for SC: " + this.name;
        if (values.size() % 2 != 0) {
            throw new Exception("values have to be added in couple");
        }
    }

    SCRecordTimeRule(String name, List recordTimes, OneOrGlobalFunction recordTimesFilterFunction) {
        super(name, recordTimes, recordTimesFilterFunction);
        description = "RecordTime filter for SC: " + this.name;
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
        URI designatorType = null;
        URI designatorId = null;
        try {
            designatorType = new URI("http://www.w3.org/2001/XMLSchema#dateTime");
            designatorId = new URI(getId());
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
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
            conditionFunction2 = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:" + "and");
        } catch (UnknownIdentifierException ex) {
            log.fatal(null, ex);
        } catch (FunctionTypeException ex) {
            log.fatal(null, ex);
        }

        factory = FunctionFactory.getGeneralInstance();
        Function greaterFunction;
        try {
            greaterFunction = factory.createFunction(ComparisonFunction.NAME_DATETIME_GREATER_THAN_OR_EQUAL);
        } catch (Exception e) {
            log.fatal("Exception in condition creation", e);
            return null;
        }

        Function lessFunction;
        try {
            lessFunction = factory.createFunction(ComparisonFunction.NAME_DATETIME_LESS_THAN_OR_EQUAL);
        } catch (Exception e) {
            log.fatal("Exception in condition creation", e);
            return null;
        }

        URI dateDesignatorId = null;
        try {
            dateDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:resource:recordTime-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }

        Function dateFunction;
        try {
            dateFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:" + "dateTime-one-and-only");
        } catch (Exception e) {
            log.fatal("Exception in SCRecordTime condition", e);
            return null;
        }

        List applyCondArgs = new ArrayList();
        List conditionArgs = new ArrayList();
        for (Object datesOb : values) {
            applyCondArgs.clear();
            List dates = (List) datesOb;
            List conditionArguments = new ArrayList();
            List applyArgs = new ArrayList();
            List applyArgsUser = new ArrayList();

            List greatApplyArgs = new ArrayList();
            List greatApplyArgsUser = new ArrayList();

            AttributeDesignator lowDesignator = new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET, designatorType, dateDesignatorId, false, null);
            applyArgsUser.add(lowDesignator);
            Apply lowApplyUser = new Apply(dateFunction, applyArgsUser);
            DateTimeAttribute lowDateAttribute = new DateTimeAttribute((Date) dates.get(0));
            applyArgs.add(lowApplyUser);
            applyArgs.add(lowDateAttribute);
            Apply lowApply = new Apply(greaterFunction, applyArgs);
            applyCondArgs.add(lowApply);

            AttributeDesignator greatDesignator = new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET, designatorType, dateDesignatorId, false, null);
            greatApplyArgsUser.add(greatDesignator);
            Apply greatApplyUser = new Apply(dateFunction, greatApplyArgsUser);
            DateTimeAttribute greatDateAttribute = new DateTimeAttribute((Date) dates.get(1));
            greatApplyArgs.add(greatApplyUser);
            greatApplyArgs.add(greatDateAttribute);
            Apply applyCondition = new Apply(lessFunction, greatApplyArgs);
            applyCondArgs.add(applyCondition);
            Apply applyCond = new Apply(conditionFunction2, applyCondArgs);
            conditionArgs.add(applyCond);
        }
        Condition condition = new Condition(conditionFunction, conditionArgs);
        return condition;
    }
}
