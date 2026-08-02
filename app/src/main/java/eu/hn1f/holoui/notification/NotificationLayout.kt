package eu.hn1f.holoui.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.LinearLayout
import eu.hn1f.holoui.SwipeHelper

class NotificationLayout: LinearLayout, SwipeHelper.Callback {
    private val mSwipeHelper: SwipeHelper

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val touchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop.toFloat()

        mSwipeHelper = SwipeHelper(SwipeHelper.X, this,
            resources.displayMetrics.density, touchSlop)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        Log.v("HoloUI", "onInterceptSlop")
        return mSwipeHelper.onInterceptTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return mSwipeHelper.onTouchEvent(event) || super.onTouchEvent(event)
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mSwipeHelper.setDensityScale(resources.displayMetrics.density)
        val pagingTouchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop
        mSwipeHelper.setPagingTouchSlop(pagingTouchSlop.toFloat())
    }

    override fun setOnLongClickListener(l: OnLongClickListener?) {
        mSwipeHelper.setLongPressListener(l)
    }

    override fun getChildAtPosition(ev: MotionEvent): View? {
        // find the view under the pointer, accounting for GONE views
        val count = childCount
        var y = 0
        var childIdx = 0
        var slidingChild: View
        while (childIdx < count) {
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

    override fun getChildContentView(v: View): View {
        return v
    }

    override fun canChildBeDismissed(v: View): Boolean {
        if(v !is NotificationRow) return false

        return v.isClearable()
    }

    override fun onChildDismissed(v: View) {
        if(v !is NotificationRow) return

        v.callOnClear()
    }

    override fun onBeginDrag(v: View) {
        // We need to prevent the surrounding ScrollView from intercepting us now;
        // the scroll position will be locked while we swipe
        requestDisallowInterceptTouchEvent(true)
    }

    override fun onDragCancelled(v: View) {}
}