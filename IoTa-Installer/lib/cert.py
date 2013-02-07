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


class CertConfigurer(installer.Configurer):
    
    def __init__(self):
        installer.Configurer.__init__(self, "Certificate and signing key", "cert", [
                ("Create a new private key/certificate?", "cert", "create_keystore", {"type":"YN"}),
                ("Enter the keystore file name", "cert", "keystore", {}),
                ("Enter the keystore password", "cert", "password", {}),
                ("Enter the key/certificate’s principal’s distinguished name (in the form: \"CN=<name>, OU=<unit>, O=<organization>, L=<location>, S=<state>, C=<country>\")",
                 "cert", "distinguished_name", {"when": ("cert", "create_keystore")}),
                ("Enter the key/certificate’s alias/name", "cert", "keyalias",
                 {"when": ("cert", "create_keystore")}),
                ("Enter the key/certificate’s password (may be empty)", "cert", "keypassword",
                 {"when": ("cert", "create_keystore")}),
                ("Export the keystore to JKS?", "cert", "exportkeystore_tojks", {"type":"YN"}),
                ("Export the private key/certificate (needed fort TLS)?", "cert", "create_certfile", {"type":"YN"}),
                ("Enter the certificate file name", "cert", "certfile",
                 {"when": ("cert", "create_certfile")}),
                ("Create truststore with the exported key/certificate?", "cert", "create_truststore", {"type":"YN"}),
                ("Enter the truststore file name", "cert", "truststore",
                 {"when": ("cert", "create_truststore")}),
                ("Enter the truststore’s password", "cert", "trustpassword",
                 {"when": ("cert", "create_truststore")}),
                ("Enter the key/certificate’s alias/name for the truststore", "cert", "trust_keyalias",
                 {"when": ("cert", "create_truststore")}),
                ("Enter the key/certificate’s password for the truststore (may be empty)", "cert", "trust_keypassword", 
                 {"when": ("cert", "create_truststore")})
                ])


    def postConfigure(self):
        if self.cisTrue("create_keystore"):
            utils.execKeytool("Creating the new key/certificate", "-genkeypair", "pkcs12",
                              self.cget("keystore"), self.cget("password"),
                              self.cget("keyalias"), self.cget("keypassword"),
                              [("-dname", self.cget("distinguished_name")),
                               ("-keyalg", "RSA")])
        if self.cisTrue("exportkeystore_tojks"):
            jkskeystore = self.cget("keystore").rpartition(".")[0]
            jkskeystore += ".jks"
            self.cset("jks_keystore", jkskeystore)
            utils.execSrcToDestKeyTool("Convert keystore type from PKCS#12 to JKS", "-importkeystore",
                                       "pkcs12", self.cget("keystore"),
                                       self.cget("password"), self.cget("keyalias"),
                                       "jks", jkskeystore,
                                       self.cget("password"), self.cget("keyalias"), [])
        if self.cisTrue("create_certfile"):
            utils.execKeytool("Exporting certificate", "-exportcert", "pkcs12",
                              self.cget("keystore"), self.cget("password"),
                              self.cget("keyalias"), self.cget("keypassword"),
                              [("-file", self.cget("certfile"))])
        if self.cisTrue("create_truststore"):
            utils.execKeytool("Creating truststore", "-importcert", "jks",
                              self.cget("truststore"), self.cget("trustpassword"),
                              self.cget("trust_keyalias"), self.cget("trust_keypassword"),
                              [("-file", self.cget("certfile")), ("-noprompt", "")])
