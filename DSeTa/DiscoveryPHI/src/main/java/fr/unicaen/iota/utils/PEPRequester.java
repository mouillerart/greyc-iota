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
package fr.unicaen.iota.utils;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.auth.User;
import fr.unicaen.iota.xacml.pep.DSPEP;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public final class PEPRequester {

    private static final Log log = LogFactory.getLog(PEPRequester.class);

    private PEPRequester() {
    }

    private static Object runMethod(Object obj, Object[] args, String methodName) {
        try {
            Class<?>[] paramTypes = null;
            if (args != null) {
                paramTypes = new Class<?>[args.length];
                for (int i = 0; i < args.length; ++i) {
                    paramTypes[i] = args[i].getClass();
                }
            }
            Method m = obj.getClass().getMethod(methodName, paramTypes);
            return m.invoke(obj, args);
        } catch (IllegalAccessException ex) {
            log.fatal(null, ex);
        } catch (IllegalArgumentException ex) {
            log.fatal(null, ex);
        } catch (InvocationTargetException ex) {
            log.fatal(null, ex);
        } catch (NoSuchMethodException ex) {
            log.fatal(null, ex);
        } catch (SecurityException ex) {
            log.fatal(null, ex);
        }
        return Result.DECISION_DENY;
    }

    public static int checkAccess(User user, String methodName) {
        String userS = user.getUserID();
        String partner = user.getPartner().getPartnerID();
        ArrayList<String> args = new ArrayList<String>();
        args.add(userS);
        args.add(partner);
        DSPEP dspep = new DSPEP();
        return (Integer) runMethod(dspep, args.toArray(), methodName);
    }
}
