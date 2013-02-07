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
package fr.unicaen.iota.validator.gui;

import fr.unicaen.iota.application.model.DSEvent;
import fr.unicaen.iota.application.rmi.AccessInterface;
import fr.unicaen.iota.validator.AnalyserResult;
import fr.unicaen.iota.validator.Configuration;
import fr.unicaen.iota.validator.IOTA;
import fr.unicaen.iota.validator.ResultRaw;
import fr.unicaen.iota.validator.model.AggregationEvent;
import fr.unicaen.iota.validator.model.BaseEvent;
import fr.unicaen.iota.validator.model.EPC;
import fr.unicaen.iota.validator.model.ObjectEvent;
import fr.unicaen.iota.validator.operations.DSEntryComparator;
import fr.unicaen.iota.validator.operations.EPCISEntryComparator;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class DetailsDialog extends JDialog {

    private static final Log log = LogFactory.getLog(DetailsDialog.class);
    private EPC container;
    private GUI gui;
    private boolean showEPCISResults = Configuration.ANALYSE_EPCIS_EVENTS;
    private boolean showDStoEPCISResults = Configuration.ANALYSE_EPCIS_TO_DS_EVENTS;
    private boolean showDStoDSResults = Configuration.ANALYSE_DS_TO_DS_EVENTS;
    private int rowId;
    private IOTA iota;

    /**
     * Creates new form DetailsDialog1
     */
    public DetailsDialog(java.awt.Frame parent, boolean modal, String epc, int rowId, IOTA iota) {
        super(parent, modal);
        this.setTitle("Details of the corresponding object");
        initComponents();
        this.gui = (GUI) parent;
        this.iota = iota;
        this.rowId = rowId;
        this.container = gui.getAnalyserResult().get(epc).getContainer(epc);
        jLabel3.setText(container.getEpc());
        jLabel5.setText(Integer.toString(gui.getAnalyserResult().getErrorCount(epc)));
        createTables();
        this.setLocationRelativeTo(parent);
        try {
            createReport(gui.getAnalyserResult());
        } catch (Exception ex) {
            log.error(null, ex);
        }
    }

    private void createReport(AnalyserResult analyserResult) throws Exception {
        ResultRaw resultRaw = analyserResult.get(container.getEpc());
        if (showEPCISResults) {
            ((DefaultTableModel) (jTable1.getModel())).getDataVector().clear();
            int i = 0;
            List<BaseEvent> eventNotFound = resultRaw.getEpcisEvents();
            for (BaseEvent baseEvent : container.getEventList()) {
                i++;
                DefaultTableModel model = (DefaultTableModel) (jTable1.getModel());
                if (baseEvent instanceof ObjectEvent) {
                    ObjectEvent objectEvent = (ObjectEvent) baseEvent;
                    model.addRow(new String[]{i + "", objectEvent.getBizStep(), objectEvent.getDisposition(), objectEvent.getReadPoint(), objectEvent.getAction().name(), "x", eventNotFound.contains(baseEvent) ? "MISSING" : "FOUND"});
                }
                if (baseEvent instanceof AggregationEvent) {
                    AggregationEvent aggregationEvent = (AggregationEvent) baseEvent;
                    model.addRow(new String[]{i + "", aggregationEvent.getBizStep(), aggregationEvent.getDisposition(), aggregationEvent.getReadPoint(), aggregationEvent.getAction().name(), aggregationEvent.getParentId(), eventNotFound.contains(baseEvent) ? "MISSING" : "FOUND"});
                }
            }
        }
        if (showDStoEPCISResults) {
            ((DefaultTableModel) (jTable2.getModel())).getDataVector().clear();
            int i = 0;
            List<DSEvent> eventNotFound = resultRaw.getDsEvents();
            for (DSEvent dsEvent : container.getDsToEPCISReferentList(resultRaw.getContainerList())) {
                i++;
                DefaultTableModel model = (DefaultTableModel) (jTable2.getModel());
                model.addRow(new String[]{i + "", dsEvent.getBizStep(), dsEvent.getReferenceAddress(), eventNotFound.contains(dsEvent) ? "MISSING" : "FOUND"});
            }
        }
        if (showDStoDSResults) {
            ((DefaultTableModel) (jTable3.getModel())).getDataVector().clear();
            int i = 0;
            List<DSEvent> eventNotFound = resultRaw.getDsToDsEvents();
            for (DSEvent dsEvent : container.getDsToDsReferentList()) {
                i++;
                DefaultTableModel model = (DefaultTableModel) (jTable3.getModel());
                model.addRow(new String[]{i + "", dsEvent.getBizStep(), dsEvent.getReferenceAddress(), eventNotFound.contains(dsEvent) ? "MISSING" : "FOUND"});
            }
        }
    }

    private void createTables() {
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "BIZ-STEP", "DISPOSITION", "READ-POINT", "ACTION", "PARENT-ID", "REPORT"}));
        jTable1.getColumnModel().getColumn(0).setCellRenderer(new EPCISEventCellRenderer());
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new EPCISEventCellRenderer());
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new EPCISEventCellRenderer());
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new EPCISEventCellRenderer());
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new EPCISEventCellRenderer());
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new EPCISEventCellRenderer());
        jTable1.getColumnModel().getColumn(6).setCellRenderer(new EPCISEventCellRenderer());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "BIZ-STEP", "REFERENCE-ADDRESS", "REPORT"}));
        jTable2.getColumnModel().getColumn(0).setCellRenderer(new DsEventCellRenderer());
        jTable2.getColumnModel().getColumn(1).setCellRenderer(new DsEventCellRenderer());
        jTable2.getColumnModel().getColumn(2).setCellRenderer(new DsEventCellRenderer());
        jTable2.getColumnModel().getColumn(3).setCellRenderer(new DsEventCellRenderer());

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "BIZ-STEP", "REFERENCE-ADDRESS", "REPORT"}));
        jTable3.getColumnModel().getColumn(0).setCellRenderer(new DsEventCellRenderer());
        jTable3.getColumnModel().getColumn(1).setCellRenderer(new DsEventCellRenderer());
        jTable3.getColumnModel().getColumn(2).setCellRenderer(new DsEventCellRenderer());
        jTable3.getColumnModel().getColumn(3).setCellRenderer(new DsEventCellRenderer());

        TableColumn column1 = jTable1.getColumnModel().getColumn(0);
        column1.setPreferredWidth(30);
        column1.setMaxWidth(30);

        TableColumn column2 = jTable2.getColumnModel().getColumn(0);
        column2.setPreferredWidth(30);
        column2.setMaxWidth(30);

        TableColumn column3 = jTable3.getColumnModel().getColumn(0);
        column3.setPreferredWidth(30);
        column3.setMaxWidth(30);


        TableColumn column4 = jTable1.getColumnModel().getColumn(6);
        column4.setPreferredWidth(70);
        column4.setMaxWidth(70);

        TableColumn column5 = jTable2.getColumnModel().getColumn(3);
        column5.setPreferredWidth(70);
        column5.setMaxWidth(70);

        TableColumn column6 = jTable3.getColumnModel().getColumn(3);
        column6.setPreferredWidth(70);
        column6.setMaxWidth(70);

        TableColumn column7 = jTable3.getColumnModel().getColumn(1);
        column7.setPreferredWidth(100);
        column7.setMaxWidth(400);

        TableColumn column8 = jTable2.getColumnModel().getColumn(1);
        column8.setPreferredWidth(130);
        column8.setMaxWidth(400);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton3 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jButton4 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();

        setBackground(new java.awt.Color(239, 235, 231));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                closeDialog(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Results of the analyse "));

        jLabel2.setText("Electronic Product Code:");

        jLabel3.setForeground(new java.awt.Color(3, 0, 255));
        jLabel3.setText("urn:epc:id:sgtin:x.y.id");

        jLabel4.setText("missing events:");

        jLabel5.setForeground(new java.awt.Color(255, 0, 3));
        jLabel5.setText("0");

        jButton6.setText("Actualize analysis");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 399, Short.MAX_VALUE)
                        .addComponent(jButton6)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jButton6))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(5, 5, 5)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        add(jPanel1, java.awt.BorderLayout.NORTH);

        jPanel2.setPreferredSize(new java.awt.Dimension(678, 56));

        jLabel6.setBackground(new java.awt.Color(0, 165, 10));
        jLabel6.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        jLabel6.setText(" ");
        jLabel6.setOpaque(true);

        jLabel7.setText("events found");

        jLabel8.setBackground(new java.awt.Color(255, 0, 3));
        jLabel8.setFont(new java.awt.Font("DejaVu Sans", 0, 10)); // NOI18N
        jLabel8.setText(" ");
        jLabel8.setOpaque(true);

        jLabel9.setText("missing events");

        jButton5.setText("Start new complete evaluation");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(40, 40, 40)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addGap(63, 63, 63)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 86, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton5)
                    .addComponent(jLabel6)
                    .addComponent(jLabel7)
                    .addComponent(jLabel9)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        add(jPanel2, java.awt.BorderLayout.SOUTH);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton2.setText("Start new EPCIS evaluation");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton9.setText("clear");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2)
                    .addComponent(jButton9))
                .addContainerGap())
        );

        jTabbedPane1.addTab("EPCIS events ", jPanel5);

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jButton3.setText("Start new DS to EPCIS evaluation");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton8.setText("clear");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton8))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DS events indexing EPCIS ", jPanel6);

        jButton4.setText("Start new DS to DS evaluation");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jButton7.setText("clear");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 646, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4)))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4)
                    .addComponent(jButton7))
                .addContainerGap())
        );

        jTabbedPane1.addTab("DS events indexing DS ", jPanel7);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
        );

        add(jPanel3, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Closes the dialog
     */
    private void closeDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeDialog
        setVisible(false);
        dispose();
    }//GEN-LAST:event_closeDialog

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        closeDialog(null);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            AccessInterface server = (AccessInterface) Naming.lookup(Configuration.RMI_SERVER_URL);
            EPCISEntryComparator epcisComparator = new EPCISEntryComparator(gui.getIdentity(), server, iota);
            List<EPC> list = new ArrayList<EPC>();
            list.add(container);
            Map<EPC, List<BaseEvent>> result = epcisComparator.getEventNotVerified(list);
            AnalyserResult analyserResult = gui.getAnalyserResult();

            int lastResult = analyserResult.get(container.getEpc()).getDsToDsEvents().size();
            int newResult = 0;

            for (EPC cont : result.keySet()) {
                analyserResult.updateRawForEPCIS(cont, result.get(cont));
                newResult += result.get(cont).size();
            }
            showEPCISResults = true;
            createReport(analyserResult);

            jLabel5.setText((Integer.parseInt(jLabel5.getText()) - (lastResult - newResult)) + "");
        } catch (NotBoundException ex) {
            log.fatal(null, ex);
        } catch (MalformedURLException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        } catch (Exception ex) {
            log.fatal(null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            AccessInterface server = (AccessInterface) Naming.lookup(Configuration.RMI_SERVER_URL);
            DSEntryComparator dSEntryComparator = new DSEntryComparator(gui.getIdentity(), server, gui.getIota());
            Map<EPC, List<DSEvent>> result = dSEntryComparator.getEventNotVerified(container, gui.getAnalyserResult().get(container.getEpc()).getContainerList());
            AnalyserResult analyserResult = gui.getAnalyserResult();

            int lastResult = analyserResult.get(container.getEpc()).getDsToDsEvents().size();
            int newResult = 0;

            for (EPC cont : result.keySet()) {
                analyserResult.updateRawForDStoEPCIS(cont, result.get(cont));
                newResult += result.get(cont).size();
            }
            showDStoEPCISResults = true;
            createReport(analyserResult);

            jLabel5.setText((Integer.parseInt(jLabel5.getText()) - (lastResult - newResult)) + "");
        } catch (NotBoundException ex) {
            log.fatal(null, ex);
        } catch (MalformedURLException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        } catch (Exception ex) {
            log.fatal(null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        try {
            AccessInterface server = (AccessInterface) Naming.lookup(Configuration.RMI_SERVER_URL);
            DSEntryComparator dSEntryComparator = new DSEntryComparator(gui.getIdentity(), server, gui.getIota());
            List<EPC> list = new ArrayList<EPC>();
            list.add(container);
            Map<EPC, List<DSEvent>> result = dSEntryComparator.verifyDSToDSReferences(list);
            AnalyserResult analyserResult = gui.getAnalyserResult();

            int lastResult = analyserResult.get(container.getEpc()).getDsToDsEvents().size();
            int newResult = 0;

            for (EPC cont : result.keySet()) {
                analyserResult.updateRawForDStoDS(cont, result.get(cont));
                newResult += result.get(cont).size();
            }
            showDStoDSResults = true;
            createReport(analyserResult);

            jLabel5.setText((Integer.parseInt(jLabel5.getText()) - (lastResult - newResult)) + "");
        } catch (NotBoundException ex) {
            log.fatal(null, ex);
        } catch (MalformedURLException ex) {
            log.fatal(null, ex);
        } catch (RemoteException ex) {
            log.fatal(null, ex);
        } catch (Exception ex) {
            log.fatal(null, ex);
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        jButton2ActionPerformed(evt);
        jButton3ActionPerformed(evt);
        jButton4ActionPerformed(evt);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        gui.updateRow(rowId, gui.getAnalyserResult().get(container.getEpc()));
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        ((DefaultTableModel) (jTable3.getModel())).getDataVector().clear();
        jTable3.updateUI();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        ((DefaultTableModel) (jTable1.getModel())).getDataVector().clear();
        jTable3.updateUI();
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        ((DefaultTableModel) (jTable1.getModel())).getDataVector().clear();
        jTable2.updateUI();
    }//GEN-LAST:event_jButton8ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    // End of variables declaration//GEN-END:variables
}
