#!/bin/sh
# Preprocess IStatusBar and KeyguardService stuff since they vary between versions
# (e.g Android 16 vs Android 16 QPR2 have different versions of those)

. ./target.sh

CPP_ARGS=""

if [ -n "$IS_BAKLAVA_QPR2_LATER" ]; then
  CPP_ARGS="$CPP_ARGS -BAKLAVA_QPR2_LATER"
fi

toPreprocess() {
  # shellcheck disable=SC2086
  cpp $CPP_ARGS -P -o "app/src/main/java/eu/hn1f/holoui/$1" "app/src/preprocess/$1"
}

toPreprocess KeyguardService.kt
toPreprocess StatusBarImpl.kt