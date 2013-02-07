/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.epcilon.query;

import fr.unicaen.iota.epcilon.conf.Configuration;
import fr.unicaen.iota.eta.query.QueryControlClientTLS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.Subscribe;

/**
 *
 *
 */
public final class StandingQueryCaptureSubscribe {

    private StandingQueryCaptureSubscribe() {
    }
    private static final Log LOG = LogFactory.getLog(StandingQueryCaptureSubscribe.class);
    private static final long serialVersionUID = 1L;

    public static void subscribe() {
        try {
            QueryControlClientTLS client = new QueryControlClientTLS(Configuration.DEFAULT_QUERY_CLIENT_ADDRESS, Configuration.PKS_FILENAME, Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
            Subscribe subscribe = StandingQueryCaptureModule.createScheduleSubscribe("SimpleEventQuery", Configuration.SUBSCRIPTION_KEY,
                    Configuration.DEFAULT_QUERY_CALLBACK_ADDRESS, Configuration.SUBSCRIPTION_TYPE, Configuration.SUBSCRIPTION_VALUE);
            client.subscribe(subscribe);
            LOG.info("Subscription shared");
        } catch (Exception e) {
            LOG.error("Error during the subscribe method", e);
        }
    }
}
