/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.epcilon.query;

import fr.unicaen.iota.epcilon.conf.Configuration;
import fr.unicaen.iota.epcilon.model.EventToPublish;
import fr.unicaen.iota.epcilon.util.SQLQueryModule;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.*;

/**
 * Servlet implementation class StandingQueryCallbackModule
 */
public class StandingQueryCallbackModule {

    private static interface EventToPublishClass {

        String object = "object";
        String aggregation = "aggregation";
        String transaction = "transaction";
        String quantity = "quantity";
    }
    private static final Log LOG = LogFactory.getLog(StandingQueryCallbackModule.class);
    private int nb_events = 0;
    private SQLQueryModule sqlQueryModule;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public StandingQueryCallbackModule() {
        sqlQueryModule = new SQLQueryModule();
    }

    public void saveEvents(EPCISQueryDocumentType doc) {
        EPCISQueryBodyType body = doc.getEPCISBody();
        QueryResults queryResults = body.getQueryResults();
        QueryResultsBody resultsBody = queryResults.getResultsBody();
        EventListType eventList = resultsBody.getEventList();
        List<Object> events = eventList.getObjectEventOrAggregationEventOrQuantityEvent();
        for (Object event : events) {
            JAXBElement elem;
            if (event instanceof JAXBElement) {
                elem = (JAXBElement) event;
                EPCISEventType eventType = (EPCISEventType) elem.getValue();
                if (eventType instanceof ObjectEventType) {
                    sqlQueryModule.saveEvents(createEventToPublishFromObject(eventType));
                }
                if (eventType instanceof AggregationEventType) {
                    sqlQueryModule.saveEvents(createEventToPublishFromAggregation(eventType));
                }
                if (eventType instanceof TransactionEventType) {
                    sqlQueryModule.saveEvents(createEventToPublishFromTransaction(eventType));
                }
                if (eventType instanceof QuantityEventType) {
                    sqlQueryModule.saveEvents(createEventToPublishFromQuantity(eventType));
                }
            }
        }
        LOG.info(nb_events + " events received");
    }

    private List<EventToPublish> createEventToPublishFromObject(EPCISEventType eventType) {
        ObjectEventType objectEvent = (ObjectEventType) eventType;
        String bizStep = objectEvent.getBizStep();
        // récupération des évenements pour ObjectEvent
        String action = objectEvent.getAction().value();
        // récupération de la liste des codes epcs de l'évenement
        EPCListType epcList = objectEvent.getEpcList();
        List<EventToPublish> result = new ArrayList<EventToPublish>();
        nb_events += epcList.getEpc().size();
        // création de l'objet à sauvegarder
        for (EPC epc : epcList.getEpc()) {
            EventToPublish eventToPublish = new EventToPublish();
            eventToPublish.setEpc(epc.getValue());
            eventToPublish.setBizStep(bizStep);
            eventToPublish.setEventType(action);
            eventToPublish.setEventClass(EventToPublishClass.object);
            long mil = objectEvent.getEventTime().toGregorianCalendar().getTimeInMillis();
            eventToPublish.setEventTime(new Timestamp(mil));
            eventToPublish.setLastUpdate(new Timestamp(Configuration.DEFAULT_EVENT_TO_PUBLISH_TIMESTAMP));
            result.add(eventToPublish);
        }
        return result;
    }

    private List<EventToPublish> createEventToPublishFromAggregation(EPCISEventType eventType) {
        AggregationEventType aggregationEvent = (AggregationEventType) eventType;
        String bizStep = aggregationEvent.getBizStep();
        // récupération des évenements pour ObjectEvent
        String action = aggregationEvent.getAction().value();
        // récupération de la liste des codes epcs de l'évenement
        EPCListType epcList = aggregationEvent.getChildEPCs();
        if (aggregationEvent.getParentID() != null) {
            EPC parent = new EPC();
            parent.setValue(aggregationEvent.getParentID());
            epcList.getEpc().add(parent);
        }
        List<EventToPublish> result = new ArrayList<EventToPublish>();
        nb_events += epcList.getEpc().size();
        // création de l'objet à sauvegarder
        for (EPC epc : epcList.getEpc()) {
            EventToPublish eventToPublish = new EventToPublish();
            eventToPublish.setEpc(epc.getValue());
            eventToPublish.setBizStep(bizStep);
            eventToPublish.setEventType(action);
            eventToPublish.setEventClass(EventToPublishClass.aggregation);
            long mil = aggregationEvent.getEventTime().toGregorianCalendar().getTimeInMillis();
            eventToPublish.setEventTime(new Timestamp(mil));
            eventToPublish.setLastUpdate(new Timestamp(Configuration.DEFAULT_EVENT_TO_PUBLISH_TIMESTAMP));
            result.add(eventToPublish);
        }
        return result;
    }

    private List<EventToPublish> createEventToPublishFromTransaction(EPCISEventType eventType) {
        TransactionEventType transactionEvent = (TransactionEventType) eventType;
        String bizStep = transactionEvent.getBizStep();
        // récupération des évenements pour ObjectEvent
        String action = transactionEvent.getAction().value();
        // récupération de la liste des codes epcs de l'évenement
        EPCListType epcList = transactionEvent.getEpcList();
        if (transactionEvent.getParentID() != null) {
            EPC parent = new EPC();
            parent.setValue(transactionEvent.getParentID());
            epcList.getEpc().add(parent);
        }
        List<EventToPublish> result = new ArrayList<EventToPublish>();
        nb_events += epcList.getEpc().size();
        // création de l'objet à sauvegarder
        for (EPC epc : epcList.getEpc()) {
            EventToPublish eventToPublish = new EventToPublish();
            eventToPublish.setEpc(epc.getValue());
            eventToPublish.setBizStep(bizStep);
            eventToPublish.setEventType(action);
            eventToPublish.setEventClass(EventToPublishClass.transaction);
            long mil = transactionEvent.getEventTime().toGregorianCalendar().getTimeInMillis();
            eventToPublish.setEventTime(new Timestamp(mil));
            eventToPublish.setLastUpdate(new Timestamp(Configuration.DEFAULT_EVENT_TO_PUBLISH_TIMESTAMP));
            result.add(eventToPublish);
        }
        return result;
    }

    private List<EventToPublish> createEventToPublishFromQuantity(EPCISEventType eventType) {
        QuantityEventType quantityEvent = (QuantityEventType) eventType;
        String bizStep = quantityEvent.getBizStep();
        // récupération des évenements pour ObjectEvent
        // récupération de la liste des codes epcs de l'évenement
        List<EventToPublish> result = new ArrayList<EventToPublish>();
        nb_events += 1;
        // création de l'objet à sauvegarder
        EventToPublish eventToPublish = new EventToPublish();
        eventToPublish.setEpc(quantityEvent.getEpcClass() + ".0");
        eventToPublish.setBizStep(bizStep);
        eventToPublish.setEventType("null");
        eventToPublish.setEventClass(EventToPublishClass.quantity);
        long mil = quantityEvent.getEventTime().toGregorianCalendar().getTimeInMillis();
        eventToPublish.setEventTime(new Timestamp(mil));
        eventToPublish.setLastUpdate(new Timestamp(Configuration.DEFAULT_EVENT_TO_PUBLISH_TIMESTAMP));
        result.add(eventToPublish);
        return result;
    }
}
