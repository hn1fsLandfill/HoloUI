package eu.hn1f.holoui

import android.animation.Animator
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Binder
import android.os.ServiceManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.widget.FrameLayout
import com.android.internal.statusbar.IStatusBarService

class StatusBar(val context: Context) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val statusbarService = IStatusBarService.Stub.asInterface(
        ServiceManager.getService(Context.STATUS_BAR_SERVICE)
    )
    val inflater = LayoutInflater.from(context)
    var root: FrameLayout? = null
    val statusBarImpl = StatusBarImpl(this)
    val barHeight = context.resources.getDimensionPixelSize(R.dimen.statusbar_height)
    var shade: NotificationShade? = null
    var lockscreen: Lockscreen? = null

    fun hideStatusBar() {
        val animator = root!!.animate()
        animator.translationY(-barHeight.toFloat())
        animator.withEndAction {
            root!!.visibility = View.GONE
        }
        animator.start()
    }
    fun showStatusBar() {
        root!!.visibility = View.VISIBLE
        val animator = root!!.animate()
        animator.translationY(0f)
        animator.start()
    }

    fun expandStatusBar() {
        shade!!.show()
    }
    fun unexpandStatusBar() {
        shade!!.hide()
    }

    fun add() {
        root = inflater.inflate(R.layout.root, null) as FrameLayout?
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            barHeight,
            WindowManager.LayoutParams.TYPE_STATUS_BAR,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                    or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            PixelFormat.TRANSLUCENT)
        lp.token = Binder()
        lp.gravity = Gravity.TOP
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        lp.title = "StatusBar"
        lp.packageName = context.packageName
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        lp.windowAnimations = android.R.anim.fade_out
        windowManager.addView(root, lp)
    }

    fun init() {
        add()
        root!!.setBackgroundColor(Color.BLACK)
        val statusBar = inflater.inflate(R.layout.status_bar, root)
        statusBar.setPadding(64, 0, 64, 0)
        statusBar.setOnClickListener {
            expandStatusBar()
        }
        shade = NotificationShade(this)
        shade!!.init()
        statusbarService.registerStatusBar(statusBarImpl)

        lockscreen = Lockscreen(context)
        lockscreen!!.showLockscreen()
    }
}