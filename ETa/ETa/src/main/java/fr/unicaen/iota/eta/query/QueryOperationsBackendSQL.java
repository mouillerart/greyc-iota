/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
 *  Copyright © 2007       ETH Zurich
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
 * Derived from org.fosstrak.epcis.repository.query.QueryOperationsBackendSQL
 */
package fr.unicaen.iota.eta.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The QueryOperationsBackendSQL uses basic SQL statements (actually
 * <code>PreparedStatement</code>s) to implement the QueryOperationsBackend
 * interface.
 */
public class QueryOperationsBackendSQL implements QueryOperationsBackend {

    private static final Log LOG = LogFactory.getLog(QueryOperationsBackendSQL.class);
    private static final String SQL_EXISTS_SUBSCRIPTION = "SELECT EXISTS (SELECT subscriptionid FROM subscription WHERE subscriptionid=?)";
    private static final String SQL_GET_SUBSCRIPTION_IDS = "SELECT subscriptionid FROM subscription";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean fetchExistsSubscriptionId(final QueryOperationsSession session, final String subscriptionID)
            throws SQLException {
        PreparedStatement stmt = session.getPreparedStatement(SQL_EXISTS_SUBSCRIPTION);
        stmt.setString(1, subscriptionID);
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: " + SQL_EXISTS_SUBSCRIPTION);
            LOG.debug("     param1 = " + subscriptionID);
        }
        ResultSet rs = stmt.executeQuery();
        rs.first();
        return rs.getBoolean(1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void storeSubscription(final QueryOperationsSession session, String subscrId, String dest, String user)
            throws SQLException {
        String insert = "INSERT INTO subscription (subscriptionid, address, user) VALUES ((?), (?), (?))";
        PreparedStatement stmt = session.getConnection().prepareStatement(insert);
        LOG.debug("QUERY: " + insert);
        stmt.setString(1, subscrId);
        LOG.debug("       query param 1: " + subscrId);
        stmt.setString(2, dest);
        LOG.debug("       query param 2: " + dest);
        stmt.setString(3, user);
        LOG.debug("       query param 3: " + user);
        stmt.executeUpdate();
        session.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteSubscription(final QueryOperationsSession session, String subscrId) throws SQLException {
        String delete = "DELETE FROM subscription WHERE subscriptionid=?";
        PreparedStatement ps = session.getConnection().prepareStatement(delete);
        ps.setString(1, subscrId);
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL: " + delete);
            LOG.debug("     param1 = " + subscrId);
        }
        ps.executeUpdate();
        session.commit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSubscriptionIds(final QueryOperationsSession session)
            throws SQLException {
        List<String> subscriptionIds = new ArrayList<String>();
        PreparedStatement stmt = session.getPreparedStatement(SQL_GET_SUBSCRIPTION_IDS);
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            String subscrId = rs.getString("subscriptionid");
            subscriptionIds.add(subscrId);
        }
        return subscriptionIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public QueryOperationsSession openSession(final DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        LOG.debug("Database connection for session established");
        return new QueryOperationsSession(connection);
    }
}
