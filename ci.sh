#!/bin/sh

. AndroidCommon/ci_setup.sh $*

cd `dirname $0`

unzip $HOME/userContent/OpenJTalk-sdcard.zip
p=com.kogasoftware.odt.invehicledevice
d=/mnt/sdcard/Android/data/$p/files/open_jtalk
#adb $adb_arg shell mkdir $d
adb $adb_arg push open_jtalk $d

ant $ant_arg ci

