package eu.hn1f.holoui

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.PowerManager

// TODO (aka get it to a usable stage):
// Navigation bar
// Pulling the status bar
// Notifications

class SystemUIApplication: Application() {
    var statusBarRunning = false
    var statusBar: StatusBar? = null
    var navigationBar: NavigationBar? = null
    var toaster: Toaster? = null

    override fun onCreate() {
        super.onCreate()
    }

    fun runInUIThread(r: Runnable) {
        Handler(Looper.getMainLooper()).post(r)
    }

    fun getRebootMessage(isReboot: Boolean, reason: String?): Int {
        if (reason != null && reason.startsWith(PowerManager.REBOOT_RECOVERY_UPDATE)) {
            return R.string.reboot_to_update_reboot
        } else if (reason != null && reason == PowerManager.REBOOT_RECOVERY) {
            return R.string.reboot_to_reset_message
        } else if (isReboot) {
            return R.string.reboot_to_reset_message
        } else {
            return R.string.shutdown_progress
        }
    }


    fun startServices() {
        setTheme(R.style.Theme_SystemUI)
        if(!statusBarRunning) {
            runInUIThread {
                toaster = Toaster(this)
                statusBar = StatusBar(this)
                statusBar!!.init()
                navigationBar = NavigationBar(this)
                navigationBar!!.init()
                statusBarRunning = true
            }
        }
    }
}