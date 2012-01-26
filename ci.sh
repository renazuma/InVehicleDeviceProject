. `dirname $0`/../../dev/android/ci.sh $*

cd `dirname $0`
ant $ant_arg debug
ant $ant_arg findbugs

cd tests
ant $ant_arg uninstall
ant $ant_arg debug
ant $ant_arg findbugs
ant $ant_arg installt
ant $ant_arg test-and-pull-results

cd ..
cp AndroidManifest.xml AndroidManifest.xml.old
ruby ../../dev/android/remove_debuggable.rb AndroidManifest.xml
cp AndroidManifest.xml AndroidManifest.xml.release
ant $ant_arg clean
ant $ant_arg release
cp AndroidManifest.xml.old AndroidManifest.xml
