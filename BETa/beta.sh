#!/bin/sh

cp="."
for f in lib/*.jar; do
    cp="${cp}:${f}"
done

java -cp ${cp} fr.unicaen.iota.application.client.Main
