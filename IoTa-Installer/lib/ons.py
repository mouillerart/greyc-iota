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
        if self.cget("create_file"):
            zone = self.cget("vendor_prefix") + "." + self.cget("domain_prefix")
            email = self.cget("email")
            date = datetime.date.today()
            serial = "%04d%02d%02d00" % (date.year, date.month, date.day)
            server = self.cget("server")
            comurl = self.cget("home_page")
            dsurl = CONFIG.get("ds", "url")
            dsetaurl = CONFIG.get("dseta", "url") + "ided_ds/"
            utils.writeFile("Creating ONS zone template", self.cget("filename"),
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
;           IN NAPTR 2     0    "u"   "epc+ided_ds"   "!^.*$!%s!" .
"""
                            % (zone, email, serial, server, comurl, dsurl, dsetaurl)) 
            utils.putWarning("This is just a template. You need to complete it with products ids.")
