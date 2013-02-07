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
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.DenyOverridesRuleAlg;
import com.sun.xacml.combine.RuleCombinerElement;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.cond.Condition;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.MyTargetFactory;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class DenyDefaultPolicy {

    private static final Log log = LogFactory.getLog(SCBizStepRule.class);

    private DenyDefaultPolicy() {
    }

    public static GroupPolicy getDenyDefaultRule() {

        URI policyId = null;
        try {
            policyId = new URI("DiscoveryServicesDenyDefaultRule");
        } catch (URISyntaxException ex) {
            Logger.getLogger(GroupPolicy.class.getName()).log(Level.SEVERE, null, ex);
        }
        String description = "Deny Default Policy";

        // Rule combining algorithm for the Policy
        URI combiningAlgId = null;
        try {
            combiningAlgId = new URI(DenyOverridesRuleAlg.algId);
        } catch (URISyntaxException ex) {
            Logger.getLogger(GroupPolicy.class.getName()).log(Level.SEVERE, null, ex);
        }
        CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
        RuleCombiningAlgorithm combiningAlg = null;
        try {
            combiningAlg = (RuleCombiningAlgorithm) (factory.createAlgorithm(combiningAlgId));
        } catch (UnknownIdentifierException ex) {
            Logger.getLogger(GroupPolicy.class.getName()).log(Level.SEVERE, null, ex);
        }
        // Create the target for the policy
        Target policyTarget = MyTargetFactory.getTargetInstance(null, null, null);;

        // Create the rules for the policy
        List ruleList = createRule();
        // Create the policy
        GroupPolicy policy = new GroupPolicy("default", "policy");
        policy.setTarget(policyTarget);
        policy.setChildren(ruleList);
        policy.setCombiningAlg(combiningAlg);
        return policy;

    }

    private static List createRule() {

        // Step 1: Define the identifier for the rule
        URI ruleId = null;
        try {
            ruleId = new URI("DenyDefaultPolicy");
        } catch (URISyntaxException ex) {
            Logger.getLogger(GroupPolicy.class.getName()).log(Level.SEVERE, null, ex);
        }
        String ruleDescription = "Rule for SC group access";
        // Step 2: Define the effect of the rule
        int effect = Result.DECISION_DENY;
        // Step 3: Get the target for the rule
        Target target = null;
        // Step 4: Get the condition for the rule
        Condition condition = null;
        // Step 5: Create the rule
        Rule openRule = new Rule(ruleId, effect, ruleDescription, target, condition);
        RuleCombinerElement combinerElement = new RuleCombinerElement(openRule);
        // Create a list for the rules and add the rule to it
        List ruleList = new ArrayList();
        ruleList.add(combinerElement);
        return ruleList;
    }

    public static void save() {
        GroupPolicy p = getDenyDefaultRule();
        try {
            p.encode(new FileOutputStream(new File("resources/policies/deny_default_policy.xml")));
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        }
    }

    public static void main(String[] args) {
        DenyDefaultPolicy.save();
    }
}
