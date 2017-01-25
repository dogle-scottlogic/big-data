#!/bin/bash -e

PUBLIC_IPS=$*

for IP in $PUBLIC_IPS ; do
  ssh -o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null ubuntu@$IP sudo /tmp/scripts/mariadb/start.sh
done
