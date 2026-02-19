package eu.hn1f.holoui.widgets

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.AttributeSet
import android.widget.TextView
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.Date

/**
 * Digital clock for the status bar.
 */
class Clock @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TextView(context, attrs, defStyle) {
    val mAttached = false
    val mIntentReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateTime()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateTime()
        val filter = IntentFilter()

        filter.addAction(Intent.ACTION_TIME_TICK)
        filter.addAction(Intent.ACTION_TIME_CHANGED)
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED)
        filter.addAction(Intent.ACTION_CONFIGURATION_CHANGED)

        context.registerReceiver(mIntentReceiver, filter, null, getHandler())
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        context.unregisterReceiver(mIntentReceiver)
    }

    fun updateTime() {
        val timeFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT);
        text = timeFormat.format(Date.from(Instant.now()))
    }
}

