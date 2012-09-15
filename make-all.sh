#!/bin/sh

# stopping on failure?
stop_on_failure=''

# remember standard outputs
exec 3>&1
exec 4>&2

# redirect all outputs to a log file
LOG_FILE=make-all.log
exec 5> $LOG_FILE
exec 1>&5
exec 2>&5

# some colors
red=$( tput setaf 1 )
green=$( tput setaf 2 )
blue=$( tput setaf 4 )
norm=$( tput op )
bold=$( tput bold )
unbold=$( tput sgr0 )
code=$( tput setaf 4 )
civis=$( tput civis )
cnorm=$( tput cnorm )
sc=$( tput sc )
rc=$( tput rc )

# output utilities
p_out() {
    printf "$@" >&3
}

p_err() {
    printf "$@" >&4
}

p_info() {
    p_out "[    ] $@"
}

p_wait() {
    p_out "[${code}....${norm}] $@"
}

p_spec() {
    p_out "${civis}${sc}\r$@${rc}${cnorm}\n"
}

p_done() {
    p_spec "[${green} ok ${norm}"
}

p_failed() {
    p_spec "[${red}FAIL${norm}"
    if [ -n "$stop_on_failure" ]; then
        exit 1
    fi
}

p_bold() {
    p_info "${bold}$@${unbold}"
}


# building utilities

do_compile() {
    local name="$1"
    local dir="$2"
    local method=${3:-"mvn -DskipTests clean install"}
    p_wait "Compiling $name"
    cd "$dir"
    $method && p_done || p_failed
    cd "$OLDPWD"
}

do_pack() {
    local name="$1"
    local dir="${2:-$1}"
    p_wait "Packing $name"
    cd "$dir"
    ./make-tar.sh && p_done || p_failed
    cd "$OLDPWD"
}

do_compile_pack() {
    local name="$1"
    local dir="${2:-$1}"
    do_compile "$@"
    mkdir -p target
    cp "$dir"/target/*-bin-with-dependencies.tar.gz target/
}

# let’s go

p_bold "\n"
p_bold "        Pitiful IoTa Building Script\n"
p_info "\n"
p_info "Compilation outputs are logged in $LOG_FILE\n"
p_info "You can follow them with ${code}tail -f $LOG_FILE${norm} in another shell.\n"
p_info "\n"

p_bold "Preparation\n"
p_wait "Installing sunxacml in the local Maven repository"
mvn install:install-file			\
    -Dfile=lib/sunxacml-2.0-SNAPSHOT.jar	\
    -DgroupId=net.sf.sunxacml			\
    -DartifactId=sunxacml			\
    -Dversion=2.0-SNAPSHOT			\
    -Dpackaging=jar				\
&& p_done || p_failed
p_info "\n"

p_bold "Fosstrak Epcis\n"
do_compile "bug-free Epcis repository webapp" lib/epcis-0.5.0
p_info "\n"

p_bold "Epcis Trust Agency (η)\n"
do_compile LibXACML-EPCIS ETa/LibXACML-EPCIS
do_compile ETa-Client ETa/ETa-Client
do_compile EpcisPHI ETa/EpcisPHI
do_compile ETa-Callback-Filter ETa/ETa-Callback/ETa-Callback-Filter
do_compile ETa-Callback-Receiver ETa/ETa-Callback/ETa-Callback-Receiver
do_compile ETa-Callback-Sender ETa/ETa-Callback/ETa-Callback-Sender
do_compile ETa ETa/ETa
p_info "\n"

p_bold "Discovery Services and Trust Agency (ds and ζ)\n"
do_compile IoTa-DiscoveryWS-Client IoTa-DiscoveryWS/IoTa-DiscoveryWS-Client
do_compile LibXACML-DS DSeTa/LibXACML-DS
do_compile DiscoveryPHI DSeTa/DiscoveryPHI
do_compile IoTa-DiscoveryWS IoTa-DiscoveryWS/IoTa-DiscoveryWS
p_info "\n"

p_bold "Epcis to DS bridge (ε)\n"
do_compile EpcILoN EpcILoN
p_info "\n"

p_bold "Application Interface (α and ω)\n"
do_compile ALfA-PI ALfA/ALfA-PI
do_compile ALfA ALfA/ALfA
do_compile_pack OmICron OMeGa/OmICron
do_compile OMeGa OMeGa/OMeGa
p_info "\n"

p_bold "Applications\n"
do_compile_pack BETa BETa
do_compile_pack DELTa DELTa
do_compile_pack PSi PSi
do_compile MuPHI MuPHI
p_info "\n"

p_bold "Creating installation tarballs\n"
do_pack ThETa
do_pack IoTa-Installer
p_info "\n"
