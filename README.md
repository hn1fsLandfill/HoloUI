## HoloUI
> An alternative SystemUI implementation for Android 16 designed to restore the look and feel of
> Android 4.4

> NOTE: This project is in alpha, do not expect something usable yet.

### Installation

You'll need the following before you can start the guide:
- A Linux computer capable of running the Android SDK (TODO: Add a helper script for compiling
HoloUI, this README is a placeholder for now)
- Root
- [Vector](https://github.com/JingMatrix/Vector)
- [Android SDK](https://developer.android.com/studio)
- Basic commands like unzip, curl
- Java runtime (You can use the one from Android Studio, usually in android-studio/jbr/bin
or jbr/bin; must be in $PATH)

Once you have these requirements you can do the folowing:

> If building in Android Studio then you can skip running build.sh and replace step 4 with clicking
> the run button

1. Run `helper.sh` then run `build.sh`
2. You'll get a resulting HoloUI apk under the build directory.
3. Install the [DroidCSS](https://github.com/hn1fsLandfill/DroidCSS) module and enable it for 
the system framework (This is needed before installing HoloUI since HoloUI needs APIS that only
platform key-signed apps can have)
4. After installing DroidCSS and rebooting, use ADB to install the resulting APK in the build directory
5. In DroidCSS settings, enable `Redirect SystemUI` then reboot and enjoy!