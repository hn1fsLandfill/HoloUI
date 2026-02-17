package eu.hn1f.holoui

import android.app.Application

class SystemUIApplication: Application() {
    var statusBarRunning = false

    override fun onCreate() {
        super.onCreate()
    }

    fun startServices() {
        setTheme(R.style.Theme_SystemUI)
        if(!statusBarRunning) {
            StatusBar(this).init()
            statusBarRunning = true
        }
    }
}