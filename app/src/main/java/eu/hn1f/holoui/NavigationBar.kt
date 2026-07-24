package eu.hn1f.holoui

import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.PixelFormat
import android.hardware.input.InputManager
import android.content.Intent
import android.hardware.input.KeyGestureEvent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.InsetsFrameProvider
import android.view.LayoutInflater
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.LinearLayout

class NavigationBar(val context: Context) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var root: LinearLayout? = null
    val barHeight = context.resources.getDimensionPixelSize(R.dimen.navigationbar_height)
    var token = Binder("NavigationBar");
    var inflater = LayoutInflater.from(context)
    var isEnabled = true
    var invertNavbar = false
    val mApplication = context.applicationContext as SystemUIApplication

    // Starting in Android 16 QPR1 some keys seem to be offloaded to SystemUI
    fun interceptKeys() {
        if(Build.VERSION.SDK_INT_FULL < Build.VERSION_CODES_FULL.BAKLAVA+1) return;

        val inputManager = InputManager(mApplication)

        inputManager.registerKeyGestureEventHandler(
            listOf(KeyGestureEvent.KEY_GESTURE_TYPE_RECENT_APPS),
            object : InputManager.KeyGestureEventHandler {
                override fun handleKeyGestureEvent(ev: KeyGestureEvent, binder: IBinder) {
                    if(ev.action != KeyGestureEvent.ACTION_GESTURE_COMPLETE || ev.isCancelled)
                        return;

                    mApplication.statusBar!!.statusBarImpl.toggleRecents()
                }
            }
        )
    }

    fun reloadSettings() {
        isEnabled = Settings.Global.getInt(context.contentResolver, "holoui_navbar", 1) == 1
        invertNavbar = Settings.Global.getInt(context.contentResolver, "holoui_invert_navbar", 1) == 1
    }

    fun hide() {
        if(!isEnabled) return;

        val animator = root!!.animate()
        animator.translationY(barHeight.toFloat())
        animator.start()
    }
    fun show() {
        if(!isEnabled) return;

        root!!.visibility = View.VISIBLE
        val animator = root!!.animate()
        animator.translationY(0f)
        animator.start()
    }

    fun semiOpaque() {
        root?.setBackgroundColor(Color.argb(128,0,0,0))
    }

    fun opaque() {
        root?.setBackgroundColor(Color.BLACK)
    }

    // TODO: Landscape navigation bar
    fun add() {
        if(root != null) return;
        root = inflater.inflate(R.layout.navigation_bar, null) as LinearLayout?
        opaque()
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            barHeight,
            WindowManager.LayoutParams.TYPE_NAVIGATION_BAR,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                    or WindowManager.LayoutParams.FLAG_SLIPPERY,
            PixelFormat.TRANSLUCENT)
        lp.privateFlags = WindowManager.LayoutParams.PRIVATE_FLAG_LAYOUT_SIZE_EXTENDED_BY_CUTOUT
        lp.token = token
        lp.gravity = Gravity.BOTTOM
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        lp.title = "NavigationBar"
        lp.packageName = context.packageName
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        lp.windowAnimations = android.R.anim.fade_out
        lp.providedInsets = arrayOf<InsetsFrameProvider>(
            InsetsFrameProvider(token, 0, WindowInsets.Type.navigationBars())
                .setInsetsSize(Insets.of(0,barHeight,0,0))
        )
        windowManager.addView(root, lp)
    }

    fun remove() {
        if(root == null) return;
        windowManager.removeView(root)
        root = null
    }

    fun init() {
        reload()
        try {
            interceptKeys()
        } catch (e: IllegalArgumentException) {
            Log.e("HoloUI", Log.getStackTraceString(e))
        }
    }

    fun reload() {
        reloadSettings()

        if(!isEnabled) {
            remove()
            return
        } else {
            add()
        }

        val normal = root!!.findViewById<LinearLayout>(R.id.normal)
        if(invertNavbar) {
            normal.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            normal.layoutDirection = View.LAYOUT_DIRECTION_LTR
        }
    }
}