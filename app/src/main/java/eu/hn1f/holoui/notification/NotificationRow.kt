package eu.hn1f.holoui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.service.notification.StatusBarNotification
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.LinearLayout
import eu.hn1f.holoui.R
import eu.hn1f.holoui.SwipeHelper

class NotificationRow: LinearLayout, SwipeHelper.Callback {
    private val mSwipeHelper: SwipeHelper
    private constructor(context: Context) : super(context) {
        val densityScale = resources.displayMetrics.density
        val pagingTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop.toFloat()
        mSwipeHelper = SwipeHelper(SwipeHelper.X, this, densityScale,
            pagingTouchSlop)
    }

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
    private var sbn: StatusBarNotification? = null

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

    fun reloadFromNotification(newNotif: StatusBarNotification) {
        sbn = newNotif
        val notification = sbn!!.notification
        val isRemoteView = notification.contentView != null;

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
                addView(bigContentView)
        }

        requestDisallowInterceptTouchEvent(false)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        val densityScale = resources.displayMetrics.density
        val pagingTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop.toFloat()
        mSwipeHelper.setDensityScale(densityScale)
        mSwipeHelper.setPagingTouchSlop(pagingTouchSlop)
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        mSwipeHelper.setLongPressListener(l)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return mSwipeHelper.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return mSwipeHelper.onTouchEvent(event) || super.onTouchEvent(event)
    }

    interface ClearCallback {
        fun onClearCallback(sbn: StatusBarNotification)
    }

    var clearCallback: ClearCallback? = null

    fun setOnClearListener(callback: ClearCallback) {
        clearCallback = callback
    }

    override fun getChildAtPosition(ev: MotionEvent): View? {
        // find the view under the pointer, accounting for GONE views
        var y = 0
        var childIdx = 0
        var slidingChild: View
        while (childIdx < childCount) {
            slidingChild = getChildAt(childIdx)
            if (slidingChild.visibility == GONE) {
                childIdx++
                continue
            }
            y += slidingChild.measuredHeight
            if (ev.y < y) return slidingChild
            childIdx++
        }
        return null
    }

    override fun getChildContentView(v: View?): View? {
        return v
    }

    override fun canChildBeDismissed(v: View?): Boolean {
        return true; // sbn!!.isClearable
    }

    override fun onBeginDrag(v: View?) {
        requestDisallowInterceptTouchEvent(true)
    }

    override fun onChildDismissed(v: View?) {
        clearCallback?.onClearCallback(sbn!!)
    }

    override fun onDragCancelled(v: View?) {}
}