package eu.hn1f.holoui.widgets

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.service.notification.StatusBarNotification
import android.util.Log
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import eu.hn1f.holoui.R
import eu.hn1f.holoui.SystemUIApplication

@SuppressLint("ViewConstructor")
class Notification(context: Context, var sbn: StatusBarNotification): LinearLayout(context) {
    private var mApplication: SystemUIApplication? = null

    private fun bigText(id: String, fallbackId: String? = null): TextView? {
        val fallback = sbn.notification.extras.getString(fallbackId, sbn.packageName)
        val text = sbn.notification.extras.getString(id, fallback)
        if(text == null) return null

        val view = findViewById<LinearLayout>(R.id.header)
        val textView = TextView(context)

        textView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        textView.text = text
        textView.setTextSize(18f)
        textView.setTextColor(Color.WHITE)

        view.addView(textView)
        return textView
    }

    private fun smallText(id: String, fallbackId: String? = null): TextView? {
        val fallback = sbn.notification.extras.getString(fallbackId)
        val text = sbn.notification.extras.getString(id, fallback)
        if(text == null) return null

        val view = findViewById<LinearLayout>(R.id.header)
        val textView = TextView(context)

        textView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        textView.text = text

        view.addView(textView)
        return textView
    }

    private fun progressBar() {
        val progress = sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS)
        val progressMax = sbn.notification.extras.getInt(Notification.EXTRA_PROGRESS_MAX, -1)
        val progressIndeterminate = sbn.notification.extras.getBoolean(Notification.EXTRA_PROGRESS_INDETERMINATE, false)

        if(progress != null && progress > 0) {
            val progressView = ProgressBar(ContextThemeWrapper(context, android.R.style.Widget_Holo_ProgressBar_Horizontal))
            progressView.progress  = progress
            progressView.max = progressMax
            progressView.isIndeterminate = progressIndeterminate
            progressView.setPadding(4, 0, 4, 0)
            progressView.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
                TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    16f,
                    context.resources.displayMetrics
                ).toInt()
            )

            val view = findViewById<LinearLayout>(R.id.header)
            view.addView(progressView)
        }
    }

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

        val veto = findViewById<Button>(R.id.veto)

        val iconBackground = if(sbn.notification.color != 0)
            ColorDrawable(sbn.notification.color)
        else
            ColorDrawable(0x1d3741)

        val bigIcon = sbn.notification.getLargeIcon()

        val layers: Array<Drawable?> = if(bigIcon == null)
            arrayOf(iconBackground, sbn.notification.smallIcon.loadDrawable(context))
        else
            arrayOf(iconBackground, bigIcon!!.loadDrawable(context))

        veto.background = LayerDrawable(layers)

        bigText(Notification.EXTRA_TITLE_BIG, Notification.EXTRA_TITLE)
        smallText(Notification.EXTRA_BIG_TEXT, Notification.EXTRA_TEXT)
        smallText(Notification.EXTRA_SUB_TEXT)
        progressBar()
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