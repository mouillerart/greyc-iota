package de.huberlin.informatik.pnk.appControl;

import de.huberlin.informatik.pnk.appControl.base.D;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.*;
import java.util.*;
import javax.swing.*;

/**
 * This class represents the menu of appControl on screen.
 */
// class ApplicationManagerMenu extends JFrame implements ActionListener, ItemListener {
public class ApplicationControlMenu implements ActionListener, ItemListener, WindowListener {
    // Hier die Konstanten für die Menubaustelle...
    public static final Integer MENU_TITLE = new Integer(0);
    public static final Integer MENU_ENTRY = new Integer(1);
    public static final Integer MENU_CHECK = new Integer(2);
    public static final Integer MENU_RADIO = new Integer(3);
    public static final Integer MENU_SELECTED = new Integer(6);
    public static final Integer MENU_UNSELECTED = new Integer(7);
    public static final Integer MENU_SEPARATOR = new Integer(8);
    public static final Integer MENU_SUB = new Integer(9);
    public static final Integer MENU_INACTIVE = new Integer(0);
    public static final Integer MENU_ACTIVE = new Integer(1);
    private ApplicationControl ac = null;
    protected JFrame frame = new JFrame("ApplicationControl");
    private JMenuBar menuBar = new JMenuBar();
    // Hashtable für die MenuActions...
    private Hashtable menuActions = new Hashtable();
    private JLabel statusLabel = new JLabel("status");
    private JLabel informLabel = new JLabel("inform");
    private Vector menustruct = new Vector();
    // Zähler für Methoden...
    private int m = 0;

