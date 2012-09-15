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
                ("Enter the URL to the Epcis (or ETa) Query service", "epcilon", "subscription_url", {}),
                ("Enter the URL to the DS", "ds", "url", {}),
                ("Enter the DS client login", "ds", "login", {}),
                ("Enter the DS client password", "ds", "password", {})
                ], [
                ("application",
                 { "query-callback-address": ("epcilon", "callback_url"),
                   "publish": "true",
                   "query-client-address": ("epcilon", "subscription_url"),
                   "discovery-service-address": ("ds", "url"),
                   "login": ("ds", "login"),
                   "password": ("ds", "password") })
                ])


    def postConfigure(self):
        CONFIG.set("epcilon", "url",
                   "http://" + CONFIG.get("global", "host") + ":" + CONFIG.get("tomcat", "http_port") +
                   "/" + CONFIG.get("epcilon", "name"))
        CONFIG.set("epcilon", "callback_url",
                   CONFIG.get("epcilon", "url") + "/StandingQueryCallbackServlet")
        CONFIG.set("epcilon", "db_jndi", "EPCILONDB")


    def postInstall(self):
        installer.DBWebAppInstaller.postInstall(self)
        utils.putWait("Subscribing to the Epcis")
        url = CONFIG.get("epcilon", "url") + "/SubscribedServlet"
        if utils.sh_exec("wget -qO /dev/null " + url):
            utils.putDoneOK()
        else:
            utils.putDoneFail()
