# -*- coding: utf-8 -*-
#
# This program is a part of the IoTa project.
#
# Copyright © 2011-2013  Université de Caen Basse-Normandie, GREYC
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
import os.path
import shutil
import re
import time
import urllib
import readline
import xml.dom.minidom
import traceback
import sys

from config import CONFIG

# Debug
SHOW_TRACEBACK = True
HALT_ON_ERROR = True


# ActiveMQ Utilities

def isActiveMQRunning():
    try:
        cnx = urllib.urlopen(CONFIG.get("activemq", "admin_url"))
        cnx.close()
        return True
    except IOError:
        return False


def startActiveMQ():
    putWait("Starting up ActiveMQ")
    if isActiveMQRunning():
        putDoneOK("(already running)")
        return
    commandStart = ("ACTIVEMQ_OPTS_MEMORY=' ' " + CONFIG.get("activemq", "home") + "/bin/activemq start")
    if not sh_exec(commandStart):
        putDoneFail()
        return
    putDoneOK()


# Tomcat Utilities

def isTomcatRunning():
    try:
        cnx = urllib.urlopen( "http://" + CONFIG.get("global", "host") +
                              ":" + CONFIG.get("tomcat", "http_port") + "/" )
        cnx.close()
        return True
    except IOError:
        return False


def startTomcat():
    putWait("Starting up Tomcat engine")
    # verify if running
    if isTomcatRunning():
        putDoneOK("(already running)")
        return
    # ask and wait for startup
    filename = CONFIG.get("tomcat", "catalina_home") + "logs/catalina.out"
    sh_touch(filename)
    with open(filename, 'r') as logfile:
        logfile.seek(0, 2)
        # start Tomcat
        commandStart = CONFIG.get("tomcat", "catalina_home") + "bin/startup.sh"
        if not sh_exec(commandStart):
            putDoneFail()
            return
        # wait for Tomcat’s loading of the webapps
        while True:
            where = logfile.tell()
            line = logfile.readline()
            if not line:
                time.sleep(1)
                logfile.seek(where)
            else:
                if line.find("Server startup in") != -1:
                    break
    putDoneOK()

        
def stopTomcat():
    putWait("Shutting down Tomcat engine")
    # verify if running
    if not isTomcatRunning():
        putDoneOK("(not running)")
        return
    # ask and wait for shutdown
    filename = CONFIG.get("tomcat", "catalina_home") + "logs/catalina.out"
    sh_touch(filename)
    with open(filename, 'r') as logfile:
        logfile.seek(0, 2)
        # stop Tomcat
        commandStop = CONFIG.get("tomcat", "catalina_home") + "bin/shutdown.sh"
        if not sh_exec(commandStop):
            putDoneFail()
            return
        # wait for Tomcat’s unloading of the webapps
        while True:
            where = logfile.tell()
            line = logfile.readline()
            if not line:
                time.sleep(1)
                logfile.seek(where)
            else:
                if line.find("destroy") != -1:
                    break
    putDoneOK()


def manageTomcat(query):
    try:
        url = ("http://" + CONFIG.get("tomcat", "login") + ":" + CONFIG.get("tomcat", "password") +
               "@" + CONFIG.get("global", "host") + ":" + CONFIG.get("tomcat", "http_port") +
               "/" + CONFIG.get("tomcat", "manager_path") + "/")
        cnx = urllib.urlopen(url + query)
        ret = cnx.readline()
        cnx.close()
        if ret[0:2] == "OK":
            putDoneOK()
        else:
            putDoneFail(error=ret)
    except IOError as e:
        putDoneFail(error=e)


# Web Applications Utiliites

def copyWar(webapp_repo, webapp_name, conffile=None):
    webapp_path = CONFIG.get("tomcat", "catalina_home") + "webapps/" + webapp_name
    warfile = webapp_path + ".war"
    if os.path.exists(warfile):
        if not getYN("Webapp exists. Do you really want to overwrite it?"):
            return
        sh_rm(warfile)
        sh_rm_r(webapp_path)
        if conffile and os.path.exists(conffile):
            sh_rm(conffile)
    sh_cp(webapp_repo, warfile)


def deployWar(pretty_name, section_name):
    webapp_name = CONFIG.get(section_name, "name")
    if CONFIG.isTrue("tomcat", "use_manager"):
        putWait("Deploying " + pretty_name)
        manageTomcat("deploy?path=/" + webapp_name + "&update=true&war=file://" + 
                     sh_pwd() + "/" + CONFIG.get(section_name, "repo"))
    else:
        putMessage("Deploying " + pretty_name + " ...")
        stopTomcat()
        copyWar(CONFIG.get(section_name, "repo"), webapp_name)
        startTomcat()


