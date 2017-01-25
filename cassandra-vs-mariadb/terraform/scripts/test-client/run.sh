#!/bin/bash -e

# Options
TESTS_TO_RUN=$*
echo TESTS_TO_RUN=$TESTS_TO_RUN

# Run tests
cd /home/ubuntu/analysis
gradle clean analysis --tests $TESTS_TO_RUN

echo TESTS COMPLETED
