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
import installer
import utils


class LDAPConfigurer(installer.Configurer):

    def __init__(self):
        installer.Configurer.__init__(self, "LDAP", "ldap", [
                ("Enter the URL to the LDAP directory", "ldap", "url", {}),
                ("Enter the LDAP's domain name", "ldap", "base_dn", {}),
                ("Enter the LDAP's login", "ldap", "login", {}),
                ("Enter the LDAP's password", "ldap", "password", {}),
                ("Enter the LDAP's user group (if multiples entries, complete the ldif file before to add it)", "ldap", "user_group", {}),
                ("Enter the LDAP's user ID", "ldap", "user_id", {}),
                ("Enter the LDAP's owner attribute", "ldap", "attribute_owner", {}),
                ("Enter the LDAP's alias attribute", "ldap", "attribute_alias", {}),
                ("Do you want to create ldif files?", "ldap", "ldif_create", {"type": "YN"}),
                ("Do you want to automatically add ldif files to LDAP?", "ldap", "ldif_install",
                 { "when": ("ldap", "ldif_create"), "type": "YN"})
                ])


    def postConfigure(self):
        if self.cisTrue("ldif_create"):
            self.createLdifs()
            if self.cisTrue("ldif_install"):
                self.addLdifs()


    def createLdifs(self):
        utils.writeFile("Creating the schema as a ldif file (user.ldif)", "user.ldif", """
dn: cn=user,cn=schema,cn=config
objectClass: olcSchemaConfig
cn: user
olcAttributeTypes: ( 1.1.2.1.1 NAME '%(owner)s' DESC 'Owner ID' SUP name )
olcAttributeTypes: ( 1.1.2.1.2 NAME '%(alias)s' DESC 'Alias DN' SUP name )
olcObjectClasses: ( 1.1.2.2.1 NAME 'user' DESC 'Define user' SUP top STRUCTURAL MUST ( %(uid)s $ %(owner)s ) MAY ( %(alias)s ) )
""" % {"uid": self.cget("user_id"), "owner": self.cget("attribute_owner"), "alias": self.cget("attribute_alias")})
        group_value = self.cget("user_group").split("=")[-1]
        utils.writeFile("Creating the user group as a ldif file (usergroup.ldif)", "usergroup.ldif", """
dn: %(group)s,%(dn)s
objectclass: top
objectclass: organizationalUnit
ou: %(group_val)s
description: users
""" % {"group": self.cget("user_group"), "group_val": group_value, "dn": self.cget("base_dn")} )
        utils.writeFile("Creating the user 'superadmin' as a ldif file (superadmin.ldif)", "superadmin.ldif", """
dn: %(uid)s=superadmin,%(group)s,%(dn)s
objectclass: top
objectclass: user
%(uid)s: superadmin
%(owner)s: superadmin
""" % {"uid": self.cget("user_id"), "group": self.cget("user_group"), "dn": self.cget("base_dn"), "owner": self.cget("attribute_owner")} )
        utils.writeFile("Creating the user '%(anonymous)s' as ldif file (anonymous.ldif)", "anonymous.ldif", """
dn: %(uid)s=%(anonymous)s,%(group)s,%(dn)s
objectclass: top
objectclass: user
%(uid)s: %(anonymous)s
%(owner)s: anonymous
""" % {"anonymous": CONFIG.get("global", "anonymous_user"), "uid": self.cget("user_id"), "group": self.cget("user_group"), "dn": self.cget("base_dn"), "owner": self.cget("attribute_owner")} )


    def addLdifs(self):
        utils.createLDAP("Adds the schema (user.ldif)", "user.ldif")
        utils.execLDAP("Adds the user group (usergroup.ldif)", "usergroup.ldif")
        utils.execLDAP("Adds the user 'superadmin'", "superadmin.ldif")
        utils.execLDAP("Adds the user 'anonymous'", "anonymous.ldif")
