package eu.hn1f.holoui

import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.PixelFormat
import android.os.Binder
import android.view.Gravity
import android.view.InsetsFrameProvider
import android.view.KeyEvent
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

    fun hide() {
        val animator = root!!.animate()
        animator.translationY(barHeight.toFloat())
        animator.withEndAction {
            root!!.visibility = View.INVISIBLE
        }
        animator.start()
    }
    fun show() {
        root!!.visibility = View.VISIBLE
        val animator = root!!.animate()
        animator.translationY(0f)
        animator.start()
    }

    // TODO: Landscape navigation bar
    fun add() {
        root = inflater.inflate(R.layout.navigation_bar, null) as LinearLayout?
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

    fun init() {
        add()
        root!!.setBackgroundColor(Color.BLACK)
    }
}