#!/bin/sh

. AndroidCommon/ci_setup.sh $*

cd `dirname $0`
ant $ant_arg ci
