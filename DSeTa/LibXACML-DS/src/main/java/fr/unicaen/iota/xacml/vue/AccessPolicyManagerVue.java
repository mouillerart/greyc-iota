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

import fr.unicaen.iota.xacml.AccessPolicyManager;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.xacml.policy.Module;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import java.util.Date;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 */
public class AccessPolicyManagerVue extends javax.swing.JFrame {

    private String owner;
    private String user;
    private AccessPolicyManager apm;
    private DefaultMutableTreeNode captureRoot;
    private DefaultMutableTreeNode queryRoot;
    private DefaultMutableTreeNode administrationRoot;
    private boolean isInitiated = false;

    /**
     * Creates new form AccessPolicyManagerInterface
     */
    public AccessPolicyManagerVue(String owner, String user) {
        this.owner = owner;
        this.user = user;
        initComponents();
        initGroups();
    }

    private void initGroups() {
        apm = new AccessPolicyManager();
        createQueryTreeNodes();
    }

    private void createQueryTreeNodes() {
        root.removeAllChildren();
        List list = apm.getPolicies(owner);
        OwnerPolicies queryPolicy = (OwnerPolicies) list.get(0);
        if (queryPolicy == null) {
            queryPolicy = new OwnerPolicies(owner, Module.queryModule);
            apm.addQueryPolicy(queryPolicy);
        }
        OwnerPoliciesTreeNode queryPolicyInterface = new OwnerPoliciesTreeNode(queryPolicy);
        queryRoot = new DefaultMutableTreeNode(queryPolicyInterface);
        root.add(queryRoot);
        List groupPolicies = queryPolicy.getPolicies();
        for (Object ob : groupPolicies) {
            GroupPolicy gp = (GroupPolicy) ob;
            GroupPolicyTreeNode groupPolicyInterface = new GroupPolicyTreeNode(gp);
            DefaultMutableTreeNode groupRoot = new DefaultMutableTreeNode(groupPolicyInterface);
            queryRoot.add(groupRoot);
            DefaultMutableTreeNode filtersRoot = new DefaultMutableTreeNode("Filters");
            groupRoot.add(filtersRoot);

            UserGroupRuleTreeNode userGroupRuleTreeNode = new UserGroupRuleTreeNode(gp.getUsers(), gp.getUsersFilterFunction());
            DefaultMutableTreeNode userGroupRoot = new DefaultMutableTreeNode(userGroupRuleTreeNode);
            groupRoot.insert(userGroupRoot, 0);

            ActionTargetTreeNode actionsTreeNode = new ActionTargetTreeNode(gp.getActions());
            DefaultMutableTreeNode actionsRoot = new DefaultMutableTreeNode(actionsTreeNode);
            groupRoot.insert(actionsRoot, 0);

            EpcsRuleTreeNode epcsRuleTreeNode = new EpcsRuleTreeNode(gp.getEpcs(), gp.getEpcsFilterFunction());
            DefaultMutableTreeNode epcsGroupRoot = new DefaultMutableTreeNode(epcsRuleTreeNode);
            filtersRoot.add(epcsGroupRoot);

            EventTypeRuleTreeNode eventTypeRuleTreeNode = new EventTypeRuleTreeNode(gp.getEventTypes(), gp.getEventTypesFilterFunction());
            DefaultMutableTreeNode eventTypeGroupRoot = new DefaultMutableTreeNode(eventTypeRuleTreeNode);
            filtersRoot.add(eventTypeGroupRoot);

            BizStepRuleTreeNode bizStepRuleTreeNode = new BizStepRuleTreeNode(gp.getBizSteps(), gp.getBizStepsFilterFunction());
            DefaultMutableTreeNode bizStepGroupRoot = new DefaultMutableTreeNode(bizStepRuleTreeNode);
            filtersRoot.add(bizStepGroupRoot);

            EventTimeRuleTreeNode eventTimeRuleTreeNode = new EventTimeRuleTreeNode(gp.getEventTimes(), gp.getEventTimesFilterFunction());
            DefaultMutableTreeNode eventTimeGroupRoot = new DefaultMutableTreeNode(eventTimeRuleTreeNode);
            filtersRoot.add(eventTimeGroupRoot);

            for (Object obAction : gp.getActions()) {
                String action = (String) obAction;
                StringAttributeTreeNode satn = new StringAttributeTreeNode(action, "action");
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(satn);
                actionsRoot.add(valueNode);
            }

            for (Object obUser : gp.getUsers()) {
                String suser = (String) obUser;
                String permission = (gp.getUsersFilterFunction().getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) ? "DENY" : "PERMIT";
                StringAttributeTreeNode satn = new StringAttributeTreeNode(suser, permission);
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(satn);
                userGroupRoot.add(valueNode);
            }

            for (Object obBizStep : gp.getBizSteps()) {
                String bizStep = (String) obBizStep;
                String permission = (gp.getBizStepsFilterFunction().getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) ? "DENY" : "PERMIT";
                StringAttributeTreeNode satn = new StringAttributeTreeNode(bizStep, permission);
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(satn);
                bizStepGroupRoot.add(valueNode);
            }

            for (Object obEpc : gp.getEpcs()) {
                String epc = (String) obEpc;
                String permission = (gp.getEpcsFilterFunction().getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) ? "DENY" : "PERMIT";
                StringAttributeTreeNode satn = new StringAttributeTreeNode(epc, permission);
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(satn);
                epcsGroupRoot.add(valueNode);
            }

            for (Object obEventType : gp.getEventTypes()) {
                String eventType = (String) obEventType;
                String permission = (gp.getEventTypesFilterFunction().getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) ? "DENY" : "PERMIT";
                StringAttributeTreeNode satn = new StringAttributeTreeNode(eventType, permission);
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(satn);
                eventTypeGroupRoot.add(valueNode);
            }
            for (Object obEventTime : gp.getEventTimes()) {
                List eventTimes = (List) obEventTime;
                Date lowDate = (Date) eventTimes.get(0);
                Date highDate = (Date) eventTimes.get(1);
                String permission = (gp.getEventTimesFilterFunction().getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) ? "DENY" : "PERMIT";
                DateTimeAttributeTreeNode satn = new DateTimeAttributeTreeNode(lowDate, highDate, permission);
                DefaultMutableTreeNode valueNode = new DefaultMutableTreeNode(satn);
                eventTimeGroupRoot.add(valueNode);
            }

        }
    }

    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabelTitle = new javax.swing.JLabel();
        jLabelOwner = new javax.swing.JLabel();
        jLabelUser = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButtonDelete = new javax.swing.JButton();
        jButtonModify = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonAddGroup = new javax.swing.JButton();
        jButtonAddFilter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        root = new DefaultMutableTreeNode(owner);
        jTreeGroupsView = new javax.swing.JTree(root);

