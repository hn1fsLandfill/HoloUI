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

    interface PreferenceInterface {
        fun onPress(value: Any);
    }

    fun switchPreference(key: String, settingName: String, default: Int, listener: PreferenceInterface? = null) {
        val preference = findPreference(key) as SwitchPreference

        preference.isChecked = Settings.Global.getInt(contentResolver, settingName, default) == 1
        preference.setOnPreferenceChangeListener { _, value ->
            Settings.Global.putInt(contentResolver, settingName, if(value == true) 1 else 0)
            listener?.onPress(value)
            true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)

        val invert_navbar = findPreference("invert_navbar") as SwitchPreference
        switchPreference("use_nav", "holoui_navbar", 1,
            object : PreferenceInterface {
                override fun onPress(value: Any) {
                    invert_navbar.isEnabled = value as Boolean
                    (applicationContext as SystemUIApplication).navigationBar?.reload()
                }
            }
        )

        invert_navbar.isEnabled = (findPreference("use_nav") as SwitchPreference).isChecked
        switchPreference("invert_navbar", "holoui_invert_navbar", 1,
            object : PreferenceInterface {
                override fun onPress(value: Any) {
                    (applicationContext as SystemUIApplication).navigationBar?.reload()
                }
            }
        )

        switchPreference("hide_warning", "holoui_hide_warning", 0,
            object : PreferenceInterface {
                override fun onPress(value: Any) {
                    showRestartRequired()
                }
            }
        )

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