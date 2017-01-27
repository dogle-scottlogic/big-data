#!/bin/bash -e

apt-get install -y libaio1 #  Dependency for mysqld
grep -q mysql /etc/group ||  groupadd mysql
grep -q mysql /etc/passwd || useradd -g mysql -s /bin/false mysql

