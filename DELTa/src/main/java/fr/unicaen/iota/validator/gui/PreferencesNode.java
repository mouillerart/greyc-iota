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

import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

/**
 *
 */
public class PreferencesNode {

    private Icon icon;
    private String text;
    private List<PreferencesLeaf> preferencesLeafs;

    PreferencesNode(String text, List<PreferencesLeaf> list) {
        this.text = text;
        this.preferencesLeafs = list;
        this.icon = createImageIcon("resources/pics/bizLoc.png", "business location");
    }

    protected ImageIcon createImageIcon(String img, String description) {
        return new ImageIcon(img, description);
    }

    @Override
    public String toString() {
        return this.getText();
    }

    /**
     * @return the icon
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text the text to set
     */
    public void setText(String text) {
        this.text = text;
    }

    JComponent getStatusIcon() {
        PreferencesStatus status = PreferencesStatus.DOWN;
        int activeCompt = 0;
        for (PreferencesLeaf pLeaf : preferencesLeafs) {
            if (pLeaf.getLink().isActiveAnalyse()) {
                activeCompt++;
            }
        }
        if (activeCompt == 0) {
            status = PreferencesStatus.DOWN;
        }
        if (activeCompt > 0 && activeCompt < preferencesLeafs.size()) {
            status = PreferencesStatus.MIDDLE;
        }
        if (activeCompt == preferencesLeafs.size()) {
            status = PreferencesStatus.UP;
        }
        return new JLabel(status.getIcon());
    }
}
