#!/bin/bash -e

NODE_NAME=$1
MARIA_IPS=$2
[[ -z $NODE_NAME ]] && echo Must pass name && exit 1
[[ -z $MARIA_IPS ]] && echo Must pass CSV Maria IPs && exit 1
PRIVATE_IP=$(ip addr show eth0 | grep -oP "inet [\d\.]+" | cut -d" " -f2)
GALERA_CONFIG=/etc/mysql/conf.d/galera.cnf

# Ensure MariaDB is stopped and in initial state
service mysql stop
rm -rf /var/lib/mysql/*

# Copy config into place and munge it
cp /tmp/scripts/mariadb/galera.cnf.template $GALERA_CONFIG
sed -i -r "s#IPS_CSV#$MARIA_IPS#g" $GALERA_CONFIG
sed -i -r "s#PRIVATE_IP#$PRIVATE_IP#g" $GALERA_CONFIG
sed -i -r "s#NODE_NAME#$NODE_NAME#g" $GALERA_CONFIG
