package eu.hn1f.holoui.widgets

import android.content.Context
import android.hardware.input.InputManagerGlobal
import android.os.InputEventInjectionSync
import android.os.SystemClock
import android.util.AttributeSet
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.ImageView
import eu.hn1f.holoui.R
import eu.hn1f.holoui.SystemUIApplication

class NavigationKey(context: Context?, attrs: AttributeSet?): ImageView(context, attrs) {
    var downTime = 0L
    val highlight = resources.getDrawable(R.drawable.ic_sysbar_highlight)
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
            background = null
            val key = key.getInt(R.styleable.NavigationKey_key, 3)
            if(key == KeyEvent.KEYCODE_HOME) (context.applicationContext as SystemUIApplication)
                .onHome()
            else sendEvent(KeyEvent.ACTION_UP, key, SystemClock.uptimeMillis())
            return true
        } else if(event?.action == MotionEvent.ACTION_DOWN) {
            background = highlight
            downTime = SystemClock.uptimeMillis()
            sendEvent(KeyEvent.ACTION_DOWN, key.getInt(R.styleable.NavigationKey_key, 3), downTime)
            return true
        }
        return super.onTouchEvent(event)
    }
}

