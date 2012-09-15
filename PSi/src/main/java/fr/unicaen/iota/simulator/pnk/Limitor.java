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

/**
 *
 */
public class Limitor extends Marking {

    private int value;

    public Limitor(Extendable ext) {
        super(ext);
    }

    public Limitor(Extendable e, String value) {
        super(e, value);
        this.value = Integer.parseInt(value);
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
        if (toString().equals("INF")) {
            this.setValue(Integer.MAX_VALUE);
        } else {
            this.setValue(Integer.parseInt(toString()));
        }
    }

    @Override
    public String defaultToString() {
        return "INF";
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
