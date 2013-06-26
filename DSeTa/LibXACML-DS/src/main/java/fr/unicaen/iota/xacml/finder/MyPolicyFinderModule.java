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
 * Derived from com.sun.xacml.support.finder.FilePolicyModule
 */
/*
 * Copyright 2003-2006 Sun Microsystems, Inc. All Rights Reserved.
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
package fr.unicaen.iota.xacml.finder;

import com.sun.xacml.AbstractPolicy;
import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.ParsingException;
import com.sun.xacml.finder.PolicyFinder;
import com.sun.xacml.finder.PolicyFinderModule;
import com.sun.xacml.finder.PolicyFinderResult;
import com.sun.xacml.support.finder.TopLevelPolicyException;
import fr.unicaen.iota.xacml.AccessPolicyManagerSession;
import fr.unicaen.iota.xacml.configuration.Configuration;
import fr.unicaen.iota.xacml.policy.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This module represents a collection of files containing polices, each of
 * which will be searched through when trying to find a policy that is
 * applicable to a specific request. It does not support policy references. <p>
 * Note that this class used to be provided in the
 * <code>com.sun.xacml.finder.impl</code> package with a warning that it would
 * move out of the core packages eventually. This is partly because this class
 * doesn't represent standard functionality, and partly because it isn't
 * designed to be generally useful as anything more than an example. Because so
 * many people have used this class, however, it stayed in place until the 2.0
 * release. <p> As of the 2.0 release, you may still use this class (in its new
 * location), but you are encouraged to migrate to the new support modules that
 * are much richer and designed for general-purpose use. Also, note that the
 * <code>loadPolicy</code> methods that used to be available from this class
 * have been removed. That functionality has been replaced by the much more
 * useful
 * <code>PolicyReader</code> class. If you need to load policies directly, you
 * should consider that new class.
 *
 * @since 1.0
 * @author Seth Proctor
 */
public class MyPolicyFinderModule extends PolicyFinderModule {

    private static final Log log = LogFactory.getLog(MyPolicyFinderModule.class);
    // the schema file we're using, if any
    private File schemaFile = null;
    // the filenames for the files we'll load
    private Set queryFileNames;
    private Set captureFileNames;
    private Set administrationFileNames;
    // the actual loaded policies
    private MyPolicyCollection policies;

    public MyPolicyCollection getPolicies() {
        return policies;
    }

    /**
     * Constructor which retrieves the schema file to validate policies against
     * from the
     * <code>PolicyReader.POLICY_SCHEMA_PROPERTY</code>. If the retrieved
     * property is null, then no schema validation will occur.
     */
    public MyPolicyFinderModule() {
        queryFileNames = new HashSet();
        captureFileNames = new HashSet();
        administrationFileNames = new HashSet();
        policies = new MyPolicyCollection();

        String schemaName =
                System.getProperty(MyPolicyReader.POLICY_SCHEMA_PROPERTY);

        if (schemaName != null) {
            schemaFile = new File(schemaName);
        }
    }

    /**
     * Constructor that uses the specified
     * <code>File</code> as the schema file for XML validation. If schema
     * validation is not desired, a null value should be used.
     *
     * @param schemaFile the schema file to validate policies against, or null
     * if schema validation is not desired.
     */
    public MyPolicyFinderModule(File schemaFile) {
        queryFileNames = new HashSet();
        captureFileNames = new HashSet();
        administrationFileNames = new HashSet();
        policies = new MyPolicyCollection();
        this.schemaFile = schemaFile;
    }

    /**
     * Constructor that uses the specified
     * <code>String</code> as the schema file for XML validation. If schema
     * validation is not desired, a null value should be used.
     *
     * @param schemaFile the schema file to validate policies against, or null
     * if schema validation is not desired.
     */
    public MyPolicyFinderModule(String schemaFile) {
        this((schemaFile != null) ? new File(schemaFile) : null);
    }

    /**
     * Constructor that specifies a set of initial policy files to use. This
     * retrieves the schema file to validate policies against from the
     * <code>PolicyReader.POLICY_SCHEMA_PROPERTY</code>. If the retrieved
     * property is null, then no schema validation will occur.
     *
     * @param queryFileNames a
     * <code>List</code> of
     * <code>String</code>s that identify policy files
     */
    public MyPolicyFinderModule(List queryFileNames, List captureFileNames, List adminFileNames) {
        this();
        if (queryFileNames != null) {
            this.queryFileNames.addAll(queryFileNames);
        }
        if (queryFileNames != null) {
            this.captureFileNames.addAll(captureFileNames);
        }
        if (adminFileNames != null) {
            this.administrationFileNames.addAll(adminFileNames);
        }
    }

