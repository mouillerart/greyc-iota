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

import fr.unicaen.iota.application.client.listener.EPCEventListener;
import fr.unicaen.iota.application.model.EPCISEvent;
import fr.unicaen.iota.application.util.TimeParser;
import fr.unicaen.iota.application.util.TravelTimeTuple;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

/**
 *
 */
public class SearchArea extends JPanel implements EPCEventListener {

    private JTable jTableGenerated = new JTable();
    private String epc;
    private String sessionId;
    private int currentIndex = 0;
    private ClosableTabbedPane jtp;
    private String title = "";
    private JLabel travelTime;
    private JLabel nbObjects;
    private JButton details;

    public SearchArea(String epc, String sessionId, ClosableTabbedPane jtp) {
        travelTime = new JLabel("0");
        nbObjects = new JLabel("0");
        this.sessionId = sessionId;
        this.jtp = jtp;
        this.epc = epc;
        this.setName(epc);
        JScrollPane jsp = new JScrollPane();
        JPanel statusbar = new JPanel();
        jTableGenerated.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "ID", "EPCs", "Event Type", "BizLoc", "BizStep", "Event Time"
                }));
        jTableGenerated.setAutoCreateRowSorter(true);
        TableColumn column2 = jTableGenerated.getColumnModel().getColumn(1);
        column2.setCellRenderer(new TextAreaRenderer());
        column2.setMaxWidth(400);
        column2.setPreferredWidth(400);
        TableColumn column1 = jTableGenerated.getColumnModel().getColumn(0);
        column1.setCellRenderer(new IdRenderer());
        column1.setPreferredWidth(27);
        column1.setMaxWidth(27);
        TableColumn column3 = jTableGenerated.getColumnModel().getColumn(2);
        column3.setPreferredWidth(90);
        column3.setMaxWidth(90);
        jsp.setViewportView(jTableGenerated);
        details = new JButton("details...");
        FlowLayout fl = new FlowLayout();
        fl.setAlignment(FlowLayout.RIGHT);
        statusbar.setLayout(fl);
        statusbar.add(new JLabel("used objects: "));
        statusbar.add(nbObjects);
        statusbar.add(new JLabel(" | "));
        statusbar.add(new JLabel("Complet travel time: "));
        statusbar.add(travelTime);
        statusbar.add(details);
        this.setLayout(new BorderLayout());
        this.add(jsp, BorderLayout.CENTER);
        this.add(statusbar, BorderLayout.SOUTH);
        jTableGenerated.setSelectionBackground(Color.WHITE);
        jTableGenerated.setForeground(Color.BLACK);
        jTableGenerated.setSelectionForeground(Color.RED);
    }

    /**
     * @return the jTableGenerated
     */
    public JTable getJTableGenerated() {
        return jTableGenerated;
    }

    /**
     * @param jTableGenerated the jTableGenerated to set
     */
    public void setJTableGenerated(JTable jTableGenerated) {
        this.jTableGenerated = jTableGenerated;
    }

    /**
     * @return the title
     */
    public String getEPC() {
        return epc;
    }

    /**
     * @param title the title to set
     */
    public void setEPC(String epc) {
        this.epc = epc;
    }

    @Override
    public void usedObjectsChanged(String session, int objects) {
        if (!session.equals(this.sessionId)) {
            return;
        }
        nbObjects.setText(objects + "");
    }

    @Override
    public void travelTimeChanged(String session, TravelTimeTuple ttt) {
        if (!session.equals(this.sessionId)) {
            return;
        }
        int d = (int) (ttt.getTravelTime() / (3600000 * 24));
        int h = (int) (ttt.getTravelTime() / (3600000)) - d * 8640;
        int m = (int) (ttt.getTravelTime() / (60000)) - h * 3600 - d * 8640;
        int s = (int) (ttt.getTravelTime() / (1000)) - m * 60 - h * 3600 - d * 8640;

        travelTime.setText(d + " D " + h + " H " + m + " M " + s + " s ");
    }

    @Override
    public void eventReveived(String session, EPCISEvent e) {
        if (!session.equals(this.sessionId)) {
            return;
        }
        currentIndex++;
        ((DefaultTableModel) getJTableGenerated().getModel()).addRow(new String[]{currentIndex + "", formatEPC(e), e.getType().toString(), e.getBizLoc(), e.getBizStep(), TimeParser.format(e.getEventTime())});
    }

    public void setTerminated() {
        String newTitle = getEPC();
        setTitle(newTitle);
    }

    public void setProcessing() {
        String newTitle = getEPC();
        setTitle(newTitle);
    }

    private String formatEPC(EPCISEvent e) {
        List<String> res = new ArrayList<String>();
        List<String> l = e.getEpcs();
        for (String s : l) {
            res.add(" EPC: " + s + " \n");
        }
        if (!e.getParentID().equals("")) {
            res.add(" Parent ID: " + e.getParentID() + " \n");
        }
        l = e.getChildren();
        for (String s : l) {
            res.add("      | child: " + s + " \n");
        }
        StringBuilder str = new StringBuilder();
        for (String s : res) {
            str.append(s);
        }
        return str.toString();
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
        jtp.setTitleAt(this, title);
        jtp.setSelectedIndex(jtp.indexOfComponent(this));
    }
}
