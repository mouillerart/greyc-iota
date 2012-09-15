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
package fr.unicaen.iota.xacml;

import com.sun.xacml.BasicEvaluationCtx;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.ParsingException;
import com.sun.xacml.attr.AttributeValue;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.finder.*;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DSPDP {

    // the single attribute finder that can be used to find external values
    private AttributeFinder attributeFinder;
    // the single policy finder that will be used to resolve policies
    private PolicyFinder policyFinder;
    // the single resource finder that will be used to resolve resources
    private ResourceFinder resourceFinder;
    // the logger we'll use for all messages
    private static final Log log = LogFactory.getLog(DSPDP.class);

    public DSPDP(PDPConfig pDPConfig) {
        log.trace("Instanciate DSPDP");
        this.attributeFinder = pDPConfig.getAttributeFinder();
        this.policyFinder = pDPConfig.getPolicyFinder();
        this.policyFinder.init();
        this.resourceFinder = pDPConfig.getResourceFinder();
    }

    public AttributeFinder getAttributeFinder() {
        return attributeFinder;
    }

    public void setAttributeFinder(AttributeFinder attributeFinder) {
        this.attributeFinder = attributeFinder;
    }

    public PolicyFinder getPolicyFinder() {
        return policyFinder;
    }

    public void setPolicyFinder(PolicyFinder policyFinder) {
        this.policyFinder = policyFinder;
    }

    public ResourceFinder getResourceFinder() {
        return resourceFinder;
    }

    public void setResourceFinder(ResourceFinder resourceFinder) {
        this.resourceFinder = resourceFinder;
    }

    //@Override
    public ResponseCtx evaluate(RequestCtx request) {
        // try to create the EvaluationCtx out of the request
        try {
            return evaluate(new BasicEvaluationCtx(request, attributeFinder));
        } catch (ParsingException pe) {
            log.warn("the PDP receieved an invalid request", pe);

            // there was something wrong with the request, so we return
            // Indeterminate with a status of syntax error...though this
            // may change if a more appropriate status type exists
            List<String> code = new ArrayList<String>();
            code.add(Status.STATUS_SYNTAX_ERROR);
            Status status = new Status(code, pe.getMessage());
            return new ResponseCtx(new Result(Result.DECISION_INDETERMINATE, status));
        }
    }

    //@Override
    public ResponseCtx evaluate(EvaluationCtx context) {
        // see if we need to call the resource finder
        if (context.getScope() != EvaluationCtx.SCOPE_IMMEDIATE) {
            AttributeValue parent = context.getResourceId();
            ResourceFinderResult resourceResult;

            if (context.getScope() == EvaluationCtx.SCOPE_CHILDREN) {
                resourceResult =
                        resourceFinder.findChildResources(parent, context);
            } else {
                resourceResult =
                        resourceFinder.findDescendantResources(parent, context);
            }

            // see if we actually found anything
            if (resourceResult.isEmpty()) {
                // this is a problem, since we couldn't find any resources
                // to work on...the spec is not explicit about what kind of
                // error this is, so we're treating it as a processing error
                ArrayList code = new ArrayList();
                code.add(Status.STATUS_PROCESSING_ERROR);
                String msg = "Couldn't find any resources to work on.";

                return new ResponseCtx(new Result(Result.DECISION_INDETERMINATE,
                        new Status(code, msg),
                        context.getResourceId().encode()));
            }

            // setup a set to keep track of the results
            HashSet results = new HashSet();

            // at this point, we need to go through all the resources we
            // successfully found and start collecting results
            Iterator it = resourceResult.getResources().iterator();
            while (it.hasNext()) {
                // get the next resource, and set it in the EvaluationCtx
                AttributeValue resource = (AttributeValue) (it.next());
                context.setResourceId(resource);

                // do the evaluation, and set the resource in the result
                Result result = evaluateContext(context);
                log.trace("DSPDP evaluate: " + result.getDecision());
                result.setResource(resource.encode());

                // add the result
                results.add(result);
            }

            // now that we've done all the successes, we add all the failures
            // from the finder result
            Map failureMap = resourceResult.getFailures();
            it = failureMap.keySet().iterator();
            while (it.hasNext()) {
                // get the next resource, and use it to get its Status data
                AttributeValue resource = (AttributeValue) (it.next());
                Status status = (Status) (failureMap.get(resource));

                // add a new result
                results.add(new Result(Result.DECISION_INDETERMINATE,
                        status, resource.encode()));
            }

            // return the set of results
            return new ResponseCtx(results);
        } else {
            // the scope was IMMEDIATE (or missing), so we can just evaluate
            // the request and return whatever we get back
            return new ResponseCtx(evaluateContext(context));
        }
    }

    private Result evaluateContext(EvaluationCtx context) {
        // first off, try to find a policy
        PolicyFinderResult finderResult = policyFinder.findPolicy(context);

        // see if there weren't any applicable policies
        if (finderResult.notApplicable()) {
            return new Result(Result.DECISION_DENY,
                    context.getResourceId().encode());
        }

        if (finderResult.indeterminate()) {
            return new Result(Result.DECISION_DENY,
                    finderResult.getStatus(),
                    context.getResourceId().encode());
        }
        Result result = finderResult.getPolicy().evaluate(context);
        return result;
    }
}
