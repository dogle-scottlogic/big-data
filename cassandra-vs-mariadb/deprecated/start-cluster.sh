#!/bin/bash -e

PROG=`basename $0`

# Options
VAGRANT_ARGS=
CASSANDRA=1
MARIADB=1

usage() {
  echo "$PROG [-v] [-c] [-m] [-h]
  -v Run VMs in Virtualbox. Default: AWS
  -c Run Cassandra only
  -m Run MariaDB only
  -h Display this help
" >&2
  exit 2
}

while getopts ":vcm" opt; do
  case $opt in
    v)
      VAGRANT_ARGS="--provider=virtualbox"
      ;;
    c)
      MARIADB=
      ;;
    m)
      CASSANDRA=
      ;;
    *)
      usage
      ;;
  esac
done

# Safeguards
[[ -z $CASSANDRA ]] && [[ -z $MARIADB ]] && echo "Invalid to pass both -c and -m" >&2 && usage
NODES=
[[ -z $MARIADB ]]     && NODES=$(for i in $(seq 1 $NUM_NODES); do echo cass$i ; done)  # Start Cassandra only
[[ -z $CASSANDRA ]]   && NODES=$(for i in $(seq 1 $NUM_NODES); do echo maria$i ; done) # Start MariaDB only

# Output cleaning method
PASSWORD=myfirstpassword
TEST_CONFIG=src/main/resources/application.conf
CASSANDRA_KEY=CASSANDRA_IP
MARIA_KEY=MARIA_IPS
TEST_KEY=TEST_IP
NUM_NODES=$(grep -P "NUM_NODES\s*=" awscredentials.rb | cut -d= -f2 | sed -r 's#\s*##g')
indent() { sed 's/^/  /'; }
remove_quotes() { sed 's/\"//g'; }
get_public_ip() {
  node=$1
  aws ec2 describe-instances --filters "Name=tag:Name,Values=$node" "Name=instance-state-name,Values=running" --query 'Reservations[0].Instances[0].PublicIpAddress' | remove_quotes
}

update_test_config() {
  key=$1
  value=$2
  sed -i -r "s#$key=.*#$key=$value#" $TEST_CONFIG
}

# Check the cluster is running
echo Ensuring VMs are up...
vagrant up $VAGRANT_ARGS $NODES | indent

if [[ ! -z $CASSANDRA ]]; then
  # Start Cassandra on all 3 nodes in turn
  echo Starting Cassandra on each node in turn...
  for i in $(seq 1 $NUM_NODES)
  do
    name=cass$i
    vagrant ssh $name -- ". /tmp/start.sh" | indent
  done

  echo STARTED CASSANDRA CLUSTER
fi

if [[ ! -z $MARIADB ]]; then
  # MariaDB
  echo Starting MariaDB on each node in turn...

  # Install the DB
  echo Bringing up maria1... | indent
  vagrant ssh maria1 -- ". /tmp/install-primary.sh $PASSWORD 2>&1" | indent

  echo Ensuring this computer has access...
  . maria/setup-maria-access.sh 172.31.0.1 | indent

  echo Ensuring test vm has access...
  TEST_IP="172.31.0.$(($NUM_NODES*2+101))"
  . maria/setup-maria-access.sh $TEST_IP | indent

  for i in $(seq 2 $NUM_NODES)
  do
    name=maria$i
    echo Bringing up $name... | indent
    vagrant ssh $name -- ". /tmp/start.sh 2>&1" | indent
  done

  echo STARTED MARIADB CLUSTER
fi

 echo Updating application.conf...
 echo Adding Cassandra IP 172.31.0.101 to $TEST_CONFIG... | indent
 update_test_config $CASSANDRA_KEY 172.31.0.101

 # Get Maria public IPs
 MARIA_IPS=
 for i in $(seq $(($NUM_NODES+1)) $(($NUM_NODES*2)))
 do
   MARIA_IPS=$MARIA_IPS,"172.31.0.$((${i}+100))"
 done

 # Strip first comma
 MARIA_IPS=$( echo $MARIA_IPS | sed -r 's#^,##' )

 echo Adding Maria IPs $MARIA_IPS to $TEST_CONFIG... | indent
 update_test_config $MARIA_KEY \"$MARIA_IPS\"

 echo FINISHED UPDATING CONFIG

