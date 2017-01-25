#!/bin/bash -e

# Check inputs
SEED_IP=$1
echo SEED_IP=$SEED_IP
CONFIG=/etc/cassandra/cassandra.yaml
[[ -z $SEED_IP ]]      && echo Must pass seed IP && exit 1

# Ensure Cassandra is in a stopped, clean state.
service cassandra stop
rm -rf /var/lib/cassandra/*

# Crudely hack our settings into the config yaml file

# Use first VM as seed
sed -i -r "s/seeds: .+/seeds: \"$SEED_IP\"/" $CONFIG

# Ensure listen_address is not defined
sed -i -r '/listen_address:/d' $CONFIG

# Comment out settings
sed -i -r "s/^rpc_address: .+/# rpc_address: 0.0.0.0/" $CONFIG
sed -i -r "s/^broadcast_address: .+/# broadcast_address:/" $CONFIG
sed -i -r "s/^listen_interface: .+/# listen_interface:/" $CONFIG
sed -i -r "s/^broadcast_rpc_address: .+/# broadcast_rpc_address:/" $CONFIG
