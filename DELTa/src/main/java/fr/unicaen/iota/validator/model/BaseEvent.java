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

import org.fosstrak.epcis.model.EPCISEventType;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.jdom.Element;

/**
 *
 */
public abstract class BaseEvent {

    private String bizStep;
    private String disposition;
    private String readPoint;
    private Map<String, String> extensions;
    protected Infrastructure infrastructure;

    protected String propertiesToXML() {
        StringBuilder str = new StringBuilder();
        str.append("\t\t<bizStep>");
        str.append(bizStep);
        str.append("</bizStep>\n");
        str.append("\t\t<disposition>");
        str.append(disposition);
        str.append("</disposition>\n");
        str.append("\t\t<readPoint>");
        str.append(readPoint);
        str.append("</readPoint>\n");
        if (extensions != null) {
            str.append("\t\t<extensions>\n");
            for (String k : extensions.keySet()) {
                str.append("\t\t\t<property name=\"");
                str.append(k);
                str.append("\" value=\"");
                str.append(extensions.get(k));
                str.append("\" />\n");
            }
            str.append("\t\t</extensions>\n");
        }
        return str.toString();
    }

    public BaseEvent(Long id, Infrastructure infrastructure) {
        setInfrastructure(infrastructure);
        setBizStep("urn:unicaen:iotatester:bizstep:xxxx:tester");
        setDisposition("urn:unicaen:iotatester:disp:xxxx:tester");
        setReadPoint(infrastructure.getBizLoc() + "," + id);
        setExtensions(null);
    }

    public BaseEvent() {
        setInfrastructure(null);
        setBizStep(null);
        setDisposition(null);
        setReadPoint(null);
        setExtensions(null);
    }

    public String getBizStep() {
        return bizStep;
    }

    public void setBizStep(String bizStep) {
        this.bizStep = bizStep;
    }

    public String getDisposition() {
        return disposition;
    }

    public void setDisposition(String disposition) {
        this.disposition = disposition;
    }

    public String getReadPoint() {
        return readPoint;
    }

    public void setReadPoint(String readPoint) {
        this.readPoint = readPoint;
    }

    public Map<String, String> getExtensions() {
        return extensions;
    }

    public void setExtensions(Map<String, String> extensions) {
        this.extensions = extensions;
    }

    public Infrastructure getInfrastructure() {
        return infrastructure;
    }

    public void setInfrastructure(Infrastructure service) {
        this.infrastructure = service;
    }

    public void loadFromXML(Element elem) {
        for (Object o : elem.getChildren("infrastructure")) {
            Element e = (Element) o;
            Infrastructure infra = new Infrastructure();
            infra.loadFromXML(e);
            infrastructure = infra;
        }
        for (Object o : elem.getChildren("bizStep")) {
            Element e = (Element) o;
            bizStep = e.getValue();
        }
        for (Object o : elem.getChildren("disposition")) {
            Element e = (Element) o;
            disposition = e.getValue();
        }
        for (Object o : elem.getChildren("readPoint")) {
            Element e = (Element) o;
            readPoint = e.getValue();
        }
        extensions = new HashMap<String, String>();
        for (Object o : elem.getChildren("extensions")) {
            Element e = (Element) o;
            for (Object o2 : e.getChildren()) {
                Element e2 = (Element) o2;
                extensions.put(e2.getAttributeValue("name"), e2.getAttributeValue("value"));
            }
        }
    }

    public abstract boolean isContainedIn(Collection<EPCISEventType> list);
}
