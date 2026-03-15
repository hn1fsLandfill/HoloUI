package eu.hn1f.holoui.activities

import android.app.Activity
import android.os.Bundle
import android.view.ViewGroup
import eu.hn1f.holoui.R
import eu.hn1f.holoui.widgets.GoogleBalls

class GoogleBalls: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val googleBalls = GoogleBalls(this, null)
        addContentView(googleBalls,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
    }
}