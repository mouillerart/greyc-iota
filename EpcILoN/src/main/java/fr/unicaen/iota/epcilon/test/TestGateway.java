/*
 *  This program is a part of the IoTa project.
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
package fr.unicaen.iota.epcilon.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TestGateway {

    /**
     * @param args
     */
    public static void main(String[] args) {
        List<String> eventsInEPCIS = new ArrayList<String>();
        List<String> eventsInDS = new ArrayList<String>();

        /*
         * long time = 1056875425; String timeStr =
         * java.lang.String.valueOf(time); timeStr =
         * timeStr.substring(timeStr.length()-3, timeStr.length());
         * System.out.println(timeStr);
         */
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost/new_epcis?user=new_epcis&password=new_epcis");
            Statement stmt = conn.createStatement();
            /*
             * String query = "select * from event_ObjectEvent"; ResultSet
             * results = stmt.executeQuery(query); while (results.next()){
             * String epc = results.getString("recordTime");
             * System.out.println(epc); }
             */

            String queryEPCIS = "select * from event_ObjectEvent_EPCs";
            ResultSet results = stmt.executeQuery(queryEPCIS);
            while (results.next()) {
                String epc = results.getString("epc");
                eventsInEPCIS.add(epc);
            }
            Connection conn2 = DriverManager.getConnection("jdbc:mysql://localhost/ds_repository?user=ds&password=ds");
            Statement stmt2 = conn2.createStatement();
            String queryEPCIS2 = "select * from event";
            ResultSet results2 = stmt2.executeQuery(queryEPCIS2);
            while (results2.next()) {
                String epc = results2.getString("epc");
                eventsInDS.add(epc);
            }
            int notPublishCount = 0;
            for (String eventInEPCIS : eventsInEPCIS) {
                String eventToRemove = null;
                for (String eventInDS : eventsInDS) {
                    if (eventInEPCIS.equals(eventInDS)) {
                        eventToRemove = eventInDS;
                        break;
                    }
                }
                if (eventToRemove != null) {
                    eventsInDS.remove(eventToRemove);
                } else {
                    notPublishCount++;
                    System.out.println("the event with epc: " + eventInEPCIS + " wasn't publish to the DS");
                }
            }
            for (String eventNotPublish : eventsInDS) {
                System.out.println("the event with epc: " + eventNotPublish + " wasn't present in the EPCIS");
            }
            System.out.println("there is " + notPublishCount + " events not publish to the DS");
            System.out.println("there is " + eventsInDS.size() + " events not present in the EPCIS");
        } catch (Exception e) {
            System.out.println("Connection failed: " + e);
            System.exit(-1);
        }
    }
}
