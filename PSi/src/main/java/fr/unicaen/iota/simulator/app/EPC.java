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
package fr.unicaen.iota.simulator.app;

import fr.unicaen.iota.simulator.model.BaseEvent;
import fr.unicaen.iota.simulator.util.Config;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class EPC {

    private static final Log log = LogFactory.getLog(EPC.class);
    private String epc;
    private List<BaseEvent> generatedEvents;
    private List<EPC> children;
    private String parent;

    public EPC(String s) {
        this.epc = s;
        generatedEvents = new ArrayList<BaseEvent>();
        children = new ArrayList<EPC>();
        parent = null;
    }

    @Override
    public String toString() {
        return this.getEpc();
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    /**
     * @return the serviceAddressList
     */
    public List<BaseEvent> getGeneratedEvents() {
        return generatedEvents;
    }

    /**
     * @param serviceAddressList the serviceAddressList to set
     */
    public void setGeneratedEvents(Collection<BaseEvent> serviceAddressList) {
        this.generatedEvents.clear();
        this.generatedEvents.addAll(serviceAddressList);
    }

    /**
     * @return the children
     */
    public List<EPC> getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(Collection<EPC> childs) {
        this.children.clear();
        this.children.addAll(childs);
    }

    public void clearChildren() {
        this.children.clear();
    }

    public void save() {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        xml.append("<tracerouteEPC>\n");
        xml.append(this.toXML(0));
        xml.append("</tracerouteEPC>\n");
        try {
            writeFile(xml.toString());
            log.trace("traceroute EPC generated: " + Config.EVENT_FILE_SAVER_FOLDER + epc + ".xml");
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
    }

    public String toXML(int i) {
        StringBuilder gap = new StringBuilder();
        for (int j = 0; j < i; j++) {
            gap.append("\t");
        }
        StringBuilder res = new StringBuilder();
        res.append(gap);
        res.append("<EPCSimulated epc=\"");
        res.append(epc);
        res.append("\">\n");
        if (parent != null) {
            res.append(gap);
            res.append("\t<parent>");
            res.append(parent);
            res.append("</parent>\n");
        }
        for (BaseEvent be : generatedEvents) {
            res.append(be.toXML());
        }
        res.append(gap);
        res.append("</EPCSimulated>\n");
        if (!children.isEmpty()) {
            res.append(gap);
            res.append("<childs>\n");
            for (EPC child : children) {
                res.append(child.toXML(i + 1));
            }
            res.append(gap);
            res.append("</childs>\n");
        }
        return res.toString();
    }

    public void writeFile(String str) throws IOException {
        FileWriter fw = new FileWriter(Config.EVENT_FILE_SAVER_FOLDER + epc + ".xml");
        fw.write(str);
        fw.close();
    }

    /**
     * @return the parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(String parent) {
        this.parent = parent;
    }
}
