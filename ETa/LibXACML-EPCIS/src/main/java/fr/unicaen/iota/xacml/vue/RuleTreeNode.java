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
package fr.unicaen.iota.xacml.vue;

import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.policy.*;
import java.util.List;

public class RuleTreeNode {

    private List values;
    private OneOrGlobalFunction function;
    private String id;

    public String getId() {
        return id;
    }

    protected void setId(String id) {
        this.id = id;
    }

    public OneOrGlobalFunction getFunction() {
        return function;
    }

    public RuleTreeNode(List v, OneOrGlobalFunction f) {
        this.values = v;
        this.function = f;
    }

    @Override
    public String toString() {
        StringBuilder representation = new StringBuilder();
        if (SCBizStepRule.RULEFILTER.equals(id)) {
            representation.append("BizStep ");
        } else if (SCEpcsRule.RULEFILTER.equals(id)) {
            representation.append("Epc ");
        } else if (SCEventTimeRule.RULEFILTER.equals(id)) {
            representation.append("EventTime ");
        } else if (SCRecordTimeRule.RULEFILTER.equals(id)) {
            representation.append("RecordTime ");
        } else if (SCOperationRule.RULEFILTER.equals(id)) {
            representation.append("Operation ");
        } else if (SCEventTypeRule.RULEFILTER.equals(id)) {
            representation.append("EventType ");
        } else if (SCParentIdRule.RULEFILTER.equals(id)) {
            representation.append("ParentId ");
        } else if (SCChildEpcRule.RULEFILTER.equals(id)) {
            representation.append("ChildEpc ");
        } else if (SCQuantityRule.RULEFILTER.equals(id)) {
            representation.append("Quantity ");
        } else if (SCReadPointRule.RULEFILTER.equals(id)) {
            representation.append("ReadPoint ");
        } else if (SCBizLocRule.RULEFILTER.equals(id)) {
            representation.append("BizLoc ");
        } else if (SCDispositionRule.RULEFILTER.equals(id)) {
            representation.append("Disposition ");
        } else if (SCMasterDataIdRule.RULEFILTER.equals(id)) {
            representation.append("MasterDataId ");
        } else if (SCgroupRule.RULEFILTER.equals(id)) {
            representation.append("Users ");
        }
        if (OneOrGlobalFunction.NAME_GLOBAL_PERMIT.equals(function.getFunctionName())) {
            representation.append("(policy ACCEPT)");
        } else if (OneOrGlobalFunction.NAME_GLOBAL_DENY.equals(function.getFunctionName())) {
            representation.append("(policy DENY)");
        }
        return representation.toString();
    }
}
