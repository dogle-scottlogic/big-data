#!/bin/bash -e

# Two step template process to work around limitations in terraform's templating engine

# Templated variables
MGMT_IPS=${mgmt_ips}
DATA_IPS=${data_ips}
MYSQL_IPS="${mysql_ips}"

# Initialise file
mkdir -p /var/lib/mysql-cluster
CONFIG_FILE=/var/lib/mysql-cluster/config.ini
rm -f $CONFIG_FILE
NODE_ID=0

cat >> $CONFIG_FILE <<FILE
[ndbd default]
NoOfReplicas=${num_of_replicas}

FILE

for MGMT_NODE in $MGMT_IPS ; do
  NODE_ID=$(($NODE_ID + 1))
  cat >> $CONFIG_FILE <<FILE
[ndb_mgmd]
HostName=$MGMT_NODE
NodeId=$NODE_ID
datadir=/var/lib/mysql-cluster

FILE
done

for DATA_NODE in $DATA_IPS ; do
  NODE_ID=$(($NODE_ID + 1))
  cat >> $CONFIG_FILE <<FILE
[ndbd]
HostName=$DATA_NODE
NodeId=$NODE_ID
datadir=/usr/local/mysql/data

FILE
done

for MYSQL_NODE in $MYSQL_IPS ; do
  NODE_ID=$(($NODE_ID + 1))
  cat >> $CONFIG_FILE <<FILE
[mysqld]
NodeId=$NODE_ID
HostName=$MYSQL_NODE

FILE
done

echo $CONFIG_FILE configured
