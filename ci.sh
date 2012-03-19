#!/bin/sh

. `dirname $0`/../../dev/android/ci_setup.sh $*

cd `dirname $0`
ant $ant_arg findbugs

cd tests
ant $ant_arg uninstall findbugs

tar xf $HOME/userContent/OpenJTalk-sdcard.tar.xz
p=com.kogasoftware.odt.invehicledevice
d=/mnt/sdcard/Android/data/$p/files/open_jtalk
#adb $adb_arg shell mkdir $d
adb $adb_arg push open_jtalk $d

ant $ant_arg all clean emma debug install test-and-pull-results

cd ..
ruby ../../dev/android/remove_debuggable.rb AndroidManifest.xml
ruby ../../dev/android/remove_debuggable.rb ../AndroidOpenJTalk/AndroidManifest.xml
ant $ant_arg clean release
