package de.huberlin.informatik.pnk.appControl;

import java.net.URL;
/**
 * Insert the type's description here.
 * NetFileObject verwaltet File und dazugeh÷rige Netze...
 * Creation date: (07.11.00 15:31:25)
 * @author:
 */
import java.util.*;

import de.huberlin.informatik.pnk.appControl.base.*; public class NFObject {
    private IOTObject iotype;
    private Vector nets;
    private URL fileURL;
/**
 * NFObject constructor comment.
 */
    public NFObject(URL fileURL, IOTObject iotype, Vector nets) {
        super();
        this.fileURL = fileURL;
        this.iotype = iotype;
        this.nets = nets;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @return java.lang.String
 */
    public void close() {
        D.d("NFObject: Close File: " + fileURL, 2);
        // verhindert Seiteneffekte... (gibts (noch) nicht;-)
        Vector v = new Vector(nets);
        for (int i = 0; i < v.size(); i++) {
            ANObject ano = ((ANObject)v.get(i));
            ano.removeNetFile(this);
            nets.remove(ano);
        }
        nets = null;
        iotype = null;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @return Vector
 */
    public Vector getFileNets() {
        return nets;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @return java.lang.String
 */
    public URL getFileURL() {
        return fileURL;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @return de.huberlin.informatik.pnk.appControl.IOTObject
 */
    public IOTObject getIotype() {
        return iotype;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @param newFileURL java.lang.String
 */
    public void setFileURL(URL newFileURL) {
        fileURL = newFileURL;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @param newIotype de.huberlin.informatik.pnk.appControl.IOTObject
 */
    public void setIotype(IOTObject newIotype) {
        iotype = newIotype;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @param newNets Vector
 */
    public void setNets(Vector newNets) {
        nets = newNets;
    }

/**
 * Insert the method's description here.
 * Creation date: (07.11.00 15:33:58)
 * @return java.lang.String
 */
    public String getFile() {
        return fileURL.getFile();
    }
}