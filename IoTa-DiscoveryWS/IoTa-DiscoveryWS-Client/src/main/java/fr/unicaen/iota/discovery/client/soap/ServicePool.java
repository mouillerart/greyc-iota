/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.discovery.client.soap;

import fr.unicaen.iota.discovery.client.util.Configuration;
import java.util.LinkedList;
import java.util.Queue;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class ServicePool {

    private static final int POOL_SIZE = Configuration.WS_CONNECTION_POOL_SIZE;
    private static final Queue<ESDS_ServiceStub> POOL = new LinkedList<ESDS_ServiceStub>();
    private static ServicePool servicePool;
    private static final Log LOG = LogFactory.getLog(ServicePool.class);

    private ServicePool() {
        for (int i = 0; i < POOL_SIZE; i++) {
            try {
                ESDS_ServiceStub service = new ESDS_ServiceStub();
                Options option = service._getServiceClient().getOptions();
                option.setCallTransportCleanup(true);
                POOL.offer(service);
            } catch (AxisFault ex) {
                LOG.error("Unable to init service Pool !", ex);
            }
        }
    }

    public int getSize() {
        return POOL.size();
    }

    public static synchronized ServicePool getInstance() {
        if (servicePool == null) {
            servicePool = new ServicePool();
        }
        return servicePool;
    }

    public synchronized ESDS_ServiceStub getServiceInstance(String EndPointEPR) throws InterruptedException {
        LOG.info("DS client pool size: " + POOL.size() + "/" + POOL_SIZE);
        if (POOL.isEmpty()) {
            LOG.warn("[DS client service pool]: waiting for service instance");
            wait();
            LOG.warn("[DS client service pool]: stop waiting for service instance");
        }
        ESDS_ServiceStub service = POOL.remove();
        service._getServiceClient().setTargetEPR(new EndpointReference(EndPointEPR));
        return service;
    }

    public synchronized void releaseInstance(ESDS_ServiceStub service) throws InterruptedException {
        POOL.offer(service);
        service._getServiceClient().setTargetEPR(new EndpointReference(null));
        notifyAll();
    }
}
