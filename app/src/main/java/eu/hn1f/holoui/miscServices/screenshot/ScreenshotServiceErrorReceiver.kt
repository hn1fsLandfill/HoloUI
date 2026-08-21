package eu.hn1f.holoui.miscServices.screenshot

import android.app.Notification
import android.app.NotificationManager
import android.app.admin.DevicePolicyManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.Display
import eu.hn1f.holoui.R


class ScreenshotServiceErrorReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        // Show a message that we've failed to save the image to disk
        val notificationManager = context.getSystemService(
            NotificationManager::class.java
        )

        GlobalScreenshot.notifyScreenshotError(context, notificationManager)
    }
}