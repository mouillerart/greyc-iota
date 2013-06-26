/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dseta.service;

import fr.unicaen.iota.ds.commons.OperationsBackendSQL;
import fr.unicaen.iota.ds.commons.OperationsSession;
import fr.unicaen.iota.ds.commons.Publish;
import fr.unicaen.iota.ds.model.CreateResponseType;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.ds.model.EventCreateResp;
import fr.unicaen.iota.ds.model.EventLookupReq;
import fr.unicaen.iota.ds.model.EventLookupResp;
import fr.unicaen.iota.ds.model.InternalException;
import fr.unicaen.iota.ds.model.MultipleEventCreateResp;
import fr.unicaen.iota.ds.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ds.soap.InternalExceptionResponse;
import fr.unicaen.iota.dseta.model.EventCreateReq;
import fr.unicaen.iota.dseta.model.MultipleEventCreateReq;
import fr.unicaen.iota.dseta.soap.DSeTaServicePortType;
import fr.unicaen.iota.dseta.soap.SecurityExceptionResponse;
import fr.unicaen.iota.dseta.utils.Constants;
import fr.unicaen.iota.dseta.utils.Utils;
import fr.unicaen.iota.tau.model.Identity;
import fr.unicaen.iota.xacml.pep.XACMLDSEvent;
import fr.unicaen.iota.xi.client.DSPEP;
import java.security.Principal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.WebServiceContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This <code>DSeTaService</code> implements the DSeTa web service. It handles DS events.
 */
public class DSeTaService implements DSeTaServicePortType {

    @Resource
    protected WebServiceContext wsContext;
    private DSPEP pep;
    private OperationsBackendSQL backend;
    private DataSource dataSource;
    private Publish publish;
    private static final Log LOG = LogFactory.getLog(DSeTaService.class);

    public DSPEP getPep() {
        return pep;
    }

