/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.validator.gui;

import fr.unicaen.iota.validator.IOTA;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 */
public class CellDetailsEditor extends AbstractCellEditor
        implements TableCellEditor, ActionListener {

    private JFrame parent;
    private IOTA iota;

    public CellDetailsEditor(JFrame f, IOTA iota) {
        this.parent = f;
        this.iota = iota;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DetailsButton button = (DetailsButton) e.getSource();
        DetailsDialog detailsDialog = new DetailsDialog(parent, true, button.getEpc(), button.getRaw(), iota);
        detailsDialog.setVisible(true);
    }

    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    @Override
    public Object getCellEditorValue() {
        return null;
    }

    //Implement the one method defined by TableCellEditor.
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        DetailsButton button = new DetailsButton(Integer.parseInt((String) (table.getValueAt(row, 0))),
                (String) table.getValueAt(row, 1));
        button.setIcon(new ImageIcon("resources/pics/play.png"));
        button.addActionListener(this);
        button.setBorderPainted(false);
        return button;
    }
}
