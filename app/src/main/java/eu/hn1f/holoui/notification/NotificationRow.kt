package eu.hn1f.holoui.notification

import android.animation.LayoutTransition
import android.app.Notification
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import eu.hn1f.holoui.R

class NotificationRow: LinearLayout {
    private constructor(context: Context) : super(context)

    companion object {
        fun createNotification(context: Context, notification: Notification): NotificationRow {
            val notificationRow = NotificationRow(context);

            notificationRow.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT)
            notificationRow.reloadFromNotification(notification)

            return notificationRow
        }
    }

    init {
        orientation = VERTICAL
    }

    private var isExpanded = false
    private var contentView: View? = null
    private var bigContentView: View? = null
    private var notification: Notification? = null

    private var topGlow: View? = null;
    private var bottomGlow: View? = null;
    private val notificationDividerHeight
        = resources.getDimensionPixelSize(R.dimen.notification_divider_height)

    fun marginLayout(): LayoutParams {
        return LayoutParams(LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT).apply {
                bottomMargin = notificationDividerHeight
                topMargin = notificationDividerHeight
        }
    }
    fun addTopGlow() {
        topGlow = View(context)
        topGlow!!.alpha = 0f
        topGlow!!.visibility = INVISIBLE
        topGlow!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
            notificationDividerHeight)
            .apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }
        topGlow!!.setBackgroundResource(R.drawable.top_divider_glow)
        addView(topGlow)
    }

    fun addBottomGlow() {
        bottomGlow = View(context)
        // TODO: Set these to something else instead
        bottomGlow!!.alpha = 0f
        bottomGlow!!.visibility = INVISIBLE
        bottomGlow!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,
            notificationDividerHeight)
            .apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            }
        bottomGlow!!.setBackgroundResource(R.drawable.top_divider_glow)
        addView(bottomGlow)
    }

    fun reloadFromNotification(newNotif: Notification) {
        notification = newNotif
        val isRemoteView = notification!!.contentView != null;

        val container = FrameLayout(context)
        container.setBackgroundResource(R.drawable.notification_bg)
        container.layoutParams = marginLayout()

        removeAllViews()
        addTopGlow()
        addView(container)
        addBottomGlow()

        if(isRemoteView) {
            contentView = notification!!.contentView.apply(context, this)
            addView(contentView)
            if(notification!!.bigContentView != null) {
                bigContentView = notification!!.bigContentView.apply(context, this);
                container.addView(bigContentView)
            }
        } else {
            // Call up John
            val john = JohnNotificationBuilder(context, notification)
            contentView = john.makeContentView()
            bigContentView = john.makeBigContentView()

            container.addView(contentView)
            if(bigContentView != null)
                addView(bigContentView)
        }
    }

    fun setExpanded(value: Boolean) {
        isExpanded = value
        requestLayout()
    }
    fun getExpanded(): Boolean {
        return isExpanded
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if(isExpanded && bigContentView != null) {
            contentView!!.visibility = GONE
            bigContentView!!.visibility = VISIBLE
        } else {
            contentView!!.visibility = VISIBLE
            bigContentView?.visibility = GONE
        }
        super.onLayout(changed, left, top, right, bottom)
    }
}