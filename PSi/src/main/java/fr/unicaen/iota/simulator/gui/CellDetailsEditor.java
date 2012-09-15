/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 2 of the License.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.simulator.gui;

import fr.unicaen.iota.simulator.util.Config;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class CellDetailsEditor extends AbstractCellEditor implements TableCellEditor, ActionListener {

    private static final Log log = LogFactory.getLog(TrashGUI.class);
    private JFrame parent;

    public CellDetailsEditor(JFrame f) {
        this.parent = f;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DetailsButton button = (DetailsButton) e.getSource();
        traceEPC(button.getEpc());
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    @Override
    public Object getCellEditorValue() {
        return null;
    }

    //Implement the one method defined by TableCellEditor.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        DetailsButton button = new DetailsButton(Integer.parseInt((String) table.getValueAt(row, 0)),
                (String) table.getValueAt(row, 1));
        button.setIcon(new ImageIcon("play.png"));
        button.addActionListener(this);
        button.setBorderPainted(false);
        return button;
    }

    private void traceEPC(String epc) {
        try {
            Socket soc = new Socket(Config.themamapAddress, Config.themamapPort);
            PrintWriter pw = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);
            pw.println(epc);
            pw.close();
            pw.flush();
            soc.close();
        } catch (UnknownHostException ex) {
            log.fatal(null, ex);
        } catch (IOException ex) {
            log.fatal(null, ex);
        }
    }
}
