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
package fr.unicaen.iota.application.client.gui;

import java.awt.Component;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.TableCellRenderer;

/**
 *
 */
public class TextAreaRenderer extends JTextArea implements TableCellRenderer {

    public TextAreaRenderer() {
        this.setOpaque(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        String content = value.toString();
        this.setText(content);
        if (isSelected) {
            this.setOpaque(true);
            this.setBackground(table.getSelectionBackground());
            this.setForeground(table.getSelectionForeground());
            int nbLigne = content.split("\\n").length;
            if (nbLigne != 1) {
                table.setRowHeight(row, nbLigne * 17);
            }
        } else {
            this.setOpaque(false);
            this.setForeground(table.getForeground());
            table.setRowHeight(row, 17);
        }
        return this;
    }
}
