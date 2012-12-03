/*
 *  This program is a part of the IoTa Project.
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

import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.BooleanAttribute;
import com.sun.xacml.attr.DoubleAttribute;
import com.sun.xacml.cond.*;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCNumBivaluesExtensionRule extends SCExtensionBivaluesRule {

    private static final Log log = LogFactory.getLog(SCNumBivaluesExtensionRule.class);

    public SCNumBivaluesExtensionRule(String ruleId, String extensionName, List values) throws Exception {
        super(ruleId, extensionName, values);
    }

    public SCNumBivaluesExtensionRule(String ruleId, String extensionName, List values, String actions) throws Exception {
        super(ruleId, extensionName, values, actions);
    }

    public SCNumBivaluesExtensionRule(String ruleId, String extensionName, List values, OneOrGlobalFunction extensionsFilterFunction) {
        super(ruleId, extensionName, values, extensionsFilterFunction);
    }

    @Override
    protected Condition createCondition() {
        URI designatorType = null;
        URI designatorId = null;
        try {
            designatorType = new URI("http://www.w3.org/2001/XMLSchema#double");
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
            greaterFunction = factory.createFunction(ComparisonFunction.NAME_DOUBLE_GREATER_THAN_OR_EQUAL);
        } catch (Exception e) {
            log.fatal("Exception in condition creation", e);
            return null;
        }

        Function lessFunction;
        try {
            lessFunction = factory.createFunction(ComparisonFunction.NAME_DOUBLE_LESS_THAN_OR_EQUAL);
        } catch (Exception e) {
            log.fatal("Exception in condition creation", e);
            return null;
        }

        Function numFunction;
        try {
            numFunction = factory.createFunction("urn:oasis:names:tc:xacml:1.0:function:" + "double-one-and-only");
        } catch (Exception e) {
            log.fatal("Exception in SCDateBiValuesExtension condition", e);
            return null;
        }

        List applyCondArgs = new ArrayList();
        List conditionArgs = new ArrayList();
        for (Object extOb : values) {
            applyCondArgs.clear();
            List extensions = (List) extOb;
            List applyArgs = new ArrayList();
            List applyArgsUser = new ArrayList();

            List greatApplyArgs = new ArrayList();
            List greatApplyArgsUser = new ArrayList();

            AttributeDesignator lowDesignator = new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET, designatorType, designatorId, false, null);
            applyArgsUser.add(lowDesignator);
            Apply lowApplyUser = new Apply(numFunction, applyArgsUser);
            DoubleAttribute lowQuantityAttribute = new DoubleAttribute((Long) extensions.get(0));
            applyArgs.add(lowApplyUser);
            applyArgs.add(lowQuantityAttribute);
            Apply lowApply = new Apply(greaterFunction, applyArgs);
            applyCondArgs.add(lowApply);

            AttributeDesignator greatDesignator = new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET, designatorType, designatorId, false, null);
            greatApplyArgsUser.add(greatDesignator);
            Apply greatApplyUser = new Apply(numFunction, greatApplyArgsUser);
            DoubleAttribute greatQuantityAttribute = new DoubleAttribute((Long) extensions.get(1));
            greatApplyArgs.add(greatApplyUser);
            greatApplyArgs.add(greatQuantityAttribute);
            Apply applyCondition = new Apply(lessFunction, greatApplyArgs);
            applyCondArgs.add(applyCondition);
            Apply applyCond = new Apply(conditionFunction2, applyCondArgs);
            conditionArgs.add(applyCond);
        }
        Condition condition = new Condition(conditionFunction, conditionArgs);
        return condition;
    }
}
