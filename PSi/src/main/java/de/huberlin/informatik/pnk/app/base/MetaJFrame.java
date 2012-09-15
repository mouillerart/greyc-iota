package de.huberlin.informatik.pnk.app.base;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import javax.swing.JFrame;

/**
 * If possible applications should use an object of the class as window,
 * because it has special properties, that the ApplicationControl needs
 * for management purposes.
 */
public class MetaJFrame extends JFrame
implements FocusListener, WindowListener {
    private String name = null;
    private MetaBigApplication metaApplication;

    /**
     * A MetaApplication can use this method to construct a
     * MetaFrame, which can un-/register automaticly, and
     * informs the application about its focus state. This
     * properties are very usefull for the ApplicationControl.
     */
    public MetaJFrame(MetaBigApplication ma) {
        super();

        this.metaApplication = ma;
        this.metaApplication.registerWindow(this);

        this.addFocusListener(this);
        this.addWindowListener(this);

        this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    }

    /**
     * A MetaApplication can use this method to construct a
     * MetaFrame, which can un-/register automaticly, and
     * informs the application about its focus state. This
     * properties are very usefull for the ApplicationControl.
     */
    public MetaJFrame(MetaBigApplication ma, String name) {
        super(name);

        this.metaApplication = ma;
        this.metaApplication.registerWindow(this);

        this.addFocusListener(this);
        this.addWindowListener(this);

        this.setDefaultCloseOperation(javax.swing.WindowConstants.HIDE_ON_CLOSE);
    }

    /**
     * The MetaFrame closes itself and unregisters from
     * the MetaApplciation.
     */
    public void closeWindow() {
        this.dispose();
    }

    /**
     * The MetaFrame closes itself and unregisters from
     * the MetaApplciation.
     */
    public void dispose() {
        this.metaApplication.unregisterWindow(this);
        super.dispose();
    }

    /**
     * This window gets the focus.
     */
    public void focusGained(FocusEvent ev) {
        de.huberlin.informatik.pnk.appControl.base.D.d("MetaJFrame: focus...");
        this.metaApplication.setFocusedWindow(this);
        this.toFront();
    }

    /**
     * This window has lost the focus.
     */
    public void focusLost(FocusEvent ev) {
        de.huberlin.informatik.pnk.appControl.base.D.d("MetaJFrame focusLost");
        // Was soll denn hier passieren???
    }

    /**
     * The visibility of the MetaFrame is set to false.
     */
    public void hideWindow() {
        // ########## versteckt das Fenster...
        this.setVisible(false);
    }

    /**
     * If the MetaFrame has the focus, it informs
     * the MetaApplication.
     */
    public void processFocusEvent(FocusEvent ev) {
        // ########### Diese Methode scheint überflüssig!!!
        //de.huberlin.informatik.pnk.appControl.base.D.d("MetaJFrame processFocusEvent");
        this.metaApplication.setFocusedWindow(this);
    }

    /**
     * The window name is set.
     */
    protected void setWindowName() {
        if (this.name == null) {
            this.setName(metaApplication.getApplicationName());
        } else {
            this.setName(metaApplication.getApplicationName() + " - " + this.name);
        }
    }

    /**
     * The window name is set.
     *
     **@param name The new name of the window.
     */
    protected void setWindowName(String name) {
        this.name = name;
        this.setWindowName();
    }

    /**
     * The visibility of the MetaFrame is set to true.
     */
    public void unhideWindow() {
        // ########## macht Fenster sichtbar...
        this.setVisible(true);
    }

    /**
     * Set the focus of the MetaApplication to this window.
     */
    public void windowActivated(WindowEvent e) {
        // de.huberlin.informatik.pnk.appControl.base.D.d("MetaJFrame windowActivated");

        this.metaApplication.setFocusedWindow(this);
        //this.toFront();
    }

    public void windowClosed(WindowEvent e) {}
    /*
     * Sets state of page_checkboxmenuitem in editormenu
     * to false.
     */
    public void windowClosing(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {
        // de.huberlin.informatik.pnk.appControl.base.D.d("MetaJFrame windowDeactivated");
    }

    public void windowDeiconified(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    /*
     *
     *
     */
    public void windowOpened(WindowEvent e) {}
}
