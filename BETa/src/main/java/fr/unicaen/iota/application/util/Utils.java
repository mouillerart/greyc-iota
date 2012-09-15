/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.application.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class Utils {

    private static final Log log = LogFactory.getLog(Utils.class);
    
    private Utils() {
    }

    public static String generateSessionId(){
        String sessionID;
        java.util.Date today = new java.util.Date();
        try {
            sessionID = MD5.MD5_Algo(Long.toString(today.getTime()));
        } catch (NoSuchAlgorithmException e) {
            log.error("Can't generate SessionId", e);
            return null;
        } catch (UnsupportedEncodingException e) {
            log.error("Can't generate SessionId", e);
            return null;
        }
        return sessionID;
    }

}
