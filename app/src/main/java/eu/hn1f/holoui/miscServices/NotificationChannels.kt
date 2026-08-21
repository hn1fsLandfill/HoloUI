package eu.hn1f.holoui.miscServices

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context


class NotificationChannels {
    companion object {
        const val ALERTS = "ALR"

        fun create(context: Context) {
            val nm = context.getSystemService(NotificationManager::class.java)

            val alerts = NotificationChannel(
                ALERTS,
                "Alerts", // TODO: Split off to strings.xml?
                NotificationManager.IMPORTANCE_HIGH
            )

            nm.createNotificationChannel(alerts)
        }
    }
}