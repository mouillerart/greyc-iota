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
 * Copied org.fosstrak.epcis.repository.query.QueryOperationsWebService
 */
package fr.unicaen.iota.eta.query;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.repository.EpcisQueryControlInterface;
import org.fosstrak.epcis.soap.*;

/**
 * This class redirects the calls received from the Web service stack to the
 * underlying QueryOperationsModule and ensures that any uncaught exception is
 * properly catched and wrapped into an ImplementationExceptionResponse.
 */
public class QueryOperationsWebService implements EPCISServicePortType {

    private static final Log LOG = LogFactory.getLog(QueryOperationsWebService.class);
    private EpcisQueryControlInterface queryModule;

    public QueryOperationsWebService() {
    }

    public QueryOperationsWebService(EpcisQueryControlInterface queryModule) {
        this.queryModule = queryModule;
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#getQueryNames(org.fosstrak.epcis.model.EmptyParms)
     */
    @Override
    public ArrayOfString getQueryNames(EmptyParms empty) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse {
        ArrayOfString aos = new ArrayOfString();
        aos.getString().addAll(queryModule.getQueryNames());
        return aos;
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#getStandardVersion(org.fosstrak.epcis.model.EmptyParms)
     */
    @Override
    public String getStandardVersion(EmptyParms empty) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse {
        return queryModule.getStandardVersion();
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#getSubscriptionIDs(org.fosstrak.epcis.model.GetSubscriptionIDs)
     */
    @Override
    public ArrayOfString getSubscriptionIDs(GetSubscriptionIDs req) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        ArrayOfString aos = new ArrayOfString();
        aos.getString().addAll(queryModule.getSubscriptionIDs(req.getQueryName()));
        return aos;
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#getVendorVersion(org.fosstrak.epcis.model.EmptyParms)
     */
    @Override
    public String getVendorVersion(EmptyParms empty) throws ImplementationExceptionResponse, SecurityExceptionResponse,
            ValidationExceptionResponse {
        return queryModule.getVendorVersion();
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#poll(org.fosstrak.epcis.model.Poll)
     */
    @Override
    public QueryResults poll(Poll poll) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        // log and wrap any error that is not one of the expected exceptions
        try {
            return queryModule.poll(poll.getQueryName(), poll.getParams());
        } catch (ImplementationExceptionResponse e) {
            throw e;
        } catch (QueryTooComplexExceptionResponse e) {
            throw e;
        } catch (QueryTooLargeExceptionResponse e) {
            throw e;
        } catch (SecurityExceptionResponse e) {
            throw e;
        } catch (ValidationExceptionResponse e) {
            throw e;
        } catch (NoSuchNameExceptionResponse e) {
            throw e;
        } catch (QueryParameterExceptionResponse e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unexpected error occurred while processing request";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (poll != null) {
                ie.setQueryName(poll.getQueryName());
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#subscribe(org.fosstrak.epcis.model.Subscribe)
     */
    @Override
    public VoidHolder subscribe(Subscribe subscribe) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        // log and wrap any error that is not one of the expected exceptions
        try {
            queryModule.subscribe(subscribe.getQueryName(), subscribe.getParams(), subscribe.getDest(),
                    subscribe.getControls(), subscribe.getSubscriptionID());
            return new VoidHolder();
        } catch (DuplicateSubscriptionExceptionResponse e) {
            throw e;
        } catch (ImplementationExceptionResponse e) {
            throw e;
        } catch (QueryTooComplexExceptionResponse e) {
            throw e;
        } catch (SecurityExceptionResponse e) {
            throw e;
        } catch (InvalidURIExceptionResponse e) {
            throw e;
        } catch (ValidationExceptionResponse e) {
            throw e;
        } catch (SubscribeNotPermittedExceptionResponse e) {
            throw e;
        } catch (NoSuchNameExceptionResponse e) {
            throw e;
        } catch (SubscriptionControlsExceptionResponse e) {
            throw e;
        } catch (QueryParameterExceptionResponse e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unknown error occurred while processing request";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (subscribe != null) {
                ie.setQueryName(subscribe.getQueryName());
                ie.setSubscriptionID(subscribe.getSubscriptionID());
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#unsubscribe(org.fosstrak.epcis.model.Unsubscribe)
     */
    @Override
    public VoidHolder unsubscribe(Unsubscribe unsubscribe) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        // log and wrap any error that is not one of the expected exceptions
        try {
            queryModule.unsubscribe(unsubscribe.getSubscriptionID());
            return new VoidHolder();
        } catch (ImplementationExceptionResponse e) {
            throw e;
        } catch (SecurityExceptionResponse e) {
            throw e;
        } catch (NoSuchSubscriptionExceptionResponse e) {
            throw e;
        } catch (Exception e) {
            String msg = "Unknown error occurred while processing request";
            LOG.error(msg, e);
            ImplementationException ie = new ImplementationException();
            ie.setReason(msg);
            ie.setSeverity(ImplementationExceptionSeverity.ERROR);
            if (unsubscribe != null) {
                ie.setSubscriptionID(unsubscribe.getSubscriptionID());
            }
            throw new ImplementationExceptionResponse(msg, ie, e);
        }
    }

    /**
     * @return the queryModule
     */
    public EpcisQueryControlInterface getQueryModule() {
        return queryModule;
    }

    /**
     * @param queryModule the queryModule to set
     */
    public void setQueryModule(EpcisQueryControlInterface queryModule) {
        this.queryModule = queryModule;
    }
}
