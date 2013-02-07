#!/usr/bin/ruby
# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2008-2013  Université de Caen Basse-Normandie, GREYC
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
if RUBY_VERSION < '1.9'
  File.open('Version', 'w') do |f|
    f.puts(IOTA_VERSION)
  end
else
  File.write('Version', IOTA_VERSION + "\n")
end

# Modify all Maven project files
%w[
  ALfA/ALfA-PI
  ALfA/ALfA
  ALfA/ALfA-RMI
  BETa
  CaPPa/DS-Xi-Client
  CaPPa/EPCIS-Xi-Client
  DELTa
  DSeTa/DSeTa-Client
  DSeTa/DSeTa
  DSeTa/DiscoveryPHI
  DSeTa/LibXACML-DS
  ETa/ETa-Callback/ETa-Callback-Filter
  ETa/ETa-Callback/ETa-Callback-Receiver
  ETa/ETa-Callback/ETa-Callback-Sender
  ETa/ETa-Capture-Client
  ETa/ETa-Query-Client
  ETa/ETa
  ETa/EpcisPHI
  ETa/LibXACML-EPCIS
  ETa/User
  ETa/User-Client
  EpcILoN
  IoTa-DiscoveryWS/IoTa-DiscoveryWS-Client
  IoTa-DiscoveryWS/IoTa-DiscoveryWS
  Mu
  MuPHI
  Nu/Nu
  Nu/Nu-PI
  OMeGa/OMeGa
  OMeGa/OmICron
  PSi
  SigMa/SigMa
  SigMa/SigMa-Client
  SigMa/SigMa-Commons
  SigMa/SigMa-Test
  TAu
].each do |project|
  project.strip!
  STDOUT.print("Changing version of #{project} ...")
  STDOUT.flush
  pomname = project + '/pom.xml'
  doc = Nokogiri::XML(open(pomname))
  (doc/'project/version').each do |version|
    version.content = IOTA_VERSION
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
