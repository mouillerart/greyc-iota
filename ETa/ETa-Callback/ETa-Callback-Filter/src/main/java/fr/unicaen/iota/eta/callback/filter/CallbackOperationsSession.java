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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The
 * <code>CallbackOperationsSession</code> maintains SQL queries. A
 * <code>PreparedStatements</code> is created and stored on first used.
 */
public class CallbackOperationsSession {

    private static final Log LOG = LogFactory.getLog(CallbackOperationsSession.class);
    private Connection connection;
    private Map<String, PreparedStatement> namedStatements = new HashMap<String, PreparedStatement>();

    public CallbackOperationsSession(final Connection connection) {
        this.connection = connection;
    }

    /**
     * Lazy instantiation of prepared statements: the PreparedStatement is
     * created when it is first used by the application and is then cached here.
     *
     * @param sql The SQL query to instantiate.
     * @return The
     * <code>PreparedStatement</code> associated to the SQL query.
     * @throws SQLException An error involving the database occurred
     */
    public PreparedStatement getPreparedStatement(final String sql) throws SQLException {
        PreparedStatement ps = namedStatements.get(sql);
        if (ps == null) {
            ps = connection.prepareStatement(sql);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Prepared SQL statement: " + sql);
            }
            namedStatements.put(sql, ps);
        }
        ps.clearParameters();
        return ps;
    }

    /**
     * Gets the
     * <code>Connection</code> object.
     *
     * @return The Connection object.
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Rollbacks transaction.
     *
     * @throws SQLException If a database access error occurs.
     */
    public void rollback() throws SQLException {
        connection.rollback();
    }

    /**
     * Commits the transaction.
     *
     * @throws SQLException If a database access error occurs.
     */
    public void commit() throws SQLException {
        connection.commit();
    }

    /**
     * Closes this
     * <code>Connection</code>.
     *
     * @throws SQLException If a database access error occurs.
     */
    public void close() throws SQLException {
        for (PreparedStatement ps : namedStatements.values()) {
            try {
                ps.close();
            } catch (SQLException e) {
                LOG.warn("Error closing prepared statement: " + e.toString() + ". Will continue ... ");
            }
        }
        connection.close();
        LOG.debug("Database connection for session closed");
    }
}
