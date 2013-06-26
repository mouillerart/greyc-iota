/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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

/** Minumum width and height of the map window */
var minBoundsSize = 10; // in degrees

var usepopup = false;

var map, select;
var gamma_layer;
var pEPSG4326 = new OpenLayers.Projection("EPSG:4326");
var pEPSG900913 = new OpenLayers.Projection("EPSG:900913");

function gamma_init() {
    map = new OpenLayers.Map ("map", {
        controls:[
            new OpenLayers.Control.Navigation(),
            new OpenLayers.Control.PanPanel(),
            new OpenLayers.Control.LayerSwitcher(),
            new OpenLayers.Control.Attribution(),
            // new OpenLayers.Control.Permalink(),
            new OpenLayers.Control.ScaleLine(),
            new OpenLayers.Control.OverviewMap(),
            new OpenLayers.Control.MousePosition()],
        maxResolution: 156543.0399,
        numZoomLevels: 19,
        // units: 'm',
        projection: new OpenLayers.Projection("EPSG:900913"),
        displayProjection: new OpenLayers.Projection("EPSG:4326")
    } );

    var mapnik = new OpenLayers.Layer.OSM("OpenStreetMap (Mapnik)");

    map.addLayer(mapnik);

    gamma_layer = new OpenLayers.Layer.Vector("Overlay", {
        styleMap: new OpenLayers.StyleMap({
            "default": new OpenLayers.Style({
                graphicName: "circle",
                pointRadius: 5,
                fillOpacity: 0.5,
                fillColor: "#ffcc66",
                strokeColor: "#666633",
                strokeWidth: 1
            })
        })
    });
    map.addLayer(gamma_layer);

    // select
    var select_layer = gamma_layer;
    select = new OpenLayers.Control.SelectFeature(select_layer);

    select_layer.events.on({
        "featureselected": onFeatureSelect,
        "featureunselected": onFeatureUnselect
    });

    map.addControl(select);
    select.activate();

}

function addPoint(layer, loc, ids) {
    var ll = loc.split(",");
    var p = new OpenLayers.Geometry.Point(parseInt(ll[0], 10), parseInt(ll[1], 10));
    p.transform(pEPSG4326, pEPSG900913);
    var feature = new OpenLayers.Feature.Vector(p);
    feature.attributes.id = ids;
    layer.addFeatures(feature);
}

function onPopupClose(evt) {
    select.unselectAll();
}

function onFeatureSelect(event) {
    var feature = event.feature;
    var ids = feature.attributes.id.split(" ");
    for (var i = ids.length-1; i >=0; --i)
        hilightElementById(ids[i]);
    if (usepopup) {
        var popup = new OpenLayers.Popup.FramedCloud("chicken",
                                                     feature.geometry.getBounds().getCenterLonLat(),
                                                     new OpenLayers.Size(100,100),
                                                     "<h2>"+feature.attributes.name + "</h2>" + feature.attributes.description,
                                                     null, true, onPopupClose
                                                    );
        feature.popup = popup;
        map.addPopup(popup);
    }
}

function onFeatureUnselect(event) {
    var feature = event.feature;
    var ids = feature.attributes.id.split(" ");
    for (var i = ids.length-1; i >=0; --i)
        unhilightElementById(ids[i]);
    if(feature.popup) {
        map.removePopup(feature.popup);
        feature.popup.destroy();
        delete feature.popup;
    }
}
function hilightElementById(id) {
    var div = document.getElementById(id);
    if (div != null) {
        div.className += " selected";
        window.location = "#"+id;
    }
}
function unhilightElementById(id) {
    var div = document.getElementById(id);
    if (div != null)
        div.className = div.className.replace(" selected", "");
}

function getLocationExtension(elems) {
    for (var i = elems.length-1; i >=0; --i) {
        if (elems[i].className == "queryEpcis event extensions"
                || elems[i].className == "trace event extensions") {
            // we suppose span extensionValue follows immediatetly span extensionName
            var l = elems[i].children.length;
            for (var j = 0; j < l; ++j) {
                if (elems[i].children[j].textContent == "http://www.opengis.net/kml/2.2##location")
                    return  elems[i].children[j+1].textContent;
            }
        }
    }
}

