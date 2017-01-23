#!/bin/bash

re='^[1-9]+$'
cNodes=1
mNodes=1
while test $# -gt 0; do
	case "$1" in
		-h|--help)
			echo "$run docker images"
			echo " "
			echo "$dockerImages [options]"
			echo " "
			echo "options:"
			echo "-h, --help                show brief help"
			echo "-c, specify number of nodes in cassandra cluster"
			echo "-m, specify number of nodes in maria cluster"
			exit 0
			;;
		-c)
			shift
			if test $# -gt 0; then
				if [[ $1 =~ $re ]] ; then
				    cNodes=$1
				else
					echo "No valid number of Cassandra nodes given"
					exit 1
				fi
			else
				echo "No valid number of Cassandra nodes given"
				exit 1
			fi
			shift
			;;
		-m)
			shift
			if test $# -gt 0; then
				if [[ $1 =~ $re ]] ; then
				    mNodes=$1
				else
					echo "No valid number of Maria nodes given"
					exit 1
				fi
			else
				echo "No valid number of Maria nodes given"
				exit 1
			fi
			shift
			;;
		*)
			break
			;;
	esac
done
docker stop $(docker ps -a -q)
docker rm $(docker ps -a -q)
echo "Starting Docker UI..."
docker run -d -p 9000:9000 --privileged -v /var/run/docker.sock:/var/run/docker.sock uifd/ui-for-docker

echo "starting cassandra with $cNodes nodes"
echo "starting node 1"
docker run -d --name cass1 -p 9042:9042 cassandra:latest
# This loop does not currently work because the secondary nodes try to connect to the first one before it has started up fully.
# Either restart the containers or run the commands manually.
for (( i=1; i<$cNodes; i++))
do
	nodeName=$(($i + 1))
	portNumber=$(($i + 9042))
	echo "starting node $nodeName"
	docker run -d --name cass$nodeName -d -p $portNumber:$portNumber -e CASSANDRA_SEEDS="$(docker inspect --format='{{ .NetworkSettings.IPAddress }}' cass1)" cassandra:latest
done

echo "Starting MariaDB with $mNodes nodes"
echo "starting node 1"
# Copy the config file to another location so it can then be made read only which is required by maria db
mkdir -p /opt/local/etc/mysql.conf.d
cp /vagrant/mysql_server.cnf /opt/local/etc/mysql.conf.d/mysql_server.cnf
chmod 0644 '/opt/local/etc/mysql.conf.d/mysql_server.cnf'

docker run \
  --detach=true \
  -v /opt/local/etc/mysql.conf.d:/etc/mysql/conf.d \
  -e MYSQL_INITDB_SKIP_TZINFO=yes \
  -e MYSQL_ROOT_PASSWORD=root \
  --name maria1 \
  -h maria1 \
  -p 3306:3306 \
  mariadb \
  --wsrep-cluster-name=local-test \
  --wsrep-cluster-address=gcomm://

# This loop does not currently work because the secondary nodes try to connect to the first one before it has started up fully.
# These commands for the secondary nodes need running manually.
for (( i=1; i<$mNodes; i++))
do
	nodeName=$(($i + 1))
	portNumber=$(($i + 3306))
	echo "starting node $nodeName"
    mkdir -p /mnt/data$nodeName/mysql
    docker run \
      --detach=true \
      -v /opt/local/etc/mysql.conf.d:/etc/mysql/conf.d \
      -v /mnt/data$nodeName:/var/lib/mysql \
      --name maria$nodeName \
      -h maria$nodeName \
      --link maria1:maria1 \
      -p $portNumber:3306 \
      mariadb \
      --wsrep-cluster-name=local-test \
      --wsrep-cluster-address=gcomm://maria1
done

#eof
