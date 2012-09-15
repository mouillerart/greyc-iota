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
from config import CONFIG

class LDAPConfigurer(installer.Configurer):

    def __init__(self):
        installer.Configurer.__init__(self, "LDAP", "ldap", [
                ("Enter the LDAP's domain name", "ldap", "base_dn", {}),
                ("Enter the LDAP's login", "ldap", "login", {}),
                ("Enter the LDAP's password", "ldap", "password", {}),
                ("Enter the URL to the LDAP directory", "ldap", "url", {}),
                ("Do you want to create ldif files?", "ldap", "ldif_create", {"type": "YN"}),
                ("Do you want to automatically add ldif files to LDAP?", "ldap", "ldif_install", {"type": "YN"})
                ])

    def postConfigure(self):
        if CONFIG.isTrue("ldap", "ldif_create"):
            self.createLdifs()
            if CONFIG.isTrue("ldap", "ldif_install"):
                self.addLdifs()

    def createLdifs(self):
        utils.writeFile("Creating the schema as a ldif file (user.ldif)", "user.ldif", """
dn: cn=user,cn=schema,cn=config
objectClass: olcSchemaConfig
cn: user
olcAttributeTypes: ( 1.1.2.1.1 NAME 'partner' DESC 'Partner ID' SUP name )
olcObjectClasses: ( 1.1.2.2.1 NAME 'user' DESC 'Define user' SUP top STRUCTURAL MUST ( uid $ userPassword $ partner ) )
""")
        utils.writeFile("Creating the user group as a ldif file (usergroup.ldif)", "usergroup.ldif", """
dn: ou=users,%s
objectclass: top
objectclass: organizationalUnit
ou: users
description: users
""" % CONFIG.get("ldap", "base_dn"))
        utils.writeFile("Creating the user 'superadmin' as a ldif file (superadmin.ldif)", "superadmin.ldif", """
dn: uid=superadmin,ou=users,%s
objectclass: top
objectclass: user
uid: superadmin
partner: superadmin
userPassword: {SHA}iJo6eRs4dc+uQTV0tT2ku4qQ1T4=
""" % CONFIG.get("ldap", "base_dn"))
        utils.writeFile("Creating the user 'anonymous' as ldif file (anonymous.ldif)", "anonymous.ldif", """
dn: uid=anonymous,ou=users,%s
objectclass: top
objectclass: user
uid: anonymous
partner: anonymous
userPassword: {SHA}CpL6syMBNMym6t2YmDJbmyrmeZg=
""" % CONFIG.get("ldap", "base_dn"))


    def addLdifs(self):
        utils.createLDAP("Adds the schema (user.ldif)", "user.ldif")
        utils.execLDAP("Adds the user group (usergroup.ldif)", "usergroup.ldif")
        utils.execLDAP("Adds the user 'superadmin'", "superadmin.ldif")
        utils.execLDAP("Adds the user 'anonymous'", "anonymous.ldif")
