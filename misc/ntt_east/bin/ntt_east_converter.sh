#!/bin/bash

cd `dirname $0`/../../..

privacy_policy_url="https://www.ntt-east.co.jp/policy/"
app_name="車載器"

function convert_strings () {
  find ./InVehicleDevice \( -name '*.java' -o -name '*.kt' -o -name '*.xml' -o -name '*.txt' -o -name build.gradle \) -type f | xargs sed -i s/kogasoftware/odekakedemand/g
  sed -i -r "s#(<string name=\"privacy_policy_url\">).*(</string>)#\1${privacy_policy_url}\2#g" ./InVehicleDevice/src/main/res/values-ja/strings.xml
  sed -i -r "s#(<string name=\"app_name\">).*(</string>)#\1${app_name}\2#g" ./InVehicleDevice/src/main/res/values-ja/strings.xml
}

function rename_dirs {
  if [ -e ./InvehicleDevice/src/androidTest/java/com/kogasoftware ]; then
    mv ./InVehicleDevice/src/androidTest/java/com/{kogasoftware,odekakedemand}
  fi
  if [ -e ./InvehicleDevice/src/main/java/com/kogasoftware ]; then
    mv ./InVehicleDevice/src/main/java/com/{kogasoftware,odekakedemand}
  fi
}

function overwrite_diffs {
  cp -rf ./misc/ntt_east/res ./InVehicleDevice/src/main
}

function overwrite_keystore {
  cp -rf ./misc/ntt_east/keystore/release.keystore ./misc/keystore
}


### 実行 ###
convert_strings
rename_dirs
overwrite_diffs
overwrite_keystore
