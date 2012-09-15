#!/usr/bin/ruby
# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2008-2012  Université de Caen Basse-Normandie, GREYC
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
require 'nokogiri'

def usage(io=STDERR)
  io.puts(<<EOS)
#{$0} -h|--help
        shows this help
#{$0} VERSION
        sets the version of all modules to VERSION
EOS
end

case ARGV[0]
when nil
  usage()
  exit(1)
when '-h', '--help'
  usage(STDOUT)
  exit(0)
end

# Write file Version
IOTA_VERSION = ARGV[0]
STDOUT.puts("Setting version to #{IOTA_VERSION} ...")
File.write('Version', IOTA_VERSION + "\n")

# Modify all Maven project files
IOTA_MODS = %w[ alfa-pi libxacml-ds libxacml-epcis discovery-client omicron eta-client ]

%w[
  ALfA/ALfA-PI
  ALfA/ALfA
  BETa
  DELTa
  DSeTa/DiscoveryPHI
  DSeTa/LibXACML-DS
  ETa/ETa-Callback/ETa-Callback-Filter
  ETa/ETa-Callback/ETa-Callback-Receiver
  ETa/ETa-Callback/ETa-Callback-Sender
  ETa/ETa-Client
  ETa/ETa
  ETa/EpcisPHI
  ETa/LibXACML-EPCIS
  EpcILoN
  IoTa-DiscoveryWS/IoTa-DiscoveryWS-Client
  IoTa-DiscoveryWS/IoTa-DiscoveryWS
  MuPHI
  OMeGa/OMeGa
  OMeGa/OmICron
  PSi
].each do |project|
  project.strip!
  STDOUT.print("Changing version of #{project} ...")
  STDOUT.flush
  pomname = project + '/pom.xml'
  doc = Nokogiri::XML(open(pomname))
  (doc/'project/version').each do |version|
    version.content = IOTA_VERSION
  end
  (doc/'project/dependencies/dependency').each do |dep|
    aid = dep.search('artifactId').first
    if IOTA_MODS.member?(aid.text.strip)
      version = aid.parent.search('version').first
      version.content = IOTA_VERSION
    end
  end
  File.open(pomname, 'w') do |file|
    doc.write_to(file, :encoding => 'utf-8', :indent => 4)
  end
  STDOUT.puts(' ok')
end

# Installer
STDOUT.print('Updates IoTa-Installer resources ...')
`IoTa-Installer/update-version.sh`
STDOUT.puts(' ok')
