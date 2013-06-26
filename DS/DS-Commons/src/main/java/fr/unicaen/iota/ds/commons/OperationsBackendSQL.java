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
package fr.unicaen.iota.ds.commons;

import fr.unicaen.iota.ds.model.DSEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class OperationsBackendSQL {

    /**
     * Inserts an event in the database.
     * @param session The session wrapping a database connection.
     * @param dsEvent The DS event to insert.
     * @param objParamMap The other parameters (in SQL types!) to add.
     * @return True if the event is correctly added.
     * @throws SQLException An error involving the database occurred.
     */
    public boolean eventCreate(final OperationsSession session,
            DSEvent dsEvent, Map<String, Object> objParamMap) throws SQLException {
        StringBuilder insert = new StringBuilder("INSERT INTO events (epc, eventType, bizStep, eventTime, serviceAddress, serviceType");
        Map<String, Object> objParamLinkedMap = null;
        if (objParamMap != null) {
            objParamLinkedMap = new LinkedHashMap(objParamMap);
            for (String columnName : objParamMap.keySet()) {
                insert.append(",");
                insert.append(columnName);
            }
        }
        insert.append(") VALUES (?, ?, ?, ?, ?, ?");
        if (objParamLinkedMap != null) {
            for (int i = 0; i < objParamLinkedMap.size(); i++) {
                insert.append(",?");
            }
        }
        insert.append(")");
        PreparedStatement stmt = session.getPreparedStatement(insert.toString());
        stmt.setString(1, dsEvent.getEpc());
        stmt.setString(2, dsEvent.getEventType());
        stmt.setString(3, dsEvent.getBizStep());
        stmt.setTimestamp(4, XMLGregorianToTimestamp(dsEvent.getEventTime()));
        stmt.setString(5, dsEvent.getServiceAddress());
        stmt.setString(6, dsEvent.getServiceType());
        if (objParamLinkedMap != null) {
            int i = 7;
            for (Object param : objParamLinkedMap.values()) {
                stmt.setObject(i, param);
                i++;
            }
        }
        stmt.executeUpdate();
        session.commit();
        return true;
    }

    /**
     * Retrieves a <code>PreParedStatement<code> corresponding to the parameters.
     * @param session The session wrapping a database connection.
     * @param epc The EPC to get.
     * @param eventType The event type to get.
     * @param bizStep The business step to get.
     * @param startingAt The lower limit of the event time.
     * @param endingAt The upper limit of the event time.
     * @param serviceType The type of the service.
     * @param columns The other column names to select.
     * @return The PreparedStatement corresponding to the parameters.
     * @throws SQLException An error involving the database occurred.
     */
    public PreparedStatement getPreparedStatementSelect(final OperationsSession session, String epc,
            String eventType, String bizStep, XMLGregorianCalendar startingAt,
            XMLGregorianCalendar endingAt, String serviceType, List<String> columns) throws SQLException {
        StringBuilder select = new StringBuilder("SELECT ");
        for (String columnName : columns) {
            select.append(columnName);
            select.append(",");
        }
        if (',' == select.charAt(select.length()-1)) {
            select.deleteCharAt(select.length()-1);
        }
        select.append(" FROM events WHERE");
        boolean parameterBefore = false;
        boolean epcIsPresent = false;
        boolean eventTypeIsPresent = false;
        boolean bizStepIsPresent = false;
        boolean startingAtIsPresent = false;
        boolean endingAtIsPresent = false;
        boolean serviceTypeIsPresent = false;
        /*
         * Beware of the parameter order
         *
         */
        if (epc != null && !epc.isEmpty()) {
            select.append(" epc=?");
            epcIsPresent = true;
            parameterBefore = true;
        }
        if (eventType != null && !eventType.isEmpty()) {
            if (parameterBefore) {
                select.append(" AND");
            }
            select.append(" eventType=?");
            eventTypeIsPresent = true;
            parameterBefore = true;
        }
        if (bizStep != null && !bizStep.isEmpty()) {
            if (parameterBefore) {
                select.append(" AND");
            }
            select.append(" bizStep=?");
            bizStepIsPresent = true;
            parameterBefore = true;
        }
        if (startingAt != null) {
            if (parameterBefore) {
                select.append(" AND");
            }
            select.append(" eventTime>=?");
            startingAtIsPresent = true;
            parameterBefore = true;
        }
        if (endingAt != null) {
            if (parameterBefore) {
                select.append(" AND");
            }
            select.append(" eventTime<=?");
            endingAtIsPresent = true;
            parameterBefore = true;
        }
        if (serviceType != null && !serviceType.isEmpty()) {
            if (parameterBefore) {
                select.append(" AND");
            }
            select.append(" serviceType=?");
            serviceTypeIsPresent = true;
            parameterBefore = true;
        }
        PreparedStatement stmt = session.getPreparedStatement(select.toString());
        int i = 1;
        if (epcIsPresent) {
            stmt.setString(i, epc);
            i++;
        }
        if (eventTypeIsPresent) {
            stmt.setString(i, eventType);
            i++;
        }
        if (bizStepIsPresent) {
            stmt.setString(i, bizStep);
            i++;
        }
        if (startingAtIsPresent) {
            stmt.setTimestamp(i, XMLGregorianToTimestamp(startingAt));
            i++;
        }
        if (endingAtIsPresent) {
            stmt.setTimestamp(i, XMLGregorianToTimestamp(endingAt));
            i++;
        }
        if (serviceTypeIsPresent) {
            stmt.setString(i, serviceType);
            i++;
        }
        return stmt;
    }

    /**
     * Opens a new session for the database transaction.
     *
     * @param dataSource The DataSource object to retrieve the database connection from.
     * @return A OperationsSession instantiated with the database connection retrieved from the given DataSource.
     * @throws SQLException If an error with the database occurred.
     */
    public OperationsSession openSession(final DataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        return new OperationsSession(connection);
    }

    /**
     * Gets {@link XMLGregorianCalendar} object corresponding to the {@link Timestamp}
     * @param time The {@link Timestamp} to convert.
     * @return The {@link XMLGregorianCalendar} associated to the timestamp.
     * @throws DatatypeConfigurationException If an error occurred during the conversion.
     */
    public XMLGregorianCalendar timestampToXmlCalendar(Timestamp time) throws DatatypeConfigurationException {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time.getTime());
            return factory.newXMLGregorianCalendar((GregorianCalendar) cal);
    }

    /**
     * Gets {@link XMLGregorianCalendar} object corresponding to the {@link Timestamp}
     * @param time The {@link XMLGregorianCalendar} to convert.
     * @return The {@link Timestamp} associated to the XMLGregorianCalandar.
     */
    public Timestamp XMLGregorianToTimestamp(XMLGregorianCalendar time){
        return (time != null)? new Timestamp(time.toGregorianCalendar().getTimeInMillis()) : null;
    }
}
