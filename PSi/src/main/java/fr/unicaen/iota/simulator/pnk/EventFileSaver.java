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
package fr.unicaen.iota.simulator.pnk;

import de.huberlin.informatik.pnk.kernel.Extendable;
import de.huberlin.informatik.pnk.netElementExtensions.base.Marking;
import fr.unicaen.iota.simulator.app.EPC;

/**
 *
 */
public class EventFileSaver extends Marking {

    private boolean on;

    public EventFileSaver(Extendable ext) {
        super(ext);
    }

    public EventFileSaver(Extendable e, String value) {
        super(e, value);
        this.on = Boolean.parseBoolean(value);
    }

    @Override
    public boolean contains(Marking marking) {
        return false;
    }

    @Override
    protected void localAdd(Marking marking) {
    }

    @Override
    protected void localSub(Marking marking) {
    }

    @Override
    protected boolean isValid() {
        return true;
    }

    @Override
    protected boolean isValid(Extendable extendable) {
        return true;
    }

    @Override
    protected boolean isValid(String state) {
        return true;
    }

    @Override
    public void checkContextAndParse() {
        on = "ON".equals(this.toString());
    }

    @Override
    public String defaultToString() {
        return "OFF";
    }

    /**
     * @return the on
     */
    public boolean isOn() {
        return on;
    }

    /**
     * @param on the on to set
     */
    public void setOn(boolean on) {
        this.on = on;
    }

    public void save(Marking marking) {
        EPCInscription epcInscription = (EPCInscription) marking;
        for (EPC epc : epcInscription.getEpcList()) {
            epc.save();
        }
    }
}
