#!/bin/bash -e

# This script starts the local node and waits for it to join the cluster. Shouldn't be run on the first node.

# Set the timeout
TIMEOUT=30

areInCluster() {
  mysql -u root --password=myfirstpassword -e "SHOW GLOBAL STATUS LIKE 'wsrep_ready';" | grep "ON" > /dev/null 2>&1
}

# Ensure the bind-address is commented out so that the DB doesn't bind to the loopback interface.
sed -i -r 's|^\s*(bind-address.*)|# \1|' /etc/mysql/my.cnf

# Join the cluster
echo $HOSTNAME is attempting to join cluster...
sudo service mysql start

# Wait for us to join the cluster
ii=0

while ! areInCluster ; do
  ii=$((ii + 1))
  sleep 1
  if [[ $ii -gt $TIMEOUT ]]; then
    echo FAILED TO JOIN CLUSTER
    exit 1
  fi
done

echo $HOSTNAME has joined cluster


