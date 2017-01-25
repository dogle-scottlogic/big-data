#!/bin/bash -e

cd terraform

show_clusters() {
  echo Clusters available:
  echo
  terraform show | grep -oP "^.*cassandra_ips" | sed 's#_cassandra_ips##'
}

CLUSTER_NAME=$1 # Cluster name as defined in main.tf module, e.g. cluster_3
shift
TESTS_TO_RUN=$* # Rest of args are test arguments
[[ -z $CLUSTER_NAME ]] && echo Must pass CLUSTER_NAME && echo && show_clusters && exit 1
[[ -z $TESTS_TO_RUN ]] && echo Must pass TESTS_TO_RUN, e.g. "showdown.RandomEventTest" && exit 1
echo Running tests on cluster $CLUSTER_NAME
echo Test args: $TESTS_TO_RUN

# Check cluster
if ! terraform output | grep $CLUSTER_NAME ; then
  echo Invalid cluster!
  echo
  show_clusters
  exit 1
fi

# Find public IP for test node
IP=$(terraform output | grep "${CLUSTER_NAME}_test-client_ip" | cut -d= -f2 | sed -r 's#\s*##g' )
echo Public IP of test node: $IP

echo Running tests...
ssh -o StrictHostKeyChecking=no ubuntu@$IP /tmp/scripts/test-client/run.sh $TESTS_TO_RUN

echo TESTS COMPLETED
