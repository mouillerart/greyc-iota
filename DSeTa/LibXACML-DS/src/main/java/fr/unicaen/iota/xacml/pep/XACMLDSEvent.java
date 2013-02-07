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
package fr.unicaen.iota.xacml.pep;

import java.util.Date;

/**
 *
 */
public class XACMLDSEvent {

    private final String owner;
    private final String bizStep;
    private final String epc;
    private final String epcClass;
    private final Date eventTime;

    public String getBizStep() {
        return bizStep;
    }

    public String getEpc() {
        return epc;
    }

    public String getEpcClass() {
        return epcClass;
    }

    public Date getEventTime() {
        return eventTime;
    }

    public String getOwner() {
        return owner;
    }

    public XACMLDSEvent(String owner, String bizStep, String epc, String epcClass, Date eventTime) {
        this.owner = owner;
        this.bizStep = bizStep;
        this.epc = epc;
        this.epcClass = epcClass;
        this.eventTime = eventTime;
    }
}
