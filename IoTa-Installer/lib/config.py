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
import ConfigParser


class Configuration:

    def __init__(self, filename):
        self.filename = filename
        self.config = ConfigParser.ConfigParser()
        self.readIni()


    def has(self, section, option):
        return self.config.has_option(section, option)


    def get(self, section, option):
        if self.config.has_option(section, option):
            return self.config.get(section, option)
        return None


    def isTrue(self, section, option):
        val = self.get(section, option)
        if val:
            return val.lower() == "true"
        return False


    def set(self, section, option, value):
        value = str(value).strip()
        self.config.set(section, option, value)
        return value


    def readIni(self):
        self.config.read(self.filename)
            

    def writeIni(self):
        with open(self.filename, 'wb') as configfile:
            self.config.write(configfile)


CONFIG = Configuration("resources/install.ini")
