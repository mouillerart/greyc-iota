/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
package fr.unicaen.iota.mu;

import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import org.fosstrak.epcis.model.*;
import org.w3c.dom.Element;

/**
 *
 */
public class ExtensionTypeHelper {

    private final List<Object> any;
    private final Map<QName, String> otherAttributes;

    public ExtensionTypeHelper(EPCISEventExtensionType extension) {
        if (extension == null) {
            any = null;
            otherAttributes = null;
        } else {
            any = (extension.getAny() != null) ? extension.getAny() : null;
            otherAttributes = (extension.getOtherAttributes() != null) ? extension.getOtherAttributes() : null;
        }
    }

    public ExtensionTypeHelper(ObjectEventExtensionType extension) {
        if (extension == null) {
            any = null;
            otherAttributes = null;
        } else {
            any = (extension.getAny() != null) ? extension.getAny() : null;
            otherAttributes = (extension.getOtherAttributes() != null) ? extension.getOtherAttributes() : null;
        }
    }

    public ExtensionTypeHelper(AggregationEventExtensionType extension) {
        if (extension == null) {
            any = null;
            otherAttributes = null;
        } else {
            any = (extension.getAny() != null) ? extension.getAny() : null;
            otherAttributes = (extension.getOtherAttributes() != null) ? extension.getOtherAttributes() : null;
        }
    }

    public ExtensionTypeHelper(QuantityEventExtensionType extension) {
        if (extension == null) {
            any = null;
            otherAttributes = null;
        } else {
            any = (extension.getAny() != null) ? extension.getAny() : null;
            otherAttributes = (extension.getOtherAttributes() != null) ? extension.getOtherAttributes() : null;
        }
    }

    public ExtensionTypeHelper(TransactionEventExtensionType extension) {
        if (extension == null) {
            any = null;
            otherAttributes = null;
        } else {
            any = (extension.getAny() != null) ? extension.getAny() : null;
            otherAttributes = (extension.getOtherAttributes() != null) ? extension.getOtherAttributes() : null;
        }
    }

    public List<Object> getAny() {
        return any;
    }

    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

    public String getAny(String name) {
        if (any != null) {
            for (Object obj : any) {
                if (obj instanceof Element) {
                    Element el = (Element) obj;
                    if (el.getLocalName().equals(name)) {
                        return el.getTextContent();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Return the first extension (attribute or any) with the given name as
     * localpart of its QName.
     *
     * @param name extension to look for
     * @return the string value for the first found extension
     */
    public String getExtension(String name) {
        if (otherAttributes != null) {
            for (Map.Entry<QName, String> attr : otherAttributes.entrySet()) {
                if (attr.getKey().getLocalPart().equals(name)) {
                    return attr.getValue();
                }
            }
        }
        return getAny(name);
    }
}
