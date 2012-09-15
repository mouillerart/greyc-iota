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
package fr.unicaen.iota.simulator.server.util;

import org.apache.xerces.parsers.DOMParser;
import fr.unicaen.iota.simulator.server.model.PlaceFIFO;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.HashMap;
import org.jdom.Element;
import org.jdom.input.DOMBuilder;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class PipeSpecParser {

    private PipeSpecParser() {}

    private static final Log log = LogFactory.getLog(PipeSpecParser.class);

    public static Map<String, PlaceFIFO> parse(String file) {
        Map<String, PlaceFIFO> pipes = new HashMap<String, PlaceFIFO>();
        DOMParser parser = new DOMParser();
        try {
            InputStream is = PipeSpecParser.class.getClassLoader().getResourceAsStream(file);
            parser.parse(new InputSource(is));
        } catch (SAXException ex) {
            log.error(file + " is not well formed !");
            return null;
        } catch (IOException ex) {
            log.error(file + " does not exist !");
            return null;
        }
        Document documentDOM = parser.getDocument();
        DOMBuilder builder = new DOMBuilder();
        org.jdom.Document documentJDOM = builder.build(documentDOM);
        log.trace("parsing file " + file + " ... ");
        for (Object obj : documentJDOM.getRootElement().getChildren("pipe")) {
            Element elem = (Element) obj;
            String key = elem.getChild("name").getValue();
            String travelTime = elem.getChild("latency").getValue();
            int pipeSize = -1;
            try {
                pipeSize = Integer.parseInt(elem.getChild("pipeSize").getValue());
            } catch (NumberFormatException ex) {
                log.error(file + " pipeSize must be a number !");
            }
            PlaceFIFO placeFIFO = new PlaceFIFO(key, pipeSize,Long.parseLong(travelTime));
            pipes.put(key, placeFIFO);
        }
        return pipes;
    }
}
