#!/bin/bash -e

# A script for ensuring a user is set up on MariaDB allowing access from this PC

# The public IP to authorise
IP_TO_ADD=$1
PASSWORD=myfirstpassword
if [[ -z $IP_TO_ADD ]]; then
  echo Must pass an IP to authorise as the first parameter
  exit 1
fi

have_access() {
  sudo mysql -u root --password=$PASSWORD -e "SELECT Host FROM mysql.user WHERE HOST LIKE '$IP_TO_ADD'" | grep $IP_TO_ADD
}

if ! have_access ; then
  # Grant privileges to our IP
  echo Allowing access from $IP_TO_ADD...
  sudo mysql -u root --password=$PASSWORD -e "GRANT ALL PRIVILEGES ON *.* TO 'root'@'$IP_TO_ADD' IDENTIFIED BY '$PASSWORD' WITH GRANT OPTION"
else
  echo Already have access!
  exit
fi

# Check we have access now
if have_access ; then
  echo Access configured!
  exit
else
  echo FAILED
  exit 1
fi

