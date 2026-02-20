package eu.hn1f.holoui.icons

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.util.AttributeSet
import android.util.Log
import android.widget.ImageView
import eu.hn1f.holoui.R

class WiFi(context: Context, attrs: AttributeSet?): ImageView(context, attrs) {
    val maxBars = 4;
    var bars = maxBars;
    val listener = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if(intent?.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED)
                when(state) {
                    WifiManager.WIFI_STATE_ENABLED -> {
                        update(-1)
                        visibility = VISIBLE
                    }
                    else -> visibility = GONE
                }
            } else if(intent?.action == WifiManager.RSSI_CHANGED_ACTION) {
                val rssi = intent.getIntExtra(WifiManager.EXTRA_NEW_RSSI, -500);
                @Suppress("DEPRECATION") val new = WifiManager.calculateSignalLevel(rssi, 5)
                Log.v("HoloUI",  "debug $rssi $new")
                update(new)
            }
        }
    }

    fun getBarDrawable(): Int {
        when(bars) {
            0 -> return R.drawable.ic_qs_wifi_not_connected
            1 -> return R.drawable.ic_qs_wifi_full_1
            2 -> return R.drawable.ic_qs_wifi_full_2
            3 -> return R.drawable.ic_qs_wifi_full_3
            4 -> return R.drawable.ic_qs_wifi_full_4
            else -> return R.drawable.ic_qs_wifi_no_network
        }
    }

    fun update(new: Int) {
        bars = new
        setImageResource(getBarDrawable())
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        val filter = IntentFilter()
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)

        context.registerReceiver(listener, filter)

        update(-1)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()

        context.unregisterReceiver(listener)
    }
}