#!/bin/bash -e

# This script starts the local node and waits for it to join the cluster.

# Get the local IP address
# PUBLIC_IP=$(curl checkip.amazonaws.com) 2>/dev/null
PUBLIC_IP=$(ip addr | grep -oP "inet 172.[\d\.]+" | cut -d" " -f2)

# Set the timeout
TIMEOUT=30

areInCluster() {
  nodetool status | grep -P "^UN\s+$PUBLIC_IP" > /dev/null 2>&1
}

echo $HOSTNAME is attempting to join cluster...

# Start the Cassandra service
sudo service cassandra start

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


