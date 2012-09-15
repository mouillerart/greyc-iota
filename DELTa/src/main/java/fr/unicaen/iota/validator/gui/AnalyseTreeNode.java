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

import java.awt.Component;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.tree.DefaultMutableTreeNode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *
 */
public class AnalyseTreeNode extends DefaultMutableTreeNode implements Comparable {

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM d hh:mm:ss z yyyy", Locale.US);
    private File userFile;
    private Icon icon;
    private Component component;
    private int type;
    private static final Log log = LogFactory.getLog(AnalyseTreeNode.class);
    // TODO: @SLS enum?
    public static final int NODE_TYPE = 0;
    public static final int LEAF_TYPE = 1;

    public AnalyseTreeNode(String userObject, File userFile, ImageIcon icon, int type) {
        super(userObject);
        this.userFile = userFile;
        this.icon = icon;
        JLabel label = new JLabel(userObject);
        label.setIcon(icon);
        this.component = label;
        this.type = type;
    }

    public File getFile() {
        return userFile;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof AnalyseTreeNode)) {
            return -1;
        }
        try {
            Date d1 = simpleDateFormat.parse(userFile.getName());
            Date d2 = simpleDateFormat.parse(((AnalyseTreeNode) o).getFile().getName());
            return d1.compareTo(d2);
        } catch (ParseException ex) {
            log.error(null, ex);
            return -1;
        }
    }

    /**
     * @return the icon
     */
    public Icon getIcon() {
        return icon;
    }

    public Component getComponent() {
        return component;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }
}
