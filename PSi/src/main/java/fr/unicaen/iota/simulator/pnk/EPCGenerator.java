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
import fr.unicaen.iota.simulator.util.KeyGen;

/**
 *
 */
public class EPCGenerator extends Marking {

    private boolean generator = false;
    private String epcBase = "";
    private int epcCount = 0;
    private boolean useKeyGen = true;
    private boolean checked = false;

    public boolean useKeyGen() {
        return useKeyGen;
    }

    public EPCGenerator(Extendable ext) {
        super(ext);
    }

    public EPCGenerator(Extendable e, String value) {
        super(e, value);
    }

    @Override
    public boolean contains(Marking marking) {
        if (!isGenerator()) {
            return false;
        }
        if (!useKeyGen) {
            return false;
        }
        if (epcCount == -1) {
            return true;
        }
        EPCInscription epcInscription = (EPCInscription) marking;
        return epcCount >= epcInscription.getCanalSize();
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
        if (!checked) {
            checked = true;
            String[] params = toString().split("%");
            switch (params.length) {
                case 2:
                    generator = true;
                    epcBase = params[0];
                    epcCount = "INF".equals(params[1]) ? -1 : Integer.parseInt(params[1]);
                    useKeyGen = true;
                    break;
                case 3:
                    generator = true;
                    epcBase = params[0];
                    epcCount = "INF".equals(params[1]) ? -1 : Integer.parseInt(params[1]);
                    useKeyGen = Boolean.parseBoolean(params[2]);
                    break;
                default:
                    generator = false;
                    break;
            }
        }
    }

    public boolean isGenerator() {
        if (!checked) {
            checkContextAndParse();
        }
        return generator;
    }

    public void setGenerator(boolean generator) {
        this.generator = generator;
    }

    @Override
    public String defaultToString() {
        return "generator";
    }

    public void generate(EPCList mPlace) {
        if (epcCount == 0) {
            return;
        }
        if (epcCount > 0) {
            epcCount--;
        }
        String code = KeyGen.generateEPC(epcBase);
        mPlace.addEPC(code);
    }
}
