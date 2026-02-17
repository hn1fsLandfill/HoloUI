package eu.hn1f.holoui

import android.app.Application

class SystemUIApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }

    fun startServices() {
        setTheme(R.style.Theme_SystemUI)
    }
}