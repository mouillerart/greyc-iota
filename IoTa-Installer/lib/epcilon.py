# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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
import utils
import installer


class EpcILoNInstaller(installer.DBWebAppInstaller):

    def __init__(self):
        installer.DBWebAppInstaller.__init__(self, "EpcILoN web application", "epcilon", [
                ("Enter the EpcILoN web application name", "epcilon", "name", {}),
                ("Enter the archive file pathname", "epcilon", "repo", {"type": "file"}),
                ("Enter this application’s identity", "epcilon", "identity", {}),
                ("Enter the EpcILoN database name", "epcilon", "db_name", {}),
                ("Enter the EpcILoN database login", "epcilon", "db_login", {}),
                ("Enter the EpcILoN database password", "epcilon", "db_password", {}),
                ("Enter the URL to the Epcis (or ETa) Query service", "epcilon", "subscription_url", {}),
                ("Enter the URL to the DS (or DSeTa)", "epcilon", "ds_url", {})
                # EpcILoN is only a DSeTa client for now
                # ("Enter the DS client login (not used by DSeTa)", "ds", "login", {}),
                # ("Enter the DS client password (not used by DSeTa)", "ds", "password", {})
                ], [
                ("application",
                 { "query-callback-address": ("epcilon", "callback_url"),
                   "publish": "true",
                   "pks-filename": ("cert", "jks_keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword"),
                   "query-client-address": ("epcilon", "subscription_url"),
                   "discovery-service-address": ("epcilon", "ds_url") })
                   #"login": ("ds", "login"),
                   #"password": ("ds", "password") })
                ])


    def postConfigure(self):
        self.setURL()
        self.cset("callback_url", self.cget("url") + "StandingQueryCallbackServlet")
        self.cset("db_jndi", "EPCILONDB")


    def postInstall(self):
        installer.DBWebAppInstaller.postInstall(self)
        utils.putWait("Subscribing to the Epcis")
        url = self.cget("url") + "SubscribedServlet"
        if utils.sh_exec("wget -qO /dev/null " + url):
            utils.putDoneOK()
        else:
            utils.putDoneFail()
