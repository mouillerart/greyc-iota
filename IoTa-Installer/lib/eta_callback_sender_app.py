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
import installer


class ETaCallbackSenderAppInstaller(installer.Installer):

    def __init__(self):
        installer.Installer.__init__(self, "ETa Callback Sender", "eta_callback_sender", [
                ("Enter the archive file pathname", "eta_callback_sender", "repo_bin", {"type": "file"}),
                ("Enter the path where you want to unpack it", "eta_callback_sender", "directory",
                 {"type": "path"}),
                ("Enter the name of the directory", "eta_callback_sender", "name", {}),
                ("Enter the ETa database name", "eta", "db_name", {}),
                ("Enter the Callback database login", "eta", "callback_db_login", {}),
                ("Enter the Callback database password", "eta", "callback_db_password", {}),
                ("Enter the ActiveMQ consummer queue name for Callback Sender", "eta_callback_filter", "send_queue_name", {}),
                ("Enter the timeout (wait for event to send, in ms)", "eta_callback_sender", "jms_timeout", {}),
                ("Enter the JMS message name property to set the time of the last try to send", "eta_callback_sender", "jms_message_time_property", {}),
                ("Enter the startup delay (in ms)", "eta_callback_sender", "startup-delay", {}),
                ("Enter the polling delay (in ms)", "eta_callback_sender", "polling-delay", {}),
                ], [
                ("application",
                 { "jms-queueName": ("eta_callback_filter", "send_queue_name"),
                   "jms-url": ("activemq", "url"),
                   "jms-login": ("activemq", "login"),
                   "jms-password": ("activemq", "password"),
                   "jms-timeout": ("eta_callback_sender", "jms_timeout"),
                   "jms-message-time-property": ("eta_callback_sender", "jms_message_time_property"),
                   "database-url": ("eta", "callback_db_url"),
                   "database-login": ("eta", "callback_db_login"),
                   "database-password": ("eta", "callback_db_password"),
                   "startup-delay": ("eta_callback_sender", "startup-delay"),
                   "polling-delay": ("eta_callback_sender", "polling-delay"),
                   "pks-filename": ("cert", "keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword")
                   })
                ])


    def postConfigure(self):
        self.cset("repo", self.cget("repo_bin"))
