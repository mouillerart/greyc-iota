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


class ALfAInstaller(installer.Installer):

    def __init__(self):
        installer.Installer.__init__(self, "ALfA RMI server", "alfa", [
                ("Enter the archive file pathname", "alfa", "repo", {"type": "file"}),
                ("Enter the path where you want to unpack it", "alfa", "directory",
                 {"type": "path"}),
                ("Enter the name of the directory", "alfa", "name", {}),
                ("Enter the RMI name", "alfa", "rmi_name", {}),
                ("Enter the RMI port number", "alfa", "rmi_port", {})
                ], [
                ("application",
                 { "rmi-server-name": ("alfa", "rmi_name"),
                   "rmi-server-host": ("global", "host"),
                   "rmi-server-port": ("alfa", "rmi_port"),
                   "ons": ("ons", "server"),
                   "ons-domain-prefix": ("ons", "domain_prefix"),
                   })
                ])


    def postConfigure(self):
        url = "//" + CONFIG.get("global", "host") + ":" + self.cget("rmi_port") + "/" + self.cget("rmi_name")
        self.cset("rmi_url", url)


    def postInstall(self):
        ## start
        #utils.putMessage("Starting ALfA RMI server ...")
        #alfa_dir = CONFIG.get("alfa", "directory") + CONFIG.get("alfa", "name")
        #utils.sh_exec("(cd " + alfa_dir + " && ./launch.sh)&" )
        pass
