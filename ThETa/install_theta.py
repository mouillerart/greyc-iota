#!/usr/bin/python
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
import os
import sys
import lib.utils
from lib.config  import CONFIG
import lib.installer


class ThETaInstaller(lib.installer.Installer):

    def __init__(self):
        lib.installer.Installer.__init__(self, "ThETa", "theta", [
                ("Enter RHO (REST/HTTP Object JSON interface) URL", "theta", "rho_url", {}),
                ("Enter your identity", "theta", "identity", {}),
                ("Enter the EPC code", "theta", "epc", {}),
                ("Enter the CSV file for readpoints lat/lon path", "theta", "csv_repo", {"type": "file"}),
                ("Enter the archive file path", "theta", "repo", {"type": "file"}),
                ("Enter the path where you want to unpack it", "theta", "directory",
                 {"type": "path"}),
                ("Enter the name of the directory", "theta", "name", {})
                ], [
                ("theta",
                 { "IoTa.sortDataStorage.ds1.ds.csvFile": ("theta", "csv_file"),
                   "IoTa.sortDataStorage.ds0.url": ("theta", "rho_url_epc"),
                   })
                ])


    def postConfigure(self):
        self.cset("rho_url_epc", self.cget("rho_url") + "?theta=true&id=" + self.cget("identity") + "&epc=" + self.cget("epc"))
        self.cset("csv_file", self.cget("directory") + self.cget("name") + "/" + "readpoints.csv")


    def postUnpack(self):
        theta_dir = self.cget("directory") + self.cget("name")
        lib.utils.sh_cp("resources/theta.properties", theta_dir)
        lib.utils.sh_cp("resources/theta.sh", theta_dir)
        lib.utils.sh_cp(self.cget("csv_repo"), self.cget("csv_file"))


if __name__ == "__main__":
    if "--accept-defaults" in sys.argv:
        CONFIG.set("global", "accept_defaults", "true")
        lib.utils.putWarning("Installing with defaults values from `resources/install.ini")
    else:
        CONFIG.set("global", "accept_defaults", "false")
    lib.utils.putTitle("                       ThETa Installer")
    ThETaInstaller().run()
    lib.utils.putWarning(
"""
Important!

`resources/install.ini` contains logins and passwords in _clear text_.
They can also be found in the property files of the application.
""")