    public void setPep(DSPEP pep) {
        this.pep = pep;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public OperationsBackendSQL getBackend() {
        return backend;
    }

    public void setBackend(OperationsBackendSQL backend) {
        this.backend = backend;
    }

    private void checkAuth(Identity id) throws SecurityExceptionResponse {
        Principal authId = wsContext.getUserPrincipal();
        if (authId == null) { // no TLS
            if (!Constants.XACML_ANONYMOUS_USER.equals(id.getAsString())) {
                throw new SecurityExceptionResponse("Can't allowed to pass as " + id.getAsString() +
                        " without authentication. Only " + Constants.XACML_ANONYMOUS_USER + " is allowed.");
            }
        } else { // TLS
            String user = fr.unicaen.iota.mu.Utils.formatId(authId.getName());
            String canBeUser = fr.unicaen.iota.mu.Utils.formatId(id.getAsString());
            if (!user.equals(canBeUser)) {
                int canBeResult = pep.canBe(user, canBeUser);
                if (!fr.unicaen.iota.xi.utils.Utils.responseIsPermit(canBeResult)) {
                    throw new SecurityExceptionResponse(authId.getName() + " isn't allowed to pass as " + id.getAsString());
                }
            }
        }
    }

    @Override
    public EventCreateResp iDedEventCreate(EventCreateReq eventCreateReq, Identity identity)
            throws ImplementationExceptionResponse, InternalExceptionResponse, SecurityExceptionResponse {
        checkAuth(identity);
        EventCreateResp createResp = new EventCreateResp();
        DSEvent event = eventCreateReq.getDsEvent();
        if (event.getEpc() == null || event.getEpc().isEmpty()
                || event.getEventType() == null || event.getEventType().isEmpty()
                || event.getBizStep() == null || event.getBizStep().isEmpty()
                || event.getEventTime() == null
                || event.getServiceType() == null || event.getServiceAddress().isEmpty()
                || event.getServiceAddress() == null || event.getServiceAddress().isEmpty()) {
            createResp.setValue(CreateResponseType.NOT_ADDED);
            createResp.setMessage("At least one field is missing or empty");
            return createResp;
        }
        String userId = identity.getAsString();
        DSEvent dsEvent = eventCreateReq.getDsEvent();
        String owner = eventCreateReq.getOwner().getAsString();
        if (!canCreateEvent(userId, dsEvent, owner)) {
            createResp.setValue(CreateResponseType.NOT_ADDED);
            createResp.setMessage("The user is not authorized to add the event");
            return createResp;
        }
        try {
            OperationsSession session = backend.openSession(dataSource);
            Map<String, Object> paramObjMap = new HashMap<String, Object>();
            paramObjMap.put("owner", owner);
            boolean result = backend.eventCreate(session, dsEvent, paramObjMap);
            createResp.setValue(CreateResponseType.CREATED_NOT_PUBLISHED);
            String msg = (result)? "Event correctly added" : "Event not added";
            LOG.info(msg);
            if (result && Constants.MULTI_DSETA_ARCHITECTURE) {
                if (publish == null) {
                    publish = new Publish(Constants.SERVICE_ID, Constants.ONS_HOSTS, Constants.JMS_URL,
                            Constants.JMS_LOGIN, Constants.JMS_PASSWORD, Constants.JMS_MESSAGE_TIME_PROPERTY);
                }
                try {
                    publish.createsJMSConnection();
                    Session producerSession = publish.createsJMSSession(false, Session.CLIENT_ACKNOWLEDGE);
                    Queue producerQueue = publish.createsJMSQueue(producerSession, Constants.JMS_QUEUE_NAME);
                    MessageProducer producer = publish.createJMSProducer(producerSession, producerQueue);
                    publish.startsJMSConnection();
                    try {
                        String eventCreateReqString = Utils.convertsEventCreateReqToString(eventCreateReq);
                        publish.sendsEvent(producerSession, producer, eventCreateReqString);
                        createResp.setValue(CreateResponseType.CREATED_AND_PUBLISHED);
                    } finally {
                        producerSession.close();
                        publish.closesJMSConnection();
                    }
                } catch (JAXBException ex) {
                    msg = "Event added but not published";
                    if (LOG.isDebugEnabled()) {
                        LOG.info(msg, ex);
                    }
                    else {
                        LOG.info(msg);
                    }
                } catch (JMSException ex) {
                    msg = "Event added but not published";
                    LOG.error(msg, ex);
                }
            }
            createResp.setMessage(msg);
            return createResp;
        } catch (SQLException ex) {
            InternalException iex = new InternalException();
            String msg = "SQL error during capture execution: " + ex.getMessage();
            LOG.error(msg, ex);
            iex.setReason(msg);
            throw new InternalExceptionResponse(msg, iex, ex);
        }
    }

    @Override
    public MultipleEventCreateResp iDedMultipleEventCreate(MultipleEventCreateReq multipleEventCreateReq, Identity identity) throws ImplementationExceptionResponse, SecurityExceptionResponse, InternalExceptionResponse {
        checkAuth(identity);
        String userId = identity.getAsString();
        MultipleEventCreateResp multipleCreateResp = new MultipleEventCreateResp();
        List<EventCreateResp> responseList = multipleCreateResp.getEventCreateResponses();
        List<EventCreateReq> eventCreateList = multipleEventCreateReq.getEventCreate();
        int nbDSEvents = eventCreateList.size();
        int nbSuccess = 0;
        try {
            OperationsSession session = backend.openSession(dataSource);
            for (int i = 0; i < nbDSEvents; i++) {
                EventCreateReq eventCreate = eventCreateList.get(i);
                EventCreateResp response = new EventCreateResp();
                response.setValue(CreateResponseType.NOT_ADDED);
                DSEvent event = eventCreate.getDsEvent();
                String msg = "";
                String owner = eventCreate.getOwner().getAsString();
                if (event.getEpc() == null || event.getEpc().isEmpty()
                        || event.getEventType() == null || event.getEventType().isEmpty()
                        || event.getBizStep() == null || event.getBizStep().isEmpty()
                        || event.getEventTime() == null
                        || event.getServiceType() == null || event.getServiceAddress().isEmpty()
                        || event.getServiceAddress() == null || event.getServiceAddress().isEmpty()
                        || owner == null || owner.isEmpty()) {
                    msg = "At least one field is missing or empty";
                }
                else {
                    if (canCreateEvent(userId, event, owner)) {
                        try {
                            Map<String, Object> paramObjMap = new HashMap<String, Object>();
                            paramObjMap.put("owner", owner);
                            boolean res = backend.eventCreate(session, event, paramObjMap);
                            if (res == true) {
                                nbSuccess++;
                                msg = "Event correctly added";
                                response.setValue(CreateResponseType.CREATED_NOT_PUBLISHED);
                            }
                            else {
                                msg = "Event not added";
                            }
                        } catch (SQLException ex) {
                            msg = "SQL error during capture execution: " + ex.getMessage();
                            if (LOG.isDebugEnabled()) {
                                LOG.error(msg, ex);
                            }
                            else {
                                LOG.error(msg);
                            }
                        }
                    }
                    else {
                        msg = "The user is not authorized to add the event";
                    }
                }
                LOG.info(msg);
                response.setMessage(msg);
                responseList.add(i, response);
            }
            multipleCreateResp.setResult((nbSuccess == nbDSEvents)?
                    CreateResponseType.CREATED_NOT_PUBLISHED : CreateResponseType.NOT_ADDED);
        } catch (SQLException ex) {
            InternalException iex = new InternalException();
            String msg = "SQL error during capture execution: " + ex.getMessage();
            LOG.error(msg, ex);
            iex.setReason(msg);
            throw new InternalExceptionResponse(msg, iex, ex);
        }

        if (Constants.MULTI_DSETA_ARCHITECTURE) {
            int nbPublishing = 0;
            if (publish == null) {
                publish = new Publish(Constants.SERVICE_ID, Constants.ONS_HOSTS, Constants.JMS_URL,
                        Constants.JMS_LOGIN, Constants.JMS_PASSWORD, Constants.JMS_MESSAGE_TIME_PROPERTY);
            }
            try {
                publish.createsJMSConnection();
                Session producerSession = publish.createsJMSSession(false, Session.CLIENT_ACKNOWLEDGE);
                Queue producerQueue = publish.createsJMSQueue(producerSession, Constants.JMS_QUEUE_NAME);
                MessageProducer producer = publish.createJMSProducer(producerSession, producerQueue);
                publish.startsJMSConnection();
                try {
                    for (int i = 0; i < responseList.size(); i++) {
                        EventCreateResp resp = responseList.get(i);
                        if (resp.getValue().equals(CreateResponseType.NOT_ADDED)) {
                            continue;
                        }
                        try {
                            String eventCreateReqString = Utils.convertsEventCreateReqToString(eventCreateList.get(i));
                            publish.sendsEvent(producerSession, producer, eventCreateReqString);
                            resp.setMessage("Event added and going to be published");
                            resp.setValue(CreateResponseType.CREATED_AND_PUBLISHED);
                            nbPublishing++;
                        } catch (Exception ex) {
                            String msg = "Event added but not published";
                            resp.setMessage(msg);
                            if (LOG.isDebugEnabled()) {
                                LOG.info(msg, ex);
                            }
                            else {
                                LOG.info(msg);
                            }
                        }
                    }
                } finally {
                    producerSession.close();
                    publish.closesJMSConnection();
                }
            } catch (JMSException ex) {
                LOG.error("Error with the JMS provider during the publishing", ex);
            }
            multipleCreateResp.setResult((nbPublishing == nbDSEvents)?
                    CreateResponseType.CREATED_AND_PUBLISHED : CreateResponseType.CREATED_NOT_PUBLISHED);
        }
        return multipleCreateResp;
    }

    @Override
    public EventLookupResp iDedEventLookup(EventLookupReq eventLookupReq, Identity identity) throws ImplementationExceptionResponse, SecurityExceptionResponse, InternalExceptionResponse {
        checkAuth(identity);
        try {
            String userId = identity.getAsString();
            EventLookupResp lookupResp = new EventLookupResp();
            OperationsSession session = backend.openSession(dataSource);
            List<String> columnNames = new ArrayList<String>();
            columnNames.add("epc");
            columnNames.add("eventType");
            columnNames.add("bizStep");
            columnNames.add("eventTime");
            columnNames.add("serviceAddress");
            columnNames.add("serviceType");
            columnNames.add("owner");
            PreparedStatement stmt = backend.getPreparedStatementSelect(session, eventLookupReq.getEpc(),
                    eventLookupReq.getEventType(), eventLookupReq.getBizStep(), eventLookupReq.getStartingAt(),
                    eventLookupReq.getEndingAt(), eventLookupReq.getServiceType(), columnNames);
            ResultSet rs = stmt.executeQuery();
            List<DSEvent> dsEventList = lookupResp.getDsEventList();
            while (rs.next()) {
                DSEvent dSEvent = new DSEvent();
                dSEvent.setEpc(rs.getString("epc"));
                dSEvent.setEventType(rs.getString("eventType"));
                dSEvent.setBizStep(rs.getString("bizStep"));
                dSEvent.setEventTime(backend.timestampToXmlCalendar(rs.getTimestamp("eventTime")));
                dSEvent.setServiceAddress(rs.getString("serviceAddress"));
                dSEvent.setServiceType(rs.getString("serviceType"));
                String owner = rs.getString("owner");
                if (canLookupEvent(userId, dSEvent, owner)) {
                    dsEventList.add(dSEvent);
                }
            }
            return lookupResp;
        } catch (DatatypeConfigurationException ex) {
            InternalException iex = new InternalException();
            String msg = "Error during event time conversion: " + ex.getMessage();
            LOG.error(msg, ex);
            iex.setReason(msg);
            throw new InternalExceptionResponse(msg, iex, ex);
        } catch (SQLException ex) {
            InternalException iex = new InternalException();
            String msg = "SQL error during query execution: " + ex.getMessage();
            LOG.error(msg, ex);
            iex.setReason(msg);
            throw new InternalExceptionResponse(msg, iex, ex);
        }
    }

    /**
     * Checks if the user can create the DSeTa event.
     * @param userId The user who wants to add the event.
     * @param dsEvent The DSEvent the user wants to add.
     * @param owner The owner of the event to add.
     * @return True if the user is not authorized to add the event.
     */
    private boolean canCreateEvent(String userId, DSEvent dsEvent, String owner) {
        String formatedOwner = fr.unicaen.iota.mu.Utils.formatId(owner);
        String formatedUser = fr.unicaen.iota.mu.Utils.formatId(userId);
        XACMLDSEvent xacmlEvent = createsXACMLDSEvent(dsEvent, formatedOwner);
        int canCreate = pep.eventCreate(formatedUser, xacmlEvent);
        return fr.unicaen.iota.xi.utils.Utils.responseIsPermit(canCreate);
    }

    /**
     * Checks if the user can lookup the DS event.
     * @param userId The user who wants to add the event.
     * @param dsEvent The DSEvent the user wants to add.
     * @param owner The owner of the event to add.
     * @return True if the user is authorized to lookup the event.
     */
    private boolean canLookupEvent(String userId, DSEvent dsEvent, String owner) {
        String formatedOwner = fr.unicaen.iota.mu.Utils.formatId(owner);
        String formatedUser = fr.unicaen.iota.mu.Utils.formatId(userId);
        XACMLDSEvent xacmlEvent = createsXACMLDSEvent(dsEvent, formatedOwner);
        int canLookup = pep.eventLookup(formatedUser, xacmlEvent);
        return fr.unicaen.iota.xi.utils.Utils.responseIsPermit(canLookup);
    }

    /**
     * Creates a XACMLDSEvent.
     * @param dsEvent The DSEvent associated to the XACMLDSevent.
     * @param owner The owner of the DSEvent.
     * @return The XACMLDSEvent associated to the given parameters.
     */
    private XACMLDSEvent createsXACMLDSEvent(DSEvent dsEvent, String owner) {
        String epc = dsEvent.getEpc();
        String eventType = dsEvent.getEventType();
        String bizStep = dsEvent.getBizStep();
        XMLGregorianCalendar eventTime = dsEvent.getEventTime();
        Date d = eventTime.toGregorianCalendar().getTime();
        XACMLDSEvent xacmlEvent = new XACMLDSEvent(owner, bizStep, epc, eventType, d);
        return xacmlEvent;
    }

}
