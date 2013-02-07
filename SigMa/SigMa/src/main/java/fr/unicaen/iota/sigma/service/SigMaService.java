/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.sigma.service;

import fr.unicaen.iota.sigma.SigMaFunctions;
import fr.unicaen.iota.sigma.wsdl.SigMaServicePortType;
import fr.unicaen.iota.sigma.xsd.Principal;
import fr.unicaen.iota.sigma.xsd.Verification;
import fr.unicaen.iota.sigma.xsd.VerifyResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 * This
 * <code>SigMAService</code> implements the SigMA web service. It receives and
 * verifies its signature.
 */
public class SigMaService implements SigMaServicePortType {

    private static final Log log = LogFactory.getLog(SigMaService.class);

    public Verification verify(EPCISEventType epcisEventType) {
        log.info("Function verify called");
        Principal principal = new Principal();
        principal.setEvent(epcisEventType);
        return verify(principal);
    }

    public Verification verify(Principal principal) {
        EPCISEventType event = principal.getEvent();
        Verification verification = new Verification();
        VerifyResponse response = new VerifyResponse();

        boolean value;
        try {
            SigMaFunctions sigMAFunctions = new SigMaFunctions(Constants.KEY_STORE_FILE_PATH, Constants.KEY_STORE_PASSWORD);
            value = sigMAFunctions.verify(event);
//            value = true;
            response.setValue(value);
        } catch (Exception e) {
            log.error("Error during verification", e);
            response.setMessage(e.getMessage());
        }
        verification.setVerifyResponse(response);
        log.info("Event signature verified");
        return verification;
//        } catch (FileNotFoundException ex) {
//            log.fatal("", ex);
//        } catch (CertificateException ex) {
//            log.fatal("", ex);
//        } catch (IOException ex) {
//            log.fatal("", ex);
//        } catch (NoSuchAlgorithmException ex) {
//            log.fatal("", ex);
//        } catch (InvalidKeyException ex) {
//            log.fatal("", ex);
//        } catch (SignatureException ex) {
//            log.fatal("", ex);
//        } catch (java.security.cert.CertificateException ex) {
//            log.fatal("", ex);
//        } catch (NoSuchPaddingException ex) {
//            log.fatal("", ex);
//        } catch (IllegalBlockSizeException ex) {
//            log.fatal("", ex);
//        } catch (BadPaddingException ex) {
//            log.fatal("", ex);
//        }
    }
}
