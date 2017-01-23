#!/bin/bash -e
# Crudely hack our settings into the config yaml file
CONFIG=/etc/cassandra/cassandra.yaml
# PUBLIC_IP=$(curl checkip.amazonaws.com)
PUBLIC_IP=$(ip addr | grep -oP "inet 172.[\d\.]+" | cut -d" " -f2)
INTERFACE=$(ip -o addr | grep "172.31" | grep -oP "^\d: \w+" | cut -d" " -f2)

# Use first VM as seed
sed -i -r "s/seeds: .+/seeds: 'PRIMARY_IP'/" $CONFIG

# Uncomment listen interface setting
sed -i -r "s/(^|#\s*)listen_interface: .+/listen_interface: $INTERFACE/" $CONFIG

# Ensure listen_address is not defined
sed -i -r '/listen_address:/d' $CONFIG

# Set broadcast_address to PUBLIC_IP so that the cluster is discoverable by clients
sed -i -r "s/(^|#\s*)broadcast_address: .+/broadcast_address: $PUBLIC_IP/" $CONFIG

# Set rpc_address
sed -i -r "s/(^|#\s*)rpc_address: .+/rpc_address: 0.0.0.0/" $CONFIG

# broadcast rpc address
sed -i -r "s/(^|#\s*)broadcast_rpc_address: .+/broadcast_rpc_address: $PUBLIC_IP/" $CONFIG
