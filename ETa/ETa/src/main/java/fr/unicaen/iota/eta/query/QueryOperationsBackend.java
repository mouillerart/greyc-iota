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
 * Derived from org.fosstrak.epcis.repository.query.QueryOperationsBackend
 */
package fr.unicaen.iota.eta.query;

import java.sql.SQLException;
import java.util.List;
import javax.sql.DataSource;
import org.fosstrak.epcis.soap.ImplementationExceptionResponse;

/**
 * The QueryOperationsBackend provides the persistence functionality required by
 * the QueryOperationsModule. It offers methods to manage query subscriptions
 * and methods to execute EPCIS queries, i.e., simple event and masterdata
 * queries. A QueryOperationsSession object which holds the database connection
 * is required to be passed into each of the methods.
 */
public interface QueryOperationsBackend {

    /**
     * Checks if the given subscription ID already exists.
     *
     * @param session The QueryOperationsSession wrapping a database connection.
     * @param subscriptionID The subscription ID to be checked.
     * @return
     * <code>true</code> if the given subscription ID already exists,
     * <code>false</code> otherwise.
     * @throws SQLException If an error with the database occurred.
     */
    public boolean fetchExistsSubscriptionId(final QueryOperationsSession session, final String subscriptionID)
            throws SQLException;

    /**
     * Fetchs all subscription IDs.
     *
     * @param session The QueryOperationsSession wrapping a database connection.
     * @return The list of subscription IDs.
     * @throws SQLException If an error with the database occurred.
     */
    public List<String> getSubscriptionIds(final QueryOperationsSession session) throws SQLException;

    /**
     * Stores a query subscription to the database.
     *
     * @param session The QueryOperationsSession wrapping a database connection.
     * @param dest The destination URL of the client.
     * @param subscrId The subscription ID of the subscription.
     * @param user The user name of the client.
     * @throws SQLException If an error with the database occurred.
     * @throws ImplementationExceptionResponse If an implementation specific
     * error occurred.
     */
    public void storeSubscription(final QueryOperationsSession session, String subscrId, String dest, String user)
            throws SQLException;

    /**
     * Deletes a query subscription from the database.
     *
     * @param session The QueryOperationsSession wrapping a database connection.
     * @param subscrId The ID of the subscription to delete.
     * @throws SQLException If an error with the database occurred.
     */
    public void deleteSubscription(final QueryOperationsSession session, String subscrId) throws SQLException;

    /**
     * Opens a new session for the database transaction.
     *
     * @param dataSource The DataSource object to retrieve the database
     * connection from.
     * @return A QueryOperationsSession instantiated with the database
     * connection retrieved from the given DataSource.
     * @throws SQLException If an error with the database occurred.
     */
    public QueryOperationsSession openSession(final DataSource dataSource) throws SQLException;
}
