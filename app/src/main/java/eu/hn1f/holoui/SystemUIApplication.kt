package eu.hn1f.holoui

import android.app.Application
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.os.UserHandle
import android.provider.Settings
import android.security.authenticationpolicy.AuthenticationPolicyManager
import android.service.notification.StatusBarNotification
import android.util.Log
import com.android.internal.policy.IKeyguardStateCallback
import eu.hn1f.holoui.recent.Recents
import eu.hn1f.holoui.widgets.Notification
import eu.hn1f.holoui.widgets.StatusBarNotificationIcons

// TODO (aka get it to a usable stage):
// [P] Authentication stuff (Biometrics and Patterns aren't implemented)
// [P] Navigation bar (Landscape navbar isn't implemented)
// [] Pulling the navigation bar or status bar in fullscreen apps
// [P] Notifications (mostly buggy)
// [] Volume dialog (nice to have)
// [] Power menu (android has a timeout for a fallback)

class SystemUIApplication: Application() {
    var statusBarRunning = false
    var statusBar: StatusBar? = null
    var notificationListener: NotificationListener? = null
    var navigationBar: NavigationBar? = null
    var toaster: Toaster? = null
    var lowBatteryWatcher: LowBatteryWatcher? = null
    var stateCallback: IKeyguardStateCallback? = null
    var recents: Recents? = null

    var authenticationForm: Authentication? = null

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
                recents = Recents(this)
                authenticationForm = Authentication(this)
                toaster = Toaster(this)
                statusBar = StatusBar(this)
                statusBar!!.init()
                lowBatteryWatcher = LowBatteryWatcher(this)
                lowBatteryWatcher!!.register()
                navigationBar = NavigationBar(this)
                navigationBar!!.init()
                notificationListener = NotificationListener()
                notificationListener!!.mApplication = this
                notificationListener!!.registerAsSystemService()

                statusBarRunning = true
            }
        }
    }
}