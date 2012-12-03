/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.discovery.client.gui;

import fr.unicaen.iota.discovery.client.DsClient;
import fr.unicaen.iota.discovery.client.model.Session;
import fr.unicaen.iota.discovery.client.model.UserInfo;
import fr.unicaen.iota.discovery.client.util.Configuration;
import fr.unicaen.iota.discovery.client.util.EnhancedProtocolException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class CaptureOperation {

    private String LOGIN;
    private String PASS;
    private String SERVICE_ADDRESS;
    private String BUSINESS_STEP;
    private String EPC;
    private String CLASS;
    private String EVENT_ID = "1";
    private int PRIORITY;
    private Calendar SOURCE_TS;
    private String SUPPLYCHAIN_ID = "default";
    private int TTL;
    private static final Log log = LogFactory.getLog(CaptureOperation.class);

    public void publishEvent(String EndPointEPR) {
        try {
            log.trace("CREATE ENTRY");
            DsClient dsClient = new DsClient(EndPointEPR);
            System.out.println("LOGIN");
            String SESSION_ID;
            Session session = dsClient.userLogin(Configuration.DEFAULT_SESSION, getLOGIN(), getPASS());
            SESSION_ID = session.getSessionId();
            log.trace("USER INFO");
            UserInfo userInfo = dsClient.userInfo(SESSION_ID, getLOGIN());
            String PARTNER_ID = userInfo.getPartnerId();
            log.trace("EVENT CREATE");
            List<String> serviceIds = new ArrayList<String>();
            serviceIds.add("epcis");
            dsClient.eventCreate(SESSION_ID, PARTNER_ID, EPC, BUSINESS_STEP, CLASS, Calendar.getInstance(), TTL, serviceIds, PRIORITY, null);
            log.trace("LOGOUT");
            dsClient.userLogout(SESSION_ID);
            log.trace("SUCCESSFULL TRANSACTION => END");
        } catch (MalformedURIException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        } catch (EnhancedProtocolException ex) {
            log.fatal(null, ex);
        }
    }

    public String getLOGIN() {
        return LOGIN;
    }

    public void setLOGIN(String LOGIN) {
        this.LOGIN = LOGIN;
    }

    public String getPASS() {
        return PASS;
    }

    public void setPASS(String PASS) {
        this.PASS = PASS;
    }

    public String getSERVICE_ADDRESS() {
        return SERVICE_ADDRESS;
    }

    public void setSERVICE_ADDRESS(String SERVICE_ADDRESS) {
        this.SERVICE_ADDRESS = SERVICE_ADDRESS;
    }

    public String getBUSINESS_STEP() {
        return BUSINESS_STEP;
    }

    public void setBUSINESS_STEP(String BUSINESS_STEP) {
        this.BUSINESS_STEP = BUSINESS_STEP;
    }

    public String getEPC() {
        return EPC;
    }

    public void setEPC(String EPC) {
        this.EPC = EPC;
    }

    public String getCLASS() {
        return CLASS;
    }

    public void setCLASS(String CLASS) {
        this.CLASS = CLASS;
    }

    public String getEVENT_ID() {
        return EVENT_ID;
    }

    public void setEVENT_ID(String EVENT_ID) {
        this.EVENT_ID = EVENT_ID;
    }

    public int getPRIORITY() {
        return PRIORITY;
    }

    public void setPRIORITY(int PRIORITY) {
        this.PRIORITY = PRIORITY;
    }

    public Calendar getSOURCE_TS() {
        return SOURCE_TS;
    }

    public void setSOURCE_TS(Calendar SOURCE_TS) {
        this.SOURCE_TS = SOURCE_TS;
    }

    public String getSUPPLYCHAIN_ID() {
        return SUPPLYCHAIN_ID;
    }

    public void setSUPPLYCHAIN_ID(String SUPPLYCHAIN_ID) {
        this.SUPPLYCHAIN_ID = SUPPLYCHAIN_ID;
    }

    public int getTTL() {
        return TTL;
    }

    public void setTTL(int TTL) {
        this.TTL = TTL;
    }
}
