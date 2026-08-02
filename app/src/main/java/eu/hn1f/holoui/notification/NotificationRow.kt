package eu.hn1f.holoui.notification

import android.content.Context
import android.service.notification.StatusBarNotification
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import eu.hn1f.holoui.R

class NotificationRow: LinearLayout {
    companion object {
        fun createNotification(context: Context, notification: StatusBarNotification): NotificationRow {
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
    private val container: FrameLayout
    private var sbn: StatusBarNotification? = null

    private var topGlow: View? = null;
    private var bottomGlow: View? = null;
    private val notificationDividerHeight
            = resources.getDimensionPixelSize(R.dimen.notification_divider_height)

    private constructor(context: Context) : super(context) {
        container = FrameLayout(context)
        container.isClickable = false
    }

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

    fun reloadFromNotification(newNotif: StatusBarNotification) {
        sbn = newNotif
        val notification = sbn!!.notification
        val isRemoteView = notification.contentView != null;

        container.removeAllViews()
        container.setBackgroundResource(R.drawable.notification_bg)
        container.layoutParams = marginLayout()

        removeAllViews()
        addTopGlow()
        addView(container)
        addBottomGlow()

        if(isRemoteView) {
            contentView = notification!!.contentView.apply(context, this)
            container.addView(contentView)
            if(notification.bigContentView != null) {
                bigContentView = notification.bigContentView.apply(context, this);
                container.addView(bigContentView)
            }
        } else {
            // Call up John
            val john = JohnNotificationBuilder(context, notification)
            contentView = john.makeContentView()
            bigContentView = john.makeBigContentView()

            container.addView(contentView)
            if(bigContentView != null)
                container.addView(bigContentView)
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

    fun isClearable(): Boolean {
        return sbn!!.isClearable
    }

    interface ClearCallback {
        fun onClearCallback(sbn: StatusBarNotification)
    }

    private var clearCallback: ClearCallback? = null

    fun setOnClearListener(callback: ClearCallback) {
        clearCallback = callback
    }

    fun callOnClear() {
        clearCallback?.onClearCallback(sbn!!)
    }
}