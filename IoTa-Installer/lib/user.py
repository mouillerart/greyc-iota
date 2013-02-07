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


class UserInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "User login web application", "user", [
                ("Enter the User web application name", "user", "name", {}),
                ("Enter the archive file pathname", "user", "repo", {"type": "file"}),
                ("Enter the URL to the XACML module", "epcis_policies", "xacml_url", {}),
                ], [
                ("application",
                 { "xacml-url": ("epcis_policies", "xacml_url"),
                   "xacml-ihm-url": ("ephi", "url"),
                   "pks-filename": ("cert", "jks_keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword"),
                   "epcis-query-url": ("epcis", "query_url"),
                   "epcis-capture-url": ("epcis", "capture_url"),
                   "eta-userservice-url": ("user", "url"),
                   "ldap-url": ("ldap", "url"),
                   "ldap-basedn": ("ldap", "base_dn"),
                   "ldap-user": ("ldap", "login"),
                   "ldap-password": ("ldap", "password") })
                ] )


    def postConfigure(self):
        self.setSecuredURL()
