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


class ETaInstaller(installer.DBWebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "ETa web application", "eta", [
                ("Enter the ETa web application name", "eta", "name", {}),
                ("Enter the archive file pathname", "eta", "repo", {"type": "file"}),
                ("Enter the ETa database name", "eta", "db_name", {}),
                ("Enter the ETa database login", "eta", "db_login", {}),
                ("Enter the ETa database password", "eta", "db_password", {}),
                ("Do you want to create the database callback management user", "eta", "db_user_create", {"type": "YN"}),
                ("Enter the database login for callback management user", "eta", "callback_db_login", {}),
                ("Enter the database password for callaback management user", "eta", "callback_db_password", {}),
                ("Enter the Epcis web application URL", "epcis", "url", {}),
                ("Enter the URL to the XACML module", "epcis_policies", "xacml_url", {}),
                ("Enter the URL to the Callback Receiver module", "eta_callback_receiver", "callback_url", {}),
                ("Do you use electronic signatures (SigMa)", "eta", "use_sigma", {"type": "YN"}),
                ], [
                ("application",
                 { "xacml-url": ("epcis_policies", "xacml_url"),
                   "xacml-ihm-url": ("ephi", "url"),
                   "xacml-default-user": ("global", "anonymous_user"),
                   "epcis-query-url": ("epcis", "query_url"),
                   "epcis-capture-url": ("epcis", "capture_url"),
                   "eta-userservice-url": ("user", "url"),
                   "eta-callback-url": ("eta_callback_receiver", "callback_url"),
                   "ldap-url": ("ldap", "url"),
                   "ldap-basedn": ("ldap", "base_dn"),
                   "ldap-user": ("ldap", "login"),
                   "ldap-password": ("ldap", "password"),
                   "sigma-url": ("sigma", "url"),
                   "sigma-verification": ("eta", "use_sigma"),})
                ] )


    def postConfigure(self):
        self.setURL()
        self.cset("db_jndi", "ETADB")
        url = self.cget("url")
        CONFIG.set("ds", "epcis_type", "ided_epcis")
        CONFIG.set("ds", "epcis_query_url", url + "ided_query")
        CONFIG.set("epcilon", "subscription_url", url + "query")
        # configure database connection for callbacks
        murl = "jdbc:mysql://" + CONFIG.get("db", "host") + ":" + CONFIG.get("db", "port") + "/" + self.cget("db_name") + "?autoReconnect=true"
        self.cset("callback_db_url", murl)


    def postInstall(self):
        if self.cisTrue("db_user_create"):
            utils.putMessage("Creating callback management user ...")
            if not utils.execDB("Granting access rights", "mysql",
                                "GRANT SELECT ON " + self.cget("db_name") + ".subscription " +
                                "TO '" + self.cget("callback_db_login") + "'@'" + CONFIG.get("db", "user_host") + "' " +
                                "IDENTIFIED BY '" + self.cget("callback_db_password") + "';"):
                return
