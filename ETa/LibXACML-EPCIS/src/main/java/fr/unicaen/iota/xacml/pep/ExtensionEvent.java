/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2011-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2011       Orange Labs
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
package fr.unicaen.iota.xacml.pep;

public class ExtensionEvent {

    private String extensionId;
    private Object extensionValue;

    public ExtensionEvent(String extensionId, Object extensionValue) {
        this.extensionId = extensionId;
        this.extensionValue = extensionValue;
    }

    public ExtensionEvent(String namespace, String extensionName, Object extensionValue) {
        this.extensionId = namespace + "#" + extensionName;
        this.extensionValue = extensionValue;
    }

    public String getExtensionId() {
        return extensionId;
    }

    public Object getExtensionValue() {
        return extensionValue;
    }
}
