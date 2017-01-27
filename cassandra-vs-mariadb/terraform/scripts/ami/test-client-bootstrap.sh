#!/bin/bash -e

# Called in AMI creation. WARNING: Changes to this file will not automatically trigger the AMIs to be recreated.

# Package installs in here

# Install Java
apt-get -y update
apt-get -y install default-jdk
apt-get -y install gradle
