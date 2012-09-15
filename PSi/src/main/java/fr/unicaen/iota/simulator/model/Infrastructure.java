/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.model;

import org.jdom.Element;

/**
 *
 */
public class Infrastructure {

    private final String bizLoc;
    private final String serviceAddress;

    public Infrastructure(String bizLoc, String serviceAddress) {
        this.bizLoc = bizLoc;
        this.serviceAddress = serviceAddress;
    }

    public String getBizLoc() {
        return bizLoc;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public String toXML() {
        StringBuilder str = new StringBuilder();
        str.append("\t\t<infrastructure>\n");
        str.append("\t\t\t<bizLoc>");
        str.append(bizLoc);
        str.append("</bizLoc>\n");
        str.append("\t\t\t<serviceAddress>");
        str.append(serviceAddress);
        str.append("</serviceAddress>\n");
        str.append("\t\t</infrastructure>\n");
        return str.toString();
    }

    public static Infrastructure loadFromXML(Element elem) {
        String bizLoc = null;
        for (Object o : elem.getChildren("bizLoc")) {
            Element e = (Element) o;
            bizLoc = e.getValue();
        }
        String serviceAddress = null;
        for (Object o : elem.getChildren("serviceAddress")) {
            Element e = (Element) o;
            serviceAddress = e.getValue();
        }
        return new Infrastructure(bizLoc, serviceAddress);
    }
}
