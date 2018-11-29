#!/bin/bash

cd `dirname $0`/../../..

privacy_policy_url="http://www.aisin.co.jp/privacy/"
app_name="車載器"

function convert_strings () {
  find ./InVehicleDevice -name \*.java -o -name \*.xml -o -name \*.txt -o -name build.gradle| xargs sed -i s/kogasoftware/choisoko/g
  sed -i -r "s#(<string name=\"privacy_policy_url\">).*(</string>)#\1${privacy_policy_url}\2#g" ./InVehicleDevice/src/main/res/values-ja/strings.xml
  sed -i -r "s#(<string name=\"app_name\">).*(</string>)#\1${app_name}\2#g" ./InVehicleDevice/src/main/res/values-ja/strings.xml
}

function rename_dirs {
  if [ -e ./InvehicleDevice/src/androidTest/java/com/kogasoftware ]; then
    mv ./InVehicleDevice/src/androidTest/java/com/{kogasoftware,choisoko}
  fi
  if [ -e ./InvehicleDevice/src/main/java/com/kogasoftware ]; then
    mv ./InVehicleDevice/src/main/java/com/{kogasoftware,choisoko}
  fi
}

function overwrite_icons {
  cp -rf ./misc/choisoko/res ./InVehicleDevice/src/main
}

function overwrite_keystore {
  cp -rf ./misc/choisoko/keystore/release.keystore ./misc/keystore
}


### 実行 ###
convert_strings
rename_dirs
overwrite_icons
overwrite_keystore
