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

import fr.unicaen.iota.validator.model.DSLink;
import fr.unicaen.iota.validator.model.EPCISLink;
import fr.unicaen.iota.validator.model.Infrastructure;
import fr.unicaen.iota.validator.model.Link;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.parsers.DOMParser;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 */
public class IOTA extends HashMap<String, EPCISLink> {

    private static final Log log = LogFactory.getLog(IOTA.class);

    public List<Link> getAllLinks() {
        List<Link> res = new ArrayList<Link>();
        for (EPCISLink l : values()) {
            res.add(l);
            if (!res.contains(l.getDSLink())) {
                res.add(l.getDSLink());
            }
        }
        return res;
    }

    public String toXML() {
        StringBuilder result = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        result.append("<iota>\n");
        for (String key : this.keySet()) {
            result.append(this.get(key).toXML());
        }
        result.append("</iota>\n");
        return result.toString();
    }

    public synchronized DSLink getDSLink(Infrastructure infra) {
        if (!this.containsKey(infra.getBizLoc())) {
            return null;
        }
        return this.get(infra.getBizLoc()).getDSLink();
    }

    public void loadFromXML() throws SAXException, IOException {
        DOMParser parser = new DOMParser();
        parser.parse(Configuration.IOTA_XML_SCHEMA);
        Document documentDOM = parser.getDocument();
        DOMBuilder builder = new DOMBuilder();
        org.jdom.Document documentJDOM = builder.build(documentDOM);
        log.trace("Parsing file " + Configuration.IOTA_XML_SCHEMA + " ... ");
        List<DSLink> witnessList = new ArrayList<DSLink>();
        for (Object obj : documentJDOM.getRootElement().getChildren("epcis")) {
            Element elem = (Element) obj;
            String key = elem.getChild("infrastructure").getChild("bizLoc").getValue();
            DSLink value = new DSLink(elem.getChild("dsLink").getChild("serviceAddress").getValue(),
                    elem.getChild("dsLink").getChild("wildCardAccount").getChild("login").getValue(),
                    elem.getChild("dsLink").getChild("wildCardAccount").getChild("password").getValue(),
                    Boolean.parseBoolean(elem.getChild("dsLink").getAttribute("activeAnalyse").getValue()));
            int index;
            if ((index = witnessList.indexOf(value)) != -1) {
                value = witnessList.get(index);
            } else {
                witnessList.add(value);
            }
            EPCISLink epcisLink = new EPCISLink(elem.getChild("infrastructure").getChild("serviceAddress").getValue(),
                    key, value, Boolean.parseBoolean(elem.getAttribute("activeAnalyse").getValue()));
            this.put(key, epcisLink);
        }
    }
}
