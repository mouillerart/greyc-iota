#!/bin/sh

name=${PWD##*/}
cd ..
version=$(cat Version 2>/dev/null)
mkdir -p target
tar chf target/theta-installer-${version}.tar  $name/lib $name/resources $name/install_theta.py $name/README $name/LISEZMOI
