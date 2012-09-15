package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * AllSelectedDialog.java
 *
 * Show a non modal Dialog that asks user
 * if all objects selected.
 *
 * Created: Wed Jan 10 09:07:52 2001
 *
 * @author Alexander Gruenewald
 * @version
 */

class AllSelectedDialog extends JDialog {
    Editor editor = null;

    /*
     * Opens a nonmodal Dialog, That requests
     * the user to press the ok button, after selecting
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

        this.show();
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