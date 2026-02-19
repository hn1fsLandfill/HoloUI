package eu.hn1f.holoui

import android.content.Context
import android.graphics.PixelFormat
import android.os.Binder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import eu.hn1f.holoui.widgets.PanelView

class NotificationShade(val core: StatusBar) {
    val context = core.context
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val inflater = LayoutInflater.from(context)
    var root: PanelView? = null

    fun show() {
        root!!.visibility = View.VISIBLE
    }
    fun hide() {
        root!!.visibility = View.GONE
    }

    fun add() {
        root = inflater.inflate(R.layout.notification_shade, null) as PanelView?
        hide()
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_STATUS_BAR_SUB_PANEL,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT)
        lp.token = Binder()
        lp.gravity = Gravity.TOP
        lp.title = "StatusBar"
        lp.packageName = context.packageName
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        lp.windowAnimations = android.R.anim.fade_out
        windowManager.addView(root, lp)
    }

    fun init() {
        add()
        root!!.findViewById<Button>(R.id.debug_shade).setOnClickListener {
            hide()
        }
    }
}