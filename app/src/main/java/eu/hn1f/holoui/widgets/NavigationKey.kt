package eu.hn1f.holoui.widgets

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.RectF
import android.hardware.input.InputManagerGlobal
import android.os.InputEventInjectionSync
import android.os.SystemClock
import android.util.AttributeSet
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.widget.ImageView
import eu.hn1f.holoui.R


class NavigationKey(context: Context?, attrs: AttributeSet?): ImageView(context, attrs) {
    val GLOW_MAX_SCALE_FACTOR: Float = 1.8f
    val DEFAULT_QUIESCENT_ALPHA: Float = 0.70f

    var downTime = 0L
    val highlight = resources.getDrawable(R.drawable.ic_sysbar_highlight)
    val mGlowWidth = highlight.intrinsicWidth
    var mGlowHeight = highlight.intrinsicHeight
    var mGlowAlpha = 0f
    var mGlowScale = 1f
    var mDrawingAlpha = 1f
    var mQuiescentAlpha = DEFAULT_QUIESCENT_ALPHA;
    var mPressedAnim: AnimatorSet? = null
    var mAnimateToQuiescent = ObjectAnimator()
    var mRect = RectF()
    val mTouchSlop = ViewConfiguration.get(context!!).scaledTouchSlop

    val key = context!!.obtainStyledAttributes(attrs, R.styleable.NavigationKey)

    fun sendEvent(action: Int, key: Int, eventTime: Long) {
        InputManagerGlobal.getInstance().injectInputEvent(KeyEvent(
            downTime, eventTime,
            action,
            key,
            0,
            0,
            KeyCharacterMap.VIRTUAL_KEYBOARD,
            0,
            KeyEvent.FLAG_FROM_SYSTEM or KeyEvent.FLAG_VIRTUAL_HARD_KEY,
            InputDevice.SOURCE_KEYBOARD
        ), InputEventInjectionSync.NONE)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(highlight.intrinsicWidth, heightMeasureSpec)
    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_UP) {
            isPressed = false
            val key = key.getInt(R.styleable.NavigationKey_key, 3)
            sendEvent(KeyEvent.ACTION_UP, key, SystemClock.uptimeMillis())
            return true
        } else if(event?.action == MotionEvent.ACTION_DOWN) {
            isPressed = true
            background = highlight
            downTime = SystemClock.uptimeMillis()
            sendEvent(KeyEvent.ACTION_DOWN, key.getInt(R.styleable.NavigationKey_key, 3), downTime)
            return true
        } else if(event?.action == MotionEvent.ACTION_CANCEL) {
            isPressed = false
            val key = key.getInt(R.styleable.NavigationKey_key, 3)
            background = null
            sendEvent(KeyEvent.ACTION_UP, key, SystemClock.uptimeMillis())
            return true
        } else if(event?.action == MotionEvent.ACTION_MOVE) {
            val x = event?.getX()!!.toInt()
            val y = event?.getY()!!.toInt()
            setPressed(x >= -mTouchSlop && x < getWidth() + mTouchSlop && y >= -mTouchSlop && y < getHeight() + mTouchSlop)
            return true
        }
        return super.onTouchEvent(event)
    }

    fun getDrawingAlpha(): Float {
        return mDrawingAlpha
    }

    fun setDrawingAlpha(x: Float) {
        // Calling setAlpha(int), which is an ImageView-specific
        // method that's different from setAlpha(float). This sets
        // the alpha on this ImageView's drawable directly
        setAlpha((x * 255).toInt())
        mDrawingAlpha = x
    }

    private fun animateToQuiescent(): ObjectAnimator {
        return ObjectAnimator.ofFloat(this, "drawingAlpha", mQuiescentAlpha)
    }

    fun getGlowAlpha(): Float {
        if (highlight == null) return 0f
        return mGlowAlpha
    }

    fun setGlowAlpha(x: Float) {
        if (highlight == null) return
        mGlowAlpha = x
        invalidate()
    }

    fun getGlowScale(): Float {
        if (highlight == null) return 0f
        return mGlowScale
    }

    fun setGlowScale(x: Float) {
        if (highlight == null) return

        mGlowScale = x
        val w = getWidth().toFloat()
        val h = getHeight().toFloat()
        if (GLOW_MAX_SCALE_FACTOR <= 1.0f) {
            // this only works if we know the glow will never leave our bounds
            invalidate()
        } else {
            // also invalidate our immediate parent to help avoid situations where nearby glows
            // interfere
            (parent as View).invalidate()
        }
    }

    override fun setPressed(pressed: Boolean) {
        if (highlight != null) {
            if (pressed != isPressed()) {
                if (mPressedAnim != null && mPressedAnim!!.isRunning()) {
                    mPressedAnim!!.cancel()
                }
                mPressedAnim = AnimatorSet()
                if (pressed) {
                    if (mGlowScale < GLOW_MAX_SCALE_FACTOR) mGlowScale = GLOW_MAX_SCALE_FACTOR
                    if (mGlowAlpha < mQuiescentAlpha) mGlowAlpha = mQuiescentAlpha
                    setDrawingAlpha(1f)
                    mPressedAnim!!.playTogether(
                        ObjectAnimator.ofFloat(this, "glowAlpha", 1f),
                        ObjectAnimator.ofFloat(this, "glowScale", GLOW_MAX_SCALE_FACTOR)
                    )
                    mPressedAnim!!.setDuration(50)
                } else {
                    mAnimateToQuiescent.cancel()
                    mAnimateToQuiescent = animateToQuiescent()
                    mPressedAnim!!.playTogether(
                        ObjectAnimator.ofFloat(this, "glowAlpha", 0f),
                        ObjectAnimator.ofFloat(this, "glowScale", 1f),
                        mAnimateToQuiescent
                    )
                    mPressedAnim!!.setDuration(500)
                }
                mPressedAnim!!.start()
            }
        }
        super.setPressed(pressed)
    }

    override fun onDraw(canvas: Canvas) {
        if (highlight != null) {
            canvas.save()
            val w = getWidth()
            val h = getHeight()
            val aspect: Float = mGlowWidth.toFloat() / mGlowHeight
            val drawW = (h * aspect).toInt()
            val drawH = h
            val margin = (drawW - w) / 2
            canvas.scale(mGlowScale, mGlowScale, w * 0.5f, h * 0.5f)
            highlight.setBounds(-margin, 0, drawW - margin, drawH)
            highlight.setAlpha((mDrawingAlpha * mGlowAlpha * 255).toInt())
            highlight.draw(canvas)
            canvas.restore()
            mRect.right = w.toFloat()
            mRect.bottom = h.toFloat()
        }
        super.onDraw(canvas)
    }

    override fun onAttachedToWindow() {
        setPressed(false)
        super.onAttachedToWindow()
    }
}

