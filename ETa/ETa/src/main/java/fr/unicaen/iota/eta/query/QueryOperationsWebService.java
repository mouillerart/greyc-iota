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
 * Derived from org.fosstrak.epcis.repository.query.QueryOperationsWebService
 */
package fr.unicaen.iota.eta.query;

import fr.unicaen.iota.eta.utils.Constants;
import fr.unicaen.iota.tau.model.Identity;
import java.security.Principal;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;
import org.fosstrak.epcis.soap.*;

/**
 * This class redirects the calls received from the Web service stack to the
 * underlying QueryOperationsModule and ensures that any uncaught exception is
 * properly catched and wrapped into an ImplementationExceptionResponse.
 */
public class QueryOperationsWebService extends IDedQueryOperationsWebService implements EPCISServicePortType {

    private static final Log LOG = LogFactory.getLog(QueryOperationsWebService.class);
    private final Identity default_user;
    private final Identity anonymous;

    public QueryOperationsWebService() {
        this(null);
    }

    public QueryOperationsWebService(QueryOperationsModule queryModule) {
        super(queryModule);
        default_user = new Identity();
        default_user.setAsString(Constants.XACML_DEFAULT_USER);
        anonymous = new Identity();
        anonymous.setAsString(Constants.XACML_ANONYMOUS_USER);
    }

    private Identity getClientId() {
        Principal authId = wsContext.getUserPrincipal();
        if (authId == null) {
            return anonymous;
        }
        if (Constants.XACML_USE_TLS_ID) {
            Identity id = new Identity();
            id.setAsString(authId.getName());
            return id;
        }
        return default_user;
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
        return iDedGetSubscriptionIDs(req, getClientId());
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
        return iDedPoll(poll, getClientId());
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
        return iDedSubscribe(subscribe, getClientId());
    }

    /**
     * @see
     * org.fosstrak.epcis.soap.EPCISServicePortType#unsubscribe(org.fosstrak.epcis.model.Unsubscribe)
     */
    @Override
    public VoidHolder unsubscribe(Unsubscribe unsubscribe) throws ImplementationExceptionResponse,
            SecurityExceptionResponse, ValidationExceptionResponse, NoSuchSubscriptionExceptionResponse {
        return iDedUnsubscribe(unsubscribe, getClientId());
    }
}
