/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.eta.user;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Session {

    private Session() {
    }
    private static final Log log = LogFactory.getLog(Session.class);
    private static List<String> sessionIDList = new ArrayList<String>();
    private static List<User> sessionUserList = new ArrayList<User>();

    public static synchronized String openSession(User user) {
        log.info("opening session for user " + user);
        String sessionID;
        java.util.Date today = new java.util.Date();
        try {
            do {
                sessionID = MD5.MD5_Algo(Long.toString(today.getTime()) + user.getUserID() + (today.getTime() * Math.random())); // TODO add login+pass
            } while (sessionIDList.contains(sessionID));
        } catch (NoSuchAlgorithmException e) {
            log.error("An error occurred invoking MD5 algorithm", e);
            return null;
        } catch (UnsupportedEncodingException e) {
            log.error("An error occurred invoking MD5 algorithm", e);
            return null;
        }
        sessionIDList.add(sessionID);
        sessionUserList.add(user);
        return sessionID;
    }

    public static synchronized boolean isValidSession(String sessionID) {
        return sessionIDList.contains(sessionID);
    }

    public static synchronized void closeSession(String sessionID) {
        log.info("closing session " + sessionID);
        int index = sessionIDList.indexOf(sessionID);
        sessionIDList.remove(sessionID);
        sessionUserList.remove(index);
    }

    public static synchronized User getUser(String sessionID) {
        int index = sessionIDList.indexOf(sessionID);
        return sessionUserList.get(index);
    }
}
