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
package fr.unicaen.iota.xacml.policy;

import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.cond.Condition;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import java.util.List;

public abstract class SCRule {

    protected String name;
    protected List values;
    protected String action;
    protected String description;
    protected OneOrGlobalFunction globalFunction;

    public SCRule(String ruleId, List values) {
        this.name = ruleId;
        this.values = values;
        this.action = null;
        this.globalFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
    }

    public SCRule(String ruleId, List values, OneOrGlobalFunction f) {
        this.name = ruleId;
        this.values = values;
        this.action = null;
        this.globalFunction = f;
    }

    public SCRule(String ruleId, List values, String action) {
        this.name = ruleId;
        this.values = values;
        this.action = action;
        this.globalFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
    }

    public SCRule(String ruleId, List values, String action, OneOrGlobalFunction function) {
        this.name = ruleId;
        this.values = values;
        this.action = action;
        this.globalFunction = function;
    }

    public OneOrGlobalFunction getGlogalFunction() {
        return globalFunction;
    }

    public void setGlogalFunction(OneOrGlobalFunction fct) {
        this.globalFunction = fct;
    }

    public void changeGlobalFunction() {
        if (OneOrGlobalFunction.getId(globalFunction.getFunctionName()) == OneOrGlobalFunction.ID_GLOBAL_DENY) {
            this.setGlogalFunction(new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT));
        } else {
            this.setGlogalFunction(new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY));
        }
    }

    abstract protected String getId();
    
    abstract public Rule createRule();

    abstract protected Target createTarget();

    abstract protected Condition createCondition();
}
