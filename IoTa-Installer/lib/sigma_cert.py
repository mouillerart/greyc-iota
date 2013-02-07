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
import utils
import installer


class SigMaCertConfigurer(installer.Configurer):
 
    def __init__(self):
        installer.Configurer.__init__(self, "Certificate and signing key for SigMa", "sigma_cert", [
                ("Create a new private key/certificate for signing events?", "sigma_cert", "create_keystore", {"type":"YN"}),
                ("Enter the keystore file name", "sigma_cert", "keystore", {}),
                ("Enter the keystore password", "sigma_cert", "password", {}),
                ("Enter the key/certificate’s principal’s distinguished name (in the form: \"CN=<name>, OU=<unit>, O=<organization>, L=<location>, S=<state>, C=<country>\")",
                 "sigma_cert", "distinguished_name", {"when": ("sigma_cert", "create_keystore")}),
                ("Enter the key/certificate’s alias/name", "sigma_cert", "keyalias",
                 {"when": ("sigma_cert", "create_keystore")}),
                ("Enter the key/certificate’s password (may be empty)", "sigma_cert", "keypassword",
                 {"when": ("sigma_cert", "create_keystore")})
                ])


    def postConfigure(self):
        if self.cisTrue("create_keystore"):
            utils.execKeytool("Creating the new key/certificate", "-genkeypair", "pkcs12",
                              self.cget("keystore"), self.cget("password"),
                              self.cget("keyalias"), self.cget("keypassword"),
                              [("-dname", self.cget("distinguished_name")),
                               ("-keyalg", "EC")])
