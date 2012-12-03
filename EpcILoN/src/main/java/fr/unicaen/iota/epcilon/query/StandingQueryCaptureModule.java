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
package fr.unicaen.iota.epcilon.query;

import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.axis2.databinding.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.QueryParams;
import org.fosstrak.epcis.model.QuerySchedule;
import org.fosstrak.epcis.model.Subscribe;
import org.fosstrak.epcis.model.SubscriptionControls;

public class StandingQueryCaptureModule {

    private static final Log LOG = LogFactory.getLog(StandingQueryCaptureModule.class);

    public static Subscribe createScheduleSubscribe(String name, String subID, String dest, int unitTime, String timeValue) throws DatatypeConfigurationException, MalformedURIException {
        Subscribe subscribe = new Subscribe();
        SubscriptionControls controls = new SubscriptionControls();
        controls.setReportIfEmpty(false);
        QuerySchedule schedule = new QuerySchedule();
        switch (unitTime) {
            case 1:
                schedule.setSecond(timeValue);
                break;
            case 2:
                schedule.setMinute(timeValue);
                break;
            case 3:
                schedule.setHour(timeValue);
                break;
            case 4:
                schedule.setDayOfMonth(timeValue);
                break;
            case 5:
                schedule.setMonth(timeValue);
                break;
            case 6:
                schedule.setDayOfWeek(timeValue);
                break;
            default:
                LOG.error("The subscription type is not recognized, verify your epcilon.properties file!");
                System.exit(-1);
        }
        controls.setSchedule(schedule);
        GregorianCalendar gregCal = new GregorianCalendar();
        gregCal.setTime(new Date()); // Won't work without
        XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        controls.setInitialRecordTime(xmlCal);
        subscribe.setControls(controls);
        subscribe.setDest(dest);
        QueryParams params = new QueryParams();
        subscribe.setParams(params);
        subscribe.setQueryName(name);
        subscribe.setSubscriptionID(subID);
        return subscribe;
    }
}
