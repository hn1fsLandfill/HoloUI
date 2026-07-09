package eu.hn1f.holoui.notification

import android.content.ComponentName
import android.os.RemoteException
import android.os.UserHandle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.LinearLayout
import eu.hn1f.holoui.NotificationShade
import eu.hn1f.holoui.R
import eu.hn1f.holoui.SystemUIApplication

class NotificationListener: NotificationListenerService() {
    var mApplication: SystemUIApplication? = null
    // TODO: Group them (maybe have a NotificationRowGroup view thing?)
    var notificationsView: LinearLayout? = null
    var statusBarNotificationsView: StatusBarNotifications? = null
    var shade: NotificationShade? = null

    private class NotificationData(var sbn: StatusBarNotification,
                                   var row: NotificationRow, var key: String);

    private val notifications = mutableListOf<NotificationData>();
    private fun findNotificationByKey(key: String): NotificationData? {
        for(notification in notifications)
            if(notification.key == key) return notification;
        return null
    }
    private fun removeNotificationByKey(key: String): NotificationData? {
        for(i in 0..notifications.size) {
            val notification = notifications.getOrNull(i) ?: return null
            if(notification.key == key) {
                notifications.removeAt(i)
                return notification
            }
        }

        return null
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = (applicationContext as SystemUIApplication)
    }

    fun updateStatusBar() {
        var sbns: Array<StatusBarNotification> = emptyArray()
        for(i in notifications) {
            sbns += i.sbn
        }
        statusBarNotificationsView!!.setNotificationSet(sbns)
    }

    fun updateNotification(sbn: StatusBarNotification): Boolean {
        val notif = findNotificationByKey(sbn.key) ?: return false;
        notif.row.reloadFromNotification(sbn.notification)
        notif.sbn = sbn
        updateStatusBar()
        return true;
    }

    fun addNotification(sbn: StatusBarNotification) {
        if(updateNotification(sbn)) return;

        val row = NotificationRow.createNotification(mApplication!!, sbn.notification)
        row.setOnClickListener {
            sbn.notification.contentIntent?.send()
            shade!!.hide()

            if(sbn.isClearable)
                onNotificationRemoved(sbn)
        }
        // expanded for now
        row.setExpanded(true)
        notificationsView!!.addView(row)
        notifications.add(NotificationData(sbn, row, sbn.key))
        updateStatusBar()
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.v("HoloUI", "Notification listener connected")
        shade = mApplication!!.statusBar!!.shade!!
        notificationsView = shade!!.root!!.findViewById(R.id.notifications)
        statusBarNotificationsView = mApplication!!.statusBar!!.root!!
            .findViewById(R.id.notification_icon_area)

        for(sbn in activeNotifications) {
            addNotification(sbn);
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        addNotification(sbn)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        super.onNotificationRemoved(sbn)
        val notif = removeNotificationByKey(sbn.key)
        notificationsView!!.removeView(notif?.row)
        updateStatusBar()
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