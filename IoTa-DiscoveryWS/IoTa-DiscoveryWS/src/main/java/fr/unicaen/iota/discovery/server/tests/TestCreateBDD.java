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
package fr.unicaen.iota.discovery.server.tests;

import fr.unicaen.iota.discovery.server.hibernate.Partner;
import fr.unicaen.iota.discovery.server.hibernate.Sc;
import fr.unicaen.iota.discovery.server.hibernate.Scassociation;
import fr.unicaen.iota.discovery.server.hibernate.User;
import fr.unicaen.iota.discovery.server.query.QueryOperationsModule;
import fr.unicaen.iota.discovery.server.util.Util;
import org.hibernate.HibernateException;

public class TestCreateBDD {

    public static void main(String[] args) throws HibernateException {
        QueryOperationsModule mod = new QueryOperationsModule();

        Partner p1 = new Partner();
        p1.setId(1);
        p1.setActive(true);
        p1.setPartnerID("p1");
        p1.setDate(Util.getActualTimestamp());
        p1.setServiceType("epcis");
        p1.setServiceAddress("www.epcisP1.com");
        mod.partnerCreate(p1);
        User u1 = new User();
        u1.setId(1);
        u1.setPartner(p1);
        u1.setPasswd("toto");
        u1.setUserID("u1");
        u1.setLogin("toto");
        u1.setDate(Util.getActualTimestamp());
        mod.userCreate(u1);

        Partner p2 = new Partner();
        p2.setId(1);
        p2.setActive(true);
        p2.setPartnerID("p2");
        p2.setDate(Util.getActualTimestamp());
        p2.setServiceType("epcis");
        p2.setServiceAddress("www.epcisP2.com");
        mod.partnerCreate(p2);
        User u2 = new User();
        u2.setId(1);
        u2.setPartner(p2);
        u2.setPasswd("titi");
        u2.setUserID("u2");
        u2.setLogin("titi");
        u2.setDate(Util.getActualTimestamp());
        mod.userCreate(u2);

        Partner p3 = new Partner();
        p3.setId(1);
        p3.setActive(true);
        p3.setPartnerID("p3");
        p3.setDate(Util.getActualTimestamp());
        p3.setServiceType("epcis");
        p3.setServiceAddress("www.epcisP3.com");
        mod.partnerCreate(p3);
        User u3 = new User();
        u3.setId(1);
        u3.setPartner(p3);
        u3.setPasswd("tata");
        u3.setUserID("u3");
        u3.setLogin("tata");
        u3.setDate(Util.getActualTimestamp());
        mod.userCreate(u3);

        Sc sc001 = new Sc();
        sc001.setId(1);
        sc001.setPartner(p1);
        sc001.setScID("sc001");
        sc001.setDate(Util.getActualTimestamp());
        mod.scCreate(sc001);

        Sc sc002 = new Sc();
        sc002.setId(1);
        sc002.setPartner(p2);
        sc002.setScID("sc002");
        sc002.setDate(Util.getActualTimestamp());
        mod.scCreate(sc002);

        Sc sc003 = new Sc();
        sc003.setId(1);
        sc003.setPartner(p3);
        sc003.setScID("sc003");
        sc003.setDate(Util.getActualTimestamp());
        mod.scCreate(sc003);

        // associations with P1
        Scassociation scasso = new Scassociation();
        scasso.setId(1);
        scasso.setSc(sc001);
        scasso.setPartner(p2);
        scasso.setDate(Util.getActualTimestamp());
        mod.scassociationCreate(scasso);
        scasso = new Scassociation();
        scasso.setId(1);
        scasso.setSc(sc001);
        scasso.setPartner(p3);
        scasso.setDate(Util.getActualTimestamp());
        mod.scassociationCreate(scasso);

        // associations with P2
        scasso = new Scassociation();
        scasso.setId(1);
        scasso.setSc(sc002);
        scasso.setPartner(p3);
        scasso.setDate(Util.getActualTimestamp());
        mod.scassociationCreate(scasso);

        // associations with P2
        scasso = new Scassociation();
        scasso.setId(1);
        scasso.setSc(sc003);
        scasso.setPartner(p1);
        scasso.setDate(Util.getActualTimestamp());
        mod.scassociationCreate(scasso);

        // create an event by p3
        //		Event event = new Event();
        //		event.setPartner(p3);
        //		event.setEpc("epc1");
        //		event.setEpcClass("C_default");
        //		event.setEventTimeStamp(new Date());
        //		event.setEventType("Object");
        //		event.setBizStep("lifecycle1");
        //		event.setSourceTimeStamp(new Date());
        //		mod.eventCreate(event);
        //
        //
        //		// create an event by p3
        //
        //		Event event2 = new Event();
        //		event2.setPartner(p3);
        //		event.setEpc("epc2");
        //		event.setEpcClass("C_default");
        //		event.setEventTimeStamp(new Date());
        //		event.setEventType("Object");
        //		event.setBizStep("lifecycle2");
        //		event.setSourceTimeStamp(new Date());
        //		mod.eventCreate(event2);
    }
}
