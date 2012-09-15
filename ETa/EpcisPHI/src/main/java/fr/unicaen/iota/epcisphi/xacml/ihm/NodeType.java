/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.epcisphi.xacml.ihm;

public enum NodeType {

    policiesNode,
    policyNode,
    bizStepFilterNode,
    epcFilterNode,
    eventTypeFilterNode,
    eventTimeFilterNode,
    recordTimeFilterNode,
    operationFilterNode,
    parentIdFilterNode,
    childEpcFilterNode,
    quantityFilterNode,
    readPointFilterNode,
    bizLocFilterNode,
    dispositionFilterNode,
    methodFilterNode,
    bizStepFilterGroupNode,
    epcFilterGroupNode,
    eventTypeFilterGroupNode,
    eventTimeFilterGroupNode,
    recordTimeFilterGroupNode,
    operationFilterGroupNode,
    parentIdFilterGroupNode,
    childEpcFilterGroupNode,
    quantityFilterGroupNode,
    readPointFilterGroupNode,
    bizLocFilterGroupNode,
    dispositionFilterGroupNode,
    methodFilterGroupNode,
    rulesNode,
    userNode,
    usersNode;

    public boolean isFilter() {
        return this == bizStepFilterNode
                || this == epcFilterNode
                || this == eventTypeFilterNode
                || this == eventTimeFilterNode
                || this == recordTimeFilterNode
                || this == operationFilterNode
                || this == parentIdFilterNode
                || this == childEpcFilterNode
                || this == quantityFilterNode
                || this == readPointFilterNode
                || this == bizLocFilterNode
                || this == dispositionFilterNode
                || this == methodFilterNode;
    }

    public boolean isFilterGroup() {
        return this == bizStepFilterGroupNode
                || this == epcFilterGroupNode
                || this == eventTypeFilterGroupNode
                || this == eventTimeFilterGroupNode
                || this == recordTimeFilterGroupNode
                || this == operationFilterGroupNode
                || this == parentIdFilterGroupNode
                || this == childEpcFilterGroupNode
                || this == quantityFilterGroupNode
                || this == readPointFilterGroupNode
                || this == bizLocFilterGroupNode
                || this == dispositionFilterGroupNode
                || this == methodFilterGroupNode;
    }

    public boolean isExpandable() {
        return this == bizStepFilterGroupNode
                || this == epcFilterGroupNode
                || this == eventTypeFilterGroupNode
                || this == eventTimeFilterGroupNode
                || this == recordTimeFilterGroupNode
                || this == operationFilterGroupNode
                || this == parentIdFilterGroupNode
                || this == childEpcFilterGroupNode
                || this == quantityFilterGroupNode
                || this == readPointFilterGroupNode
                || this == bizLocFilterGroupNode
                || this == dispositionFilterGroupNode
                || this == policyNode
                || this == policiesNode
                || this == rulesNode
                || this == usersNode
                || this == methodFilterGroupNode;
    }
}
