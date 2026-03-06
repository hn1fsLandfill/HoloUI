package eu.hn1f.holoui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.os.Process
import android.preference.PreferenceActivity
import android.preference.SwitchPreference
import android.provider.Settings
import eu.hn1f.holoui.R

@SuppressLint("ExportedPreferenceActivity")
@Suppress("DEPRECATION")
class Settings: PreferenceActivity() {
    fun showRestartRequired() {
        AlertDialog.Builder(this)
            .setTitle("Note")
            .setMessage("A SystemUI restart is required to apply the changes.")
            .setNegativeButton(android.R.string.cancel, {_, _ ->})
            .setPositiveButton(android.R.string.ok, {_, _ ->
                finish()
                Process.killProcess(Process.myPid())
            })
            .create()
            .show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
        val use_nav = (findPreference("use_nav") as SwitchPreference)

        use_nav.isChecked = Settings.Global.getInt(contentResolver, "holoui_navbar", 1) == 1

        use_nav.setOnPreferenceChangeListener { _, value ->
            Settings.Global.putInt(contentResolver, "holoui_navbar", if(value == true) 1 else 0)
            showRestartRequired()
            true
        }
    }
}