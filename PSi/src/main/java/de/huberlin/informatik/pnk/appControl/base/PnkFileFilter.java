package de.huberlin.informatik.pnk.appControl.base;

import java.io.File;
/**
 * Insert the type's description here.
 * Creation date: (16.10.00 01:37:06)
 * @author:
 */
/**
 * This class implements a file filter, that extends
 * javax.swing.filechooser.FileFilter. It is mainly used
 * for file open/save dialogues to preselect a specific
 * file type.
 */
public class PnkFileFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter {
    private java.lang.String description = "unknown (*.*)";
    private String extension = "*";
/**
 * PnkFileFilter constructor.
 *
 **@param des gives a description of the file type(s) chosen (default: "unknown (*.*)")
 **@param ext extension of the file type(s) (default: "*"); can be a list separated by ','
 */
    public PnkFileFilter(String des, String ext) {
        super();
        description = des;
        if (ext != null && ext.length() != 0) {
            extension = ext;
        } else {
            extension = "*";
        }
    }

/**
 * Returns true if the given 'pathname' matches the file extension list
 * of this pnk file filter.
 */
    public boolean accept(File pathname) {
        if (pathname.isDirectory()) {
            return true;
        } else {
            String e = extension;

            int i = e.indexOf(',');
            while (i != -1) {
                if (pathname.toString().endsWith("." + e.substring(0, i)) || e.substring(0, i).equals("*")) {
                    if ((pathname.canRead()) || (!pathname.exists())) {
                        return true;
                    } else {
                        System.out.println("can't read file " + pathname.toString());
                        return false;
                    }
                }
                e = e.substring(i + 1);
                i = e.indexOf(',');
            }
            if (pathname.toString().endsWith("." + e) || e.equals("*")) {
                if ((pathname.canRead()) || (!pathname.exists())) {
                    return true;
                } else {
                    System.out.println("can't read file " + pathname.toString());
                    return false;
                }
            } else {
                return false;
            }
        }
    }

/**
 * Creates and returns a description of this file filter by concatinating
 * the allowed file extensions. -> (*.ext1, *.ext2 ... )
 * This method is independant from setDescription(). For this purpose
 * use getShortDescription() instead.
 */
    public String getDescription() {
        String des = description + " (*.";
        String e = extension;

        int i = e.indexOf(',');
        while (i != -1) {
            des = des + e.substring(0, i) + ", *.";

            e = e.substring(i + 1);
            i = e.indexOf(',');
        }
        des = des + e + ")";
        return des;
    }

/**
 * Returns the list (a String, separated by ',') of allowed
 * file extensions
 */
    public String getExtension() {
        return extension;
    }

/**
 * Get a short description of this file filter.
 * This method is dependant from the value set by setDescription().
 */
    public String getShortDescription() {
        return description;
    }

/**
 * Give a short description. The description is visible in several
 * dialogues, where the pnk file filter is used.
 */
    public void setDescription(String des) {
        description = des;
    }

/**
 * Set extension list for this pnk file filter
 *
 **@param ext extension of the file type(s) (default: "*"); can be a list separated by ','
 */
    public void setExtension(String ext) {
        if (ext != null && ext.length() != 0) {
            extension = ext;
        } else {
            extension = "*";
        }
    }

/**
 * Standard string representation -> getDescription()
 */
    public String toString() {
        return getDescription();
    }

/**
 * Renames the File to filename + stdExtension (first in list)
 */
    public File extend(File pathname) {
        if (pathname.isDirectory()) {
            return pathname;
        } else {
            String e = extension;

            int i = e.indexOf(',');
            if (i != -1) {
                e = e.substring(0, i);
            }
            return new File(pathname.toString() + "." + e);
        }
    }
}
