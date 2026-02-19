package eu.hn1f.holoui.widgets

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import eu.hn1f.holoui.R

class PanelView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    var handle: View? = null
    var offsetY: Float = 0f
    var handleHeight = resources.getDimensionPixelSize(R.dimen.handle_height)

    fun handleTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_MOVE || event?.action == MotionEvent.ACTION_DOWN) {
            offsetY = event.rawY-handleHeight;
            handle!!.translationY = offsetY
            return true
        } else if(event?.action == MotionEvent.ACTION_UP) {
            val uv = offsetY/height
            if(uv > 0.7) {
                offsetY = height.toFloat()-handleHeight
            } else {
                offsetY = 0.0f
                visibility = GONE
            }
            handle!!.translationY = offsetY
            return true
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        handle = findViewById(R.id.handle)
        handle!!.setOnTouchListener { view, event ->
            handleTouchEvent(event)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        offsetY = height.toFloat()-handleHeight
        handle!!.translationY = offsetY
    }
}