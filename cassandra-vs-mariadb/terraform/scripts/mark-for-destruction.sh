#!/bin/bash -e
# Script to instances in a cluster for destruction on the next terraform apply
CLUSTER=$1
INSTANCE=$2
[[ -z $CLUSTER ]] && echo Must pass CLUSTER && exit 1

if ! (terraform show | grep -P "module.$CLUSTER[\w\.]+$INSTANCE" ) ; then
  echo $CLUSTER or $INSTANCE are not valid
  exit 1
fi

MODULES=$(terraform show | grep -P module.$CLUSTER.+aws_instance.+$INSTANCE | sed -r 's|^(.*)\.aws_instance.*|\1|g' | sed -r 's|module\.(.*)|\1|g' | uniq)

for mod in $MODULES ; do
  INSTANCES=$(terraform show | grep -oP "module.$mod.+aws_instance\.$INSTANCE\S*" | sed -r 's|^.*\.(aws_instance\.\S*):|\1|g')

  for instance in $INSTANCES ; do
    echo "  Tainting $instance..."
    terraform taint -module $mod $instance
  done
done

terraform show | grep "module.$CLUSTER"

