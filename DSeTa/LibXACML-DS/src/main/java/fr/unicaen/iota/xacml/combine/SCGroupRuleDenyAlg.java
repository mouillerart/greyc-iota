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
package fr.unicaen.iota.xacml.combine;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.Rule;
import com.sun.xacml.combine.RuleCombinerElement;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.ctx.Result;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;

/**
 * This is the standard SC Group rule combining algorithm. It
 * allows a single evaluation of not applicable or indeterminate results to take precedence over any number
 * of permit, deny. The result will be Deny.
 *
 * @since 1.0
 * @author Seth Proctor
 */
public class SCGroupRuleDenyAlg extends RuleCombiningAlgorithm {

    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId =
            "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:"
            + "sc-rule-deny-group";
    // a URI form of the identifier
    private static URI identifierURI;
    // exception if the URI was invalid, which should never be a problem
    private static RuntimeException earlyException;

    static {
        try {
            identifierURI = new URI(algId);
        } catch (URISyntaxException se) {
            earlyException = new IllegalArgumentException();
            earlyException.initCause(se);
        }
    }

    @Override
    public URI getIdentifier(){
        return identifierURI;
    }

    /**
     * Standard constructor.
     */
    public SCGroupRuleDenyAlg() {
        super(identifierURI);
        if (earlyException != null) {
            throw earlyException;
        }
    }

    /**
     * Protected constructor used by the ordered version of this algorithm.
     *
     * @param identifier the algorithm's identifier
     */
    public SCGroupRuleDenyAlg(URI identifier) {
        super(identifier);
    }

    /**
     * Applies the combining rule to the set of rules based on the
     * evaluation context.
     *
     * @param context the context from the request
     * @param parameters a (possibly empty) non-null <code>List</code> of
     *                   <code>CombinerParameter<code>s
     * @param ruleElements the rules to combine
     *
     * @return the result of running the combining algorithm
     */
    @Override
    public Result combine(EvaluationCtx context, List parameters, List ruleElements) {
        Iterator it = ruleElements.iterator();
        while (it.hasNext()) {
            Rule rule = ((RuleCombinerElement) (it.next())).getRule();
            Result result = rule.evaluate(context);
            int value = result.getDecision();
            // if there was a value of DENY, INDETERMINATE or NOT_APPLICABLE, then regardless of what else
            // we've seen, we always return DENY
            if (value == Result.DECISION_DENY || value == Result.DECISION_INDETERMINATE || value == Result.DECISION_NOT_APPLICABLE) {
                return new Result(Result.DECISION_DENY);
            }
        }
        return new Result(Result.DECISION_PERMIT);
    }
}
