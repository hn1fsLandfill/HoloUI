package eu.hn1f.holoui

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log

class NotificationListener: NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        val text = sbn!!.notification.shortCriticalText;
        // val icon = sbn.notification.smallIcon
        Log.v("HoloUI", "new notification: $text")
    }
}