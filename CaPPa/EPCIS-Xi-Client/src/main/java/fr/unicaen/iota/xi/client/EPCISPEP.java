/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Oranges Labs
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
package fr.unicaen.iota.xi.client;

import fr.unicaen.iota.xacml.pep.MethodNamesCapture;
import fr.unicaen.iota.xacml.pep.MethodNamesQuery;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import fr.unicaen.iota.xacml.pep.XACMLEPCISMasterData;
import fr.unicaen.iota.xacml.policy.Module;
import fr.unicaen.iota.xacml.request.EventRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class EPCISPEP extends PEP implements MethodNamesQuery, MethodNamesCapture {

    private static final Log log = LogFactory.getLog(EPCISPEP.class);

    public EPCISPEP(String url, String pksFilename, String pksPassword, String trustPksFilename, String trustPksPassword) {
        super(url, pksFilename, pksPassword, trustPksFilename, trustPksPassword);
    }

    @Override
    public int hello(String userId, String partnerId, String module) {
        log.debug("process hello policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "hello", partnerId, module);
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int queryEvent(String userId, XACMLEPCISEvent epcisEvent) {
        log.debug("process queryEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "queryEvent", epcisEvent, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int queryMasterData(String userId, XACMLEPCISMasterData epcisMasterData) {
        log.debug("process queryMasterData policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "queryMasterData", epcisMasterData, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int subscribe(String userId, String partnerId) {
        log.debug("process subscribe policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "subscribe", partnerId, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int unsubscribe(String userId, String partnerId) {
        log.debug("process unsubscribe policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "unsubscribe", partnerId, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int canBe(String userId, String partnerId) {
        log.debug("process canBe policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "canBe", partnerId, Module.queryModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int captureEvent(String userId, XACMLEPCISEvent epcisEvent) {
        log.debug("process captureEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "captureEvent", epcisEvent, Module.captureModule.getValue());
        return processXACMLRequest(eventRequest);
    }

    @Override
    public int captureMasterData(String userId, XACMLEPCISMasterData epcisMasterData) {
        log.debug("process captureMasterDataEvent policy for user: " + userId);
        EventRequest eventRequest = new EventRequest(userId, "captureMasterDataEvent", epcisMasterData, Module.captureModule.getValue());
        return processXACMLRequest(eventRequest);
    }

}
