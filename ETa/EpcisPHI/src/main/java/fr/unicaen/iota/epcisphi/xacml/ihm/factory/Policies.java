/*
 *  This program is a part of the IoTa Project.
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
package fr.unicaen.iota.epcisphi.xacml.ihm.factory;

import fr.unicaen.iota.epcisphi.xacml.ihm.Module;
import fr.unicaen.iota.epcisphi.xacml.ihm.NodeType;
import fr.unicaen.iota.epcisphi.xacml.ihm.TreeNode;
import java.util.ArrayList;
import java.util.List;

public class Policies {

    public List<TreeNode> policiesQuery = new ArrayList<TreeNode>();
    public List<TreeNode> policiesAdmin = new ArrayList<TreeNode>();
    public List<TreeNode> policiesCapture = new ArrayList<TreeNode>();

    public Policies() {
        createQueryPolicies();
        createAdminPolicies();
        createCapturePolicies();
    }

    public void createQueryPolicies() {
        Node policy1 = new Node("group_query_1", NodeType.policyNode, null, Module.queryModule, "id_group_query_1");

        Node users = new Node("Associated users:", NodeType.usersNode, null, Module.queryModule, "id_group_query_1");   // NATIVE
        users.addChild(new Node("user1", NodeType.userNode, null, Module.queryModule, "id_group_query_1"));
        users.addChild(new Node("user2", NodeType.userNode, null, Module.queryModule, "id_group_query_1"));
        users.addChild(new Node("user3", NodeType.userNode, null, Module.queryModule, "id_group_query_1"));

        Node filters = new Node("Restricted filters", NodeType.rulesNode, null, Module.queryModule, "id_group_query_1");  // NATIVE

        Node rule1 = new Node("ACCEPT", NodeType.bizStepFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node filter1 = new Node("blablabla", NodeType.bizStepFilterNode, null, Module.queryModule, "id_group_query_1");
        Node filter2 = new Node("bliblibli", NodeType.bizStepFilterNode, null, Module.queryModule, "id_group_query_1");
        Node filter3 = new Node("blobloblo", NodeType.bizStepFilterNode, null, Module.queryModule, "id_group_query_1");
        rule1.addChild(filter1);
        rule1.addChild(filter2);
        rule1.addChild(filter3);

        Node rule2 = new Node("DROP", NodeType.epcFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node filter21 = new Node("clacclac", NodeType.epcFilterNode, null, Module.queryModule, "id_group_query_1");
        Node filter22 = new Node("clicclic", NodeType.epcFilterNode, null, Module.queryModule, "id_group_query_1");
        rule2.addChild(filter21);
        rule2.addChild(filter22);

        Node rule3 = new Node("DROP", NodeType.eventTypeFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule4 = new Node("DROP", NodeType.eventTimeFilterGroupNode, null, Module.queryModule, "id_group_query_1");

        Node rule5 = new Node("DROP", NodeType.recordTimeFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule6 = new Node("DROP", NodeType.operationFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule7 = new Node("DROP", NodeType.parentIdFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule8 = new Node("DROP", NodeType.childEpcFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule9 = new Node("DROP", NodeType.quantityFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule10 = new Node("DROP", NodeType.readPointFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule11 = new Node("DROP", NodeType.bizLocFilterGroupNode, null, Module.queryModule, "id_group_query_1");
        Node rule12 = new Node("DROP", NodeType.dispositionFilterGroupNode, null, Module.queryModule, "id_group_query_1");

        filters.addChild(rule1);
        filters.addChild(rule2);
        filters.addChild(rule3);
        filters.addChild(rule4);
        filters.addChild(rule5);
        filters.addChild(rule6);
        filters.addChild(rule7);
        filters.addChild(rule8);
        filters.addChild(rule9);
        filters.addChild(rule10);
        filters.addChild(rule11);
        filters.addChild(rule12);

        policy1.addChild(users);
        policy1.addChild(filters);

        Node policy2 = new Node("group_query_2", NodeType.policyNode, null, Module.queryModule, "id_group_query_2");

        Node users2 = new Node("Associated users:", NodeType.usersNode, null, Module.queryModule, "id_group_query_2");
        users2.addChild(new Node("userx", NodeType.userNode, null, Module.queryModule, "id_group_query_2"));
        users2.addChild(new Node("usery", NodeType.userNode, null, Module.queryModule, "id_group_query_2"));
        users2.addChild(new Node("userz", NodeType.userNode, null, Module.queryModule, "id_group_query_2"));
        users2.addChild(new Node("useru", NodeType.userNode, null, Module.queryModule, "id_group_query_2"));
        users2.addChild(new Node("userv", NodeType.userNode, null, Module.queryModule, "id_group_query_2"));
        users2.addChild(new Node("userw", NodeType.userNode, null, Module.queryModule, "id_group_query_2"));

        policy2.addChild(users2);

        Node policies = new Node("Groups", NodeType.policiesNode, null, Module.queryModule, null); // NATIVE

        policies.addChild(policy1);
        policies.addChild(policy2);
        policiesQuery.add(policies);
    }

    public void createAdminPolicies() {
        Node policy1 = new Node("group_admin_1", NodeType.policyNode, null, Module.adminModule, "id_group_admin_1");

        Node users = new Node("Associated users", NodeType.usersNode, null, Module.adminModule, "id_group_admin_1");
        users.addChild(new Node("user1", NodeType.userNode, null, Module.adminModule, "id_group_admin_1"));
        users.addChild(new Node("user3", NodeType.userNode, null, Module.adminModule, "id_group_admin_1"));

        Node rule1 = new Node("ACCEPT", NodeType.methodFilterGroupNode, null, Module.adminModule, "id_group_admin_1");
        Node filter1 = new Node("eventCreate", NodeType.methodFilterNode, null, Module.adminModule, "id_group_admin_1");
        Node filter2 = new Node("filterCreate", NodeType.methodFilterNode, null, Module.adminModule, "id_group_admin_1");
        rule1.addChild(filter1);
        rule1.addChild(filter2);

        policy1.addChild(users);
        policy1.addChild(rule1);

        Node policy2 = new Node("group_admin_2", NodeType.policyNode, null, Module.adminModule, "id_group_admin_2");

        Node users2 = new Node("Associated users", NodeType.usersNode, null, Module.adminModule, "id_group_admin_2");
        users2.addChild(new Node("userx", NodeType.userNode, null, Module.adminModule, "id_group_admin_2"));
        users2.addChild(new Node("userz", NodeType.userNode, null, Module.adminModule, "id_group_admin_2"));
        users2.addChild(new Node("useru", NodeType.userNode, null, Module.adminModule, "id_group_admin_2"));
        users2.addChild(new Node("userv", NodeType.userNode, null, Module.adminModule, "id_group_admin_2"));
        users2.addChild(new Node("userw", NodeType.userNode, null, Module.adminModule, "id_group_admin_2"));

        Node rule2 = new Node("DROP", NodeType.methodFilterGroupNode, null, Module.adminModule, "id_group_admin_2");
        Node filter21 = new Node("groupCreate", NodeType.methodFilterNode, null, Module.adminModule, "id_group_admin_2");
        Node filter22 = new Node("groupUpdate", NodeType.methodFilterNode, null, Module.adminModule, "id_group_admin_2");
        rule2.addChild(filter21);
        rule2.addChild(filter22);

        policy2.addChild(rule2);
        policy2.addChild(users2);

        Node policies = new Node("Groups", NodeType.policiesNode, null, Module.adminModule, null);

        policies.addChild(policy1);
        policies.addChild(policy2);
        policiesAdmin.add(policies);
    }

    private void createCapturePolicies() {
        Node policies = new Node("Groups", NodeType.policiesNode, null, Module.captureModule, null);
        policiesCapture.add(policies);
    }
}
