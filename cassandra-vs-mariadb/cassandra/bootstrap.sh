#!/bin/bash -e

# Install Java
apt-get -y update
apt-get -y install default-jre dos2unix libjna-java

# Install Cassandra as a service

echo deb http://debian.datastax.com/community stable main | sudo tee /etc/apt/sources.list.d/cassandra.sources.list
curl http://debian.datastax.com/debian/repo_key > /tmp/key
apt-key add /tmp/key
apt-get -y update
apt-get -y install cassandra

# Cassandra starts automatically. Stop it and clear the data so that we can configure it properly
service cassandra stop
rm -rf /var/lib/cassandra/*
