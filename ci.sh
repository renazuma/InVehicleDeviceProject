#!/bin/sh

. AndroidCommon/ci_setup.sh $*

cd `dirname $0`
ant $ant_arg findbugs
#ant $ant_arg sonar
ant $ant_arg uninstall clean
cd tests
ant $ant_arg uninstall findbugs
ant $ant_arg all clean emma debug install test-and-pull-results

cd ..
ant $ant_arg clean release
