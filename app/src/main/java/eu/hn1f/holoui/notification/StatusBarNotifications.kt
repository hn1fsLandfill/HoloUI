package eu.hn1f.holoui.notification

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.service.notification.StatusBarNotification
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import eu.hn1f.holoui.R
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
            // tint to white for now
            imgView.scaleType = ImageView.ScaleType.FIT_CENTER
            imgView.imageTintList = ColorStateList(
                arrayOf(intArrayOf(android.R.attr.state_enabled)), intArrayOf(Color.WHITE)
            )

            val iconSize = height // outer bounds
            val drawingSize = context.resources.getDimensionPixelSize(R.dimen.status_bar_icon_drawing_size)
            imgView.layoutParams = LayoutParams(height, height)
            imgView.scaleX = drawingSize.toFloat()/iconSize.toFloat()
            imgView.scaleY = drawingSize.toFloat()/iconSize.toFloat()

            val drawable = notif.notification.smallIcon.loadDrawable(context)
            imgView.setImageDrawable(drawable)
            addView(imgView)
        }
    }
}