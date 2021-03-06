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
package fr.unicaen.iota.eta.callback.filter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The
 * <code>CallbackOperationsBackendSQL</code> provides methode to retrieve the
 * user ID associated with a subscription.
 */
public class CallbackOperationsBackendSQL {

    private static final Log LOG = LogFactory.getLog(CallbackOperationsBackendSQL.class);

    /**
     * Fetch user ID corresponding to subscription ID from the database.
     *
     * @param session The CallbackOperationsSession wrapping a database
     * connection.
     * @param subscriptionID The subscription ID.
     * @return The user ID corresponding to the subscription.
     * @throws SQLException An error involving the database occurred.
     */
    public String fetchUser(final CallbackOperationsSession session, final String subscriptionID)
            throws SQLException {
        String select = "SELECT user FROM subscription WHERE subscriptionid=?";
        PreparedStatement stmt = session.getPreparedStatement(select);
        stmt.setString(1, subscriptionID);
        LOG.debug("SQL:" + select);
        LOG.debug("     param1 = " + subscriptionID);
        ResultSet rs = stmt.executeQuery();
        if (rs.first()) {
            return rs.getString("user");
        }
        return "";
    }
}
