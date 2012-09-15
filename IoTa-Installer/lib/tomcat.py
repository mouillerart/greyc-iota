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
import os
import shutil
import xml.dom.minidom

from config import CONFIG
import utils
import installer


class TomcatInstaller(installer.Installer):

    def __init__(self):
        installer.Installer.__init__(self, "Apache Tomcat", "tomcat", [
                ("Use the manager webapp?", "tomcat", "use_manager", {"type": "YN"}),
                ("Enter the manager’s path", "tomcat", "manager_path",
                 {"when": ("tomcat", "use_manager")}),
                ("Enter the manager’s login (manager or manager-script role)", "tomcat", "login",
                 {"when": ("tomcat", "use_manager")}),
                ("Enter the manager’s password", "tomcat", "password",
                 {"when": ("tomcat", "use_manager")}),
                ("Enter the HTTP port", "tomcat", "http_port", {}),
                ("Enter the server shutdown port", "tomcat", "shutdown_port",
                 {"when": ("tomcat", "install")}),
                ("Enter the AJP port", "tomcat", "ajp_port",
                 {"when": ("tomcat", "install")}),
                ("Enter the redirect port", "tomcat", "redirect_port",
                 {"when": ("tomcat", "install")}),
                ("Enter the archive file pathname", "tomcat", "repo",
                 {"when": ("tomcat", "install"), "type": "file"}),
                ("Enter the path where you want to unpack it", "tomcat", "directory",
                 {"when": ("tomcat", "install"), "type": "path"}),
                ("Enter the name of the directory", "tomcat", "name",
                 {"when": ("tomcat", "install")}),
                ("Enter the Catalina Home directory", "tomcat", "catalina_home",
                 {"unless": ("tomcat", "install"), "type": "path"}),
                ("Is autodeploy enabled?", "tomcat", "autodeploy",
                 {"unless": ("tomcat", "install"), "type": "YN"})
                ])


    def postConfigure(self):
        catalina_home = ""
        if self.installp:
            tomcat_repo = CONFIG.get("tomcat", "repo")
            tomcat_dir = CONFIG.get("tomcat", "directory")
            tomcat_name = CONFIG.get("tomcat", "name")
            catalina_home = tomcat_dir + tomcat_name + "/"
            CONFIG.set("tomcat", "catalina_home", catalina_home)
            utils.putMessage("The Catalina Home directory will be: " + catalina_home)
        else:
            catalina_home = CONFIG.get("tomcat", "catalina_home")
            CONFIG.set("tomcat", "name", catalina_home.rstrip("/").rpartition("/")[2])

    
    def postUnpack(self):
        utils.putWait("Configuring Apache Tomcat’s ports")
        serverConfigFile = CONFIG.get("tomcat", "catalina_home") + "conf/server.xml"
        doc = xml.dom.minidom.parse(serverConfigFile)
        configuration = doc.getElementsByTagName("Server")[0]
        configuration.setAttribute("port", CONFIG.get("tomcat", "shutdown_port"))
        properties = configuration.getElementsByTagName("Connector")
        for prop in properties:
            if prop.getAttribute("protocol") == "HTTP/1.1":
                prop.setAttribute("port", CONFIG.get("tomcat", "http_port"))
                prop.setAttribute("redirectPort", CONFIG.get("tomcat", "redirect_port"))
            elif prop.getAttribute("protocol") == "AJP/1.3":
                prop.setAttribute("port", CONFIG.get("tomcat", "ajp_port"))
                prop.setAttribute("redirectPort", CONFIG.get("tomcat", "redirect_port"))
        with open(serverConfigFile, "w") as scf:
            scf.write(doc.toxml())
        utils.putDoneOK()
        
        if CONFIG.isTrue("tomcat", "use_manager"):
            utils.putWait("Configuring Apache Tomcat’s managering account")
            usersConfigFile = CONFIG.get("tomcat", "catalina_home") + "conf/tomcat-users.xml"
            doc = xml.dom.minidom.parse(usersConfigFile)
            configuration = doc.getElementsByTagName("tomcat-users")[0]
            role = doc.createElement("role")
            role.setAttribute("rolename", "manager-script")
            configuration.appendChild(role)
            role = doc.createElement("role")
            role.setAttribute("rolename", "manager")
            configuration.appendChild(role)
            user = doc.createElement("user")
            user.setAttribute("username", CONFIG.get("tomcat", "login"))
            user.setAttribute("password", CONFIG.get("tomcat", "password"))
            user.setAttribute("roles", "manager,manager-script")
            configuration.appendChild(user)
            with open(usersConfigFile, "w") as scf:
                scf.write(doc.toxml())
            utils.putDoneOK()


    def install(self):
        # try to install
        if not installer.Installer.install(self):
            # else, get infos
            self.configure()
        utils.startTomcat()