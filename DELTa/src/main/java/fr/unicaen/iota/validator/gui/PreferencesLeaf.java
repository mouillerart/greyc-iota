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

import fr.unicaen.iota.validator.model.Link;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;

/**
 *
 */
class PreferencesLeaf implements ActionListener {

    public static enum Type {
        DS,
        EPCIS,
        BIZLOC
    }
    private Preferences preferences;
    private Type currentType;
    private JCheckBox checkbox;
    private Icon icon;
    private String text;
    private Link link;

    PreferencesLeaf(Link link, Type type, Preferences preferences) {
        this.preferences = preferences;
        this.text = link.getServiceAddress();
        this.link = link;
        this.currentType = type;
        checkbox = new JCheckBox();
        checkbox.addActionListener(this);
        checkbox.setEnabled(true);
        checkbox.setFocusable(true);
        checkbox.setSelected(link.isActiveAnalyse());
        if (type == Type.DS) {
            icon = createImageIcon("resources/pics/ds.png", "ds");
        } else if (type == Type.EPCIS) {
            icon = createImageIcon("resources/pics/epcis.png", "epcis");
        }
    }

    protected ImageIcon createImageIcon(String img, String description) {
        Icon ic = new ImageIcon(img, description);
        return new ImageIcon(img, description);
    }

    @Override
    public String toString() {
        return this.getText();
    }

    public Type getCurrentType() {
        return currentType;
    }

    /**
     * @return the checkbox
     */
    public JCheckBox getCheckbox() {
        return checkbox;
    }

    /**
     * @param checkbox the checkbox to set
     */
    public void setCheckbox(JCheckBox checkbox) {
        this.checkbox = checkbox;
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

    @Override
    public void actionPerformed(ActionEvent e) {
        getLink().setActiveAnalyse(!link.isActiveAnalyse());
        preferences.updateTreeModel();
    }

    /**
     * @return the link
     */
    public Link getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(Link link) {
        this.link = link;
    }
}
