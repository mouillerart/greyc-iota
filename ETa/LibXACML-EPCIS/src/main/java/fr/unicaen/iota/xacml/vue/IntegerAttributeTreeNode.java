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
package fr.unicaen.iota.xacml.vue;

public class IntegerAttributeTreeNode {

    private Long minInteger;
    private Long maxInteger;
    private String permission;

    public Long getMaxInteger() {
        return maxInteger;
    }

    public void setMaxInteger(Long maxInteger) {
        this.maxInteger = maxInteger;
    }

    public Long getMinInteger() {
        return minInteger;
    }

    public void setMinDate(Long minInteger) {
        this.minInteger = minInteger;
    }

    public IntegerAttributeTreeNode(Long minL, Long maxL, String f) {
        maxInteger = maxL;
        minInteger = minL;
        permission = f;
    }

    @Override
    public String toString() {
        return permission + ": [" + minInteger + " ; " + maxInteger + ")";
    }
}
