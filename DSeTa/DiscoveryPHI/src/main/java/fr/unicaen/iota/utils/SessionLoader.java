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
package fr.unicaen.iota.utils;

import fr.unicaen.iota.ypsilon.client.YPSilonClient;
import fr.unicaen.iota.ypsilon.client.model.UserInfoOut;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ypsilon.client.soap.SecurityExceptionResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class SessionLoader {

    private SessionLoader() {
    }
    private static final Log LOG = LogFactory.getLog(SessionLoader.class);

    public static synchronized String loadSession(String sessionId, YPSilonClient ypsilonClient, String userId, HttpSession session) {
        try {
            UserInfoOut uInfo = ypsilonClient.userInfo(sessionId, userId);
            session.setAttribute("uInfo", uInfo);
            session.setAttribute("session-id", sessionId);
            return "";
        } catch (ImplementationExceptionResponse ex) {
            LOG.error("An error occurred", ex);
            return "?message=" + ex.getMessage();
        } catch (SecurityExceptionResponse ex) {
            LOG.error("A security error occurred", ex);
            return "?message=" + ex.getMessage();
        }
    }

    public static void clearSession(HttpSession session) {
        session.setAttribute("session-id", null);
        session.setAttribute("uInfo", null);
    }
}
