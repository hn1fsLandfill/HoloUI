## HoloUI
> An alternative SystemUI implementation for Android 16 designed to restore the look and feel of
> Android 4.4

> NOTE: This project is in alpha, do not expect something usable yet.

### Installation

You'll need the following before you can start the guide:
- A Linux computer capable of running the Android SDK (TODO: Add a helper script for compiling
HoloUI, this README is a placeholder for now)
- Root
- [LSPosed](https://github.com/JingMatrix/LSPosed)

1. Using ADB pull the framework.jar from your ROM (`adb pull /system/framework/framework.jar .`)
2. Finally run `helper.sh` with a path to your framework.jar
3. You'll get the resulting HoloUI apk under the build directory.
4. Install the [DroidCSS](https://github.com/hn1fsLandfill/DroidCSS) module and enable it for all
 recommended apps under LSPosed's
 settings. This is required to force Android to use HoloUI instead of it's SystemUI implementation
 and also allow installation of HoloUI without requiring the signing keys of the ROM.
5. After installing DroidCSS and rebooting, use ADB to install the resulting APK.
(`adb install build/HoloUI.apk`)