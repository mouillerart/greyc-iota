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


class ActiveMQInstaller(installer.Installer):

    def __init__(self):
        installer.Installer.__init__(self, "Apache ActiveMQ", "activemq", [
                ("Enter the archive file pathname", "activemq", "repo",
                 {"when": ("activemq", "install"), "type": "file"}),
                ("Enter the path where you want to unpack it", "activemq", "directory",
                 {"when": ("activemq", "install"), "type": "path"}),
                ("Enter the name of the directory", "activemq", "name",
                 {"when": ("activemq", "install")}),
                ("Enter ActiveMQ home directory (empty if not on this machine)", "activemq", "home",
                 {"unless": ("activemq", "install")}),
                ("Enter the TCP URL", "activemq", "url", {}),
                ("Enter the admin console URL", "activemq", "admin_url", {}),
                ("Enter the user login (may be empty)", "activemq", "login", {}),
                ("Enter the user password (may be empty)", "activemq", "password", {}),
                ])


    def postConfigure(self):
        if self.installp:
            self.cset("home", self.cget("directory") + '/' + self.cget("name"))
 
 
    def install(self):
        # try to install
        if not installer.Installer.install(self):
            # else, get infos
            self.configure()
        if self.cget("home"):
            utils.startActiveMQ()
