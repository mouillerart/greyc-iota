/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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

import fr.unicaen.iota.ypsilon.client.YPSilonClient;
import fr.unicaen.iota.ypsilon.client.model.UserInfoOut;
import fr.unicaen.iota.ypsilon.client.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ypsilon.client.soap.SecurityExceptionResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionLoader {

    private SessionLoader() {
    }
    private static final Log LOG = LogFactory.getLog(SessionLoader.class);

    public static synchronized String loadSession(String sessionId, String userId, HttpSession session) throws SecurityExceptionResponse {
        try {
            YPSilonClient client = new YPSilonClient(Constants.YPSILON_ADDRESS, Constants.PKS_FILENAME, Constants.PKS_PASSWORD,
                    Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
            UserInfoOut uInfo = client.userInfo(sessionId, userId);
            session.setAttribute("uInfo", uInfo);
            /*
             * TODO PartnerInfo pInfo = YPSilonClient.partnerInfo(sessionId,
             * uInfo.getPartnerId()); session.setAttribute("pInfo", pInfo);
             */
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
        // TODO session.setAttribute("pInfo", null);
    }
}
