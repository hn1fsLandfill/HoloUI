package eu.hn1f.holoui

import android.content.ComponentName
import android.os.Binder
import android.os.IBinder
import android.os.RemoteException
import android.os.UserHandle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import eu.hn1f.holoui.notification.PhoneNotification

class NotificationListener: NotificationListenerService() {
    var mApplication: SystemUIApplication? = null
    var notifications: PhoneNotification? = null

    override fun onCreate() {
        super.onCreate()
        mApplication = (applicationContext as SystemUIApplication)
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.v("HoloUI", "Notification listener connected")
        notifications = PhoneNotification(mApplication)

        for(sbn in activeNotifications) {
            notifications!!.updateNotification(Binder(sbn.packageName+sbn.id.toString()), sbn);
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        notifications!!.addNotification(sbn);
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        notifications!!.removeNotification(sbn);
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