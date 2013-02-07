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
import javax.swing.JTabbedPane;

/**
 *
 */
public class ClosableTabbedPane extends JTabbedPane {

    public TabComponent addClosableTabComponent(Component c) {
        this.add(c);
        int index = this.indexOfComponent(c);
        TabComponent tb = new TabComponent(c.getName(), this);
        this.setTabComponentAt(index, tb);
        return tb;
    }

    public void setTitleAt(Component aThis, String newTitle) {
        int i = this.indexOfComponent(aThis);
        Component comp = this.getTabComponentAt(i);
        if (comp instanceof TabComponent) {
            TabComponent tc = (TabComponent) comp;
            tc.setTitle(newTitle);
        } else {
            this.setTitleAt(i, newTitle);
        }
    }

    @Override
    public void setTitleAt(int i, String newTitle) {
        Component comp = this.getTabComponentAt(i);
        if (comp instanceof TabComponent) {
            TabComponent tc = (TabComponent) comp;
            tc.setTitle(newTitle);
        } else {
            super.setTitleAt(i, newTitle);
        }
    }

    public void closeTab(Component comp) {
        int i = this.indexOfTabComponent(comp);
        this.remove(i);
    }
}
