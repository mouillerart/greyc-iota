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
package fr.unicaen.iota.simulator.server.util;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class AccessControlModule {

    private static final Log log = LogFactory.getLog(AccessControlModule.class);

    private AccessControlModule() {
    }

    public static boolean isAuthenticated(String publicKey, String message) {
        try {
            return MD5.MD5_Algo(publicKey + Configuration.MDP).equals(message);
        } catch (NoSuchAlgorithmException ex) {
            log.fatal(null, ex);
        } catch (UnsupportedEncodingException ex) {
            log.fatal(null, ex);
        }
        return false;
    }
}
