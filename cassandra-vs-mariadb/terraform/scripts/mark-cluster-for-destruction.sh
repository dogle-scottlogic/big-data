#!/bin/bash -e

# Script to mark all instances in a cluster for destruction on the next terraform apply
CLUSTER=$1
[[ -z $CLUSTER ]] && echo Must pass CLUSTER && exit 1

if ! (terraform show | grep "module.$CLUSTER" ) ; then
  echo $CLUSTER is not a valid cluster
  exit 1
fi

MODULES=$(terraform show | grep -P module.$CLUSTER.+aws_instance | sed -r 's|^(.*)\.aws_instance.*|\1|g' | sed -r 's|module\.(.*)|\1|g' | uniq)

for mod in $MODULES ; do
  echo "Tainting instances for $mod..."
  INSTANCES=$(terraform show | grep -P "module.$mod.+aws_instance" | sed -r 's|^.*\.(aws_instance\..*):|\1|g')

  for instance in $INSTANCES ; do
    echo "  Tainting $instance..."
    terraform taint -module $mod $instance
  done
done

terraform show | grep "module.$CLUSTER"

