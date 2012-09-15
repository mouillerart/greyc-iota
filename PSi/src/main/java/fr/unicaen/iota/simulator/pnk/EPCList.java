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
public class EPCList extends Marking {

    private List<EPC> epcList;
    private boolean generator = false;

    public EPCList(Extendable ext) {
        super(ext);
        epcList = new ArrayList<EPC>();
    }

    @Override
    public boolean contains(Marking marking) {
        EPCInscription epcInscription = (EPCInscription) marking;
        return epcList.size() >= epcInscription.getCanalSize();
    }

    @Override
    protected void localAdd(Marking marking) {
        EPCInscription epcInscription = (EPCInscription) marking;
        epcList.addAll(epcInscription.getEpcList());
        epcInscription.clearEpcList();
        updateValue2();
    }

    @Override
    protected void localSub(Marking marking) {
        EPCInscription epcInscription = (EPCInscription) marking;
        for (int i = 0; i < epcInscription.getCanalSize(); i++) {
            EPC epc = epcList.get(i);
            epcInscription.addEPC(epc);
        }
        for (int i = 0; i < epcInscription.getCanalSize(); i++) {
            epcList.remove(0);
        }
        updateValue2();
    }

    public void updateValue2() {
        setExternState(epcListToString());
        updateValue();
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

    public List<EPC> getEpcList() {
        return epcList;
    }

    public void setEpcList(Collection<EPC> epcList) {
        this.epcList.clear();
        this.epcList.addAll(epcList);
    }

    @Override
    public void checkContextAndParse() {
        epcList.clear();
        String str = toString();
        List<EPC> list = selectSubstrings(str, " ,;\n");
        if (!list.isEmpty()) {
            this.epcList.addAll(list);
        }
    }

    private String epcListToString() {
        StringBuilder result2 = new StringBuilder();
        for (EPC epc : epcList) {
            result2.append(epc);
            result2.append("\n");
        }
        return result2.toString();
    }

    private List<EPC> selectSubstrings(String str, String delimiters) {
        List<EPC> list = new ArrayList<EPC>();
        int l = str.length();
        int i = 0;
        while (i < l) {
            while (i < l) {
                if (delimiters.indexOf(str.charAt(i)) < 0) {
                    break; // Zeichen an pos i kein Delimiter
                }
                i++;
            }
            // Markenende bestimmen
            if (i >= l) {
                break; // Ende erreicht
            }
            int start = i;
            // look for tuples
            if (str.charAt(i) == '(') {
                i++;
                while (i < l - 1 && str.charAt(i) != ')') {
                    i++;
                }
                i++;
                list.add(new EPC(str.substring(start, i)));
            } else { // lock for single token
                for (; i < l; i++) {
                    if (delimiters.indexOf(str.charAt(i)) >= 0) {
                        break; // char at i is delimiter
                    }
                }
                list.add(new EPC(str.substring(start, i)));
            }
        }
        return list;
    }

    public boolean isGenerator() {
        return generator;
    }

    public void setGenerator(boolean generator) {
        this.generator = generator;
    }

    public void addEPC(String generatedEPC) {
        epcList.add(new EPC(generatedEPC));
    }

    void loadPipe(Pipe pipe) {
        for (String s : pipe.loadPipe()) {
            this.addEPC(s);
        }
    }
}
