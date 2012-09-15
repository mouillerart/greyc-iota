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

import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.PartnerInfo;
import fr.unicaen.iota.discovery.client.model.UserInfo;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import java.rmi.RemoteException;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class SessionLoader {

    private SessionLoader() {
    }
    private static final Log log = LogFactory.getLog(SessionLoader.class);

    public static synchronized String loadSession(String sessionId, DsClient dsClient, String userId, HttpSession session) {
        try {
            UserInfo uInfo = dsClient.userInfo(sessionId, userId);
            log.trace("#######################" + sessionId + " " + uInfo.getPartnerId());
            PartnerInfo pInfo = dsClient.partnerInfo(sessionId, uInfo.getPartnerId());
            session.setAttribute("uInfo", uInfo);
            session.setAttribute("pInfo", pInfo);
            session.setAttribute("session-id", sessionId);
        } catch (EnhancedProtocolException ex) {
            log.error(null, ex);
            return "?message=" + ex.getMessage();
        } catch (RemoteException e) {
            log.error(null, e);
            return "?message=Internal server error";
        }
        return "";
    }

    public static void clearSession(HttpSession session) {
        session.setAttribute("session-id", null);
        session.setAttribute("uInfo", null);
        session.setAttribute("pInfo", null);
    }
}
