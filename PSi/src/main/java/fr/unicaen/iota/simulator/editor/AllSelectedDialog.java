/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.editor;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * AllSelectedDialog
 *
 * Show a non modal Dialog that asks user if all objects selected.
 *
 * Copied from de.huberlin.informatik.pnk.editor.AllSelectedDialog
 */
class AllSelectedDialog extends JDialog {

    private Editor editor = null;

    /**
     * Opens a nonmodal Dialog, That requests the user to press the ok button, after selecting
     * all interesting objects.
     */
    protected AllSelectedDialog(Editor editor) {
        super();
        this.editor = editor;

        // create the dialog and add the ActionListenerImplementation
        this.init();

        Dimension screenSize = this.getToolkit().getScreenSize();
        Point location = new Point();
        location.x = (screenSize.width >> 1);
        location.y = (screenSize.height >> 1);
        int dx = (this.getWidth() >> 1);
        int dy = (this.getHeight() >> 1);
        location.translate(-dx, -dy);
        this.setLocation(location);
    }

    private void init() {
        Container container = this.getContentPane();
        container.setLayout(new BorderLayout());
        JLabel label = new JLabel("All objects selected.");
        container.add(label, BorderLayout.CENTER);
        JPanel bottomPanel = new JPanel();
        JButton okButton = new JButton("OK");
        bottomPanel.add(okButton);
        container.add(bottomPanel, BorderLayout.SOUTH);
        this.pack();

        okButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                //###
                //### the setSelectedNetobject() Method of
                //### editor makes a notify with the correct monitor
                //### of the editor
                //###
                editor.setSelectedNetobject(null);
                // close the dialog
                dispose();
            }
        });
    }
} // AllSelectedDialog
