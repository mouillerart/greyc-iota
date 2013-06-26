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

import com.sun.xacml.*;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.combine.*;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionTypeException;
import com.sun.xacml.ctx.Result;
import com.sun.xacml.finder.PolicyFinder;
import fr.unicaen.iota.xacml.AccessPolicyManager;
import fr.unicaen.iota.xacml.MyTargetFactory;
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
public class OwnerPolicies extends AbstractPolicy {

    // TODO SLS gros merdier: attributs doublons de ceux de super!
    private Module module;
    private String owner;
    private List policies;
    private String filePath;
    private URI idAttr;
    private Target target;
    private List childElements;
    private List parameters;
    private List children;
    private PolicyMetaData metaData;
    private String defaultVersion;
    private static final Log log = LogFactory.getLog(OwnerPolicies.class);

    public OwnerPolicies(String owner, Module module) {
        this.owner = owner;
        this.policies = new ArrayList();
        this.combiningAlg = new PermitOverridesPolicyAlg();
        this.module = module;
        this.target = createTarget();
        this.metaData = new PolicyMetaData(PolicyMetaData.XACML_1_0_IDENTIFIER, PolicyMetaData.XPATH_1_0_IDENTIFIER);
        try {
            idAttr = new URI(this.owner);
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
    }

    private OwnerPolicies(Node root, PolicyFinder finder) throws ParsingException {
        super(root, "PolicySet", "PolicyCombiningAlgId");

        List policiesList = new ArrayList();
        HashMap policyParameters = new HashMap();
        HashMap policySetParameters = new HashMap();
        metaData = new PolicyMetaData(root.getNamespaceURI(), PolicyMetaData.XPATH_1_0_IDENTIFIER);
        NamedNodeMap attrs = root.getAttributes();
        owner = attrs.getNamedItem("PolicySetId").getNodeValue();
        try {
            // get the attribute Id
            idAttr = new URI(owner);
        } catch (Exception e) {
            throw new ParsingException("Error parsing required attribute "
                    + "PolicySetId", e);
        }

        // now get the combining algorithm...
        try {
            URI algId = new URI(attrs.getNamedItem("PolicyCombiningAlgId").
                    getNodeValue());
            CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
            combiningAlg = (PolicyCombiningAlgorithm) factory.createAlgorithm(algId);
        } catch (Exception e) {
            throw new ParsingException("Error parsing combining algorithm"
                    + " in " + "PolicyCombiningAlgId", e);
        }

        if (!(combiningAlg instanceof PolicyCombiningAlgorithm)) {
            throw new ParsingException("PolicySet must use a Policy "
                    + "Combining Algorithm");
        }

        parameters = new ArrayList();

        // collect the PolicySet-specific elements
        NodeList rootChildren = root.getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); i++) {
            Node child = rootChildren.item(i);
            String name = child.getNodeName();
            if (name.equals("PolicySet")) {
                OwnerPolicies ownerPolicies = OwnerPolicies.getInstance(child, finder);
                policiesList.add(ownerPolicies);
            } else if (name.equals("Policy")) {
                GroupPolicy groupPolicy = GroupPolicy.getInstance(child);
                policiesList.add(groupPolicy);
            } else if (name.equals("PolicySetIdReference")) {
                policiesList.add(PolicyReference.getInstance(child, finder, metaData));
            } else if (name.equals("PolicyIdReference")) {
                policiesList.add(PolicyReference.getInstance(child, finder, metaData));
            } else if (name.equals("PolicyCombinerParameters")) {
                paramaterHelper(policyParameters, child, "Policy");
            } else if (name.equals("PolicySetCombinerParameters")) {
                paramaterHelper(policySetParameters, child, "PolicySet");
            } else if (name.equals("Target")) {
                target = Target.getInstance(child, metaData);
            } else if (name.equals("CombinerParameters")) {
                handleParameters(child);
            }
        }
        // now make sure that we can match up any parameters we may have
        // found to a cooresponding Policy or PolicySet...
        List elements = new ArrayList();
        Iterator it = policiesList.iterator();

        // right now we have to go though each policy and based on several
        // possible cases figure out what paranmeters might apply...but
        // there should be a better way to do this

