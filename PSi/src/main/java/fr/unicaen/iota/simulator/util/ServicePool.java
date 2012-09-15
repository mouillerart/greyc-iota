/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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

import org.fosstrak.epcis.captureclient.CaptureClient;

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

    public synchronized CaptureClient getServiceInstance(String EndPointEPR) throws InterruptedException {
        if (available == 0) {
            wait();
        }
        available--;
        CaptureClient service = new CaptureClient(EndPointEPR);
        return service;
    }

    public synchronized void releaseInstance(CaptureClient service) throws InterruptedException {
        available++;
        notifyAll();
    }
}
