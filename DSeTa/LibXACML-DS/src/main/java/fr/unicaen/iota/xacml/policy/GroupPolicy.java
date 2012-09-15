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
/*
 * Derived from com.sun.xacml.AbstractPolicy
 */
/*
 * Copyright 2003-2005 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistribution of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 *
 *   2. Redistribution in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */
package fr.unicaen.iota.xacml.policy;

import com.sun.xacml.*;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.combine.*;
import com.sun.xacml.cond.*;
import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.xacml.MyTargetFactory;
import fr.unicaen.iota.xacml.combine.SCGroupRuleAlg;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.configuration.Configuration;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 */
public class GroupPolicy extends AbstractPolicy {

    private static final Log log = LogFactory.getLog(GroupPolicy.class);
    // atributes associated with this policy
    private Set definitions;
    private URI idAttr;
    private String version;
    private CombiningAlgorithm combiningAlg;
    private String description;
    private Target target;
    private String defaultVersion;
    private PolicyMetaData metaData;
    private List children;
    private List childElements;
    private Set obligations;
    private List parameters;
    private Module module;
    private String name;
    private String owner;
    //############ FILTERS #############
    private List users;
    private List bizSteps;
    private List epcs;
    private List epcClasses;
    private List eventTimes;
    private OneOrGlobalFunction usersFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY);
    private OneOrGlobalFunction bizStepsFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
    private OneOrGlobalFunction epcsFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
    private OneOrGlobalFunction epcClassesFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
    private OneOrGlobalFunction eventTimesFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
    private List actions;

    public GroupPolicy(String name, String owner) {
        this.name = name;
        this.owner = owner;
        this.users = new ArrayList();
        this.bizSteps = new ArrayList();
        this.epcs = new ArrayList();
        this.epcClasses = new ArrayList();
        this.eventTimes = new ArrayList();
        this.obligations = new HashSet();
        this.combiningAlg = new SCGroupRuleAlg();
        this.actions = new ArrayList();
        try {
            idAttr = new URI(this.name);
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
    }

    protected GroupPolicy(URI id, String version,
            CombiningAlgorithm combiningAlg,
            String description, Target target) {
        this(id, version, combiningAlg, description, target, null);
    }

    protected GroupPolicy(URI id, String version,
            CombiningAlgorithm combiningAlg,
            String description, Target target,
            String defaultVersion) {
        this(id, version, combiningAlg, description, target, defaultVersion,
                null, null);
    }

    protected GroupPolicy(URI id, String version,
            CombiningAlgorithm combiningAlg,
            String description, Target target,
            String defaultVersion, Set obligations,
            List parameters) {
        idAttr = id;
        this.combiningAlg = combiningAlg;
        this.description = description;
        this.target = target;
        this.defaultVersion = defaultVersion;

        if (version == null) {
            this.version = "1.0";
        } else {
            this.version = version;
        }

        // FIXME: this needs to fill in the meta-data correctly
        metaData = null;

        if (obligations == null) {
            this.obligations = Collections.EMPTY_SET;
        } else {
            this.obligations = Collections.unmodifiableSet(new HashSet(obligations));
        }

        if (parameters == null) {
            this.parameters = Collections.EMPTY_LIST;
        } else {
            this.parameters = Collections.unmodifiableList(new ArrayList(parameters));
        }
    }

    protected GroupPolicy(Node root, String policyPrefix,
            String combiningName) throws ParsingException {
        // get the attributes, all of which are common to Policies
        NamedNodeMap attrs = root.getAttributes();

        try {
            // get the attribute Id
            idAttr = new URI(attrs.getNamedItem(policyPrefix + "Id").
                    getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + policyPrefix + "Id", e);
        }

        // see if there's a version
        Node versionNode = attrs.getNamedItem("Version");
        if (versionNode != null) {
            version = versionNode.getNodeValue();
        } else {
            // assign the default version
            version = "1.0";
        }

        // now get the combining algorithm...
        try {
            URI algId = new URI(attrs.getNamedItem(combiningName).
                    getNodeValue());
            CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
            combiningAlg = factory.createAlgorithm(algId);
        } catch (Exception e) {
            throw new ParsingException("Error parsing combining algorithm"
                    + " in " + policyPrefix, e);
        }

        // ...and make sure it's the right kind
        if (policyPrefix.equals("Policy")) {
            if (!(combiningAlg instanceof RuleCombiningAlgorithm)) {
                throw new ParsingException("Policy must use a Rule "
                        + "Combining Algorithm");
            }
        } else {
            if (!(combiningAlg instanceof PolicyCombiningAlgorithm)) {
                throw new ParsingException("GroupPolicy must use a Policy "
                        + "Combining Algorithm");
            }
        }

        // do an initial pass through the elements to pull out the
        // defaults, if any, so we can setup the meta-data
        NodeList childrenTmp = root.getChildNodes();
        String xpathVersion = null;

        for (int i = 0; i < childrenTmp.getLength(); i++) {
            Node child = childrenTmp.item(i);
            if (child.getNodeName().equals(policyPrefix + "Defaults")) {
                handleDefaults(child);
            }
        }

        // with the defaults read, create the meta-data
        metaData = new PolicyMetaData(root.getNamespaceURI(), defaultVersion);

        // now read the remaining policy elements
        obligations = new HashSet();
        parameters = new ArrayList();
        childrenTmp = root.getChildNodes();

        for (int i = 0; i < childrenTmp.getLength(); i++) {
            Node child = childrenTmp.item(i);
            String cname = child.getNodeName();

            if (cname.equals("Description")) {
                if (child.hasChildNodes()) {
                    description = child.getFirstChild().getNodeValue();
                }
            } else if (cname.equals("Target")) {
                target = Target.getInstance(child, metaData);
            } else if (cname.equals("Obligations")) {
                parseObligations(child);
            } else if (cname.equals("CombinerParameters")) {
                handleParameters(child);
            }
        }

        // finally, make sure the obligations and parameters are immutable
        obligations = Collections.unmodifiableSet(obligations);
        parameters = Collections.unmodifiableList(parameters);
    }

    private GroupPolicy(Node root) throws ParsingException {
        this(root, "Policy", "RuleCombiningAlgId");
        NamedNodeMap attrs = root.getAttributes();
        try {
            // get the attribute Id
            idAttr = new URI(attrs.getNamedItem("PolicyId").getNodeValue());
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "PolicyId", e);
        }
        this.name = idAttr.toString();

        // now get the combining algorithm...
        try {
            URI algId = new URI(attrs.getNamedItem("RuleCombiningAlgId").
                    getNodeValue());
            CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
            combiningAlg = (RuleCombiningAlgorithm) factory.createAlgorithm(algId);
        } catch (Exception e) {
            throw new ParsingException("Error parsing combining algorithm"
                    + " in " + "PolicyCombiningAlgId", e);
        }

        if (!(combiningAlg instanceof RuleCombiningAlgorithm)) {
            throw new ParsingException("PolicySet must use a Policy "
                    + "Combining Algorithm");
        }

        List rules = new ArrayList();
        HashMap parametersMap = new HashMap();
        HashMap variableIds = new HashMap();
        PolicyMetaData metaDataTmp = getMetaData();

        // first off, go through and look for any definitions to get their
        // identifiers up front, since before we parse any references we'll
        // need to know what definitions we support

        NodeList childrenTmp = root.getChildNodes();
        for (int i = 0; i
                < childrenTmp.getLength(); i++) {
            Node child = childrenTmp.item(i);

            if (child.getNodeName().equals("VariableDefinition")) {
                String id = child.getAttributes().
                        getNamedItem("VariableId").getNodeValue();
                // it's an error to have more than one definition with the
                // same identifier
                if (variableIds.containsKey(id)) {
                    throw new ParsingException("multiple definitions for "
                            + "variable " + id);
                }
                variableIds.put(id, child);
            }
        }

        // now create a manager with the defined variable identifiers
        VariableManager manager = new VariableManager(variableIds, metaDataTmp);
        definitions = new HashSet();

        parameters = new ArrayList();

        // next, collect the Policy-specific elements
        for (int i = 0; i
                < childrenTmp.getLength(); i++) {
            Node child = childrenTmp.item(i);
            String nameTmp = child.getNodeName();
            if (nameTmp.equals("Rule")) {
                rules.add(Rule.getInstance(child, metaDataTmp, manager));
            } else if (nameTmp.equals("RuleCombinerParameters")) {
                String ref = child.getAttributes().getNamedItem("RuleIdRef").
                        getNodeValue();

                // if we found the parameter before than add it the end of
                // the previous paramters, otherwise create a new entry

                if (parametersMap.containsKey(ref)) {
                    List list = (List) (parametersMap.get(ref));
                    parseParameters(
                            list, child);
                } else {
                    List list = new ArrayList();
                    parseParameters(
                            list, child);
                    parametersMap.put(ref, list);
                }
            } else if (nameTmp.equals("VariableDefinition")) {
                String id = child.getAttributes().
                        getNamedItem("VariableId").getNodeValue();
                definitions.add(manager.getDefinition(id));
            } else if (nameTmp.equals("Target")) {
                target = Target.getInstance(child, metaDataTmp);
            } else if (nameTmp.equals("CombinerParameters")) {
                handleParameters(child);
            }
        }

        definitions = Collections.unmodifiableSet(definitions);

        List elements = new ArrayList();
        Iterator it = rules.iterator();
        this.actions = new ArrayList();
        this.initFilters(rules);
        initTarget(target);
        while (it.hasNext()) {
            Rule rule = (Rule) (it.next());
            String id = rule.getId().toString();
            List list = (List) (parametersMap.remove(id));
            elements.add(new RuleCombinerElement(rule, list));
            //elements.add(rule);
        }
        if (!parametersMap.isEmpty()) {
            throw new ParsingException("Unmatched parameters in Rule");
        }
        setChildren(elements);
        this.parameters = Collections.unmodifiableList(parameters);
    }

    private void handleDefaults(Node root) throws ParsingException {
        defaultVersion = null;
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("XPathVersion")) {
                defaultVersion = node.getFirstChild().getNodeValue();
            }
        }
    }

    private void parseObligations(Node root) throws ParsingException {
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("Obligation")) {
                obligations.add(Obligation.getInstance(node));
            }
        }
    }

    @Override
    public Result evaluate(EvaluationCtx context) {
        Result result = combiningAlg.combine(context, parameters,
                childElements);
        if (obligations.isEmpty()) {
            return result;
        }
        int effect = result.getDecision();
        if ((effect == Result.DECISION_INDETERMINATE)
                || (effect == Result.DECISION_NOT_APPLICABLE)) {
            return result;
        }
        Iterator it = obligations.iterator();
        while (it.hasNext()) {
            Obligation obligation = (Obligation) (it.next());
            if (obligation.getFulfillOn() == effect) {
                result.addObligation(obligation);
            }
        }
        return result;
    }

    public void createPolicy() {
        this.description = "This AccessPolicy applies to users in SCgroup " + this.name + " to access to any events of the user: " + this.owner;
        // Rule combining algorithm for the Policy
        URI combiningAlgId = combiningAlg.getIdentifier();
        CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
        try {
            setCombiningAlg((RuleCombiningAlgorithm) (factory.createAlgorithm(combiningAlgId)));
        } catch (UnknownIdentifierException ex) {
            log.fatal(null, ex);
        }
        // Create the target for the policy
        Target policyTarget = createTarget();
        setTarget(policyTarget);
        // Create the rules for the policy
        List ruleList = new ArrayList();
        if (this.users != null) {
            SCgroupRule sCgroupRule;
            sCgroupRule = new SCgroupRule(name, users, usersFilterFunction);
            Rule rule = sCgroupRule.createRule();
            RuleCombinerElement ruleCombinerElement = new RuleCombinerElement(rule);
            ruleList.add(ruleCombinerElement);
        }

        if (!module.equals(Module.administrationModule)) {
            if (this.bizSteps != null) {
                SCBizStepRule sCBizStepRule = new SCBizStepRule(name, bizSteps, bizStepsFilterFunction);
                Rule rule = sCBizStepRule.createRule();
                RuleCombinerElement ruleCombinerElement = new RuleCombinerElement(rule);
                ruleList.add(ruleCombinerElement);
            }

            if (this.epcs != null) {
                SCEPCsRule sCepcsRule = new SCEPCsRule(name, epcs, epcsFilterFunction);
                Rule rule = sCepcsRule.createRule();
                RuleCombinerElement ruleCombinerElement = new RuleCombinerElement(rule);
                ruleList.add(ruleCombinerElement);
            }
            if (this.epcClasses != null) {
                SCEPCClassRule sCEpcClassesRule = new SCEPCClassRule(name, epcClasses, epcClassesFilterFunction);
                Rule rule = sCEpcClassesRule.createRule();
                RuleCombinerElement ruleCombinerElement = new RuleCombinerElement(rule);
                ruleList.add(ruleCombinerElement);
            }
            if (this.eventTimes != null) {
                SCEventTimeRule sCEventTimesRule;
                try {
                    sCEventTimesRule = new SCEventTimeRule(name, eventTimes, eventTimesFilterFunction);
                    Rule rule = sCEventTimesRule.createRule();
                    RuleCombinerElement ruleCombinerElement = new RuleCombinerElement(rule);
                    ruleList.add(ruleCombinerElement);
                } catch (Exception ex) {
                    log.fatal(null, ex);
                }
            }
        }
        setChildren(ruleList);

    }

    public void save() {
        createPolicy();
        try {
            String filename = Configuration.QUERY_POLICIES_DIRECTORY + this.owner + this.name + ".xml";
            encode(new FileOutputStream(new File(filename)));
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        }
    }

    public void initFilters(List rules) {
        Iterator it = rules.iterator();
        while (it.hasNext()) {
            Rule rule = (Rule) (it.next());
            String ruleid = rule.getId().toString();
            OneOrGlobalFunction function = (OneOrGlobalFunction) rule.getCondition().getFunction();
            if (SCBizStepRule.RULEFILTER.equals(ruleid)) {
                bizSteps = getFiltersValues(rule);
                bizStepsFilterFunction = function;
            } else if (SCEPCClassRule.RULEFILTER.equals(ruleid)) {
                epcClasses = getFiltersValues(rule);
                epcClassesFilterFunction = function;
            } else if (SCEPCsRule.RULEFILTER.equals(ruleid)) {
                epcs = getFiltersValues(rule);
                epcsFilterFunction = function;
            } else if (SCEventTimeRule.RULEFILTER.equals(ruleid)) {
                eventTimes = getFiltersValues(rule);
                eventTimesFilterFunction = function;
            } else if (SCgroupRule.RULEFILTER.equals(ruleid)) {
                users = getFiltersValues(rule);
                usersFilterFunction = function;
            }
        }
    }

    public List getFiltersValues(Rule rule) {
        List res = new ArrayList();
        Condition condition = rule.getCondition();
        List conditionChildren = condition.getChildren();
        for (Object object : conditionChildren) {
            if (object instanceof Apply) {
                List dateTimes = new ArrayList();
                Apply apply = (Apply) object;
                List applyChildren = apply.getChildren();
                for (Object o2 : applyChildren) {
                    if (o2 instanceof StringAttribute) {
                        StringAttribute sa = (StringAttribute) o2;
                        res.add(sa.getValue());
                        continue;
                    }
                    if (o2 instanceof Apply) {
                        Apply apply2 = (Apply) o2;
                        List apply2Children = apply2.getChildren();
                        for (Object o3 : apply2Children) {
                            if (o3 instanceof DateTimeAttribute) {
                                DateTimeAttribute sa = (DateTimeAttribute) o3;
                                dateTimes.add(sa.getValue());
                            }
                        }
                    }
                }
                if (dateTimes.size() == 2) {
                    ArrayList tmp = new ArrayList();
                    tmp.addAll(dateTimes);
                    res.add(tmp);
                    dateTimes.clear();
                }
            }
        }
        return res;
    }

    private void handleParameters(Node root) throws ParsingException {
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i
                < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("CombinerParameter")) {
                parameters.add(CombinerParameter.getInstance(node));
            }
        }
    }

    private void parseParameters(List parameters, Node root)
            throws ParsingException {
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i
                < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("CombinerParameter")) {
                parameters.add(CombinerParameter.getInstance(node));
            }
        }
    }

    public static GroupPolicy getInstance(Node root) throws ParsingException {
        if (!root.getNodeName().equals("Policy")) {
            throw new ParsingException("Cannot create Policy from root of "
                    + "type " + root.getNodeName());
        }
        return new GroupPolicy(root);
    }

    @Override
    public void encode(OutputStream output) {
        encode(output, new Indenter(0));
    }

    @Override
    public void encode(OutputStream output, Indenter indenter) {
        PrintStream out = new PrintStream(output);
        String indent = indenter.makeString();
        out.println(indent + "<Policy PolicyId=\"" + getId().toString() + "\" RuleCombiningAlgId=\""
                + getCombiningAlg().getIdentifier().toString() + "\">");
        indenter.in();
        String nextIndent = indenter.makeString();
        String descriptionTmp = getDescription();
        if (descriptionTmp != null) {
            out.println(nextIndent + "<Description>" + descriptionTmp
                    + "</Description>");
        }
        String versionTmp = getDefaultVersion();
        if (versionTmp != null) {
            out.println("<PolicyDefaults><XPathVersion>" + versionTmp
                    + "</XPathVersion></PolicyDefaults>");
        }
        getTarget().encode(output, indenter);
        if (definitions != null) {
            Iterator it = definitions.iterator();
            while (it.hasNext()) {
                ((VariableDefinition) (it.next())).encode(output, indenter);
            }
        }
        encodeCommonElements(output, indenter);
        indenter.out();
        out.println(indent + "</Policy>");
    }

    public static void main(String[] args) {
        GroupPolicy gp = new GroupPolicy("tata", "toto");
        gp.save();
    }

    @Override
    public MatchResult match(EvaluationCtx context) {
        return target.match(context);
    }

    @Override
    protected void setChildren(List children) {
        if (children == null) {
            this.children = Collections.EMPTY_LIST;
        } else {
            List list = new ArrayList();
            Iterator it = children.iterator();

            while (it.hasNext()) {
                CombinerElement element = (CombinerElement) (it.next());
                list.add(element.getElement());
            }
            this.children = Collections.unmodifiableList(list);
            childElements = Collections.unmodifiableList(children);
        }
    }

    @Override
    protected void encodeCommonElements(OutputStream output,
            Indenter indenter) {
        Iterator it = childElements.iterator();
        while (it.hasNext()) {
            ((CombinerElement) (it.next())).encode(output, indenter);
        }
        if (obligations != null && !obligations.isEmpty()) {
            PrintStream out = new PrintStream(output);
            String indent = indenter.makeString();
            out.println(indent + "<Obligations>");
            indenter.in();
            it = obligations.iterator();
            while (it.hasNext()) {
                ((Obligation) (it.next())).encode(output, indenter);
            }
            out.println(indent + "</Obligations>");
            indenter.out();
        }
    }

    private Target createTarget() {
        if (this.actions == null || this.actions.isEmpty()) {
            return MyTargetFactory.getTargetInstance(null, null, null);
        }
        List actionsList = new ArrayList();
        for (Object o : this.actions) {
            String action = (String) o;
            List actionList = new ArrayList();
            URI actionDesignatorType = null;
            URI actionDesignatorId = null;
            try {
                actionDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
                actionDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");
            } catch (URISyntaxException ex) {
                log.fatal(null, ex);
            }
            AttributeDesignator actionDesignator = new AttributeDesignator(AttributeDesignator.ACTION_TARGET,
                    actionDesignatorType, actionDesignatorId, false);
            StringAttribute actionValue = new StringAttribute(action);
            FunctionFactory factory = FunctionFactory.getTargetInstance();
            String actionMatchId = "urn:oasis:names:tc:xacml:1.0:function:string-equal";
            Function actionFunction = null;
            try {
                actionFunction = factory.createFunction(actionMatchId);
            } catch (UnknownIdentifierException ex) {
                log.fatal(null, ex);
            } catch (FunctionTypeException ex) {
                log.fatal(null, ex);
            }
            TargetMatch actionMatch = new TargetMatch(TargetMatch.ACTION, actionFunction, actionDesignator, actionValue);
            actionList.add(actionMatch);
            TargetMatchGroup actionMatchGroup = new TargetMatchGroup(actionList, TargetMatch.ACTION);
            actionsList.add(actionMatchGroup);
        }
        return MyTargetFactory.getTargetInstance(null, null, actionsList);
    }

    private void initTarget(Target target) {
        TargetSection actionSection = target.getActionsSection();
        List groups = actionSection.getMatchGroups();
        for (Object o : groups) {
            TargetMatchGroup targetMatchGroup = (TargetMatchGroup) o;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            targetMatchGroup.encode(baos);
            String targetMatchGroupStr;
            try {
                targetMatchGroupStr = new String(baos.toByteArray(), "UTF-8");
                String[] tab = targetMatchGroupStr.split("\n");
                targetMatchGroupStr = tab[2];
                targetMatchGroupStr = targetMatchGroupStr.replace("<AttributeValue DataType=\"http://www.w3.org/2001/XMLSchema#string\">", "");
                targetMatchGroupStr = targetMatchGroupStr.replace("</AttributeValue>", "");
                this.actions.add(targetMatchGroupStr);
            } catch (UnsupportedEncodingException ex) {
                log.fatal(null, ex);
            }
        }
    }

    @Override
    public String toString() {
        return "Group = " + this.idAttr.toString();
    }

    /*
     * ###########################################* ######### ACCESS POLICY
     * MANAGER ###########* ###########################################
     */
    public boolean addAction(String action) {
        if (actions.contains(action)) {
            return false;
        }
        return actions.add(action);
    }

    public boolean removeAction(String action) {
        return actions.remove(action);
    }

    public boolean addUserFilter(String userName) {
        if (users.contains(userName)) {
            return false;
        }
        return users.add(userName);
    }

    public boolean addBizStepFilter(String bizStep) {
        if (bizSteps.contains(bizStep)) {
            return false;
        }
        return bizSteps.add(bizStep);
    }

    public boolean addEPCFilter(String epc) {
        if (epcs.contains(epc)) {
            return false;
        }
        return epcs.add(epc);
    }

    public boolean addEpcClassFilter(String epcClass) {
        if (epcClasses.contains(epcClass)) {
            return false;
        }
        return epcClasses.add(epcClass);
    }

    public boolean addEventTimeFilter(List dates) {
        if (eventTimes.contains(dates)) {
            return false;
        }
        return eventTimes.add(dates);
    }

    public boolean removeUserFilter(String userName) {
        return users.remove(userName);
    }

    public boolean removeBizStepFilter(String bizStep) {
        return bizSteps.remove(bizStep);
    }

    public boolean removeEPCFilter(String epc) {
        return epcs.remove(epc);
    }

    public boolean removeEpcClassFilter(String epcClass) {
        return epcClasses.remove(epcClass);
    }

    public boolean removeEventTimeFilter(List dates) {
        return (eventTimes.remove(dates));
    }

    public boolean updateUserFilter(String newUserName, String oldUserName) {
        int i = users.indexOf(oldUserName);
        users.remove(oldUserName);
        users.add(i, newUserName);
        return true;
    }

    public boolean updateBizStepFilter(String newBizStepName, String oldBizStepName) {
        int i = bizSteps.indexOf(oldBizStepName);
        bizSteps.remove(oldBizStepName);
        bizSteps.add(i, newBizStepName);
        return true;
    }

    public boolean updateEPCFilter(String newEpcName, String oldEpcName) {
        int i = epcs.indexOf(oldEpcName);
        epcs.remove(oldEpcName);
        epcs.add(i, newEpcName);
        return true;
    }

    public boolean updateEpcClassFilter(String newEpcClassName, String oldEpcClassName) {
        int i = epcClasses.indexOf(oldEpcClassName);
        epcClasses.remove(oldEpcClassName);
        epcClasses.add(i, newEpcClassName);
        return true;
    }

    public boolean updateEventTimeFilter(Date newLowDate, Date newHighDate, Date oldLowDate, Date oldHighDate) {
        int i = eventTimes.indexOf(oldLowDate);
        epcClasses.remove(oldLowDate);
        epcClasses.add(i, newLowDate);
        i = eventTimes.indexOf(oldHighDate);
        epcClasses.remove(oldHighDate);
        epcClasses.add(i, newHighDate);
        return true;
    }

    public boolean switchUsersFunction() {
        if (OneOrGlobalFunction.NAME_GLOBAL_PERMIT.equals(usersFilterFunction.getFunctionName())) {
            usersFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY);
            return true;
        }
        if (OneOrGlobalFunction.NAME_GLOBAL_DENY.equals(usersFilterFunction.getFunctionName())) {
            usersFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
            return true;
        }
        return false;
    }

    public boolean switchBizStepsFunction() {
        if (OneOrGlobalFunction.NAME_GLOBAL_PERMIT.equals(bizStepsFilterFunction.getFunctionName())) {
            bizStepsFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY);
            return true;
        }
        if (OneOrGlobalFunction.NAME_GLOBAL_DENY.equals(bizStepsFilterFunction.getFunctionName())) {
            bizStepsFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
            return true;
        }
        return false;
    }

    public boolean switchEpcsFunction() {
        if (OneOrGlobalFunction.NAME_GLOBAL_PERMIT.equals(epcsFilterFunction.getFunctionName())) {
            epcsFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY);
            return true;
        }
        if (OneOrGlobalFunction.NAME_GLOBAL_DENY.equals(epcsFilterFunction.getFunctionName())) {
            epcsFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
            return true;
        }
        return false;
    }

    public boolean switchEpcClassesFunction() {
        if (OneOrGlobalFunction.NAME_GLOBAL_PERMIT.equals(epcClassesFilterFunction.getFunctionName())) {
            epcClassesFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY);
            return true;
        }
        if (OneOrGlobalFunction.NAME_GLOBAL_DENY.equals(epcClassesFilterFunction.getFunctionName())) {
            epcClassesFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
            return true;
        }
        return false;
    }

    public boolean switchEventTimesFunction() {
        if (OneOrGlobalFunction.NAME_GLOBAL_PERMIT.equals(eventTimesFilterFunction.getFunctionName())) {
            eventTimesFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY);
            return true;
        }
        if (OneOrGlobalFunction.NAME_GLOBAL_DENY.equals(eventTimesFilterFunction.getFunctionName())) {
            eventTimesFilterFunction = new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT);
            return true;
        }
        return false;
    }

    /*
     * ###########################################* ########### GETTER AND
     * SETTER #############* ###########################################
     */
    @Override
    public List getCombiningParameters() {
        return parameters;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getDefaultVersion() {
        return defaultVersion;
    }

    @Override
    public List getChildElements() {
        return childElements;
    }

    @Override
    public Set getObligations() {
        return obligations;
    }

    @Override
    public PolicyMetaData getMetaData() {
        return metaData;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public void setTarget(Target policyTarget) {
        this.target = policyTarget;
    }

    @Override
    public List getChildren() {
        return children;
    }

    public Set getVariableDefinitions() {
        return definitions;
    }

    @Override
    public CombiningAlgorithm getCombiningAlg() {
        return combiningAlg;
    }

    public Module getType() {
        return module;
    }

    public void setType(Module type) {
        this.module = type;
    }

    public OneOrGlobalFunction getBizStepsFilterFunction() {
        return bizStepsFilterFunction;
    }

    public void setBizStepsFilterFunction(OneOrGlobalFunction bizStepsFilterFunction) {
        this.bizStepsFilterFunction = bizStepsFilterFunction;
    }

    public OneOrGlobalFunction getEpcClassesFilterFunction() {
        return epcClassesFilterFunction;
    }

    public void setEpcClassesFilterFunction(OneOrGlobalFunction epcClassesFilterFunction) {
        this.epcClassesFilterFunction = epcClassesFilterFunction;
    }

    public OneOrGlobalFunction getEpcsFilterFunction() {
        return epcsFilterFunction;
    }

    public void setEpcsFilterFunction(OneOrGlobalFunction epcsFilterFunction) {
        this.epcsFilterFunction = epcsFilterFunction;
    }

    public OneOrGlobalFunction getEventTimesFilterFunction() {
        return eventTimesFilterFunction;
    }

    public void setEventTimesFilterFunction(OneOrGlobalFunction eventTimesFilterFunction) {
        this.eventTimesFilterFunction = eventTimesFilterFunction;
    }

    public OneOrGlobalFunction getUsersFilterFunction() {
        return usersFilterFunction;
    }

    public void setUsersFilterFunction(OneOrGlobalFunction usersFilterFunction) {
        this.usersFilterFunction = usersFilterFunction;
    }

    public void setCombiningAlg(CombiningAlgorithm c) {
        combiningAlg = c;
    }

    public void setActions(List a) {
        this.actions = a;
    }

    public List getActions() {
        return actions;
    }

    @Override
    public Target getTarget() {
        return target;
    }

    @Override
    public URI getId() {
        return idAttr;
    }

    public void setId(URI policyId) {
        this.idAttr = policyId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List getBizSteps() {
        return bizSteps;
    }

    public void setBizSteps(List bizSteps) {
        this.bizSteps = bizSteps;
    }

    public List getEpcClasses() {
        return epcClasses;
    }

    public void setEpcClasses(List epcClasses) {
        this.epcClasses = epcClasses;
    }

    public List getEpcs() {
        return epcs;
    }

    public void setEpcs(List epcs) {
        this.epcs = epcs;
    }

    public List getEventTimes() {
        return eventTimes;
    }

    public void setEventTimes(List eventTimes) {
        this.eventTimes = eventTimes;
    }

    public List getUsers() {
        return users;
    }

    public void setUsers(List users) {
        this.users = users;
    }
}
