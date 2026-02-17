package eu.hn1f.holoui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class KeyguardService: Service() {
    override fun onCreate() {
        super.onCreate()
        // TODO?
        Log.v("HoloUI", "STUB: KeyguardService created")
        // throw RuntimeException("mrow meow mrrp")
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }
}