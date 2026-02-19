package eu.hn1f.holoui

import android.content.Context
import android.media.RingtoneManager
import android.net.Uri
import android.provider.Settings

class Sounds(val context: Context) {
    fun playLowBattery() {
        val uri = Uri.parse("file://"+Settings.Global.getString(context.contentResolver,
            Settings.Global.LOW_BATTERY_SOUND))
        val ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone.play()
    }
    fun playLock() {
        val uri = Uri.parse("file://"+Settings.Global.getString(context.contentResolver,
            Settings.Global.LOCK_SOUND))
        val ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone.play()
    }
    fun playUnlock() {
        val uri = Uri.parse("file://"+Settings.Global.getString(context.contentResolver,
            Settings.Global.UNLOCK_SOUND))
        val ringtone = RingtoneManager.getRingtone(context, uri)
        ringtone.play()
    }
}