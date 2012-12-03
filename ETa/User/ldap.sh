#!/bin/sh
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

createLdifs() {
    echo "Creating the schema into ldif format (user.ldif)"
    cat <<EOF > user.ldif
dn: cn=user,cn=schema,cn=config
objectClass: olcSchemaConfig
cn: user
olcAttributeTypes: ( 1.1.2.1.1 NAME 'partner' DESC 'Partner ID' SUP name )
olcObjectClasses: ( 1.1.2.2.1 NAME 'user' DESC 'Define user' SUP top STRUCTURAL MUST ( uid $ userPassword $ partner ) )
EOF

    echo "Creating the user group into ldif format (usergroup.ldif)"
    cat <<EOF > usergroup.ldif
dn: ou=users,$dn
objectclass: top
objectclass: organizationalUnit
ou: users
description: users
EOF

    echo "Creating the user 'superadmin' into ldif format (superadmin.ldif)"
    cat <<EOF > superadmin.ldif
dn: uid=superadmin,ou=users,$dn
objectclass: top
objectclass: user
uid: superadmin
partner: superadmin
userPassword: {SHA}iJo6eRs4dc+uQTV0tT2ku4qQ1T4=
EOF

    echo "Creating the user 'anonymous' into ldif format (anonymous.ldif)"
    cat <<EOF > anonymous.ldif
dn: uid=anonymous,ou=users,$dn
objectclass: top
objectclass: user
uid: anonymous
partner: anonymous
userPassword: {SHA}CpL6syMBNMym6t2YmDJbmyrmeZg=
EOF

    echo "All the ldif files are created."
}


addLdifs() {
    echo "Adds the schema (user.ldif)"
    ldapadd -Y EXTERNAL -H ldapi:/// -f user.ldif || {
        echo "Adding aborted"
        exit 1
    }
    echo "Adds the user group (usergroup.ldif)"
    ldapadd -x -D "cn=$login,$dn" -f usergroup.ldif -w "$password" || {
        echo "Adding aborted"
        exit 1
    }
    echo "Adds the user 'superadmin'"
    ldapadd -x -D "cn=$login,$dn" -f superadmin.ldif -w "$password" || {
        echo "Adding aborted"
        exit 1
    }
    echo "Adds the user 'anonymous'"
    ldapadd -x -D "cn=$login,$dn" -f anonymous.ldif -w "$password" || {
        echo "Adding aborted"
        exit 1
    }
}


b=true
while $b
do
    echo "Enter the LDAP's domaine name (for example dc=mydomain,dc=com):"
    read dn
    echo "$dn" | grep -E "^(dc=[a-z]+)(,dc=[a-z]+)+$" && b=false
done

echo "Enter the LDAP's login: (admin by default)"
read login
if [ -z "$login" ];
then
    login=admin
fi

echo "Enter the LDAP's password:"
stty -echo
read password
stty +echo

while true; do
    read -p "Do you want to create ldif files? (y/n)" yn
    case $yn in
        [Yy]*) createLdifs; break;;
        [Nn]*) break;;
        *) echo "Please answer yes or no.";;
    esac
done

while true; do
    read -p "Do you want to automatically add ldif files to LDAP? (y/n)" yn
    case $yn in
        [Yy]*) addLdifs; break;;
        [Nn]*) break;;
        *) echo "Please answer yes or no.";;
    esac
done
