/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dphi.xacml.ihm.factory;

import fr.unicaen.iota.dphi.utils.InterfaceHelper;
import fr.unicaen.iota.dphi.utils.MapSessions;
import fr.unicaen.iota.dphi.xacml.ihm.Module;
import fr.unicaen.iota.dphi.xacml.ihm.NodeType;
import fr.unicaen.iota.dphi.xacml.ihm.TreeNode;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 */
public class AccessPolicies {

    private List<TreeNode> policiesQuery = new ArrayList<TreeNode>();
    private List<TreeNode> policiesAdmin = new ArrayList<TreeNode>();
    private List<TreeNode> policiesCapture = new ArrayList<TreeNode>();

    public AccessPolicies() {
    }

    public AccessPolicies(String userId, String owner) {
        this.createQueryPolicies(userId, owner);
        this.createCapturePolicies(userId, owner);
        this.createAdminPolicies(userId, owner);
    }

    public AccessPolicies(String userId, String owner, Module module) {
        switch (module) {
            case adminModule:
                this.createAdminPolicies(userId, owner);
                break;
            case queryModule:
                this.createQueryPolicies(userId, owner);
                break;
            case captureModule:
                this.createCapturePolicies(userId, owner);
                break;
        }
    }

    private synchronized void createQueryPolicies(String userId, String owner) {
        OwnerPolicies ownerPolicies = MapSessions.getAPMSession(userId, owner).APMSession.getQueryPolicy(owner);
        Node policies = new Node("", NodeType.policiesNode, null, Module.queryModule, null);
        if (ownerPolicies != null) {
            for (Object ogp : ownerPolicies.getPolicies()) {
                if (ogp instanceof GroupPolicy) {
                    GroupPolicy gp = (GroupPolicy) ogp;
                    GroupPolicyTreeNode gptn = new GroupPolicyTreeNode(gp, gp.getId().toString(), Module.queryModule);
                    UserGroupRuleTreeNode groupRoot = new UserGroupRuleTreeNode(gp.getUsersFilterFunction(), gp.getId().toString(), Module.queryModule);
                    for (Object ob : gp.getUsers()) {
                        String value = (String) ob;
                        UserTreeNode userTreeNode = new UserTreeNode(value, value, gp.getId().toString(), Module.queryModule);
                        groupRoot.addChild(userTreeNode);
                    }
                    gptn.addChild(groupRoot);

                    ActionTargetTreeNode actionTargetTreeNode = new ActionTargetTreeNode(gp.getId().toString(), Module.queryModule);

                    for (Object ob : gp.getActions()) {
                        String value = (String) ob;
                        ActionTreeNode actionTreeNode = new ActionTreeNode(value, value, gp.getId().toString(), Module.queryModule);
                        actionTargetTreeNode.addChild(actionTreeNode);
                    }
                    gptn.addChild(actionTargetTreeNode);

                    Node filters = new Node("", NodeType.rulesNode, null, Module.queryModule, "id_group_query_1");  // NATIVE

                    BizStepRuleTreeNode bizStepRuleTreeNode = new BizStepRuleTreeNode(gp.getBizStepsFilterFunction(), gp.getId().toString(), Module.queryModule);

                    for (Object ob : gp.getBizSteps()) {
                        String value = (String) ob;
                        BizStepTreeNode treeNode = new BizStepTreeNode(value, value, gp.getId().toString(), Module.queryModule);
                        bizStepRuleTreeNode.addChild(treeNode);
                    }
                    filters.addChild(bizStepRuleTreeNode);

                    EventTypeRuleTreeNode eventTypeRuleTreeNode = new EventTypeRuleTreeNode(gp.getEventTypesFilterFunction(), gp.getId().toString(), Module.queryModule);

                    for (Object ob : gp.getEventTypes()) {
                        String value = (String) ob;
                        EventTypeTreeNode treeNode = new EventTypeTreeNode(value, value, gp.getId().toString(), Module.queryModule);
                        eventTypeRuleTreeNode.addChild(treeNode);
                    }

                    filters.addChild(eventTypeRuleTreeNode);

                    EpcsRuleTreeNode epcRuleTreeNode = new EpcsRuleTreeNode(gp.getEpcsFilterFunction(), gp.getId().toString(), Module.queryModule);

                    for (Object ob : gp.getEpcs()) {
                        String value = (String) ob;
                        EpcTreeNode treeNode = new EpcTreeNode(value, value, gp.getId().toString(), Module.queryModule);
                        epcRuleTreeNode.addChild(treeNode);
                    }

                    filters.addChild(epcRuleTreeNode);

                    EventTimeRuleTreeNode eventTimeRuleTreeNode = new EventTimeRuleTreeNode(gp.getEventTimesFilterFunction(), gp.getId().toString(), Module.queryModule);

                    for (Object ob : gp.getEventTimes()) {
                        List value = (List) ob;
                        EventTimeTreeNode treeNode = new EventTimeTreeNode((Date) value.get(0), (Date) value.get(1), gp.getId().toString(), Module.queryModule);
                        eventTimeRuleTreeNode.addChild(treeNode);
                    }
                    filters.addChild(eventTimeRuleTreeNode);
                    gptn.addChild(filters);
                    policies.addChild(gptn);
                }
            }
        } else {
            InterfaceHelper ih = MapSessions.getAPMSession(userId, owner);
            ownerPolicies = new OwnerPolicies(owner, fr.unicaen.iota.xacml.policy.Module.queryModule);
            ih.APMSession.addQueryPolicy(ownerPolicies);
            ih.APMSession.saveQueryPolicies(owner);
            ih.updateAPM();
        }
        getPoliciesQuery().add(policies);
    }

