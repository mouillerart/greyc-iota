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
from config import CONFIG
import installer


class DSeTaInstaller(installer.WebAppInstaller):
    
    def __init__(self):
        installer.WebAppInstaller.__init__(self, "DSeTa Web Server", "dseta", [
                ("Enter the DSeTa web application name", "dseta", "name", {}),
                ("Enter the archive file pathname", "dseta", "repo", {"type": "file"}),
                ("Enter the server identity (sgln) (same as actual DS)", "ds", "server_identity", {}),
                ("Enter the URL of the actual DS", "ds", "url", {}),
                ("Enter your DS login for the actual DS", "dseta", "ds_login", {}),
                ("Enter your DS password for the actual DS", "dseta", "ds_password", {})
                ], [
                ("application",
                 { "service-id": ("ds", "server_identity"),
                   "ons": ("ons", "server"),
                   "ons-domain-prefix": ("ons", "domain_prefix"),
                   "wings-login": ("dseta", "ds_login"),
                   "wings-password": ("dseta", "ds_password"),
                   "wings-url": ("ds", "url"),
                   "xacml-default-user": ("global", "anonymous_user") })
                ])


    def postConfigure(self):
        self.setURL()
        CONFIG.set("epcilon", "ds_url", self.cget("url") + "ided_ds")
