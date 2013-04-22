#!/bin/sh
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

createLdifs() {
    echo "Creating the schema into ldif format (user.ldif)"
    cat <<EOF > user.ldif
dn: cn=user,cn=schema,cn=config
objectClass: olcSchemaConfig
cn: user
olcAttributeTypes: ( 1.1.2.1.1 NAME '$owner' DESC 'Owner ID' SUP name )
olcAttributeTypes: ( 1.1.2.1.2 NAME '$alias' DESC 'Alias DN' SUP name )
olcObjectClasses: ( 1.1.2.2.1 NAME 'user' DESC 'Define user' SUP top STRUCTURAL MUST ( $uid $ $owner ) MAY ( $alias ) )
EOF

    echo "Creating the user group into ldif format (usergroup.ldif)"
    cat <<EOF > usergroup.ldif
dn: $group,$dn
objectclass: top
objectclass: organizationalUnit
ou: $group_value
description: users
EOF

    echo "Creating the user 'superadmin' into ldif format (superadmin.ldif)"
    cat <<EOF > superadmin.ldif
dn: $uid=superadmin,$group,$dn
objectclass: top
objectclass: user
$uid: superadmin
$owner: superadmin
EOF

    echo "Creating the user 'anonymous' into ldif format (anonymous.ldif)"
    cat <<EOF > anonymous.ldif
dn: $uid=$anonymous,$group,$dn
objectclass: top
objectclass: user
$uid: $anonymous
$owner: anonymous
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
stty echo

echo "Enter the LDAP's user group: (ou=users by default)"
read group
if [ -z "$group" ];
then
    group="ou=users"
fi

group_value=${group##*=}

echo "Enter the LDAP's user ID: (uid by default)"
read uid
if [ -z "$uid" ];
then
    uid=uid
fi

echo "Enter the LDAP's owner attribute: (ownerid by default)"
read owner
if [ -z "$owner" ];
then
    owner=ownerid
fi

echo "Enter the LDAP's alias attribute: (aliasdn by default)"
read alias
if [ -z "$alias" ];
then
    alias=aliasdn
fi

echo "Enter the anonymous identity: (anonymous by default)"
read anonymous
if [ -z "$anonymous" ];
then
    anonymous=anonymous
fi

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