    private synchronized void createCapturePolicies(String userId, String owner) {
        OwnerPolicies ownerPolicies = MapSessions.getAPMSession(userId, owner).APMSession.getCapturePolicy(owner);
        Node policies = new Node("", NodeType.policiesNode, null, Module.captureModule, null);

        if (ownerPolicies != null) {
            for (Object ogp : ownerPolicies.getPolicies()) {
                if (ogp instanceof GroupPolicy) {
                    GroupPolicy gp = (GroupPolicy) ogp;
                    GroupPolicyTreeNode gptn = new GroupPolicyTreeNode(gp, gp.getId().toString(), Module.captureModule);
                    UserGroupRuleTreeNode groupRoot = new UserGroupRuleTreeNode(gp.getUsersFilterFunction(), gp.getId().toString(), Module.captureModule);
                    for (Object ob : gp.getUsers()) {
                        String value = (String) ob;
                        UserTreeNode userTreeNode = new UserTreeNode(value, value, gp.getId().toString(), Module.captureModule);
                        groupRoot.addChild(userTreeNode);
                    }
                    gptn.addChild(groupRoot);

                    ActionTargetTreeNode actionTargetTreeNode = new ActionTargetTreeNode(gp.getId().toString(), Module.captureModule);

                    for (Object ob : gp.getActions()) {
                        String value = (String) ob;
                        ActionTreeNode actionTreeNode = new ActionTreeNode(value, value, gp.getId().toString(), Module.captureModule);
                        actionTargetTreeNode.addChild(actionTreeNode);
                    }
                    gptn.addChild(actionTargetTreeNode);

                    Node filters = new Node("", NodeType.rulesNode, null, Module.captureModule, "id_group_capture_1");  // NATIVE

                    BizStepRuleTreeNode bizStepRuleTreeNode = new BizStepRuleTreeNode(gp.getBizStepsFilterFunction(), gp.getId().toString(), Module.captureModule);

                    for (Object ob : gp.getBizSteps()) {
                        String value = (String) ob;
                        BizStepTreeNode treeNode = new BizStepTreeNode(value, value, gp.getId().toString(), Module.captureModule);
                        bizStepRuleTreeNode.addChild(treeNode);
                    }
                    filters.addChild(bizStepRuleTreeNode);

                    EventTypeRuleTreeNode eventTypeRuleTreeNode = new EventTypeRuleTreeNode(gp.getEventTypesFilterFunction(), gp.getId().toString(), Module.captureModule);

                    for (Object ob : gp.getEventTypes()) {
                        String value = (String) ob;
                        EventTypeTreeNode treeNode = new EventTypeTreeNode(value, value, gp.getId().toString(), Module.captureModule);
                        eventTypeRuleTreeNode.addChild(treeNode);
                    }

                    filters.addChild(eventTypeRuleTreeNode);

                    EpcsRuleTreeNode epcRuleTreeNode = new EpcsRuleTreeNode(gp.getEpcsFilterFunction(), gp.getId().toString(), Module.captureModule);

                    for (Object ob : gp.getEpcs()) {
                        String value = (String) ob;
                        EpcTreeNode treeNode = new EpcTreeNode(value, value, gp.getId().toString(), Module.captureModule);
                        epcRuleTreeNode.addChild(treeNode);
                    }

                    filters.addChild(epcRuleTreeNode);

                    EventTimeRuleTreeNode eventTimeRuleTreeNode = new EventTimeRuleTreeNode(gp.getEventTimesFilterFunction(), gp.getId().toString(), Module.captureModule);

                    for (Object ob : gp.getEventTimes()) {
                        List value = (List) ob;
                        EventTimeTreeNode treeNode = new EventTimeTreeNode((Date) value.get(0), (Date) value.get(1), gp.getId().toString(), Module.captureModule);
                        eventTimeRuleTreeNode.addChild(treeNode);
                    }
                    filters.addChild(eventTimeRuleTreeNode);
                    gptn.addChild(filters);
                    policies.addChild(gptn);

                }
            }
        } else {
            InterfaceHelper ih = MapSessions.getAPMSession(userId, owner);
            ownerPolicies = new OwnerPolicies(owner, fr.unicaen.iota.xacml.policy.Module.captureModule);
            ih.APMSession.addCapturePolicy(ownerPolicies);
            ih.APMSession.saveCapturePolicies(owner);
            ih.updateAPM();
        }
        getPoliciesCapture().add(policies);
    }

