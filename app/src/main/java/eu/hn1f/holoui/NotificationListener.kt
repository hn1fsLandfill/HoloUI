package eu.hn1f.holoui

import android.content.ComponentName
import android.os.RemoteException
import android.os.UserHandle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener: NotificationListenerService() {
    var mApplication: SystemUIApplication? = null

    override fun onCreate() {
        super.onCreate()
        mApplication = (applicationContext as SystemUIApplication)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.v("HoloUI", "Notification listener connected")

        /* for(sbn in activeNotifications) {
            notifications!!.addNotification(sbn.packageName+sbn.id, sbn);
        } */
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
    }

    fun registerAsSystemService() {
        try {
            registerAsSystemService(
                mApplication!!,
                ComponentName(mApplication!!.getPackageName(), javaClass.getCanonicalName()),
                UserHandle.USER_ALL
            )
        } catch (e: RemoteException) {
            Log.e("HoloUI", "Unable to register notification listener", e)
        }
    }

}