    /**
     * Constructor that specifies a set of initial policy files to use and the
     * schema file used to validate the policies. If schema validation is not
     * desired, a null value should be used.
     *
     * @param queryFileNames a
     * <code>List</code> of
     * <code>String</code>s that identify policy files
     * @param schemaFile the schema file to validate policies against, or null
     * if schema validation is not desired.
     */
    public MyPolicyFinderModule(List queryFileNames, List captureFileNames, List adminFileNames, String schemaFile) {
        this(schemaFile);
        if (queryFileNames != null) {
            this.queryFileNames.addAll(queryFileNames);
        }
        if (captureFileNames != null) {
            this.captureFileNames.addAll(captureFileNames);
        }
        if (adminFileNames != null) {
            this.administrationFileNames.addAll(adminFileNames);
        }
    }

    @Override
    public boolean isRequestSupported() {
        return true;
    }

    @Override
    public void init(PolicyFinder finder) {
        log.trace("Initialize MyPolicyFinderModule");
        //QUERY
        MyPolicyReader reader = new MyPolicyReader(finder, schemaFile);
        Iterator it = queryFileNames.iterator();
        while (it.hasNext()) {
            String fname = (String) (it.next());
            log.trace("->[Initialize MyPolicyFinderModule][QueryModule][LoadFile]" + fname);
            try {
                AbstractPolicy policy = reader.readPolicy(new FileInputStream(fname));
                OwnerPolicies ownerPolicies = (OwnerPolicies) policy;
                for (Object o : ownerPolicies.getPolicies()) {
                    if (o instanceof GroupPolicy) {
                        GroupPolicy gp = (GroupPolicy) o;
                        gp.setType(Module.queryModule);
                    }
                }
                ownerPolicies.setType(Module.queryModule);
                addQueryPolicy(ownerPolicies);
            } catch (FileNotFoundException fnfe) {
                if (log.isWarnEnabled()) {
                    log.warn("File couldn't be read: " + fname, fnfe);
                }
            } catch (ParsingException pe) {
                if (log.isWarnEnabled()) {
                    log.warn("Error reading policy from file " + fname, pe);
                }
            }
        }

        //CAPTURE
        Iterator it2 = captureFileNames.iterator();
        while (it2.hasNext()) {
            String fname = (String) (it2.next());
            log.trace("->[Initialize MyPolicyFinderModule][CaptureModule][LoadFile]" + fname);
            try {
                AbstractPolicy policy = reader.readPolicy(new FileInputStream(fname));
                OwnerPolicies ownerPolicies = (OwnerPolicies) policy;
                for (Object o : ownerPolicies.getPolicies()) {
                    if (o instanceof GroupPolicy) {
                        GroupPolicy gp = (GroupPolicy) o;
                        gp.setType(Module.captureModule);
                    }
                }
                ownerPolicies.setType(Module.captureModule);
                policies.addCapturePolicy(ownerPolicies);
            } catch (FileNotFoundException fnfe) {
                if (log.isWarnEnabled()) {
                    log.warn("File couldn't be read: " + fname, fnfe);
                }
            } catch (ParsingException pe) {
                if (log.isWarnEnabled()) {
                    log.warn("Error reading policy from file " + fname, pe);
                }
            }
        }

        //CAPTURE
        Iterator it3 = administrationFileNames.iterator();
        while (it3.hasNext()) {
            String fname = (String) (it3.next());
            log.trace("->[Initialize MyPolicyFinderModule][AdminModule][LoadFile]" + fname);
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
                policies.addAdminPolicy(ownerPolicies);
            } catch (FileNotFoundException fnfe) {
                if (log.isWarnEnabled()) {
                    log.warn("File couldn't be read: " + fname, fnfe);
                }
            } catch (ParsingException pe) {
                if (log.isWarnEnabled()) {
                    log.warn("Error reading policy from file " + fname, pe);
                }
            }
        }

    }

    @Override
    public PolicyFinderResult findPolicy(EvaluationCtx context) {
        try {
            OwnerPolicies policy = (OwnerPolicies) policies.getPolicy(context);
            if (policy == null) {
                return new PolicyFinderResult();
            } else {
                return new PolicyFinderResult(policy);
            }

        } catch (TopLevelPolicyException tlpe) {
            return new PolicyFinderResult(tlpe.getStatus());
        }
    }

    public void save(String identifier) {
        getPolicies().save(identifier);
    }

    public boolean saveQueryPolicies(String identifier) {
        return getPolicies().saveQueryPolicies(identifier);
    }

    public boolean saveCapturePolicies(String identifier) {
        return getPolicies().saveCapturePolicies(identifier);
    }

    public boolean saveAdminPolicies(String identifier) {
        return getPolicies().saveAdminPolicies(identifier);
    }

    public boolean updateQueryGroupName(String ownerID, String groupId, String value) {
        return policies.updateQueryGroupName(ownerID, groupId, value);
    }

    public boolean updateCaptureGroupName(String ownerID, String groupId, String value) {
        return policies.updateCaptureGroupName(ownerID, groupId, value);
    }

    public boolean updateAdminGroupName(String ownerID, String groupId, String value) {
        return policies.updateAdminGroupName(ownerID, groupId, value);
    }

    //##################################################
    //################# QUERY MODULE ###################
    //##################################################
    public boolean addQueryPolicy(OwnerPolicies policy) {
        return policies.addQueryPolicy(policy);
    }

    public boolean updateQueryPolicy(OwnerPolicies policy) {
        return policies.updateQueryPolicy(policy);
    }

    public boolean updateQueryPolicy(String identifier, GroupPolicy groupPolicy) {
        return policies.updateQueryPolicy(groupPolicy, identifier);
    }

    public boolean deleteQueryOwnerPolicy(OwnerPolicies policy) {
        return policies.deleteQueryOwnerPolicy(policy);
    }

    public boolean deleteQueryGroupPolicy(String identifier, GroupPolicy policy) {
        return policies.deleteQueryGroupPolicy(identifier, policy.getId().toString());
    }

    public boolean deleteQueryGroupPolicy(String identifier, String policy) {
        return policies.deleteQueryGroupPolicy(identifier, policy);
    }

    public boolean addQueryGroupPolicy(String identifier, GroupPolicy groupPolicy) {
        return policies.addQueryGroupPolicy(groupPolicy, identifier);
    }

    public boolean addQueryUserFilter(String identifier, String groupName, String userName) {
        return policies.addQueryFilter(identifier, groupName, userName, SCgroupRule.RULEFILTER);
    }

    public boolean addQueryBizStepFilter(String identifier, String groupName, String bizStep) {
        return policies.addQueryFilter(identifier, groupName, bizStep, SCBizStepRule.RULEFILTER);
    }

    public boolean addQueryEpcFilter(String identifier, String groupName, String epc) {
        return policies.addQueryFilter(identifier, groupName, epc, SCEPCsRule.RULEFILTER);
    }

    public boolean addQueryEventTypeFilter(String identifier, String groupName, String eventType) {
        return policies.addQueryFilter(identifier, groupName, eventType, SCEventTypeRule.RULEFILTER);
    }

    public boolean addQueryEventTimeFilter(String identifier, String groupName, List dates) {
        return policies.addQueryFilter(identifier, groupName, dates, SCEventTimeRule.RULEFILTER);
    }

    public boolean removeQueryUserFilter(String identifier, String groupName, String userName) {
        return policies.removeQueryFilter(identifier, groupName, userName, SCgroupRule.RULEFILTER);
    }

    public boolean removeQueryBizStepFilter(String identifier, String groupName, String bizStep) {
        return policies.removeQueryFilter(identifier, groupName, bizStep, SCBizStepRule.RULEFILTER);
    }

    public boolean removeQueryEpcFilter(String identifier, String groupName, String epc) {
        return policies.removeQueryFilter(identifier, groupName, epc, SCEPCsRule.RULEFILTER);
    }

    public boolean removeQueryEventTypeFilter(String identifier, String groupName, String eventType) {
        return policies.removeQueryFilter(identifier, groupName, eventType, SCEventTypeRule.RULEFILTER);
    }

    public boolean removeQueryEventTimeFilter(String identifier, String groupName, List dates) {
        return policies.removeQueryFilter(identifier, groupName, dates, SCEventTimeRule.RULEFILTER);
    }

    public boolean addQueryActionFilter(String identifier, String groupName, String actionName) {
        return policies.addQueryActionFilter(identifier, groupName, actionName);
    }

    public boolean removeQueryActionFilter(String identifier, String groupName, String actionName) {
        return policies.removeQueryActionFilter(identifier, groupName, actionName);
    }

    public boolean switchQueryPermissionUsers(String identifier, String policyId) {
        return policies.switchQueryPermissionUsers(identifier, policyId);
    }

    public boolean switchQueryPermissionBizSteps(String identifier, String policyId) {
        return policies.switchQueryPermissionBizSteps(identifier, policyId);
    }

    public boolean switchQueryPermissionEpcs(String identifier, String policyId) {
        return policies.switchQueryPermissionEpcs(identifier, policyId);
    }

    public boolean switchQueryPermissionEventTypes(String identifier, String policyId) {
        return policies.switchQueryPermissionEventTypes(identifier, policyId);
    }

    public boolean switchQueryPermissionEventTimes(String identifier, String policyId) {
        return policies.switchQueryPermissionEventTimes(identifier, policyId);
    }

    //####################################################
    //################# CAPTURE MODULE ###################
    //####################################################
    public boolean switchCapturePermissionUsers(String identifier, String policyId) {
        return policies.switchCapturePermissionUsers(identifier, policyId);
    }

    public boolean switchCapturePermissionBizSteps(String identifier, String policyId) {
        return policies.switchCapturePermissionBizSteps(identifier, policyId);
    }

    public boolean switchCapturePermissionEpcs(String identifier, String policyId) {
        return policies.switchCapturePermissionEpcs(identifier, policyId);
    }

    public boolean switchCapturePermissionEventTypes(String identifier, String policyId) {
        return policies.switchCapturePermissionEventTypes(identifier, policyId);
    }

    public boolean switchCapturePermissionEventTimes(String identifier, String policyId) {
        return policies.switchCapturePermissionEventTimes(identifier, policyId);
    }

    public boolean addCapturePolicy(OwnerPolicies policy) {
        return policies.addCapturePolicy(policy);
    }

    public boolean updateCapturePolicy(OwnerPolicies policy) {
        return policies.updateCapturePolicy(policy);
    }

    public boolean updateCapturePolicy(String identifier, GroupPolicy groupPolicy) {
        return policies.updateCapturePolicy(groupPolicy, identifier);
    }

    public boolean deleteCaptureOwnerPolicy(OwnerPolicies policy) {
        return policies.deleteCaptureOwnerPolicy(policy);
    }

    public boolean deleteCaptureGroupPolicy(String identifier, GroupPolicy policy) {
        return policies.deleteCaptureGroupPolicy(identifier, policy.getId().toString());
    }

    public boolean deleteCaptureGroupPolicy(String identifier, String policy) {
        return policies.deleteCaptureGroupPolicy(identifier, policy);
    }

    public boolean addCaptureGroupPolicy(String identifier, GroupPolicy groupPolicy) {
        return policies.addCaptureGroupPolicy(groupPolicy, identifier);
    }

    public boolean addCaptureUserFilter(String identifier, String groupName, String userName) {
        return policies.addCaptureFilter(identifier, groupName, userName, SCgroupRule.RULEFILTER);
    }

    public boolean addCaptureBizStepFilter(String identifier, String groupName, String bizStep) {
        return policies.addCaptureFilter(identifier, groupName, bizStep, SCBizStepRule.RULEFILTER);
    }

    public boolean addCaptureEpcFilter(String identifier, String groupName, String epc) {
        return policies.addCaptureFilter(identifier, groupName, epc, SCEPCsRule.RULEFILTER);
    }

    public boolean addCaptureEventTypeFilter(String identifier, String groupName, String eventType) {
        return policies.addCaptureFilter(identifier, groupName, eventType, SCEventTypeRule.RULEFILTER);
    }

    public boolean addCaptureEventTimeFilter(String identifier, String groupName, List dates) {
        return policies.addCaptureFilter(identifier, groupName, dates, SCEventTimeRule.RULEFILTER);
    }

    public boolean removeCaptureUserFilter(String identifier, String groupName, String userName) {
        return policies.removeCaptureFilter(identifier, groupName, userName, SCgroupRule.RULEFILTER);
    }

    public boolean removeCaptureBizStepFilter(String identifier, String groupName, String bizStep) {
        return policies.removeCaptureFilter(identifier, groupName, bizStep, SCBizStepRule.RULEFILTER);
    }

    public boolean removeCaptureEpcFilter(String identifier, String groupName, String epc) {
        return policies.removeCaptureFilter(identifier, groupName, epc, SCEPCsRule.RULEFILTER);
    }

    public boolean removeCaptureEventTypeFilter(String identifier, String groupName, String eventType) {
        return policies.removeCaptureFilter(identifier, groupName, eventType, SCEventTypeRule.RULEFILTER);
    }

    public boolean removeCaptureEventTimeFilter(String identifier, String groupName, List dates) {
        return policies.removeCaptureFilter(identifier, groupName, dates, SCEventTimeRule.RULEFILTER);
    }

    public boolean addCaptureActionFilter(String identifier, String groupName, String actionName) {
        return policies.addCaptureActionFilter(identifier, groupName, actionName);
    }

    public boolean removeCaptureActionFilter(String identifier, String groupName, String actionName) {
        return policies.removeCaptureActionFilter(identifier, groupName, actionName);
    }

    //##################################################
    //############# ADMINISTRATION MODULE ##############
    //##################################################
    public boolean addAdminPolicy(OwnerPolicies policy) {
        return policies.addAdminPolicy(policy);
    }

    public boolean deleteAdminOwnerPolicy(OwnerPolicies policy) {
        return policies.deleteAdminOwnerPolicy(policy);
    }

    public boolean deleteAdminGroupPolicy(String identifier, GroupPolicy policy) {
        return policies.deleteAdminGroupPolicy(identifier, policy.getId().toString());
    }

    public boolean deleteAdminGroupPolicy(String identifier, String policy) {
        return policies.deleteAdminGroupPolicy(identifier, policy);
    }

    public boolean addAdminGroupPolicy(String identifier, GroupPolicy groupPolicy) {
        return policies.addAdminGroupPolicy(groupPolicy, identifier);
    }

    public boolean addAdminUserFilter(String identifier, String groupName, String userName) {
        return policies.addAdminFilter(identifier, groupName, userName, SCgroupRule.RULEFILTER);
    }

    public boolean removeAdminUserFilter(String identifier, String groupName, String userName) {
        return policies.removeAdminFilter(identifier, groupName, userName, SCgroupRule.RULEFILTER);
    }

    public boolean addAdminActionFilter(String identifier, String groupName, String actionName) {
        return policies.addAdminActionFilter(identifier, groupName, actionName);
    }

    public boolean removeAdminActionFilter(String identifier, String groupName, String actionName) {
        return policies.removeAdminActionFilter(identifier, groupName, actionName);
    }

    public boolean switchAdminPermissionUsers(String identifier, String policyId) {
        return policies.switchAdminPermissionUsers(identifier, policyId);
    }

    public boolean updateAPMSession(AccessPolicyManagerSession APMS, PolicyFinder policyFinder) {
        MyPolicyReader reader = new MyPolicyReader(policyFinder, schemaFile);
        return policies.updateAPMSession(APMS, reader);
    }

    public boolean updateAPMQuerySession(AccessPolicyManagerSession APMS, PolicyFinder policyFinder) {
        OwnerPolicies ownerPolicies = reloadPolicy(Configuration.QUERY_POLICIES_DIRECTORY + APMS.getOwner() + ".xml", policyFinder);
        return policies.updateAPMQuerySession(APMS, ownerPolicies);
    }

    public boolean updateAPMCaptureSession(AccessPolicyManagerSession APMS, PolicyFinder policyFinder) {
        OwnerPolicies ownerPolicies = reloadPolicy(Configuration.CAPTURE_POLICIES_DIRECTORY + APMS.getOwner() + ".xml", policyFinder);
        return policies.updateAPMCaptureSession(APMS, ownerPolicies);
    }

    public boolean updateAPMAdminSession(AccessPolicyManagerSession APMS, PolicyFinder policyFinder) {
        OwnerPolicies ownerPolicies = reloadPolicy(Configuration.ADMIN_POLICIES_DIRECTORY + APMS.getOwner() + ".xml", policyFinder);
        return policies.updateAPMAdminSession(APMS, ownerPolicies);
    }

    private OwnerPolicies reloadPolicy(String filename, PolicyFinder finder) {
        try {
            MyPolicyReader reader = new MyPolicyReader(finder, schemaFile);
            AbstractPolicy policy = reader.readPolicy(new FileInputStream(filename));
            OwnerPolicies ownerPolicies = (OwnerPolicies) policy;
            for (Object o : ownerPolicies.getPolicies()) {
                if (o instanceof GroupPolicy) {
                    GroupPolicy gp = (GroupPolicy) o;
                    gp.setType(Module.queryModule);
                }
            }
            ownerPolicies.setType(Module.queryModule);
            return ownerPolicies;
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        } catch (ParsingException ex) {
            log.fatal(null, ex);
        }
        return null;
    }
}
