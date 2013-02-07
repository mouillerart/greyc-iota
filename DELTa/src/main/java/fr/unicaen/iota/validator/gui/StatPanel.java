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

import fr.unicaen.iota.validator.operations.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class StatPanel extends javax.swing.JPanel implements ActionListener {

    private File fileToAnalyse;
    private JPanel contentPane = new JPanel();
    private JFrame parent;
    private static final Log log = LogFactory.getLog(StatPanel.class);

    StatPanel(File file, JFrame parent) {
        this.fileToAnalyse = file;
        initComponents();
        jButton1.addActionListener(this);
        getjButton2().setEnabled(false);
        contentPane.setLayout(new BorderLayout());
        jScrollPane1.add(contentPane);
        jScrollPane1.setViewportView(contentPane);
        contentPane.setBackground(Color.WHITE);
        this.parent = parent;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jProgressBar1 = new javax.swing.JProgressBar();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jPanel1.setLayout(new java.awt.BorderLayout());

        jButton1.setText("analyse files");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1, java.awt.BorderLayout.LINE_END);

        jButton2.setText("clear");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton2, java.awt.BorderLayout.LINE_START);

        add(jPanel1, java.awt.BorderLayout.PAGE_END);

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        jPanel2.setLayout(new java.awt.BorderLayout());

        jLabel1.setText("ananlysis progress: ");
        jPanel2.add(jLabel1, java.awt.BorderLayout.LINE_START);
        jPanel2.add(jProgressBar1, java.awt.BorderLayout.CENTER);

        jLabel2.setText("           0%");
        jPanel2.add(jLabel2, java.awt.BorderLayout.LINE_END);

        add(jPanel2, java.awt.BorderLayout.PAGE_START);
        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jProgressBar1.setValue(0);
        jLabel2.setText(" 0%");
        getContentPane().removeAll();
        jButton1.setEnabled(true);
        getjButton2().setEnabled(false);
        getContentPane().updateUI();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    }//GEN-LAST:event_jButton1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == getjButton1()) {
            log.trace("analysing " + getFileToAnalyse().getAbsolutePath());
            if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Are you shure to analyse this file (size: " + getFileToAnalyse().length() + ") ?")) {
                return;
            }
            List<List<String>> l = analyseFiles();
            ChartDialog dialog = new ChartDialog(parent, true, l.get(0));
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            if (dialog.getResponseCode() != ChartDialog.YES_OPTION) {
                return;
            }

            ChartPreferences chartPreferences = null;

            switch (dialog.getChartType()) {
                case XY_CHART:
                    chartPreferences = new XYChartPreferences(dialog.getXYParam(), dialog.getXYThreshold());
                    break;
                case BOX_AND_WISKER:
                    chartPreferences = new BoxChartPreferences();
                    break;
                case BAR_CHART:
                    chartPreferences = new BarChartPreferences(dialog.getIntervals());
                    break;
            }
            new ChartBuilder(this, l, chartPreferences).start();
        }
    }

    public List<List<String>> analyseFiles() {
        List<List<String>> result = new ArrayList<List<String>>();
        List<String> list = new ArrayList<String>();
        if (!getFileToAnalyse().getName().equals("all")) {
            result.add(readFile(getFileToAnalyse()));
        } else {
            File[] fileList = getFileToAnalyse().getAbsoluteFile().getAbsoluteFile().getParentFile().listFiles();
            for (File f : fileList) {
                if (f.getName().equals("all")) {
                    continue;
                }
                result.add(readFile(f));
            }
        }
        return result;
    }

    public List<String> readFile(File f) {
        List<String> list = new ArrayList<String>();
        try {
            FileInputStream fstream = new FileInputStream(f);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                String[] tab = strLine.split(" ");
                for (int i = 0; i < tab.length - 1; i++) {
                    try {
                        Integer.parseInt(tab[i]);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    list.add(tab[i]);
                }
            }
            in.close();
        } catch (Exception e) {
            log.error("Error", e);
        }
        return list;
    }

    /**
     * @return the fileToAnalyse
     */
    public File getFileToAnalyse() {
        return fileToAnalyse;
    }

    /**
     * @return the jButton1
     */
    public javax.swing.JButton getjButton1() {
        return jButton1;
    }

    /**
     * @return the jLabel2
     */
    public javax.swing.JLabel getjLabel2() {
        return jLabel2;
    }

    /**
     * @return the jProgressBar1
     */
    public javax.swing.JProgressBar getjProgressBar1() {
        return jProgressBar1;
    }

    /**
     * @return the jButton2
     */
    public javax.swing.JButton getjButton2() {
        return jButton2;
    }

    /**
     * @return the contentPane
     */
    public JPanel getContentPane() {
        return contentPane;
    }
}
