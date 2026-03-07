package eu.hn1f.holoui.widgets

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import eu.hn1f.holoui.R
import eu.hn1f.holoui.SystemUIApplication

@SuppressLint("ViewConstructor")
class Notification(context: Context, var sbn: StatusBarNotification): LinearLayout(context) {
    private var mApplication: SystemUIApplication? = null
    private var isRemoteView = false

    // TODO: ID and RemoteView stuff
    init {
        LayoutInflater.from(context)
            .inflate(R.layout.notification, this)

        updateNotification(sbn)

        isClickable = true
        mApplication = context.applicationContext as SystemUIApplication

        setOnClickListener {
            mApplication!!.statusBar!!.shade!!.hide()
            sbn.notification.contentIntent.send()
        }
    }

    fun updateNotification(newSbn: StatusBarNotification) {
        sbn = newSbn
        val header = findViewById<LinearLayout>(R.id.header)
        val icon = findViewById<ImageView>(R.id.icon)
        val iconBg = findViewById<LinearLayout>(R.id.icon_background)

        iconBg.setBackgroundColor(sbn.notification.color)
        icon.setImageIcon(sbn.notification.smallIcon)

        if(sbn.notification.bigContentView != null) {
            Log.v("HoloUI", "detected big remoteView")
            header.removeAllViews()
            val mainView = sbn.notification.bigContentView.apply(context, header)
            mainView.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            header.addView(mainView)
            isClickable = false
            setOnClickListener {}
        } else if(sbn.notification.contentView != null) {
            Log.v("HoloUI", "detected remoteView")
            header.removeAllViews()
            val mainView = sbn.notification.contentView.apply(context, header)
            mainView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            header.addView(mainView)
            isClickable = false
            setOnClickListener {}
        } else if(!isRemoteView) {
            val icon = findViewById<ImageView>(R.id.icon)
            val title = findViewById<TextView>(R.id.title)
            val subtitle = findViewById<TextView>(R.id.subtitle)

            title.text = sbn.notification.extras.getString(Notification.EXTRA_TITLE, "No Title")
            subtitle.text = sbn.notification.extras.getString(Notification.EXTRA_SUB_TEXT, "")

            val progress = sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS, -1)
            val progressMax = sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS_MAX, -1)
            val progressIndeterminate = sbn.notification.extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, false)
            val progressView = findViewById<ProgressBar>(R.id.progress)

            if(progress != -1) {
                progressView.progress  = progress
                progressView.max = progressMax
                progressView.isIndeterminate = progressIndeterminate
                progressView.visibility = View.VISIBLE
            }
        }

        tag = sbn.packageName+sbn.id
    }
}