package eu.hn1f.holoui

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class SystemUIService: Service() {
    override fun onCreate() {
        super.onCreate()
        Log.v("HoloUI", "Hello from SystemUIService!")
        (application as SystemUIApplication).startServices()
        // throw RuntimeException("mrow meow mrrp")
    }
    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }
}