#!/bin/bash -e

PASSWORD=myfirstpassword

MGMT_IPS=$(terraform output | grep mgmt_public_ips | grep -oP "[\d,\.]+" | sed 's#,# #g')

SQL_IPS=$(terraform output | grep sql_public_ips | grep -oP "[\d,\.]+" | sed 's#,# #g')
DATA_IPS=$(terraform output | grep data_public_ips | grep -oP "[\d,\.]+" | sed 's#,# #g')
TEST_IP=$(terraform output | grep test-client_private_ip | grep -oP "[\d,\.]+" )
START_CMD='nohup mysqld_safe --ndbcluster --ndb-connectstring=$(grep ndb-connectstring /etc/my.cnf | cut -d= -f2) --datadir=/var/lib/mysql --pid-file=/var/lib/mysql/mysql.pid --syslog --user=ubuntu >/tmp/output.txt 2>&1 &'
SSH_OPTS="-o StrictHostKeyChecking=no -o UserKnownHostsFile=/dev/null"

#for IP in $MGMT_IPS ; do
#  echo Restarting management node $IP...
#  ssh $SSH_OPTS ubuntu@$IP -- "sudo pkill ndb_mgmd || true"
#  ssh $SSH_OPTS ubuntu@$IP -- sudo ndb_mgmd -f /var/lib/mysql-cluster/config.ini --ndb-nodeid=1 --reload
#done
#
#for IP in $DATA_IPS ; do
#    echo Restarting data node $IP...
#    ssh $SSH_OPTS ubuntu@$IP -- "sudo pkill ndbd || true"
#    ssh $SSH_OPTS ubuntu@$IP -- "sudo ndbd"
#done

for IP in $SQL_IPS ; do
  echo Ensuring $IP is started...
  # ssh $SSH_OPTS ubuntu@$IP -- "sudo pkill mysqld || true"
  ssh $SSH_OPTS ubuntu@$IP -- $START_CMD
done

for IP in $SQL_IPS ; do
  echo Setting root password and granting access on $IP...
  ssh $SSH_OPTS ubuntu@$IP -- "mysqladmin -u root password \"$PASSWORD\" || mysqladmin -u root --password=$PASSWORD password \"$PASSWORD\""
  ssh $SSH_OPTS ubuntu@$IP -- "mysql -u root --password=$PASSWORD -e \"GRANT ALL PRIVILEGES ON *.* TO 'root'@'$TEST_IP' IDENTIFIED BY '$PASSWORD' WITH GRANT OPTION\""
done
