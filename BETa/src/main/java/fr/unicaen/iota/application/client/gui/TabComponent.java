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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 */
public class TabComponent extends JPanel implements ActionListener {

    private JLabel title;
    private JButton closeButton;
    private ClosableTabbedPane parent;

    public TabComponent(String label, ClosableTabbedPane parent) {
        this.parent = parent;
        this.setLayout(new BorderLayout());
        title = new JLabel(label);
        title.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 10));
        this.add(title, BorderLayout.CENTER);
        closeButton = new JButton("c");
        closeButton.addActionListener(this);
        this.add(closeButton, BorderLayout.EAST);
        this.setOpaque(false);
    }

    void setTitle(String newTitle) {
        title.setText(newTitle);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == closeButton) {
            parent.closeTab(this);
        }
    }
}
