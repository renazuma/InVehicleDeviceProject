. `dirname $0`/../../dev/android/ci.sh $*

cd `dirname $0`
ant $ant_arg findbugs

cd tests
ant $ant_arg uninstall findbugs
ant $ant_arg all clean emma debug install test-and-pull-results

cd ..
ruby ../../dev/android/remove_debuggable.rb AndroidManifest.xml
ruby ../../dev/android/remove_debuggable.rb ../AndroidOpenJTalk/AndroidManifest.xml
ant $ant_arg clean release
