#!/bin/sh
# I LOVE ANDROID GRADLE PLUGIN OVERRIDDING
# MY SHIT!!!!

rm -rf build
mkdir -p build

cd src
javac -d ../build -target 11 -source 11 -cp ../../libs/framework.jar eu/hn1f/holoui/transitions/*.java
cd ../build
jar cvf ../animationEngine.jar *
cd ..

rm -rf build
