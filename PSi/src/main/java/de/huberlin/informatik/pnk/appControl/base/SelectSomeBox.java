package de.huberlin.informatik.pnk.appControl.base;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
        The NetTypeFactory can create a dynamic net type, that is only specified
        by certain parameters, such as "BlackToken, MultiSet". The meaning of
        each parameter is declared in an extern XML configuration file. The URL
        of this file must be passed when calling the constructor.
 */
public class SelectSomeBox implements ActionListener {
    /*
          This dialog is used, when getSpecification is called without any net type
          parameters.
     */
    private JDialog dialog;

    private Vector buttonGroups = new Vector();

    /**
          Implementation of the ActionListener
     */
    public void actionPerformed(ActionEvent e) {
        String cmd = e.getActionCommand();
        if (cmd.equals("OK")) ok();
        if (cmd.equals("Cancel")) cancel();
    }

    /*
          called if ok button was pressed
     */
    private void ok() {
        selection = new Vector();
        if (!selectMulti) {
            /*evaluate the user choice building an argument vector*/
            for (int i = 0; i < buttonGroups.size(); i++) {
                /*get ButtonGroup for dimension i*/
                ButtonGroup bg = (ButtonGroup)buttonGroups.get(i);
                /*get selected button*/
                ButtonModel button = bg.getSelection();
                /*add parameter(=ActionCommand) to the vector*/
                if (button != null) {
                    selection.add(button.getActionCommand());
                }
            }
        } else {
            for (int i = 0; i < buttonGroups.size(); i++) {
                JCheckBox jcb = (JCheckBox)buttonGroups.get(i);
                if (jcb.isSelected()) {
                    selection.add(jcb.getActionCommand());
                }
            }
        }
        /*dispose the dialog; now the method getSpecification() continues*/
        dialog.dispose();
    }

    /*
          called if cancel button was pressed
     */
    private void cancel() {
        /*dispose the dialog; now the method getSpecification() continues*/
        selection = new Vector();
        dialog.dispose();
    }

    /*
          Returns the maximum number of arguments available for
          a dimension. Expects a vector representation of the
          format, that is returned by getDialogItems().
     */
    private int getMaxItems(Vector dimVector) {
        /*at least 2 items (dimension name+default => 0 parameters)*/
        int max = 2;
        /*for every dimension*/
        for (int i = 0; i < dimVector.size(); i++) {
            /*get itemVector*/
            Vector itemVector = (Vector)dimVector.get(i);
            /*if it has more elements max=size of this itemVector*/
            if (itemVector != null && itemVector.size() > max)
                max = itemVector.size();
        }
        /*only the number of available parameters are interesting*/
        return max - 2;
    }

    /*public static void main(String[] args) {
          NetTypeFactory ntf = null;
          try {
            ntf = new NetTypeFactory(new URL("file:netTypeSpecifications/dynamic/PNCube.xml"));
          } catch(MalformedURLException e) {
            System.out.println("URL not found!");
            System.exit(0);
          }

          Hashtable spec = ntf.getSpecification();
          dump(spec);
       }*/
/*public static void main(String[] args) {
        NetTypeFactory ntf = null;
        try {
          ntf = new NetTypeFactory(new URL("file:netTypeSpecifications/dynamic/PNCube.xml"));
        } catch(MalformedURLException e) {
          System.out.println("URL not found!");
          System.exit(0);
        }

        Hashtable spec = ntf.getSpecification();
        dump(spec);
   }*/
    private String info; /*
                            The Vector contains all RadioButtonGroups of the dialog. This makes it
                            easier to evaluate the user's choice.
                          */
    private Hashtable items; /*
                                The Vector contains all RadioButtonGroups of the dialog. This makes it
                                easier to evaluate the user's choice.
                              */
    private Vector parameters;  private Vector preselected; /*
                                                               Keeps the URL of the configuration file.
                                                             */
    private Vector selection;  private boolean selectMulti;  private String title;      /**
                                                                                           Shows the values of the Hashtable and returns keys of selected items
                                                                                           preselected keys will be selected...
                                                                                         */
    public SelectSomeBox(String title, String info, Hashtable items) {
        // will show values from Hashtable...
        this.title = title;
        this.info = info;
        this.items = items;
        this.selectMulti = true;
        this.preselected = new Vector();

        doSelect();
    }           /**
                   Shows the values of the Hashtable and returns keys of selected items
                   preselected keys will be selected...
                 */

    public SelectSomeBox(String title, String info, Hashtable items, Vector preselected) {
        // will show values from Hashtable...
        this.title = title;
        this.info = info;
        this.items = items;
        this.selectMulti = true;
        this.preselected = preselected;

        doSelect();
    }           /**
                   Shows the values of the Hashtable and returns keys of selected items
                   preselected keys will be selected...
                 */

    public SelectSomeBox(String title, String info, Hashtable items, boolean selectMulti) {
        // will show values from Hashtable...
        this.title = title;
        this.info = info;
        this.items = items;
        this.selectMulti = selectMulti;
        this.preselected = new Vector();

        doSelect();
    }           /**
                   Shows the values of the Hashtable and returns keys of selected items
                   preselected keys will be selected...
                 */

    public SelectSomeBox(String title, String info, Hashtable items, boolean selectMulti, Vector preselected) {
        // will show values from Hashtable...
        this.title = title;
        this.info = info;
        this.items = items;
        this.selectMulti = selectMulti;
        this.preselected = preselected;

        doSelect();
    }           /**
                   builds the modal Dialog...
                 */

    private void doSelect() {
        // will show values from Hashtable...
        selection = new Vector();

        dialog = new JDialog(new JFrame(), this.title, true);
        dialog.setSize(400, 120 + (items.size() * 18));

        JPanel dimPanel = new JPanel(new GridLayout(items.size(), 1));
        dimPanel.setBorder(new TitledBorder(info));
        dialog.getContentPane().add(dimPanel);

        if (!selectMulti) {
            Enumeration e = items.keys();
            ButtonGroup bg = new ButtonGroup();
            boolean first = true;
            while (e.hasMoreElements()) {
                Object o = e.nextElement();
                String choiceText = (String)items.get(o);
                JRadioButton choice = new JRadioButton(choiceText);
                choice.setActionCommand((String)o);
                if (preselected.contains(o) && first) {
                    choice.setSelected(true);
                    first = false;
                }
                dimPanel.add(choice);
                bg.add(choice);
            }
            buttonGroups.add(bg);
        } else {
            Enumeration e = items.keys();
            //ButtonGroup bg = new ButtonGroup();
            while (e.hasMoreElements()) {
                Object o = e.nextElement();
                String choiceText = (String)items.get(o);
                JCheckBox choice = new JCheckBox(choiceText);
                choice.setActionCommand((String)o);
                if (preselected.contains(o)) {
                    choice.setSelected(true);
                }
                dimPanel.add(choice);
                buttonGroups.add(choice);
            }
        }

        /*add an ok and a cancel button and show the dialog*/
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton okButton = new JButton("OK");
        okButton.addActionListener(this);
        buttons.add(okButton);
        JButton cancelButton = new JButton("Cancel");
        buttons.add(cancelButton);
        cancelButton.addActionListener(this);
        dialog.getContentPane().add("South", buttons);
        //The dialog is modal - waits here for destruction of dialog
        dialog.show();
        //After pressing the ok or cancel button this Constructor ends.
    }      /**
              This method evokes a dialog to request the net type parameters.
              The resulting specification is returned as Hashtable.
              The parent parameter is the parent frame of the dialog.
            */

    public Vector getSelection() {
        return selection;
    }
}