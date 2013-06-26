# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2013  Université de Caen Basse-Normandie, GREYC
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


class LaMBDaInstaller(installer.WebAppInstaller):

    def __init__(self):
        installer.WebAppInstaller.__init__(self, "LaMBDa web application", "lambda", [
                ("Enter the LaMBDa web application name", "lambda", "name", {}),
                ("Enter the archive file pathname", "lambda", "repo", {"type": "file"}),
                ("Enter the URL of the OMeGa web service", "omega", "url", {}),
                ("Enter the URL of the SigMa web service", "sigma", "url", {}),
                ("Use GaMMa?", "lambda", "use_gamma", {"type": "YN"}),
                ("Enter the GaMMa archive file path", "lambda", "gamma_repo",
                 {"when": ("lambda", "use_gamma")}),
                ("Enter the path where you want to unpack it", "lambda", "gamma_path",
                 {"when": ("lambda", "use_gamma")})
                ], [
                ("application",
                 { "omega-url": ("omega", "url"),
                   "sigma-url": ("sigma", "url"),
                   "pks-filename": ("cert", "keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword") })
                ] )


    def postConfigure(self):
        url = self.setSecuredURL()


    def postUnpack(self):
        if self.cget("use_gamma"):
            lambda_path = CONFIG.get("tomcat", "catalina_home") + "webapps/" + self.cget("name")
            gamma_path = self.cget("gamma_path")
            utils.sh_mkdir_p(gamma_path)
            detar_command = "tar -C " + gamma_path + " -xaf " + self.cget("gamma_repo")
            if utils.sh_exec(detar_command):
                gamma_path = gamma_path + "/GaMMa"
                utils.sh_cp(gamma_path+"/src/scripts/gamma.js", lambda_path+"/scripts")
                utils.sh_cp(gamma_path+"/src/styles/gamma-style.css", lambda_path+"/styles")
                openlayers_repo = gamma_path + "/src/OpenLayers-2.12.tar.gz"
                detar_command_openlayers = "tar -C " + lambda_path + " -xaf " + openlayers_repo
                if utils.sh_exec(detar_command_openlayers):
                    jsp_queryepcis = lambda_path + "/jsp/pages/queryepcis.jsp"
                    jsp_trace = lambda_path + "/jsp/pages/trace.jsp"
                    cmd = """sed -i '
/<\/head>/i\\
    <link rel="stylesheet" type="text/css" href="OpenLayers-2.12/theme/default/style.css">\\
    <link rel="stylesheet" type="text/css" href="styles/gamma-style.css">\\
    <script type="text/javascript" src="./OpenLayers-2.12/OpenLayers.js"></script>\\
    <script type="text/javascript" src="scripts/gamma.js"></script>' %(file)s
sed -i '
/<\/body>/i\\
<div id="map" class="smallmap"></div>\\
<script type="text/javascript">\\
    gamma_init(); /* OpenLayers and div map init */\\
    initShowOnMap("eventItems");\\
</script>' %(file)s """
                    utils.sh_exec(cmd % dict(file=jsp_queryepcis))
                    utils.sh_exec(cmd % dict(file=jsp_trace))
