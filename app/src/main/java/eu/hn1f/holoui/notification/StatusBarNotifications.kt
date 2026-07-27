package eu.hn1f.holoui.notification

import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.AttributeSet
import android.widget.LinearLayout
import java.lang.Integer.min

class StatusBarNotifications: LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    val MAX_NOTIFICATIONS = 4;

    init {
        orientation = HORIZONTAL
    }

    fun setNotificationSet(n: Array<StatusBarNotification>) {
        removeAllViews()
        for(i in 0..min(MAX_NOTIFICATIONS, n.size)) {
            val notif = n.getOrNull(i) ?: return
            val imgView = AnimatedImageView(context)
            imgView.maxWidth = height
            imgView.maxHeight = height
            imgView.minimumWidth = height
            imgView.minimumHeight = height
            val drawable = notif.notification.smallIcon.loadDrawable(context)
            drawable?.setBounds(0,0,height,height)
            imgView.setImageDrawable(drawable)
            addView(imgView)
        }
    }
}