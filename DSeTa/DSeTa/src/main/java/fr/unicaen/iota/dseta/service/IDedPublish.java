/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013 Université de Caen Basse-Normandie, GREYC
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

import fr.unicaen.iota.ds.commons.Publish;
import fr.unicaen.iota.ds.model.CreateResponseType;
import fr.unicaen.iota.ds.model.EventCreateResp;
import fr.unicaen.iota.dseta.client.DSeTaClient;
import fr.unicaen.iota.dseta.model.EventCreateReq;
import fr.unicaen.iota.dseta.utils.Constants;
import fr.unicaen.iota.dseta.utils.Utils;
import fr.unicaen.iota.nu.ONSEntryType;
import fr.unicaen.iota.tau.model.Identity;
import java.io.IOException;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.SAXException;

public class IDedPublish extends Publish {

    private static final Log LOG = LogFactory.getLog(IDedPublish.class);

    public IDedPublish(String myAddress, String[] onsHosts, String jmsUrl, String jmsLogin, String jmsPassword, String timeProperty) {
        super(myAddress, onsHosts, jmsUrl, jmsLogin, jmsPassword, timeProperty);
    }

    /**
     * Publishes the event. If the event can not be published, the event is resend to the queue.
     * @param consumer The JMS consumer which retrieves the event to publish.
     * @param timeout Waits until this time expires (in milliseconds).
     * @param producerSession The JMS session associated to the producer.
     * @param producer The JMS producer which sends the event to the next queue if the event could not be published.
     * @param runtime The runtime.
     * @return True if the publishing can continue.
     * @throws JMSException If an error occurred with the JMS provider.
     * @throws JAXBException If an error occurred during the conversion from the string to <code>DSEvent</code>.
     * @throws ParserConfigurationException If an error occurred during the conversion from the JMS message to string.
     * @throws SAXException If an error occurred during the conversion from the JMS message to string.
     * @throws IOException If an I/O error occurred.
     */
    @Override
    public boolean publishEvent(MessageConsumer consumer, long timeout, Session producerSession, MessageProducer producer,
            long runtime) throws JMSException, JAXBException, ParserConfigurationException, SAXException, IOException {
        boolean continuePublishing = true;
        boolean republish = true;
        Message message = consumer.receive(timeout);
        if (message == null) {
            return false;
        }
        TextMessage text = (TextMessage) message;
        String docText = text.getText();
        if (text.getLongProperty(getTimeProperty()) <= runtime) {
            EventCreateReq evtReq = Utils.extractsEventCreateReq(docText);
            String epc = evtReq.getDsEvent().getEpc();
            String dsAddress = getDSAdress(epc, ONSEntryType.ided_ds);
            String owner = evtReq.getOwner().getAsString();
            if (dsAddress == null) {
                LOG.error("Could not get the referent DSeTa address for " + epc);
            }
            else {
                try {
                    if (notMe(dsAddress)) {
                        Identity id = new Identity();
                        id.setAsString(owner);
                        DSeTaClient dsetaClient = new DSeTaClient(id, dsAddress, Constants.PKS_FILENAME, Constants.PKS_PASSWORD,
                                Constants.TRUST_PKS_FILENAME, Constants.TRUST_PKS_PASSWORD);
                        EventCreateResp result = dsetaClient.eventCreate(evtReq.getDsEvent(), owner);
                        if (!CreateResponseType.NOT_ADDED.equals(result.getValue())) {
                            republish = false;
                            LOG.info("Event published to " + dsAddress);
                        }
                    }
                    else {
                        republish = false;
                        LOG.info("Event not published: already in the corresponding DSeTa.");
                    }
                } catch (Exception ex) {
                    String msg = "Fails to send event to " + dsAddress;
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(msg, ex);
                    }
                    else {
                        LOG.info(msg);
                    }
                }
            }
        }
        else {
            continuePublishing = false;
        }

        if (republish) {
            sendsEvent(producerSession, producer, docText);
        }
        message.acknowledge();
        return continuePublishing;
    }

}
