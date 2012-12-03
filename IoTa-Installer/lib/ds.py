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
import utils


class DSInstaller(installer.DBWebAppInstaller):
    
    def __init__(self):
        installer.DBWebAppInstaller.__init__(self, "Discovery Web Server", "ds", [
                ("Enter the DS web application name", "ds", "name", {}),
                ("Enter the archive file pathname", "ds", "repo", {"type": "file"}),
                ("Enter the server identity (sgln)", "ds", "server_identity", {}),
                ("Enter the DS database name", "ds", "db_name", {}),
                ("Enter the DS database login", "ds", "db_login", {}),
                ("Enter the DS database password", "ds", "db_password", {}),
                ("Enter the URL to the XACML module", "ds_policies", "xacml_url", {}),
                ("Use as multi DS instance?", "publisher", "multi_ds_architecture",
                 {"type": "YN"}),
                ("Enter your DS login for publisher", "publisher", "login",
                 {"when": ("publisher", "multi_ds_architecture")}),
                ("Enter your DS password for publisher", "publisher", "password",
                 {"when": ("publisher", "multi_ds_architecture")}),
                ("Enter the URL of the Epcis Query service (or ETa)", "ds", "epcis_query_url", {})
                ], [
                ("application",
                 { "service-id": ("ds", "server_identity"),
                   "ons": ("ons", "server"),
                   "ons-domain-prefix": ("ons", "domain_prefix"),
                   "xacml-url": ("ds_policies", "xacml_url"),
                   "xacml-ihm-url": ("dphi", "url") }),
                ("publisher",
                 { "multi-ds-architecture": ("publisher", "multi_ds_architecture"),
                   "ds-login": ("publisher", "login"),
                   "ds-password": ("publisher", "password") })
                ])


    def postConfigure(self):
        self.setURL()
        self.cset("url", self.cget("url") + "services/ESDS_Service")
        self.cset("db_jndi", "DSDB")


    def postUnpack(self):
        if self.cisTrue("db_install"):
            utils.execDB("Setting Anonymous partner’s service address", self.cget("db_name"),
                         "UPDATE partner SET serviceType='" + self.cget("epcis_type") +
                         "', serviceAddress='" + self.cget("epcis_query_url") +
                         "' WHERE partnerID='anonymous'")
        # note: this 'anonymous' should be the Epcis/EpcILoN identity
