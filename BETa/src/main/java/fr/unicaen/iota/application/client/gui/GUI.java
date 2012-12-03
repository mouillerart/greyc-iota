/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.application.client.gui;

import fr.unicaen.iota.application.client.CallbackClientImpl;
import fr.unicaen.iota.application.client.Configuration;
import fr.unicaen.iota.application.client.TraceEPCRMIAsync;
import fr.unicaen.iota.application.soap.IoTaException;
import fr.unicaen.iota.application.soap.client.IoTaFault;
import fr.unicaen.iota.application.soap.client.OmICron;
import fr.unicaen.iota.application.util.Utils;
import fr.unicaen.iota.tau.model.Identity;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.fosstrak.epcis.model.EPCISEventType;

/**
 *
 */
public class GUI extends javax.swing.JFrame implements ChangeListener, Observer {

    private static enum AO {

        alpha, omega
    };
    private static final Log log = LogFactory.getLog(GUI.class);
    private String defaultEPC = "";
    private final CallbackClientImpl callBackHandler;

    /**
     * Creates new form mainGui
     *
     * @param callBackHandler
     */
    public GUI(CallbackClientImpl callBackHandler) {
        this.callBackHandler = callBackHandler;
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
            /*
             * 1.7 } catch (ClassNotFoundException| InstantiationException|
             * IllegalAccessException| UnsupportedLookAndFeelException ex) {
             */
        } catch (Exception ex) {
            log.warn(null, ex);
        }
        initComponents();
        log.trace("GUI started");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     */
    @SuppressWarnings("unchecked")
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jButtona = new javax.swing.JButton();
        jButtonw = new javax.swing.JButton();
        closableTabbedPane = new ClosableTabbedPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("EPC:");
        jButtonw.setText("ω-search");
        jButtonw.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActionPerformed(evt, AO.omega);
            }
        });
        jButtona.setText("α-search");
        jButtona.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActionPerformed(evt, AO.alpha);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).
                addGroup(
                layout.createSequentialGroup().
                addContainerGap().addComponent(jLabel1).
                addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).
                addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE).
                addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).
                addComponent(jButtona, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE).
                addComponent(jButtonw, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)).
                addComponent(closableTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 655, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).
                addGroup(layout.createSequentialGroup().
                addContainerGap().
                addGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).
                addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).
                addComponent(jLabel1).
                addComponent(jButtona).
                addComponent(jButtonw)).
                addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).
                addComponent(closableTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)));

        pack();
        /*
         * SearchArea home = new SearchArea("", closableTabbedPane);
         * closableTabbedPane.addClosableTabComponent(home);
         * home.setTitle("Home");
         */
        jTextField1.setText(defaultEPC);
        closableTabbedPane.addChangeListener(this);
        this.setTitle("BETa: Basic Epcis Test Application");
        this.setSize(800, 600);
    }

    private void jButtonActionPerformed(java.awt.event.ActionEvent evt, AO ao) {
        String sessionID = Utils.generateSessionId();
        SearchArea searchArea = new SearchArea(jTextField1.getText(), sessionID, closableTabbedPane);
        callBackHandler.addEPCEventListener(searchArea);
        closableTabbedPane.addClosableTabComponent(searchArea);
        searchArea.setProcessing();
        int index = closableTabbedPane.indexOfComponent(searchArea);
        closableTabbedPane.setSelectedIndex(index);
        switch (ao) {
            case alpha:
                alphaTraceEPC(jTextField1.getText(), sessionID);
                break;
            case omega:
                omegaTraceEPC(jTextField1.getText(), sessionID);
                break;
        }
    }
    // Variables declaration
    private javax.swing.JButton jButtona;
    private javax.swing.JButton jButtonw;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextField1;
    private ClosableTabbedPane closableTabbedPane;

    @Override
    public void stateChanged(ChangeEvent e) {
        if (e.getSource() == closableTabbedPane) {
            for (int i = 0; i < closableTabbedPane.getTabCount(); i++) {
                if (i == closableTabbedPane.getSelectedIndex()) {
                    continue;
                }
                SearchArea sa = (SearchArea) closableTabbedPane.getComponentAt(i);
                int toCut = Math.min(sa.getTitle().length(), 4);
                closableTabbedPane.setTitleAt(i, sa.getTitle().substring(0, toCut) + "...");
            }
            int j = closableTabbedPane.getSelectedIndex();
            SearchArea sa = (SearchArea) closableTabbedPane.getComponentAt(j);
            closableTabbedPane.setTitleAt(j, sa.getTitle());
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        log.trace(arg);
    }

    private void alphaTraceEPC(String text, String sessionID) {
        log.trace("Start RMI Asynchronous Trace");
        Identity identity = new Identity();
        identity.setAsString(Configuration.DEFAULT_IDENTITY);
        new TraceEPCRMIAsync(text, identity, sessionID, callBackHandler).start();
        log.trace("Done");
    }

    private void omegaTraceEPC(String epc, String sessionID) {
        log.trace("Processing omegaTraceEPC ...");
        Identity identity = new Identity();
        identity.setAsString(Configuration.DEFAULT_IDENTITY);
        OmICron client = new OmICron(identity, Configuration.SOAP_SERVICE_URL);
        try {
            List<EPCISEventType> events = client.traceEPC(epc);
            if (!events.isEmpty()) {
                for (EPCISEventType evt : events) {
                    try {
                        callBackHandler.pushEvent(sessionID, evt);
                    } catch (RemoteException ex) {
                        log.error("Could not push event to callback client", ex);
                    }
                }
            } else {
                log.trace("(no events)");
            }
        } catch (IoTaException ex) {
            log.warn("Could not retreive events: " + IoTaFault.explain(ex), ex);
        }
        log.trace("Done");
    }
}
