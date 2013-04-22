/*
 *  This program is a part of the IoTa project.
 *
 *  Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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

// Hides the elements with the css property "display" defined to "block", if the Web browser use Javascript.
function initToggleDisplay(className) {
    var elems = document.getElementsByClassName(className);
    for (var i = 0; i < elems.length; i++) {
        var children = elems[i].children;
        for (var j = 0; j < children.length; j++) {
            if (!children[j].style) {
                continue;
            }
            var displayValue = children[j].style.display;
            if (!displayValue || displayValue == "") {
                displayValue = children[j].currentStyle ? children[j].currentStyle.display :
                    getComputedStyle(children[j], null).display;
            }
            if (displayValue == "block") {
                hideElem(children[j]);
            }
        }
        if (elems[i].children.length > 0) {
            var newDiv = document.createElement("div");
            newDiv.className = "event toggleDisplay display";
            newDiv.setAttribute("onclick", "toggleDisplayChildren(this)");
            newDiv.textContent = "Details";
            elems[i].insertBefore(newDiv, elems[i].firstChild);
        }
    }
}

// Toggle display of the child elements: "block" <-> "none". The display "inline" is not modified.
function toggleDisplayChildren(elem) {
    var children = elem.parentNode.children;
    for (var i = 0; i < children.length; i++) {
        if (hasClass(children[i], "event") &&  hasClass(children[i], "toggleDisplay")) {
            if (hasClass(children[i], "hide")) {
                children[i].className = "event toggleDisplay display";
                children[i].textContent = "Details";
            }
            else if (hasClass(children[i], "display")) {
                children[i].className = "event toggleDisplay hide";
                children[i].textContent = "Summary";
            }
            continue;
        }
        if (!children[i].style) {
            continue;
        }
        var displayValue = children[i].style.display;
        if (!displayValue || displayValue == "") {
            displayValue = children[i].currentStyle ? children[i].currentStyle.display :
                getComputedStyle(children[i], null).display;
        }
        if (displayValue == "none") {
            displayElem(children[i]);
        }
        else if (displayValue == "block") {
            hideElem(children[i]);
        }
    }
}

function toggleDisplayServiceURL(select) {
    var serviceElem = document.getElementById("serviceURL");
    var signatureElem = document.getElementById("signature");
    if (select == "all") {
        hideElem(serviceElem);
        displayElem(signatureElem);
    }
    else {
        displayElem(serviceElem);
        if (select == "epcis") {
            displayElem(signatureElem);
        }
        else {
            hideElem(signatureElem);
        }
    }
}

function displayElem(elem) {
    elem.style.display = "block";
}

function displayId(id) {
    var elem = document.getElementById(id);
    displayElem(elem);
}

function hideElem(elem) {
    elem.style.display = "none";
}

function hideId(id) {
    var elem = document.getElementById(id);
    hideElem(elem);
}

// Replaces classList.contains for cross-browser compatibility
function hasClass(element, cls) {
    return (' ' + element.className + ' ').indexOf(' ' + cls + ' ') > -1;
}
