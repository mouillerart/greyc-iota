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
import fr.unicaen.iota.epcisphi.utils.MapSessions;
import fr.unicaen.iota.epcisphi.xacml.pep.EPCISPEP;
import fr.unicaen.iota.xacml.pep.MethodNamesQuery;
import fr.unicaen.iota.xacml.pep.XACMLEPCISEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class QueryModuleClient extends javax.swing.JFrame {

    private static final Log log = LogFactory.getLog(QueryModuleClient.class);
    private String ownerName;

    public QueryModuleClient(String ownerName) {
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
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        sendButton = new javax.swing.JButton();
        actionComboBox = new javax.swing.JComboBox();
        userField = new javax.swing.JTextField();
        ownerField = new javax.swing.JTextField();
        epcField = new javax.swing.JTextField();
        epcClassField = new javax.swing.JTextField();
        bizStepField = new javax.swing.JTextField();
        eventTimeDayBox = new javax.swing.JComboBox();
        eventTimeMonthBox = new javax.swing.JComboBox();
        eventTimeYearBox = new javax.swing.JComboBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        jLabel1.setText("Query Module");
        jLabel2.setText("Owner: ");
        jLabel3.setText("Owner owner: ");
        jLabel4.setText("EPC: ");
        jLabel5.setText("EPCClass: ");
        jLabel6.setText("BizStep: ");
        jLabel7.setText("EventTime: ");
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
        for (Method m : MethodNamesQuery.class.getMethods()) {
            actions.add(m.getName());
        }
        actionComboBox.setModel(new javax.swing.DefaultComboBoxModel(actions.toArray()));

        String[] days = new String[31];
        for (int i = 1; i <= 31; i++) {
            days[i - 1] = "" + i;
        }
        eventTimeDayBox.setModel(new javax.swing.DefaultComboBoxModel(days));

        String[] months = new String[12];
        for (int i = 1; i <= 12; i++) {
            months[i - 1] = "" + i;
        }
        eventTimeMonthBox.setModel(new javax.swing.DefaultComboBoxModel(months));

        ArrayList years = new ArrayList();
        for (int i = 1970; i <= 2020; i++) {
            years.add("" + i);
        }
        eventTimeYearBox.setModel(new javax.swing.DefaultComboBoxModel(years.toArray()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(jLabel2).addComponent(jLabel3).addComponent(jLabel8).addComponent(jLabel4).addComponent(jLabel5).addComponent(jLabel6).addComponent(jLabel7)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(eventTimeDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(eventTimeMonthBox, 0, 51, Short.MAX_VALUE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(eventTimeYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)).addComponent(userField, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE).addComponent(ownerField, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE).addComponent(actionComboBox, 0, 212, Short.MAX_VALUE).addComponent(epcField, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE).addComponent(epcClassField, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE).addComponent(bizStepField, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE).addComponent(jLabel1))).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addContainerGap(254, Short.MAX_VALUE).addComponent(sendButton))).addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(jLabel1).addGap(12, 12, 12).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel2).addComponent(userField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(ownerField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel3)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(actionComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel8)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(epcField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel4)).addGap(7, 7, 7).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(epcClassField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel5)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(bizStepField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(jLabel6)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE).addComponent(jLabel7).addComponent(eventTimeDayBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(eventTimeYearBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE).addComponent(eventTimeMonthBox, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED).addComponent(sendButton).addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        pack();
    }

    private Date convertStringToDate(String day, String month, String year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return cal.getTime();
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
        Date eventTime = convertStringToDate((String) eventTimeDayBox.getSelectedItem(), (String) eventTimeMonthBox.getSelectedItem(), (String) eventTimeYearBox.getSelectedItem());
        String action = (String) actionComboBox.getSelectedItem();
        ArrayList args = new ArrayList();
        args.add(MapSessions.APM);
        args.add(userField.getText());
        args.add(ownerField.getText());
        if ((!("hello".equals(action))) && (!("ownerInfo".equals(action)))) {
            XACMLEPCISEvent xacmldse = new XACMLEPCISEvent(ownerField.getText(), bizStepField.getText(), epcField.getText(), eventTime, eventTime, epcClassField.getText(), "add", "parentId", "childEpc", new Long(1), "readPoint", "bizLoc", "bizTrans", "disposition", null);
            args.add(xacmldse);
        }
        args.add(fr.unicaen.iota.xacml.policy.Module.queryModule.getValue());
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
                new QueryModuleClient(owner).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify
    private javax.swing.JComboBox actionComboBox;
    private javax.swing.JTextField bizStepField;
    private javax.swing.JTextField epcClassField;
    private javax.swing.JTextField epcField;
    private javax.swing.JComboBox eventTimeDayBox;
    private javax.swing.JComboBox eventTimeMonthBox;
    private javax.swing.JComboBox eventTimeYearBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JTextField ownerField;
    private javax.swing.JButton sendButton;
    private javax.swing.JTextField userField;
    // End of variables declaration
}
