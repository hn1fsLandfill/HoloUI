package eu.hn1f.holoui.icons

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Paint
import android.os.BatteryManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import eu.hn1f.holoui.R
import eu.hn1f.holoui.Sounds
import kotlin.math.roundToInt


class Battery(context: Context?, attrs: AttributeSet? = null): View(context, attrs) {
    private class BatteryTracker(val main: Battery): BroadcastReceiver() {
        final val UNKNOWN_LEVEL = -1
        fun showLowBatteryDialog(context: Context, level: Int) {

            val dialog_view = LayoutInflater.from(context)
                .inflate(R.layout.low_battery, null)
            dialog_view.findViewById<TextView>(R.id.level_percent).text =
                context.resources.getString(R.string.battery_low_percent_format, level)

            val dialog = AlertDialog.Builder(context)
                .setNegativeButton("Battery usage", { _, _ -> })
                .setPositiveButton(android.R.string.ok, { _, _ -> })
                .setView(dialog_view)
                .setTitle(R.string.battery_low_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()

            Sounds(context).playLowBattery()
            dialog.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
            dialog.show()
        }

        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action

            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percentage = level * 100 / scale.toFloat()

            if(action.equals(Intent.ACTION_BATTERY_CHANGED)) {
                // TODO: status bar battery icon shenanigans
                Log.v("HoloUI", "new battey level: $level")
                main.percentage = percentage.roundToInt()
                main.invalidate()
            } else if(action.equals(Intent.ACTION_BATTERY_LOW)) {
                main.low_battery = true
                showLowBatteryDialog(context, main.percentage)
            } else if(action.equals(Intent.ACTION_BATTERY_OKAY)) {
                main.low_battery = false
            }
        }
    }

    var percentage = 70
    var low_battery = false
    val paint = Paint()
    val batteryIcon = resources.getDrawable(R.drawable.stat_sys_drawable)

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        /* val percentageHeight = (percentage.toFloat()/100)*height
        paint.setColor(Color.GRAY)
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        if(low_battery) paint.setColor(Color.RED)
        else paint.setColor(Color.WHITE)
        canvas.drawRect(0f, height-percentageHeight, width.toFloat(), height.toFloat(), paint) */

        val battery_width = batteryIcon.minimumWidth

        batteryIcon.setBounds(0,0,battery_width,height)
        batteryIcon.setLevel(percentage)
        batteryIcon.draw(canvas)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_LOW)
        filter.addAction(Intent.ACTION_BATTERY_OKAY)

        context.registerReceiver(BatteryTracker(this), filter)

        invalidate()
    }
}