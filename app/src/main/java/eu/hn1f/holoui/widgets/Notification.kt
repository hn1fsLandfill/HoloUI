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
        return null;
    }

    private fun smallText(id: String, fallbackId: String? = null): TextView? {
        return null;
    }

    private fun progressBar() {

    }

    fun build() {

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