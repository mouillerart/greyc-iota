/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.eta.callback.filter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 *
 */
public class FilterContextListener implements ServletContextListener {

    private final Filter filter;

    public FilterContextListener() {
        filter = new Filter(Constants.STARTUP_DELAY, Constants.POLLING_DELAY);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        filter.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        filter.stop();
    }
}
