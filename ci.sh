#!/bin/sh

git clone -b master git@github.com:odt/InVehicleDevice.git
dir=src/com/kogasoftware/odt/invehicledevice/logic/
mkdir -p $dir
cp InVehicleDevice/$dir/SharedPreferencesKey.java $dir

. AndroidCommon/ci_setup.sh $*

cd `dirname $0`
ant $ant_arg ci
