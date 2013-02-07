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
package fr.unicaen.iota.epcisphi.utils;

import fr.unicaen.iota.xacml.AccessPolicyManager;
import fr.unicaen.iota.xacml.finder.MyPolicyCollection;
import fr.unicaen.iota.xacml.finder.MyPolicyFinderModule;
import fr.unicaen.iota.xacml.policy.OwnerPolicies;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class MapSessions {

    // TODO: SLS public!
    public final static AccessPolicyManager APM;
    private static final Log log = LogFactory.getLog(MapSessions.class);

    static {
        APM = new AccessPolicyManager();
    }

    public static synchronized String AdminAPMtoString() {
        StringBuilder res = new StringBuilder();
        for (Object o : APM.getEpcispdp().getPolicyFinder().getModules()) {
            if (o instanceof MyPolicyFinderModule) {
                MyPolicyFinderModule mod = (MyPolicyFinderModule) o;
                MyPolicyCollection col = mod.getPolicies();
                Map map = col.getAdminPolicies();
                for (Object k : map.keySet()) {
                    String key = ((String) k);
                    res.append(key);
                    res.append(" \n");
                    OwnerPolicies ownerPolicies = (OwnerPolicies) map.get(k);
                }
            }
        }
        return res.toString();
    }
    public static HashMap<String, InterfaceHelper> APMSessions = new HashMap<String, InterfaceHelper>();

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
