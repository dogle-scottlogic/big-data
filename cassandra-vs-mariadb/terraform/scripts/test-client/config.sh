#!/bin/bash -e

# Options
CASSANDRA_PRIMARY=$1
MARIA_IPS=$2
CONFIG="/home/ubuntu/analysis/src/main/resources/application.conf"

[[ -z $CASSANDRA_PRIMARY ]] && echo "Must pass CASSANDRA_PRIMARY" && exit 1
[[ -z $MARIA_IPS ]] && echo "Must pass MARIA_IPS" && exit 1

sed -i -r "s#CASSANDRA_IP=.*#CASSANDRA_IP=$CASSANDRA_PRIMARY#" $CONFIG
sed -i -r "s#MARIA_IPS=.*#MARIA_IPS=\"$MARIA_IPS\"#" $CONFIG
