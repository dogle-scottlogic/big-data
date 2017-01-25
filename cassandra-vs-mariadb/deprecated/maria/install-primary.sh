#!/bin/bash -e

# Script to bootstrap the first node in a MariaDB cluster

PASSWORD=$1
if [[ -z $PASSWORD ]]; then
  echo Must pass password as first argument
  exit 1
fi

have_started() {
  sudo service mysql status 2>&1
}

if ! have_started ; then
  # Install and start
  sudo mysql_install_db

  # Ensure DB is externally reachable
  sudo sed -i -r 's|^\s*(bind-address.*)|# \1|' /etc/mysql/my.cnf
  sudo galera_new_cluster

  # Set root password
  mysqladmin -u root password "$PASSWORD"
fi

# Test command
mysql -u root -e "SHOW STATUS LIKE 'wsrep_cluster_size'" --password=$PASSWORD
