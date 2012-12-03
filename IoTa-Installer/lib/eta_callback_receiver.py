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


class ETaCallbackReceiverInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "ETa Callback Receiver web application", "eta_callback_receiver", [
                ("Enter the ETa Callback web application name", "eta_callback_receiver", "name", {}),
                ("Enter the archive file pathname", "eta_callback_receiver", "repo", {"type": "file"}),
                ("Enter the ActiveMQ sender queue name for Callback Filter", "eta_callback_receiver", "send_queue_name", {}),
                ], [
                ("application",
                 { "activemq-url": ("activemq", "url"),
                   "activemq-login": ("activemq", "login"),
                   "activemq-password": ("activemq", "password"),
                   "activemq-queueName": ("eta_callback_receiver", "send_queue_name") })
                ] )


    def postConfigure(self):
        self.setURL()
        self.cset("callback_url", self.cget("url") + self.cget("callbackservlet_name"))
