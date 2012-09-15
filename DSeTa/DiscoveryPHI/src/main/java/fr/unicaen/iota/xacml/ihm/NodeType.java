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
package fr.unicaen.iota.xacml.ihm;

/**
 *
 */
public enum NodeType {

    policiesNode,
    policyNode,
    bizStepFilterNode,
    epcFilterNode,
    epcClassFilterNode,
    eventTimeFilterNode,
    methodFilterNode,
    bizStepFilterGroupNode,
    epcFilterGroupNode,
    epcClassFilterGroupNode,
    eventTimeFilterGroupNode,
    methodFilterGroupNode,
    rulesNode,
    userNode,
    usersNode;

    public boolean isFilter() {
        return this == bizStepFilterNode
                || this == epcFilterNode
                || this == epcClassFilterNode
                || this == eventTimeFilterNode
                || this == methodFilterNode;
    }

    public boolean isFilterGroup() {
        return this == bizStepFilterGroupNode
                || this == epcFilterGroupNode
                || this == epcClassFilterGroupNode
                || this == eventTimeFilterGroupNode
                || this == methodFilterGroupNode;
    }

    public boolean isExpandable() {
        return this == bizStepFilterGroupNode
                || this == epcFilterGroupNode
                || this == epcClassFilterGroupNode
                || this == eventTimeFilterGroupNode
                || this == policyNode
                || this == policiesNode
                || this == rulesNode
                || this == usersNode
                || this == methodFilterGroupNode;
    }
}
