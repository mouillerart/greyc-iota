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
import installer
from config import CONFIG


class EpcisInstaller(installer.DBWebAppInstaller):

    def __init__(self):
        installer.DBWebAppInstaller.__init__(self, "Fosstrak EPCIS", "epcis", [
                ("Enter the EPCIS web application name", "epcis", "name", {}),
                ("Enter the archive file pathname", "epcis", "repo", {"type": "file"}),
                ("Enter the EPCIS database name", "epcis", "db_name", {}),
                ("Enter the EPCIS database login", "epcis", "db_login", {}),
                ("Enter the EPCIS database password", "epcis", "db_password", {})
                ])


    def postConfigure(self):
        url = ("http://" + CONFIG.get("global", "host") + ":" +
               CONFIG.get("tomcat", "http_port") + "/" + CONFIG.get("epcis", "name"))
        CONFIG.set("epcis", "url", url)
        CONFIG.set("epcis", "query_url", url + "/query")
        CONFIG.set("epcis", "capture_url", url + "/capture")
        CONFIG.set("epcis", "db_jndi", "EPCISDB")
        CONFIG.set("ds", "epcis_query_url", url + "/query")
        CONFIG.set("epcilon", "subscription_url", url + "/query")
