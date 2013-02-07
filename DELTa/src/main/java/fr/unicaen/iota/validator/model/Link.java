/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.validator.model;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Link {

    private int supervisorRank = 0;
    private Map<Integer,Long> timeResponseSupervisor;
    private String serviceAddress;
    private boolean activeAnalyse;

    public Link(boolean active, String serviceAddress) {
        this.activeAnalyse = active;
        this.serviceAddress = serviceAddress;
        this.timeResponseSupervisor = new HashMap<Integer, Long>();
    }
    

    /**
     * @return the activeAnalyse
     */
    public boolean isActiveAnalyse() {
        return activeAnalyse;
    }

    /**
     * @param activeAnalyse the activeAnalyse to set
     */
    public void setActiveAnalyse(boolean activeAnalyse) {
        this.activeAnalyse = activeAnalyse;
    }

    /**
     * @return the serviceAddress
     */
    public String getServiceAddress() {
        return serviceAddress;
    }

    /**
     * @param serviceAddress the serviceAddress to set
     */
    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    /**
     * @return the timeResponseSupervisor
     */
    public Map<Integer, Long> getTimeResponseSupervisor() {
        return timeResponseSupervisor;
    }

    /**
     * @param timeResponseSupervisor the timeResponseSupervisor to set
     */
    public void setTimeResponseSupervisor(Map<Integer, Long> timeResponseSupervisor) {
        this.timeResponseSupervisor = timeResponseSupervisor;
    }

    public synchronized void addTimeResponse(Long l){
        timeResponseSupervisor.put(supervisorRank, l);
        supervisorRank++;
    }
}
