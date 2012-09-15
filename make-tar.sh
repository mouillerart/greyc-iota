#!/bin/sh

mkdir -p target

version=$(cat Version 2>/dev/null)

name=IoTa-Installer
tar chf target/iota-installer-${version}.tar README LISEZMOI Licenses AUTHORS copyright GPL-3 $name/lib $name/resources $name/install_iota.py $name/README $name/LISEZMOI

srctar=iota-${version}-src.tar.gz
find . -type f					\
    -not -name .directory			\
    -not -name "*~"				\
    -not -name "*.bak"				\
    -not -name "*.class"			\
    -not -name "*.pyc"				\
    -not -name "*.log"				\
    -not -path "*/.git/*"			\
    -not -path "*/target/*"			\
    -print0 | xargs -0 tar caf target/$srctar
