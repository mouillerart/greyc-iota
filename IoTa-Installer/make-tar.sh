#!/bin/sh

name=${PWD##*/}
./update-version.sh

cd ..
read version < Version

echo "Making Installer tarball ..."
mkdir -p target
tar chf target/iota-installer-${version}.tar  $name/lib $name/resources $name/install_iota.py $name/README $name/LISEZMOI $name/LICENSE $name/INSTALL*

echo "Making DS installer tarball ..."
tar chf target/iota-ds-installer-${version}.tar $name/lib $name/install_ds.py $name/README $name/LISEZMOI $name/LICENSE $name/resources/apache-tomcat-*.tar.gz $name/resources/ds-*.war $name/resources/ds_schema.sql $name/resources/install.ini
