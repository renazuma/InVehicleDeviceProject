#!/bin/sh

. AndroidCommon/ci_setup.sh $*

cd `dirname $0`
ant $ant_arg findbugs
#ant $ant_arg sonar

cd tests
ant $ant_arg uninstall findbugs
ant $ant_arg test-and-pull-results

cd ..
ant $ant_arg clean release
