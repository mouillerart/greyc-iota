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


class ETaCallbackFilterAppInstaller(installer.Installer):

    def __init__(self):
        installer.Installer.__init__(self, "ETa Callback Filter", "eta_callback_filter", [
                ("Enter the archive file pathname", "eta_callback_filter", "repo_bin", {"type": "file"}),
                ("Enter the path where you want to unpack it", "eta_callback_filter", "directory",
                 {"type": "path"}),
                ("Enter the name of the directory", "eta_callback_filter", "name", {}),
                ("Enter the ActiveMQ consummer queue name for Callback Filter", "eta_callback_receiver", "send_queue_name", {}),
                ("Enter the ActiveMQ sender queue name for Callback Filter", "eta_callback_filter", "send_queue_name", {}),
                ("Enter the URL to the XACML module", "epcis_policies", "xacml_url", {}),
                ("Enter the ETa database name", "eta", "db_name", {}),
                ("Enter the Callback database login", "eta", "callback_db_login", {}),
                ("Enter the Callback database password", "eta", "callback_db_password", {}),
                ("Enter the startup delay (in ms)", "eta_callback_filter", "startup-delay", {}),
                ("Enter the polling delay (in ms)", "eta_callback_filter", "polling-delay", {}),
                ], [
                ("application",
                 { "activemq-consQueueName": ("eta_callback_receiver", "send_queue_name"),
                   "activemq-sendQueueName": ("eta_callback_filter", "send_queue_name"),
                   "activemq-url": ("activemq", "url"),
                   "activemq-login": ("activemq", "login"),
                   "activemq-password": ("activemq", "password"),
                   "database-url": ("eta", "callback_db_url"),
                   "database-login": ("eta", "callback_db_login"),
                   "database-password": ("eta", "callback_db_password"),
                   "xacml-url": ("epcis_policies", "xacml_url"),
                   "pks-filename": ("cert", "jks_keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword"),
                   "startup-delay": ("eta_callback_filter", "startup-delay"),
                   "polling-delay": ("eta_callback_filter", "polling-delay"),
                   })
                ])


    def postConfigure(self):
        self.cset("repo", self.cget("repo_bin"))
