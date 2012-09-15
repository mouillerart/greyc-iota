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
package fr.unicaen.iota.discovery.client.model;

import org.apache.axis2.databinding.types.URI;

public class Service {

    private final String id;
    private final URI uri;
    private final String type;

    public String getId() {
        return id;
    }

    public URI getUri() {
        return uri;
    }

    public String getType() {
        return type;
    }

    public Service(String id, String type, URI uri) {
        super();
        this.id = id;
        this.uri = uri;
        this.type = type;
    }
}
