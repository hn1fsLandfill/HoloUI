package eu.hn1f.holoui.notification

import android.app.INotificationManager
import android.app.Notification
import android.app.NotificationChannel
import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.IPowerManager
import android.os.RemoteException
import android.os.ServiceManager
import android.os.UserHandle
import android.service.dreams.IDreamManager
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import eu.hn1f.holoui.NotificationShade
import eu.hn1f.holoui.R
import eu.hn1f.holoui.Sounds
import eu.hn1f.holoui.SystemUIApplication

class NotificationListener: NotificationListenerService() {
    var mApplication: SystemUIApplication? = null
    // TODO: Group them (maybe have a NotificationRowGroup view thing?)
    var notificationsView: LinearLayout? = null
    var statusBarNotificationsView: StatusBarNotifications? = null
    var shade: NotificationShade? = null
    var mINotificationManager: INotificationManager? = null;

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


    private fun shouldInterrupt(sbn: StatusBarNotification): Boolean {
        val notification: Notification = sbn.getNotification()

        val dreamManager = IDreamManager.Stub.asInterface(
            ServiceManager.getServiceOrThrow(Context.DREAM_SERVICE))
        val powerManager = IPowerManager.Stub.asInterface(
            ServiceManager.getService(Context.POWER_SERVICE));

        // some predicates to make the boolean logic legible
        val isNoisy =
            (notification.defaults and Notification.DEFAULT_SOUND) !== 0
                    || (notification.defaults and Notification.DEFAULT_VIBRATE) !== 0
                    || notification.sound != null
                    || notification.vibrate != null
        val isHighPriority = sbn.notification.groupAlertBehavior >= Notification.GROUP_ALERT_ALL
        val isFullscreen = notification.fullScreenIntent != null
        val isAllowed = sbn.notification.groupAlertBehavior >= Notification.GROUP_ALERT_ALL

        val keyguard = mApplication!!.statusBar!!.lockscreen!!
        var interrupt = (isFullscreen || (isHighPriority && isNoisy))
                && isAllowed
                && powerManager.isInteractive
                && !keyguard.shown // && !keyguard.isInputRestricted()
        try {
            interrupt = interrupt && !dreamManager.isDreaming()
        } catch (e: RemoteException) {
            // Log.d(TAG, "failed to query dream manager", e)
        }
        return interrupt
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = (applicationContext as SystemUIApplication)
    }

    fun clearAll() {
        // get all the sbns because we don't wanna get a funny ConcurrentModificationException
        var sbns: Array<StatusBarNotification> = emptyArray()
        for(i in notifications) {
            sbns += i.sbn
        }

        for(sbn in sbns) {
            if(!sbn.isClearable) continue

            sbn.notification.deleteIntent?.send()
            onNotificationRemoved(sbn)
        }
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
        notif.row.reloadFromNotification(sbn)
        notif.sbn = sbn
        updateStatusBar()
        return true;
    }

    fun addNotification(sbn: StatusBarNotification) {
        if(updateNotification(sbn)) return;

        val row = NotificationRow.createNotification(mApplication!!, sbn)
        row.setOnClickListener {
            // live laugh love doing weird shit because dex2jar stuff doesn't expose everything
            val options = Bundle()
            // 3 is ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOW_ALWAYS
            options.putInt("android.pendingIntent.backgroundActivityAllowed", 3)
            sbn.notification.contentIntent?.send(mApplication!!, 0, null, null, null, null, options)
            shade!!.hide()

            if(sbn.isClearable)
                onNotificationRemoved(sbn)
        }
        row.setOnClearListener(object : NotificationRow.ClearCallback {
            override fun onClearCallback(sbn: StatusBarNotification) {
                onNotificationRemoved(sbn)
            }
        })
        // expanded for now
        row.setExpanded(true)
        notificationsView!!.addView(row)
        notifications.add(NotificationData(sbn, row, sbn.key))
        updateStatusBar()

        var channel: NotificationChannel? = null

        try {
            channel = mINotificationManager!!.getNotificationChannel(
                mApplication!!.packageName,
                sbn.user.identifier,
                sbn.packageName,
                sbn.notification.channelId
            )
        } catch (ignored: RemoteException) {} // this is probably some old app

        // for testing rn
        if(channel?.sound != null) {
            Sounds(mApplication!!).playUri(channel.sound)
        }
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.v("HoloUI", "Notification listener connected")
        mINotificationManager = INotificationManager.Stub.asInterface(
            ServiceManager.getServiceOrThrow(Context.NOTIFICATION_SERVICE))
        shade = mApplication!!.statusBar!!.shade!!
        notificationsView = shade!!.root!!.findViewById(R.id.notifications)
        statusBarNotificationsView = mApplication!!.statusBar!!.root!!
            .findViewById(R.id.notification_icon_area)

        shade!!.root!!.findViewById<ImageView>(R.id.clear_all_button).setOnClickListener {
            clearAll()
        }

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