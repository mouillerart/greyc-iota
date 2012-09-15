/*
 *  This program is a part of the IoTa Project.
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
package fr.unicaen.iota.epcilon.discovery;

import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.Event;
import fr.unicaen.iota.discovery.client.model.EventInfo;
import fr.unicaen.iota.discovery.client.model.Session;
import fr.unicaen.iota.discovery.client.model.UserInfo;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.epcilon.conf.Configuration;
import fr.unicaen.iota.epcilon.model.EventToPublish;
import fr.unicaen.iota.epcilon.util.SQLQueryModule;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class XPublisher extends Thread {

    private static final Log log = LogFactory.getLog(XPublisher.class);
    private Session session;
    private SQLQueryModule queryOperationsModule;
    private DsClient dsClient;

    public XPublisher() {
        queryOperationsModule = new SQLQueryModule();
        dsClient = new DsClient(Configuration.DISCOVERY_SERVICE_ADDRESS);
    }

    @Override
    public void run() {
        log.info("Starting event publication thread");
        PublisherMonitor.notification();
        while (true) {
            log.debug("publisher [RUNNING]");
            List<EventToPublish> events = queryOperationsModule.listEventToPublish(Configuration.EPCIS_TO_DS_POOL_EVENT);
            if (events != null && !events.isEmpty()) {
                List<EventToPublish> blacklist = proceedEvents(events);
                List<EventToPublish> whitelist = new ArrayList<EventToPublish>();
                for (EventToPublish ev : events) {
                    if (blacklist.contains(ev)) {
                        continue;
                    }
                    whitelist.add(ev);
                }
                if (!whitelist.isEmpty()) {
                    queryOperationsModule.deleteFromDB(whitelist);
                    log.debug(whitelist.size() + " events deleted in the list of events to publish");
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

    private List<EventToPublish> proceedEvents(List<EventToPublish> toPublishList) {
        List<EventToPublish> blackList = new ArrayList<EventToPublish>();
        String dsAddress = Configuration.DISCOVERY_SERVICE_ADDRESS;
        try {
            if (login().equals(Configuration.SESSION_FAILED_ID)) {
                log.warn("Can't connect to the DS with these login/password " + dsAddress);
                blackList.addAll(toPublishList);
                return blackList;
            }
            log.debug("connected to " + dsAddress);
            log.debug("USER INFO");
            if (session == null) {
                throw new Exception("unloged user try to publish");
            }
            UserInfo uInfo = dsClient.userInfo(session.getSessionId(), Configuration.LOGIN);
            String partnerId = uInfo.getPartnerId();
            int tmp = 0;
            while (tmp < toPublishList.size()) {
                List<EventInfo> list = new ArrayList<EventInfo>();
                for (int i = tmp; i < tmp + Configuration.SIMULTANEOUS_PUBLISH_LIMIT; i++) {
                    if (i > toPublishList.size() - 1) {
                        break;
                    }
                    EventToPublish e = toPublishList.get(i);
                    Calendar ets = Calendar.getInstance();
                    Calendar sts = Calendar.getInstance();
                    ets.setTime(e.getEventTime());
                    sts.setTime(new Date());
                    Event event = new Event(0,
                            e.getEpc(),
                            partnerId,
                            uInfo.getUserId(),
                            e.getBizStep(),
                            e.getEventType(), e.getEventClass(), ets, sts,
                            new HashMap<String, String>());
                    EventInfo eInfo = new EventInfo(event, 1, 30);
                    list.add(eInfo);
                }
                tmp += Configuration.SIMULTANEOUS_PUBLISH_LIMIT;
                dsClient.multipleEventCreate(session.getSessionId(), partnerId, list);
                log.info(list.size() + " events published at " + dsAddress);
                Thread.sleep(1);
            }
            logout();
        } catch (RemoteException ex) {
            log.error("Events not published, the DS does not answer", ex);
            blackList.addAll(toPublishList);
        } catch (Exception ex) {
            log.error("Publisher thread interrupted", ex);
            blackList.addAll(toPublishList);
        }
        return blackList;
    }

    private String login() throws Exception {
        log.debug("LOGIN");
        session = dsClient.userLogin(fr.unicaen.iota.discovery.client.util.Configuration.DEFAULT_SESSION, Configuration.LOGIN, Configuration.PASS);
        if (session == null) {
            log.error("BAD LOGIN OR PASSWORD => END");
            return null;
        }
        return session.getSessionId();
    }

    private void logout() throws RemoteException, EnhancedProtocolException {
        log.debug("LOGOUT");
        if (session != null) {
            dsClient.userLogout(session.getSessionId());
            session = null;
        }
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