        while (it.hasNext()) {
            AbstractPolicy policy = (AbstractPolicy) (it.next());
            List list;
            if (policy instanceof GroupPolicy) {
                list = (List) (policyParameters.remove(policy.getId().
                        toString()));
            } else if (policy instanceof OwnerPolicies) {
                list = (List) (policySetParameters.remove(policy.getId().
                        toString()));
            } else {
                PolicyReference ref = (PolicyReference) policy;
                String id = ref.getReference().toString();

                if (ref.getReferenceType()
                        == PolicyReference.POLICY_REFERENCE) {
                    list = (List) (policyParameters.remove(id));
                } else {
                    list = (List) (policySetParameters.remove(id));
                }
            }
            elements.add(new PolicyCombinerElement(policy, list));
        }

        // ...and that there aren't extra parameters
        if (!policyParameters.isEmpty()) {
            throw new ParsingException("Unmatched parameters in Policy");
        }
        if (!policySetParameters.isEmpty()) {
            throw new ParsingException("Unmatched parameters in PolicySet");
        }

        policies = new ArrayList();

        for (Object o : elements) {
            PolicyCombinerElement policyCombinerElement = (PolicyCombinerElement) o;
            policies.add(policyCombinerElement.getPolicy());
        }
        // finally, set the list of Rules
        this.setChildren(elements);
        this.parameters = Collections.unmodifiableList(parameters);
    }

    @Override
    protected void setChildren(List children) {
        // we always want a concrete list, since we're going to pass it to
        // a combiner that expects a non-null input

        if (children == null) {
            this.children = Collections.EMPTY_LIST;
        } else {
            // NOTE: since this is only getting called by known child
            // classes we don't check that the types are all the same
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

    private void handleParameters(Node root) throws ParsingException {
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("CombinerParameter")) {
                parameters.add(CombinerParameter.getInstance(node));
            }
        }
    }

    /**
     * Private helper method that handles parsing a collection of parameters
     */
    private void paramaterHelper(HashMap parameters, Node root,
            String prefix) throws ParsingException {
        String ref = root.getAttributes().getNamedItem(prefix + "IdRef").
                getNodeValue();

        if (parameters.containsKey(ref)) {
            List list = (List) (parameters.get(ref));
            parseParameters(list, root);
        } else {
            List list = new ArrayList();
            parseParameters(list, root);
            parameters.put(ref, list);
        }
    }

    /**
     * Private helper method that handles parsing a single parameter.
     */
    private void parseParameters(List parameters, Node root)
            throws ParsingException {
        NodeList nodes = root.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeName().equals("CombinerParameter")) {
                parameters.add(CombinerParameter.getInstance(node));
            }
        }
    }

    public static OwnerPolicies getInstance(Node root) throws ParsingException {
        return getInstance(root, null);
    }

    public static OwnerPolicies getInstance(Node root, PolicyFinder finder) throws ParsingException {
        // first off, check that it's the right kind of node
        if (!root.getNodeName().equals("PolicySet")) {
            throw new ParsingException("Cannot create PolicySet from root of"
                    + " type " + root.getNodeName());
        }

        return new OwnerPolicies(root, finder);
    }

    public Target createTarget() {
        List resources = new ArrayList();
        List resource = new ArrayList();
        URI resourceDesignatorType = null;
        URI resourceDesignatorId = null;
        try {
            resourceDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
            resourceDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:resource:owner-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        AttributeDesignator resourceDesignator = new AttributeDesignator(AttributeDesignator.RESOURCE_TARGET, resourceDesignatorType, resourceDesignatorId, true);
        StringAttribute resourceValue = new StringAttribute(this.owner);
        String resourceMatchId = "urn:oasis:names:tc:xacml:1.0:function:string-equal";
        FunctionFactory factory = FunctionFactory.getTargetInstance();
        Function resourceFunction = null;
        try {
            resourceFunction = factory.createFunction(resourceMatchId);
        } catch (UnknownIdentifierException ex) {
            log.fatal(null, ex);
        } catch (FunctionTypeException ex) {
            log.fatal(null, ex);
        }

        TargetMatch resourceMatch = new TargetMatch(TargetMatch.RESOURCE, resourceFunction, resourceDesignator, resourceValue);
        resource.add(resourceMatch);
        TargetMatchGroup resourceMatchGroup = new TargetMatchGroup(resource, TargetMatch.RESOURCE);
        resources.add(resourceMatchGroup);

        List subjects = new ArrayList();
        List type = new ArrayList();
        URI typeDesignatorType = null;
        URI typeDesignatorId = null;
        try {
            typeDesignatorType = new URI("http://www.w3.org/2001/XMLSchema#string");
            typeDesignatorId = new URI("urn:oasis:names:tc:xacml:1.0:subject:module-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        AttributeDesignator typeDesignator = new AttributeDesignator(AttributeDesignator.SUBJECT_TARGET, typeDesignatorType, typeDesignatorId, true);
        StringAttribute typeValue = new StringAttribute(this.module.getValue());
        String typeMatchId = "urn:oasis:names:tc:xacml:1.0:function:string-equal";
        Function typeFunction = null;
        try {
            typeFunction = factory.createFunction(typeMatchId);
        } catch (UnknownIdentifierException ex) {
            log.fatal(null, ex);
        } catch (FunctionTypeException ex) {
            log.fatal(null, ex);
        }

        TargetMatch typeMatch = new TargetMatch(TargetMatch.SUBJECT, typeFunction, typeDesignator, typeValue);
        type.add(typeMatch);
        TargetMatchGroup typeMatchGroup = new TargetMatchGroup(type, TargetMatch.SUBJECT);
        subjects.add(typeMatchGroup);

        return MyTargetFactory.getTargetInstance(subjects, resources, null);
    }

    @Override
    public Result evaluate(EvaluationCtx context) {
        log.trace("OwnerPolicies evaluate...");
        Result result = combiningAlg.combine(context, parameters,
                childElements);

        int effect = result.getDecision();

        if ((effect == Result.DECISION_INDETERMINATE)
                || (effect == Result.DECISION_NOT_APPLICABLE)) {
            return result;
        }
        return result;
    }

    public GroupPolicy getGroupPolicy(String groupPolicyId) {
        for (Object p : policies) {
            GroupPolicy groupPolicy = (GroupPolicy) p;
            if (groupPolicy.getId().toString().equals(groupPolicyId)) {
                return groupPolicy;
            }
        }
        return null;
    }

    public boolean updatePolicy(GroupPolicy policy) {
        for (Object o : policies) {
            GroupPolicy groupPolicy = (GroupPolicy) o;
            if (groupPolicy.getId().toString().equals(policy.getId().toString())) {
                groupPolicy = policy;
                // TODO: and then? what do we do with policy?
                return true;
            }
        }
        return false;
    }

    public boolean addGroupPolicy(GroupPolicy policy) {
        if (getGroupPolicy(policy.getName()) != null) {
            return false;
        }
        if (policies.contains(policy)) {
            return false;
        }
        policy.createPolicy();
        return policies.add(policy);
    }

    public boolean updateGroupPolicyName(String gpName, String newName) {
        if (getGroupPolicy(newName) != null) {
            return false;
        }
        for (Object object : policies) {
            GroupPolicy group = (GroupPolicy) object;
            if (newName.equals(group.getName())) {
                return false;
            }
        }
        GroupPolicy gp = this.getGroupPolicy(gpName);
        URI uri;
        try {
            uri = new URI(newName);
            gp.setId(uri);
            gp.setName(newName);
            gp.createPolicy();
            return true;
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
            return false;
        }
    }

    public boolean removePolicy(GroupPolicy policy) {
        return policies.remove(policy);
    }

    public boolean removePolicy(String groupPolicyId) {
        return removePolicy(getGroupPolicy(groupPolicyId));
    }

    public void saveAsQueryPolicies() {
        try {
            encode(new FileOutputStream(new File(Configuration.QUERY_POLICIES_DIRECTORY + this.owner + ".xml")));
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        }
    }

    public void saveAsCapturePolicies() {
        try {
            encode(new FileOutputStream(new File(Configuration.CAPTURE_POLICIES_DIRECTORY + this.owner + ".xml")));
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        }
    }

    public void saveAsAdminPolicies() {
        try {
            encode(new FileOutputStream(new File(Configuration.ADMIN_POLICIES_DIRECTORY + this.owner + ".xml")));
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        }
    }

    public void delete() {
        File file = new File(filePath);
        file.delete();
    }

    public void encode(String filename) {
        try {
            encode(new FileOutputStream(new File(filename)));
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        }
    }

    public void encode(OutputStream output) {
        encode(output, new Indenter(0));
    }

    public void encode(OutputStream output, Indenter indenter) {
        PrintStream out = new PrintStream(output);
        String indent = indenter.makeString();

        out.println(indent + "<PolicySet PolicySetId=\"" + getId().toString()
                + "\" PolicyCombiningAlgId=\""
                + getCombiningAlg().getIdentifier().toString()
                + "\">");

        indenter.in();
        String nextIndent = indenter.makeString();

        String description = getDescription();
        if (description != null) {
            out.println(nextIndent + "<Description>" + description
                    + "</Description>");
        }

        String version = getDefaultVersion();
        if (version != null) {
            out.println("<PolicySetDefaults><XPathVersion>" + version
                    + "</XPathVersion></PolicySetDefaults>");
        }

        getTarget().encode(output, indenter);
        //encodeCommonElements(output, indenter);
        if (!policies.isEmpty()) {
            for (Object o : policies) {
                GroupPolicy gp = (GroupPolicy) o;
                gp.createPolicy();
                gp.encode(output, indenter);
            }
        }
        indenter.out();
        out.println(indent + "</PolicySet>");
    }
    private PolicyCombiningAlgorithm combiningAlg;

    @Override
    public MatchResult match(EvaluationCtx context) {
        log.trace("OwnerPolicies match...");
        return target.match(context);
    }

    @Override
    public String toString() {
        return this.idAttr.toString();
    }

    @Override
    public URI getId() {
        return idAttr;
    }

    public void setId(URI id) {
        this.idAttr = id;
    }

    @Override
    public PolicyCombiningAlgorithm getCombiningAlg() {
        return combiningAlg;
    }

    private void setCombiningAlg(PolicyCombiningAlgorithm policyCombiningAlgorithm) {
        this.combiningAlg = policyCombiningAlgorithm;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public URI getPolicyId() {
        return idAttr;
    }

    public void setPolicyId(URI policyId) {
        this.idAttr = policyId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List getPolicies() {
        return policies;
    }

    public void setPolicies(List policies) {
        this.policies = policies;
    }

    public Module getType() {
        return module;
    }

    public void setType(Module type) {
        if (Module.queryModule.equals(type)) {
            this.filePath = Configuration.QUERY_POLICIES_DIRECTORY + this.owner + ".xml";
        }
        if (Module.captureModule.equals(type)) {
            this.filePath = Configuration.CAPTURE_POLICIES_DIRECTORY + this.owner + ".xml";
        }
        if (Module.administrationModule.equals(type)) {
            this.filePath = Configuration.ADMIN_POLICIES_DIRECTORY + this.owner + ".xml";
        }
        this.module = type;
    }

    @Override
    public Target getTarget() {
        return target;
    }

    public void setTarget(Target t) {
        this.target = t;
    }

    public static void main(String[] args) {

        AccessPolicyManager apm = new AccessPolicyManager();//!!!!!!!!!! Très important pour importer les nouvelles méthodes !!!!!!!!!
        OwnerPolicies op = new OwnerPolicies("PartnerDeTest", Module.queryModule);
        String name = "groupTest";
        String owner = "PartnerDeTest";
        GroupPolicy gp = new GroupPolicy(name, owner);
        List<String> users = new ArrayList<String>();
        for (int i = 2; i < 5; i++) {
            String user = "user" + i;
            users.add(user);
        }
        gp.setUsers(users);

        //////////////// Actions ////////////////

        List<String> actions = new ArrayList<String>();
        for (int i = 1; i < 5; i++) {
            String bizStep = "action" + i;
            actions.add(bizStep);
        }
        gp.setActions(actions);


        //////////////// BizSteps ////////////////

        List<String> bizSteps = new ArrayList<String>();
        for (int i = 1; i < 5; i++) {
            String bizStep = "bizStep" + i;
            bizSteps.add(bizStep);
        }
        gp.setBizSteps(bizSteps);

        //////////////// EPCs ////////////////

        List<String> epcs = new ArrayList<String>();
        for (int i = 1; i < 5; i++) {
            String epc = "epc:" + i;
            epcs.add(epc);
        }
        gp.setEpcs(epcs);

        //////////////// EventTypes ////////////////

        List<String> eventTypes = new ArrayList<String>();
        for (int i = 1; i < 5; i++) {
            String eventType = "eventType:" + i;
            eventTypes.add(eventType);
        }
        gp.setEventTypes(eventTypes);

        //////////////// EventTimes ////////////////

        List<Date> eventTimes = new ArrayList<Date>();
        Date lowDate = new Date();
        lowDate.setTime(lowDate.getTime() - 100000);
        eventTimes.add(lowDate);
        Date highDate = new Date();
        highDate.setTime(highDate.getTime() - 20000);
        eventTimes.add(highDate);
        gp.setEventTimes(eventTimes);

        List policies = new ArrayList();
        gp.createPolicy();
        gp.setType(Module.queryModule);
        policies.add(gp);
        op.setPolicies(policies);
        op.setType(Module.queryModule);
        op.saveAsQueryPolicies();
    }
}
