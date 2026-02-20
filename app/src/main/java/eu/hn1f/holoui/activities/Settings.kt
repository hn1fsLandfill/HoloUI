package eu.hn1f.holoui.activities

import android.app.Activity
import android.os.Bundle
import android.preference.PreferenceActivity
import eu.hn1f.holoui.R

@Suppress("DEPRECATION")
class Settings: PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.settings)
    }
}