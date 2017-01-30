#!/bin/bash -e

cd terraform

# INPUTS
CLUSTER_NAME=$1 # Cluster name as defined in main.tf module, e.g. cluster_3
shift
TESTS_TO_RUN=$* # Rest of args are test arguments

show_clusters() {
  echo Clusters available:
  echo
  terraform show | grep -oP "^.*cassandra_ips" | sed 's#_cassandra_ips##'
}

check_cluster() {
  CLUSTER=$1
  if ! terraform output | grep $CLUSTER_NAME ; then
    echo Invalid cluster!
    echo
    show_clusters
    exit 1
  fi
}

clean_test_client() {
  CLUSTER=$1
  terraform taint -module $CLUSTER_NAME.test-client null_resource.config
  terraform apply
}

# Check inputs
[[ -z $CLUSTER_NAME ]] && echo Must pass CLUSTER_NAME && echo && show_clusters && exit 1
[[ -z $TESTS_TO_RUN ]] && echo Must pass TESTS_TO_RUN, e.g. "showdown.RandomEventTest" && exit 1
echo Running tests on cluster $CLUSTER_NAME
echo Test args: $TESTS_TO_RUN

# Check cluster
check_cluster

# Find public IP for test node
IP=$(terraform output | grep "${CLUSTER_NAME}_test-client_ip" | cut -d= -f2 | sed -r 's#\s*##g' )
echo Public IP of test node: $IP

echo Cleaning test client...
clean_test_client

echo Running tests...
ssh -o StrictHostKeyChecking=no ubuntu@$IP /tmp/scripts/test-client/run.sh $TESTS_TO_RUN

# Copy results
cd ..
LOCAL_DIR=out/testLogs-$CLUSTER_NAME-$(date +%y%m%d-%H%M%S)
echo Copying results
mkdir -p $LOCAL_DIR
scp -o StrictHostKeyChecking=no ubuntu@$IP:/home/ubuntu/analysis/testLogs/* $LOCAL_DIR/
echo COPYING COMPLETE
echo Tests copied to: $LOCAL_DIR

SHARED_DEST=/s/temp/domk/$(basename $LOCAL_DIR)
cp -r $LOCAL_DIR $SHARED_DEST
echo Copied to $SHARED_DEST

# Record types of nodes
cd terraform
NUM_OF_CASSANDRA=$(terraform output | grep ${CLUSTER_NAME}_cassandra_ips | grep -oP "[\d\.\,]+" | sed "s#,#\n#g" | wc -l)
NUM_OF_DATA=$(terraform output | grep ${CLUSTER_NAME}_data | grep -oP "[\d\.\,]+" | sed "s#,#\n#g" | wc -l)
NUM_OF_SQL=$(terraform output | grep ${CLUSTER_NAME}_sql | grep -oP "[\d\.\,]+" | sed "s#,#\n#g" | wc -l)
NUM_REPLICAS=$(terraform output | grep ${CLUSTER_NAME}_num_ndb_replicas)
NUM_FRAGMENTS=$(terraform output | grep ${CLUSTER_NAME}_num_ndb_fragments)
FILE=$SHARED_DEST/details.readme
echo Number of Cassandra nodes: $NUM_OF_CASSANDRA > $FILE
echo Number of data nodes: $NUM_OF_DATA >> $FILE
echo Number of SQL nodes: $NUM_OF_SQL >> $FILE
echo Number of NDB fragments: $NUM_FRAGMENTS >> $FILE
echo Number of NDB replicas: $NUM_REPLICAS >> $FILE
