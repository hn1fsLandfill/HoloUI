#!/bin/sh

rm -rf build
mkdir -p build

cd src
javac -d ../build -target 11 -source 11 -classpath ~/Android/Sdk/platforms/android-36.1/android.jar android/*/*.java
cd ../build
jar cvf ../stubs.jar *
cd ..

rm -rf build
