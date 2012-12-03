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

class SigMAInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "SigMA web application", "sigma", [
                ("Enter the SigMA web application name", "sigma", "name", {}),
                ("Enter the archive file pathname", "sigma", "repo", {"type": "file"})
                ], [
                ("application",
                 { "key-store-file-path": ("cert", "keystore"),
                   "key-store-password": ("cert", "password"), })
                ] )
