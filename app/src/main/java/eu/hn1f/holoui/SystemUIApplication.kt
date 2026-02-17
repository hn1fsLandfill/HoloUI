package eu.hn1f.holoui

import android.app.AlertDialog
import android.app.Application
import android.content.DialogInterface
import android.view.WindowManager

class SystemUIApplication: Application() {
    var statusBarRunning = false

    override fun onCreate() {
        super.onCreate()
    }

    fun startServices() {
        setTheme(R.style.Theme_SystemUI)
        if(!statusBarRunning) {
            val warning = AlertDialog.Builder(this)
                .setTitle("Android System")
                .setMessage("You're currently using HoloUI, an alternative SystemUI implementation"
                    + " which is work-in-progress.\n\n"
                    + "You may encounter bugs you wouldn't see with the original implementation.")
                .setPositiveButton("OK", DialogInterface.OnClickListener {_, _ ->})
                .create()

            warning.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
            warning.show()

            StatusBar(this).init()
            statusBarRunning = true
        }
    }
}