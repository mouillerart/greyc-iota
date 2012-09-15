#!/usr/bin/python
# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2012  Université de Caen Basse-Normandie, GREYC
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
import sys
import lib.utils
from lib.config  import CONFIG
from lib.common  import GlobalConfigurer
from lib.tomcat  import TomcatInstaller
from lib.db      import DBConfigurer
from lib.epcis   import EpcisInstaller
from lib.dphi    import DPHIInstaller
from lib.ds      import DSInstaller
from lib.ldap    import LDAPConfigurer
from lib.eta     import ETaInstaller
from lib.ephi    import EPHIInstaller
from lib.epcilon import EpcILoNInstaller
from lib.ons     import ONSConfigurer
from lib.alfa    import ALfAInstaller
from lib.omega   import OMeGaInstaller

if __name__ == "__main__":
    if "--accept-defaults" in sys.argv:
        CONFIG.set("global", "accept_defaults", "true")
        lib.utils.putWarning("Installing with defaults values from `resources/install.ini")
    else:
        CONFIG.set("global", "accept_defaults", "false")
    lib.utils.putTitle("                       IoTa Installer")
    GlobalConfigurer().run()
    TomcatInstaller().run()
    DBConfigurer().run()
    EpcisInstaller().run()
    LDAPConfigurer().run()
    ETaInstaller().run()
    EPHIInstaller().run()
    DPHIInstaller().run()
    DSInstaller().run()
    EpcILoNInstaller().run()
    ONSConfigurer().run()
    ALfAInstaller().run()
    OMeGaInstaller().run()
    lib.utils.putWarning(
"""
Important!

`resources/install.ini` contains logins and passwords in _clear text_.
They can also be found in Apache Tomcat configuration files and in the
property files of each application.
""")
