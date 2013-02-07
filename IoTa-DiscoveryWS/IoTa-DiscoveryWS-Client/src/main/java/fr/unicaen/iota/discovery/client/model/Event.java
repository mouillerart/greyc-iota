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
package fr.unicaen.iota.discovery.client.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class Event {

    private final int eventId;
    private final String objectId;
    private final String partnerId;
    private final String userId;
    private final String bizStep;
    private final String eventType;
    private final String eventClass;
    private final Calendar eventTimeStamp;
    private final Calendar sourceTimeStamp;
    private final List<Service> serviceList;
    private final Map<String, String> extensions;

    public Event(int eventId, String objectId, String partnerId, String userId, String bizStep, String eventType,
            String eventClass, Calendar ets, Calendar sts, Map<String, String> extensions) {
        this.eventId = eventId;
        this.objectId = objectId;
        this.partnerId = partnerId;
        this.userId = userId;
        this.bizStep = bizStep;
        this.eventType = eventType;
        this.eventClass = eventClass;
        this.eventTimeStamp = ets;
        this.sourceTimeStamp = sts;
        this.serviceList = new ArrayList<Service>();
        this.extensions = extensions;
    }

    public void addService(Service service) {
        serviceList.add(service);
    }

    /**
     * @return the eventId
     */
    public int getEventId() {
        return eventId;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * @return the partnerId
     */
    public String getPartnerId() {
        return partnerId;
    }

    /**
     * @return the userId
     */
    public String getUserId() {
        return userId;
    }

    /**
     * @return the bizStep
     */
    public String getBizStep() {
        return bizStep;
    }

    /**
     * @return the eventType
     */
    public String getEventType() {
        return eventType;
    }

    /**
     * @return the eventClass
     */
    public String getEventClass() {
        return eventClass;
    }

    /**
     * @return the eventTimeStamp
     */
    public Calendar getEventTimeStamp() {
        return eventTimeStamp;
    }

    /**
     * @return the sourceTimeStamp
     */
    public Calendar getSourceTimeStamp() {
        return sourceTimeStamp;
    }

    /**
     * @return the serviceList
     */
    public List<Service> getServiceList() {
        return serviceList;
    }

    /**
     * @return the extensions
     */
    public Map<String, String> getExtensions() {
        return extensions;
    }
}
