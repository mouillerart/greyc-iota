/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.ds.service;

import fr.unicaen.iota.ds.commons.OperationsBackendSQL;
import fr.unicaen.iota.ds.commons.OperationsSession;
import fr.unicaen.iota.ds.commons.Publish;
import fr.unicaen.iota.ds.model.CreateResponseType;
import fr.unicaen.iota.ds.model.DSEvent;
import fr.unicaen.iota.ds.model.EventCreateReq;
import fr.unicaen.iota.ds.model.EventCreateResp;
import fr.unicaen.iota.ds.model.EventLookupReq;
import fr.unicaen.iota.ds.model.EventLookupResp;
import fr.unicaen.iota.ds.model.InternalException;
import fr.unicaen.iota.ds.model.MultipleEventCreateReq;
import fr.unicaen.iota.ds.model.MultipleEventCreateResp;
import fr.unicaen.iota.ds.soap.DSServicePortType;
import fr.unicaen.iota.ds.soap.ImplementationExceptionResponse;
import fr.unicaen.iota.ds.soap.InternalExceptionResponse;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.sql.DataSource;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This <code>DSService</code> implements the DS web service. It handles DS events.
 */
public class DSService implements DSServicePortType {

    private OperationsBackendSQL backend;
    private DataSource dataSource;
    private Publish publish;
    private static final Log LOG = LogFactory.getLog(DSService.class);

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

    @Override
    public EventCreateResp eventCreate(EventCreateReq eventCreateReq) throws ImplementationExceptionResponse, InternalExceptionResponse {
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
        try {
            OperationsSession session = backend.openSession(dataSource);
            boolean result = backend.eventCreate(session, event, null);
            createResp.setValue(CreateResponseType.CREATED_NOT_PUBLISHED);
            String msg = (result)? "Event correctly added" : "Event not added";
            LOG.info(msg);
            if (result && Constants.MULTI_DS_ARCHITECTURE) {
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
                        publish.sendsEvent(producerSession, producer, event);
                        createResp.setValue(CreateResponseType.CREATED_AND_PUBLISHED);
                    } finally {
                        producerSession.close();
                        publish.closesJMSConnection();
                    }
                    msg = "Event added and going to be published";
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
    public MultipleEventCreateResp multipleEventCreate(MultipleEventCreateReq multipleEventCreateReq) throws ImplementationExceptionResponse, InternalExceptionResponse {
        MultipleEventCreateResp multipleCreateResp = new MultipleEventCreateResp();
        List<EventCreateResp> responseList = multipleCreateResp.getEventCreateResponses();
        List<DSEvent> dsEvents = multipleEventCreateReq.getDsEvent();
        int nbDSEvents = dsEvents.size();
        int nbSuccess = 0;
        try {
            OperationsSession session = backend.openSession(dataSource);
            for (int i = 0; i < nbDSEvents; i ++) {
                EventCreateResp response = new EventCreateResp();
                response.setValue(CreateResponseType.NOT_ADDED);
                DSEvent event = dsEvents.get(i);
                String msg = "";
                if (event.getEpc() == null || event.getEpc().isEmpty()
                        || event.getEventType() == null || event.getEventType().isEmpty()
                        || event.getBizStep() == null || event.getBizStep().isEmpty()
                        || event.getEventTime() == null
                        || event.getServiceType() == null || event.getServiceAddress().isEmpty()
                        || event.getServiceAddress() == null || event.getServiceAddress().isEmpty()) {
                    msg = "At least one field is missing or empty";
                }
                else {
                    try {
                        boolean res = backend.eventCreate(session, event, null);
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

        if (Constants.MULTI_DS_ARCHITECTURE) {
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
                            DSEvent eventToPublish = dsEvents.get(i);
                            publish.sendsEvent(producerSession, producer, eventToPublish);
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
    public EventLookupResp eventLookup(EventLookupReq eventLookupReq) throws ImplementationExceptionResponse, InternalExceptionResponse {
        try {
            EventLookupResp lookupResp = new EventLookupResp();
            OperationsSession session = backend.openSession(dataSource);
            List<String> columnNames = new ArrayList<String>();
            columnNames.add("epc");
            columnNames.add("eventType");
            columnNames.add("bizStep");
            columnNames.add("eventTime");
            columnNames.add("serviceAddress");
            columnNames.add("serviceType");
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
                dsEventList.add(dSEvent);
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

}
