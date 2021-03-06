#!/bin/bash -e

# Called in AMI creation. WARNING: Changes to this file will not automatically trigger the AMIs to be recreated.

# Install Java
apt-get update -y
apt-get install -y default-jre dos2unix libjna-java

# Install Cassandra as a service

echo deb http://debian.datastax.com/community stable main | tee /etc/apt/sources.list.d/cassandra.sources.list
curl http://debian.datastax.com/debian/repo_key > /tmp/key
apt-key add /tmp/key
apt-get update -y
apt-get install -y cassandra

# Cassandra starts automatically. Stop it and clear the data so that we can configure it properly
service cassandra stop
rm -rf /var/lib/cassandra/*


