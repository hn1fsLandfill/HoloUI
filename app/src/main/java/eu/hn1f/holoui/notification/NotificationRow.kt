package eu.hn1f.holoui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.service.notification.StatusBarNotification
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.FrameLayout
import android.widget.LinearLayout
import eu.hn1f.holoui.R
import kotlin.contracts.contract

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

    class SwipeHelper(val callback: Callback, val row: View) {
        interface Callback {
            fun childDismissed()
            fun isClearable(): Boolean
        }
        private var velocityTracker: VelocityTracker? = null
        private var offsetX = 0f
        private var densityScale = 0f
        private var dragging = false

        companion object {
            private const val MAX_DISMISS_VELOCITY = 2000f
            private const val SWIPE_ESCAPE_VELOCITY = 100f
        }

        fun setDensityScale(value: Float) {
            densityScale = value
        }

        fun onInterceptTouchEvent(e: MotionEvent): Boolean {
            return dragging
        }

        fun onTouchEvent(e: MotionEvent): Boolean {
            when(e.action) {
                MotionEvent.ACTION_DOWN -> {
                    offsetX = e.x
                    velocityTracker = VelocityTracker.obtain()
                    return true
                }
                MotionEvent.ACTION_MOVE -> {
                    velocityTracker!!.addMovement(e)
                    dragging = true
                    row.translationX = e.x-offsetX
                    return true
                }
                MotionEvent.ACTION_CANCEL -> {
                    row.animate()
                        .translationX(0f)
                        .setDuration(200)
                        .start()
                    dragging = false
                    try {
                        velocityTracker!!.recycle()
                    } catch(ignored: IllegalStateException) {}
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val maxVelocity = MAX_DISMISS_VELOCITY * densityScale
                    val swipeEscape = SWIPE_ESCAPE_VELOCITY * densityScale
                    velocityTracker!!.computeCurrentVelocity(1000, maxVelocity)
                    if(velocityTracker!!.xVelocity > swipeEscape && callback.isClearable())
                        row.animate()
                            .translationX(row.width.toFloat())
                            .setDuration(200)
                            .withEndAction {
                                callback.childDismissed()
                            }
                            .start()
                    else if(velocityTracker!!.xVelocity < 10*densityScale && row.translationX > 0
                        && row.translationX < 10*densityScale) {
                        row.callOnClick()
                        row.animate()
                            .translationX(0f)
                            .setDuration(200)
                            .start()
                    } else
                        row.animate()
                            .translationX(0f)
                            .setDuration(200)
                            .start()
                    dragging = false
                    try {
                        velocityTracker!!.recycle()
                    } catch(ignored: IllegalStateException) {}
                    return true
                }
            }
            return false
        }
    }

    val mSwipeHelper: SwipeHelper
    private constructor(context: Context) : super(context) {
        container = FrameLayout(context)
        container.isClickable = false
        // i still gotta figure out swipehelper shit
        // or i might just rip out the code and throw it here

        mSwipeHelper = SwipeHelper(object : SwipeHelper.Callback {
            override fun childDismissed() {
                clearCallback?.onClearCallback(sbn!!)
            }

            override fun isClearable(): Boolean {
                return sbn!!.isClearable
            }
        }, container)
        mSwipeHelper.setDensityScale(resources.displayMetrics.density)
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

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mSwipeHelper.setDensityScale(resources.displayMetrics.density)
        // val pagingTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop.toFloat()
        // mSwipeHelper.setPagingTouchSlop(pagingTouchSlop)
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        // TODO
        // mSwipeHelper.setLongPressListener(l)
    }

    override fun setOnClickListener(l: OnClickListener?) {
        container.setOnClickListener(l)
        container.isClickable = false
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.v("HoloUI", "onInterceptSlop")
        return mSwipeHelper.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mSwipeHelper.onTouchEvent(event) || super.onTouchEvent(event)
    }

    interface ClearCallback {
        fun onClearCallback(sbn: StatusBarNotification)
    }

    var clearCallback: ClearCallback? = null

    fun setOnClearListener(callback: ClearCallback) {
        clearCallback = callback
    }
}