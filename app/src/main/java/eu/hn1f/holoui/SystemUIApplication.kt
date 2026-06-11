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
// [P] Authentication stuff (Biometrics, PINs and Patterns aren't implemented)
// [X] Navigation bar
// [] Pulling the navigation bar or status bar in fullscreen apps
// [X] Notifications (mostly buggy)
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

    fun onHome() {
        val intent = Intent().apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_HOME)
        }
        startActivity(intent)
    }

    var sbns: Array<StatusBarNotification> = emptyArray()

    fun addNotification(sbn: StatusBarNotification) {
        Log.v("HoloUI", "new notification")
        runInUIThread {
            val stuff = statusBar!!.shade!!.stuff!!
            val statusBarIconList = statusBar!!.statusBar!!.findViewById<StatusBarNotificationIcons>(R.id.notificationIcons)
            val existing = stuff.findViewWithTag<Notification?>(sbn.packageName+sbn.id)

            if(existing == null) {
                val notification = Notification(this, sbn)
                statusBar!!.shade!!.stuff!!.addView(notification)
                // todo: use notificationChannel
                if(sbn.notification.sound != null)
                    Sounds(this).playUri(sbn.notification.sound)
                else
                    Sounds(this).playDefaultNotificationSound()

                sbns += arrayOf(sbn)
            } else existing.updateNotification(sbn)

            var icons = emptyArray<StatusBarNotification>()
            for (i in 0..stuff.childCount) {
                val child = stuff.getChildAt(i)

                if(child != null && (child as? Notification) != null) {
                    icons += child.sbn
                }
            }
            sbns = icons
            statusBarIconList.setIcons(sbns)
        }
    }

    fun removeNotification(sbn: StatusBarNotification) {
        Log.v("HoloUI", "bai bai")
        runInUIThread {
            val stuff = statusBar!!.shade!!.stuff!!
            val notification = stuff.findViewWithTag<Notification?>(sbn.packageName+sbn.id)
            if(notification != null) stuff.removeView(notification)
            else Log.v("HoloUI", "tried to remove null notification")
        }
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
                if(Settings.Global.getInt(contentResolver, "holoui_navbar", 1) == 1) {
                    navigationBar = NavigationBar(this)
                    navigationBar!!.init()
                }
                notificationListener = NotificationListener()
                notificationListener!!.mApplication = this
                notificationListener!!.registerAsSystemService()

                /* addNotification(StatusBarNotification(
                    "hhh", "hhh", 0, "thing", 0, 0, 0, android.app.Notification.Builder(this, "test")
                        .setSmallIcon(R.drawable.thenews)
                        .setContentTitle("BREAKING NEWS!!!")
                        .setSubText("Someone just died! Who? We don't know.")
                        .build(),
                    UserHandle.getUserHandleForUid(0), 10
                )) */
                statusBarRunning = true
            }
        }
    }
}