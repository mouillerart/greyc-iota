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
package fr.unicaen.iota.utils;

import fr.unicaen.iota.xacml.AccessPolicyManager;
import fr.unicaen.iota.xacml.finder.MyPolicyCollection;
import fr.unicaen.iota.xacml.finder.MyPolicyFinderModule;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class MapSessions {

    private MapSessions() {
    }
    private final static Log log = LogFactory.getLog(MapSessions.class);
    // TODO: SLS public hence not procected by synchronized
    public final static AccessPolicyManager APM = new AccessPolicyManager();
    private final static Map<String, InterfaceHelper> APMSessions = new HashMap<String, InterfaceHelper>();

    public static synchronized String AdminAPMtoString() {
        StringBuilder res = new StringBuilder();
        for (Object o : APM.getDspdp().getPolicyFinder().getModules()) {
            if (o instanceof MyPolicyFinderModule) {
                MyPolicyFinderModule mod = (MyPolicyFinderModule) o;
                MyPolicyCollection col = mod.getPolicies();
                Map<String, OwnerPolicies> map = col.getAdminPolicies();
                for (String key : map.keySet()) {
                    res.append(key);
                    res.append(" \n");
                    OwnerPolicies ownerPolicies = map.get(key);
                    //TODO: SLS and?
                }
            }
        }
        return res.toString();
    }

    public static synchronized InterfaceHelper getAPMSession(String sessionId, String partnerId) {
        if (APMSessions.containsKey(sessionId)) {
            return APMSessions.get(sessionId);
        } else {
            InterfaceHelper IH = new InterfaceHelper(partnerId);
            APMSessions.put(sessionId, IH);
            return IH;
        }
    }

    public static synchronized void releaseSession(String sessionId) {
        APMSessions.remove(sessionId);
    }

    public static synchronized void init() {
        APM.init();
    }
}