        setDefaultCloseOperation(
                javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelTitle.setText("AccessPolicyManager");

        jLabelOwner.setText("owner: " + owner);

        jLabelUser.setText("user: " + user);

        jButtonDelete.setText("Delete");
        jButtonDelete.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteActionPerformed(evt);
            }
        });

        jButtonModify.setText("Modify Filter");
        jButtonModify.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonModifyActionPerformed(evt);
            }
        });


        jButtonSave.setText("Save");
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });

        jButtonAddGroup.setText("Add new group");
        jButtonAddGroup.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAddGroupActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(jTreeGroupsView);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap(588, Short.MAX_VALUE).addComponent(jButtonSave).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonModify).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jButtonDelete).addContainerGap()).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addContainerGap(309, Short.MAX_VALUE).addComponent(jButtonAddGroup, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE).addGap(305, 305, 305)).addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE));
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup().addGap(27, 27, 27).addComponent(jButtonAddGroup).addGap(18, 18, 18).addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 272, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE).addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jButtonDelete).addComponent(jButtonModify).addComponent(jButtonSave)).addContainerGap()));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addContainerGap().addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabelOwner).addContainerGap(759, Short.MAX_VALUE)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabelUser).addContainerGap(778, Short.MAX_VALUE)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup().addComponent(jLabelTitle).addGap(330, 330, 330)).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()))));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(jPanel1Layout.createSequentialGroup().addComponent(jLabelTitle).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabelOwner).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jLabelUser).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        mouseEventHandler = new MouseEventHandler(jTreeGroupsView, this);
        jTreeGroupsView.addMouseListener(mouseEventHandler);

        pack();
    }// </editor-fold>

    private void jButtonAddGroupActionPerformed(java.awt.event.ActionEvent evt) {
        Object o = jTreeGroupsView.getLastSelectedPathComponent();
        DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) o;
        Object object = nodeSelected.getUserObject();
        if (object instanceof OwnerPoliciesTreeNode) {
            GroupPolicyInterface dialog = new GroupPolicyInterface(this, rootPaneCheckingEnabled);
            dialog.setVisible(true);
        }
    }

    public void handleGroupPolicyAdding(String content) {
        Object o = jTreeGroupsView.getLastSelectedPathComponent();
        DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) o;
        Object object = nodeSelected.getUserObject();
        if (object instanceof OwnerPoliciesTreeNode) {
            OwnerPoliciesTreeNode ownerPoliciesTreeNode = (OwnerPoliciesTreeNode) object;
            if (Module.queryModule.equals(ownerPoliciesTreeNode.getOwnerPolicies().getType())) {
                GroupPolicy groupPolicy = new GroupPolicy(content, owner);
                groupPolicy.addAction("test");
                groupPolicy.createPolicy();
                apm.addQueryGroupPolicy(owner, groupPolicy);
                GroupPolicyTreeNode groupPolicyTreeNode = new GroupPolicyTreeNode(groupPolicy);
                createQueryTreeNodes();
            }
        }
        jTreeGroupsView.updateUI();
    }

    private void jButtonDeleteActionPerformed(java.awt.event.ActionEvent evt) {
    }

    private void jButtonModifyActionPerformed(java.awt.event.ActionEvent evt) {
        Object o = jTreeGroupsView.getLastSelectedPathComponent();
        DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) o;
        Object object = nodeSelected.getUserObject();
    }

    private void addFilterInTree(DefaultMutableTreeNode parent, String content) {
        if (parent.getUserObject() instanceof RuleTreeNode) {
            RuleTreeNode ruleTreeNode = (RuleTreeNode) parent.getUserObject();
            String permission = (ruleTreeNode.getFunction().getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) ? "DENY" : "PERMIT";
            StringAttributeTreeNode epcAtt = new StringAttributeTreeNode(content, permission);
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(epcAtt);
            parent.add(childNode);
            jTreeGroupsView.updateUI();
        }
    }

    private void addFilterInTree(DefaultMutableTreeNode parent, List dates) {
        if (parent.getUserObject() instanceof RuleTreeNode) {
            RuleTreeNode ruleTreeNode = (RuleTreeNode) parent.getUserObject();
            String permission = (ruleTreeNode.getFunction().getFunctionName().equals(OneOrGlobalFunction.NAME_GLOBAL_PERMIT)) ? "DENY" : "PERMIT";
            DateTimeAttributeTreeNode epcAtt = new DateTimeAttributeTreeNode((Date) dates.get(0), (Date) dates.get(1), permission);
            DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(epcAtt);
            parent.add(childNode);
            jTreeGroupsView.updateUI();
        }
    }

    void handleUserAdding(String text) {
        Object object = mouseEventHandler.getLastObjectSelected();
        DefaultMutableTreeNode nodeSelected = mouseEventHandler.getLastNodeSelected();
        DefaultMutableTreeNode groupPolicyNode = (DefaultMutableTreeNode) nodeSelected.getParent();
        Object groupObject = groupPolicyNode.getUserObject();
        if (groupObject instanceof GroupPolicyTreeNode) {
            GroupPolicyTreeNode groupPolicyTreeNode = (GroupPolicyTreeNode) groupObject;
            apm.addQueryUserFilter(owner, groupPolicyTreeNode.getGroupPolicy().getName(), text);
        }
        addFilterInTree(nodeSelected, text);
    }

    void handleActionAdding(String text) {
        Object object = mouseEventHandler.getLastObjectSelected();
        DefaultMutableTreeNode nodeSelected = mouseEventHandler.getLastNodeSelected();
        DefaultMutableTreeNode groupPolicyNode = (DefaultMutableTreeNode) nodeSelected.getParent();
        Object groupObject = groupPolicyNode.getUserObject();
        if (groupObject instanceof GroupPolicyTreeNode) {
            GroupPolicyTreeNode groupPolicyTreeNode = (GroupPolicyTreeNode) groupObject;
            apm.addQueryActionFilter(owner, groupPolicyTreeNode.getGroupPolicy().getName(), text);
        }
        addFilterInTree(nodeSelected, text);
    }

    void handleBizStepAdding(String text) {
        Object object = mouseEventHandler.getLastObjectSelected();
        DefaultMutableTreeNode nodeSelected = mouseEventHandler.getLastNodeSelected();
        DefaultMutableTreeNode filterNode = (DefaultMutableTreeNode) nodeSelected.getParent();
        DefaultMutableTreeNode groupPolicyNode = (DefaultMutableTreeNode) filterNode.getParent();
        Object groupObject = groupPolicyNode.getUserObject();
        if (groupObject instanceof GroupPolicyTreeNode) {
            GroupPolicyTreeNode groupPolicyTreeNode = (GroupPolicyTreeNode) groupObject;
            apm.addQueryBizStepFilter(owner, groupPolicyTreeNode.getGroupPolicy().getName(), text);
        }
        addFilterInTree(nodeSelected, text);
    }

    void handleEpcAdding(String text) {
        Object object = mouseEventHandler.getLastObjectSelected();
        DefaultMutableTreeNode nodeSelected = mouseEventHandler.getLastNodeSelected();
        DefaultMutableTreeNode filterNode = (DefaultMutableTreeNode) nodeSelected.getParent();
        DefaultMutableTreeNode groupPolicyNode = (DefaultMutableTreeNode) filterNode.getParent();
        Object groupObject = groupPolicyNode.getUserObject();
        if (groupObject instanceof GroupPolicyTreeNode) {
            GroupPolicyTreeNode groupPolicyTreeNode = (GroupPolicyTreeNode) groupObject;
            apm.addQueryEpcFilter(owner, groupPolicyTreeNode.getGroupPolicy().getName(), text);
        }
        addFilterInTree(nodeSelected, text);
    }

    void handleEventTypeAdding(String text) {
        Object object = mouseEventHandler.getLastObjectSelected();
        DefaultMutableTreeNode nodeSelected = mouseEventHandler.getLastNodeSelected();
        DefaultMutableTreeNode filterNode = (DefaultMutableTreeNode) nodeSelected.getParent();
        DefaultMutableTreeNode groupPolicyNode = (DefaultMutableTreeNode) filterNode.getParent();
        Object groupObject = groupPolicyNode.getUserObject();
        if (groupObject instanceof GroupPolicyTreeNode) {
            GroupPolicyTreeNode groupPolicyTreeNode = (GroupPolicyTreeNode) groupObject;
            apm.addQueryEventTypeFilter(owner, groupPolicyTreeNode.getGroupPolicy().getName(), text);
        }
        addFilterInTree(nodeSelected, text);
    }

    void handleEventTimeAdding(List dates) {
        Object object = mouseEventHandler.getLastObjectSelected();
        DefaultMutableTreeNode nodeSelected = mouseEventHandler.getLastNodeSelected();
        DefaultMutableTreeNode filterNode = (DefaultMutableTreeNode) nodeSelected.getParent();
        DefaultMutableTreeNode groupPolicyNode = (DefaultMutableTreeNode) filterNode.getParent();
        Object groupObject = groupPolicyNode.getUserObject();
        if (groupObject instanceof GroupPolicyTreeNode) {
            GroupPolicyTreeNode groupPolicyTreeNode = (GroupPolicyTreeNode) groupObject;
            apm.addQueryEventTimeFilter(owner, groupPolicyTreeNode.getGroupPolicy().getName(), dates);
        }
        addFilterInTree(nodeSelected, dates);
    }

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {
        apm.save(owner);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                new AccessPolicyManagerVue("toto", "user1.1").setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify
    private javax.swing.JButton jButtonAddGroup;
    private javax.swing.JButton jButtonAddFilter;
    private javax.swing.JButton jButtonDelete;
    private javax.swing.JButton jButtonModify;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JLabel jLabelOwner;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JLabel jLabelUser;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTree jTreeGroupsView;
    private DefaultMutableTreeNode root;
    private MouseEventHandler mouseEventHandler;
    // End of variables declaration
}
