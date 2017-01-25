#!/bin/bash -e
IP_TO_ADD=$1
if [[ -z $IP_TO_ADD ]]; then
  echo Must pass IP to add
  exit 1
fi
vagrant ssh maria1 -- ". /tmp/allow-access.sh $IP_TO_ADD 2>&1"
