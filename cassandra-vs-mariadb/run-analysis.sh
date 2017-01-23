#!/bin/bash -e

PROG=`basename $0`

# Options
VAGRANT_ARGS=
TESTS=
GRADLE_ARGS=

usage() {
  echo "$PROG [-v] [-t] [-h]
  -v Run VMs in Virtualbox. Default: AWS
  -t specify the analysis test(s) to run
  -d Run gradle in debug mode
  -h Display this help
" >&2
  exit 2
}

while getopts "vt:d" opt; do
  case $opt in
    v)
      VAGRANT_ARGS="--provider=virtualbox"
      ;;
    t)
      TESTS="--tests ${OPTARG}"
      ;;
    d)
      GRADLE_ARGS="--debug"
      ;;
    *)
      usage
      ;;
  esac
done

vagrant up test ${VAGRANT_ARGS}
gradle clean
vagrant ssh test -- "mkdir ./analysis"
vagrant scp . test:./analysis
vagrant ssh test -- "cd ./analysis; gradle clean analysis ${TESTS} ${GRADLE_ARGS}"
vagrant scp test:./analysis/testLogs .