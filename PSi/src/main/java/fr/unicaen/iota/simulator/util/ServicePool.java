/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.util;

import fr.unicaen.iota.eta.capture.ETaCaptureClient;

/**
 * Not a real pool since, from Fosstrak Epcis Capture Client version 0.4.2, a
 * CaptureClient can’t be reused anymore.
 * 
 * Serves as a limitor for the number of simultaneous clients.
 *
 * @stereotype Singleton
 */
public class ServicePool {

    private static final int POOL_SIZE = 10;
    private static final ServicePool servicePool = new ServicePool();
    private int available;

    private ServicePool() {
        available = POOL_SIZE;
    }

    public static synchronized ServicePool getInstance() {
        return servicePool;
    }

    public synchronized ETaCaptureClient getServiceInstance(String EndPointEPR) throws InterruptedException {
        if (available == 0) {
            wait();
        }
        available--;
        ETaCaptureClient service = new ETaCaptureClient(EndPointEPR,
                Config.pks_filename, Config.pks_password, Config.trust_pks_filename, Config.trust_pks_password);
        return service;
    }

    public synchronized void releaseInstance(ETaCaptureClient service) throws InterruptedException {
        available++;
        notifyAll();
    }
}
