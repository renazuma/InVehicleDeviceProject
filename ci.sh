. `dirname $0`/../../dev/android/ci.sh $*

cd `dirname $0`
ant $ant_arg findbugs

cd tests
ant $ant_arg uninstall findbugs
ant $ant_arg all clean emma debug install test-and-pull-results

cd ..
cp AndroidManifest.xml AndroidManifest.xml.old
ruby ../../dev/android/remove_debuggable.rb AndroidManifest.xml
cp AndroidManifest.xml AndroidManifest.xml.release
ant $ant_arg clean release
cp AndroidManifest.xml.old AndroidManifest.xml
