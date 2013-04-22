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
package fr.unicaen.iota.sigma.test;

import fr.unicaen.iota.sigma.test.controler.Controler;
import fr.unicaen.iota.sigma.test.gui.SigMaTestFrame;

public class SigMaTestGUI {

    public static void main(String[] args) {
        if (args.length != 8) {
            System.err.println("usage: SigMaTestGUI ETaCaptureURL SigMaURL keystoreTLS ksPasswordTLS truststoreTLS tsPasswordTLS keystoreSign ksPasswordSign");
            System.err.println();
            System.err.println("example: SigMaTestGUI https://localhost:8443/eta/capture https://localhost:8443/sigma /srv/keystore.jks store_pw /srv/truststore.jks trust_pw /srv/sigma-cert.p12 store_pw");
            System.exit(1);
        }
        String captureUrl = args[0];
        String sigmaUrl = args[1];
        String tlsKeystore = args[2];
        String tlsKsPassword = args[3];
        String tlsTruststore = args[4];
        String tlsTsPassword = args[5];
        String signKeystore = args[6];
        String signKsPassword = args[7];

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(SigMaTestFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        final Controler controler = new Controler(captureUrl, sigmaUrl, tlsKeystore, tlsKsPassword,
            tlsTruststore, tlsTsPassword, signKeystore, signKsPassword);

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SigMaTestFrame(controler).setVisible(true);
            }
        });
    }

}
