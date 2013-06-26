/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.epcilon.discovery;

import fr.unicaen.iota.ds.client.DSClient;
import fr.unicaen.iota.ds.model.CreateResponseType;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.ds.model.EventCreateResp;
import fr.unicaen.iota.ds.model.MultipleEventCreateResp;
import fr.unicaen.iota.dseta.client.DSeTaClient;
import fr.unicaen.iota.epcilon.conf.Configuration;
import fr.unicaen.iota.epcilon.model.EventToPublish;
import fr.unicaen.iota.epcilon.util.SQLQueryModule;
import fr.unicaen.iota.epcilon.util.Utils;
import fr.unicaen.iota.tau.model.Identity;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class XPublisher extends Thread {

    private static final Log log = LogFactory.getLog(XPublisher.class);
    private SQLQueryModule queryOperationsModule;
    private DSeTaClient dsetaClient;
    private DSClient dsClient;
    private Identity identity;

    public XPublisher() {
        queryOperationsModule = new SQLQueryModule();
        identity = new Identity();
        identity.setAsString(Configuration.IDENTITY);
    }

    @Override
    public void run() {
        log.info("Starting event publication thread");
        PublisherMonitor.notification();
        while (true) {
            if (Configuration.IOTA_IDED) {
                dsetaClient = new DSeTaClient(identity, Configuration.DISCOVERY_SERVICE_ADDRESS,
                        Configuration.PKS_FILENAME, Configuration.PKS_PASSWORD,
                        Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
            }
            else {
                dsClient = new DSClient(Configuration.DISCOVERY_SERVICE_ADDRESS);
            }
            log.debug("publisher [RUNNING]");
            List<EventToPublish> events = queryOperationsModule.listEventToPublish(Configuration.EPCIS_TO_DS_POOL_EVENT);
            if (events != null && !events.isEmpty()) {
                List<EventToPublish> eventsSent = proceedEvents(events);
                if (!eventsSent.isEmpty()) {
                    queryOperationsModule.deleteFromDB(eventsSent);
                    log.debug(eventsSent.size() + " events deleted in the list of events to publish");
                }
            }
            try {
                log.debug("publisher [WAITNING]");
                PublisherMonitor.notification();
                Thread.sleep(Configuration.PUBLISHER_FREQUENCY);
            } catch (InterruptedException ex) {
                log.error("the publisher has been interrupted", ex);
                return;
            }
        }
    }

    /**
     * Tries to send the events and returns the events sent.
     * @param toPublishList The events to send.
     * @return The events that could be sent.
     */
    private List<EventToPublish> proceedEvents(List<EventToPublish> toPublishList) {
        List<EventToPublish> eventSentList = new ArrayList<EventToPublish>();
        String dsAddress = Configuration.DISCOVERY_SERVICE_ADDRESS;
        try {
            int rank = 0;
            while (rank < toPublishList.size()) {
                LinkedHashMap<DSEvent, String> events = new LinkedHashMap<DSEvent, String>();
                for (int i = rank; i < rank + Configuration.SIMULTANEOUS_PUBLISH_LIMIT; i++) {
                    if (i > toPublishList.size() - 1) {
                        break;
                    }
                    EventToPublish e = toPublishList.get(i);
                    DSEvent event = new DSEvent();
                    event.setEpc(e.getEpc());
                    event.setEventType(e.getEventType());
                    event.setBizStep(e.getBizStep());
                    event.setEventTime(Utils.dateToXmlCalendar(e.getEventTime()));
                    event.setServiceAddress(Configuration.DEFAULT_QUERY_CLIENT_ADDRESS);
                    event.setServiceType(Configuration.QUERY_CLIENT_TYPE);
                    events.put(event, e.getOwner());
                }
                MultipleEventCreateResp multipleResp;
                if (Configuration.IOTA_IDED) {
                    multipleResp = dsetaClient.multipleEventCreate(events);
                }
                else {
                    multipleResp = dsClient.multipleEventCreate(new ArrayList<DSEvent>(events.keySet()));
                }
                int nbSuccess = 0;
                if (multipleResp.getEventCreateResponses().size() == events.size()) {
                    for (int idResp = 0; idResp < multipleResp.getEventCreateResponses().size(); idResp++) {
                        EventCreateResp resp = multipleResp.getEventCreateResponses().get(idResp);
                        if (CreateResponseType.CREATED_AND_PUBLISHED.equals(resp.getValue())
                                || CreateResponseType.CREATED_NOT_PUBLISHED.equals(resp.getValue())) {
                            eventSentList.add(toPublishList.get(rank + idResp));
                            nbSuccess++;
                        }
                    }
                }
                rank += Configuration.SIMULTANEOUS_PUBLISH_LIMIT;
                log.info(nbSuccess + " events published at " + dsAddress);
            }
        } catch (Exception ex) {
            log.error("Publisher thread interrupted", ex);
        }
        return eventSentList;
    }

    public String epcToEpcClass(String epc) {
        String[] epcTab = epc.split("\\.");
        StringBuilder epcClass = new StringBuilder();
        for (int i = 0; i < epcTab.length - 1; i++) {
            epcClass.append(epcTab[i]);
            if (i < epcTab.length - 2) {
                epcClass.append(".");
            }
        }
        return epcClass.toString();
    }
}