    ApplicationControlMenu(ApplicationControl ac) {
        this.ac = ac;
        this.frame.setDefaultCloseOperation(this.frame.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(this);
        this.frame.setJMenuBar(this.menuBar);
        this.frame.getContentPane().setLayout(new BorderLayout());
        JPanel jpanel = new ImagePanel("pictures/pnet.png");
        this.frame.add(jpanel, BorderLayout.CENTER);
        this.frame.setSize(360, 180);
        this.frame.setLocationRelativeTo(null);
        this.frame.pack();
    }
    //**********************************************************************
    //Interface ActionListener
    //**********************************************************************

    public void actionPerformed(ActionEvent ae) {
        String cmd = ae.getActionCommand();
        Object source = ae.getSource();

        //D.d("menuActions: " + cmd + " " + menuActions);
        if (menuActions.containsKey(cmd)) {
            Object[] com = (Object[])menuActions.get(cmd);
            if (com[0] != null) {
                try {
                    ((Method)com[0]).invoke((Object)com[1], (Object[])com[2]);
                } catch (IllegalAccessException e) {
                    D.d("ACMenu - actionPerformed: IllegalAccessException");
                } catch (InvocationTargetException e) {
                    D.d("ACMenu - actionPerformed: InvocationTargetException");
                    D.d(e.getTargetException());
                    e.printStackTrace();
                }
            } else {
                D.d("### ACMenu - actionPerformed: No command found!");
            }
        } else {
            D.d("### ACMenu - actionPerformed: Command not found... " + cmd);
        }
    }

    /**
     * Show an errormessage on screen.
     */
    public void displayErrorMessage(String msg) {
        String title = "ERROR";
        int messageTyp = JOptionPane.ERROR_MESSAGE;
        JOptionPane.showMessageDialog(null, msg, title, messageTyp);
    }

    protected boolean displayQuestion(String msg) {
        int answer = JOptionPane.showConfirmDialog(null, msg);
        if (answer == 0) {
            return true;
        }
        return false;
    }

    protected String getInformation(String msg) {
        return JOptionPane.showInputDialog(msg);
    }

    //*****************************************************************************
    //Interface ItemListener
    //*****************************************************************************

    public void itemStateChanged(ItemEvent ie) {}

    /*
     * Setzt das InformationLabel im editormenu, weleches ein zusaetzliche
     * Meldung anzeigt. Standardwert fuer informationLabel wenn "null"als argument.
     */

    protected void makeVisible() {
        this.frame.setVisible(true);
    }

    /*
     * Setzt das InformationLabel im editormenu, weleches ein zusaetzliche
     * Meldung anzeigt. Standardwert fuer informationLabel wenn "null"als argument.
     */

    protected void setInformationLabel() {
        this.informLabel.setText("...");
        // ########## nötig???
        this.frame.repaint(0);
    }

    /*
     * Setzt das InformationLabel im editormenu, weleches ein zusaetzliche
     * Meldung anzeigt. Standardwert fuer informationLabel wenn "null"als argument.
     */

    protected void setInformationLabel(String info) {
        //if (info == null) info = "...";
        this.informLabel.setText(info);
        // ########## nötig???
        this.frame.repaint(0);
    }

    public void setMenu(int pos, JMenu[] menus) {
        // pos gibt die Position an, cont ist der Inhalt...
        // D.d("### ACM setMenu M " + pos + " " + menus.length);

        if (pos != -1) {
            if (this.menustruct.size() <= pos) {
                this.menustruct.setSize(pos + 1);
            }
            this.menustruct.setElementAt(menus, pos);

            // ########### Pending: Hier nur die Menus des Index loeschen...
            this.menuBar.removeAll();

            Enumeration e = this.menustruct.elements();
            while (e.hasMoreElements()) {
                JMenu[] jm = (JMenu[])e.nextElement();
                for (int j = 0; j < jm.length; j++) {
                    this.menuBar.add(jm[j]);
                }
            }
            // Dimension d = this.frame.getSize();
            this.frame.pack();
            // this.frame.setSize(d);
            // this.frame.repaint();
        }
    }

    public JMenu[] setMenu(int pos, String id, Object[] struct) {
        // pos gibt die Position an, id ist die App-ID, struct ist der Inhalt...

        // D.d("### ACM setMenu O " + pos + " " + id + " " + struct.length);

        // Alte Methoden aus Hashtable entfernen...
        this.m = 0;
        while (menuActions.containsKey(id + "-" + m)) {
            menuActions.remove(id + "-" + m++);
        }

        // Zähler für Methoden... Klassenvariable...
        this.m = 0;
        // Liste der Menus...
        JMenu[] actmenus = new JMenu[struct.length];
        for (int i = 0; i < struct.length; i++) {
            JMenu menu = setSingleMenu(id, (Object[])struct[i]);
            actmenus[i] = menu;
        }

        this.setMenu(pos, actmenus);

        return actmenus;
    }

    private JMenu setSingleMenu(String id, Object struct[]) {
        //D.d("Menu: " + struct + " " + struct[0]);
        JMenu menu = new JMenu((String)struct[0]);
        if (MENU_ACTIVE == (Integer)struct[1]) {
            for (int j = 2; j < struct.length; j++) {
                Object[] mm = (Object[])struct[j];
                // D.d("Entry: " + mm + " " + mm[0]);
                Integer entrytype = (Integer)mm[0];
                if (MENU_SEPARATOR == entrytype) {
                    menu.addSeparator();
                } else if (MENU_ENTRY == entrytype) {
                    JMenuItem mi = new JMenuItem((String)mm[2]);
                    if (MENU_ACTIVE == (Integer)mm[1]) {
                        Class[] parclasses = new Class[mm.length - 5];
                        Object[] pars = new Object[mm.length - 5];
                        for (int k = 5; k < mm.length; k++) {
                            parclasses[k - 5] = mm[k].getClass();
                            pars[k - 5] = mm[k];
                        }
                        Class c = mm[3].getClass();
                        Method met = null;
                        while (met == null && c != null) {
                            try {
                                met = c.getDeclaredMethod((String)mm[4], parclasses);
                            } catch (NoSuchMethodException e) {
                                //D.d("NoSuchMethodException: " + c.getName() + " " + mm[4]);
                                c = c.getSuperclass();
                            }
                        }
                        if (met == null) {
                            //D.d("ACM.setSingleMenu: Methode nicht auf herkömmlichem Weg gefunden.");
                        } else {
                            //D.d("ACM.setSingleMenu: Methode gefunden!");
                        }
                        c = mm[3].getClass();
                        while (met == null && c != null) {
                            // hier besser:
                            // alle Methoden holen...
                            Method[] mets = c.getDeclaredMethods();
                            for (int x1 = 0; x1 < mets.length; x1++) {
                                // nach Name checken...
                                if (mets[x1].getName().equals((String)mm[4])) {
                                    Class[] metpars = mets[x1].getParameterTypes();
                                    // nach Parameterlänge checken...
                                    if (metpars.length == pars.length) {
                                        // indiziert Typen prüfen... (implements...)
                                        boolean notfound = false;
                                        for (int x2 = 0; x2 < metpars.length; x2++) {
                                            if (!metpars[x2].isInstance(pars[x2])) {
                                                notfound = true;
                                                break;
                                            }
                                        }
                                        if (notfound == false) {
                                            met = mets[x1];
                                            break;
                                        }
                                    }
                                }
                            }
                            if (met == null) {
                                c = c.getSuperclass();
                            }
                        }
                        if (met != null) {
                            menuActions.put(id + "-" + m, new Object[] {met, mm[3], pars});
                            mi.setActionCommand(id + "-" + m++);
                            mi.addActionListener(this);
                        } else {
                            // D.d("### No Method found, no action added... " + mm[2]);
                        }
                    } else {
                        mi.setEnabled(false);
                    }
                    menu.add(mi);
                } else if (MENU_CHECK == entrytype) {
                    JCheckBoxMenuItem mi = new JCheckBoxMenuItem((String)mm[2]);
                    mi.setState(((Boolean)mm[3]).booleanValue());
                    if (MENU_ACTIVE == (Integer)mm[1]) {
                        Class c = mm[4].getClass();
                        Class[] parclasses = new Class[mm.length - 6];
                        Object[] pars = new Object[mm.length - 6];
                        for (int k = 6; k < mm.length; k++) {
                            parclasses[k - 6] = mm[k].getClass();
                            pars[k - 6] = mm[k];
                        }
                        Method met = null;
                        while (met == null && c != null) {
                            try {
                                met = c.getDeclaredMethod((String)mm[5], parclasses);
                            } catch (NoSuchMethodException e) {
                                //D.d("NoSuchMethodException: " + c.getName() + " " + mm[5]);
                                c = c.getSuperclass();
                            }
                        }
                        if (met == null) {
                            //D.d("ACM.setSingleMenu: Methode nicht auf herkömmlichem Weg gefunden.");
                        } else {
                            //D.d("ACM.setSingleMenu: Methode gefunden!");
                        }
                        c = mm[4].getClass();
                        while (met == null && c != null) {
                            // hier besser:
                            // alle Methoden holen...
                            Method[] mets = c.getDeclaredMethods();
                            for (int x1 = 0; x1 < mets.length; x1++) {
                                // nach Name checken...
                                if (mets[x1].getName().equals((String)mm[5])) {
                                    Class[] metpars = mets[x1].getParameterTypes();
                                    // nach Parameterlünge checken...
                                    if (metpars.length == pars.length) {
                                        // indiziert Typen prüfen... (implements...)
                                        boolean notfound = false;
                                        for (int x2 = 0; x2 < metpars.length; x2++) {
                                            if (!metpars[x2].isInstance(pars[x2])) {
                                                notfound = true;
                                                break;
                                            }
                                        }
                                        if (notfound == false) {
                                            met = mets[x1];
                                            break;
                                        }
                                    }
                                }
                            }
                            if (met == null) {
                                c = c.getSuperclass();
                            }
                        }
                        if (met != null) {
                            menuActions.put(id + "-" + m, new Object[] {met, mm[4], pars});
                            mi.setActionCommand(id + "-" + m++);
                            mi.addActionListener(this);
                        } else {
                            // D.d("### No Method found, no action added... " + mm[2]);
                        }
                    } else {
                        mi.setEnabled(false);
                    }
                    menu.add(mi);
                } else if (MENU_RADIO == entrytype) {
                    // ########## Pending: Radiobuttons...
                    ButtonGroup bg = new ButtonGroup();
                    for (int l = 1; l < mm.length; l++) {
                        Object[] oo = (Object[])mm[l];
                        JRadioButtonMenuItem mi;
                        if (MENU_SELECTED == (Integer)oo[2]) {
                            mi = new JRadioButtonMenuItem((String)oo[1], true);
                        } else {
                            mi = new JRadioButtonMenuItem((String)oo[1], false);
                        }
                        if (MENU_ACTIVE == (Integer)oo[0]) {
                            Class c = oo[3].getClass();
                            Class[] parclasses = new Class[oo.length - 5];
                            Object[] pars = new Object[oo.length - 5];
                            for (int k = 5; k < oo.length; k++) {
                                parclasses[k - 5] = oo[k].getClass();
                                pars[k - 5] = oo[k];
                            }
                            Method met = null;
                            while (met == null && c != null) {
                                try {
                                    met = c.getDeclaredMethod((String)oo[4], parclasses);
                                } catch (NoSuchMethodException e) {
                                    //D.d("NoSuchMethodException: " + c.getName() + " " + mm[5]);
                                    c = c.getSuperclass();
                                }
                            }
                            if (met == null) {
                                // D.d("ACM.setSingleMenu: Methode nicht auf herkömmlichem Weg gefunden.");
                            } else {
                                // D.d("ACM.setSingleMenu: Methode gefunden!");
                            }
                            c = oo[3].getClass();
                            while (met == null && c != null) {
                                // hier besser:
                                // alle Methoden holen...
                                Method[] mets = c.getDeclaredMethods();
                                for (int x1 = 0; x1 < mets.length; x1++) {
                                    // nach Name checken...
                                    if (mets[x1].getName().equals((String)oo[4])) {
                                        Class[] metpars = mets[x1].getParameterTypes();
                                        // nach Parameterlünge checken...
                                        if (metpars.length == pars.length) {
                                            // indiziert Typen prüfen... (implements...)
                                            boolean notfound = false;
                                            for (int x2 = 0; x2 < metpars.length; x2++) {
                                                if (!metpars[x2].isInstance(pars[x2])) {
                                                    notfound = true;
                                                    break;
                                                }
                                            }
                                            if (notfound == false) {
                                                met = mets[x1];
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (met == null) {
                                    c = c.getSuperclass();
                                }
                            }
                            if (met != null) {
                                menuActions.put(id + "-" + m, new Object[] {met, oo[3], pars});
                                mi.setActionCommand(id + "-" + m++);
                                mi.addActionListener(this);
                            } else {
                                // D.d("### No Method found, no action added... " + oo[1]);
                            }

                            bg.add(mi);
                        } else {
                            mi.setEnabled(false);
                            bg.add(mi);
                        }
                        menu.add(mi);
                    }
                } else if (MENU_SUB == entrytype) {
                    JMenu submenu = setSingleMenu(id, (Object[])mm[1]);
                    menu.add(submenu);
                } else {
                    // D.d("### unknown Menu-ID: " + entrytype);
                }
            } // for (int j=2; j<struct.length; j++)
        } // if (MENU_ACTIVE == (Integer) struct[1])
        else {
            menu.setEnabled(false);
        }
        return menu;
    }

    /*
     * StatusLabel zeigt den gerade aktiven modus des editormenus an.
     * Bei Aenderung des modus, muss es mit dieser Methode aktualisiert werden.
     */

    protected void setStatusLabel() {
        this.setStatusLabel(null);
    }

    /*
     * StatusLabel zeigt den gerade aktiven modus des editormenus an.
     * Bei Aenderung des modus, muss es mit dieser Methode aktualisiert werden.
     */

    protected void setStatusLabel(String status) {
        if (status == null) {
            status = "<no net loaded>";
        }
        this.statusLabel.setText(status);
        // ########## nötig???
        // this.frame.repaint();
    }

    //*********************************************************
    // Some methods to show text on screen
    //*********************************************************

    protected void showInformation(String msg) {
        String title = "Information";
        int messageTyp = JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(null, msg, title, messageTyp);
    }

    //*********************************************************
    // Some methods to show text on screen
    //*********************************************************

    public void windowActivated(WindowEvent e) {}

    //*********************************************************
    // Some methods from WindowsListener
    //*********************************************************

    public void windowClosed(WindowEvent e) {}

    //*********************************************************
    // Some methods from WindowsListener
    //*********************************************************

    public void windowClosing(WindowEvent e) {
        this.ac.menuQuit();
    }

    //*********************************************************
    // Some methods from WindowsListener
    //*********************************************************

    public void windowDeactivated(WindowEvent e) {}

    //*********************************************************
    // Some methods from WindowsListener
    //*********************************************************

    public void windowDeiconified(WindowEvent e) {}

    //*********************************************************
    // Some methods from WindowsListener
    //*********************************************************

    public void windowIconified(WindowEvent e) {}

    //*********************************************************
    // Some methods from WindowsListener
    //*********************************************************

    public void windowOpened(WindowEvent e) {}


    class ImagePanel extends JPanel {
        private Image img;

        public ImagePanel(String img) {
            this(new ImageIcon(img).getImage());
        }

        public ImagePanel(Image img) {
            this.img = img;
            Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
            setPreferredSize(size);
            setMinimumSize(size);
            setMaximumSize(size);
            setSize(size);
            setLayout(null);
        }

        public void paintComponent(Graphics g) {
            g.drawImage(img, 0, 0, null);
        }
    }
}
