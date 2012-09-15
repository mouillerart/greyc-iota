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
package fr.unicaen.iota.discovery.server.query;

import fr.unicaen.iota.discovery.server.hibernate.*;
import fr.unicaen.iota.discovery.server.util.Constants;
import fr.unicaen.iota.discovery.server.util.HibernateUtil;
import fr.unicaen.iota.discovery.server.util.Util;
import java.io.IOException;
import java.math.BigInteger;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.xbill.DNS.*;

public class QueryOperationsModule {

    private static final Log log = LogFactory.getLog(QueryOperationsModule.class);

    public boolean eventToPublishDelete(Collection<EventToPublish> list) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            StringBuilder req = new StringBuilder("DELETE FROM eventtopublish WHERE ");
            for (EventToPublish e : list) {
                req.append(" ID = '");
                req.append(e.getId());
                req.append("' OR ");
            }
            req.append("0");
            Query query = session.createSQLQuery(req.toString());
            query.executeUpdate();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public List<EventToPublish> eventToPublishLookup(int limit) {
        List<EventToPublish> events;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            long value = Calendar.getInstance().getTimeInMillis() - Constants.PUBLISHER_EVENT_REPUBLISH_GAP;
            String request = "select * from eventtopublish where lastupdate < '" + new Timestamp(value) + "' OR lastupdate = '" + Constants.DEFAULT_EVENT_TOPUBLISH_TIMESTAMP + "' limit " + limit;
            Query query = session.createSQLQuery(request);
            List<Object> resultsRaw = query.list();
            events = createEventToPublishList(resultsRaw);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        if (events == null || events.isEmpty()) {
            return null;
        }
        return events;
    }

