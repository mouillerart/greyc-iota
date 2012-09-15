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
package fr.unicaen.iota.xacml.request;

import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;
import fr.unicaen.iota.xacml.pep.XACMLDSEvent;
import fr.unicaen.iota.xacml.policy.Module;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class EventRequest {

    private static final Log log = LogFactory.getLog(EventRequest.class);
    private String user;
    private String action;
    private String owner;
    private String module;
    private XACMLDSEvent dsEvent;

    public String getOwner() {
        return owner;
    }

    public String getUser() {
        return user;
    }

    public String getAction() {
        return action;
    }

    public String getBizStep() {
        if (dsEvent == null) {
            return null;
        }
        return dsEvent.getBizStep();
    }

    public String getEpc() {
        if (dsEvent == null) {
            return null;
        }
        return dsEvent.getEpc();
    }

    public String getEpcClass() {
        if (dsEvent == null) {
            return null;
        }
        return dsEvent.getEpcClass();
    }

    public Date getEventTime() {
        if (dsEvent == null) {
            return null;
        }
        return dsEvent.getEventTime();
    }

    public EventRequest(String user, String action, String owner, String module) {
        this.user = user;
        this.action = action;
        this.owner = owner;
        this.dsEvent = null;
        this.module = module;
    }

    public EventRequest(String user, String action, XACMLDSEvent dsEvent, String module) {
        this.user = user;
        this.action = action;
        this.dsEvent = dsEvent;
        this.owner = dsEvent.getOwner();
        this.module = module;
    }

    public RequestCtx createRequest() {

        Set subjects = new HashSet();
        Set resources = new HashSet();
        Set actions = new HashSet();
        Set environment = new HashSet();

        //////////////////////// User ID //////////////////////

        URI subjectAttributeId = null;
        try {
            subjectAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:subject:user-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        StringAttribute subjectValue = new StringAttribute(this.user);
        Attribute subjectAttribute = new Attribute(subjectAttributeId, null, null, subjectValue);
        Set subjectSet = new HashSet();
        subjectSet.add(subjectAttribute);
        Subject subject = new Subject(subjectSet);
        subjects.add(subject);

        //////////////////////// Module ID //////////////////////

        URI moduleAttributeId = null;
        try {
            moduleAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:subject:module-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        StringAttribute moduleValue = new StringAttribute(getModule());
        Attribute moduleAttribute = new Attribute(moduleAttributeId, null, null, moduleValue);
        Set moduleSet = new HashSet();
        moduleSet.add(moduleAttribute);
        Subject subjectModule = new Subject(moduleSet);
        subjects.add(subjectModule);

        //////////////////////// Resource ID //////////////////////

        URI resourceAttributeId = null;
        try {
            resourceAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:resource-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        StringAttribute resource = new StringAttribute("DSevent");
        Attribute resourceAttribute = new Attribute(resourceAttributeId, null, null, resource);
        resources.add(resourceAttribute);

        //////////////////////// Owner ID //////////////////////

        URI ownerAttributeId = null;
        try {
            ownerAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:owner-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        StringAttribute ownerValue = new StringAttribute(getOwner());
        Attribute ownerAttribute = new Attribute(ownerAttributeId, null, null, ownerValue);
        resources.add(ownerAttribute);



        //////////////////////// Action ID //////////////////////

        URI actionAttributeId = null;
        try {
            actionAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:action:action-id");
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        StringAttribute actionValue = new StringAttribute(this.action);
        Attribute actionAttribute = new Attribute(actionAttributeId, null, null, actionValue);
        actions.add(actionAttribute);

        //////////////////////// BizStep ID //////////////////////

        if (getBizStep() != null) {
            URI bizStepAttributeId = null;
            try {
                bizStepAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:bizStep-id");
            } catch (URISyntaxException ex) {
                log.fatal(null, ex);
            }
            StringAttribute bizStepValue = new StringAttribute(getBizStep());
            Attribute bizStepAttribute = new Attribute(bizStepAttributeId, null, null, bizStepValue);
            resources.add(bizStepAttribute);
        }

        //////////////////////// EPC ID //////////////////////

        if (getEpc() != null) {
            URI epcAttributeId = null;
            try {
                epcAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:epc-id");
            } catch (URISyntaxException ex) {
                log.fatal(null, ex);
            }
            StringAttribute epcValue = new StringAttribute(getEpc());
            Attribute epcAttribute = new Attribute(epcAttributeId, null, null, epcValue);
            resources.add(epcAttribute);
        }

        //////////////////////// EPCClass ID //////////////////////

        if (getEpcClass() != null) {
            URI epcClassAttributeId = null;
            try {
                epcClassAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:epcClass-id");
            } catch (URISyntaxException ex) {
                log.fatal(null, ex);
            }
            StringAttribute epcClassValue = new StringAttribute(getEpcClass());
            Attribute epcClassAttribute = new Attribute(epcClassAttributeId, null, null, epcClassValue);
            resources.add(epcClassAttribute);
        }

        //////////////////////// EventTime ID //////////////////////

        if (getEventTime() != null) {
            URI eventTimeAttributeId = null;
            try {
                eventTimeAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:eventTime-id");
            } catch (URISyntaxException ex) {
                log.fatal(null, ex);
            }
            DateTimeAttribute eventTimeValue = new DateTimeAttribute(getEventTime());
            Attribute eventTimeAttribute = new Attribute(eventTimeAttributeId, null, null, eventTimeValue);
            resources.add(eventTimeAttribute);
        }
        RequestCtx request = new RequestCtx(subjects, resources, actions, environment); //new RequestCtx(subjects, resourceAttrs,actionAttrs, environmentAttrs);
        return request;
    }

    public void save() {
        RequestCtx rc = this.createRequest();
        String fileName = "resources/policies/request_" + this.user + ".xml";
        try {
            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException ex) {
                    log.fatal(null, ex);
                }
            }
            rc.encode(new FileOutputStream(new File(fileName)));
        } catch (FileNotFoundException ex) {
            log.fatal(null, ex);
        }
    }

    public static void main(String[] args) {
        String bizStep = "bizStep1";
        String epcClass = "epcClass:1";
        String epc = "epc:1";
        Date date = new Date(8000);
        XACMLDSEvent event = new XACMLDSEvent("toto", bizStep, epc, epcClass, date);
        EventRequest er = new EventRequest("user8", "read", event, Module.queryModule.getValue());
        er.save();
    }

    private String getModule() {
        return module;
    }
}
