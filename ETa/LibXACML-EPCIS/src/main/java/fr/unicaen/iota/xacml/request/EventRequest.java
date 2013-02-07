/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.xacml.request;

import com.sun.xacml.EvaluationCtx;
import com.sun.xacml.attr.DateTimeAttribute;
import com.sun.xacml.attr.DoubleAttribute;
import com.sun.xacml.attr.IntegerAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;
import fr.unicaen.iota.xacml.pep.ExtensionEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISMasterData;
import fr.unicaen.iota.xacml.policy.*;
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

public class EventRequest {

    private static final Log log = LogFactory.getLog(EventRequest.class);
    private String user;
    private String action;
    private String owner;
    private String module;
    private XACMLEPCISEvent epcisEvent;
    private XACMLEPCISMasterData epcisMasterData;

    public EventRequest(String user, String action, String owner, String module) {
        this.user = user;
        this.action = action;
        this.owner = owner;
        this.epcisEvent = null;
        this.module = module;
    }

    public EventRequest(String user, String action, XACMLEPCISEvent epcisEvent, String module) {
        this.user = user;
        this.action = action;
        this.epcisEvent = epcisEvent;
        this.owner = epcisEvent.getOwner();
        this.module = module;
    }

    public EventRequest(String user, String action, XACMLEPCISMasterData epcisMasterData, String module) {
        this.user = user;
        this.action = action;
        this.epcisMasterData = epcisMasterData;
        this.owner = epcisMasterData.getOwner();
        this.module = module;
    }

    public String getOwner() {
        return owner;
    }

    public String getUser() {
        return user;
    }

    public String getAction() {
        return action;
    }

    private String getModule() {
        return module;
    }

