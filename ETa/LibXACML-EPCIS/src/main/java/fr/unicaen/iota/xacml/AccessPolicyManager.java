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
package fr.unicaen.iota.xacml;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.PDPConfig;
import com.sun.xacml.PolicyMetaData;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.combine.BaseCombiningAlgFactory;
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.CombiningAlgFactoryProxy;
import com.sun.xacml.combine.StandardCombiningAlgFactory;
import com.sun.xacml.cond.FunctionFactory;
import com.sun.xacml.cond.FunctionFactoryProxy;
import com.sun.xacml.cond.StandardFunctionFactory;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.ResponseCtx;
import com.sun.xacml.finder.AttributeFinder;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.impl.CurrentEnvModule;
import fr.unicaen.iota.xacml.combine.SCGroupPolicyAlg;
import fr.unicaen.iota.xacml.combine.SCGroupRuleAcceptAlg;
import fr.unicaen.iota.xacml.combine.SCGroupRuleAlg;
import fr.unicaen.iota.xacml.combine.SCGroupRuleDenyAlg;
import fr.unicaen.iota.xacml.cond.OneOrGlobalFunction;
import fr.unicaen.iota.xacml.configuration.Configuration;
import fr.unicaen.iota.xacml.finder.MyPolicyFinderModule;
import fr.unicaen.iota.xacml.policy.GroupPolicy;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import fr.unicaen.iota.xacml.policy.function.RevertRegexpMatch;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AccessPolicyManager {

    private static final Log log = LogFactory.getLog(AccessPolicyManager.class);
    protected EPCISPDP epcispdp;
    protected MyPolicyFinderModule finderModule;

    public synchronized EPCISPDP getEpcispdp() {
        return epcispdp;
    }

    public synchronized void setEpcispdp(EPCISPDP epcispdp) {
        this.epcispdp = epcispdp;
    }

    public synchronized void init() {
        log.trace("INIT AccessPolicyManager");
    }

    public AccessPolicyManager() {
        initCombiningAlg();
        initFunction();
        initEPCISPDP();
        initFinderModule();
    }

    public AccessPolicyManager(String partnerName) {
        initCombiningAlg();
        initFunction();
        initEPCISPDP(partnerName);
        initFinderModule();
    }

    public synchronized static AccessPolicyManagerSession getInstance(String partnerName) {
        return new AccessPolicyManagerSession(partnerName);
    }

    public void updatePartner(String partnerName) {
    }

    public synchronized void initFinderModule() {
        PolicyFinder policyFinder = this.epcispdp.getPolicyFinder();
        if (policyFinder.getModules().size() != 1) {
            finderModule = null;
        }
        Iterator it = policyFinder.getModules().iterator();
        finderModule = (MyPolicyFinderModule) it.next();
    }

    public synchronized void initEPCISPDP(String partnerName) {
        String queryPoliciesDirFileName = Configuration.QUERY_POLICIES_DIRECTORY + partnerName + ".xml";
        File queryPoliciesDir = new File(queryPoliciesDirFileName);
        ArrayList queryFilesList = new ArrayList();
        if (queryPoliciesDir.exists()) {
            queryFilesList.add(queryPoliciesDir.getAbsolutePath());
        }
        String capturePoliciesDirFileName = Configuration.CAPTURE_POLICIES_DIRECTORY + partnerName + ".xml";
        File capturePoliciesDir = new File(capturePoliciesDirFileName);
        ArrayList captureFilesList = new ArrayList();
        if (capturePoliciesDir.exists()) {
            captureFilesList.add(capturePoliciesDir.getAbsolutePath());
        }

        String adminPoliciesDirFileName = Configuration.ADMIN_POLICIES_DIRECTORY + partnerName + ".xml";
        File adminPoliciesDir = new File(adminPoliciesDirFileName);
        ArrayList adminFilesList = new ArrayList();
        if (adminPoliciesDir.exists()) {
            adminFilesList.add(adminPoliciesDir.getAbsolutePath());
        }

        MyPolicyFinderModule policyModule = new MyPolicyFinderModule(queryFilesList, captureFilesList, adminFilesList);
        CurrentEnvModule envModule = new CurrentEnvModule();
        PolicyFinder policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(policyModule);
        policyFinder.setModules(policyModules);
        AttributeFinder attributeFinder = new AttributeFinder();
        List attrModules = new ArrayList();
        attrModules.add(envModule);
        attributeFinder.setModules(attrModules);
        PDPConfig pdpConfig = new PDPConfig(attributeFinder, policyFinder, null);
        log.trace("INITEPCISPDP -> " + partnerName);
        epcispdp = new EPCISPDP(pdpConfig);
    }

    public synchronized void initEPCISPDP() {
        String queryPoliciesDirFileName = Configuration.QUERY_POLICIES_DIRECTORY;
        File queryPoliciesDir = new File(queryPoliciesDirFileName);
        String[] queryPoliciesFileList = queryPoliciesDir.list();
        ArrayList queryFilesList = new ArrayList();
        for (String fileN : queryPoliciesFileList) {
            File file = new File(queryPoliciesDir.getAbsolutePath() + "/" + fileN);
            if (file.exists()) {
                queryFilesList.add(queryPoliciesDir.getAbsolutePath() + "/" + fileN);
            }
        }

        String capturePoliciesDirFileName = Configuration.CAPTURE_POLICIES_DIRECTORY;
        File capturePoliciesDir = new File(capturePoliciesDirFileName);
        String[] capturePoliciesFileList = capturePoliciesDir.list();
        ArrayList captureFilesList = new ArrayList();
        for (String fileN : capturePoliciesFileList) {
            File file = new File(capturePoliciesDir.getAbsolutePath() + "/" + fileN);
            if (file.exists()) {
                captureFilesList.add(capturePoliciesDir.getAbsolutePath() + "/" + fileN);
            }
        }

        String adminPoliciesDirFileName = Configuration.ADMIN_POLICIES_DIRECTORY;
        File adminPoliciesDir = new File(adminPoliciesDirFileName);
        String[] adminPoliciesFileList = adminPoliciesDir.list();
        ArrayList adminFilesList = new ArrayList();
        for (String fileN : adminPoliciesFileList) {
            File file = new File(adminPoliciesDir.getAbsolutePath() + "/" + fileN);
            if (file.exists()) {
                adminFilesList.add(adminPoliciesDir.getAbsolutePath() + "/" + fileN);
            }
        }


        MyPolicyFinderModule policyModule = new MyPolicyFinderModule(queryFilesList, captureFilesList, adminFilesList);
        CurrentEnvModule envModule = new CurrentEnvModule();
        PolicyFinder policyFinder = new PolicyFinder();
        Set policyModules = new HashSet();
        policyModules.add(policyModule);
        policyFinder.setModules(policyModules);
        AttributeFinder attributeFinder = new AttributeFinder();
        List attrModules = new ArrayList();
        attrModules.add(envModule);
        attributeFinder.setModules(attrModules);
        PDPConfig pdpConfig = new PDPConfig(attributeFinder, policyFinder, null);
        log.trace("INITEPCISPDPx ");
        epcispdp = new EPCISPDP(pdpConfig);
    }

    public synchronized void initCombiningAlg() {
        StandardCombiningAlgFactory factory = StandardCombiningAlgFactory.getFactory();
        final BaseCombiningAlgFactory newFactory;
        Set set = new HashSet();
        try {
            set = StandardCombiningAlgFactory.getStandardAlgorithms(PolicyMetaData.XACML_1_0_IDENTIFIER);
        } catch (UnknownIdentifierException ex) {
            log.fatal(null, ex);
        }
        Set algorithms = new HashSet();
        for (Object o : set) {
            try {
                algorithms.add(factory.createAlgorithm(new URI((String) o)));
            } catch (UnknownIdentifierException ex) {
                log.fatal(null, ex);
            } catch (URISyntaxException ex) {
                log.fatal(null, ex);
            }
        }
        newFactory = new BaseCombiningAlgFactory(algorithms);
        newFactory.addAlgorithm(new SCGroupPolicyAlg());
        newFactory.addAlgorithm(new SCGroupRuleDenyAlg());
        newFactory.addAlgorithm(new SCGroupRuleAcceptAlg());
        newFactory.addAlgorithm(new SCGroupRuleAlg());
        CombiningAlgFactory.setDefaultFactory(new CombiningAlgFactoryProxy() {

            @Override
            public CombiningAlgFactory getFactory() {
                return newFactory;
            }
        });
    }

    public synchronized void initFunction() {
        log.trace("init functions");
        FunctionFactoryProxy proxy = StandardFunctionFactory.getNewFactoryProxy();
        proxy.getConditionFactory().addFunction(new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_DENY));
        proxy.getConditionFactory().addFunction(new OneOrGlobalFunction(OneOrGlobalFunction.NAME_GLOBAL_PERMIT));
        FunctionFactory.setDefaultFactory(proxy);
        FunctionFactory factory = FunctionFactory.getTargetInstance();
        factory.addFunction(new RevertRegexpMatch());

    }

    public synchronized ResponseCtx evaluate(RequestCtx request) {
        return epcispdp.evaluate(request);
    }

    public synchronized ResponseCtx evaluate(EvaluationCtx context) {
        return epcispdp.evaluate(context);
    }

    public synchronized List getPolicies(String owner) {
        List res = new ArrayList();
        res.add(getQueryPolicy(owner));
        res.add(getCapturePolicy(owner));
        res.add(getAdminPolicy(owner));
        return res;
    }

    public synchronized void save(String identifier) {
        finderModule.save(identifier);
    }

    public synchronized boolean updateAPMSession(AccessPolicyManagerSession APMS) {
        return finderModule.updateAPMSession(APMS, this.epcispdp.getPolicyFinder());
    }

    public synchronized boolean updateAPMQuerySession(AccessPolicyManagerSession APMS) {
        return finderModule.updateAPMQuerySession(APMS, this.epcispdp.getPolicyFinder());
    }

    public synchronized boolean updateAPMCaptureSession(AccessPolicyManagerSession APMS) {
        return finderModule.updateAPMCaptureSession(APMS, this.epcispdp.getPolicyFinder());
    }

    public synchronized boolean updateAPMAdminSession(AccessPolicyManagerSession APMS) {
        return finderModule.updateAPMAdminSession(APMS, this.epcispdp.getPolicyFinder());
    }

    public synchronized boolean saveQueryPolicies(String identifier) {
        return finderModule.saveQueryPolicies(identifier);
    }

    public synchronized boolean saveCapturePolicies(String identifier) {
        return finderModule.saveCapturePolicies(identifier);
    }

    public synchronized boolean saveAdminPolicies(String identifier) {
        return finderModule.saveAdminPolicies(identifier);
    }

    public synchronized boolean updateQueryGroupName(String partnerID, String groupId, String value) {
        return finderModule.updateQueryGroupName(partnerID, groupId, value);
    }

    public synchronized boolean updateCaptureGroupName(String partnerID, String groupId, String value) {
        return finderModule.updateCaptureGroupName(partnerID, groupId, value);
    }

    public synchronized boolean updateAdminGroupName(String partnerID, String groupId, String value) {
        return finderModule.updateAdminGroupName(partnerID, groupId, value);
    }

    //#####################################################
    //################## QUERY MODULE #####################
    //#####################################################
    public synchronized OwnerPolicies getQueryPolicy(String owner) {
        return finderModule.getPolicies().getQueryPolicy(owner);
    }

    public synchronized boolean addQueryPolicy(OwnerPolicies ownerPolicies) {
        return finderModule.addQueryPolicy(ownerPolicies);
    }

    public synchronized boolean addQueryGroupPolicy(String identifier, GroupPolicy groupPolicy) {
        return finderModule.addQueryGroupPolicy(identifier, groupPolicy);
    }

    //####################################################
    //#################### FILTERS #######################
    //####################################################
    public synchronized boolean addQueryActionFilter(String identifier, String groupName, String actionName) {
        return finderModule.addQueryActionFilter(identifier, groupName, actionName);
    }

    public synchronized boolean addQueryUserFilter(String identifier, String groupName, String userName) {
        return finderModule.addQueryUserFilter(identifier, groupName, userName);
    }

    public synchronized boolean addQueryBizStepFilter(String identifier, String groupName, String bizStep) {
        return finderModule.addQueryBizStepFilter(identifier, groupName, bizStep);
    }

    public synchronized boolean addQueryEpcFilter(String identifier, String groupName, String epc) {
        return finderModule.addQueryEpcFilter(identifier, groupName, epc);
    }

    public synchronized boolean addQueryEventTimeFilter(String identifier, String groupName, List dates) {
        return finderModule.addQueryEventTimeFilter(identifier, groupName, dates);
    }

    public synchronized boolean addQueryRecordTimeFilter(String identifier, String groupName, List recordTimes) {
        return finderModule.addQueryRecordTimeFilter(identifier, groupName, recordTimes);
    }

    public synchronized boolean addQueryOperationFilter(String identifier, String groupName, String operation) {
        return finderModule.addQueryOperationFilter(identifier, groupName, operation);
    }

    public synchronized boolean addQueryEventTypeFilter(String identifier, String groupName, String eventType) {
        return finderModule.addQueryEventTypeFilter(identifier, groupName, eventType);
    }

    public synchronized boolean addQueryParentIdFilter(String identifier, String groupName, String parentId) {
        return finderModule.addQueryParentIdFilter(identifier, groupName, parentId);
    }

    public synchronized boolean addQueryChildEpcFilter(String identifier, String groupName, String childEpc) {
        return finderModule.addQueryChildEpcFilter(identifier, groupName, childEpc);
    }

    public synchronized boolean addQueryQuantityFilter(String identifier, String groupName, List quantities) {
        return finderModule.addQueryQuantityFilter(identifier, groupName, quantities);
    }

    public synchronized boolean addQueryReadPointFilter(String identifier, String groupName, String readPoint) {
        return finderModule.addQueryReadPointFilter(identifier, groupName, readPoint);
    }

    public synchronized boolean addQueryBizLocFilter(String identifier, String groupName, String bizLoc) {
        return finderModule.addQueryBizLocFilter(identifier, groupName, bizLoc);
    }

    public synchronized boolean addQueryBizTransFilter(String identifier, String groupName, String bizTrans) {
        return finderModule.addQueryBizTransFilter(identifier, groupName, bizTrans);
    }

    public synchronized boolean addQueryDispositionFilter(String identifier, String groupName, String disposition) {
        return finderModule.addQueryDispositionFilter(identifier, groupName, disposition);
    }

    public synchronized boolean addQueryExtensionFilter(String identifier, String groupName, String extensionName, String extensionType) {
        return finderModule.addQueryExtensionFilter(identifier, groupName, extensionName, extensionType);
    }

    public synchronized boolean addQueryExtensionFilter(String identifier, String groupName, List extensions, String extensionType) {
        return finderModule.addQueryExtensionFilter(identifier, groupName, extensions, extensionType);
    }

    public synchronized boolean addQueryMasterDataIdFilter(String identifier, String groupName, String masterDataId) {
        return finderModule.addQueryMasterDataIdFilter(identifier, groupName, masterDataId);
    }

    public synchronized boolean removeQueryActionFilter(String identifier, String groupName, String actionName) {
        return finderModule.removeQueryActionFilter(identifier, groupName, actionName);
    }

    public synchronized boolean removeQueryUserFilter(String identifier, String groupName, String userName) {
        return finderModule.removeQueryUserFilter(identifier, groupName, userName);
    }

    public synchronized boolean removeQueryBizStepFilter(String identifier, String groupName, String bizStep) {
        return finderModule.removeQueryBizStepFilter(identifier, groupName, bizStep);
    }

    public synchronized boolean removeQueryEpcFilter(String identifier, String groupName, String epc) {
        return finderModule.removeQueryEpcFilter(identifier, groupName, epc);
    }

    public synchronized boolean removeQueryEventTimeFilter(String identifier, String groupName, List dates) {
        return finderModule.removeQueryEventTimeFilter(identifier, groupName, dates);
    }

    public synchronized boolean removeQueryRecordTimeFilter(String identifier, String groupName, List recordTimes) {
        return finderModule.removeQueryRecordTimeFilter(identifier, groupName, recordTimes);
    }

    public synchronized boolean removeQueryOperationFilter(String identifier, String groupName, String operation) {
        return finderModule.removeQueryOperationFilter(identifier, groupName, operation);
    }

    public synchronized boolean removeQueryEventTypeFilter(String identifier, String groupName, String eventType) {
        return finderModule.removeQueryEventTypeFilter(identifier, groupName, eventType);
    }

    public synchronized boolean removeQueryParentIdFilter(String identifier, String groupName, String parentId) {
        return finderModule.removeQueryParentIdFilter(identifier, groupName, parentId);
    }

    public synchronized boolean removeQueryChildEpcFilter(String identifier, String groupName, String childEpc) {
        return finderModule.removeQueryChildEpcFilter(identifier, groupName, childEpc);
    }

    public synchronized boolean removeQueryQuantityFilter(String identifier, String groupName, List quantities) {
        return finderModule.removeQueryQuantityFilter(identifier, groupName, quantities);
    }

    public synchronized boolean removeQueryReadPointFilter(String identifier, String groupName, String readPoint) {
        return finderModule.removeQueryReadPointFilter(identifier, groupName, readPoint);
    }

    public synchronized boolean removeQueryBizLocFilter(String identifier, String groupName, String bizLoc) {
        return finderModule.removeQueryBizLocFilter(identifier, groupName, bizLoc);
    }

    public synchronized boolean removeQueryBizTransFilter(String identifier, String groupName, String bizTrans) {
        return finderModule.removeQueryBizTransFilter(identifier, groupName, bizTrans);
    }

    public synchronized boolean removeQueryDispositionFilter(String identifier, String groupName, String disposition) {
        return finderModule.removeQueryDispositionFilter(identifier, groupName, disposition);
    }

    public synchronized boolean removeQueryExtensionFilter(String identifier, String groupName, String extensionName, String extensionType) {
        return finderModule.removeQueryExtensionFilter(identifier, groupName, extensionName, extensionType);
    }

    public synchronized boolean removeQueryExtensionFilter(String identifier, String groupName, List extensions, String extensionType) {
        return finderModule.removeQueryExtensionFilter(identifier, groupName, extensions, extensionType);
    }

    public synchronized boolean removeQueryMasterDataIdFilter(String identifier, String groupName, String masterDataId) {
        return finderModule.removeQueryMasterDataIdFilter(identifier, groupName, masterDataId);
    }

    public synchronized boolean updateQueryPolicy(OwnerPolicies ownerPolicies) {
        return finderModule.updateQueryPolicy(ownerPolicies);
    }

    public synchronized boolean updateQueryPolicy(String identifier, GroupPolicy groupPolicy) {
        return finderModule.updateQueryPolicy(identifier, groupPolicy);
    }

    public synchronized boolean deleteQueryOwnerPolicy(OwnerPolicies policy) {
        return finderModule.deleteQueryOwnerPolicy(policy);
    }

    public synchronized boolean deleteQueryGroupPolicy(String identifier, GroupPolicy policy) {
        return finderModule.deleteQueryGroupPolicy(identifier, policy);
    }

    public synchronized boolean deleteQueryGroupPolicy(String identifier, String policyId) {
        return finderModule.deleteQueryGroupPolicy(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionUsers(String identifier, String policyId) {
        return finderModule.switchQueryPermissionUsers(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionBizSteps(String identifier, String policyId) {
        return finderModule.switchQueryPermissionBizSteps(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionEpcs(String identifier, String policyId) {
        return finderModule.switchQueryPermissionEpcs(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionEventTimes(String identifier, String policyId) {
        return finderModule.switchQueryPermissionEventTimes(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionRecordTimes(String identifier, String policyId) {
        return finderModule.switchQueryPermissionRecordTimes(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionOperations(String identifier, String policyId) {
        return finderModule.switchQueryPermissionOperations(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionEventTypes(String identifier, String policyId) {
        return finderModule.switchQueryPermissionEventTypes(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionParentIds(String identifier, String policyId) {
        return finderModule.switchQueryPermissionParentIds(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionChildEpcs(String identifier, String policyId) {
        return finderModule.switchQueryPermissionChildEpcs(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionQuantities(String identifier, String policyId) {
        return finderModule.switchQueryPermissionQuantities(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionReadPoints(String identifier, String policyId) {
        return finderModule.switchQueryPermissionReadPoints(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionBizLocs(String identifier, String policyId) {
        return finderModule.switchQueryPermissionBizLocs(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionBizTrans(String identifier, String policyId) {
        return finderModule.switchQueryPermissionBizTrans(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionDispositions(String identifier, String policyId) {
        return finderModule.switchQueryPermissionDispositions(identifier, policyId);
    }

    public synchronized boolean switchQueryPermissionExtensions(String identifier, String policyId, String extensionId) {
        return finderModule.switchQueryPermissionExtensions(identifier, policyId, extensionId);
    }

    public synchronized boolean switchQueryPermissionMasterDataIds(String identifier, String policyId) {
        return finderModule.switchQueryPermissionMasterDataIds(identifier, policyId);
    }

    //####################################################
    //################# CAPTURE MODULE ###################
    //####################################################
    public synchronized boolean switchCapturePermissionUsers(String identifier, String policyId) {
        return finderModule.switchCapturePermissionUsers(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionBizSteps(String identifier, String policyId) {
        return finderModule.switchCapturePermissionBizSteps(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionEpcs(String identifier, String policyId) {
        return finderModule.switchCapturePermissionEpcs(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionEventTimes(String identifier, String policyId) {
        return finderModule.switchCapturePermissionEventTimes(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionRecordTimes(String identifier, String policyId) {
        return finderModule.switchCapturePermissionRecordTimes(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionOperations(String identifier, String policyId) {
        return finderModule.switchCapturePermissionOperations(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionEventTypes(String identifier, String policyId) {
        return finderModule.switchCapturePermissionEventTypes(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionParentIds(String identifier, String policyId) {
        return finderModule.switchCapturePermissionParentIds(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionChildEpcs(String identifier, String policyId) {
        return finderModule.switchCapturePermissionChildEpcs(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionQuantities(String identifier, String policyId) {
        return finderModule.switchCapturePermissionQuantities(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionReadPoints(String identifier, String policyId) {
        return finderModule.switchCapturePermissionReadPoints(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionBizLocs(String identifier, String policyId) {
        return finderModule.switchCapturePermissionBizLocs(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionBizTrans(String identifier, String policyId) {
        return finderModule.switchCapturePermissionBizTrans(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionDispositions(String identifier, String policyId) {
        return finderModule.switchCapturePermissionDispositions(identifier, policyId);
    }

    public synchronized boolean switchCapturePermissionExtensions(String identifier, String policyId, String extensionId) {
        return finderModule.switchCapturePermissionExtensions(identifier, policyId, extensionId);
    }

    public synchronized boolean switchCapturePermissionMasterDataIds(String identifier, String policyId) {
        return finderModule.switchCapturePermissionMasterDataIds(identifier, policyId);
    }

    public synchronized OwnerPolicies getCapturePolicy(String owner) {
        return finderModule.getPolicies().getCapturePolicy(owner);
    }

    public synchronized boolean addCapturePolicy(OwnerPolicies ownerPolicies) {
        return finderModule.addCapturePolicy(ownerPolicies);
    }

    public synchronized boolean addCaptureGroupPolicy(String identifier, GroupPolicy groupPolicy) {
        return finderModule.addCaptureGroupPolicy(identifier, groupPolicy);
    }

    //####################################################
    //################## FILTERS #########################
    //####################################################
    public synchronized boolean addCaptureActionFilter(String identifier, String groupName, String actionName) {
        return finderModule.addCaptureActionFilter(identifier, groupName, actionName);
    }

    public synchronized boolean addCaptureUserFilter(String identifier, String groupName, String userName) {
        return finderModule.addCaptureUserFilter(identifier, groupName, userName);
    }

    public synchronized boolean addCaptureBizStepFilter(String identifier, String groupName, String bizStep) {
        return finderModule.addCaptureBizStepFilter(identifier, groupName, bizStep);
    }

    public synchronized boolean addCaptureEpcFilter(String identifier, String groupName, String epc) {
        return finderModule.addCaptureEpcFilter(identifier, groupName, epc);
    }

    public synchronized boolean addCaptureEventTimeFilter(String identifier, String groupName, List dates) {
        return finderModule.addCaptureEventTimeFilter(identifier, groupName, dates);
    }

    public synchronized boolean addCaptureRecordTimeFilter(String identifier, String groupName, List recordTimes) {
        return finderModule.addCaptureRecordTimeFilter(identifier, groupName, recordTimes);
    }

    public synchronized boolean addCaptureOperationFilter(String identifier, String groupName, String operation) {
        return finderModule.addCaptureOperationFilter(identifier, groupName, operation);
    }

    public synchronized boolean addCaptureEventTypeFilter(String identifier, String groupName, String eventType) {
        return finderModule.addCaptureEventTypeFilter(identifier, groupName, eventType);
    }

    public synchronized boolean addCaptureParentIdFilter(String identifier, String groupName, String parentId) {
        return finderModule.addCaptureParentIdFilter(identifier, groupName, parentId);
    }

    public synchronized boolean addCaptureChildEpcFilter(String identifier, String groupName, String childEpc) {
        return finderModule.addCaptureChildEpcFilter(identifier, groupName, childEpc);
    }

    public synchronized boolean addCaptureQuantityFilter(String identifier, String groupName, List quantities) {
        return finderModule.addCaptureQuantityFilter(identifier, groupName, quantities);
    }

    public synchronized boolean addCaptureReadPointFilter(String identifier, String groupName, String readPoint) {
        return finderModule.addCaptureReadPointFilter(identifier, groupName, readPoint);
    }

    public synchronized boolean addCaptureBizLocFilter(String identifier, String groupName, String bizLoc) {
        return finderModule.addCaptureBizLocFilter(identifier, groupName, bizLoc);
    }

    public synchronized boolean addCaptureBizTransFilter(String identifier, String groupName, String bizTrans) {
        return finderModule.addCaptureBizTransFilter(identifier, groupName, bizTrans);
    }

    public synchronized boolean addCaptureDispositionFilter(String identifier, String groupName, String disposition) {
        return finderModule.addCaptureDispositionFilter(identifier, groupName, disposition);
    }

    public synchronized boolean addCaptureExtensionFilter(String identifier, String groupName, String extensionName, String extensionType) {
        return finderModule.addCaptureExtensionFilter(identifier, groupName, extensionName, extensionType);
    }

    public synchronized boolean addCaptureExtensionFilter(String identifier, String groupName, List extensions, String extensionType) {
        return finderModule.addCaptureExtensionFilter(identifier, groupName, extensions, extensionType);
    }

    public synchronized boolean addCaptureMasterDataIdFilter(String identifier, String groupName, String masterDataId) {
        return finderModule.addCaptureMasterDataIdFilter(identifier, groupName, masterDataId);
    }

    public synchronized boolean removeCaptureActionFilter(String identifier, String groupName, String actionName) {
        return finderModule.removeCaptureActionFilter(identifier, groupName, actionName);
    }

    public synchronized boolean removeCaptureUserFilter(String identifier, String groupName, String userName) {
        return finderModule.removeCaptureUserFilter(identifier, groupName, userName);
    }

    public synchronized boolean removeCaptureBizStepFilter(String identifier, String groupName, String bizStep) {
        return finderModule.removeCaptureBizStepFilter(identifier, groupName, bizStep);
    }

    public synchronized boolean removeCaptureEpcFilter(String identifier, String groupName, String epc) {
        return finderModule.removeCaptureEpcFilter(identifier, groupName, epc);
    }

    public synchronized boolean removeCaptureEventTimeFilter(String identifier, String groupName, List dates) {
        return finderModule.removeCaptureEventTimeFilter(identifier, groupName, dates);
    }

    public synchronized boolean removeCaptureRecordTimeFilter(String identifier, String groupName, List recordTimes) {
        return finderModule.removeCaptureRecordTimeFilter(identifier, groupName, recordTimes);
    }

    public synchronized boolean removeCaptureOperationFilter(String identifier, String groupName, String operation) {
        return finderModule.removeCaptureOperationFilter(identifier, groupName, operation);
    }

    public synchronized boolean removeCaptureEventTypeFilter(String identifier, String groupName, String eventType) {
        return finderModule.removeCaptureEventTypeFilter(identifier, groupName, eventType);
    }

    public synchronized boolean removeCaptureParentIdFilter(String identifier, String groupName, String parentId) {
        return finderModule.removeCaptureParentIdFilter(identifier, groupName, parentId);
    }

    public synchronized boolean removeCaptureChildEpcFilter(String identifier, String groupName, String childEpc) {
        return finderModule.removeCaptureChildEpcFilter(identifier, groupName, childEpc);
    }

    public synchronized boolean removeCaptureQuantityFilter(String identifier, String groupName, List quantities) {
        return finderModule.removeCaptureQuantityFilter(identifier, groupName, quantities);
    }

    public synchronized boolean removeCaptureReadPointFilter(String identifier, String groupName, String readPoint) {
        return finderModule.removeCaptureReadPointFilter(identifier, groupName, readPoint);
    }

    public synchronized boolean removeCaptureBizLocFilter(String identifier, String groupName, String bizLoc) {
        return finderModule.removeCaptureBizLocFilter(identifier, groupName, bizLoc);
    }

    public synchronized boolean removeCaptureBizTransFilter(String identifier, String groupName, String bizTrans) {
        return finderModule.removeCaptureBizTransFilter(identifier, groupName, bizTrans);
    }

    public synchronized boolean removeCaptureDispositionFilter(String identifier, String groupName, String disposition) {
        return finderModule.removeCaptureDispositionFilter(identifier, groupName, disposition);
    }

    public synchronized boolean removeCaptureExtensionFilter(String identifier, String groupName, String extensionName, String extensionType) {
        return finderModule.removeCaptureExtensionFilter(identifier, groupName, extensionName, extensionType);
    }

    public synchronized boolean removeCaptureExtensionFilter(String identifier, String groupName, List extensions, String extensionType) {
        return finderModule.removeCaptureExtensionFilter(identifier, groupName, extensions, extensionType);
    }

    public synchronized boolean removeCaptureMasterDataIdFilter(String identifier, String groupName, String masterDataId) {
        return finderModule.removeCaptureMasterDataIdFilter(identifier, groupName, masterDataId);
    }

    public synchronized boolean updateCapturePolicy(OwnerPolicies ownerPolicies) {
        return finderModule.updateCapturePolicy(ownerPolicies);
    }

    public synchronized boolean updateCapturePolicy(String identifier, GroupPolicy groupPolicy) {
        return finderModule.updateCapturePolicy(identifier, groupPolicy);
    }

    public synchronized boolean deleteCaptureOwnerPolicy(OwnerPolicies policy) {
        return finderModule.deleteCaptureOwnerPolicy(policy);
    }

    public synchronized boolean deleteCaptureGroupPolicy(String identifier, GroupPolicy policy) {
        return finderModule.deleteCaptureGroupPolicy(identifier, policy);
    }

    public synchronized boolean deleteCaptureGroupPolicy(String identifier, String policyId) {
        return finderModule.deleteCaptureGroupPolicy(identifier, policyId);
    }

    //####################################################
    //############## ADMINISTRATION MODULE ###############
    //####################################################
    public synchronized OwnerPolicies getAdminPolicy(String owner) {
        return finderModule.getPolicies().getAdminPolicy(owner);
    }

    public synchronized boolean addAdminPolicy(OwnerPolicies ownerPolicies) {
        return finderModule.addAdminPolicy(ownerPolicies);
    }

    public synchronized boolean addAdminGroupPolicy(String identifier, GroupPolicy groupPolicy) {
        return finderModule.addAdminGroupPolicy(identifier, groupPolicy);
    }

    public synchronized boolean addAdminActionFilter(String identifier, String groupName, String actionName) {
        return finderModule.addAdminActionFilter(identifier, groupName, actionName);
    }

    public synchronized boolean addAdminUserFilter(String identifier, String groupName, String userName) {
        return finderModule.addAdminUserFilter(identifier, groupName, userName);
    }

    public synchronized boolean removeAdminActionFilter(String identifier, String groupName, String actionName) {
        return finderModule.removeAdminActionFilter(identifier, groupName, actionName);
    }

    public synchronized boolean removeAdminUserFilter(String identifier, String groupName, String userName) {
        return finderModule.removeAdminUserFilter(identifier, groupName, userName);
    }

    public synchronized boolean deleteAdminOwnerPolicy(OwnerPolicies policy) {
        return finderModule.deleteAdminOwnerPolicy(policy);
    }

    public synchronized boolean deleteAdminGroupPolicy(String identifier, GroupPolicy policy) {
        return finderModule.deleteAdminGroupPolicy(identifier, policy);
    }

    public synchronized boolean deleteAdminGroupPolicy(String identifier, String policyId) {
        return finderModule.deleteAdminGroupPolicy(identifier, policyId);
    }

    public synchronized boolean switchAdminPermissionUsers(String identifier, String policyId) {
        return finderModule.switchAdminPermissionUsers(identifier, policyId);
    }
}
