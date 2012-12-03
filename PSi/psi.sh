#!/bin/sh

java -cp ".:lib/*" fr.unicaen.iota.simulator.app.ApplicationControl toolSpecifications/toolSpecification.xml "$@"
# -n sampleNets/EPC/IOTA_GS1-poisson.pnml "$@"
