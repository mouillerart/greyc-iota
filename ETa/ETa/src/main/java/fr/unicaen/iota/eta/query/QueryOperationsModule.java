/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
 * Derived from org.fosstrak.epcis.repository.query.QueryOperationsModule
 */
package fr.unicaen.iota.eta.query;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.eta.constants.Constants;
import fr.unicaen.iota.xacml.XACMLConstantsEventType;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletContext;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.queryclient.QueryControlClient;
import org.fosstrak.epcis.repository.EpcisConstants;
import org.fosstrak.epcis.repository.EpcisQueryControlInterface;
import org.fosstrak.epcis.soap.*;

/**
 * EPCIS Query Operations Module implementing the SOAP/HTTP binding of the Query
 * Control Interface. The implementation converts invocations from Axis into
 * queries to EPCIS interface, XACML query for each result to check access
 * rigths and returns the final result to the requesting client through Axis.
 */
public class QueryOperationsModule implements EpcisQueryControlInterface {

    private static final Log LOG = LogFactory.getLog(QueryOperationsModule.class);
    /**
     * The version of the standard that this service is implementing.
     */
    private static final String STD_VERSION = "1.0";
    /**
     * The names of all the implemented queries.
     */
    private static final List<String> QUERYNAMES;

    static {
        QUERYNAMES = new ArrayList<String>(2);
        QUERYNAMES.add("SimpleEventQuery");
        QUERYNAMES.add("SimpleMasterDataQuery");
    }
    /**
     * The version of this service implementation. The empty string indicates
     * that the implementation implements only standard functionality with no
     * vendor extensions.
     */
    private String serviceVersion = "";
    private ServletContext servletContext;
    private DataSource dataSource;
    private QueryOperationsBackend backend;
    private QueryControlClient epcisQueryClient;
    private QueryCheck queryCheck;


    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getQueryNames() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getQueryNames'");
        return QUERYNAMES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getStandardVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getStandardVersion'");
        return STD_VERSION;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getSubscriptionIDs(String queryName) throws NoSuchNameExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        try {
            LOG.debug("Invoking 'getSubscriptionIDs'");
            QueryOperationsSession session = backend.openSession(dataSource);
            try {
                return backend.getSubscriptionIds(session);
            } finally {
                session.close();
                LOG.debug("DB connection closed");
            }
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVendorVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getVendorVersion'");
        return serviceVersion;
    }

