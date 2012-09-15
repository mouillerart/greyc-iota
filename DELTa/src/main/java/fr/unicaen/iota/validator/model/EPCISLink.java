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

/**
 *
 */
public class EPCISLink extends Link {

    private String bizLoc;
    private DSLink dSLink;

    public EPCISLink(String epcisAddress, String bizLoc, DSLink dslink, boolean activeAnalyse) {
        super(activeAnalyse, epcisAddress);
        this.bizLoc = bizLoc;
        this.dSLink = dslink;
    }

    /**
     * @return the epcisAddress
     */
    public String getEpcisAddress() {
        return getServiceAddress();
    }

    /**
     * @param epcisAddress the epcisAddress to set
     */
    public void setEpcisAddress(String epcisAddress) {
        setServiceAddress(epcisAddress);
    }

    /**
     * @return the bizLoc
     */
    public String getBizLoc() {
        return bizLoc;
    }

    /**
     * @param bizLoc the bizLoc to set
     */
    public void setBizLoc(String bizLoc) {
        this.bizLoc = bizLoc;
    }

    /**
     * @return the dSLink
     */
    public DSLink getDSLink() {
        return dSLink;
    }

    /**
     * @param dSLink the dSLink to set
     */
    public void setDSLink(DSLink dSLink) {
        this.dSLink = dSLink;
    }

    public String toXML() {
        StringBuilder result = new StringBuilder();
        result.append("<epcis activeAnalyse=\"");
        result.append(isActiveAnalyse());
        result.append("\">\n");
        result.append("<infrastructure>\n");
        result.append("<bizLoc>");
        result.append(bizLoc);
        result.append("</bizLoc>\n");
        result.append("<serviceAddress>");
        result.append(getEpcisAddress());
        result.append("</serviceAddress>\n");
        result.append("</infrastructure>\n");
        result.append(dSLink.toXML());
        result.append("</epcis>\n");
        return result.toString();
    }
}
