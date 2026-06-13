package eu.hn1f.holoui.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Process
import android.preference.Preference
import android.preference.PreferenceActivity
import android.preference.SwitchPreference
import android.provider.Settings
import eu.hn1f.holoui.R
import eu.hn1f.holoui.SystemUIApplication

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
        val use_nav = findPreference("use_nav") as SwitchPreference
        val invert_navbar = findPreference("invert_navbar") as SwitchPreference

        use_nav.isChecked = Settings.Global.getInt(contentResolver, "holoui_navbar", 1) == 1
        use_nav.setOnPreferenceChangeListener { _, value ->
            Settings.Global.putInt(contentResolver, "holoui_navbar", if(value == true) 1 else 0)
            invert_navbar.isEnabled = value as Boolean
            (applicationContext as SystemUIApplication).navigationBar?.reload()
            true
        }

        invert_navbar.isEnabled = use_nav.isChecked
        invert_navbar.isChecked = Settings.Global.getInt(contentResolver, "holoui_invert_navbar", 1) == 1
        invert_navbar.setOnPreferenceChangeListener { _, value ->
            Settings.Global.putInt(contentResolver, "holoui_invert_navbar", if(value == true) 1 else 0)
            (applicationContext as SystemUIApplication).navigationBar?.reload()
            true
        }

        val aboutversion = findPreference("aboutversion") as Preference
        var taps = 0

        aboutversion.onPreferenceClickListener = object: Preference.OnPreferenceClickListener {
            override fun onPreferenceClick(preference: Preference?): Boolean {
                taps++;
                if(taps == 5) {
                    val intent = Intent(applicationContext, GoogleBalls::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    taps = 0
                }
                return true
            }
        }
    }
}