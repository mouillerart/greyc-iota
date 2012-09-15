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


class ETaInstaller(installer.DBWebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "ETa web application", "eta", [
                ("Enter the ETa web application name", "eta", "name", {}),
                ("Enter the archive file pathname", "eta", "repo", {"type": "file"}),
                ("Enter the Epcis web application URL", "epcis", "url", {}),
                ("Enter the URL to the XACML module", "epcis_policies", "xacml_url", {}),
                ("Enter the ETa database name", "eta", "db_name", {}),
                ("Enter the ETa database login", "eta", "db_login", {}),
                ("Enter the ETa database password", "eta", "db_password", {})
                ], [
                ("application",
                 { "xacml-url": ("epcis_policies", "xacml_url"),
                   "xacml-ihm-url": ("ephi", "url"),
                   "epcis-query-url": ("epcis", "query_url"),
                   "epcis-capture-url": ("epcis", "capture_url"),
                   "eta-userservice-url": ("eta", "userservice_url"),
                   "ldap-url": ("ldap", "url"),
                   "ldap-basedn": ("ldap", "base_dn"),
                   "ldap-user": ("ldap", "login"),
                   "ldap-password": ("ldap", "password"), })
                ] )


    def postConfigure(self):
        # set default url (for User web service)
        url = "http://" + CONFIG.get("global", "host") + ":" + CONFIG.get("tomcat", "http_port") + "/" + CONFIG.get("eta", "name")
        CONFIG.set("eta", "userservice_url", url + "/" + CONFIG.get("eta", "userservice_name"))
        CONFIG.set("eta", "db_jndi", "ETADB")
        CONFIG.set("ds", "epcis_query_url", url + "/query")
        CONFIG.set("epcilon", "subscription_url", url + "/query")
