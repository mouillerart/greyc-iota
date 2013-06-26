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


class DSInstaller(installer.DBWebAppInstaller):

    def __init__(self):
        installer.DBWebAppInstaller.__init__(self, "Discovery Web Server", "ds", [
                ("Enter the DS web application name", "ds", "name", {}),
                ("Enter the archive file pathname", "ds", "repo", {"type": "file"}),
                ("Enter the DS database name", "ds", "db_name", {}),
                ("Enter the DS database login", "ds", "db_login", {}),
                ("Enter the DS database password", "ds", "db_password", {}),
                ("Use as multi DS instance?", "ds", "multi_ds_architecture",
                 {"type": "YN"}),
                ("Enter the server identity (URL)", "ds", "server_identity",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter one or several ONS IP address(es) (comma separated)", "ds", "ons_hosts",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the ONS Domain Prefix", "ds", "ons_domain_prefix",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the ActiveMQ URL", "activemq", "url",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the ActiveMQ user login (may be empty)", "activemq", "login",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the ActiveMQ user password (may be empty)", "activemq", "password",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the JMS queue name of events to publish", "ds", "topublish_jms_queue_name",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the JMS message name property to set the time of the last try to publish", "ds", "jms_message_time_property",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the startup delay", "ds", "publisher_delay",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the publishing timeout (wait for event to publish)", "ds", "publisher_timeout",
                 {"when": ("ds", "multi_ds_architecture")}),
                ("Enter the publishing period (time between each launch)", "ds", "publisher_period",
                 {"when": ("ds", "multi_ds_architecture")}),
                ], [
                ("application",
                 { "service-id": ("ds", "server_identity"),
                   "ons-hosts": ("ds", "ons_hosts"),
                   "ons-domain-prefix": ("ds", "ons_domain_prefix"),
                   "multi-ds-architecture": ("ds", "multi_ds_architecture"),
                   "publisher-delay": ("ds", "publisher_delay"),
                   "publisher-timeout": ("ds", "publisher_timeout"),
                   "publisher-period": ("ds", "publisher_period"),
                   "jms-url": ("activemq", "url"),
                   "jms-login": ("activemq", "login"),
                   "jms-password": ("activemq", "password"),
                   "jms-queue-name": ("ds", "topublish_jms_queue_name"),
                   "jms-message-time-property": ("ds", "jms_message_time_property"),
                   })
                ])


    def postConfigure(self):
        self.setURL()
        self.cset("db_jndi", "DSDB")
        CONFIG.set("epcilon", "ds_url", self.cget("url"))
        CONFIG.set("epcilon", "iota_ided", "False")


    def postUnpack(self):
        if self.cisTrue("multi_ds_architecture"):
            webxml_path = CONFIG.get("tomcat", "catalina_home") + "webapps/" + self.cget("name") + "/WEB-INF/web.xml"
            cmd = """sed -i '
/<\/web-app>/i\\
    <listener>\\
        <listener-class>fr.unicaen.iota.ds.service.PublisherContextListener</listener-class>\\
    </listener>' %s""" % webxml_path
            if not utils.sh_exec(cmd):
                utils.putWarning("The Publisher listener could not be added to web.xml")
