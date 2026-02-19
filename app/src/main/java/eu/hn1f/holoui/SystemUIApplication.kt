package eu.hn1f.holoui

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.os.Handler
import android.os.Looper
import android.view.WindowManager

class SystemUIApplication: Application() {
    var statusBarRunning = false
    var statusBar: StatusBar? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun runInUIThread(r: Runnable) {
        Handler(Looper.getMainLooper()).post(r)
    }

    fun startServices() {
        setTheme(R.style.Theme_SystemUI)
        if(!statusBarRunning) {
            runInUIThread {
                statusBar = StatusBar(this)
                statusBar!!.init()
                statusBarRunning = true
            }
        }
    }
}