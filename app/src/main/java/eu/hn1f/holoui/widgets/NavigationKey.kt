package eu.hn1f.holoui.widgets

import android.content.Context
import android.hardware.input.InputManagerGlobal
import android.os.InputEventInjectionSync
import android.os.SystemClock
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.ImageView

class NavigationKey(context: Context?, val key: Int): ImageView(context) {
    var downTime = 0L

    fun sendEvent(action: Int, key: Int, eventTime: Long) {
        InputManagerGlobal.getInstance().injectInputEvent(KeyEvent(
            downTime, eventTime,
            action,
            key,
            0
        ), InputEventInjectionSync.NONE)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if(event?.action == MotionEvent.ACTION_UP) {
            sendEvent(KeyEvent.ACTION_UP, key, SystemClock.uptimeMillis())
            return true
        } else if(event?.action == MotionEvent.ACTION_DOWN) {
            downTime = SystemClock.uptimeMillis()
            sendEvent(KeyEvent.ACTION_DOWN, key, downTime)
            return true
        }
        return super.onTouchEvent(event)
    }
}

