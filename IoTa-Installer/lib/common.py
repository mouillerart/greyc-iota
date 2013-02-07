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
import installer
from config import CONFIG


class GlobalConfigurer(installer.Configurer):

    def __init__(self):
        installer.Configurer.__init__(self, "Global", "global", [
                ("Enter this server hostname (as it will be known to the clients)", "global", "host", {}),
                ("Enter the anonymous user identity", "global", "anonymous_user", {}),
                ("Is the TLS client ID used?", "global", "use_tls_id", {"type": "YN"}),
                ("Enter the default user identity", "global", "default_user", {"unless": ("global", "use_tls_id")})
                ])
