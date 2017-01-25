#!/bin/bash -e

PUBLIC_IPS=$*

for IP in $PUBLIC_IPS ; do
  echo Starting $IP...
  ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@$IP sudo /tmp/scripts/cassandra/start.sh
done
