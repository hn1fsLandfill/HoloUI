package eu.hn1f.holoui

import android.os.Handler
import android.os.Looper

fun runInUIThread(r: Runnable) {
    Handler(Looper.getMainLooper()).post(r)
}