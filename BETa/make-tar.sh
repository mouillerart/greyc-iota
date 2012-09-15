#!/bin/sh

name=${PWD##*/}
cd ..
tar chf beta.tar  $name/dist $name/README $name/LISEZMOI