def deployWarDB(pretty_name, section_name):
    webapp_name = CONFIG.get(section_name, "name")
    contextfile = configureDB(pretty_name, webapp_name, section_name)
    if CONFIG.isTrue("tomcat", "use_manager"):
        putWait("Deploying " + pretty_name)
        pwd = sh_pwd() + "/"
        manageTomcat("deploy?path=/" + webapp_name + "&update=true&config=file://" + pwd +
                     contextfile + "&war=file://" + pwd + CONFIG.get(section_name, "repo"))
    else:
        putMessage("Deploying " + pretty_name + " ...")
        stopTomcat()
        host_contextfile = (CONFIG.get("tomcat", "catalina_home") +
                            "conf/Catalina/localhost/" + webapp_name + ".xml")
        copyWar(CONFIG.get(section_name, "repo"), webapp_name, host_contextfile)
        webapp_contextfile = (CONFIG.get("tomcat", "catalina_home") + webapp_name + "/META-INF/context.xml")
        sh_cp(contextfile, webapp_contextfile)
        sh_cp(contextfile, host_contextfile)
        startTomcat()


def configurePropertiesWebApp(pretty_name, filename, section_name, dic):
    putMessage("Configuring " + pretty_name + " (" + filename + ".properties) ...")
    webapp = CONFIG.get(section_name, "name")
    propfilename = (CONFIG.get("tomcat", "catalina_home") + "webapps/" + webapp +
                    "/WEB-INF/classes/" + filename + ".properties")
    waitForFile(propfilename)
    putWait("Modifying file " + filename + ".properties")
    modifyPropertiesFile(propfilename, dic)


# Application Configuration Utilities

def configureProperties(pretty_name, filename, section_name, dic):
    putWait("Configuring " + pretty_name + " (" + filename + ".properties) ...")
    # create file
    propfilename = CONFIG.get(section_name, "directory") + "/" + CONFIG.get(section_name, "name") + "/" + filename + ".properties"
    sh_touch(propfilename)
    modifyPropertiesFile(propfilename, dic)


def modifyPropertiesFile(propfilename, dic):
    # read existing file
    with open(propfilename, "r") as propfile:
        lines = propfile.readlines()
    # write new version
    with open(propfilename, "w") as propfile:
        # replace old values
        for line in lines:
            words = re.split("[=:]", line)
            # don’t touch lines without = or :
            if len(words) < 2:
                propfile.write(line)
                continue
            # get the key (part before first = or :)
            key = words[0].strip()
            if key in dic:
                val = dic[key]
                if not isinstance(val, basestring):
                    val = CONFIG.get(val[0], val[1])
                # beware =
                val = val.replace("=", "\\=")
                propfile.write(key + " = " + val + "\n")
                del dic[key]
            else:
                propfile.write(line)
        # add new variables
        for key in dic.keys():
            val = dic[key]
            if not isinstance(val, basestring):
                val = CONFIG.get(val[0], val[1])
            # beware =
            val = val.replace("=", "\\=")
            propfile.write(key + " = " + val + "\n")
    putDoneOK()


# Database Utilities

def configureDB(pretty_name, webapp_name, section_name):
    putMessage("Tomcat DB configuration for " + pretty_name)
    if getSetConfigYN("Do you want to create the " + pretty_name + " database?", section_name, "db_install"):
        createDB(pretty_name, section_name)
    contextfile = "resources/" + webapp_name + "_context.xml"
    putWait("Creating " + contextfile)
    doc = xml.dom.minidom.Document()
    ctxt = doc.createElement("Context")
    ctxt.setAttribute("path", "/" + webapp_name)
    ctxt.setAttribute("reloadable", "true")
    doc.appendChild(ctxt)
    resc = doc.createElement("Resource")
    resc.setAttribute("auth", "Container")
    resc.setAttribute("defaultAutoCommit", "false")
    resc.setAttribute("type", "javax.sql.DataSource")
    resc.setAttribute("name", "jdbc/" + CONFIG.get(section_name, "db_jndi"))
    resc.setAttribute("driverClassName", "com.mysql.jdbc.Driver")
    resc.setAttribute("username", CONFIG.get(section_name, "db_login"))
    resc.setAttribute("password", CONFIG.get(section_name, "db_password"))
    url = ("jdbc:mysql://" + CONFIG.get("db", "host") + ":" + CONFIG.get("db", "port") +
           "/" + CONFIG.get(section_name, "db_name") + "?autoReconnect=true")
    resc.setAttribute("url", url)
    ctxt.appendChild(resc)
    # write result
    with open(contextfile, "w") as cf:
        doc.writexml(writer=cf, addindent="  ", encoding="utf-8")
    putDoneOK()
    return contextfile


