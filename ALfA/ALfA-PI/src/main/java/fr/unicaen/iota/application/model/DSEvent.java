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
package fr.unicaen.iota.application.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 *
 */
public class DSEvent implements Serializable {

    private final String EPC;
    private final String referenceAddress;
    private final String BizStep;
    private final Timestamp eventTime;

    public DSEvent(String EPC, String referenceAddress, String bizStep, Timestamp eventTime) {
        this.EPC = EPC;
        this.referenceAddress = referenceAddress;
        this.BizStep = bizStep;
        this.eventTime = eventTime;
    }

    /**
     * @return the EPC
     */
    public String getEPC() {
        return EPC;
    }

    /**
     * @return the referenceAddress
     */
    public String getReferenceAddress() {
        return referenceAddress;
    }

    /**
     * @return the BizStep
     */
    public String getBizStep() {
        return BizStep;
    }

    /**
     * @return the eventTime
     */
    public Timestamp getEventTime() {
        return eventTime;
    }


    @Override
    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (that == this) {
            return true;
        }
        if (that instanceof DSEvent) {
            DSEvent evt = (DSEvent) that;
            return this.getBizStep().equals(evt.getBizStep())
                    && this.getEPC().equals(evt.getEPC())
                    && this.getReferenceAddress().equals(evt.getReferenceAddress());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.EPC != null ? this.EPC.hashCode() : 0);
        hash = 17 * hash + (this.referenceAddress != null ? this.referenceAddress.hashCode() : 0);
        hash = 17 * hash + (this.BizStep != null ? this.BizStep.hashCode() : 0);
        return hash;
    }
}