    private Event eventLookupForPublisher(int id) {
        Event event;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            long value = Calendar.getInstance().getTimeInMillis() - Constants.PUBLISHER_EVENT_REPUBLISH_GAP;
            String request = "select * from event where ID='" + id + "'";
            Query query = session.createSQLQuery(request);
            Object resultRaw = query.uniqueResult();
            event = createEvent(resultRaw);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", e);
            }
            session.close();
            return null;
        }
        return event;
    }

    private Event createEvent(Object object) {
        Event event = new Event();
        Object[] tab = (Object[]) object;
        event.setId(((BigInteger) tab[0]).intValue());
        event.setEpc((String) tab[1]);
        event.setEPCClass((String) tab[3]);
        event.setEventTimeStamp((Timestamp) tab[4]);
        event.setSourceTimeStamp((Timestamp) tab[5]);
        event.setBizStep((String) tab[6]);
        event.setEventType((String) tab[7]);
        return event;
    }

    private List<EventToPublish> createEventToPublishList(Collection<Object> resultsRaw) {
        List<EventToPublish> levt = new ArrayList<EventToPublish>();
        for (Object obj : resultsRaw) {
            Object[] tab = (Object[]) obj;
            EventToPublish eventToPublish = new EventToPublish();
            eventToPublish.setId(((BigInteger) tab[0]).intValue());
            eventToPublish.setLastupdate((Timestamp) tab[2]);
            int evtId = ((BigInteger) tab[1]).intValue();
            Event e = eventLookupForPublisher(evtId);
            eventToPublish.setEvent(e);
            levt.add(eventToPublish);
        }
        return levt;
    }

    public void eventToPublishEnque(Collection<EventToPublish> list) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            StringBuilder req = new StringBuilder("UPDATE eventtopublish SET lastupdate = '" + Util.getActualTimestamp() + "' WHERE ");
            for (EventToPublish e : list) {
                req.append(" ID = '");
                req.append(e.getId());
                req.append("' OR ");
            }
            req.append("0");
            Query query = session.createSQLQuery(req.toString());
            query.executeUpdate();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", e);
            }
            session.close();
        }
    }

    public boolean eventDeleteAll(String partnerId) {
        List<Event> events;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            events = new ArrayList<Event>(session.createQuery("from Event where partner_ID='" + partnerId + "'").list());
            for (Event e : events) {
                session.delete(e);
            }
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", e);
            }
            session.close();
            return false;
        }
        return true;
    }

    public List<Event> eventLookup(Partner p) {
        List<Event> events;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            events = new ArrayList<Event>(session.createQuery("from Event where partner_ID='" + p.getId() + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return new ArrayList<Event>();
        }
        return events == null ? new ArrayList<Event>() : events;
    }

    public boolean partnerCreate(Partner partner) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(partner);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean partnerDelete(Partner partner) {
        boolean res = true;
        partner.setActive(false);
        return res && partnerUpdate(partner);
    }

    public boolean partnerUpdate(Partner partner) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(partner);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public List<Partner> partnerLookup(String partnerID) {
        List<Partner> partners;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            partners = new ArrayList<Partner>(session.createQuery("from Partner where partnerID='" + partnerID + "'").list());
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            session.close();
            return null;
        }
        return partners;
    }

    public Partner partnerLookup(int partnerUID) {
        List<Partner> partners;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            partners = new ArrayList<Partner>(session.createQuery("from Partner where ID='" + partnerUID + "'").list());
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            session.close();
            return null;
        }
        return partners.isEmpty() ? null : partners.get(0);
    }

    public List<Partner> partnerLookupAll() {
        List<Partner> partners;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            partners = new ArrayList<Partner>(session.createQuery("from Partner").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return partners;
    }

    public boolean userCreate(User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(user);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean userDelete(User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(user);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean userUpdate(User user) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(user);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public List<User> userLookup(String userID) {
        List<User> users;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            users = new ArrayList<User>(session.createQuery("from User where userID='" + userID + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return users;
    }

    public User userLookup(int uid) {
        List<User> users;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            users = new ArrayList<User>(session.createQuery("from User where ID='" + uid + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return users.isEmpty() ? null : users.get(0);
    }

    public List<User> userLookup(String login, String passwd) {
        List<User> users;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            users = new ArrayList<User>(session.createQuery("from User where login='" + login + "' AND passwd='" + passwd + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return users;
    }

    public boolean scCreate(Sc sc) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(sc);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scDelete(String ScID) {
        return true;
    }

    public boolean scDelete(Sc sc) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(sc);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scUpdate(Sc sc) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(sc);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public List<Sc> scLookup(String scID) {
        List<Sc> Scs;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Scs = new ArrayList<Sc>(session.createQuery("from Sc where scID='" + scID + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return Scs;
    }

    public List<Sc> scLookup(String scID, Partner partner) {
        List<Sc> Scs;
        Session session = null;
        int partner_ID = partner.getId();
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            Scs = new ArrayList<Sc>(session.createQuery("from Sc where scID='" + scID + "' AND partner_ID='" + partner_ID + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return Scs;
    }

    public boolean eventCreate(Event event) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(event);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception ex) {
                log.error("unable to rollback", ex);
            }
            session.close();
            return false;
        } catch (Exception e) {
            log.error(null, e);
        }
        return true;
    }

    public Event eventLookup(double eventId) {
        List<Event> events;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            events = new ArrayList<Event>(session.createQuery("from Event where id='" + eventId + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return events.isEmpty() ? null : events.get(0);
    }

    public boolean eventDelete(double eventID) {
        Event e = eventLookup(eventID);
        e.setEventType("void");
        return eventUpdate(e);
    }

    public boolean eventRealDelete(double eventId) {
        Session session = null;
        try {
            Event e = eventLookup(eventId);
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(e);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException ex) {
            log.error(null, ex);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean eventUpdate(Event event) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.update(event);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public List<Event> eventLookup(String epc) {
        List<Event> events;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            events = new ArrayList<Event>(session.createQuery("select e from Event e where e.Epc='" + epc + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return events;
    }

    @Deprecated
    public List<Event> eventLookup(String epc, Partner p) {
        log.trace(epc + ": ");
        p.getId();
        List<Event> events;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            log.trace(epc + ": ");
            String req = "select e FROM Event e, Scassociation sca WHERE e.Epc='" + epc + "' AND (e.Partner.Id='" + p.getId() + "' OR (sca.Sc.Partner.Id = e.Partner.Id AND sca.Partner.Id='" + p.getId() + "'))";
            events = new ArrayList<Event>(session.createQuery(req).list());
            log.trace(epc + ": ");
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            session.close();
            return null;
        }
        return events;
    }

    public boolean scassociationCreate(Scassociation scasso) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(scasso);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scassociationDelete(Scassociation scasso) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.delete(scasso);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scassociationUpdate(Scassociation scasso) {
        return true;
    }

    public List<Scassociation> scassociationLookupBySc(Integer scID) {
        List<Scassociation> scasso;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            scasso = new ArrayList<Scassociation>(session.createQuery("from Scassociation where sc_ID='" + scID + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return scasso;
    }

    public List<Scassociation> scassociationLookup(String id) {
        List<Scassociation> scasso;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            scasso = new ArrayList<Scassociation>(session.createQuery("from Scassociation where ID='" + id + "'").list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return scasso;
    }

    public Map<String, String> queryONS(String formatedEpc, String[] ONSAddresses) {
        Map<String, String> result = new HashMap<String, String>();
        Record[] records;
        try {
            records = reverseDns(formatedEpc, ONSAddresses);
        } catch (IOException e) {
            log.error(null, e);
            return null;
        }
        if (records == null) {
            return null;
        }
        for (Record record : records) {
            String entry = record.rdataToString();
            if (entry.split(Constants.EPCIS_ONS_TYPE).length > 1) {
                String[] tab = entry.split("\\!\\^\\.\\*\\$\\!|\\!");
                result.put(Constants.EPCIS_SERVICE_TYPE, tab[1]);
            }
            if (entry.split(Constants.SPEC_ONS_TYPE).length > 1) {
                String[] tab = entry.split("\\!\\^\\.\\*\\$\\!|\\!");
                result.put(Constants.SPEC_SERVICE_TYPE, tab[1]);
            }
            if (entry.split(Constants.DS_ONS_TYPE).length > 1) {
                String[] tab = entry.split("\\!\\^\\.\\*\\$\\!|\\!");
                result.put(Constants.DS_SERVICE_TYPE, tab[1]);
            }
        }
        return result;
    }

    public void pingONS(String ONSAddresses) throws TextParseException, UnknownHostException {
        Lookup l = new Lookup("version.bind.", Type.TXT, DClass.CH);
        l.setResolver(new SimpleResolver(ONSAddresses));
        l.run();
        if (l.getResult() == Lookup.SUCCESSFUL) {
            log.trace("PING ONS: " + l.getAnswers()[0].rdataToString());
        } else {
            throw new UnknownHostException("ONS addresse unreachable");
        }
    }

    private String printTab(String[] tab) {
        StringBuilder result = new StringBuilder("[");
        for (String s : tab) {
            result.append(" ");
            result.append(s);
        }
        result.append(" ]");
        return result.toString();
    }

    private Record[] reverseDns(String hostIp, String[] ONSAddresses) throws IOException {
        log.trace("resolve ons: " + hostIp + ": " + printTab(ONSAddresses));
        Record opt = null;
        Resolver res = new ExtendedResolver(ONSAddresses);

        Name name = new Name(hostIp);
        int type = Type.NAPTR;
        int dclass = DClass.IN;
        Record rec = Record.newRecord(name, type, dclass);
        Message query = Message.newQuery(rec);
        Message response;
        try {
            response = res.send(query);
        } catch (SocketTimeoutException e) {
            log.error("ONS not answering!", e);
            return null;
        }
        Record[] answers = response.getSectionArray(Section.ANSWER);
        if (answers.length == 0) {
            return null;
        } else {
            for (Record r : answers) {
                if (r.getType() == Type.CNAME) {
                    log.trace("Domain changed to: " + r.rdataToString());
                }
            }
            return answers;
        }
    }

    public BizStepId bizStepLookup(String uri) {
        List<BizStepId> bizStepIds;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String request = "from BizStepId where uri='" + uri + "'";
            bizStepIds = new ArrayList<BizStepId>(session.createQuery(request).list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return bizStepIds.isEmpty() ? null : bizStepIds.get(0);
    }

    public boolean scBusinessStepRestrictionCreate(ScBusinessStepRestriction scBusinessStepRestriction) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(scBusinessStepRestriction);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean bizStepCreate(BizStepId bizStepId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(bizStepId);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scBusinessStepRestrictionRemove(int scBizStepRestrictionId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<ScBusinessStepRestriction> list = (List<ScBusinessStepRestriction>) session.createQuery("from ScBusinessStepRestriction where id=" + scBizStepRestrictionId).list();
            if (list == null || list.isEmpty()) {
                log.error("no scBizStepRestriction found for id: " + scBizStepRestrictionId);
                return false;
            }
            session.delete(list.get(0));
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public EPCClassId epcClassLookup(String uri) {
        List<EPCClassId> epcClassIds;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String request = "from EPCClassId where uri='" + uri + "'";
            epcClassIds = new ArrayList<EPCClassId>(session.createQuery(request).list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        return epcClassIds.isEmpty() ? null : epcClassIds.get(0);
    }

    public boolean epcClassCreate(EPCClassId epcClassId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(epcClassId);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scEPCClassRestrictionCreate(ScEPCClassRestriction scEPCClassRestriction) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(scEPCClassRestriction);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scEPCClassRestrictionRemove(int scEPCClassRestrictionId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<ScEPCClassRestriction> list = (List<ScEPCClassRestriction>) session.createQuery("from ScEPCClassRestriction where id=" + scEPCClassRestrictionId).list();
            if (list == null || list.isEmpty()) {
                log.error("no scBizStepRestriction found for id: " + scEPCClassRestrictionId);
                return false;
            }
            session.delete(list.get(0));
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public EPCs epcsLookup(String uri) {
        List<EPCs> epcs;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            String request = "from EPCs where uri='" + uri + "'";
            epcs = new ArrayList<EPCs>(session.createQuery(request).list());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return null;
        }
        if (!epcs.isEmpty()) {
            return epcs.get(0);
        }
        return null;
    }

    public boolean EPCsCreate(EPCs epcs) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(epcs);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scEPCsRestrictionCreate(ScEPCsRestriction scEPCsRestriction) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(scEPCsRestriction);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scEPCsRestrictionRemove(int scEPCsRestrictionId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<ScEPCsRestriction> list = (List<ScEPCsRestriction>) session.createQuery("from ScEPCsRestriction where id=" + scEPCsRestrictionId).list();
            if (list == null || list.isEmpty()) {
                log.error("no scBizStepRestriction found for id: " + scEPCsRestrictionId);
                return false;
            }
            session.delete(list.get(0));
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scEventTimeRestrictionCreate(ScEventTimeRestriction scEventTimeRestriction) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(scEventTimeRestriction);
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }

    public boolean scEventTimeRestrictionRemove(int scEventTimeRestrictionId) {
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            List<ScEventTimeRestriction> list = (List<ScEventTimeRestriction>) session.createQuery("from ScEventTimeRestriction where id=" + scEventTimeRestrictionId).list();
            if (list == null || list.isEmpty()) {
                log.error("no scBizStepRestriction found for id: " + scEventTimeRestrictionId);
                return false;
            }
            session.delete(list.get(0));
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            log.error(null, e);
            try {
                session.getTransaction().rollback();
            } catch (Exception exep) {
                log.error("unable to rollback", exep);
            }
            session.close();
            return false;
        }
        return true;
    }
}
