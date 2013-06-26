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


class DSeTaInstaller(installer.DBWebAppInstaller):

    def __init__(self):
        installer.DBWebAppInstaller.__init__(self, "DSeTa Web Server", "dseta", [
                ("Enter the DSeTa web application name", "dseta", "name", {}),
                ("Enter the archive file pathname", "dseta", "repo", {"type": "file"}),
                ("Enter the DSeTa database name", "dseta", "db_name", {}),
                ("Enter the DSeTa database login", "dseta", "db_login", {}),
                ("Enter the DSeTa database password", "dseta", "db_password", {}),
                ("Enter the URL to the XACML module", "ds_policies", "xacml_url", {}),
                ("Use as multi DSeTa instance?", "dseta", "multi_dseta_architecture",
                 {"type": "YN"}),
                ("Enter the server identity (URL)", "dseta", "server_identity",
                 {"when": ("dseta", "multi_ds_architecture")}),
                ("Enter one or several ONS IP address(es) (comma separated)", "dseta", "ons_hosts",
                 {"when": ("dseta", "multi_ds_architecture")}),
                ("Enter the ONS Domain Prefix", "dseta", "ons_domain_prefix",
                 {"when": ("dseta", "multi_ds_architecture")}),
                ("Enter the ActiveMQ URL", "activemq", "url",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ("Enter the ActiveMQ user login (may be empty)", "activemq", "login",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ("Enter the ActiveMQ user password (may be empty)", "activemq", "password",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ("Enter the JMS queue name of events to publish", "dseta", "topublish_jms_queue_name",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ("Enter the JMS message name property to set the time of the last try to publish", "dseta", "jms_message_time_property",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ("Enter the startup delay", "dseta", "publisher_delay",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ("Enter the publishing timeout (wait for event to publish)", "dseta", "publisher_timeout",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ("Enter the publishing period (time between each launch)", "dseta", "publisher_period",
                 {"when": ("dseta", "multi_dseta_architecture")}),
                ], [
                ("application",
                 { "xacml-url": ("ds_policies", "xacml_url"),
                   "xacml-use-tls-id" : ("global", "use_tls_id"),
                   "xacml-default-user" : ("global", "default_user"),
                   "xacml-anonymous-user" : ("global", "anonymous_user"),
                   "pks-filename": ("cert", "keystore"),
                   "pks-password": ("cert", "password"),
                   "trust-pks-filename": ("cert", "truststore"),
                   "trust-pks-password": ("cert", "trustpassword"),
                   "service-id": ("dseta", "server_identity"),
                   "ons-hosts": ("dseta", "ons_hosts"),
                   "ons-domain-prefix": ("dseta", "ons_domain_prefix"),
                   "multi-dseta-architecture": ("dseta", "multi_dseta_architecture"),
                   "publisher-delay": ("dseta", "publisher_delay"),
                   "publisher-timeout": ("dseta", "publisher_timeout"),
                   "publisher-period": ("dseta", "publisher_period"),
                   "jms-url": ("activemq", "url"),
                   "jms-login": ("activemq", "login"),
                   "jms-password": ("activemq", "password"),
                   "jms-queue-name": ("dseta", "topublish_jms_queue_name"),
                   "jms-message-time-property": ("dseta", "jms_message_time_property"),
                   })
                ])


    def postConfigure(self):
        self.setSecuredURL()
        self.cset("db_jndi", "DSETADB")
        CONFIG.set("epcilon", "ds_url", self.cget("url"))
        CONFIG.set("epcilon", "iota_ided", "True")


    def postUnpack(self):
        if self.cisTrue("multi_dseta_architecture"):
            webxml_path = CONFIG.get("tomcat", "catalina_home") + "webapps/" + self.cget("name") + "/WEB-INF/web.xml"
            cmd = """sed -i '
/<\/web-app>/i\\
    <listener>\\
        <listener-class>fr.unicaen.iota.dseta.service.PublisherContextListener</listener-class>\\
    </listener>' %s""" % webxml_path
            if not utils.sh_exec(cmd):
                utils.putWarning("The Publisher listener could not be added to web.xml")
