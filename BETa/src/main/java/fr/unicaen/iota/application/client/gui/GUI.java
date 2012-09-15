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

import fr.unicaen.iota.application.client.CallBackClientImpl;
import fr.unicaen.iota.application.client.Configuration;
import fr.unicaen.iota.application.client.TraceEPCRMIAsync;
import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.rmi.CallBackClient;
import fr.unicaen.iota.application.soap.client.IOTA_ServiceStub;
import fr.unicaen.iota.application.util.Utils;
import java.rmi.RemoteException;
import java.util.*;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class GUI extends javax.swing.JFrame implements ChangeListener, Observer {

    private static enum AO {

        alpha, omega
    };
    private String defaultEPC = "";
    private CallBackClientImpl callBackHandler;
    private static final Log log = LogFactory.getLog(GUI.class);

    /**
     * Creates new form mainGui
     */
    public GUI() {
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
        SearchArea home = new SearchArea("", null, closableTabbedPane);
        closableTabbedPane.addClosableTabComponent(home);
        home.setTitle("Home");
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
                alphaTraceEPC(jTextField1.getText(), sessionID, callBackHandler);
                break;
            case omega:
                omegaTraceEPC(jTextField1.getText(), sessionID, callBackHandler);
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

    /**
     * @return the callBackHandler
     */
    public CallBackClient getCallBackHandler() {
        return callBackHandler;
    }

    /**
     * @param callBackHandler the callBackHandler to set
     */
    public void setCallBackHandler(CallBackClientImpl callBackHandler) {
        this.callBackHandler = callBackHandler;
    }

    private void alphaTraceEPC(String text, String sessionID, CallBackClientImpl callBackHandler) {
        log.trace("Start RMI Asynchronous Trace");
        new TraceEPCRMIAsync(text, sessionID, callBackHandler).start();
        log.trace("Done");
    }

    private void omegaTraceEPC(String text, String sessionID, CallBackClientImpl callBackHandler) {
        log.trace("Processing omegaTraceEPC ...");
        IOTA_ServiceStub iota_ServiceStub;
        try {
            iota_ServiceStub = new IOTA_ServiceStub(Configuration.SOAP_SERVICE_URL);
        } catch (AxisFault ex) {
            log.fatal(null, ex);
            return;
        }
        IOTA_ServiceStub.TraceEPCRequest traceEPCRequest = new IOTA_ServiceStub.TraceEPCRequest();
        IOTA_ServiceStub.TraceEPCRequestIn in = new IOTA_ServiceStub.TraceEPCRequestIn();
        in.setEpc(text);
        traceEPCRequest.setTraceEPCRequest(in);
        IOTA_ServiceStub.TraceEPCResponse respTrac;
        try {
            respTrac = iota_ServiceStub.traceEPC(traceEPCRequest);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
            return;
        }
        IOTA_ServiceStub.Event[] events = respTrac.getTraceEPCResponse().getEventList().getEvent();
        if (events != null) {
            for (IOTA_ServiceStub.Event e : events) {
                EPCISEvent evt = new EPCISEvent();
                evt.setAction(EPCISEvent.ActionType.valueOf(e.getAction().toString()));
                evt.setBizLoc(e.getBizLoc());
                evt.setBizStep(e.getBizStep());
                List<String> childs = new ArrayList<String>();
                if (e.getChildList().getChilds() != null) {
                    childs.addAll(Arrays.asList(e.getChildList().getChilds()));
                }
                evt.setChildren(childs);
                evt.setDisposition(e.getDisposition());
                List<String> epcs = new ArrayList<String>();
                if (e.getEpcList().getEpcs() != null) {
                    epcs.addAll(Arrays.asList(e.getEpcList().getEpcs()));
                }
                evt.setEpcs(epcs);
                evt.setEventTime(e.getRecordTime());
                evt.setInsertedTime(e.getEventTime());
                evt.setParentID(e.getParentId());
                evt.setQuantity(e.getQuantity() + "");
                evt.setReadPoint(e.getReadPoint());
                evt.setType(EPCISEvent.EventType.valueOf(e.getType().toString()));
                try {
                    callBackHandler.pushEvent(sessionID, evt);
                } catch (RemoteException ex) {
                    log.fatal(null, ex);
                }
            }
        } else {
            log.trace("(no events)");
        }
        log.trace("Done");
    }
}
