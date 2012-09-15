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
import installer
import utils
from config import CONFIG


class DBConfigurer(installer.Configurer):

    def __init__(self):
        installer.Configurer.__init__(self, "Database", "db", [
                ("Enter the MySQL host", "db", "host", {}),
                ("Enter the MySQL port", "db", "port", {}),
                ("Enter the MySQL login", "db", "login", {}),
                ("Enter the MySQL password", "db", "password", {}),
                ("Install the Java MySQL connector?", "db", "jar_install", {"type": "YN"})
                ])


    def postConfigure(self):
        if CONFIG.isTrue("db", "jar_install"):
            lib = CONFIG.get("tomcat", "catalina_home") + "lib/"
            utils.sh_cp(CONFIG.get("db", "repo"), lib)
            utils.stopTomcat()
            utils.startTomcat()
