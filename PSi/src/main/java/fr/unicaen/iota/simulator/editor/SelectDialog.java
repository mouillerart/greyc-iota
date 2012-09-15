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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *  Copied from de.huberlin.informatik.pnk.editor.SelectDialog
 */
public class SelectDialog extends Thread implements ActionListener {

    public static int SELECTDIALOG_OK = 0;
    public static int SELECTDIALOG_CANCEL = 1;
    private boolean finish = false;
    private JFrame jf;
    private int value = SELECTDIALOG_OK;

    /**
     * SelectDialog constructor comment.
     */
    public SelectDialog(String title, String text) {
        super();
        boolean withOK = false;
        initDialog(title, text, withOK);
    }

    /**
     * SelectDialog constructor comment.
     */
    public SelectDialog(String title, String text, boolean withOK) {
        super();
        initDialog(title, text, withOK);
    }

    /**
     * SelectDialog constructor comment.
     */
    @Override
    public void actionPerformed(ActionEvent ae) {
        //###Editor.msg("Action...");

        String cmd = ae.getActionCommand();
        if (cmd.equals("OK")) {
            value = SELECTDIALOG_OK;
        } else {
            value = SELECTDIALOG_CANCEL;
        }
        finish();
    }

    /**
     * SelectDialog constructor comment.
     */
    public void finish() {
        // Fenster zerst_ren!!!
        jf.dispose();
        finish = true;
    }

    /**
     * SelectDialog constructor comment.
     */
    public void initDialog(String title, String text, boolean withOK) {
        jf = new JFrame(title);
        JPanel jp = new JPanel(new BorderLayout());
        jf.getContentPane().add(jp);
        JLabel jl = new JLabel(text);
        jp.add(jl, BorderLayout.NORTH);
        JButton cb = new JButton("Cancel");
        jp.add(cb, BorderLayout.WEST);
        cb.addActionListener(this);
        if (withOK) {
            JButton ob = new JButton("OK");
            jp.add(ob, BorderLayout.EAST);
            ob.addActionListener(this);
        }
        jf.pack();
    }

    /**
     * SelectDialog constructor comment.
     */
    public void show() {
        // Fenster anzeigen...
        jf.setVisible(true);
    }

    /**
     * SelectDialog constructor comment.
     */
    public synchronized int waitWindow() {
        while (finish == false) {
            try {
                //###Editor.msg("warte...");
                wait(1000);
            } catch (InterruptedException e) {
            }
        }
        return value;
    }
}
