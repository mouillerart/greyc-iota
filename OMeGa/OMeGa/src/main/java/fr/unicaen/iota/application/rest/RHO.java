/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.application.rest;

import fr.unicaen.iota.application.ALfA;
import fr.unicaen.iota.application.AccessInterface;
import fr.unicaen.iota.application.Configuration;

/**
 *
 */
public class RHO extends BaseRHO {

    @Override
    protected AccessInterface getControler() {
        return new ALfA(Configuration.PKS_FILENAME, Configuration.PKS_PASSWORD, Configuration.TRUST_PKS_FILENAME, Configuration.TRUST_PKS_PASSWORD);
    }
}
