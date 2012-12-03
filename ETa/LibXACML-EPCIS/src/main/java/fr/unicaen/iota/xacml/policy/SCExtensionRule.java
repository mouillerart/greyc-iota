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

import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.cond.Condition;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.MyTargetFactory;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SCExtensionRule extends SCRule {

    private static final Log log = LogFactory.getLog(SCExtensionRule.class);
    private String id;
    private String ruleFilter;

    public SCExtensionRule(String ruleId, String extensionName, List values) {
        super(ruleId, values);
        description = "Extension filter for SC: " + this.name;
        ruleFilter = extensionName;
        id = ruleFilter;
    }

    public SCExtensionRule(String ruleId, String extensionName, List values, String actions) {
        super(ruleId, values, actions);
        description = "Extension filter for SC: " + this.name;
        ruleFilter = extensionName;
        id = ruleFilter;
    }

    public SCExtensionRule(String ruleId, String extensionName, List values, OneOrGlobalFunction extensionsFilterFunction) {
        super(ruleId, values, extensionsFilterFunction);
        description = "Extension filter for SC: " + this.name;
        ruleFilter = extensionName;
        id = ruleFilter;
    }

    public String getRuleFilter() {
        return ruleFilter;
    }

    public String getId() {
        return id;
    }

    @Override
    public Rule createRule() {
        // Step 1: Define the identifier for the rule
        URI ruleId = null;
        try {
            ruleId = new URI(ruleFilter);
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
        return null;
    }
}
