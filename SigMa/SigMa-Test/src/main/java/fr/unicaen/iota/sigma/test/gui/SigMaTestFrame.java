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
package fr.unicaen.iota.sigma.test.gui;

import fr.unicaen.iota.sigma.test.controler.Controler;
import fr.unicaen.iota.sigma.xsd.VerifyResponse;
import org.fosstrak.epcis.model.ObjectEventType;

public class SigMaTestFrame extends javax.swing.JFrame {

    private Controler controler;
    private ObjectEventType objectEvent = null;
    private PublishDialog publishDialog;
    private UnpublishDialog unpublishDialog;
    private VerifyDialog verifyDialog;
    private UnverifyDialog unverifyDialog;

    /**
     * Creates new form SigMaTestFrame
     */
    public SigMaTestFrame(Controler controler) {
        initComponents();
        this.controler = controler;
        publishDialog = new PublishDialog(this, rootPaneCheckingEnabled);
        publishDialog.setVisible(false);
        unpublishDialog = new UnpublishDialog(this, rootPaneCheckingEnabled);
        unpublishDialog.setVisible(false);
        verifyDialog = new VerifyDialog(this, rootPaneCheckingEnabled);
        verifyDialog.setVisible(false);
        unverifyDialog = new UnverifyDialog(this, rootPaneCheckingEnabled);
        unverifyDialog.setVisible(false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        epcField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        bizStepField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        dispositionField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        readPointField = new javax.swing.JTextField();
        bizLocationField = new javax.swing.JTextField();
        signatureLabel = new javax.swing.JLabel();
        signButton = new javax.swing.JButton();
        publishButton = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        verifyButton = new javax.swing.JButton();
        errorInjectionButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("EPC:");

        epcField.setText("urn:epc:id:sgtin:00000.00000.00001");
        epcField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                epcFieldActionPerformed(evt);
            }
        });

        jLabel3.setText("BizStep:");

        bizStepField.setText("urn:epcglobal:epcis:bizstep:fmcg:shipped");

        jLabel4.setText("Disposition:");

        dispositionField.setText("urn:epcglobal:epcis:disp:fmcg:unknown");

        jLabel5.setText("ReadPoint:");

        jLabel6.setText("BizLocation:");

        jLabel8.setText("Signature:");

        readPointField.setText("urn:epc:id:sgln:0614141.07346.1234");

        bizLocationField.setText("urn:epcglobal:fmcg:loc:0614141073467.A23-49");

        signatureLabel.setText("no signature");

        signButton.setText("SIgn");
        signButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                signButtonActionPerformed(evt);
            }
        });

        publishButton.setText("Publish");
        publishButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                publishButtonActionPerformed(evt);
            }
        });

        jLabel10.setText("SigMa Test Publisher");

        verifyButton.setText("Verify");
        verifyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verifyButtonActionPerformed(evt);
            }
        });

        errorInjectionButton.setText("Error Injection");
        errorInjectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                errorInjectionButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel11)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(verifyButton)
                                .addGap(18, 18, 18)
                                .addComponent(errorInjectionButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(signButton)))
                        .addGap(18, 18, 18)
                        .addComponent(publishButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(61, 61, 61)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(97, 97, 97)
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(bizStepField)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addGap(42, 42, 42)))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                    .addComponent(jLabel6)
                                    .addGap(33, 33, 33)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(45, 45, 45)))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(dispositionField)
                            .addComponent(readPointField)
                            .addComponent(bizLocationField)
                            .addComponent(signatureLabel)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(89, 89, 89)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(epcField))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(jLabel10)
                .addGap(36, 36, 36)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(epcField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(bizStepField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(dispositionField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(readPointField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(bizLocationField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(signatureLabel)))
                .addGap(27, 27, 27)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(signButton)
                    .addComponent(publishButton)
                    .addComponent(verifyButton)
                    .addComponent(errorInjectionButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void epcFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_epcFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_epcFieldActionPerformed

    private void signButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_signButtonActionPerformed
        objectEvent = controler.sign(epcField.getText(),
                bizStepField.getText(),
                dispositionField.getText(),
                readPointField.getText(),
                bizLocationField.getText());
        String signature = controler.getSignature(objectEvent);
        signature = signature.substring(0, 30);
        signature += "...";
        if (signature != null) {
            this.signatureLabel.setText(signature);
        } else {
            this.signatureLabel.setText("Error during signature !");
        }
    }//GEN-LAST:event_signButtonActionPerformed

    private void publishButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_publishButtonActionPerformed
        boolean result = false;
        if (objectEvent != null) {
            result = controler.publish(objectEvent);
        }
        if (result) {
            publishDialog.setVisible(true);
        } else {
            unpublishDialog.setVisible(true);
        }
    }//GEN-LAST:event_publishButtonActionPerformed

    private void verifyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verifyButtonActionPerformed
        boolean result = false;
        VerifyResponse verifyResponse = null;
        if (objectEvent != null) {
            verifyResponse = controler.verify(objectEvent);
            result = verifyResponse.isValue();
        }
        if (result) {
            verifyDialog.setVisible(true);
        } else {
            unverifyDialog.setVisible(true);
        }
    }//GEN-LAST:event_verifyButtonActionPerformed

    private void errorInjectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_errorInjectionButtonActionPerformed
        boolean result = controler.insertErrors(objectEvent);
        if (result) {
            String signature = controler.getSignature(objectEvent);
            signature = signature.substring(0, 30);
            signature += "...";
            if (signature != null) {
                this.signatureLabel.setText(signature);
            } else {
                this.signatureLabel.setText("Error during signature !");
            }
        } else {
            this.signatureLabel.setText("Error during inserting signature errors");
        }
    }//GEN-LAST:event_errorInjectionButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bizLocationField;
    private javax.swing.JTextField bizStepField;
    private javax.swing.JTextField dispositionField;
    private javax.swing.JTextField epcField;
    private javax.swing.JButton errorInjectionButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JButton publishButton;
    private javax.swing.JTextField readPointField;
    private javax.swing.JButton signButton;
    private javax.swing.JLabel signatureLabel;
    private javax.swing.JButton verifyButton;
    // End of variables declaration//GEN-END:variables
}
