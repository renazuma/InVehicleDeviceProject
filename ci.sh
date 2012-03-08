sh `dirname $0`/../../dev/android/ci.sh $*

cd `dirname $0`
ant $ant_arg findbugs

cd tests
ant $ant_arg uninstall findbugs
tar xf $HOME/userContent/InVehicleDevice-sdcard.tar.xz
adb $adb_arg shell mkdir /mnt/sdcard/.odt
adb $adb_arg push .odt /mnt/sdcard/.odt
ant $ant_arg all clean emma debug install test-and-pull-results

cd ..
ruby ../../dev/android/remove_debuggable.rb AndroidManifest.xml
ruby ../../dev/android/remove_debuggable.rb ../AndroidOpenJTalk/AndroidManifest.xml
ant $ant_arg clean release
