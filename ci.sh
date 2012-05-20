#!/bin/sh

PATH=$PATH:/usr/local/bin

./stop_servers.sh
./start_servers.sh

sed 's%^\(.*/AndroidWebTestAPI\)$%#\1%' --in-place tests/project.properties
cat tests/project.properties
cp -ru AndroidWebTestAPI/src tests/src

sed 's%=\.\./AndroidWebAPI$%=..%' --in-place AndroidWebTestAPI/project.properties
cat AndroidWebTestAPI/project.properties

# workaround
sed 's/192\.168\.104\.63:3000/10.0.2.2:3334/' --in-place tests/src/com/kogasoftware/odt/webapi/test/WebAPITestCase.java
sed 's/192\.168\.104\.63:3333/10.0.2.2:3333/' --in-place tests/src/com/kogasoftware/odt/webapi/test/WebAPITestCase.java
sed 's/192\.168\.104\.63:3333/10.0.2.2:3333/' --in-place AndroidWebTestAPI/tests/src/com/kogasoftware/odt/webtestapi/test/WebTestAPITestCase.java

# android sdkへのパスを追加
if [ "$1" != "" ]; then
    export PATH="$1/tools:$1/platform-tools:$PATH"
    echo PATH=$PATH
fi

# adbやantに渡す引数を設定
if [ "$2" != "" ]; then
    adb_arg="-s $2"
    ant_arg="-Dadb.device.arg=\"$adb_arg\""
    echo adb_arg=$adb_arg
    echo ant_arg=$ant_arg
fi

ant $ant_arg -f AndroidCommon/ci.xml

./stop_servers.sh
