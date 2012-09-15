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
package fr.unicaen.iota.xacml.vue;

import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.policy.SCBizStepRule;
import java.util.List;

/**
 *
 */
public class BizStepRuleTreeNode extends RuleTreeNode {

    public BizStepRuleTreeNode(List v, OneOrGlobalFunction f) {
        super(v, f);
        id = SCBizStepRule.RULEFILTER;
    }
}
