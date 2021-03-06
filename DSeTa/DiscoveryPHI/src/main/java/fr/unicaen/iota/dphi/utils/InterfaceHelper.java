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
package fr.unicaen.iota.dphi.utils;

import fr.unicaen.iota.xacml.AccessPolicyManagerSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class InterfaceHelper {

    private static final Log log = LogFactory.getLog(InterfaceHelper.class);
    // TODO @SLS public field!
    public AccessPolicyManagerSession APMSession;

    public InterfaceHelper(String owner) {
        log.info("InterfaceHelper instanciated");
        APMSession = MapSessions.APM.getInstance(owner);
    }

    public void updateAPM() {
        MapSessions.APM.updateAPMSession(APMSession);
    }

    public void updateQueryAPM() {
        MapSessions.APM.updateAPMQuerySession(APMSession);
    }

    public void updateCaptureAPM() {
        MapSessions.APM.updateAPMCaptureSession(APMSession);
    }

    public void updateAdminAPM() {
        MapSessions.APM.updateAPMAdminSession(APMSession);
    }

    public void reload() {
        log.info("RELAOD DSPDP");
        APMSession.initDSPDP(APMSession.getOwner());
        APMSession.initFinderModule();
    }
}
