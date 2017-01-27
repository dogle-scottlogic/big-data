#!/bin/bash -e

# Called in AMI creation. WARNING: Changes to this file will not automatically trigger the AMIs to be recreated.

MYSQL_CLUSTER_PACKAGE=https://dev.mysql.com/get/Downloads/MySQL-Cluster-7.5/mysql-cluster-gpl-7.5.5-debian8-x86_64.deb

echo Downloading mysql-cluster...
wget -q -O /tmp/mysql-cluster.deb $MYSQL_CLUSTER_PACKAGE
dpkg -i /tmp/mysql-cluster.deb

# Setup up symlinks
for EXE in /opt/mysql/server-*/bin/* ; do
  ln -s $EXE /usr/bin/$(basename $EXE)
done
