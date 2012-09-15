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
public class SCGroupRuleAcceptAlg extends RuleCombiningAlgorithm {

    /**
     * The standard URN used to identify this algorithm
     */
    public static final String algId =
            "urn:oasis:names:tc:xacml:1.0:rule-combining-algorithm:"
            + "sc-rule-accept-group";
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
    public URI getIdentifier() {
        return identifierURI;
    }

    /**
     * Standard constructor.
     */
    public SCGroupRuleAcceptAlg() {
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
    public SCGroupRuleAcceptAlg(URI identifier) {
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
        boolean atLeastOneError = false;
        boolean potentialDeny = false;
        boolean atLeastOnePermit = false;
        Result firstIndeterminateResult = null;
        Iterator it = ruleElements.iterator();

        while (it.hasNext()) {
            Rule rule = ((RuleCombinerElement) (it.next())).getRule();
            Result result = rule.evaluate(context);
            int value = result.getDecision();
            // if there was a value of DENY, then regardless of what else
            // we've seen, we always return DENY
            if (value == Result.DECISION_DENY) {
                return result;
            }

            // if it was INDETERMINATE, then we couldn't figure something
            // out, so we keep track of these cases...
            if (value == Result.DECISION_INDETERMINATE) {
                atLeastOneError = true;

                // there are no rules about what to do if multiple cases
                // cause errors, so we'll just return the first one
                if (firstIndeterminateResult == null) {
                    firstIndeterminateResult = result;
                }

                // if the Rule's effect is DENY, then we can't let this
                // alg return PERMIT, since this Rule might have denied
                // if it could do its stuff
                if (rule.getEffect() == Result.DECISION_DENY) {
                    potentialDeny = true;
                }
            } else {
                // keep track of whether we had at least one rule that
                // actually pertained to the request
                if (value == Result.DECISION_PERMIT) {
                    atLeastOnePermit = true;
                }
            }
        }

        // we didn't explicitly DENY, but we might have had some Rule
        // been evaluated, so we have to return INDETERMINATE
        if (potentialDeny) {
            return firstIndeterminateResult;
        }

        // some Rule said PERMIT, so since nothing could have denied,
        // we return PERMIT
        if (atLeastOnePermit) {
            return new Result(Result.DECISION_PERMIT,
                    context.getResourceId().encode());
        }

        // we didn't find anything that said PERMIT, but if we had a
        // problem with one of the Rules, then we're INDETERMINATE
        if (atLeastOneError) {
            return firstIndeterminateResult;
        }

        // if we hit this point, then none of the rules actually applied
        // to us, so we return NOT_APPLICABLE
        return new Result(Result.DECISION_NOT_APPLICABLE,
                context.getResourceId().encode());
    }


}
