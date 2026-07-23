#!/bin/bash
# constants
export DEX2JAR_ZIP="https://github.com/ThexXTURBOXx/dex2jar/releases/download/2.4.38/dex-tools-2.4.38.zip"
export DEX2JAR_SHA256="5d88e269bd120852fec68de268f6900f5466601e06ca8c16611f4377c6254e5c"

addFlag() {
  echo "export $1=1" >> app/src/target.sh
}

FOR_36.0() {
  true
}
FOR_36.1() {
  echo "Using Android 16 QPR1 flags"
  addFlag IS_BAKLAVA_QPR1_LATER
}
FOR_36.2() {
  echo "Using Android 16 QPR2 flags"
  addFlag IS_BAKLAVA_QPR1_LATER
  addFlag IS_BAKLAVA_QPR2_LATER
}

notPresent=""
isPresent() {
  printf "Checking if \"%s\" is installed: " "$1"
  if which "$1" > /dev/null 2>&1; then
    printf "yes\n"
  else
    printf "no\n"
    notPresent="$notPresent $1"
  fi
}

if [ -d "$HOME/Android/Sdk" ] && [ -z "$ANDROID_HOME" ]; then
  echo "Found SDK automatically in $HOME/Android/Sdk"
  export ANDROID_HOME="$HOME/Android/Sdk"
elif [ -z "$ANDROID_HOME" ]; then
  echo "Couldn't find the SDK :("
  echo "You can download it from https://developer.android.com/studio"
  notPresent="$notPresent android-sdk"
else
  echo "Found SDK in $ANDROID_HOME"
fi

PATH="$PATH:$ANDROID_HOME/platform-tools"

isPresent java
isPresent adb
isPresent curl
isPresent unzip
isPresent cpp

if [ -n "$notPresent" ]; then
  echo "Whoops, you need these installed:$notPresent"
  exit 1
fi

echo "Determining target device's SDK version"
SDK_VERSION="$(adb shell getprop ro.system_ext.build.version.sdk_full)"

rm -f app/src/target.sh
touch app/src/target.sh
if ! "FOR_$SDK_VERSION" 2>/dev/null; then
  echo "Your Android version (SDK $SDK_VERSION) doesn't seem to be supported :("
else
  echo "SDK $SDK_VERSION is supported!"
fi

echo "Converting device's framework.jar into an actual jar"
rm -rf dump
mkdir dump
(
  cd dump || exit 1
  curl -L "$DEX2JAR_ZIP" > d2j.zip || exit 1
  printf "Checking checksum: "
  if ! [ "$(sha256sum d2j.zip | awk '{print $1}')" = "$DEX2JAR_SHA256" ]; then
    printf "MISMATCH\n"
    exit 1
  else
    printf "OK\n"
  fi
  unzip d2j.zip
  mv dex-tools-* d2j
  echo "Pulling device's framework.jar"
  adb pull /system/framework/framework.jar framework.jar
  sh d2j/d2j-dex2jar.sh framework.jar
  cp framework-dex2jar.jar ../libs/framework.jar
) || ( echo "Couldn't convert device's framework.jar :(")

echo "You may compile HoloUI as normal now"