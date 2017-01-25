#!/bin/bash -e

# Package installs in here

# Install Java
apt-get -y update
apt-get -y install default-jdk
apt-get -y install gradle
