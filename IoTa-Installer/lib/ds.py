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
                ("Enter the hostname of XACML module (socket)", "ds_policies", "xacml_host", {}),
                ("Enter the port of the XACML module (socket)", "ds_policies", "xacml_port", {}),
                ("Enter the URL to the XACML module", "ds_policies", "xacml_url", {}),
                ("Use as multi DS instance?", "publisher", "multi_ds_architecture",
                 {"type": "YN"}),
                ("Enter one or several ONS IP address(es) (comma separated)",
                 "publisher", "ons_hosts",
                 {"when": ("publisher", "multi_ds_architecture")}),
                ("Enter your DS LOGIN for publisher", "publisher", "login",
                 {"when": ("publisher", "multi_ds_architecture")}),
                ("Enter your DS PASSWORD for publisher", "publisher", "password",
                 {"when": ("publisher", "multi_ds_architecture")}),
                ("Enter the URL of the Epcis Query service (or ETa)", "ds", "epcis_query_url", {})
                ], [
                ("application",
                 { "service-id": ("ds", "server_identity"),
                   "xacml-address": ("ds_policies", "xacml_host"),
                   "xacml-port": ("ds_policies", "xacml_port"),
                   "xacml-ihm-url": ("dphi", "url") }),
                ("publisher",
                 { "multi-ds-architecture": ("publisher", "multi_ds_architecture"),
                   "ons-hosts": ("publisher", "ons_hosts"),
                   "ds-login": ("publisher", "login"),
                   "ds-password": ("publisher", "password")
                   })
                ])


    def postConfigure(self):
        CONFIG.set("ds", "url",
                   "http://" + CONFIG.get("global", "host") + ":" + CONFIG.get("tomcat", "http_port") + "/" +
                   CONFIG.get("ds", "name") + "/services/ESDS_Service")
        CONFIG.set("ds", "db_jndi", "DSDB")


    def postUnpack(self):
        if CONFIG.isTrue("ds", "db_install"):
            utils.execDB("Setting Anonymous partner’s service address", CONFIG.get("ds", "db_name"),
                         "UPDATE partner SET serviceAddress='" + CONFIG.get("ds", "epcis_query_url") + "' WHERE partnerID='anonymous'")
