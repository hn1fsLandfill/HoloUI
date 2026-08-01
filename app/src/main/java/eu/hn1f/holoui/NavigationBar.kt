package eu.hn1f.holoui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.PixelFormat
import android.hardware.input.InputManager
import android.content.Intent
import android.content.res.Configuration
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
import android.widget.ImageView
import android.widget.LinearLayout

private const val PORTRAIT = 0
private const val LANDSCAPE = 1

private const val BACK_IME = 0
private const val BACK = 1
private const val HOME = 2
private const val RECENT = 3

private val KEY_ICONS_KITKAT = arrayOf(
    intArrayOf(
        R.drawable.ic_sysbar_back_ime,
        R.drawable.ic_sysbar_back,
        R.drawable.ic_sysbar_home,
        R.drawable.ic_sysbar_recent
    ),
    intArrayOf(
        R.drawable.ic_sysbar_back_land,
        R.drawable.ic_sysbar_back_land,
        R.drawable.ic_sysbar_home_land,
        R.drawable.ic_sysbar_recent_land
    )
)

class NavigationBar(val context: Context) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var root: LinearLayout? = null
    val barHeight = context.resources.getDimensionPixelSize(R.dimen.navigationbar_height)
    val barWidth = context.resources.getDimensionPixelSize(R.dimen.navigationbar_width)
    var token = Binder("NavigationBar");
    var inflater = LayoutInflater.from(context)
    var isEnabled = true
    var invertNavbar = false
    val mApplication = context.applicationContext as SystemUIApplication
    var lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
    var keys = KEY_ICONS_KITKAT

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
        lp = WindowManager.LayoutParams(
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
        lp.setTrustedOverlay()
        windowManager.addView(root, lp)
    }

    fun updateKeyIcons() {
        val normal: LinearLayout = root!!.findViewById(R.id.normal)
        val landscape: LinearLayout = root!!.findViewById(R.id.landscape)

        normal.findViewById<ImageView>(R.id.back).setImageResource(keys[PORTRAIT][BACK])
        normal.findViewById<ImageView>(R.id.home).setImageResource(keys[PORTRAIT][HOME])
        normal.findViewById<ImageView>(R.id.recent_apps).setImageResource(keys[PORTRAIT][RECENT])
        landscape.findViewById<ImageView>(R.id.back).setImageResource(keys[LANDSCAPE][BACK])
        landscape.findViewById<ImageView>(R.id.home).setImageResource(keys[LANDSCAPE][HOME])
        landscape.findViewById<ImageView>(R.id.recent_apps).setImageResource(keys[LANDSCAPE][RECENT])
    }

    fun updateKeys(isLandscape: Boolean) {
        val normal: LinearLayout = root!!.findViewById(R.id.normal)
        val landscape: LinearLayout = root!!.findViewById(R.id.landscape)

        if(isLandscape) {
            normal.visibility = View.GONE
            landscape.visibility = View.VISIBLE
        } else {
            normal.visibility = View.VISIBLE
            landscape.visibility = View.GONE
        }
    }

    @SuppressLint("RtlHardcoded")
    fun onRotation(deg: Int) {
        windowManager.removeViewImmediate(root)
        when(deg) {
            0,
            360,
            180 -> {
                // portrait
                lp.gravity = Gravity.BOTTOM
                lp.width = WindowManager.LayoutParams.MATCH_PARENT
                lp.height = barHeight
                lp.providedInsets = arrayOf<InsetsFrameProvider>(
                    InsetsFrameProvider(token, 0, WindowInsets.Type.navigationBars())
                        .setInsetsSize(Insets.of(0,barHeight,0,0))
                )
                updateKeys(false)
            }
            90,
            270 -> {
                // landscape
                lp.gravity = Gravity.RIGHT
                lp.width = barWidth
                lp.height = WindowManager.LayoutParams.MATCH_PARENT
                lp.providedInsets = arrayOf<InsetsFrameProvider>(
                    InsetsFrameProvider(token, 0, WindowInsets.Type.navigationBars())
                        .setInsetsSize(Insets.of(0,0,barWidth,0))
                )
                updateKeys(true)
            }
        }
        windowManager.addView(root, lp)
        root?.invalidate()
    }

    fun onConfigurationChanged(newConfig: Configuration) {
        val rotation = if(newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE)
            90
        else
            0

        onRotation(rotation)
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
        val landscape: LinearLayout = root!!.findViewById(R.id.landscape)
        if(invertNavbar) {
            normal.layoutDirection = View.LAYOUT_DIRECTION_RTL
            landscape.layoutDirection = View.LAYOUT_DIRECTION_RTL
        } else {
            normal.layoutDirection = View.LAYOUT_DIRECTION_LTR
            landscape.layoutDirection = View.LAYOUT_DIRECTION_RTL
        }
    }
}