    private synchronized void createAdminPolicies(String userId, String owner) {
        OwnerPolicies ownerPolicies = MapSessions.getAPMSession(userId, owner).APMSession.getAdminPolicy(owner);
        Node policies = new Node("", NodeType.policiesNode, null, Module.adminModule, null);

        if (ownerPolicies != null) {
            for (Object ogp : ownerPolicies.getPolicies()) {
                if (ogp instanceof GroupPolicy) {
                    GroupPolicy gp = (GroupPolicy) ogp;
                    GroupPolicyTreeNode gptn = new GroupPolicyTreeNode(gp, gp.getId().toString(), Module.adminModule);
                    UserGroupRuleTreeNode groupRoot = new UserGroupRuleTreeNode(gp.getUsersFilterFunction(), gp.getId().toString(), Module.adminModule);
                    for (Object ob : gp.getUsers()) {
                        String value = (String) ob;
                        UserTreeNode userTreeNode = new UserTreeNode(value, value, gp.getId().toString(), Module.adminModule);
                        groupRoot.addChild(userTreeNode);
                    }
                    gptn.addChild(groupRoot);

                    ActionTargetTreeNode actionTargetTreeNode = new ActionTargetTreeNode(gp.getId().toString(), Module.adminModule);

                    for (Object ob : gp.getActions()) {
                        String value = (String) ob;
                        ActionTreeNode actionTreeNode = new ActionTreeNode(value, value, gp.getId().toString(), Module.adminModule);
                        actionTargetTreeNode.addChild(actionTreeNode);
                    }
                    gptn.addChild(actionTargetTreeNode);
                    policies.addChild(gptn);

                }
            }
        } else {
            InterfaceHelper ih = MapSessions.getAPMSession(userId, owner);
            ownerPolicies = new OwnerPolicies(owner, fr.unicaen.iota.xacml.policy.Module.administrationModule);
            ih.APMSession.addAdminPolicy(ownerPolicies);
            ih.APMSession.saveAdminPolicies(owner);
            ih.updateAPM();
        }
        getPoliciesAdmin().add(policies);
    }

