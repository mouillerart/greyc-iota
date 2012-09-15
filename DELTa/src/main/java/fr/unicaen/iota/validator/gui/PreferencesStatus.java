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

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 *
 */
public enum PreferencesStatus {

    UP("resources/pics/up.png"),
    DOWN("resources/pics/down.png"),
    MIDDLE("resources/pics/middle.png");
    
    private Icon icon;
    
    private PreferencesStatus(String iconpic) {
        this.icon = new ImageIcon(iconpic, this.name());
    }
    
    public Icon getIcon() {
        return icon;
    }
}
