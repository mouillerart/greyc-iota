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
import fr.unicaen.iota.simulator.app.Trash;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class EPCDeactivator extends Marking {

    private boolean active = false;

    public EPCDeactivator(Extendable ext) {
        super(ext);
    }

    public EPCDeactivator(Extendable e, String value) {
        super(e, value);
    }

    @Override
    public boolean contains(Marking marking) {
        return active;
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
        String str = toString();
        active = Boolean.parseBoolean(str);
    }

    public boolean isDeactivator() {
        return active;
    }

    public void setActivator(boolean active) {
        this.active = active;
    }

    @Override
    public String defaultToString() {
        return "deactivator";
    }

    void sendToTrash(Collection<EPC> epcList) {
        List<String> list = formatList(epcList);
        Trash.getInstance().addEPCToTrash(list);
    }

    private List<String> formatList(Collection<EPC> epcList) {
        List<String> res = new ArrayList<String>();
        for (EPC epc : epcList) {
            res.add(epc.getEpc());
        }
        return res;
    }
}
