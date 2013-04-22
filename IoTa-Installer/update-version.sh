#!/bin/sh

cd ${0%/*}
read IOTA_VERSION < ../Version
echo "Updating Installer to version number: $IOTA_VERSION"

echo "Updating install.ini configuration file ..."
cat <<EOS > resources/install.ini
[global]
accept_defaults = false
host = localhost
callback_war = true
anonymous_user = anonymous
use_tls_id = true
default_user = default-id

[ldap]
login = admin
password = admin
base_dn = dc=iota,dc=greyc,dc=fr
user_group = ou=users
user_id = uid
attribute_owner = ownerid
attribute_alias = aliasdn
url = ldap://localhost:389/
ldif_create = false
ldif_install = false

[db]
host = localhost
port = 3306
login = root
password = root
user_host = localhost
repo = resources/mysql-connector-java-5.1.21.jar
jar_install = true

[tomcat]
repo = resources/apache-tomcat-7.0.33.tar.gz
name = apache-tomcat
directory = /srv/
catalina_home = /srv/apache-tomcat/
secure_port = 8443
shutdown_port = 8005
http_port = 8080
ajp_port = 8009
autodeploy = true
use_manager = true
login = admin
password = admin
manager_path = manager/text
keystore_file = \${catalina.home}/conf/ssl/keystore.jks
keystore_password = changeit
key_alias =
key_password =
truststore_file = \${catalina.home}/conf/ssl/keystore.jks
truststore_password = changeit
revocations_file = \${catalina.home}/conf/ssl/revocations_list.pem
install = false

[activemq]
name = apache-activemq
url = tcp://localhost:61616
admin_url = http://localhost:8161/admin
login = 
password =
repo = resources/apache-activemq-5.6.0-bin.tar.gz
directory = /srv/
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
identity = anonymous
repo = resources/epcilon-${IOTA_VERSION}.war
url = http://localhost:8080/epcilon
callback_url = http://localhost:8080/epcilon/StandingQueryCallbackServlet
subscription_url = http://localhost:8080/eta/query
ds_url = http://localhost:8080/dseta
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
xacml_url = http://localhost:8080/dphi/xi

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
epcis_type = epcis
epcis_query_url = http://localhost:8080/epcis/query

[dseta]
name = dseta
repo = resources/dseta-server-${IOTA_VERSION}.war
ds_login = anonymous
ds_password = anonymous
install = false
url = http://localhost:8080/dseta

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
repo = resources/alfa-rmi-${IOTA_VERSION}-bin-with-dependencies.tar.gz
directory = /srv/
ds_login = anonymous
ds_password = anonymous
rmi_url = //localhost:1099/ALfA
rmi_name = ALfA
rmi_port = 1099
install = false

[ypsilon]
name = ypsilon
repo = resources/ypsilon-${IOTA_VERSION}.war
url = http://localhost:8080/ypsilon
install = false

[lambda]
name = lambda
repo = resources/lambda-${IOTA_VERSION}.war
install = false

[eta]
name = eta
repo = resources/eta-${IOTA_VERSION}.war
install = false
db_name = ETA_DB
db_login = eta_usr
db_password = eta_pw
db_install = false
db_jndi = ETADB
callback_db_login = callback_usr
callback_db_password = callback_pwd
db_user_create = false
use_sigma = False

[eta_callback_receiver]
name = eta-callback-receiver
repo = resources/eta-callback-receiver-${IOTA_VERSION}.war
install = false
callbackservlet_name = callback
callback_url = http://localhost:8080/eta-callback-receiver/callback
send_queue_name = queueToFilter

[eta_callback_filter]
name = eta-callback-filter
repo_bin = resources/eta-callback-filter-${IOTA_VERSION}-bin-with-dependencies.tar.gz
repo_war = resources/eta-callback-filter-${IOTA_VERSION}.war
directory = /srv/
send_queue_name = queueToSender
startup-delay = 10000
polling-delay = 60000
install = false

[eta_callback_sender]
name = eta-callback-sender
repo_bin = resources/eta-callback-sender-${IOTA_VERSION}-bin-with-dependencies.tar.gz
repo_war = resources/eta-callback-sender-${IOTA_VERSION}.war
directory = /srv/
startup-delay = 10000
polling-delay = 60000
install = false

[ephi]
name = ephi
repo = resources/epcis-phi-${IOTA_VERSION}.war
install = false
deploy_policies = true
url = https://localhost:8443/ephi/

[epcis_policies]
dir = /srv/epcis-policies/
query_dir = /srv/epcis-policies/query/
capture_dir = /srv/epcis-policies/capture/
admin_dir = /srv/epcis-policies/admin/
xacml_url = http://localhost:8080/ephi/xi

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
html_entry = epc\\\\+html
entry_regex = \\\\!\\\\^\\\\.\\\\*\\\\$\\\\!|\\\\!

[sigma]
name = sigma
repo = resources/sigma-${IOTA_VERSION}.war
install = True
url = http://localhost:8080/sigma/

[sigma_cert]
create_keystore = True
keystore = /srv/sigma-cert.p12
password = store_pw
distinguished_name = CN=anonymous
keyalias = key
keypassword =

[cert]
keystore = /srv/keystore.jks
p12_keystore = /srv/keystore.p12
jks_keystore = /srv/keystore.jks
pem_keystore = /srv/keystore.pem
password = store_pw
distinguished_name = CN=anonymous
keyalias = key
keypassword =
certfile = /srv/key.cert
truststore = /srv/truststore.jks
pem_truststore = /srv/truststore.pem
trustpassword = trust_pw
trust_keyalias = key
trust_keypassword =
create_keystore = True
create_certfile = True
create_truststore = True
create_truststore_pem = True
exportkeystore_tojks = True
exportkeystore_topem = True

EOS

# Installer links to tarballs and wars
echo "Cleaning resources links ..."
rm -f resources/alfa-rmi-*-bin-with-dependencies.tar.gz			\
    resources/discovery-phi-*.war					\
    resources/discovery-server-*.war					\
    resources/dseta-server-*.war					\
    resources/epcilon-*.war						\
    resources/epcis-phi-*.war						\
    resources/ypsilon-*.war						\
    resources/eta-*.war							\
    resources/eta-callback-receiver-*.war				\
    resources/eta-callback-filter-*-bin-with-dependencies.tar.gz	\
    resources/eta-callback-sender-*-bin-with-dependencies.tar.gz	\
    resources/omega-*.war						\
    resources/sigma-*.war						\
    resources/lambda-*.war
echo "Setting resources links ..."
ln -s ../../ALfA/ALfA-RMI/target/alfa-rmi-${IOTA_VERSION}-bin-with-dependencies.tar.gz	\
    ../../DSeTa/DiscoveryPHI/target/discovery-phi-${IOTA_VERSION}.war			\
    ../../DSeTa/DSeTa/target/dseta-server-${IOTA_VERSION}.war				\
    ../../IoTa-DiscoveryWS/IoTa-DiscoveryWS/target/discovery-server-${IOTA_VERSION}.war	\
    ../../EpcILoN/target/epcilon-${IOTA_VERSION}.war					\
    ../../ETa/EpcisPHI/target/epcis-phi-${IOTA_VERSION}.war				\
    ../../YPSilon/YPSilon/target/ypsilon-${IOTA_VERSION}.war					\
    ../../ETa/ETa/target/eta-${IOTA_VERSION}.war					\
    ../../ETa/ETa-Callback/ETa-Callback-Receiver/target/eta-callback-receiver-${IOTA_VERSION}.war			\
    ../../ETa/ETa-Callback/ETa-Callback-Filter/target/eta-callback-filter-${IOTA_VERSION}.war				\
    ../../ETa/ETa-Callback/ETa-Callback-Sender/target/eta-callback-sender-${IOTA_VERSION}.war				\
    ../../ETa/ETa-Callback/ETa-Callback-Filter/target/eta-callback-filter-${IOTA_VERSION}-bin-with-dependencies.tar.gz	\
    ../../ETa/ETa-Callback/ETa-Callback-Sender/target/eta-callback-sender-${IOTA_VERSION}-bin-with-dependencies.tar.gz	\
    ../../OMeGa/OMeGa/target/omega-${IOTA_VERSION}.war					\
    ../../SigMa/SigMa/target/sigma-${IOTA_VERSION}.war					\
    ../../LaMBDa/target/lambda-${IOTA_VERSION}.war					\
    resources/
