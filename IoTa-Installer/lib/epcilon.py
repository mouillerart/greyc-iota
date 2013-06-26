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
                ("Enter the EpcILoN database name", "epcilon", "db_name", {}),
                ("Enter the EpcILoN database login", "epcilon", "db_login", {}),
                ("Enter the EpcILoN database password", "epcilon", "db_password", {}),
                ("Use IoTa IDed (ETa and DSeTa) or not (Epcis and DS)?", "epcilon", "iota_ided", {"type": "YN"}),
                ("Enter the URL to the Epcis (or ETa) Query service", "epcilon", "subscription_url", {}),
                ("Enter the URL to the DS (or DSeTa)", "epcilon", "ds_url", {}),
                ("Enter this application’s identity", "epcilon", "identity",
                 {"when": ("epcilon", "iota_ided")}),
                ("Enter the keystore file name (PEM format)", "cert", "pem_keystore",
                 {"when": ("epcilon", "iota_ided")}),
                ("Enter the keystore password", "cert", "password",
                 {"when": ("epcilon", "iota_ided")}),
                ("Enter the truststore file name (PEM format)", "cert", "pem_truststore",
                 {"when": ("epcilon", "iota_ided")}),
                ("Enter the publisher frequency (time between each launch)", "epcilon", "publisher_frequency", {}),
                ("Enter the time before another try to publish", "epcilon", "publisher_pending_republish", {})
                ], [
                ("application",
                 { "publish": "true",
                   "query-callback-address": ("epcilon", "callback_url"),
                   "query-client-address": ("epcilon", "subscription_url"),
                   "discovery-service-address": ("epcilon", "ds_url"),
                   "publisher-frequency" : ("epcilon", "publisher_frequency"),
                   "publisher-pending-republish" : ("epcilon", "publisher_pending_republish"),
                   "iota-ided" : ("epcilon", "iota_ided"),
                   "identity": ("epcilon", "identity"),
                   "pks-filename": ("cert", "keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword")})
                ])


    def postConfigure(self):
        if self.cisTrue("iota_ided"):
            self.setSecuredURL()
        else:
            self.setURL()
        self.cset("callback_url", self.cget("url") + "StandingQueryCallbackServlet")
        self.cset("db_jndi", "EPCILONDB")


    def postInstall(self):
        installer.DBWebAppInstaller.postInstall(self)
        utils.putWait("Subscribing to the Epcis")
        url = self.cget("url") + "SubscribedServlet"
        cmd = "curl"
        if self.cisTrue("iota_ided"):
            keystore = CONFIG.get("cert", "pem_keystore")
            keystore_pwd = CONFIG.get("cert", "password")
            truststore = CONFIG.get("cert", "pem_truststore")
            cmd += " --cert \"" + keystore + "\":\"" + keystore_pwd + "\" --cacert \"" + truststore + "\""
        cmd += " " + url
        if utils.sh_exec(cmd):
            utils.putDoneOK()
        else:
            utils.putDoneFail()
