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
package fr.unicaen.iota.eta.callback.filter.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public final class Constants {

    private Constants() {
    }
    private static final Log LOG = LogFactory.getLog(Constants.class);
    public static final String XACML_URL;

    static {
        Properties properties = new Properties();
        InputStream is = Constants.class.getClassLoader().getResourceAsStream("application.properties");
        LOG.debug("Chargement des propriétés de l'application");

        try {
            properties.load(is);
        } catch (IOException ex) {
            LOG.fatal(null, ex);
        }
        XACML_URL = properties.getProperty("xacml-url");
    }
}