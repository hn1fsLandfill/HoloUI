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

    fun build() {
        removeAllViews()
        LayoutInflater.from(context)
            .inflate(R.layout.notification, this)

        val core = findViewById<LinearLayout>(R.id.core)

        if(sbn.notification.bigContentView != null) {
            Log.v("HoloUI", "detected big remoteView")
            core.removeAllViews()
            val mainView = sbn.notification.bigContentView.apply(context, core)
            mainView.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            core.addView(mainView)
            isClickable = false
            setOnClickListener {}
            return
        } else if(sbn.notification.contentView != null) {
            Log.v("HoloUI", "detected remoteView")
            core.removeAllViews()
            val mainView = sbn.notification.contentView.apply(context, this)
            mainView.layoutParams =
                LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            core.addView(mainView)
            isClickable = false
            setOnClickListener {}
            return
        }

        isClickable = true
        setOnClickListener {
            mApplication!!.statusBar!!.shade!!.hide()
            sbn.notification.contentIntent.send()
        }

        val iconBg = findViewById<LinearLayout>(R.id.icon_background)
        val icon = findViewById<ImageView>(R.id.icon)
        val title = findViewById<TextView>(R.id.title)
        val subtitle = findViewById<TextView>(R.id.subtitle)

        if(sbn.notification.color != 0) iconBg.setBackgroundColor(sbn.notification.color)
        else iconBg.setBackgroundColor(0x1d3741)
        icon.setImageIcon(sbn.notification.smallIcon)

        val fallback = sbn.notification.extras.getString(Notification.EXTRA_TEXT)
        title.text = sbn.notification.extras.getString(Notification.EXTRA_TITLE, fallback)
        subtitle.text = sbn.notification.extras.getString(Notification.EXTRA_SUB_TEXT, "")

        val progress = sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS, -1)
        val progressMax = sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS_MAX, -1)
        val progressIndeterminate = sbn.notification.extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, false)
        val progressView = findViewById<ProgressBar>(R.id.progress)

        if(progress > 0) {
            progressView.progress  = progress
            progressView.max = progressMax
            progressView.isIndeterminate = progressIndeterminate
            progressView.visibility = View.VISIBLE
        }
    }

    // TODO: ID and RemoteView stuff
    init {
        mApplication = context.applicationContext as SystemUIApplication
        updateNotification(sbn)
    }

    fun updateNotification(newSbn: StatusBarNotification) {
        sbn = newSbn
        build()
        tag = sbn.packageName+sbn.id
    }
}