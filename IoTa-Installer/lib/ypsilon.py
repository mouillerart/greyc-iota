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


class YPSilonInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "YPSilon web application", "ypsilon", [
                ("Enter the YPSilon web application name", "ypsilon", "name", {}),
                ("Enter the archive file pathname", "ypsilon", "repo", {"type": "file"}),
                ], [
                ("application",
                 { "ldap-url": ("ldap", "url"),
                   "ldap-basedn": ("ldap", "base_dn"),
                   "ldap-user": ("ldap", "login"),
                   "ldap-password": ("ldap", "password"),
                   "ldap-user-group": ("ldap", "user_group"),
                   "ldap-user-id": ("ldap", "user_id"),
                   "ldap-attribute-owner": ("ldap", "attribute_owner"),
                   "ldap-attribute-alias": ("ldap", "attribute_alias") })
                ] )


    def postConfigure(self):
        self.setSecuredURL()