    public synchronized TreeNode createGroupPolicy(GroupPolicy gp, Module module) {
        GroupPolicyTreeNode gptn = new GroupPolicyTreeNode(gp, gp.getId().toString(), module);
        UserGroupRuleTreeNode groupRoot = new UserGroupRuleTreeNode(gp.getUsersFilterFunction(), gp.getId().toString(), module);
        for (Object ob : gp.getUsers()) {
            String value = (String) ob;
            UserTreeNode userTreeNode = new UserTreeNode(value, value, gp.getId().toString(), module);
            groupRoot.addChild(userTreeNode);
        }
        gptn.addChild(groupRoot);

        ActionTargetTreeNode actionTargetTreeNode = new ActionTargetTreeNode(gp.getId().toString(), module);

        for (Object ob : gp.getActions()) {
            String value = (String) ob;
            ActionTreeNode actionTreeNode = new ActionTreeNode(value, value, gp.getId().toString(), module);
            actionTargetTreeNode.addChild(actionTreeNode);
        }
        gptn.addChild(actionTargetTreeNode);

        if (!module.equals(Module.adminModule)) {
            Node filters = new Node("", NodeType.rulesNode, null, module, "id_group_query_1");  // NATIVE

            BizStepRuleTreeNode bizStepRuleTreeNode = new BizStepRuleTreeNode(gp.getBizStepsFilterFunction(), gp.getId().toString(), module);

            for (Object ob : gp.getBizSteps()) {
                String value = (String) ob;
                BizStepTreeNode treeNode = new BizStepTreeNode(value, value, gp.getId().toString(), module);
                bizStepRuleTreeNode.addChild(treeNode);
            }
            filters.addChild(bizStepRuleTreeNode);

            EventTypeRuleTreeNode eventTypeRuleTreeNode = new EventTypeRuleTreeNode(gp.getEventTypesFilterFunction(), gp.getId().toString(), module);

            for (Object ob : gp.getEventTypes()) {
                String value = (String) ob;
                EventTypeTreeNode treeNode = new EventTypeTreeNode(value, value, gp.getId().toString(), module);
                eventTypeRuleTreeNode.addChild(treeNode);
            }

            filters.addChild(eventTypeRuleTreeNode);

            EpcsRuleTreeNode epcRuleTreeNode = new EpcsRuleTreeNode(gp.getEpcsFilterFunction(), gp.getId().toString(), module);

            for (Object ob : gp.getEpcs()) {
                String value = (String) ob;
                EpcTreeNode treeNode = new EpcTreeNode(value, value, gp.getId().toString(), module);
                epcRuleTreeNode.addChild(treeNode);
            }

            filters.addChild(epcRuleTreeNode);

            EventTimeRuleTreeNode eventTimeRuleTreeNode = new EventTimeRuleTreeNode(gp.getEventTimesFilterFunction(), gp.getId().toString(), module);

            for (Object ob : gp.getEventTimes()) {
                List value = (List) ob;
                EventTimeTreeNode treeNode = new EventTimeTreeNode((Date) value.get(0), (Date) value.get(1), gp.getId().toString(), module);
                eventTimeRuleTreeNode.addChild(treeNode);
            }
            filters.addChild(eventTimeRuleTreeNode);
            gptn.addChild(filters);
        }
        return gptn;
    }

    /**
     * @return the policiesQuery
     */
    public synchronized List<TreeNode> getPoliciesQuery() {
        return policiesQuery;
    }

    /**
     * @param policiesQuery the policiesQuery to set
     */
    public synchronized void setPoliciesQuery(List<TreeNode> policiesQuery) {
        this.policiesQuery = policiesQuery;
    }

    /**
     * @return the policiesAdmin
     */
    public synchronized List<TreeNode> getPoliciesAdmin() {
        return policiesAdmin;
    }

    /**
     * @param policiesAdmin the policiesAdmin to set
     */
    public synchronized void setPoliciesAdmin(List<TreeNode> policiesAdmin) {
        this.policiesAdmin = policiesAdmin;
    }

    /**
     * @return the policiesCapture
     */
    public synchronized List<TreeNode> getPoliciesCapture() {
        return policiesCapture;
    }

    /**
     * @param policiesCapture the policiesCapture to set
     */
    public synchronized void setPoliciesCapture(List<TreeNode> policiesCapture) {
        this.policiesCapture = policiesCapture;
    }
}
