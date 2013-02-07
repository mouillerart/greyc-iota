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
package fr.unicaen.iota.validator;

import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.validator.model.BaseEvent;
import fr.unicaen.iota.validator.model.EPC;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class ResultRaw {

    private List<BaseEvent> epcisEvents;
    private List<DSEvent> dsEvents;
    private List<DSEvent> dsToDsEvents;
    private List<EPC> containerList;

    public ResultRaw(List<EPC> containerList) {
        epcisEvents = new ArrayList<BaseEvent>();
        dsEvents = new ArrayList<DSEvent>();
        dsToDsEvents = new ArrayList<DSEvent>();
        this.containerList = containerList;
    }

    public List<EPC> getContainerList() {
        return containerList;
    }

    /**
     * @return the epcisEvents
     */
    public List<BaseEvent> getEpcisEvents() {
        return epcisEvents;
    }

    /**
     * @param epcisEvents the epcisEvents to set
     */
    public void setEpcisEvents(List<BaseEvent> epcisEvents) {
        this.epcisEvents = epcisEvents;
    }

    /**
     * @return the dsEvents
     */
    public List<DSEvent> getDsEvents() {
        return dsEvents;
    }

    /**
     * @param dsEvents the dsEvents to set
     */
    public void setDsEvents(List<DSEvent> dsEvents) {
        this.dsEvents = dsEvents;
    }

    /**
     * @return the dsToDsEvents
     */
    public List<DSEvent> getDsToDsEvents() {
        return dsToDsEvents;
    }

    /**
     * @param dsToDsEvents the dsToDsEvents to set
     */
    public void setDsToDsEvents(List<DSEvent> dsToDsEvents) {
        this.dsToDsEvents = dsToDsEvents;
    }

    /**
     * @return the containerList
     */
    public EPC getContainer(String epc) {
        for (EPC container : containerList) {
            if (container.getEpc().equals(epc)) {
                return container;
            }
        }
        return null;
    }

    /**
     * @param containerList the containerList to set
     */
    public void setContainerList(List<EPC> containerList) {
        this.containerList = containerList;
    }
}
