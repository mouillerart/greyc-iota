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
import utils
from config import CONFIG


class Configurer:
    """
    A Configurer asks questions to set values to options.
    """

    def __init__(self, pretty_name, section_name, options=[]):
        """
        - pretty_name is the pretty (or print) name
        - section_name is the section name for this configurer in the CONFIG object
        - options is a list of tuples

        An option is a tuple (question, section, option-name, dictionary) where:
        - question is the text to explain the option to the user,
        - section is the name of a section in the install.ini file/CONFIG object,
        - option-name is the name of the option in this file,
        - dictionary is a dictionary with these possible key:value pairs:
          - "when": (dep_section, dep_name)
            the question is asked only when the option (dep_section, dep_name) is true,
          - "unless": (dep_section, dep_name)
            the question is asked only when the option (dep_section, dep_name) is false,
          - "type": type of possible answer
            - "YN": for a Yes/No question,
            - "path": for an existing directory path,
            - "file": for an existing file
        """
        self.pretty_name = pretty_name
        self.section_name = section_name
        self.options = options


    def cget(self, option_name):
        return CONFIG.get(self.section_name, option_name)


    def cisTrue(self, option_name):
        return CONFIG.isTrue(self.section_name, option_name)


    def cset(self, option_name, value):
        CONFIG.set(self.section_name, option_name, value)
        return value


    def configure(self):
        # asks all the questions
        for question, section, option, dic in self.options:
            if "when" in dic:
                wsec, wopt = dic["when"]
                if not CONFIG.isTrue(wsec, wopt):
                    continue
            if "unless" in dic:
                usec, uopt = dic["unless"]
                if CONFIG.isTrue(usec, uopt):
                    continue
            gstype = None
            if "type" in dic:
                gstype = dic["type"]
            utils.getSetConfigType(question, section, option, gstype)
        self.postConfigure()
        # save the answers
        CONFIG.writeIni()


    def postConfigure(self):
        """
        Hook for adding actions just after the configuration phase.
        Like calculating other options.
        """
        return True


    def run(self):
        try:
            utils.putTitle(self.pretty_name + " Configuration")
            self.configure()
        except Exception as e: 
            utils.putError(self.pretty_name + " configuration interrupted!", e)
        else:
            utils.putSuccess(self.pretty_name + " configuration successfull")


class Installer(Configurer):
    """
    An Installer is a Configurer which also unpacks an archive.
    """


    def __init__(self, pretty_name, section_name, options=[], properties_options=[]):
        """
        See Configure.__init__(…)

        Properties options are (fname, dictionary) pairs, where:
        - fname is the name of the properties configuration file to modify or
          create,
        - dictionary contains key:value pairs where:
          - the key is be the name of the property,
          - the value is either a litteral value or a (section, name) option pair.
        """
        Configurer.__init__(self, pretty_name, section_name, options)
        self.properties_options = properties_options
        self.installp = True


    def unpack(self):
        utils.putWait("Unpacking " + self.pretty_name)
        directory = self.cget("directory") + "/" + self.cget("name")
        utils.sh_mkdir_p(directory)
        detar_command = "tar -C " + directory + " --strip-components=1 -xaf " + self.cget("repo")
        if utils.sh_exec(detar_command):
            utils.putDoneOK()
            self.postUnpack()
        else:
            utils.putDoneFail()


    def postUnpack(self):
        """
        Hook for adding actions just after the unpacking phase.
        """
        return


    def install(self):
        self.installp = utils.getSetConfigYN("Do you want to install a new " + self.pretty_name + "?",
                                             self.section_name, "install")
        if not self.installp:
            return False
        self.configure()
        self.unpack()
        self.configureProperties()
        self.postInstall()
        return True


    def postInstall(self):
        """
        Hook for adding actions after the overall installation.
        """
        return


    def configureProperties(self):
        """
        Fill properties configuration files
        """
        for filename, dic in self.properties_options:
            utils.configureProperties(self.pretty_name, filename, self.section_name, dic)
        self.postConfigureProperties()


    def postConfigureProperties(self):
        """
        Hook for adding actions just after the properties phase.
        """
        return


    def run(self):
        try:
            utils.putTitle(self.pretty_name + " Installation")
            self.install()
        except Exception as e: 
            utils.putError(self.pretty_name + " installation interrupted!", e)
        else:
            if self.installp:
                utils.putSuccess(self.pretty_name + " installation successfull")
            else:
                utils.putSuccess(self.pretty_name + " not installed")


class WebAppInstaller(Installer):
    """
    Installer specialized for WebApps.
    """

    def __init__(self, pretty_name, section_name, options=[], properties_options=[]):
        Installer.__init__(self, pretty_name, section_name, options, properties_options)


    def unpack(self):
        utils.deployWar(self.pretty_name, self.section_name)
        self.postUnpack()


    def setURL(self):
        return self.cset("url", "http://" + CONFIG.get("global", "host") + ":" +
                         CONFIG.get("tomcat", "http_port") + "/" + self.cget("name") + "/")


    def setSecuredURL(self):
        return self.cset("url", "https://" + CONFIG.get("global", "host") + ":" +
                         CONFIG.get("tomcat", "secure_port") + "/" + self.cget("name") + "/")


    # default implementation, beware when overrinding
    def postConfigure(self):
        self.setURL()


    def configureProperties(self):
        for filename, dic in self.properties_options:
            utils.configurePropertiesWebApp(self.pretty_name, filename, self.section_name, dic)
        if self.properties_options:
            if CONFIG.isTrue("tomcat", "use_manager"):
                utils.putWait("Reloading " + self.pretty_name)
                utils.manageTomcat("reload?path=/" + self.cget("name"))
            else:
                utils.stopTomcat()
                utils.startTomcat()
        self.postConfigureProperties()


class DBWebAppInstaller(WebAppInstaller):
    """
    Installer specialized for WebApps with a database.
    """

    def __init__(self, pretty_name, section_name, options=[], properties_options=[]):
        WebAppInstaller.__init__(self, pretty_name, section_name, options, properties_options)


    def unpack(self):
        utils.deployWarDB(self.pretty_name, self.section_name)
        self.postUnpack()
