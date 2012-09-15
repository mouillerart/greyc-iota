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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 */
public class EPCInscription extends Marking {

    private int canalSize;
    private List<EPC> epcList;

    public EPCInscription(Extendable ext) {
        super(ext);
        epcList = new ArrayList<EPC>();
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
    public boolean contains(Marking marking) {
        return true;
    }

    @Override
    protected void localAdd(Marking marking) {
    }

    @Override
    protected void localSub(Marking marking) {
    }

    @Override
    public void checkContextAndParse() {
        epcList = new ArrayList<EPC>();
        String str = toString();
        setCanalSize(Integer.parseInt(str.trim()));
    }

    public int getCanalSize() {
        return canalSize;
    }

    public void setCanalSize(int canalSize) {
        this.canalSize = canalSize;
    }

    public List<EPC> getEpcList() {
        return epcList;
    }

    public void addEPC(EPC epc) {
        epcList.add(epc);
    }

    public void setEpcList(Collection<EPC> epcList) {
        this.epcList.clear();
        this.epcList.addAll(epcList);
    }

    public void clearEpcList() {
        this.epcList.clear();
    }

    public void addEpcList(Collection<EPC> list) {
        epcList.addAll(list);
    }

    @Override
    public String defaultToString() {
        return "1";
    }
}
