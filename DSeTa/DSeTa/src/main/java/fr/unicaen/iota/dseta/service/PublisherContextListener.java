/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.dseta.service;

import fr.unicaen.iota.dseta.utils.Constants;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class PublisherContextListener implements ServletContextListener {

    private Publisher publisher;

    public PublisherContextListener() {
        publisher = new Publisher(Constants.PUBLISHER_DELAY, Constants.PUBLISHER_PERIOD, Constants.PUBLISHER_TIMEOUT);
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        publisher.start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        publisher.stop();
    }

}
