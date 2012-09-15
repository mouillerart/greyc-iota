#!/bin/sh

cp="."
for f in lib/*.jar; do
    cp="${cp}:${f}"
done

java -cp ${cp} fr.unicaen.iota.simulator.app.ApplicationControl toolSpecifications/toolSpecification.xml "$@"
# -n sampleNets/EPC/IOTA_GS1-poisson.pnml "$@"
