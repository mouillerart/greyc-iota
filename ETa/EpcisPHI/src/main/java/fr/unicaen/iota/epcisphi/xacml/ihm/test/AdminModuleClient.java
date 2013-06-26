/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.epcisphi.xacml.ihm.test;

import com.sun.xacml.ctx.Result;
import fr.unicaen.iota.epcisphi.utils.InterfaceHelper;
import fr.unicaen.iota.epcisphi.xacml.pep.EPCISPEP;
import fr.unicaen.iota.xacml.pep.MethodNamesAdmin;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AdminModuleClient extends javax.swing.JFrame {

    private static final Log log = LogFactory.getLog(AdminModuleClient.class);
    private String ownerName;

    /**
     * Creates new form CaptureModule
     */
    public AdminModuleClient(String ownerName) {
        this.ownerName = ownerName;
        initComponents();
        this.setLocationRelativeTo(null);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        sendButton = new javax.swing.JButton();
        actionComboBox = new javax.swing.JComboBox();
        userField = new javax.swing.JTextField();
        ownerField = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jLabel1.setText("Admin Module");
        jLabel2.setText("User: ");
        jLabel3.setText("Owner: ");
        jLabel8.setText("Action: ");
        sendButton.setText("Send");
        sendButton.addActionListener(new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                InterfaceHelper IH = new InterfaceHelper(ownerName);
                IH.reload();
                sendButtonActionPerformed(evt);
            }
        });

        ArrayList actions = new ArrayList();
        for (Method m : MethodNamesAdmin.class.getMethods()) {
            actions.add(m.getName());
        }
        actionComboBox.setModel(new javax.swing.DefaultComboBoxModel(actions.toArray()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel3)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel2)).addGroup(layout.createSequentialGroup().addContainerGap().addComponent(jLabel8))).addGap(20, 20, 20).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(userField, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE).addComponent(ownerField, javax.swing.GroupLayout.DEFAULT_SIZE, 231, Short.MAX_VALUE).addComponent(actionComboBox, 0, 231, Short.MAX_VALUE).addComponent(jLabel1))).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(273, Short.MAX_VALUE).addComponent(sendButton))).addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel1).addGap(12, 12, 12).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel2)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(ownerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel3)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(actionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel8)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(sendButton).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));
        pack();
    }

    private Object runMethod(Object obj, Object[] args, String methodName) throws Exception {
        Class[] paramTypes = null;
        if (args != null) {
            paramTypes = new Class[args.length];
            for (int i = 0; i < args.length; ++i) {
                paramTypes[i] = args[i].getClass();
            }
        }
        Method m = obj.getClass().getMethod(methodName, paramTypes);
        return m.invoke(obj, args);
    }

    private void sendButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String action = (String) actionComboBox.getSelectedItem();
        ArrayList args = new ArrayList();
        args.add(userField.getText());
        args.add(ownerField.getText());
        args.add(fr.unicaen.iota.xacml.policy.Module.administrationModule.getValue());
        try {
            EPCISPEP dspep = new EPCISPEP();
            int response = (Integer) runMethod(dspep, args.toArray(), action);
            String res = (response == Result.DECISION_PERMIT)? "ACCEPT": "DENY";
            AccessResponseDialog dialog = new AccessResponseDialog(this, true);
            dialog.setLocationRelativeTo(this);
            dialog.setResponse(res);
            dialog.setVisible(true);
        } catch (InvocationTargetException ex) {
            AccessResponseDialog dialog = new AccessResponseDialog(this, true);
            dialog.setLocationRelativeTo(this);
            dialog.setResponse("DENY");
            dialog.setVisible(true);
        } catch (NullPointerException ex) {
            AccessResponseDialog dialog = new AccessResponseDialog(this, true);
            dialog.setLocationRelativeTo(this);
            dialog.setResponse("DENY");
            dialog.setVisible(true);
        } catch (Exception ex) {
            log.fatal(null, ex);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                String owner = "anonymous";
                new AdminModuleClient(owner).setVisible(true);
            }
        });
    }
    private javax.swing.JComboBox actionComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField ownerField;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField userField;
}
