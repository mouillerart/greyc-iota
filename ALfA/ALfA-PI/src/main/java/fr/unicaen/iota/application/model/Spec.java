/*
 *  This program is a part of the IoTa Project.
 *
 *  Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
 *  Copyright © 2008-2012  Orange Labs
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
package fr.unicaen.iota.application.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Spec implements Serializable {

    private String epc;
    private Map<String, String> manufacturer;
    private Map<String, String> product;
    private Map<String, String> extension;

    public Spec() {
        super();
        this.epc = "";
        this.manufacturer = new HashMap<String, String>();
        this.product = new HashMap<String, String>();
        this.extension = new HashMap<String, String>();
    }

    public String getEpc() {
        return epc;
    }

    public void setEpc(String epc) {
        this.epc = epc;
    }

    public Map<String, String> getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(Map<String, String> manufacturer) {
        this.manufacturer = manufacturer;
    }

    public Map<String, String> getProduct() {
        return product;
    }

    public void setProduct(Map<String, String> product) {
        this.product = product;
    }

    public Map<String, String> getExtension() {
        return extension;
    }

    public void setExtension(Map<String, String> extension) {
        this.extension = extension;
    }
}