/**
 * This function :
 * adds to the gamma_layer of the map :
 *    - a lineString figuring the route defined by the events
 *    - a point for each different location
 * adds to each div of class className, a clickable div
 * that will highlight the location of the event on the companion map
 */
function initShowOnMap(className) {
    /** locations and id of the div of class className */
    var locationsAndIds = [];
    /** the div of class className */
    var elems = document.getElementsByClassName(className);
    for (var i = 0; i < elems.length; i++) {
        var children = elems[i].children;
        if (elems[i].children.length > 0) {
            var loc = getLocationExtension(elems[i].children);
            if (loc != null)
                locationsAndIds.push({loc: loc, id: elems[i].id});
            var newDiv = document.createElement("div");
            newDiv.className = "event showOnMap display";
            newDiv.setAttribute("onclick", "showOnMap(this)");
            newDiv.textContent = " [Show on Map] ";
            elems[i].insertBefore(newDiv, elems[i].firstChild);
        }
    }
    // add route and init bounds
    var points = [];
    var bounds = null;
    for (var k = 0; k < locationsAndIds.length; ++k) {
        var ll = locationsAndIds[k].loc.split(",");
        var lon = parseInt(ll[0], 10);
        var lat = parseInt(ll[1], 10);
        var p = new OpenLayers.Geometry.Point(lon, lat);
        if (bounds == null)
            bounds = new OpenLayers.Bounds(p.x, p.y, p.x, p.y);
        else
            bounds.extend(p);
        p.transform(pEPSG4326, pEPSG900913);
        points.push(p);
    }
    var feature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.LineString(points));
    gamma_layer.addFeatures(feature);

    // extend bounds width and height to minBoundsSize
    if (bounds.getWidth() < minBoundsSize) {
        bounds.extend(new OpenLayers.Geometry.Point(bounds.left-minBoundsSize/2, bounds.top));
        bounds.extend(new OpenLayers.Geometry.Point(bounds.right+minBoundsSize/2, bounds.top));
    }
    if (bounds.getHeight() < minBoundsSize) {
        bounds.extend(new OpenLayers.Geometry.Point(bounds.left, bounds.bottom-minBoundsSize/2));
        bounds.extend(new OpenLayers.Geometry.Point(bounds.left, bounds.top+minBoundsSize/2));
    }
    map.zoomToExtent(bounds.transform(map.displayProjection, map.projection));

    // add points
    locationsAndIds.sort(function(a, b){
        var locA=a.loc, locB=b.loc;
        if (locA < locB) //sort string ascending
            return -1;
        if (locA > locB)
            return 1;
        return 0; //default return value (no sorting)
    });
    var currentloc = "", currentids = "";
    for (var k = 0; k < locationsAndIds.length; ++k) {
        if (locationsAndIds[k].loc == currentloc) {
            // same location : record id and continue
            currentids += " " + locationsAndIds[k].id;
            continue;
        }
        if (currentloc != "") // add geom
            addPoint(gamma_layer, currentloc, currentids);
        // record new loc and id
        currentloc = locationsAndIds[k].loc;
        currentids = locationsAndIds[k].id;
    }
    if (currentloc != "") // add last geom
        addPoint(gamma_layer, currentloc, currentids);

}

function getById(vector, id) {
    for(var i = vector.length -1; i >= 0; --i) {
        var ids = vector[i].attributes.id.split(" ");
        for (var k = ids.length-1; k >=0; --k)
            if (ids[k] == id)
                return vector[i];
    }
    return null;
}

function showOnMap(evtElem) {
    var feature = getById(gamma_layer.features, evtElem.parentElement.id);
    if (feature == null)
        return;
    select.unselectAll();
    select.select(feature);
}
