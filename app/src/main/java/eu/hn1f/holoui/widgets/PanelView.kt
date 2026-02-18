package eu.hn1f.holoui.widgets

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import eu.hn1f.holoui.R

class PanelView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    var offsetY = 0.0f
    var handle: View? = null

    override fun onFinishInflate() {
        super.onFinishInflate()
        handle = findViewById(R.id.handle)
    }

    /* override fun onTouchEvent(event: MotionEvent?): Boolean {
        /* if(event?.action == MotionEvent.ACTION_MOVE || event?.action == MotionEvent.ACTION_DOWN) {
            offsetY = event.y;
            return true
        } else if(event?.action == MotionEvent.ACTION_UP) {
            val uv = offsetY/height
            if(uv > 0.4) {
                offsetY = 1.0f
            } else {
                offsetY = 0.0f
            }
            return true
        } */
        return super.onTouchEvent(event)
    } */

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        canvas.translate(0.0f,offsetY)
        handle!!.draw(canvas)
        canvas.translate(0.0f,-offsetY)
    }
}