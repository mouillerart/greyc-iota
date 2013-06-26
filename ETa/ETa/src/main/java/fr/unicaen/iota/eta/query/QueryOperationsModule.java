/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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

import fr.unicaen.iota.eta.utils.Constants;
import fr.unicaen.iota.tau.model.Identity;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
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
import org.fosstrak.epcis.soap.*;
import org.w3c.dom.Element;

/**
 * EPCIS Query Operations Module implementing the SOAP/HTTP binding of the Query
 * Control Interface. The implementation converts invocations from Axis into
 * queries to EPCIS interface, XACML query for each result to check access
 * rigths and returns the final result to the requesting client through Axis.
 */
public class QueryOperationsModule {

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
    private String serviceVersion = "1.0-eta";
    private DataSource dataSource;
    private QueryOperationsBackend backend;
    private QueryControlClient epcisQueryClient;
    private QueryCheck queryCheck;

    public QueryOperationsBackend getBackend() {
        return backend;
    }

    public void setBackend(QueryOperationsBackend backend) {
        this.backend = backend;
    }

    public QueryControlClient getEpcisQueryClient() {
        return epcisQueryClient;
    }

    public void setEpcisQueryClient(QueryControlClient epcisQueryClient) {
        this.epcisQueryClient = epcisQueryClient;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setServletContext(ServletContext servletContext) {
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public QueryCheck getQueryCheck() {
        return queryCheck;
    }

    public void setQueryCheck(QueryCheck queryCheck) {
        this.queryCheck = queryCheck;
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

    public List<String> getQueryNames() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getQueryNames'");
        return QUERYNAMES;
    }

    public String getStandardVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getStandardVersion'");
        return STD_VERSION;
    }

    public List<String> getSubscriptionIDs(String queryName, String user) throws NoSuchNameExceptionResponse,
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

    public String getVendorVersion() throws SecurityExceptionResponse, ValidationExceptionResponse,
            ImplementationExceptionResponse {
        LOG.debug("Invoking 'getVendorVersion'");
        return serviceVersion;
    }

    public QueryResults poll(String queryName, QueryParams queryParams, String user) throws NoSuchNameExceptionResponse,
            QueryParameterExceptionResponse, QueryTooComplexExceptionResponse, QueryTooLargeExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, ImplementationExceptionResponse {
        QueryResults results = null;
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

        Poll poll = new Poll();
        poll.setQueryName(queryName);

        if ("SimpleEventQuery".equals(queryName)) {
            int maxEventCount = -1;
            int eventCountLimit = -1;
            boolean paramOrderByIsPresent = false;
            Iterator<QueryParam> iterParam = queryParams.getParam().iterator();
            try {
                while (iterParam.hasNext()) {
                    QueryParam param = iterParam.next();
                    if ("maxEventCount".equals(param.getName())) {
                        maxEventCount = parseAsInteger(param.getValue()).intValue();
                        iterParam.remove();
                    }
                    else if ("eventCountLimit".equals(param.getName())) {
                        eventCountLimit = parseAsInteger(param.getValue()).intValue();
                        iterParam.remove();
                    }
                    else if ("orderBy".equals(param.getName())) {
                        paramOrderByIsPresent = true;
                    }
                }
            } catch (NumberFormatException ex) {
                throw queryParameterException("The QueryParam can not be parsed", ex);
            }
            if (maxEventCount > -1 && eventCountLimit > -1) {
                String msg = "Paramters 'maxEventCount' and 'eventCountLimit' are mutually exclusive";
                throw queryParameterException(msg, null);
            }
            if (paramOrderByIsPresent && eventCountLimit > -1) {
                String msg = "'eventCountLimit' may only be used when 'orderBy' is specified";
                throw queryParameterException(msg, null);
            }
            poll.setParams(queryParams);
            LOG.debug("Invoking 'poll'");
            results = epcisQueryClient.poll(poll);
            List<Object> eventList = results.getResultsBody().getEventList().getObjectEventOrAggregationEventOrQuantityEvent();
            queryCheck.xacmlCheck(eventList, user);
            if (eventCountLimit > -1 && eventList.size() > eventCountLimit) {
                eventList.subList(eventCountLimit, eventList.size()).clear();
            }
            else if (maxEventCount > -1 && eventList.size() > maxEventCount) {
                // according to spec, this must result in a QueryTooLargeException
                String msg = "The query returned more results than specified by 'maxEventCount'";
                LOG.info(msg);
                QueryTooLargeException e = new QueryTooLargeException();
                e.setReason(msg);
                throw new QueryTooLargeExceptionResponse(msg, e);
            }
        } else if ("SimpleMasterDataQuery".equals(queryName)) {
            int maxElementCount = -1;
            Iterator<QueryParam> iterParam = queryParams.getParam().iterator();
            try {
                while (iterParam.hasNext()) {
                    QueryParam param = iterParam.next();
                    if ("maxElementCount".equals(param.getName())) {
                        maxElementCount = parseAsInteger(param.getValue()).intValue();
                        iterParam.remove();
                    }
                }
            } catch (NumberFormatException ex) {
                throw queryParameterException("The QueryParam can not be parsed", ex);
            }
            poll.setParams(queryParams);
            LOG.debug("Invoking 'poll'");
            results = epcisQueryClient.poll(poll);
            List<VocabularyType> vocList = results.getResultsBody().getVocabularyList().getVocabulary();
            queryCheck.xacmlCheckMasterD(vocList, user);
            if (maxElementCount > -1 && vocList.size() > maxElementCount) {
                String msg = "The query returned more results than specified by 'maxElementCount'";
                LOG.info(msg);
                QueryTooLargeException e = new QueryTooLargeException();
                e.setReason(msg);
                throw new QueryTooLargeExceptionResponse(msg, e);
            }
        }
        return results;

    }

    public void subscribe(String queryName, QueryParams params, String dest, SubscriptionControls controls, String subscriptionID, String user)
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
            URL url = new URL(dest.toString());
            if (!"https".equalsIgnoreCase(url.getProtocol())) {
                String msg = "Destination URI is not HTTPS. TLS mutual authentication is required to send events.";
                LOG.warn("QueryParameterException: " + msg);
                InvalidURIException e = new InvalidURIException();
                e.setReason(msg);
                throw new InvalidURIExceptionResponse(msg, e);
            }
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
                backend.storeSubscription(session, subscriptionID, dest, user);
                LOG.debug("New subscription from user: " + user);
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

    public void unsubscribe(String subscriptionID, String user) throws NoSuchSubscriptionExceptionResponse,
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

    public boolean canBe(Principal principal, Identity identity) {
        String user = principal.getName();
        String canBeUser = identity.getAsString();
        if (user.equals(canBeUser)) {
            return true;
        }
        return queryCheck.canBe(user, canBeUser);
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

    /**
     * Parses the given query parameter value as Integer.
     *
     * @param queryParamValue The query parameter value to be parsed as Integer.
     * @return The Integer holding the value of the query parameter.
     * @throws NumberFormatException If the query parameter value cannot be parsed as Integer.
     */
    private Integer parseAsInteger(Object queryParamValue) throws NumberFormatException {
        if (queryParamValue instanceof Integer) {
            return (Integer) queryParamValue;
        } else if (queryParamValue instanceof Element) {
            Element elem = (Element) queryParamValue;
            return Integer.valueOf(elem.getTextContent().trim());
        } else {
            return Integer.valueOf(queryParamValue.toString());
        }
    }

    /**
     * Writes the given message and exception to the application's log file,
     * creates a QueryParameterException from the given message, and returns a
     * new QueryParameterExceptionResponse. Use this method to conveniently
     * return a user error message back to the requesting service caller, e.g.:
     *
     * <pre>
     * String msg = &quot;unable to parse query parameter&quot;
     * throw new queryParameterException(msg, null);
     * </pre>
     *
     * @param msg
     *            A user error message.
     * @param e
     *            An internal exception - this exception will not be delivered
     *            back to the service caller as it contains application specific
     *            information. It will be used to print some details about the
     *            user error to the log file (useful for debugging).
     * @return A new QueryParameterExceptionResponse containing the given user
     *         error message.
     */
    private QueryParameterExceptionResponse queryParameterException(String msg, Exception e) {
        LOG.info("QueryParameterException: " + msg);
        if (LOG.isTraceEnabled() && e != null) {
            LOG.trace("Exception details: " + e.getMessage(), e);
        }
        QueryParameterException qpe = new QueryParameterException();
        qpe.setReason(msg);
        return new QueryParameterExceptionResponse(msg, qpe);
    }

}
