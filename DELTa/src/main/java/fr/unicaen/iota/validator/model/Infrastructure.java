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

import org.jdom.Element;

/**
 *
 */
public class Infrastructure {

    private String bizLoc;
    private String serviceAddress;

    public Infrastructure(String bizLoc, String serviceAddress) {
        this.bizLoc = bizLoc;
        this.serviceAddress = serviceAddress;
    }

    public Infrastructure() {
        this.bizLoc = null;
        this.serviceAddress = null;
    }

    public String getBizLoc() {
        return bizLoc;
    }

    public void setBizLoc(String bizLoc) {
        this.bizLoc = bizLoc;
    }

    public String getServiceAddress() {
        return serviceAddress;
    }

    public void setServiceAddress(String serviceAddress) {
        this.serviceAddress = serviceAddress;
    }

    public void loadFromXML(Element elem) {
        for (Object o : elem.getChildren("bizLoc")) {
            Element e = (Element) o;
            bizLoc = e.getValue();
        }
        for (Object o : elem.getChildren("serviceAddress")) {
            Element e = (Element) o;
            serviceAddress = e.getValue();
        }

    }

    @Override
    public String toString() {
        return bizLoc + " => " + serviceAddress;
    }

    @Override
    public int hashCode() {
        return bizLoc.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (obj instanceof Infrastructure) {
            Infrastructure that = (Infrastructure) obj;
            return ((this.bizLoc == null && that.bizLoc == null)
                    || this.bizLoc.equals(that.bizLoc))
                && ((this.serviceAddress == null && that.bizLoc == null)
                    || this.serviceAddress.equals(that.serviceAddress));
        }
        return false;
    }
}
