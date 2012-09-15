#!/bin/sh

name=${PWD##*/}
cd ..
read version < Version
mkdir -p target
tar chf target/iota-installer-${version}.tar  $name/lib $name/resources $name/install_iota.py $name/README $name/LISEZMOI $name/LICENSE

tar chf target/iota-ds-installer-${version}.tar $name/lib $name/install_ds.py $name/README $name/LISEZMOI $name/LICENSE $name/resources/apache-tomcat-*.tar.gz $name/resources/discovery-server*.war $name/resources/ds_schema.sql $name/resources/install.ini
