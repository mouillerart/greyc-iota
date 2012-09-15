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
package fr.unicaen.iota.epcilon.query;

import fr.unicaen.iota.epcilon.conf.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.Subscribe;
import org.fosstrak.epcis.queryclient.QueryControlClient;

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
            QueryControlClient client = new QueryControlClient(Configuration.DEFAULT_QUERY_CLIENT_ADDRESS);
            Subscribe subscribe = StandingQueryCaptureModule.createScheduleSubscribe("SimpleEventQuery", Configuration.SUBSCRIPTION_KEY,
                    Configuration.DEFAULT_QUERY_CALLBACK_ADDRESS, Configuration.SUBSCRIPTION_TYPE, Configuration.SUBSCRIPTION_VALUE);
            client.subscribe(subscribe);
            LOG.info("Subscription shared");
        } catch (Exception e) {
            LOG.error("Error during the subscribe method", e);
        }
    }

}
