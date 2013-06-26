/*
 *  This program is a part of the IoTa project.
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
package fr.unicaen.iota.xacml.finder;

import com.sun.xacml.*;
import com.sun.xacml.combine.PolicyCombiningAlgorithm;
import com.sun.xacml.ctx.Status;
import com.sun.xacml.support.finder.PolicyCollection;
import com.sun.xacml.support.finder.TopLevelPolicyException;
import fr.unicaen.iota.xacml.AccessPolicyManagerSession;
import fr.unicaen.iota.xacml.configuration.Configuration;
import fr.unicaen.iota.xacml.policy.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URI;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MyPolicyCollection extends PolicyCollection {

    // the actual collection of policies
    private Map queryPolicies;
    private Map capturePolicies;
    private Map adminPolicies;
    // the optional combining algorithm used when wrapping multiple policies
    private PolicyCombiningAlgorithm combiningAlg;
    // the optional policy id used when wrapping multiple policies
    private URI parentId;
    // the single instance of the comparator we'll use for managing version
    private VersionComparator versionComparator = new VersionComparator();
    private static final Target target;
    private static final Log log = LogFactory.getLog(MyPolicyCollection.class);

    static {
        target = new Target(new TargetSection(null, TargetMatch.SUBJECT, PolicyMetaData.XACML_VERSION_2_0),
                new TargetSection(null, TargetMatch.RESOURCE, PolicyMetaData.XACML_VERSION_2_0),
                new TargetSection(null, TargetMatch.ACTION, PolicyMetaData.XACML_VERSION_2_0),
                new TargetSection(null, TargetMatch.ENVIRONMENT, PolicyMetaData.XACML_VERSION_2_0));
    }

    public Map getQueryPolicies() {
        return queryPolicies;
    }

    public Map getCapturePolicies() {
        return capturePolicies;
    }

    public Map getAdminPolicies() {
        return adminPolicies;
    }

    public MyPolicyCollection() {
        super();
        queryPolicies = new HashMap();
        capturePolicies = new HashMap();
        adminPolicies = new HashMap();
    }

    public MyPolicyCollection(PolicyCombiningAlgorithm combiningAlg, URI parentPolicyId) {
        super(combiningAlg, parentPolicyId);
        queryPolicies = new HashMap();
        capturePolicies = new HashMap();
        adminPolicies = new HashMap();
    }

    @Override
    public AbstractPolicy getPolicy(EvaluationCtx context)
            throws TopLevelPolicyException {
        List allPolicies = new ArrayList();
        allPolicies.addAll(queryPolicies.values());
        allPolicies.addAll(capturePolicies.values());
        allPolicies.addAll(adminPolicies.values());
        List list = new ArrayList();
        for (Object object : allPolicies) {
            int result = -1;
            MatchResult match = null;
            AbstractPolicy policy = null;
            if (object instanceof OwnerPolicies) {
                OwnerPolicies ownerPolicies = (OwnerPolicies) object;
                policy = ownerPolicies;
                MatchResult ownerMatch = ownerPolicies.match(context);
                result = ownerMatch.getResult();
                if (result != MatchResult.MATCH) {
                    result = MatchResult.INDETERMINATE;
                    match = new MatchResult(MatchResult.INDETERMINATE);
                }
            }
            if (result == MatchResult.INDETERMINATE) {
                continue;
            }
            if (result == MatchResult.MATCH) {
                if ((combiningAlg == null) && (list.size() > 0)) {
                    List code = new ArrayList();
                    code.add(Status.STATUS_PROCESSING_ERROR);
                    Status status = new Status(code, "too many applicable"
                            + " top-level policies");
                    throw new TopLevelPolicyException(status);
                }
                if (policy != null) {
                    return policy;
                }
            }
        }
        switch (list.size()) {
            case 0:
                return null;
            case 1:
                return ((OwnerPolicies) (list.get(0)));
            default:
                return new PolicySet(parentId, combiningAlg, target, list);
        }
    }

    public boolean updateQueryGroupName(String ownerID, String groupId, String value) {
        OwnerPolicies ownerPolicies = getQueryPolicy(ownerID);
        return ownerPolicies.updateGroupPolicyName(groupId, value);
    }

    public boolean updateCaptureGroupName(String ownerID, String groupId, String value) {
        OwnerPolicies ownerPolicies = getCapturePolicy(ownerID);
        return ownerPolicies.updateGroupPolicyName(groupId, value);
    }

    public boolean updateAdminGroupName(String ownerID, String groupId, String value) {
        OwnerPolicies ownerPolicies = getAdminPolicy(ownerID);
        return ownerPolicies.updateGroupPolicyName(groupId, value);
    }

    //########################################################################################
    //################################## Query Module ########################################
    //########################################################################################
    public boolean addQueryPolicy(OwnerPolicies policy) {
        return addQueryPolicy(policy, policy.getId().toString());
    }

    public boolean addQueryPolicy(OwnerPolicies policy, String identifier) {
        if (!queryPolicies.containsKey(identifier)) {
            policy.setType(Module.queryModule);
            queryPolicies.put(identifier, policy);
            return true;
        }
        return false;
    }

    public boolean updateQueryPolicy(OwnerPolicies policy) {
        return updateQueryPolicy(policy, policy.getId().toString());
    }

    public boolean updateQueryPolicy(OwnerPolicies policy, String identifier) {
        if (queryPolicies.containsKey(identifier)) {
            queryPolicies.put(identifier, policy);
            return true;
        }
        return false;
    }

    public boolean addQueryGroupPolicy(GroupPolicy policy, String identifier) {
        if (queryPolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
            policy.setType(Module.queryModule);
            return ownerPolicies.addGroupPolicy(policy);
        }
        return false;
    }

    public boolean updateQueryPolicy(GroupPolicy policy, String identifier) {
        if (queryPolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
            return ownerPolicies.updatePolicy(policy);
        }
        return false;
    }

    public boolean deleteQueryOwnerPolicy(OwnerPolicies policy) {
        return deleteQueryOwnerPolicy(policy.getId().toString());
    }

    public boolean deleteQueryOwnerPolicy(String identifier) {
        if (queryPolicies.containsKey(identifier)) {
            queryPolicies.remove(identifier);
            return true;
        }
        return false;
    }

    public boolean deleteQueryGroupPolicy(String identifier, String groupId) {
        if (queryPolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
            return ownerPolicies.removePolicy(groupId);
        }
        return false;
    }

    public OwnerPolicies getQueryPolicy(String identifier) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        return ownerPolicies;
    }

    public boolean addQueryFilter(String identifier, String groupName, String filterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addUserFilter(filterName);
        }
        if (SCBizStepRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addBizStepFilter(filterName);
        }
        if (SCEpcsRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addEpcFilter(filterName);
        }
        if (SCEventTypeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addEventTypeFilter(filterName);
        }
        if (SCOperationRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addOperationFilter(filterName);
        }
        if (SCParentIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addParentIdFilter(filterName);
        }
        if (SCChildEpcRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addChildEpcFilter(filterName);
        }
        if (SCReadPointRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addReadPointFilter(filterName);
        }
        if (SCBizLocRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addBizLocFilter(filterName);
        }
        if (SCBizTransRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addBizTransFilter(filterName);
        }
        if (SCDispositionRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addDispositionFilter(filterName);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.addExtensionFilter(filterType, filterName);
        }
        if (SCMasterDataIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addMasterDataIdFilter(filterName);
        }
        return false;
    }

    public boolean addQueryFilter(String identifier, String groupName, List filters, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (filters.size() != 2) {
            return false;
        }
        if (SCEventTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addEventTimeFilter(filters);
        }
        if (SCRecordTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addRecordTimeFilter(filters);
        }
        if (SCQuantityRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addQuantityFilter(filters);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.addExtensionFilter(filterType, filters);
        }
        return false;
    }

    boolean addQueryActionFilter(String identifier, String groupName, String actionName) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        return groupPolicy.addAction(actionName);
    }

    boolean removeQueryActionFilter(String identifier, String groupName, String actionName) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        return groupPolicy.removeAction(actionName);
    }

    public boolean removeQueryFilter(String identifier, String groupName, String filterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeUserFilter(filterName);
        }
        if (SCBizStepRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeBizStepFilter(filterName);
        }
        if (SCEpcsRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeEpcFilter(filterName);
        }
        if (SCOperationRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeOperationFilter(filterName);
        }
        if (SCEventTypeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeEventTypeFilter(filterName);
        }
        if (SCParentIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeParentIdFilter(filterName);
        }
        if (SCChildEpcRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeChildEpcFilter(filterName);
        }
        if (SCReadPointRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeReadPointFilter(filterName);
        }
        if (SCBizLocRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeBizLocFilter(filterName);
        }
        if (SCBizTransRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeBizTransFilter(filterName);
        }
        if (SCDispositionRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeDispositionFilter(filterName);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.removeExtensionFilter(filterType, filterName);
        }
        if (SCMasterDataIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeMasterDataIdFilter(filterName);
        }
        return false;
    }

    public boolean removeQueryFilter(String identifier, String groupName, List filters, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (filters.size() != 2) {
            return false;
        }
        if (SCEventTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeEventTimeFilter(filters);
        }
        if (SCRecordTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeRecordTimeFilter(filters);
        }
        if (SCQuantityRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeQuantityFilter(filters);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.removeExtensionFilter(filterType, filters);
        }
        return false;
    }

    public boolean updateQueryFilter(String identifier, String groupName, String newFilterName, String oldFilterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateUserFilter(newFilterName, oldFilterName);
        }
        if (SCBizStepRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateBizStepFilter(newFilterName, oldFilterName);
        }
        if (SCEpcsRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateEpcFilter(newFilterName, oldFilterName);
        }
        if (SCOperationRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateOperationFilter(newFilterName, oldFilterName);
        }
        if (SCEventTypeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateEventTypeFilter(newFilterName, oldFilterName);
        }
        if (SCParentIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateParentIdFilter(newFilterName, oldFilterName);
        }
        if (SCChildEpcRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateChildEpcFilter(newFilterName, oldFilterName);
        }
        if (SCReadPointRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateReadPointFilter(newFilterName, oldFilterName);
        }
        if (SCBizLocRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateBizLocFilter(newFilterName, oldFilterName);
        }
        if (SCBizTransRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateBizTransFilter(newFilterName, oldFilterName);
        }
        if (SCDispositionRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateDispositionFilter(newFilterName, oldFilterName);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            List newList = Arrays.asList(newFilterName);
            List oldList = Arrays.asList(oldFilterName);
            return groupPolicy.updateExtensionFilter(filterType, newList, oldList);
        }
        if (SCMasterDataIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateMasterDataIdFilter(newFilterName, oldFilterName);
        }
        return false;
    }

    public boolean updateQueryFilter(String identifier, String groupName, List newFilters, List oldFilters, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (oldFilters.size() != 2 || newFilters.size() != 2) {
            return false;
        }
        if (SCEventTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateEventTimeFilter((Date) newFilters.get(0), (Date) newFilters.get(1), (Date) oldFilters.get(0), (Date) oldFilters.get(1));
        }
        if (SCRecordTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateRecordTimeFilter((Date) newFilters.get(0), (Date) newFilters.get(1), (Date) oldFilters.get(0), (Date) oldFilters.get(1));
        }
        if (SCQuantityRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateQuantityFilter(((Long) newFilters.get(0)).longValue(), ((Long) newFilters.get(1)).longValue(), ((Long) oldFilters.get(0)).longValue(), ((Long) oldFilters.get(1)).longValue());
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.updateExtensionFilter(filterType, newFilters, oldFilters);
        }
        return false;
    }

    public boolean switchQueryPermissionUsers(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchUsersFunction();

    }

    public boolean switchQueryPermissionBizSteps(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchBizStepsFunction();
    }

    public boolean switchQueryPermissionEpcs(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchEpcsFunction();
    }

    public boolean switchQueryPermissionEventTimes(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchEventTimesFunction();
    }

    public boolean switchQueryPermissionRecordTimes(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchRecordTimesFunction();
    }

    public boolean switchQueryPermissionOperations(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchOperationsFunction();
    }

    public boolean switchQueryPermissionEventTypes(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchEventTypesFunction();
    }

    public boolean switchQueryPermissionParentIds(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchParentIdsFunction();
    }

    public boolean switchQueryPermissionChildEpcs(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchChildEpcsFunction();
    }

    public boolean switchQueryPermissionQuantities(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchQuantitiesFunction();
    }

    public boolean switchQueryPermissionReadPoints(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchReadPointsFunction();
    }

    public boolean switchQueryPermissionBizLocs(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchBizLocsFunction();
    }

    public boolean switchQueryPermissionBizTrans(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchBizTransFunction();
    }

    public boolean switchQueryPermissionDispositions(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchDispositionsFunction();
    }

    public boolean switchQueryPermissionExtensions(String identifier, String policyId, String extensionId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchExtensionsFunction(extensionId);
    }

    public boolean switchQueryPermissionMasterDataIds(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) queryPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchMasterDataIdsFunction();
    }

    //####################################################
    //################# CAPTURE MODULE ###################
    //####################################################
    public boolean switchCapturePermissionUsers(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchUsersFunction();

    }

    public boolean switchCapturePermissionBizSteps(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchBizStepsFunction();
    }

    public boolean switchCapturePermissionEpcs(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchEpcsFunction();
    }

    public boolean switchCapturePermissionEventTimes(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchEventTimesFunction();
    }

    public boolean switchCapturePermissionRecordTimes(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchRecordTimesFunction();
    }

    public boolean switchCapturePermissionOperations(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchOperationsFunction();
    }

    public boolean switchCapturePermissionEventTypes(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchEventTypesFunction();
    }

    public boolean switchCapturePermissionParentIds(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchParentIdsFunction();
    }

    public boolean switchCapturePermissionChildEpcs(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchChildEpcsFunction();
    }

    public boolean switchCapturePermissionQuantities(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchQuantitiesFunction();
    }

    public boolean switchCapturePermissionReadPoints(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchReadPointsFunction();
    }

    public boolean switchCapturePermissionBizLocs(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchBizLocsFunction();
    }

    public boolean switchCapturePermissionBizTrans(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchBizTransFunction();
    }

    public boolean switchCapturePermissionDispositions(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchDispositionsFunction();
    }

    public boolean switchCapturePermissionExtensions(String identifier, String policyId, String extensionId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchExtensionsFunction(extensionId);
    }

    public boolean switchCapturePermissionMasterDataIds(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchMasterDataIdsFunction();
    }

    public boolean addCapturePolicy(OwnerPolicies policy) {
        return addCapturePolicy(policy, policy.getId().toString());
    }

    public boolean addCapturePolicy(OwnerPolicies policy, String identifier) {
        if (!capturePolicies.containsKey(identifier)) {
            policy.setType(Module.captureModule);
            capturePolicies.put(identifier, policy);
            return true;
        }
        return false;
    }

    public boolean updateCapturePolicy(OwnerPolicies policy) {
        return updateCapturePolicy(policy, policy.getId().toString());
    }

    public boolean updateCapturePolicy(OwnerPolicies policy, String identifier) {
        if (capturePolicies.containsKey(identifier)) {
            capturePolicies.put(identifier, policy);
            return true;
        }
        return false;
    }

    public boolean addCaptureGroupPolicy(GroupPolicy policy, String identifier) {
        if (capturePolicies.containsKey(identifier)) {
            policy.setType(Module.captureModule);
            OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
            return ownerPolicies.addGroupPolicy(policy);
        }
        return false;
    }

    public boolean updateCapturePolicy(GroupPolicy policy, String identifier) {
        if (capturePolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
            return ownerPolicies.updatePolicy(policy);
        }
        return false;
    }

    public boolean deleteCaptureOwnerPolicy(OwnerPolicies policy) {
        return deleteCaptureOwnerPolicy(policy.getId().toString());
    }

    public boolean deleteCaptureOwnerPolicy(String identifier) {
        if (capturePolicies.containsKey(identifier)) {
            capturePolicies.remove(identifier);
            return true;
        }
        return false;
    }

    public boolean deleteCaptureGroupPolicy(String identifier, String groupId) {
        printPolicies(capturePolicies);
        if (capturePolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
            return ownerPolicies.removePolicy(groupId);
        }
        return false;
    }

    public OwnerPolicies getCapturePolicy(String identifier) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        return ownerPolicies;
    }

    public boolean addCaptureFilter(String identifier, String groupName, String filterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addUserFilter(filterName);
        }
        if (SCBizStepRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addBizStepFilter(filterName);
        }
        if (SCEpcsRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addEpcFilter(filterName);
        }
        if (SCOperationRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addOperationFilter(filterName);
        }
        if (SCEventTypeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addEventTypeFilter(filterName);
        }
        if (SCParentIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addParentIdFilter(filterName);
        }
        if (SCChildEpcRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addChildEpcFilter(filterName);
        }
        if (SCReadPointRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addReadPointFilter(filterName);
        }
        if (SCBizLocRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addBizLocFilter(filterName);
        }
        if (SCBizTransRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addBizTransFilter(filterName);
        }
        if (SCDispositionRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addDispositionFilter(filterName);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.addExtensionFilter(filterType, filterName);
        }
        if (SCMasterDataIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addMasterDataIdFilter(filterName);
        }
        return false;
    }

    boolean addCaptureActionFilter(String identifier, String groupName, String actionName) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        return groupPolicy.addAction(actionName);
    }

    boolean removeCaptureActionFilter(String identifier, String groupName, String actionName) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        return groupPolicy.removeAction(actionName);
    }

    public boolean addCaptureFilter(String identifier, String groupName, List filters, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (filters.size() != 2) {
            return false;
        }
        if (SCEventTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addEventTimeFilter(filters);
        }
        if (SCRecordTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addRecordTimeFilter(filters);
        }
        if (SCQuantityRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addQuantityFilter(filters);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.addExtensionFilter(filterType, filters);
        }
        return false;
    }

    public boolean removeCaptureFilter(String identifier, String groupName, String filterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeUserFilter(filterName);
        }
        if (SCBizStepRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeBizStepFilter(filterName);
        }
        if (SCEpcsRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeEpcFilter(filterName);
        }
        if (SCOperationRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeOperationFilter(filterName);
        }
        if (SCEventTypeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeEventTypeFilter(filterName);
        }
        if (SCParentIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeParentIdFilter(filterName);
        }
        if (SCChildEpcRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeChildEpcFilter(filterName);
        }
        if (SCReadPointRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeReadPointFilter(filterName);
        }
        if (SCBizLocRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeBizLocFilter(filterName);
        }
        if (SCBizTransRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeBizTransFilter(filterName);
        }
        if (SCDispositionRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeDispositionFilter(filterName);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.removeExtensionFilter(filterType, filterName);
        }
        if (SCMasterDataIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeMasterDataIdFilter(filterName);
        }
        return false;
    }

    public boolean removeCaptureFilter(String identifier, String groupName, List filters, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (filters.size() != 2) {
            return false;
        }
        if (SCEventTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeEventTimeFilter(filters);
        }
        if (SCRecordTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeRecordTimeFilter(filters);
        }
        if (SCQuantityRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeQuantityFilter(filters);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.removeExtensionFilter(filterType, filters);
        }
        return false;
    }

    public boolean updateCaptureFilter(String identifier, String groupName, String newFilterName, String oldFilterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateUserFilter(newFilterName, oldFilterName);
        }
        if (SCBizStepRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateBizStepFilter(newFilterName, oldFilterName);
        }
        if (SCEpcsRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateEpcFilter(newFilterName, oldFilterName);
        }
        if (SCOperationRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateOperationFilter(newFilterName, oldFilterName);
        }
        if (SCEventTypeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateEventTypeFilter(newFilterName, oldFilterName);
        }
        if (SCParentIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateParentIdFilter(newFilterName, oldFilterName);
        }
        if (SCChildEpcRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateChildEpcFilter(newFilterName, oldFilterName);
        }
        if (SCReadPointRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateReadPointFilter(newFilterName, oldFilterName);
        }
        if (SCBizLocRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateBizLocFilter(newFilterName, oldFilterName);
        }
        if (SCBizTransRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateBizTransFilter(newFilterName, oldFilterName);
        }
        if (SCDispositionRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateDispositionFilter(newFilterName, oldFilterName);
        }
        if (groupPolicy.extensionsContains(filterType)) {
            List newList = Arrays.asList(newFilterName);
            List oldList = Arrays.asList(oldFilterName);
            return groupPolicy.updateExtensionFilter(filterType, newList, oldList);
        }
        if (SCMasterDataIdRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateMasterDataIdFilter(newFilterName, oldFilterName);
        }
        return false;
    }

    public boolean updateCaptureFilter(String identifier, String groupName, List newFilters, List oldFilters, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) capturePolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (oldFilters.size() != 2 || newFilters.size() != 2) {
            return false;
        }
        if (SCEventTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateEventTimeFilter((Date) newFilters.get(0), (Date) newFilters.get(1), (Date) oldFilters.get(0), (Date) oldFilters.get(1));
        }
        if (SCRecordTimeRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateRecordTimeFilter((Date) newFilters.get(0), (Date) newFilters.get(1), (Date) oldFilters.get(0), (Date) oldFilters.get(1));
        }
        if (SCQuantityRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.updateQuantityFilter((Long) newFilters.get(0), (Long) newFilters.get(1), (Long) oldFilters.get(0), (Long) oldFilters.get(1));
        }
        if (groupPolicy.extensionsContains(filterType)) {
            return groupPolicy.updateExtensionFilter(filterType, newFilters, oldFilters);
        }
        return false;
    }

    //########################################################################################
    //############################## Administration Module ###################################
    //########################################################################################
    public boolean addAdminPolicy(OwnerPolicies policy) {
        return addAdminPolicy(policy, policy.getId().toString());
    }

    public boolean addAdminPolicy(OwnerPolicies policy, String identifier) {
        if (!adminPolicies.containsKey(identifier)) {
            policy.setType(Module.administrationModule);
            adminPolicies.put(identifier, policy);
            return true;
        }
        return false;
    }

    public boolean addAdminGroupPolicy(GroupPolicy policy, String identifier) {
        if (adminPolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
            policy.setType(Module.administrationModule);
            return ownerPolicies.addGroupPolicy(policy);
        }
        return false;
    }

    public boolean updateAdminPolicy(GroupPolicy policy, String identifier) {
        if (adminPolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
            return ownerPolicies.updatePolicy(policy);
        }
        return false;
    }

    public boolean deleteAdminOwnerPolicy(OwnerPolicies policy) {
        return deleteAdminOwnerPolicy(policy.getId().toString());
    }

    public boolean deleteAdminOwnerPolicy(String identifier) {
        if (adminPolicies.containsKey(identifier)) {
            adminPolicies.remove(identifier);
            return true;
        }
        return false;
    }

    public boolean deleteAdminGroupPolicy(String identifier, String groupId) {
        if (adminPolicies.containsKey(identifier)) {
            OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
            return ownerPolicies.removePolicy(groupId);
        }
        return false;
    }

    public OwnerPolicies getAdminPolicy(String identifier) {
        return (OwnerPolicies) adminPolicies.get(identifier);
    }

    public boolean addAdminFilter(String identifier, String groupName, String filterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.addUserFilter(filterName);
        }
        return false;
    }

    boolean addAdminActionFilter(String identifier, String groupName, String actionName) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        return groupPolicy.addAction(actionName);
    }

    boolean removeAdminActionFilter(String identifier, String groupName, String actionName) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        return groupPolicy.removeAction(actionName);
    }

    public boolean removeAdminFilter(String identifier, String groupName, String filterName, String filterType) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(groupName);
        if (SCgroupRule.RULEFILTER.equals(filterType)) {
            return groupPolicy.removeUserFilter(filterName);
        }
        return false;
    }

    public boolean switchAdminPermissionUsers(String identifier, String policyId) {
        OwnerPolicies ownerPolicies = (OwnerPolicies) adminPolicies.get(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        GroupPolicy groupPolicy = ownerPolicies.getGroupPolicy(policyId);
        if (groupPolicy == null) {
            return false;
        }
        return groupPolicy.switchUsersFunction();

    }

    //##################################################################################
    void save(String identifier) {
        getQueryPolicy(identifier).saveAsQueryPolicies();
        getCapturePolicy(identifier).saveAsCapturePolicies();
        getAdminPolicy(identifier).saveAsAdminPolicies();
    }

    boolean saveQueryPolicies(String identifier) {
        OwnerPolicies ownerPolicies = getQueryPolicy(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        ownerPolicies.saveAsQueryPolicies();
        return true;
    }

    boolean saveCapturePolicies(String identifier) {
        OwnerPolicies ownerPolicies = getCapturePolicy(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        ownerPolicies.saveAsCapturePolicies();
        return true;
    }

    boolean saveAdminPolicies(String identifier) {
        OwnerPolicies ownerPolicies = getAdminPolicy(identifier);
        if (ownerPolicies == null) {
            return false;
        }
        ownerPolicies.saveAsAdminPolicies();
        return true;
    }

    private void printPolicies(Map capturePolicies) {
        if (log.isTraceEnabled()) {
            log.trace("policiesId: ");
            for (Object o : capturePolicies.keySet()) {
                log.trace("  | " + (String) o);
            }
        }
    }

    private OwnerPolicies getOwnerPolicies(MyPolicyReader reader, String fname) {
        try {
            AbstractPolicy policy = reader.readPolicy(new FileInputStream(fname));
            OwnerPolicies ownerPolicies = (OwnerPolicies) policy;
            for (Object o : ownerPolicies.getPolicies()) {
                if (o instanceof GroupPolicy) {
                    GroupPolicy gp = (GroupPolicy) o;
                    gp.setType(Module.administrationModule);
                }
            }
            ownerPolicies.setType(Module.administrationModule);
            return ownerPolicies;
        } catch (FileNotFoundException ex) {
            log.error("File not found: " + fname, ex);
        } catch (ParsingException ex) {
            log.error("Parsing error file: " + fname, ex);
        }
        return null;
    }

    boolean updateAPMSession(AccessPolicyManagerSession APMS, MyPolicyReader reader) {
        String owner = APMS.getOwner();
        OwnerPolicies adminOP = APMS.getAdminPolicy(owner);
        String ownerAdminOP = adminOP.getOwner();
        String adminFilename = Configuration.ADMIN_POLICIES_DIRECTORY + ownerAdminOP + ".xml";
        if (adminPolicies.containsKey(owner)) {
            deleteAdminOwnerPolicy(owner);
            if (APMS.getAdminPolicy(owner) != null) {
                addAdminPolicy(getOwnerPolicies(reader, adminFilename));
            }
        } else {
            if (APMS.getAdminPolicy(owner) != null) {
                addAdminPolicy(getOwnerPolicies(reader, adminFilename));
            }
        }
        String captureFilename = Configuration.CAPTURE_POLICIES_DIRECTORY + ownerAdminOP + ".xml";
        if (capturePolicies.containsKey(owner)) {
            deleteCaptureOwnerPolicy(owner);
            if (APMS.getCapturePolicy(owner) != null) {
                addCapturePolicy(getOwnerPolicies(reader, captureFilename));
            }
        } else {
            if (APMS.getCapturePolicy(owner) != null) {
                addCapturePolicy(getOwnerPolicies(reader, captureFilename));
            }
        }
        String queryFilename = Configuration.QUERY_POLICIES_DIRECTORY + ownerAdminOP + ".xml";
        if (queryPolicies.containsKey(owner)) {
            deleteQueryOwnerPolicy(owner);
            if (APMS.getQueryPolicy(owner) != null) {
                addQueryPolicy(getOwnerPolicies(reader, queryFilename));
            }
        } else {
            if (APMS.getQueryPolicy(owner) != null) {
                addQueryPolicy(getOwnerPolicies(reader, queryFilename));
            }
        }
        return true;
    }

    public boolean updateAPMQuerySession(AccessPolicyManagerSession APMS, OwnerPolicies ownerPolicies) {
        String owner = APMS.getOwner();
        if (queryPolicies.containsKey(owner)) {
            deleteQueryOwnerPolicy(owner);
            if (APMS.getQueryPolicy(owner) != null) {
                addQueryPolicy(ownerPolicies);
            }
        } else {
            if (APMS.getQueryPolicy(owner) != null) {
                addQueryPolicy(ownerPolicies);
            }
        }
        return true;
    }

    public boolean updateAPMCaptureSession(AccessPolicyManagerSession APMS, OwnerPolicies ownerPolicies) {
        String owner = APMS.getOwner();
        if (capturePolicies.containsKey(owner)) {
            deleteCaptureOwnerPolicy(owner);
            if (APMS.getCapturePolicy(owner) != null) {
                addCapturePolicy(ownerPolicies);
            }
        } else {
            if (APMS.getCapturePolicy(owner) != null) {
                addCapturePolicy(ownerPolicies);
            }
        }
        return true;
    }

    public boolean updateAPMAdminSession(AccessPolicyManagerSession APMS, OwnerPolicies ownerPolicies) {
        String owner = APMS.getOwner();
        if (adminPolicies.containsKey(owner)) {
            deleteAdminOwnerPolicy(owner);
            if (APMS.getAdminPolicy(owner) != null) {
                addAdminPolicy(ownerPolicies);
            }
        } else {
            if (APMS.getAdminPolicy(owner) != null) {
                addAdminPolicy(ownerPolicies);
            }
        }
        return true;
    }

    class VersionComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            String v1 = ((AbstractPolicy) o2).getVersion();
            String v2 = ((AbstractPolicy) o1).getVersion();

            if (v1.equals(v2)) {
                return 0;
            }

            StringTokenizer tok1 = new StringTokenizer(v1, ".");
            StringTokenizer tok2 = new StringTokenizer(v2, ".");

            while (tok1.hasMoreTokens()) {
                if (!tok2.hasMoreTokens()) {
                    return 1;
                }

                int num1 = Integer.parseInt(tok1.nextToken());
                int num2 = Integer.parseInt(tok2.nextToken());

                if (num1 > num2) {
                    return 1;
                }

                if (num1 < num2) {
                    return -1;
                }
            }

            if (tok2.hasMoreTokens()) {
                return -1;
            }

            return 0;
        }
    }
}
