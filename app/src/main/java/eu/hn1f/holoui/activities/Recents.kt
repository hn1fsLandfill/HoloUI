package eu.hn1f.holoui.activities

import android.app.Activity
import android.os.Bundle
import eu.hn1f.holoui.R

class Recents: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recents)
    }
}