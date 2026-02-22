package eu.hn1f.holoui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.hardware.biometrics.IAuthService
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.security.authenticationpolicy.AuthenticationPolicyManager
import eu.hn1f.holoui.activities.Recents

// TODO (aka get it to a usable stage):
// [] Authentication stuff (Keyguard related probably)
// [X] Navigation bar
// [] Pulling the navigation bar or status bar in fullscreen apps
// [] Notifications (shade's already done)
// [] Volume dialog (nice to have)
// [] Power menu (android has a timeout for a fallback)

class SystemUIApplication: Application() {
    var authenticationPolicyService: AuthenticationPolicyManager? = null;
    var statusBarRunning = false
    var statusBar: StatusBar? = null
    var navigationBar: NavigationBar? = null
    var toaster: Toaster? = null
    var lowBatteryWatcher: LowBatteryWatcher? = null

    override fun onCreate() {
        super.onCreate()
        authenticationPolicyService =
            this.getSystemService(Context.AUTHENTICATION_POLICY_SERVICE) as AuthenticationPolicyManager
    }

    fun runInUIThread(r: Runnable) {
        Handler(Looper.getMainLooper()).post(r)
    }

    fun onHome() {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        }
        startActivity(intent)
    }
    fun onRecentApps() {
        val intent = Intent(this, Recents::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(intent)
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
                lowBatteryWatcher = LowBatteryWatcher(this)
                lowBatteryWatcher!!.register()
                statusBarRunning = true
            }
        }
    }
}