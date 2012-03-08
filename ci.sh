sh `dirname $0`/../../dev/android/ci.sh $*

cd `dirname $0`
ant $ant_arg uninstall findbugs

cd tests
ant $ant_arg uninstall findbugs
ant $ant_arg all clean emma instrument install
ant $ant_arg emma test-and-pull-results

cd ..
ruby ../../dev/android/remove_debuggable.rb AndroidManifest.xml
ant $ant_arg clean release
cp bin/classes.jar bin/androidodtwebapi.jar

