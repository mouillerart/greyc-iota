package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

import java.util.*;

/**
 * EditDialog.java
 *
 * Displays a dialog, to edit extensions of a netobject.
 *
 * Created: Tue Jan  2 14:14:17 2001
 *
 * @author
 * @version
 */

class EditDialog extends JDialog
implements ActionListener {
    private Editor editor;
    private MemberSprite sprite;

    /*
     * Is this an modal dialog?
     */
    static boolean modal = false;

    /*
     * Holds the JTextAreas for evaluation
     * Store (JTextArea extensionValue -> String extensionId) tupel
     */
    private Hashtable textAreaToId = new Hashtable();

    protected EditDialog(Editor e, MemberSprite s) {
        this.sprite = s;
        this.editor = e;
        this.init();
        this.show();
    }

    /*
     * Creates a dialog, so that extendables can be edited.
     * @param page the page where the extendable lives
     * @param sprite the sprite of the extendable choosen by user
     */
    protected EditDialog(Page page, MemberSprite sprite) {
        super(page.getEditor().getEditorwindow(), "Edit Dialog", modal);

        this.editor = page.getEditor();
        this.sprite = sprite;

        // create menu
        this.init();

        // set Location
        JFrame frame = page.frame;
        Dimension dialogDim = this.getSize();
        Dimension frameDim = frame.getSize();
        Point location = frame.getLocation();
        Dimension screenSize = this.getToolkit().getScreenSize();
        int dx = ((frameDim.width - dialogDim.width) >> 1);
        int dy = ((frameDim.height - dialogDim.height) >> 1);
        location.translate(dx, dy);
        location.x = Math.max(0, Math.min(location.x, screenSize.width - getSize().width));
        location.y = Math.max(0, Math.min(location.y, screenSize.height - getSize().height));
        this.setLocation(location.x, location.y);

        this.show();
    }

    /*
     * Listening if Ok or Cancel -Button was pressed.
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("OK")) this.ok();
        if (cmd.equals("Cancel")) this.cancel();
    }

    /*
     * Cancel-Button was pressed in Dialog.
     */
    private void cancel() {
        this.dispose();
    }

    /*
     * Initialises TextAreas for editing
     * Extensions of netobjects.
     */
    private void init() {
        JPanel mainPanel = new JPanel();
        JPanel buttonPanel = new JPanel();
        JTabbedPane extensionPanel = new JTabbedPane();

        // Get the hashtable of extensions
        Object netobject = this.sprite.getNetobject();
        GraphProxy graph = this.editor.getGraphproxy();
        Hashtable extensions = graph.getExtensionIdToValue(netobject);
        // init main panel
        mainPanel.setLayout(new BorderLayout());

        // init the edit panel for extensions
        int ext_num = extensions.size();
        //    extensionPanel.setLayout(new GridLayout(ext_num,1));
        //    extensionPanel.setBorder(BorderFactory.createTitledBorder("Extensions"));
        Enumeration e = extensions.keys();
        while (e.hasMoreElements()) {
            JPanel panel = new JPanel();
            panel.setSize(400, 400);
            String id = (String)e.nextElement();
            String value = (String)extensions.get(id);
            panel.setBorder(new TitledBorder(id));
            JTextArea value_text = new JTextArea(value, 16, 30);
            value_text.setSize(400, 400);
            JScrollPane scrollpane = new JScrollPane(value_text);
            //      scrollpane.setSize(400,200);
            panel.add(scrollpane);
            extensionPanel.add(id, panel);
            this.textAreaToId.put(value_text, id);
        }
        mainPanel.add(extensionPanel, BorderLayout.CENTER);

        // init button panel with 'ok' and 'cancel' button
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // add main panel in window
        this.getContentPane().add(mainPanel);
        this.pack();
    }

    /*
     * Ok-Button was pressed in Dialog.
     */
    private void ok() {
        Enumeration e = this.textAreaToId.keys();
        while (e.hasMoreElements()) {
            // extract values of extensions in textAreas
            JTextArea valueField = (JTextArea)e.nextElement();
            String id = (String) this.textAreaToId.get(valueField);
            String value = valueField.getText();
            // Get a reference of netobject that the sprite represents
            Object netobject = this.sprite.getNetobject();
            // change extension in this editor
            ReferenceTable rtable = this.editor.getReferencetable();
            rtable.changeExtension(netobject, id, value);
            // change extensions in net
            GraphProxy graph = this.editor.getGraphproxy();
            graph.changeExtension(netobject, id, value);
            // close dialog frame
            this.dispose();
        }
    }
} // EditDialog