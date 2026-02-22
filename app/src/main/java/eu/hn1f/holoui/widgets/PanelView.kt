package eu.hn1f.holoui.widgets

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import eu.hn1f.holoui.R

// TODO: Flinging
class PanelView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    var handle: View? = null
    var offsetY: Float = 0f
    var handleHeight = resources.getDimensionPixelSize(R.dimen.handle_height)

    var touchOffsetY = 0f

    fun handleTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_DOWN) {
            handle!!.isPressed = true
            touchOffsetY = handle!!.translationY-event.rawY
            invalidate()
            return true
        } else if(event?.action == MotionEvent.ACTION_MOVE) {
            handle!!.isPressed = true
            offsetY = event.rawY+touchOffsetY;
            if(offsetY < height.toFloat()-handleHeight) handle!!.translationY = offsetY
            invalidate()
            return true
        } else if(event?.action == MotionEvent.ACTION_UP) {
            handle!!.isPressed = false
            val uv = offsetY/height
            if(uv > 0.7) {
                onOpen()
            } else {
                onClose()
            }
            if(offsetY < height.toFloat()-handleHeight) handle!!.translationY = offsetY
            return true
        }
        return false
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onFinishInflate() {
        super.onFinishInflate()
        handle = View(context)
        handle!!.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, handleHeight)
        handle!!.setOnTouchListener { view, event ->
            handleTouchEvent(event)
        }
        addView(handle)
        // dispatch it
        setOnTouchListener { view, event ->
            handle!!.dispatchTouchEvent(event)
        }
        handle!!.background = resources.getDrawable(R.drawable.status_bar_close)
        setWillNotDraw(false)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.clipRect(0f, 0f, width.toFloat(), offsetY+handleHeight)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    fun onOpen() {
        val animation = ValueAnimator.ofFloat(offsetY, height.toFloat()-handleHeight)
        animation.addUpdateListener { animator ->
            offsetY = animator.animatedValue as Float
            handle!!.translationY = offsetY
            invalidate()
        }
        animation.start()
    }

    fun onClose() {
        val animation = ValueAnimator.ofFloat(offsetY, 0f)
        animation.addUpdateListener { animator ->
            offsetY = animator.animatedValue as Float
            handle!!.translationY = offsetY
            invalidate()
        }
        animation.addListener(object: Animator.AnimatorListener {
            override fun onAnimationEnd(animation: Animator) {
                visibility = GONE
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
            override fun onAnimationStart(animation: Animator) {}
        })
        animation.start()
    }
}