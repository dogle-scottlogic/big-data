#!/bin/bash
echo "Starting Docker UI..."
sudo docker run -d -p 9000:9000 --privileged -v /var/run/docker.sock:/var/run/docker.sock uifd/ui-for-docker
cker.sock uifd/ui-for-docker:
echo "Starting RabbitMQ..."
sudo docker run -d -p 5672:5672 rabbitmq
echo "Starting Cassandra..."
sudo docker run -d -p 7191:7191 -p 7001:7001 -p 9160:9160 -p 9042:9042 -e CASSANDRA_BROADCARST_ADDRESS=127.0.0.1 cassandra:latest
#eof
