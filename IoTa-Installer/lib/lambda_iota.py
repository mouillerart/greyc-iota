# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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


class LaMBDaInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "LaMBDa web application", "lambda", [
                ("Enter the LaMBDa web application name", "lambda", "name", {}),
                ("Enter the archive file pathname", "lambda", "repo", {"type": "file"}),
                ("Enter the URL of the OMeGa web service", "omega", "url", {}),
                ("Enter the URL of the SigMa web service", "sigma", "url", {})
                ], [
                ("application",
                 { "omega-url": ("omega", "url"),
                   "sigma-url": ("sigma", "url"),
                   "pks-filename": ("cert", "keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword") })
                ] )


    def postConfigure(self):
        url = self.setSecuredURL()
