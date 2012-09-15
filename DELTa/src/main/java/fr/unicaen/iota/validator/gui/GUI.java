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
import fr.unicaen.iota.validator.*;
import fr.unicaen.iota.validator.listener.AnalyserListener;
import fr.unicaen.iota.validator.model.BaseEvent;
import fr.unicaen.iota.validator.model.EPC;
import fr.unicaen.iota.validator.operations.LogAnalyser;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class GUI extends javax.swing.JFrame implements AnalyserListener, WindowListener {

    private static final Log log = LogFactory.getLog(GUI.class);
    private int nbFiles;
    private Controler controler;
    private int tableId = 0;
    private IOTA iota;
    private AnalyserResult analyserResult;
    private JPanel tree;
    private JTabbedPane jTabbedPane;

    /**
     * Creates new form GUI
     */
    public GUI() {
        initComponents();
        this.setTitle("DELTa");
        nbFiles = new File(Configuration.XML_EVENT_FOLDER).list().length;
        jLabel6.setText(nbFiles + "");
    }

    public GUI(Controler controleur, IOTA iota) {
        initComponents();
        this.setTitle("DELTa");
        this.iota = iota;
        nbFiles = new File(Configuration.XML_EVENT_FOLDER).list().length;
        jLabel6.setText(nbFiles + "");
        this.controler = controleur;
        initAnalyserRestricions();
        createTable();
        createTabbedPane();
        createTree();
        this.setSize(1000, 800);
        jSplitPane1.setDividerLocation(280);
        jSplitPane3.setDividerLocation(500);
        this.setLocationRelativeTo(null);
        this.addWindowListener(this);

    }

    public void setResults(AnalyserResult analyserResult) {
        this.analyserResult = analyserResult;
    }

    public void updateRow(int rowId, ResultRaw res) {
        int size = ((DefaultTableModel) jTable1.getModel()).getRowCount();
        Vector vector = ((DefaultTableModel) jTable1.getModel()).getDataVector();
        for (int i = 0; i < size; i++) {
            Vector row = (Vector) vector.get(i);
            if (Integer.parseInt((String) row.get(0)) != rowId) {
                continue;
            }

            int epcisValue = "unchecked".equals(row.get(2)) ? 0 : Integer.parseInt((String) row.get(2));
            int dsValue = "unchecked".equals(row.get(3)) ? 0 : Integer.parseInt((String) row.get(3));
            int dsToDsValue = "unchecked".equals(row.get(4)) ? 0 : Integer.parseInt((String) row.get(4));

            int lastErrorCountReport = epcisValue + dsToDsValue + dsValue;

            int epcisResultSize = 0;
            if (res.getEpcisEvents() != null) {
                epcisResultSize = res.getEpcisEvents().size();
            }
            int dsResultSize = 0;
            if (res.getDsEvents() != null) {
                dsResultSize = res.getDsEvents().size();
            }
            int dsToDsResultSize = 0;
            if (res.getDsToDsEvents() != null) {
                dsToDsResultSize = res.getDsToDsEvents().size();
            }
            row.set(2, res.getEpcisEvents() != null ? epcisResultSize + "" : "unchecked");
            row.set(3, res.getDsEvents() != null ? dsResultSize + "" : "unchecked");
            row.set(4, res.getDsToDsEvents() != null ? dsToDsResultSize + "" : "unchecked");
            row.set(5, epcisResultSize + dsResultSize + dsToDsResultSize == 0 ? "SUCCESS" : "FAIL");
            int uncheckedEvents = epcisResultSize + dsResultSize + dsToDsResultSize;
            int diff = lastErrorCountReport - uncheckedEvents;
            if (diff > 0) {
                jLabel10.setText((Integer.parseInt(jLabel10.getText()) - diff) + "");
                if (uncheckedEvents == 0) {
                    jLabel8.setText((Integer.parseInt(jLabel8.getText()) - 1) + "");
                }
            }
            jTable1.updateUI();
        }
    }

    private void initAnalyserRestricions() {
        jCheckBox1.setSelected(Configuration.ANALYSE_EPCIS_EVENTS);
        jCheckBox2.setSelected(Configuration.ANALYSE_EPCIS_TO_DS_EVENTS);
        jCheckBox3.setSelected(Configuration.ANALYSE_DS_TO_DS_EVENTS);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel8 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setPreferredSize(new java.awt.Dimension(112, 140));

        jPanel8.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel8.setMaximumSize(new java.awt.Dimension(800, 800));
        jPanel8.setLayout(new java.awt.BorderLayout());

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel4.setPreferredSize(new java.awt.Dimension(3120, 26));
        jPanel4.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("analysis progress: ");
        jPanel4.add(jLabel1, java.awt.BorderLayout.LINE_START);
        jPanel4.add(jProgressBar1, java.awt.BorderLayout.CENTER);

        jLabel2.setText("         0%");
        jPanel4.add(jLabel2, java.awt.BorderLayout.LINE_END);

        jPanel8.add(jPanel4, java.awt.BorderLayout.PAGE_START);

        jPanel6.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel6.setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Analyser restrictions"));

        jCheckBox1.setText("Analyse EPCIS events");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("Analyse DS to EPCIS events");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("Analyse DS to DS events");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jLabel12.setText("object correctly tracked");

        jLabel11.setBackground(new java.awt.Color(0, 165, 10));
        jLabel11.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        jLabel11.setText(" ");
        jLabel11.setOpaque(true);

        jLabel13.setBackground(new java.awt.Color(255, 0, 3));
        jLabel13.setFont(new java.awt.Font("DejaVu Sans", 0, 10));
        jLabel13.setText(" ");
        jLabel13.setOpaque(true);

        jLabel14.setText("some events are missing");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jCheckBox1)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox2)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 12, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel14)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jLabel12))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jCheckBox1)
                            .addComponent(jCheckBox2)
                            .addComponent(jCheckBox3))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(jPanel1, java.awt.BorderLayout.PAGE_START);

        jPanel7.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jLabel5.setText("files in queue:");
        jPanel7.add(jLabel5);

        jLabel6.setText("0");
        jPanel7.add(jLabel6);

        jLabel3.setText("analysed files: ");
        jPanel7.add(jLabel3);

        jLabel4.setText("0");
        jPanel7.add(jLabel4);

        jLabel7.setText("errors:");
        jPanel7.add(jLabel7);

        jLabel8.setForeground(new java.awt.Color(255, 0, 0));
        jLabel8.setText("0");
        jPanel7.add(jLabel8);

        jLabel9.setText("missing events: ");
        jPanel7.add(jLabel9);

        jLabel10.setForeground(new java.awt.Color(255, 0, 0));
        jLabel10.setText("0");
        jPanel7.add(jLabel10);

        jButton2.setText("Analyse events");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton2);

        jButton5.setText("Suspend Analyse");
        jButton5.setEnabled(false);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton5);

        jButton3.setText("load");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton3);

        jButton4.setText("clear repository");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton4);

        jButton1.setText("Close");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel7.add(jButton1);

        jPanel6.add(jPanel7, java.awt.BorderLayout.PAGE_END);

        jPanel8.add(jPanel6, java.awt.BorderLayout.PAGE_END);

        jPanel5.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel5.setLayout(new java.awt.BorderLayout());

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

        jPanel5.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jPanel8.add(jPanel5, java.awt.BorderLayout.CENTER);

        jSplitPane3.setBottomComponent(jPanel8);

        jPanel2.setMinimumSize(new java.awt.Dimension(0, 100));
        jPanel2.setPreferredSize(new java.awt.Dimension(956, 200));
        jPanel2.setLayout(new java.awt.BorderLayout());
        jPanel2.add(jSplitPane1, java.awt.BorderLayout.CENTER);

        jSplitPane3.setLeftComponent(jPanel2);

        getContentPane().add(jSplitPane3, java.awt.BorderLayout.CENTER);

        jMenu1.setText("File");

        jMenuItem2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pics/Chart.png"))); // NOI18N
        jMenuItem2.setText("Servers Stats");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/pics/preferences.png"))); // NOI18N
        jMenuItem1.setText("Network preferences");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if (controler.isAlive()) {
            controler.resumeAnalyse();
        } else {
            controler.start();
        }

        jButton2.setEnabled(false);
        jButton5.setEnabled(true);
        jButton4.setEnabled(false);
        jButton3.setEnabled(false);
        jButton1.setEnabled(false);

    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        LogAnalyser logAnalyser = new LogAnalyser(controler.getAnalyserStatus());
        try {
            logAnalyser.load();
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        int i = JOptionPane.showConfirmDialog(this, "You are about to delete all files in the repository.", "Do you want to delete?", JOptionPane.YES_NO_OPTION);
        if (i != JOptionPane.YES_OPTION) {
            log.debug("Deletion aborted.");
            return;

        }
        for (File f : new File(Configuration.LOG_DIRECTORY).listFiles()) {
            f.delete();
        }
        for (File f : new File(Configuration.UNVERIFIED_DIRECTORY).listFiles()) {
            f.delete();
        }
        for (File f : new File(Configuration.VERIFIED_DIRECTORY).listFiles()) {
            f.delete();
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        Configuration.ANALYSE_EPCIS_EVENTS = !Configuration.ANALYSE_EPCIS_EVENTS;
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
        Configuration.ANALYSE_EPCIS_TO_DS_EVENTS = !Configuration.ANALYSE_EPCIS_TO_DS_EVENTS;
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
        Configuration.ANALYSE_DS_TO_DS_EVENTS = !Configuration.ANALYSE_DS_TO_DS_EVENTS;
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        controler.suspendAnalyse();
        jButton2.setEnabled(true);
        jButton5.setEnabled(false);
        jButton4.setEnabled(true);
        jButton3.setEnabled(true);
        jButton1.setEnabled(true);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        new Preferences(this, true, iota).setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        new Stats(iota, this).setVisible(true);
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void createTable() {
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "EPCs", "EPCIS LINK", "EPCIS-DS LINK", "DS-DS LINK", "REPORT", ""
                }));
        jTable1.setAutoCreateRowSorter(true);
        TableColumn column2 = jTable1.getColumnModel().getColumn(1);
        column2.setMaxWidth(400);
        column2.setPreferredWidth(400);
        TableColumn column1 = jTable1.getColumnModel().getColumn(0);
        column1.setPreferredWidth(48);
        column1.setMaxWidth(48);
        TableColumn column3 = jTable1.getColumnModel().getColumn(6);
        column3.setCellEditor(new CellDetailsEditor(this, iota));
        column3.setMaxWidth(20);
        column3.setPreferredWidth(20);
        column3.setCellRenderer(new DetailsCellRenderer("resources/pics/play.png"));

        jTable1.getColumnModel().getColumn(0).setCellRenderer(new IdCellRenderer());
        jTable1.getColumnModel().getColumn(1).setCellRenderer(new ReportCellRenderer());
        jTable1.getColumnModel().getColumn(2).setCellRenderer(new ReportCellRenderer());
        jTable1.getColumnModel().getColumn(3).setCellRenderer(new ReportCellRenderer());
        jTable1.getColumnModel().getColumn(4).setCellRenderer(new ReportCellRenderer());
        jTable1.getColumnModel().getColumn(5).setCellRenderer(new ReportCellRenderer());
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void analysedObject() {
        int nbAnalysedFiles = Integer.parseInt(jLabel4.getText()) + 1;
        jLabel4.setText(nbAnalysedFiles + "");
        int progress = (int) (100 * nbAnalysedFiles / nbFiles);
        jProgressBar1.setValue(progress);
        jLabel2.setText(progress + "%");
        jLabel6.setText(new File(Configuration.XML_EVENT_FOLDER).list().length + "");
    }

    @Override
    public void errorFound() {
        jLabel8.setText((Integer.parseInt(jLabel8.getText()) + 1) + "");
    }

    public void missingEvent(int i) {
        jLabel10.setText((Integer.parseInt(jLabel10.getText()) + i) + "");
    }

    @Override
    public synchronized void publishResults(List<EPC> epcList, Map<EPC, List<BaseEvent>> epcisResults, Map<EPC, List<DSEvent>> dsResults, Map<EPC, List<DSEvent>> dsToDsResults) {
        try {
            for (EPC container : epcList) {
                DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
                int epcisResultSize = 0;
                if (Configuration.ANALYSE_EPCIS_EVENTS) {
                    epcisResultSize = epcisResults.get(container).size();
                }

                int dsResultSize = 0;
                if (Configuration.ANALYSE_EPCIS_TO_DS_EVENTS) {
                    dsResultSize = dsResults.get(container).size();
                }

                int dsToDsResultSize = 0;
                if (Configuration.ANALYSE_DS_TO_DS_EVENTS) {
                    dsToDsResultSize = dsToDsResults.get(container).size();
                }

                tableId++;
                model.insertRow(0, new String[]{tableId + "",
                            container.getEpc(),
                            Configuration.ANALYSE_EPCIS_EVENTS ? epcisResultSize + "" : "unchecked",
                            Configuration.ANALYSE_EPCIS_TO_DS_EVENTS ? dsResultSize + "" : "unchecked",
                            Configuration.ANALYSE_DS_TO_DS_EVENTS ? dsToDsResultSize + "" : "unchecked",
                            epcisResultSize + dsResultSize + dsToDsResultSize == 0 ? "SUCCESS" : "FAIL"});
                int uncheckedEvents = epcisResultSize + dsResultSize + dsToDsResultSize;
                if (uncheckedEvents > 0) {
                    missingEvent(uncheckedEvents);
                }

            }
        } catch (Exception e) {
            log.error(null, e);
        }

    }

    @Override
    public void logFileAnalysed(Map<String, Integer> epcisResults, Map<String, Integer> dsResults, Map<String, Integer> dsToDsResults) {
        for (String container : epcisResults.keySet()) {
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            int epcisResultSize = epcisResults.get(container);
            int dsResultSize = dsResults.get(container);
            int dsToDsResultSize = dsToDsResults.get(container);
            tableId++;

            model.addRow(new String[]{tableId + "", container, epcisResultSize + "", dsResultSize + "", dsToDsResultSize + "", epcisResultSize + dsResultSize + dsToDsResultSize == 0 ? "SUCCESS" : "FAIL"});
            int uncheckedEvents = epcisResultSize + dsResultSize + dsToDsResultSize;
            if (uncheckedEvents > 0) {
                missingEvent(uncheckedEvents);
            }
        }
    }

    /**
     * @return the iota
     */
    public IOTA getIota() {
        return iota;
    }

    /**
     * @param iota the iota to set
     */
    public void setIota(IOTA iota) {
        this.iota = iota;
    }

    /**
     * @return the analyserResult
     */
    public AnalyserResult getAnalyserResult() {
        return analyserResult;
    }

    private void createTree() {
        tree = new FileTree(new File(Configuration.STATS_FOLDER), jTabbedPane, this);
        jSplitPane1.setLeftComponent(tree);
        jSplitPane1.setRightComponent(jTabbedPane);
    }

    private void createTabbedPane() {
        jTabbedPane = new JTabbedPane();
        jTabbedPane.addTab("Home", new JPanel());
    }

    @Override
    public void windowOpened(WindowEvent e) {
    }

    @Override
    public void windowClosing(WindowEvent e) {
        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Do you want to quit the program?")) {
            return;
        }
        System.exit(0);
    }

    @Override
    public void windowClosed(WindowEvent e) {
    }

    @Override
    public void windowIconified(WindowEvent e) {
    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {
    }

    @Override
    public void windowDeactivated(WindowEvent e) {
    }
}
