package eu.hn1f.holoui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.PixelFormat
import android.os.Binder
import android.os.ServiceManager
import android.util.Log
import android.view.Gravity
import android.view.InsetsFrameProvider
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.WindowManagerPolicyConstants
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.android.internal.statusbar.IStatusBarService
import eu.hn1f.holoui.policy.NetworkController
import eu.hn1f.holoui.widgets.SignalClusterView



class StatusBar(val context: Context) {
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val statusbarService = IStatusBarService.Stub.asInterface(
        ServiceManager.getService(Context.STATUS_BAR_SERVICE)
    )
    val inflater = LayoutInflater.from(context)
    var root: FrameLayout? = null
    var statusBar: LinearLayout? = null
    val statusBarImpl = StatusBarImpl(this)
    val barHeight = context.resources.getDimensionPixelSize(R.dimen.statusbar_height)
    var shade: NotificationShade? = null
    var lockscreen: Lockscreen? = null
    var windowInsetsOwner = Binder();

    val pointEventListener = object : WindowManagerPolicyConstants.PointerEventListener {
        override fun onPointerEvent(motionEvent: MotionEvent) {
            TODO("Not yet implemented")
            Log.v("HoloUI", "onPointerEvent $motionEvent")
        }
    }

    fun hideStatusBar() {
        val animator = statusBar!!.animate()
        animator.translationY(-barHeight.toFloat())
        animator.withEndAction {
            statusBar!!.visibility = View.INVISIBLE
        }
        animator.start()
    }
    fun showStatusBar() {
        statusBar!!.visibility = View.VISIBLE
        val animator = statusBar!!.animate()
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
        lp.providedInsets = arrayOf<InsetsFrameProvider>(
            InsetsFrameProvider(windowInsetsOwner, 0, WindowInsets.Type.statusBars())
                .setInsetsSize(Insets.of(0,barHeight,0,0))
        )
        windowManager.addView(root, lp)
    }

    fun semiOpaque() {
        statusBar!!.setBackgroundColor(Color.pack(0f,0f,0f,0.5f).toInt())
    }
    fun opaque() {
        statusBar!!.setBackgroundColor(Color.BLACK)
    }

    @SuppressLint("ClickableViewAccessibility")
    fun init() {
        add()
        root!!.setOnTouchListener { v, event ->
            // dispatch to the notification shade
            Log.v("HoloUI", "touch event $event")
            return@setOnTouchListener shade!!.root!!.dispatchTouchEvent(event)
        }

        statusBar = inflater.inflate(R.layout.status_bar, null) as LinearLayout?
        opaque()
        root!!.addView(statusBar)

        shade = NotificationShade(this)
        shade!!.init()
        statusbarService.registerStatusBar(statusBarImpl)
        lockscreen = Lockscreen(context)
        lockscreen!!.showLockscreen()

        val mNetworkController = NetworkController(context)
        val signalCluster: SignalClusterView? =
            root!!.findViewById(R.id.signal_cluster)

        mNetworkController.addSignalCluster(signalCluster)
        signalCluster!!.setNetworkController(mNetworkController)

        // TODO: Escaping full-screen apps gesture
        // windowManagerService.registerPointerEventListener(pointEventListener, 0);
    }
}
