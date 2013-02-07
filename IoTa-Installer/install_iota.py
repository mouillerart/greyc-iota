#!/usr/bin/python
# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2012-2013  Université de Caen Basse-Normandie, GREYC
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
from lib.cert    import CertConfigurer
from lib.sigma_cert import SigMaCertConfigurer
from lib.sigma   import SigMAInstaller
from lib.epcis   import EpcisInstaller
from lib.dphi    import DPHIInstaller
from lib.ds      import DSInstaller
from lib.dseta   import DSeTaInstaller
from lib.ldap    import LDAPConfigurer
from lib.eta     import ETaInstaller
from lib.activemq import ActiveMQInstaller
from lib.eta_callback_receiver import ETaCallbackReceiverInstaller
from lib.eta_callback_filter import ETaCallbackFilterInstaller
from lib.eta_callback_sender import ETaCallbackSenderInstaller
from lib.eta_callback_filter_app import ETaCallbackFilterAppInstaller
from lib.eta_callback_sender_app import ETaCallbackSenderAppInstaller
from lib.ephi    import EPHIInstaller
from lib.epcilon import EpcILoNInstaller
from lib.ons     import ONSConfigurer
from lib.alfa    import ALfAInstaller
from lib.omega   import OMeGaInstaller
from lib.user    import UserInstaller

if __name__ == "__main__":
    if "--accept-defaults" in sys.argv:
        CONFIG.set("global", "accept_defaults", "true")
        lib.utils.putWarning("Installing with defaults values from `resources/install.ini")
    else:
        CONFIG.set("global", "accept_defaults", "false")
    lib.utils.putTitle("                       IoTa Installer")
    GlobalConfigurer().run()
    CertConfigurer().run()
    TomcatInstaller().run()
    DBConfigurer().run()
    SigMaCertConfigurer().run()
    SigMAInstaller().run()
    EpcisInstaller().run()
    LDAPConfigurer().run()
    UserInstaller().run()
    ETaInstaller().run()
    EPHIInstaller().run()
    ActiveMQInstaller().run()
    ETaCallbackReceiverInstaller().run()
    print "[    ]"
    if lib.utils.getSetConfigYN("Use webapp versions for ETa Callback Filter and Sender?", "global", "callback_war"):
        ETaCallbackFilterInstaller().run()
        ETaCallbackSenderInstaller().run()
    else:
        ETaCallbackFilterAppInstaller().run()
        ETaCallbackSenderAppInstaller().run()
    DPHIInstaller().run()
    DSInstaller().run()
    DSeTaInstaller().run()
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
