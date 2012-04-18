#!/bin/sh

. AndroidCommon/ci_setup.sh $*

cd `dirname $0`
ant $ant_arg findbugs
#ant $ant_arg sonar

cd tests

tar xf $HOME/userContent/OpenJTalk-sdcard.tar.xz
p=com.kogasoftware.odt.invehicledevice
d=/mnt/sdcard/Android/data/$p/files/open_jtalk
#adb $adb_arg shell mkdir $d
adb $adb_arg push open_jtalk $d

ant $ant_arg uninstall findbugs
ant $ant_arg all clean emma debug install test-and-pull-results

cd ..
ant $ant_arg clean release
