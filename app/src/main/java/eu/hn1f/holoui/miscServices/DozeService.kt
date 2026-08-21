package eu.hn1f.holoui.miscServices

import android.service.dreams.DreamService
import android.util.Log

class DozeService: DreamService() {
    override fun onCreate() {
        Log.v("HoloUI", "DozeService")
        super.onCreate()
    }
}