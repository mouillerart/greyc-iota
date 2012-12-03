/*
 *  This program is a part of the IoTa Project.
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
 * Copied from org.fosstrak.epcis.repository.EpcisConstants
 */
package fr.unicaen.iota.eta.callback.filter;

import java.util.Arrays;
import java.util.List;

/**
 * This class holds the formal URI names defined by the core event types module
 * (EPCIS 1.0, Section 7.2), e.g., event types or vocabulary types.
 */
public final class EpcisConstants {

    //TODO: SLS enum
    // vocabulary types
    public static final String READ_POINT_ID = "urn:epcglobal:epcis:vtype:ReadPoint";
    public static final String BUSINESS_LOCATION_ID = "urn:epcglobal:epcis:vtype:BusinessLocation";
    public static final String BUSINESS_STEP_ID = "urn:epcglobal:epcis:vtype:BusinessStep";
    public static final String DISPOSITION_ID = "urn:epcglobal:epcis:vtype:Disposition";
    public static final String BUSINESS_TRANSACTION_ID = "urn:epcglobal:epcis:vtype:BusinessTransaction";
    public static final String BUSINESS_TRANSACTION_TYPE_ID = "urn:epcglobal:epcis:vtype:BusinessTransactionType";
    public static final String EPC_CLASS_ID = "urn:epcglobal:epcis:vtype:EPCClass";
    // all vocabulary types in a list
    public static final List<String> VOCABULARY_TYPES = Arrays.asList(new String[]{
                READ_POINT_ID,
                BUSINESS_LOCATION_ID,
                BUSINESS_STEP_ID,
                DISPOSITION_ID,
                BUSINESS_TRANSACTION_ID,
                BUSINESS_TRANSACTION_TYPE_ID,
                EPC_CLASS_ID});
    // event types
    public static final String AGGREGATION_EVENT = "AggregationEvent";
    public static final String OBJECT_EVENT = "ObjectEvent";
    public static final String QUANTITY_EVENT = "QuantityEvent";
    public static final String TRANSACTION_EVENT = "TransactionEvent";
    // all event types in a list
    public static final List<String> EVENT_TYPES = Arrays.asList(new String[]{
                AGGREGATION_EVENT, OBJECT_EVENT, QUANTITY_EVENT, TRANSACTION_EVENT});

    /**
     * Hidden default constructor.
     */
    private EpcisConstants() {
    }
}
