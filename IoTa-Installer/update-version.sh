#!/bin/sh

cd ${0%/*}
read IOTA_VERSION < ../Version

# Installer install.ini configuration file
cat <<EOS > resources/install.ini
[global]
accept_defaults = false
host = localhost

[ldap]
login = admin
password = admin
base_dn = dc=iota,dc=greyc,dc=fr
url = ldap://localhost:389/
ldif_create = false
ldif_install = false

[db]
host = localhost
port = 3306
login = root
password = root
repo = resources/mysql-connector-java-5.1.21.jar
jar_install = true

[tomcat]
repo = resources/apache-tomcat-7.0.29.tar.gz
name = apache-tomcat
directory = /srv/
catalina_home = /srv/apache-tomcat/
redirect_port = 8443
shutdown_port = 8005
http_port = 8080
ajp_port = 8009
autodeploy = true
use_manager = true
login = admin
password = admin
manager_path = manager/text
install = false

[epcis]
name = epcis
repo = resources/epcis-repository-0.5.0.war
db_jndi = EPCISDB
db_name = EPCIS_DB
db_login = epcis_usr
db_password = epcis_pw
db_install = false
url = http://localhost:8080/epcis
query_url = http://localhost:8080/epcis/query
capture_url = http://localhost:8080/epcis/query
install = false

[epcilon]
name = epcilon
repo = resources/epcilon-${IOTA_VERSION}.war
url = http://localhost:8080/epcilon
callback_url = http://localhost:8080/epcilon/StandingQueryCallbackServlet
db_jndi = EPCILONDB
db_name = EPCILON_DB
db_login = epcilon_usr
db_password = epcilon_pw
db_install = false
install = false

[dphi]
name = dphi
repo = resources/discovery-phi-${IOTA_VERSION}.war
install = false
url = http://localhost:8080/dphi/index.jsp

[ds_policies]
dir = /srv/ds-policies/
query_dir = /srv/ds-policies/query/
capture_dir = /srv/ds-policies/capture/
admin_dir = /srv/ds-policies/admin/
xacml_host = localhost
xacml_port = 9999
xacml_url = http://localhost:8080/dphi/xacml

[ds]
name = ds
repo = resources/discovery-server-${IOTA_VERSION}.war
login = anonymous
password = anonymous
db_jndi = DSDB
db_name = DS_DB
db_login = ds_usr
db_password = ds_pw
db_install = false
server_identity = urn:epc:id:gsrn:1.1
install = false
url = http://localhost:8080/ds/services/ESDS_Service
epcis_query_url = http://localhost:8080/epcis/query

[publisher]
my_address = ds.domain.com
login = localusr
password = localpsw
ons_hosts = localhost
multi_ds_architecture = False
my_port = 8080

[omega]
name = omega
repo = resources/omega-${IOTA_VERSION}.war
install = false

[alfa]
name = alfa
repo = resources/alfa-${IOTA_VERSION}-bin-with-dependencies.tar.gz
directory = /srv/
ds_login = anonymous
ds_password = anonymous
rmi_url = //localhost:1099/ALfA
rmi_name = ALfA
rmi_port = 1099
install = false

[eta]
name = eta
repo = resources/eta-${IOTA_VERSION}.war
userservice_name = user
userservice_url = http://localhost:8080/eta/user
install = false
db_name = ETA_DB
db_login = eta_usr
db_password = eta_pw
db_install = false
db_jndi = ETADB

[ephi]
name = ephi
repo = resources/epcis-phi-${IOTA_VERSION}.war
install = false
url = http://localhost:8080/ephi/

[epcis_policies]
dir = /srv/epcis-policies/
query_dir = /srv/epcis-policies/query/
capture_dir = /srv/epcis-policies/capture/
admin_dir = /srv/epcis-policies/admin/
xacml_port = 9998
xacml_url = http://localhost:8080/ephi/xacml

[ons]
server = localhost
create_file = True
domain_prefix = ons-peer.com.
vendor_prefix = 7.6.5.4.3.2.1.sgtin.id
email = info.acme.com.
home_page = http://www.acme.com/index.jsp
filename = db.ons-peer.com
spec_level = 2.0
epcis_entry = epc\\\\+epcis
ds_entry = epc\\\\+ds
spec_entry = epc\\\\+spec
entry_regex = \\\\!\\\\^\\\\.\\\\*\\\\$\\\\!|\\\\!

EOS

# Installer links to tarballs and wars
rm -f resources/alfa-*-bin-with-dependencies.tar.gz	\
    resources/discovery-phi-*.war			\
    resources/discovery-server-*.war			\
    resources/epcilon-*.war				\
    resources/epcis-phi-*.war				\
    resources/eta-*.war					\
    resources/omega-*.war
ln -s ../../ALfA/ALfA/target/alfa-${IOTA_VERSION}-bin-with-dependencies.tar.gz		\
    ../../DSeTa/DiscoveryPHI/target/discovery-phi-${IOTA_VERSION}.war			\
    ../../IoTa-DiscoveryWS/IoTa-DiscoveryWS/target/discovery-server-${IOTA_VERSION}.war	\
    ../../EpcILoN/target/epcilon-${IOTA_VERSION}.war					\
    ../../ETa/EpcisPHI/target/epcis-phi-${IOTA_VERSION}.war				\
    ../../ETa/ETa/target/eta-${IOTA_VERSION}.war					\
    ../../OMeGa/OMeGa/target/omega-${IOTA_VERSION}.war					\
    resources/