def createDB(pretty_name, section_name):
    if not execDB("Creating " + pretty_name + "’s database", "mysql",
                  "CREATE DATABASE " + CONFIG.get(section_name, "db_name") + ";"):
        return
    if not execDB("Granting access rights", "mysql",
                  "GRANT SELECT, INSERT, UPDATE, DELETE ON " + 
                  CONFIG.get(section_name, "db_name") + ".* TO " +
                  CONFIG.get(section_name, "db_login") + "@'" + CONFIG.get("db", "user_host") + "' " +
                  "IDENTIFIED BY '" + CONFIG.get(section_name, "db_password") + "';"):
        return
    if not execDB("Creating tables", CONFIG.get(section_name, "db_name"),
                  "source resources/" + section_name + "_schema.sql;"):
        return


def execDB(msg, db, query):
    putWait(msg)
    if sh_exec("mysql --host=" + CONFIG.get("db", "host") +
                   " --port=" + CONFIG.get("db", "port") +
                   " --user=" + CONFIG.get("db", "login") +
                   " --password=" + CONFIG.get("db", "password") +
                   " --database=" + db +
                   " --execute=\"" + query + "\""):
        putDoneOK()
        return True
    putDoneFail()
    return False


def createLDAP(msg, schema):
    putWait(msg)
    if sh_exec("ldapadd -Y EXTERNAL -H ldapi:/// -f " + schema):
        putDoneOK()
        return True
    putDoneFail()
    return False    


def execLDAP(msg, ldiffile):
    putWait(msg)
    if sh_exec("ldapadd -x -D\"cn=" + CONFIG.get("ldap", "login") + "," + CONFIG.get("ldap", "base_dn") + "\" " +
               "-w \"" + CONFIG.get("ldap", "password") + "\" -f " + ldiffile):
       putDoneOK()
       return True
    putDoneFail()
    return False


# Key and certificate tool
def execKeytool(msg, keycmd, storetype, keystore, password, keyalias, keypass, other_opts):
    putWait(msg)
    if keypass:
        keypass = "-keypass \"" + keypass + "\""
    cmd =  ("keytool " + keycmd + " -storetype \"" + storetype + "\" -keystore \"" + keystore +
            "\" -storepass \"" + password + "\" -alias \"" + keyalias + "\" " + keypass)
    for opt, value in other_opts:
        if value:
            value = " \"" + value + "\""
        cmd += " " + opt + value
    if sh_exec(cmd):
        putDoneOK()
    else:
        putDoneFail()


def execSrcToDestKeyTool(msg, keycmd, srcstoretype, srckeystore, srcstorepass, srcalias, deststoretype, destkeystore, deststorepass, deststorealias, other_opts):
    putWait(msg)
    cmd = ("keytool " + keycmd + " -srcstoretype \"" + srcstoretype + "\" -srckeystore \"" + srckeystore +
           "\" -srcstorepass \"" + srcstorepass + "\" -srcalias \""+ srcalias +
           "\" -deststoretype \"" + deststoretype + "\" -destkeystore \"" + destkeystore +
           "\" -deststorepass \"" + deststorepass + "\" -destalias \"" + deststorealias + "\"")
    for opt, value in other_opts:
        if value:
            value = " \"" + value + "\""
        cmd += " " + opt + value
    if sh_exec(cmd):
        putDoneOK()
    else:
        putDoneFail()


# File and Shell Utilities

def sh_rm(filename):
    try:
        putWait("Deleting " + filename)
        os.remove(filename)
        putDoneOK()
    except Exception as e:
        putDoneFail(error=e)


def sh_rm_r(dirname):
    try:
        if dirname[-1] != "/":
            dirname += "/"
        putWait("Deleting " + dirname)
        shutil.rmtree(dirname)
        putDoneOK()
    except Exception as e:
        putDoneFail(error=e)


