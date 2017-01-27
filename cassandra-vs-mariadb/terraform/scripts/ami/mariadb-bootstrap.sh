#!/bin/bash -e

# Called in AMI creation. WARNING: Changes to this file will not automatically trigger the AMIs to be recreated.

# Inputs
PASSWORD=$1
[[ -z $PASSWORD ]] && echo Must pass password && exit 1

# Install necessary packages for MariaDB nodes

# Add the MariaDB repository
apt-get install -y software-properties-common
apt-key adv --recv-keys --keyserver hkp://keyserver.ubuntu.com:80 0xF1656F24C74CD1D8
add-apt-repository 'deb [arch=amd64,i386,ppc64el] http://mirrors.coreix.net/mariadb/repo/10.1/ubuntu xenial main'
apt-get -y update

# Irritatingly, installing the MariaDB package is sufficient to bring up a load of prompts. Sigh. Here's a hack to work around that.
export DEBIAN_FRONTEND="noninteractive"
debconf-set-selections <<< "mariadb-server mysql-server/root_password password $PASSWORD"
debconf-set-selections <<< "mariadb-server mysql-server/root_password_again password $PASSWORD"
apt-get install -y mariadb-server python-mysqldb

# rsync and dos2unix
apt-get install -y rsync dos2unix libjna-java

# Ensure MariaDB is stopped and in initial state
service mysql stop
rm -rf /var/lib/mysql/*

