package eu.hn1f.holoui.icons

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.os.BatteryManager
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import eu.hn1f.holoui.R
import eu.hn1f.holoui.Sounds
import kotlin.math.roundToInt


class Battery(context: Context?, attrs: AttributeSet? = null): ImageView(context, attrs) {
    private class BatteryTracker: BroadcastReceiver() {
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
            } else if(action.equals(Intent.ACTION_BATTERY_LOW)) {
                val level = 100f*(intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0) /
                        intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100))

                showLowBatteryDialog(context, percentage.roundToInt())
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_CHANGED)
        filter.addAction(Intent.ACTION_BATTERY_LOW)

        setImageResource(R.drawable.stat_sys_battery_100)
        imageTintList = ColorStateList.valueOf(context.getColor(R.color.white))

        context.registerReceiver(BatteryTracker(), filter)
    }
}