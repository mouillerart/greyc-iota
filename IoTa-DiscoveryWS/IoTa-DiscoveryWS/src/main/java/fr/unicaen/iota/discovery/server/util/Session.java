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
package fr.unicaen.iota.discovery.server.util;

import fr.unicaen.iota.discovery.server.hibernate.User;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Session {

    private Session() {
    }
    private static final Log log = LogFactory.getLog(Session.class);
    private static final Map<String, User> sessions = new HashMap<String, User>();
    private static MessageDigest MD5;

    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            log.fatal(null, e);
        }
    }

    public static synchronized String openSession(User user) {
        if (MD5 == null) {
            log.error("MD5 not avalaible");
            return null;
        }
        log.debug("opening session for user " + user.getUserID());
        String sessionID;
        long today = new Date().getTime();
        try {
            do {
                String text = Long.toString(today) + user.getLogin() + (today * Math.random()); // TODO add login+pass
                byte[] digest = MD5.digest(text.getBytes("UTF-8"));
                sessionID = new BigInteger(1, digest).toString(16);
            } while (sessions.containsKey(sessionID));
        } catch (UnsupportedEncodingException e) {
            log.error(null, e);
            return null;
        }
        sessions.put(sessionID, user);
        return sessionID;
    }

    public static synchronized boolean isValidSession(String sessionID) {
        return sessions.containsKey(sessionID);
    }

    public static synchronized void closeSession(String sessionID) {
        log.debug("closing session " + sessionID);
        sessions.remove(sessionID);
    }

    public static synchronized User getUser(String sessionID) {
        return sessions.get(sessionID);
    }
}
