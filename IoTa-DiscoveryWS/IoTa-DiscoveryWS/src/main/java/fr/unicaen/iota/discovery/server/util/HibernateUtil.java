/*
 * Generated via NetBeans Hibernate plugin
 */
package fr.unicaen.iota.discovery.server.util;

import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 */
public final class HibernateUtil {

    private HibernateUtil() {
    }
    private static final SessionFactory sessionFactory;

    static {
        try {
            // Create the SessionFactory from standard (hibernate.cfg.xml)
            // config file.
            Configuration conf = new Configuration();
            conf.configure();
            sessionFactory = conf.buildSessionFactory();
        } catch (Throwable ex) {
            // Log the exception.
            LogFactory.getLog(HibernateUtil.class).fatal("Initial SessionFactory creation failed.", ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
