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
package fr.unicaen.iota.xacml.vue;

import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.policy.SCDispositionRule;
import java.util.List;

public class DispositionRuleTreeNode extends RuleTreeNode {

    public DispositionRuleTreeNode(List v, OneOrGlobalFunction f) {
        super(v, f);
        setId(SCDispositionRule.RULEFILTER);
    }
}
