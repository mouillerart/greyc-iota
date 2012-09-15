# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2012  Université de Caen Basse-Normandie, GREYC
#                    		
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
# <http://www.gnu.org/licenses/>
#
# See AUTHORS for a list of contributors.
#
import datetime
from config import CONFIG
import utils
import installer


class ONSConfigurer(installer.Configurer):
    
    def __init__(self):
        installer.Configurer.__init__(self, "Object Name Server", "ons", [
                ("Enter the ONS server name (FDQN)", "ons", "server", {}),
                ("Enter the ONS Domain Prefix", "ons", "domain_prefix", {}),
                ("Enter the ONS Spec Level", "ons", "spec_level", {}),
                ("Enter the ONS DS Entry", "ons", "ds_entry", {}),
                ("Enter the ONS Epcis Entry", "ons", "epcis_entry", {}),
                ("Enter the ONS Spec Entry", "ons", "spec_entry", {}),
                ("Enter the ONS Entry regular expression", "ons", "entry_regex", {}),
                ("Create the zone file template?", "ons", "create_file", {"type":"YN"}),
                ("Enter the Vendor dmain prefix", "ons", "vendor_prefix",
                 {"when": ("ons", "create_file")}),
                ("Enter the information email address (DNS format)", "ons", "email",
                 {"when": ("ons", "create_file")}),
                ("Enter the serving DS URL", "ds", "url",
                 {"when": ("ons", "create_file")}),
                ("Enter the company’s home page URL", "ons", "home_page",
                 {"when": ("ons", "create_file")}),
                ("Enter the output file name", "ons", "filename",
                 {"when": ("ons", "create_file")})
                ])


    def postConfigure(self):
        if CONFIG.get("ons", "create_file"):
            zone = CONFIG.get("ons", "vendor_prefix") + "." + CONFIG.get("ons", "domain_prefix")
            email = CONFIG.get("ons", "email")
            date = datetime.date.today()
            serial = "%04d%02d%02d00" % (date.year, date.month, date.day)
            server = CONFIG.get("ons", "server")
            comurl = CONFIG.get("ons", "home_page")
            dsurl = CONFIG.get("ds", "url")
            utils.writeFile("Creating ONS zone template", CONFIG.get("ons", "filename"),
"""
;; 
$TTL 1d

;; zone
$ORIGIN %s

@ IN SOA localhost %s (
                        %s ; serial
                        3h         ; refresh
                        1h         ; retry
                        1d         ; expire
                        1h         ; cache
                        )

;; this server is the nameserver for this zone
;  IN NS %s.

; NAPTRs for products
;; example product
;;                 order pref flags service    regex                                                   replacement
;2.1.0.9.8  IN NAPTR 0     0    "u"   "epc+html" "!^.*$!%s!"                  .
;           IN NAPTR 1     0    "u"   "epc+ds"   "!^.*$!%s!" .
"""
                            % (zone, email, serial, server, comurl, dsurl)) 
            utils.putWarning("This is just a template. You need to complete it with products ids.")
