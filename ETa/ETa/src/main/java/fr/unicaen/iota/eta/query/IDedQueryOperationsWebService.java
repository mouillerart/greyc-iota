/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.query;

import fr.unicaen.iota.eta.constants.Constants;
import fr.unicaen.iota.eta.soap.IDedEPCISServicePortType;
import fr.unicaen.iota.tau.model.Identity;
import java.security.Principal;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.soap.*;

/**
 * This class redirects the calls received from the Web service stack to the
 * underlying QueryOperationsModule and ensures that any uncaught exception is
 * properly catched and wrapped into an ImplementationExceptionResponse.
 */
//@WebService
public class IDedQueryOperationsWebService implements IDedEPCISServicePortType {

    private static final Log LOG = LogFactory.getLog(IDedQueryOperationsWebService.class);
    @Resource
    private WebServiceContext wsContext;
    protected QueryOperationsModule queryModule;
    protected final Identity anonymous;

    public IDedQueryOperationsWebService() {
        anonymous = new Identity();
        anonymous.setAsString(Constants.XACML_DEFAULT_USER);
    }

    public IDedQueryOperationsWebService(QueryOperationsModule queryModule) {
        this();
        this.queryModule = queryModule;
    }

    /**
     * @return the queryModule
     */
    public QueryOperationsModule getQueryModule() {
        return queryModule;
    }

    /**
     * @param queryModule the queryModule to set
     */
    public void setQueryModule(QueryOperationsModule queryModule) {
        this.queryModule = queryModule;
    }

    private void checkAuth(Identity id) throws SecurityExceptionResponse {
        Principal authId = wsContext.getUserPrincipal();
        if (authId == null || id == anonymous) {
            return;
        }
        if (!queryModule.canBe(authId, id)) {
            throw new SecurityExceptionResponse(authId.getName() + " isn't allowed to pass as " + id.getAsString());
        }
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#getSubscriptionIDs(org.fosstrak.epcis.model.GetSubscriptionIDs)
     */
    @Override
    public ArrayOfString iDedGetSubscriptionIDs(GetSubscriptionIDs req, Identity id) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchNameExceptionResponse {
        checkAuth(id);
        ArrayOfString aos = new ArrayOfString();
        aos.getString().addAll(queryModule.getSubscriptionIDs(req.getQueryName(), id.getAsString()));
        return aos;
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#poll(org.fosstrak.epcis.model.Poll)
     */
    @Override
    public QueryResults iDedPoll(Poll poll, Identity id) throws ImplementationExceptionResponse, QueryTooComplexExceptionResponse,
            QueryTooLargeExceptionResponse, SecurityExceptionResponse, ValidationExceptionResponse,
            NoSuchNameExceptionResponse, QueryParameterExceptionResponse {
        checkAuth(id);
        // log and wrap any error that is not one of the expected exceptions
        try {
            return queryModule.poll(poll.getQueryName(), poll.getParams(), id.getAsString());
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
    public VoidHolder iDedSubscribe(Subscribe subscribe, Identity id) throws DuplicateSubscriptionExceptionResponse,
            ImplementationExceptionResponse, QueryTooComplexExceptionResponse, SecurityExceptionResponse,
            InvalidURIExceptionResponse, ValidationExceptionResponse, SubscribeNotPermittedExceptionResponse,
            NoSuchNameExceptionResponse, SubscriptionControlsExceptionResponse, QueryParameterExceptionResponse {
        checkAuth(id);
        // log and wrap any error that is not one of the expected exceptions
        try {
            queryModule.subscribe(subscribe.getQueryName(), subscribe.getParams(), subscribe.getDest(),
                    subscribe.getControls(), subscribe.getSubscriptionID(), id.getAsString());
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
    public VoidHolder iDedUnsubscribe(Unsubscribe unsubscribe, Identity id) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        checkAuth(id);
        // log and wrap any error that is not one of the expected exceptions
        try {
            queryModule.unsubscribe(unsubscribe.getSubscriptionID(), id.getAsString());
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
}
