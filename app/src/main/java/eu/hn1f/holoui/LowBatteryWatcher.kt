package eu.hn1f.holoui

import android.app.AlertDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.TextView


class LowBatteryWatcher(val context: Context) {
    val watcher = object: BroadcastReceiver() {
        fun showLowBatteryDialog(context: Context, level: Int) {
            val dialog_view = LayoutInflater.from(context)
                .inflate(R.layout.low_battery, null)
            dialog_view.findViewById<TextView>(R.id.level_percent).text =
                context.resources.getString(R.string.battery_low_percent_format, level)

            val dialog = AlertDialog.Builder(context)
                .setNegativeButton("Battery usage") { _, _ -> }
                .setPositiveButton(android.R.string.ok) { _, _ -> }
                .setView(dialog_view)
                .setTitle(R.string.battery_low_title)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .create()

            Sounds(context).playLowBattery()
            dialog.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
            dialog.show()
        }

        override fun onReceive(context: Context, intent: Intent) {
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            val percentage = level * 100 / scale.toFloat()

            showLowBatteryDialog(context, percentage.toInt())
        }
    }

    fun register() {
        val filter = IntentFilter()
        filter.addAction(Intent.ACTION_BATTERY_LOW)

        context.registerReceiver(watcher, filter)
    }

    fun unregister() {
        context.unregisterReceiver(watcher)
    }
}