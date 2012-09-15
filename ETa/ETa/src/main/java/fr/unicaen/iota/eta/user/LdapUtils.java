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

import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LdapUtils {

    private static final Log log = LogFactory.getLog(LdapUtils.class);

    public static void listUsers(DirContext dirContext) {
        try {
            log.trace("Object= " + dirContext);
            NamingEnumeration e = dirContext.list("");

            for (; e.hasMoreElements();) {
                NameClassPair o = (NameClassPair) e.nextElement();
                String name = o.getName();
                Attributes att = dirContext.getAttributes(o.getName());
                Attribute pwd = att.get("userpassword");

                log.trace("Object=" + name + " pwd: " + pwd + "/n"); //+ " class=" + o.getClass().getName());
            }
        } catch (NamingException e) {
            log.error(null, e);
        }
    }
}