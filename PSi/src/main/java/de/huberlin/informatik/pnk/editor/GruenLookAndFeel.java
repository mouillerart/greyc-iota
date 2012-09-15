package de.huberlin.informatik.pnk.editor;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.basic.*;
import javax.swing.plaf.metal.*;

//public class GruenLookAndFeel extends BasicLookAndFeel {
public class GruenLookAndFeel extends MetalLookAndFeel {
    public GruenLookAndFeel() {
        super();
    }

    protected void initClassDefaults(UIDefaults table) {
        super.initClassDefaults(table);
    }

    public boolean isSupportedLookAndFeel() {
        return true;
    }

    public boolean isNativeLookAndFeel() {
        return false;
    }

    public String getName() {
        return "Gruen Look and Feel";
    }

    public String getID() {
        return "Gruen";
    }

    public String getDescription() {
        return "Gruen Look and Feel";
    }

    protected void initSystemColorDefaults(UIDefaults t) {
        super.initSystemColorDefaults(t);
        Color green = new ColorUIResource(0, 255, 0);
        Color white = new ColorUIResource(255, 255, 255);
        Color mytes = new ColorUIResource(225, 225, 225);

        t.put("control", mytes);
    }

    protected void initComponentDefaults(UIDefaults t) {
        super.initComponentDefaults(t);
        Color green = new ColorUIResource(0, 255, 0);
        Color white = new ColorUIResource(255, 255, 255);
        Color mytes = new ColorUIResource(225, 225, 225);

        Border marginBorder = new BasicBorders.MarginBorder();
        Insets insets = new InsetsUIResource(6, 10, 6, 10);
        Border emptyBorder = new EmptyBorder(insets);

        Object buttonBorder =
            new BorderUIResource.CompoundBorderUIResource
                (new LineBorder(Color.gray, 1), marginBorder);
        Object buttonBorder2 =
            new BorderUIResource.CompoundBorderUIResource
                (new LineBorder(Color.black, 2), marginBorder);
        t.put("Button.background", mytes);
        t.put("Button.border", buttonBorder);
        t.put("Button.margin", insets);
        t.put("RadioButton.background", mytes);
        t.put("RadioButton.border", buttonBorder);
        t.put("RadioButton.margin", insets);
        t.put("ToggleButton.background", mytes);
        t.put("ToggleButton.border", buttonBorder);
        t.put("ToggleButton.margin", insets);
        t.put("RadioButtonMenuItem.background", mytes);
        //	t.put ("RadioButtonMenuItem.border", buttonBorder);
        t.put("RadioButtonMenuItem.margin", insets);
        t.put("CheckBoxMenuItem.background", mytes);
        //	t.put ("CheckBoxMenuItem.border", buttonBorder);
        t.put("CheckBoxMenuItem.margin", insets);
        t.put("CheckBox.background", mytes);
        //	t.put ("CheckBox.border", buttonBorder);
        t.put("Checkbox.margin", insets);
        //t.put ("ToolBar.background", mytes);
        t.put("ComboBox.margin", insets);
        t.put("InternalFrame.background", mytes);
        t.put("InternalFrame.border", buttonBorder2);
        t.put("InternalFrame.margin", insets);
        //	t.put ("TextField.background", mytes);
        t.put("TextField.border", buttonBorder);
        t.put("TextField.margin", insets);
        //	t.put ("TextArea.background", mytes);
        t.put("TextArea.border", buttonBorder);
        t.put("TextArea.margin", insets);
        //	t.put ("TextPane.background", mytes);
        t.put("TextPane.border", marginBorder);
        t.put("TextPane.margin", insets);
        //	t.put ("EditorPane.background", mytes);
        t.put("EditorPane.border", buttonBorder);
        t.put("EditorPane.margin", insets);
        //	t.put ("PasswordField.background", mytes);
        t.put("PasswordField.border", buttonBorder);
        t.put("PasswordField.margin", insets);
        t.put("MenuBar.background", mytes);
        t.put("Menu.background", mytes);
        //	t.put ("MenuBar.border", buttonBorder);
        t.put("MenuBar.margin", insets);
        t.put("MenuItem.background", mytes);
        //      t.put ("MenuItem.border", buttonBorder);
        t.put("MenuItem.margin", insets);
        t.put("Panel.background", mytes);
        t.put("OptionPane.background", mytes);
        t.put("Tree.editorBorder", buttonBorder);
        t.put("Pane.border", buttonBorder);
        t.put("Pane.background", mytes);
        t.put("TabbedPane.border", buttonBorder);
        t.put("TabbedPane.background", mytes);
        t.put("SplitPane.border", buttonBorder);
        t.put("SplitPane.background", mytes);
        t.put("ScrollPane.border", buttonBorder);
        t.put("ScrollPane.background", mytes);
        t.put("ScrollBar.background", mytes);
        t.put("ScrollBar.border", buttonBorder);
        t.put("PopupMenu.border", buttonBorder);
        t.put("PopupMenu.background", mytes);
        t.put("List.border", buttonBorder);
        //	t.put ("List.background", mytes);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(500, 500);
        frame.getContentPane().setLayout(new FlowLayout());

        JButton button = new JButton("Hello...!");
        //button.setPreferredSize(new Dimension(100,100));

        frame.getContentPane().add(button);
        frame.show();

        String lfClassName = "GruenLookAndFeel";
        try {
            UIManager.setLookAndFeel(lfClassName);
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
            System.err.println("Fehler: " + e);
        }
    }
}