    public String getBizStep() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getBizStep();
    }

    public String getEpc() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getEpc();
    }

    public Date getEventTime() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getEventTime();
    }

    public Date getRecordTime() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getRecordTime();
    }

    public String getOperation() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getOperation();
    }

    public String getEventType() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getEventType();
    }

    public String getParentId() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getParentId();
    }

    public String getChildEpc() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getChildEpc();
    }

    public Long getQuantity() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getQuantity();
    }

    public String getReadPoint() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getReadPoint();
    }

    public String getBizLoc() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getBizLoc();
    }

    public String getBizTrans() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getBizTrans();
    }

    public String getDisposition() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getDisposition();
    }

    public ExtensionEvent getExtensions() {
        if (epcisEvent == null) {
            return null;
        }
        return epcisEvent.getExtension();
    }

    public String getMasterDataId() {
        if (epcisMasterData == null) {
            return null;
        }
        return epcisMasterData.getId();
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
        Subject localModule = new Subject(moduleSet);
        subjects.add(localModule);

        //////////////////////// Resource ID //////////////////////

        URI resourceAttributeId = null;
        try {
            resourceAttributeId = new URI(EvaluationCtx.RESOURCE_ID);
        } catch (URISyntaxException ex) {
            log.fatal(null, ex);
        }
        StringAttribute resource = new StringAttribute("EPCISevent");
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

        // EPCIS event Query
        if (epcisEvent != null) {

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

            //////////////////////// RecordTime ID //////////////////////

            if (getRecordTime() != null) {
                URI recordTimeAttributeId = null;
                try {
                    recordTimeAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:recordTime-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                DateTimeAttribute recordTimeValue = new DateTimeAttribute(getRecordTime());
                Attribute recordTimeAttribute = new Attribute(recordTimeAttributeId, null, null, recordTimeValue);
                resources.add(recordTimeAttribute);
            }

            //////////////////////// Operation ID //////////////////////

            if (getOperation() != null) {
                URI operationAttributeId = null;
                try {
                    operationAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:operation-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute operationValue = new StringAttribute(getOperation());
                Attribute operationAttribute = new Attribute(operationAttributeId, null, null, operationValue);
                resources.add(operationAttribute);
            }

            //////////////////////// EventType ID //////////////////////

            if (getEventType() != null) {
                URI eventTypeAttributeId = null;
                try {
                    eventTypeAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:eventType-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute eventTypeValue = new StringAttribute(getEventType());
                Attribute eventTypeAttribute = new Attribute(eventTypeAttributeId, null, null, eventTypeValue);
                resources.add(eventTypeAttribute);
            }

            //////////////////////// Parent ID //////////////////////

            if (getParentId() != null) {
                URI parentIdAttributeId = null;
                try {
                    parentIdAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:parentId-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute parentIdValue = new StringAttribute(getParentId());
                Attribute parentIdAttribute = new Attribute(parentIdAttributeId, null, null, parentIdValue);
                resources.add(parentIdAttribute);
            }

            //////////////////////// ChildEpc ID //////////////////////

            if (getChildEpc() != null) {
                URI childEpcAttributeId = null;
                try {
                    childEpcAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:childEpc-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute childEpcValue = new StringAttribute(getChildEpc());
                Attribute childEpcAttribute = new Attribute(childEpcAttributeId, null, null, childEpcValue);
                resources.add(childEpcAttribute);
            }

            //////////////////////// Quantity ID //////////////////////

            if (getQuantity() != null) {
                URI quantityAttributeId = null;
                try {
                    quantityAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:quantity-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                IntegerAttribute quantityValue = new IntegerAttribute(getQuantity());
                Attribute quantityAttribute = new Attribute(quantityAttributeId, null, null, quantityValue);
                resources.add(quantityAttribute);
            }

            //////////////////////// ReadPoint ID //////////////////////

            if (getReadPoint() != null) {
                URI readPointAttributeId = null;
                try {
                    readPointAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:readPoint-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute readPointValue = new StringAttribute(getReadPoint());
                Attribute readPointAttribute = new Attribute(readPointAttributeId, null, null, readPointValue);
                resources.add(readPointAttribute);
            }

            //////////////////////// BizLoc ID //////////////////////

            if (getBizLoc() != null) {
                URI bizLocAttributeId = null;
                try {
                    bizLocAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:bizLoc-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute bizLocValue = new StringAttribute(getBizLoc());
                Attribute bizLocAttribute = new Attribute(bizLocAttributeId, null, null, bizLocValue);
                resources.add(bizLocAttribute);
            }

            //////////////////////// BizTrans ID //////////////////////

            if (getBizTrans() != null) {
                URI bizTransAttributeId = null;
                try {
                    bizTransAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:bizTrans");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute bizTransValue = new StringAttribute(getBizTrans());
                Attribute bizTransAttribute = new Attribute(bizTransAttributeId, null, null, bizTransValue);
                resources.add(bizTransAttribute);
            }

            //////////////////////// Disposition ID //////////////////////

            if (getDisposition() != null) {
                URI dispositionAttributeId = null;
                try {
                    dispositionAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:disposition-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute dispositionValue = new StringAttribute(getDisposition());
                Attribute dispositionAttribute = new Attribute(dispositionAttributeId, null, null, dispositionValue);
                resources.add(dispositionAttribute);
            }

            //////////////////////// Extensions ////////////////////////////

            if (getExtensions() != null) {
                ExtensionEvent extension = getExtensions();
                URI extensionAttributeId = null;
                try {
                    extensionAttributeId = new URI(extension.getExtensionId());
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                Object extensionValue = extension.getExtensionValue();
                Attribute extensionAttribute = null;
                if (extensionValue instanceof Float) {
                    Double d = new Double(((Float) extensionValue).toString());
                    DoubleAttribute doubleValue = new DoubleAttribute(d);
                    extensionAttribute = new Attribute(extensionAttributeId, null, null, doubleValue);
                } else if (extensionValue instanceof Integer) {
                    IntegerAttribute intValue = new IntegerAttribute((long) ((Integer) extensionValue));
                    extensionAttribute = new Attribute(extensionAttributeId, null, null, intValue);
                } else if (extensionValue instanceof Date) {
                    DateTimeAttribute dateTimeValue = new DateTimeAttribute((Date) extensionValue);
                    extensionAttribute = new Attribute(extensionAttributeId, null, null, dateTimeValue);
                } else if (extensionValue instanceof String) {
                    StringAttribute stringValue = new StringAttribute((String) extensionValue);
                    extensionAttribute = new Attribute(extensionAttributeId, null, null, stringValue);
                }
                if (extensionAttribute != null) {
                    resources.add(extensionAttribute);
                }
            }
        } // EPCIS Master Data Query
        else if (epcisMasterData != null) {

            //////////////////////// Master Data Id /////////////////:

            if (getMasterDataId() != null) {
                URI masterDataIdAttributeId = null;
                try {
                    masterDataIdAttributeId = new URI("urn:oasis:names:tc:xacml:1.0:resource:masterdata-id");
                } catch (URISyntaxException ex) {
                    log.fatal(null, ex);
                }
                StringAttribute masterDataIdValue = new StringAttribute(getMasterDataId());
                Attribute masterDataIdAttribute = new Attribute(masterDataIdAttributeId, null, null, masterDataIdValue);
                resources.add(masterDataIdAttribute);
            }
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
        String epc = "epc:1";
        Date eventtime = new Date(8000);
        Date recordtime = new Date(7999);
        String operation = "operation:ADD";
        String eventType = "eventType:1";
        String parentId = "parentId:1";
        String childEpc = "childEpc:1";
        Long quantity = new Long(1);
        String readPoint = "readPoint:1";
        String bizLoc = "bizLoc:1";
        String bizTrans = "bizTrans:1";
        String disposition = "disposition:1";
        ExtensionEvent extension = null;
        XACMLEPCISEvent event = new XACMLEPCISEvent("toto", bizStep, epc, eventtime, recordtime, operation, eventType, parentId,
                childEpc, quantity, readPoint, bizLoc, bizTrans, disposition, extension);
        EventRequest er = new EventRequest("user8", "read", event, Module.queryModule.getValue());
        er.save();
    }
}
