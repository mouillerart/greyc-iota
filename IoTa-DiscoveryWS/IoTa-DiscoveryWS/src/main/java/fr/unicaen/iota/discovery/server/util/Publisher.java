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
package fr.unicaen.iota.discovery.server.util;

import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.EventInfo;
import fr.unicaen.iota.discovery.client.model.UserInfo;
import fr.unicaen.iota.discovery.client.util.Configuration;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import fr.unicaen.iota.discovery.server.hibernate.Event;
import fr.unicaen.iota.discovery.server.hibernate.EventToPublish;
import fr.unicaen.iota.discovery.server.query.QueryOperationsModule;
import fr.unicaen.iota.nu.EPCUtilities;
import fr.unicaen.iota.nu.EPCUtilities.InvalidFormatException;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.nu.ONSOperation;
import java.rmi.RemoteException;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class Publisher extends Thread {

    private static final Log log = LogFactory.getLog(Publisher.class);
    private QueryOperationsModule queryOperationsModule;
    private ONSOperation ons;
    private transient fr.unicaen.iota.discovery.client.model.Session sessionInfo;
    private transient DsClient dsClient;

    public Publisher() {
        ons = new ONSOperation(Constants.ONS_HOSTS);
        queryOperationsModule = new QueryOperationsModule();
    }

    @Override
    public void run() {
        log.trace("Events publication to the referent DS => start thread");
        PublisherMonitor.notification();
        if (!Constants.MULTI_DS_ARCHITECTURE) {
            log.info("The publisher was not used and terminated normally.");
            return;
        }
        // TODO: use a Timer
        while (true) {
            log.debug("publisher [RUNNING]");
            Collection<EventToPublish> events = queryOperationsModule.eventToPublishLookup(Constants.DS_TO_DS_POOL_EVENT);
            if (events != null && !events.isEmpty()) {
                Collection<EventToPublish> blacklist = proceedEvents(events);
                Collection<EventToPublish> whitelist = new ArrayList<EventToPublish>();
                for (EventToPublish ev : events) {
                    if (blacklist.contains(ev)) {
                        continue;
                    }
                    whitelist.add(ev);
                }
                if (!whitelist.isEmpty()) {
                    queryOperationsModule.eventToPublishDelete(whitelist);
                    log.info(whitelist.size() + " events removed from the events to publish list");
                }
                if (!blacklist.isEmpty()) {
                    queryOperationsModule.eventToPublishEnque(blacklist);
                    log.info(blacklist.size() + " events changed their lastupdate field");
                }
            }
            try {
                log.debug("publisher [WAITING]");
                PublisherMonitor.notification();
                Thread.sleep(Constants.PUBLISHER_FREQUENCY);
            } catch (InterruptedException ex) {
                log.error("The publisher has been interrupted!", ex);
                return;
            }
        }
    }

    private String login(String dsAddress) throws Exception {
        log.debug("LOGIN: " + dsAddress);
        try {
            dsClient = new DsClient(dsAddress);
            sessionInfo = dsClient.userLogin(Configuration.DEFAULT_SESSION, Constants.DS_LOGIN, Constants.DS_PASSWORD);
        } catch (EnhancedProtocolException ex) {
            log.error("BAD LOGIN OR PASSWORD => END (" + ex.getMessage() + ")");
            return null;
        }
        return sessionInfo.getSessionId();
    }

    private void logout() throws RemoteException, EnhancedProtocolException {
        log.debug("LOGOUT");
        if (sessionInfo != null) {
            dsClient.userLogout(sessionInfo.getSessionId());
            sessionInfo = null;
            dsClient = null;
        }
    }

    private boolean notMe(String addr) throws RemoteException, EnhancedProtocolException {
        String identity = new DsClient(addr).hello(Configuration.DEFAULT_SESSION);
        return !Constants.SERVICE_ID.equals(identity);
    }

    private List<EventToPublish> proceedEvents(Collection<EventToPublish> events) {
        List<EventToPublish> blackList = new ArrayList<EventToPublish>();
        Map<String, List<EventToPublish>> map = splitByEPCBase(events);
        for (String key : map.keySet()) { // sort by epcBase
            Map<ONSEntryType, String> dsAddresses = ons.queryONS(key);
            List<EventToPublish> toPublishList = map.get(key);
            if (dsAddresses == null) {
                log.error(key + " could not get the referent DS address!");
                blackList.addAll(toPublishList);
                continue;
            }
            String dsAddress = dsAddresses.get(ONSEntryType.ds);
            if (dsAddress == null) {
                log.error("The ONS doesn't know the address of the referent DS for the EPC code: " + key);
                blackList.addAll(toPublishList);
                continue;
            }
            try {
                if (!notMe(dsAddress)) {
                    log.warn("Event not published: already in the corresponding DS.");
                    continue;
                } else {
                    String SESSION_ID;
                    if ((SESSION_ID = login(dsAddress)).equals(Constants.SESSION_FAILED_ID)) {
                        log.warn("Can't connect to the DS with provided login and password" + dsAddress);
                        blackList.addAll(toPublishList);
                        continue;
                    }
                    log.debug("publisher session id: " + SESSION_ID);
                    log.debug("connected to " + dsAddress);
                    log.debug("USER INFO");
                    if (sessionInfo == null) {
                        throw new Exception("not logged user tryed to publish");
                    }
                    UserInfo userInfo = dsClient.userInfo(sessionInfo.getSessionId(), Constants.DS_LOGIN);
                    String partnerId = userInfo.getPartnerId();
                    log.debug("partner: " + partnerId);
                    int tmp = 0;
                    while (tmp < toPublishList.size()) {
                        List<EventInfo> list = new ArrayList<EventInfo>();
                        for (int i = tmp; i < tmp + Constants.SIMULTANEOUS_PUBLISH_LIMIT; i++) {
                            if (i > toPublishList.size() - 1) {
                                break;
                            }
                            Event e = toPublishList.get(i).getEvent();
                            Calendar ets = Calendar.getInstance();
                            ets.setTime(e.getEventTimeStamp());
                            Calendar sts = Calendar.getInstance();
                            sts.setTime(e.getSourceTimeStamp());
                            fr.unicaen.iota.discovery.client.model.Event dSEvent = new fr.unicaen.iota.discovery.client.model.Event(0, e.getEpc(), partnerId, Constants.DS_LOGIN, e.getBizStep(), e.getEventType(), e.getEPCClass(), ets, sts, new HashMap<String, String>());
                            EventInfo eventInfo = new EventInfo(dSEvent, 1, 30);
                            list.add(eventInfo);
                        }
                        tmp += Constants.SIMULTANEOUS_PUBLISH_LIMIT;
                        dsClient.multipleEventCreate(sessionInfo.getSessionId(), partnerId, list);
                        log.debug(list.size() + " events published to " + dsAddress);
                    }
                    logout();
                }
            } catch (RemoteException ex) {
                log.error("Events can't be published; the DS won't answer.", ex);
                blackList.addAll(toPublishList);
            } catch (Exception ex) {
                log.error("Publisher thread interrupted", ex);
                blackList.addAll(toPublishList);
            }
        }
        return blackList;
    }

    private Map<String, List<EventToPublish>> splitByEPCBase(Collection<EventToPublish> list) {
        Map<String, List<EventToPublish>> result = new HashMap<String, List<EventToPublish>>();
        for (EventToPublish e : list) {
            String epcBase = e.getEvent().getEpc();
            if (result.containsKey(epcBase)) {
                result.get(epcBase).add(e);
            } else {
                List<EventToPublish> toPublish = new ArrayList<EventToPublish>();
                toPublish.add(e);
                result.put(epcBase, toPublish);
            }
        }
        return result;
    }
}
