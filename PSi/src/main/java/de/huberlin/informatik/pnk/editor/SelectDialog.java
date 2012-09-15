package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * Insert the type's description here.
 * Creation date: (27.2.2001 15:27:27)
 * @author:
 */
public class SelectDialog extends Thread implements ActionListener {
    boolean finish = false;
    JFrame jf;
    public static int SELECTDIALOG_OK = 0;
    public static int SELECTDIALOG_CANCEL = 1;
    int value = SELECTDIALOG_OK;

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
        jf.show();
    }

    /**
     * SelectDialog constructor comment.
     */
    public synchronized int waitWindow() {
        while (finish == false) {
            try {
                //###Editor.msg("warte...");
                wait(1000);
            } catch (InterruptedException e) {}
        }
        return value;
    }
}