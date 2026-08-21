package eu.hn1f.holoui.miscServices.screenshot

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import android.util.Log
import com.android.internal.util.ScreenshotRequest

class TakeScreenshotService: Service() {
    private val handler = Handler(Looper.getMainLooper(), this::handleMsg)

    override fun onBind(p0: Intent): IBinder {
        Log.v("HoloUI", "screenshot bind")
        return Messenger(handler).binder
    }

    fun handleMsg(msg: Message): Boolean {
        val request = msg.obj as ScreenshotRequest
        val replyTo = msg.replyTo
        Log.v("HoloUI", "request $request ${request.type}")
        Log.v("HoloUI", "${request.bitmap}")

        val screenshot = GlobalScreenshot(this)
        screenshot.takeScreenshot(replyTo, request, true, true)

        return true
    }
}