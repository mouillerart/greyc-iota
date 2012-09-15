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
from config import CONFIG
import installer
import utils


class DPHIInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "DiscoveryPHI web application", "dphi", [
                ("Enter the DiscoveryPHI web application name", "dphi", "name", {}),
                ("Enter the archive file pathname", "dphi", "repo", {"type": "file"}),
                ("Enter the path where the policies will be saved", "ds_policies", "dir", {}), # not "type": "path" as the directories are created
                ("Enter the port that will be used between DSeTa (the DS) and XACML module", "ds_policies", "xacml_port", {}),
                ("Enter the URL of the Discovery Web Services", "ds", "url", {})
                ], [
                ("xacml_configuration",
                 { "query-policy-directory": ("ds_policies", "query_dir"),
                   "capture-policy-directory": ("ds_policies", "capture_dir"),
                   "admin-policy-directory": ("ds_policies", "admin_dir") }),
                ("application", 
                 { "xacml-service-port": ("ds_policies", "xacml_port"),
                   "ds-address": ("ds", "url") })
                ] )


    def postConfigure(self):
        # set default urls (for DSeTa)
        url = "http://" + CONFIG.get("global", "host") + ":" + CONFIG.get("tomcat", "http_port") + "/" + CONFIG.get("dphi", "name")
        CONFIG.set("dphi", "url", url + "/index.jsp")
        CONFIG.set("ds_policies", "xacml_url", url + "/xacml")
        CONFIG.set("ds_policies", "xacml_host", CONFIG.get("global", "host"))

        # set policies directories (for DSeTa)
        policies_dir = CONFIG.get("ds_policies", "dir")
        admin_dir = (policies_dir + "/admin/").replace("//", "/")
        capture_dir = (policies_dir + "/capture/").replace("//", "/")
        query_dir = (policies_dir + "/query/").replace("//", "/")
        CONFIG.set("ds_policies", "admin_dir", admin_dir)
        CONFIG.set("ds_policies", "capture_dir", capture_dir)
        CONFIG.set("ds_policies", "query_dir",query_dir)

        #
        utils.putWait("Initializing policies in " + policies_dir)
        utils.sh_mkdir_p(policies_dir)
        if utils.sh_exec("tar -C " + policies_dir + " --strip-components=1 -xaf resources/ds_policies.tar"):
            utils.putDoneOK()
        else:
            utils.putDoneFail()
