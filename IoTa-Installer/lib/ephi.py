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
from config import CONFIG
import installer
import utils


class EPHIInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "EpcisPHI web application", "ephi", [
                ("Enter the EpcisPHI web application name", "ephi", "name", {}),
                ("Enter the archive file pathname", "ephi", "repo", {"type": "file"}),
                ("Enter the path where the policies will be saved", "epcis_policies", "dir", {}), # not "type": "path" as the directories are created
                ("Enter the URL of the User web service", "user", "url", {})
                ], [
                ("xacml_configuration",
                 { "query-policy-directory": ("epcis_policies", "query_dir"),
                   "capture-policy-directory": ("epcis_policies", "capture_dir"),
                   "admin-policy-directory": ("epcis_policies", "admin_dir") }),
                ("application",
                 { "eta.userservice.url": ("user", "url"),
                   "pks-filename": ("cert", "jks_keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword") })
                ] )


    def postConfigure(self):
        url = self.setSecuredURL()
        CONFIG.set("epcis_policies", "xacml_url", url + "xi")

        # set policies directories (for ETa)
        policies_dir = CONFIG.get("epcis_policies", "dir")
        admin_dir = (policies_dir + "/admin/").replace("//", "/")
        capture_dir = (policies_dir + "/capture/").replace("//", "/")
        query_dir = (policies_dir + "/query/").replace("//", "/")
        CONFIG.set("epcis_policies", "admin_dir", admin_dir)
        CONFIG.set("epcis_policies", "capture_dir", capture_dir)
        CONFIG.set("epcis_policies", "query_dir",query_dir)

        #
        utils.putMessage("Initializing policies in " + policies_dir)
        utils.sh_mkdir_p(policies_dir)
        if utils.sh_exec("tar -C " + policies_dir + " --strip-components=1 -xaf resources/epcis_policies.tar"):
            utils.putDoneOK()
        else:
            utils.putDoneFail()
