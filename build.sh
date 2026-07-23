#!/bin/sh
mkdir -p build || exit 1
./gradlew assembleDebug || exit 1
cp app/build/outputs/apk/debug/app-debug.apk build || exit 1