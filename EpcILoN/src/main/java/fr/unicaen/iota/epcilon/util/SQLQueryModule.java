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
package fr.unicaen.iota.epcilon.util;

import fr.unicaen.iota.epcilon.conf.Configuration;
import fr.unicaen.iota.epcilon.model.EventToPublish;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 */
public class SQLQueryModule {

    private static final Log LOG = LogFactory.getLog(SQLQueryModule.class);

    public List<EventToPublish> listEventToPublish(int limit) {
        List<EventToPublish> genericListRequest = new ArrayList<EventToPublish>();
        Session session = null;
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            long value = Calendar.getInstance().getTimeInMillis() - Configuration.PUBLISHER_PENDING_REPUBLISH;
            String request = "FROM EventToPublish WHERE lastUpdate < '" + new Timestamp(value).getTime() + "' OR lastUpdate = '" + new Timestamp(Configuration.DEFAULT_EVENT_TO_PUBLISH_TIMESTAMP) + "' ";

            LOG.debug("DB connection opened.");
            genericListRequest = new ArrayList<EventToPublish>(session.createQuery(request).setMaxResults(limit).list());
            LOG.trace("list size: " + genericListRequest.size());
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            try {
                session.getTransaction().rollback();
            } catch (Exception e1) {
                LOG.error("Unable to rollback", e1);
            }
            LOG.error("Can't retrieve events to publish!", e);
            session.close();
        }
        return genericListRequest;
    }

    public void deleteFromDB(List<EventToPublish> whiteList) {
        Session session = null;
        try {
            LOG.info("white list size: " + whiteList.size());
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            LOG.debug("DB connection opened.");
            for (EventToPublish eventToPublish : whiteList) {
                session.delete(eventToPublish);
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            try {
                session.getTransaction().rollback();
            } catch (Exception e1) {
                LOG.error("Unable to rollback", e1);
            }
            LOG.error("Can't delete events to publish!", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    public void saveEvents(List<EventToPublish> evtList) {
        Session session = null;
        try {
            SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
            session = sessionFactory.openSession();
            session.beginTransaction();
            for (EventToPublish evt : evtList) {
                session.save(evt);
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            try {
                session.getTransaction().rollback();
            } catch (Exception e1) {
                LOG.error("unable to rollback", e1);
            }
            LOG.error("Can't delete events to publish!", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
