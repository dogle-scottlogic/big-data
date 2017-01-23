#!/bin/bash -e
exec 2>&1
sudo apt-get update

# Update apt sources
sudo apt-get install -y apt-transport-https ca-certificates
sudo apt-key adv \
               --keyserver hkp://ha.pool.sks-keyservers.net:80 \
               --recv-keys 58118E89F3A912897C070ADBF76221572C52609D
echo deb https://apt.dockerproject.org/repo ubuntu-precise main | sudo tee /etc/apt/sources.list.d/docker.list
sudo apt-get update

# Need support for autofs storage driver
sudo apt-get install -y linux-image-extra-$(uname -r) linux-image-extra-virtual

# Install docker
sudo apt-get install -y docker.engine

# Add vagrant user to docker group. Allows docker commands to be run without sudo
sudo usermod -aG docker vagrant

