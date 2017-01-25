#!/bin/bash -e

# Install and configure sysstat monitoring
apt-get install -y sysstat
sed -i 's#ENABLED="false"#ENABLED="true"#' /etc/default/sysstat
sed -i 's#5-55/10#*/5#' /etc/cron.d/sysstat
