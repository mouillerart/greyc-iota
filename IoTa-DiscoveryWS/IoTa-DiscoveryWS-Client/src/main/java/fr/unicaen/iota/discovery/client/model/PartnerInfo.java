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
package fr.unicaen.iota.discovery.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class PartnerInfo {

    private final int uid;
    private final String partnerId;
    private final List<Service> serviceList;

    public PartnerInfo(int uid, String partnerId) {
        this.uid = uid;
        this.partnerId = partnerId;
        this.serviceList = new ArrayList<Service>();
    }

    /**
     * @return the uid
     */
    public int getUid() {
        return uid;
    }

    /**
     * @return the partnerId
     */
    public String getPartnerId() {
        return partnerId;
    }

    /**
     * @return the serviceList
     */
    public List<Service> getServiceList() {
        return serviceList;
    }

    public void addService(Service service) {
        serviceList.add(service);
    }
}
