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
package fr.unicaen.iota.epcilon.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class Utils {

    /**
     * Gets {@link XMLGregorianCalendar} object corresponding to the {@link Date}
     * @param time The {@link Date} to convert.
     * @return The {@link XMLGregorianCalendar} associated to the date.
     * @throws DatatypeConfigurationException If an error occurred during the conversion.
     */
    public static XMLGregorianCalendar dateToXmlCalendar(Date time) throws DatatypeConfigurationException {
            DatatypeFactory factory = DatatypeFactory.newInstance();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(time.getTime());
            return factory.newXMLGregorianCalendar((GregorianCalendar) cal);
    }

}
