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

import javax.swing.JButton;

/**
 *
 */
public class DetailsButton extends JButton {

    private int rawId;
    private String epc;

    public DetailsButton(int rowId, String epc) {
        this.rawId = rowId;
        this.epc = epc;
    }

    /**
     * @return the raw
     */
    public int getRaw() {
        return rawId;
    }

    /**
     * @param raw the raw to set
     */
    public void setRaw(int raw) {
        this.rawId = raw;
    }

    /**
     * @return the epc
     */
    public String getEpc() {
        return epc;
    }

    /**
     * @param epc the epc to set
     */
    public void setEpc(String epc) {
        this.epc = epc;
    }
}