    @Override
    public QueryResults poll(String queryName, QueryParams queryParams) throws NoSuchNameExceptionResponse,
            QueryParameterExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        QueryResults results;
        try {
            configureEPCISQueryClient();
        } catch (Exception e) {
            String msg = "Internal error occurred while processing request";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (queryName != null) {
                ie.setQueryName(queryName);
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }
        queryCheck = new QueryCheck();

        /**
         * TODO: add user and owner in the XACMLEPCISEvent owner =
         * ((ObjectEventType)result).getAny(); user =
         */
        String owner = "anonymous";
        String user = "anonymous";

        if ("SimpleEventQuery".equals(queryName)) {
            for (QueryParam queryParam : queryParams.getParam()) {
                if (queryParam.getName() != null && "eventType".equals(queryParam.getName())) {
                    try {
                        ArrayOfString aos = (ArrayOfString) queryParam.getValue();
                        if (aos == null || aos.getString() == null) {
                            continue;
                        }
                        List<String> eventTypes = aos.getString();
                        Iterator<String> iterType = eventTypes.iterator();
                        while (iterType.hasNext()) {
                            String eventType = iterType.next();
                            if (EpcisConstants.AGGREGATION_EVENT.equals(eventType)) {
                                XACMLEPCISEvent e = new XACMLEPCISEvent(owner, null, null, null, null, null,
                                        XACMLConstantsEventType.AGGREGATION, null, null, null, null, null, null, null, null);
                                if (!queryCheck.xacmlCheck(e, user)) {
                                    iterType.remove();
                                }
                            } else if (EpcisConstants.OBJECT_EVENT.equals(eventType)) {
                                XACMLEPCISEvent e = new XACMLEPCISEvent(owner, null, null, null, null, null,
                                        XACMLConstantsEventType.OBJECT, null, null, null, null, null, null, null, null);
                                if (!queryCheck.xacmlCheck(e, user)) {
                                    iterType.remove();
                                }
                            } else if (EpcisConstants.QUANTITY_EVENT.equals(eventType)) {
                                XACMLEPCISEvent e = new XACMLEPCISEvent(owner, null, null, null, null, null,
                                        XACMLConstantsEventType.QUANTITY, null, null, null, null, null, null, null, null);
                                if (!queryCheck.xacmlCheck(e, user)) {
                                    iterType.remove();
                                }
                            } else if (EpcisConstants.TRANSACTION_EVENT.equals(eventType)) {
                                XACMLEPCISEvent e = new XACMLEPCISEvent(owner, null, null, null, null, null,
                                        XACMLConstantsEventType.TRANSACTION, null, null, null, null, null, null, null, null);
                                if (!queryCheck.xacmlCheck(e, user)) {
                                    iterType.remove();
                                }
                            }
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        LOG.debug("Invoking 'poll'");
        Poll poll = new Poll();
        poll.setQueryName(queryName);
        poll.setParams(queryParams);

        results = epcisQueryClient.poll(poll);

        //TODO xacml_active
        boolean xacml_active = true;

        if (xacml_active && "SimpleEventQuery".equals(results.getQueryName())) {
            queryCheck.xacmlCheck(results.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent(), user, owner);
        } else if ("SimpleMasterDataQuery".equals(results.getQueryName())) {
            queryCheck.xacmlCheckMasterD(results.getResultsBody().getVocabularyList().getVocabulary(), user, owner);
        }
        return results;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void subscribe(String queryName, QueryParams params, String dest, SubscriptionControls controls, String subscriptionID)
            throws NoSuchNameExceptionResponse, InvalidURIExceptionResponse, DuplicateSubscriptionExceptionResponse,
            QueryParameterExceptionResponse, QueryTooComplexExceptionResponse, SubscriptionControlsExceptionResponse,
            SubscribeNotPermittedExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'subscribe'");

        // a few input sanity checks

        // dest may be null or empty. But we don't support pre-arranged
        // destinations and throw an InvalidURIException according to
        // the standard.
        if (dest == null || "".equals(dest.toString())) {
            String msg = "Destination URI is empty. This implementation doesn't support pre-arranged destinations.";
            LOG.warn("QueryParameterException: " + msg);
            InvalidURIException e = new InvalidURIException();
            e.setReason(msg);
            throw new InvalidURIExceptionResponse(msg, e);
        }
        try {
            new URL(dest.toString());
        } catch (MalformedURLException ex) {
            String msg = "Destination URI is invalid: " + ex.getMessage();
            LOG.warn("InvalidURIException: " + msg);
            InvalidURIException e = new InvalidURIException();
            e.setReason(msg);
            throw new InvalidURIExceptionResponse(msg, e, ex);
        }

        // check query name
        if (!QUERYNAMES.contains(queryName)) {
            String msg = "Illegal query name '" + queryName + "'";
            LOG.warn("NoSuchNameException: " + msg);
            NoSuchNameException e = new NoSuchNameException();
            e.setReason(msg);
            throw new NoSuchNameExceptionResponse(msg, e);
        }

        // SimpleMasterDataQuery only valid for polling
        if ("SimpleMasterDataQuery".equals(queryName)) {
            String msg = "Subscription not allowed for SimpleMasterDataQuery";
            LOG.warn("SubscribeNotPermittedException: " + msg);
            SubscribeNotPermittedException e = new SubscribeNotPermittedException();
            e.setReason(msg);
            throw new SubscribeNotPermittedExceptionResponse(msg, e);
        }

        // subscriptionID cannot be empty
        if (subscriptionID == null || subscriptionID.isEmpty()) {
            String msg = "SubscriptionID is empty. Choose a valid subscriptionID";
            LOG.warn(msg);
            ValidationException e = new ValidationException();
            e.setReason(msg);
            throw new ValidationExceptionResponse(msg, e);
        }

        // trigger and schedule may no be used together, but one of them
        // must be set
        if (controls.getSchedule() != null && controls.getTrigger() != null) {
            String msg = "Schedule and trigger cannot be used together";
            LOG.debug("SubscriptionControlsException: " + msg);
            SubscriptionControlsException e = new SubscriptionControlsException();
            e.setReason(msg);
            throw new SubscriptionControlsExceptionResponse(msg, e);
        }
        if (controls.getSchedule() == null && controls.getTrigger() == null) {
            String msg = "Either schedule or trigger has to be provided";
            LOG.debug("SubscriptionControlsException: " + msg);
            SubscriptionControlsException e = new SubscriptionControlsException();
            e.setReason(msg);
            throw new SubscriptionControlsExceptionResponse(msg, e);
        }

        try {
            QueryOperationsSession session = backend.openSession(dataSource);

            try {
                // check for already existing subscriptionID
                if (backend.fetchExistsSubscriptionId(session, subscriptionID)) {
                    String msg = "SubscriptionID '" + subscriptionID
                            + "' already exists. Choose a different subscriptionID";
                    LOG.debug("DuplicateSubscriptionException: " + msg);
                    DuplicateSubscriptionException e = new DuplicateSubscriptionException();
                    e.setReason(msg);
                    throw new DuplicateSubscriptionExceptionResponse(msg, e);
                }

                try {
                    configureEPCISQueryClient();
                } catch (Exception e) {
                    String msg = "Internal error occurred while processing request";
                    LOG.error(msg, e);
                    ImplementationException ie = new ImplementationException();
                    ie.setReason(msg);
                    ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                    if (queryName != null) {
                        ie.setQueryName(queryName);
                    }
                    throw new ImplementationExceptionResponse(msg, ie, e);
                }

                queryCheck = new QueryCheck();

                // XACML check
                // TODO user
                String user = "ppda";
                String owner = "test";
                int subscriptionResponse = queryCheck.checkSubscribe(user, owner);
                if (subscriptionResponse == Result.DECISION_PERMIT) {
                    String callbackAddress;
                    try {
                        callbackAddress = Constants.CALLBACK_URL;
                    } catch (Exception e) {
                        String msg = "Internal error occurred while processing request";
                        LOG.error(msg, e);
                        ImplementationException ie = new ImplementationException();
                        ie.setReason(msg);
                        ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                        if (queryName != null) {
                            ie.setQueryName(queryName);
                        }
                        throw new ImplementationExceptionResponse(msg, ie, e);
                    }
                    Subscribe subscribe = new Subscribe();
                    subscribe.setQueryName(queryName);
                    subscribe.setParams(params);
                    subscribe.setDest(callbackAddress);
                    subscribe.setControls(controls);
                    subscribe.setSubscriptionID(subscriptionID);
                    // Subscribe query to the EPCIS
                    epcisQueryClient.subscribe(subscribe);
                    // Stores the query subscription to the database
                    backend.storeSubscription(session, subscriptionID, dest);
                    LOG.debug("New subscription from user: " + user);
                } else {
                    String msg = "Subscription not allowed";
                    LOG.warn("SubscribeNotPermittedException: " + msg);
                    SubscribeNotPermittedException e = new SubscribeNotPermittedException();
                    e.setReason(msg);
                    throw new SubscribeNotPermittedExceptionResponse(msg, e);
                }
            } finally {
                session.close();
                LOG.debug("DB connection closed");
            }
        } catch (SQLException e) {
            String msg = "An SQL error occurred while creating the subscription in the database.";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (queryName != null) {
                ie.setQueryName(queryName);
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void unsubscribe(String subscriptionID) throws NoSuchSubscriptionExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        try {
            LOG.debug("Invoking 'unsubscribe'");
            QueryOperationsSession session = backend.openSession(dataSource);
            try {
                if (!backend.fetchExistsSubscriptionId(session, subscriptionID)) {
                    String msg = "There is no subscription with ID '" + subscriptionID + "'";
                    LOG.warn("NoSuchSubscriptionException: " + msg);
                    NoSuchSubscriptionException e = new NoSuchSubscriptionException();
                    e.setReason(msg);
                    throw new NoSuchSubscriptionExceptionResponse(msg, e);
                }

                //TODO xacml check?

                try {
                    configureEPCISQueryClient();
                } catch (Exception e) {
                    String msg = "Internal error occurred while processing request";
                    LOG.error(msg, e);
                    ImplementationException ie = new ImplementationException();
                    ie.setReason(msg);
                    ie.setSeverity(ImplementationExceptionSeverity.ERROR);
                    throw new ImplementationExceptionResponse(msg, ie, e);
                }

                // Unsubscribes the query to the EPCIS
                epcisQueryClient.unsubscribe(subscriptionID);

                // Deletes the subscription from the database
                backend.deleteSubscription(session, subscriptionID);
            } finally {
                session.close();
                LOG.debug("DB connection closed");
            }
        } catch (SQLException e) {
            ImplementationException iex = new ImplementationException();
            String msg = "SQL error during query execution: " + e.getMessage();
            LOG.error(msg, e);
            iex.setReason(msg);
            iex.setSeverity(ImplementationExceptionSeverity.ERROR);
            throw new ImplementationExceptionResponse(msg, iex, e);
        }
    }

    /**
     * @return the dataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * @param dataSource the dataSource to set
     */
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * @param servletContext the servletContextservletContext to set
     */
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * @return the serviceVersion
     */
    public String getServiceVersion() {
        return serviceVersion;
    }

    /**
     * @param serviceVersion the serviceVersion to set
     */
    public void setServiceVersion(String serviceVersion) {
        if (!"".equals(serviceVersion)) {
            // serviceVersion must be a valid URL
            try {
                new URL(serviceVersion);
            } catch (MalformedURLException e) {
                serviceVersion = "https://code.google.com/p/fosstrak/wiki/EpcisMain/" + serviceVersion;
            }
        }
        this.serviceVersion = serviceVersion;
    }

    /**
     * @return the backend
     */
    public QueryOperationsBackend getBackend() {
        return backend;
    }

    /**
     * @param backend the backend to set
     */
    public void setBackend(QueryOperationsBackend backend) {
        this.backend = backend;
    }

    public QueryControlClient getEpcisQueryClient() {
        return epcisQueryClient;
    }

    public void setEpcisQueryClient(QueryControlClient epcisQueryClient) {
        this.epcisQueryClient = epcisQueryClient;
    }

    private void configureEPCISQueryClient() throws IOException, Exception {
        if (epcisQueryClient == null) {
            epcisQueryClient = new QueryControlClient(Constants.EPCIS_QUERY_URL);
        }
        if (!epcisQueryClient.isServiceConfigured()) {
            String epcisAddressString = Constants.EPCIS_QUERY_URL;
            URL epcisAddress = new URL(epcisAddressString);
            epcisQueryClient.configureService(epcisAddress, null);
            if (!epcisQueryClient.isServiceConfigured()) {
                throw new Exception("EPCIS Query Client not configured.");
            }
        }
    }
}
