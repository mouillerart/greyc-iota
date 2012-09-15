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
package fr.unicaen.iota.simulator.pnk;

import de.huberlin.informatik.pnk.kernel.Extendable;
import de.huberlin.informatik.pnk.kernel.Extension;
import fr.unicaen.iota.simulator.app.EPC;
import fr.unicaen.iota.simulator.model.*;
import fr.unicaen.iota.simulator.util.StatControler;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.captureclient.CaptureClientException;
import org.fosstrak.epcis.model.ActionType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 *
 */
public class Event extends Extension {

    private static final Log log = LogFactory.getLog(Event.class);
    private BaseEvent event;

    public Event(Extendable e) {
        super(e);
    }

    public Event(Extendable e, String value) {
        super(e, value);
    }

    @Override
    public String defaultToString() {
        return readFileAsString("eventSpecification/default.xml");
    }

    private void publish(BaseEvent evt) throws IOException, JAXBException, CaptureClientException {
        Long d1 = new Date().getTime();
        evt.publish();
        Long d2 = new Date().getTime();
        StatControler.addPublication(evt.getInfrastructure().getBizLoc());
        StatControler.addPublicationTimestamp((int) (d2 - d1), evt.getInfrastructure().getBizLoc());
    }

    public void publish(Collection<EPC> epcList) {
        if (getEvent() instanceof ObjectEvent) {
            ObjectEvent oe = (ObjectEvent) getEvent();
            ObjectEvent toSave = new ObjectEvent(oe);
            List<String> list = new ArrayList<String>();
            for (EPC epc : epcList) {
                list.add(epc.getEpc());
                epc.getGeneratedEvents().add(toSave);  // for unit test
            }
            oe.setEpcList(list);
            toSave.setEpcList(oe.getEpcList());
        }
        while (true) {
            try {
                publish(getEvent());
                break;
            } catch (Exception ex) {
                StatControler.addPublicationTimeout(getEvent().getInfrastructure().getBizLoc());
                log.error("[ " + getEvent().getInfrastructure().getServiceAddress() + " ] trying again ...", ex);
            }
        }
    }

    public void publish(Collection<EPC> epcList, EPC parent) {
        String parentId = parent.getEpc();
        if (getEvent() instanceof AggregationEvent) {
            AggregationEvent ae = (AggregationEvent) getEvent();
            if (ae.getAction().equals(ActionType.ADD)) {
                AggregationEvent toSave = new AggregationEvent(ae);
                List<String> list = new ArrayList<String>();
                for (EPC epc : epcList) {
                    list.add(epc.getEpc());
                    epc.setParent(parentId);
                }
                ae.setChildEpcs(list);
                ae.setParentId(parentId);
                toSave.setChildEpcs(ae.getChildEpcs());
                toSave.setParentId(parentId);
                toSave.setAction(ae.getAction());
                parent.getGeneratedEvents().add(toSave); // for unit test
                parent.setChildren(epcList);      // for unit test
            } else if (ae.getAction().equals(ActionType.DELETE)) {
                AggregationEvent toSave = new AggregationEvent(ae);
                List<String> list = new ArrayList<String>();
                for (EPC epc : epcList) {
                    list.add(epc.getEpc());
                    epc.setParent(null);
                }
                ae.setChildEpcs(list);
                ae.setParentId(parentId);
                toSave.setChildEpcs(ae.getChildEpcs());
                toSave.setParentId(parentId);
                toSave.setAction(ae.getAction());
                parent.getGeneratedEvents().add(toSave); // for unit test
                parent.clearChildren();      // for unit test
            }
        }
        if (getEvent() instanceof TransactionEvent) {
            TransactionEvent te = (TransactionEvent) getEvent();
            TransactionEvent toSave = new TransactionEvent(te);
            List<String> list = new ArrayList<String>();
            for (EPC epc : epcList) {
                list.add(epc.getEpc());
            }
            te.setEpcList(list);
            te.setParentId(parentId);
            toSave.setEpcList(te.getEpcList());
            parent.getGeneratedEvents().add(te); // for unit test
            parent.setChildren(epcList);      // for unit test
        }
        while (true) {
            try {
                publish(getEvent());
                break;
            } catch (Exception ex) {
                StatControler.addPublicationTimeout(getEvent().getInfrastructure().getBizLoc());
                log.error("[ " + getEvent().getInfrastructure().getServiceAddress() + " ] trying again ...", ex);
            }
        }
    }

    /**
    internState in extenState umwandeln
     */
    private String externRepresentation() {
        return "<- event ->";
    }

    @Override
    protected boolean isValid() {
        String str = toString();
        return isValid(str);
    }

    @Override
    protected boolean isValid(Extendable extendable) {
        return true;
    }

    @Override
    protected boolean isValid(String str) {
        return true;
    }

    /**
     * Intern state is the int value represented by externstate.
     */
    @Override
    protected void localParse() {
        try {
            String xml = toString();
            SAXBuilder sxb = new SAXBuilder();
            InputStream in = new ByteArrayInputStream(xml.getBytes());
            Document document = sxb.build(in);

            // INFRASTRUCTURES
            Infrastructure infrastructure = null;
            for (Object o : document.getRootElement().getChildren("infrastructure")) {
                Element elem = (Element) o;
                infrastructure = Infrastructure.loadFromXML(elem);
            }

            // LOCATION
            LatLonLocation location = null;
            for (Object o : document.getRootElement().getChildren("location")) {
                location = LatLonLocation.loadFromXML((Element) o);
            }

            // NODES
            for (Object o : document.getRootElement().getChildren("node")) {
                Element elem = (Element) o;
                String type = elem.getAttributeValue("type");
                if ("object".equals(type)) {
                    ObjectEvent objectNode = new ObjectEvent();
                    objectNode.loadFromXML(elem, infrastructure, location);
                    setEvent(objectNode);
                } else if ("aggregation".equals(type)) {
                    AggregationEvent aggregationNode = new AggregationEvent();
                    aggregationNode.loadFromXML(elem, infrastructure, location);
                    setEvent(aggregationNode);
                } else if ("transition".equals(type)) {
                    TransactionEvent transactionNode = new TransactionEvent();
                    transactionNode.loadFromXML(elem, infrastructure, location);
                    setEvent(transactionNode);
                } else if ("quantity".equals(type)) {
                    QuantityEvent quantityNode = new QuantityEvent();
                    quantityNode.loadFromXML(elem, infrastructure, location);
                    setEvent(quantityNode);
                }
            }
        } catch (JDOMException ex) {
            log.fatal("parsing error: JDOMException", ex);
        } catch (IOException ex) {
            log.fatal("parsing error: IOException", ex);
        }
    }

    public void setEvent(BaseEvent event) {
        this.event = event;
    }

    private String readFileAsString(String filePath) {
        BufferedReader reader = null;
        StringBuilder fileData = new StringBuilder(1000);
        try {
            reader = new BufferedReader(new FileReader(filePath));
            char[] buf = new char[1024];
            int numRead;
            while ((numRead = reader.read(buf)) != -1) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }
            reader.close();
        } catch (IOException ex) {
            log.fatal(null, ex);
        } finally {
            try {
                reader.close();
                return fileData.toString();
            } catch (IOException ex) {
                log.fatal(null, ex);
                return null;
            }
        }
    }

    public BaseEvent getEvent() {
        return event;
    }
}
