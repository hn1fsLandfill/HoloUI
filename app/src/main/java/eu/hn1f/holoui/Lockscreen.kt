package eu.hn1f.holoui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Binder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button

class Lockscreen(val context: Context) {
    val root = LayoutInflater.from(context).inflate(R.layout.lock_screen, null)
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val lp = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT)
    val token = Binder("Lockscreen")
    var shown = false

    init {
        lp.token = token
        lp.gravity = Gravity.TOP
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        lp.title = "Keyguard"
        lp.packageName = context.packageName
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        root.findViewById<Button>(R.id.unlock).setOnClickListener {
            Sounds(context).playUnlock()
            hideLockscreen()
        }
    }

    fun showLockscreen(sound: Boolean = false) {
        if(!shown) {
            windowManager.addView(root, lp)
            if(sound) Sounds(context).playLock()
            shown = true
        }
    }
    // when unlocked
    fun hideLockscreen() {
        if(shown) {
            windowManager.removeView(root)
            shown = false
        }
    }
}