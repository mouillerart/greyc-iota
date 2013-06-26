#!/bin/sh

name=${PWD##*/}
cd ..
version=$(cat Version 2>/dev/null)
mkdir -p resources
tar chf IoTa-Installer/resources/gamma-${version}.tar $name/src $name/README $name/LISEZMOI
