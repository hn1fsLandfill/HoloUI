package eu.hn1f.holoui.widgets

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import eu.hn1f.holoui.R

@SuppressLint("ViewConstructor")
class Notification(context: Context, val sbn: StatusBarNotification): LinearLayout(context) {
    // TODO: ID and RemoteView stuff
    init {
        LayoutInflater.from(context)
            .inflate(R.layout.notification, this)

        val icon = findViewById<ImageView>(R.id.icon)
        val title = findViewById<TextView>(R.id.title)
        val subtitle = findViewById<TextView>(R.id.subtitle)

        icon.setImageIcon(sbn.notification.smallIcon)
        title.text = sbn.notification.extras.getString(Notification.EXTRA_TITLE, "")
        subtitle.text = sbn.notification.extras.getString(Notification.EXTRA_SUB_TEXT, "")

        tag = sbn.packageName+sbn.postTime
    }
}