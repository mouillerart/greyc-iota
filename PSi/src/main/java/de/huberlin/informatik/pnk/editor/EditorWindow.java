package de.huberlin.informatik.pnk.editor;

import de.huberlin.informatik.pnk.app.base.*;
import java.awt.*;
import javax.swing.*;

/**
 * EditorWindow.java
 *
 *
 * Created: Thu May 24 22:58:47 2001
 *
 * @author
 * @version
 */

public class EditorWindow extends MetaJFrame {
    Editor editor;

    /**
     * Get the value of editor.
     * @return Value of editor.
     */
    public Editor getEditor() {
        return editor;
    }

    /**
     * Set the value of editor.
     * @param v  Value to assign to editor.
     */
    public void setEditor(Editor v) {
        this.editor = v;
    }

    JToolBar toolbar;

    /**
     * Get the value of toolbar.
     * @return Value of toolbar.
     */
    public JToolBar getToolbar() {
        return toolbar;
    }

    /**
     * Set the value of toolbar.
     * @param v  Value to assign to toolbar.
     */
    public void setToolbar(JToolBar v) {
        this.toolbar = v;
    }

    JTextField textfield;

    /**
     * Get the value of textfield.
     * @return Value of textfield.
     */
    public JTextField getTextfield() {
        return textfield;
    }

    /**
     * Set the value of textfield.
     * @param v  Value to assign to textfield.
     */
    public void setTextfield(JTextField v) {
        this.textfield = v;
    }

    JSplitPane splitpane;

    /**
     * Get the value of splitpane.
     * @return Value of splitpane.
     */
    public JSplitPane getSplitpane() {
        return splitpane;
    }

    /**
     * Set the value of splitpane.
     * @param v  Value to assign to splitpane.
     */
    public void setSplitpane(JSplitPane v) {
        this.splitpane = v;
    }

    JScrollPane viewscrollpane;

    /**
     * Get the value of viewscrollpane.
     * @return Value of viewscrollpane.
     */
    public JScrollPane getViewscrollpane() {
        return viewscrollpane;
    }

    /**
     * Set the value of viewscrollpane.
     * @param v  Value to assign to viewscrollpane.
     */
    public void setViewscrollpane(JScrollPane v) {
        this.viewscrollpane = v;
    }

    JPanel viewpanes;

    /**
     * Get the value of viewpanes.
     * @return Value of viewpanes.
     */
    public JPanel getViewpanes() {
        return viewpanes;
    }

    /**
     * Set the value of viewpanes.
     * @param v  Value to assign to viewpanes.
     */
    public void setViewpanes(JPanel v) {
        this.viewpanes = v;
    }

    public EditorWindow(Editor editor) {
        super(editor, "Editor");
        this.editor = editor;
    }

    void setNet() {
        //open the editorwindow
        this.setSize(900, 600);
        this.getContentPane().setLayout(new BorderLayout());
        this.toolbar = new JToolBar(JToolBar.VERTICAL);
        this.getContentPane().add(this.toolbar, BorderLayout.WEST);
        this.textfield = new JTextField();
        this.textfield.setEditable(false);
        this.getContentPane().add(this.textfield, BorderLayout.SOUTH);

        this.viewpanes = new JPanel();
        this.viewpanes.setLayout(new BoxLayout(this.viewpanes, BoxLayout.Y_AXIS));
        this.viewpanes.add(Box.createVerticalStrut(4));
        this.viewscrollpane = new JScrollPane(this.viewpanes);

        // Insert a first page in this editorwindow
        Page page;
        PageVector pagevector = this.editor.getPagevector();
        if (!pagevector.isEmpty())
            page = (Page)pagevector.get(0);
        else
            page = pagevector.createPage();

        this.splitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                        page.scrollpane,
                                        this.viewscrollpane);
        this.splitpane.setDividerSize(3);
        this.getContentPane().add(this.splitpane, BorderLayout.CENTER);

        //this.pack();
        this.show();
    }

    void close() {
        dispose();
    }
} // EditorWindow