def sh_cp(orig, dest):
    try:
        putWait("Copying " + orig + " to " + dest)
        shutil.copy(orig, dest)
        putDoneOK()
    except Exception as e:
        putDoneFail(error=e)


def sh_touch(filename):
    # just making sure the file exists
    with open(filename, "a") as touch:
        pass


def sh_mkdir_p(path):
    try:
        os.makedirs(path)
    except:
        # we don’t care if the path already exists
        pass


def sh_pwd():
    return os.getcwd()


def sh_exec(cmd):
    with open("install.log", "a") as logfile:
        logfile.write("\n## executing <" + cmd + ">:\n")
    return os.system(cmd + " >>install.log 2>&1") == 0


def waitForFile(filename):
    putWait("Waiting for " + filename)
    while not os.path.exists(filename):
        time.sleep(1)
    putDoneOK()


def writeFile(msg, filename, content):
    putWait(msg)
    with open(filename, "w") as wfile:
        wfile.write(content)
    putDoneOK()


# Input/Output Utilities

def putTitle(title):
    print "[    ]"
    print "[    ]\033[1m " + title + "\033[0m"
    print "[    ]"


def putMessage(msg):
    for line in msg.split("\n"):
        print "[\033[1;34minfo\033[0m] " + line


def putError(msg, error=None):
    if SHOW_TRACEBACK:
        print ""
        if error:
            print error
        traceback.print_exc()
    for line in msg.split("\n"):
        print "[\033[1;31mFAIL\033[0m] " + line
    if HALT_ON_ERROR:
        sys.exit(-1)


def putAgain(msg):
    for line in msg.split("\n"):
        print "[\033[1;31mfail\033[0m] " + line


def putSuccess(msg):
    for line in msg.split("\n"):
        print "[\033[0;32m ok \033[0m] " + line


def putWarning(msg):
    for line in msg.split("\n"):
        print "[\033[0;33mWARN\033[0m] " + line


def putWait(msg):
    print "[\033[0;34m....\033[0m] " + msg,
    sys.stdout.flush()


def putDoneOK(note=None):
    putDone(note)
    putSuccess("")


def putDoneFail(note=None, error=None):
    putDone(note)
    putError("", error)


def putDone(note):
    if note:
        print note,
    sys.stdout.write("\r")


def ask(question):
    return raw_input("[ ?  ] " + question)


def getYN(question, default_value=None):
    valid = { 'y': True, 'yes': True, 'n': False, 'no': False }
    if default_value != None:
        if not default_value in [ True, False ]:
            default_value = str(default_value).lower() == "true"
        valid[''] = default_value

    prompt = { True: " (YES/no) ", False: " (yes/NO) ", None: " (Yes/No) " }
    question += prompt[default_value]

    while True:
        response = ask(question).lower()
        if response in valid:
            return valid[response]
        putWarning("Please answer 'Yes' or 'No' (or 'y' or 'n').")


def getWithDefault(question, value):
    question += ": "
    readline.set_startup_hook(lambda: readline.insert_text(value))
    try:
        return ask(question)
    finally:
        readline.set_startup_hook()


def getPathWithDefault(question, value):
    while True:
        path = getWithDefault(question, value)
        if os.path.exists(path) and os.path.isdir(path):
            if path[-1] != "/":
                path += "/"
            return path
        else:
            putAgain(path + " is not a valid folder path!")


def getFileWithDefault(question, value):
    while True:
        path = getWithDefault(question, value)
        if os.path.exists(path) and os.path.isfile(path):
            return path;
        else:
            putAgain(path + " does not exist!")


def getSetConfig(question, section, option, reader=getWithDefault):
    value = CONFIG.get(section, option)
    if CONFIG.isTrue("global", "accept_defaults"):
        putMessage(question + " : using <" + value + ">")
        return value
    new_value = reader(question, value)
    CONFIG.set(section, option, new_value)
    return new_value


def getSetConfigYN(question, section, option):
    value = getSetConfig(question, section, option, getYN)
    return str(value).lower() == "true"


def getSetConfigPath(question, section, option):
    return getSetConfig(question, section, option, getPathWithDefault)


def getSetConfigFile(question, section, option):
    return getSetConfig(question, section, option, getFileWithDefault)


def getSetConfigType(question, section, option, gstype=None):
    types = { "YN":   getSetConfigYN,
              "file": getSetConfigFile,
              "path": getSetConfigPath }
    func = getSetConfig
    if gstype in types:
        func = types[gstype]
    return func(question, section, option)
