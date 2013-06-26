/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *  <http://www.gnu.org/licenses/>
 *
 *  See AUTHORS for a list of contributors.
 */
package fr.unicaen.iota.application.soap.client;

import fr.unicaen.iota.application.soap.IoTaException;

/**
 *
 */
public enum IoTaFault {

    unknown(0, "Error"),
    ons(0x100, "ONS Error"),
    ds(0x200, "DS Error"),
    epcis(0x400, "EPCIS Error"),
    alfa(0x700, "Access Layer Error"),
    tau(0x800, "Identity Error");
    private final int code;
    private final String explanation;

    private IoTaFault(int code, String msg) {
        this.code = code;
        this.explanation = msg;
    }

    public int getCode() {
        return code;
    }

    public String getExplanation() {
        return explanation;
    }

    public static String explain(IoTaException ex) {
        for (IoTaFault fault : values()) {
            if (fault.getCode() == ex.getFaultInfo()) {
                return fault.getExplanation();
            }
        }
        return "Unknown error";
    }
}
