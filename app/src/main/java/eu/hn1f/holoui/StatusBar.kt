package eu.hn1f.holoui

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.Insets
import android.graphics.PixelFormat
import android.hardware.input.InputManagerGlobal
import android.os.Binder
import android.os.Looper
import android.os.ServiceManager
import android.util.Log
import android.view.Gravity
import android.view.IDisplayWindowInsetsController
import android.view.IWindowManager
import android.view.InputChannel
import android.view.InputEvent
import android.view.InputEventReceiver
import android.view.InsetsFrameProvider
import android.view.InsetsSourceControl
import android.view.InsetsState
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.view.inputmethod.ImeTracker
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.android.internal.statusbar.IStatusBarService
import eu.hn1f.holoui.policy.NetworkController
import eu.hn1f.holoui.widgets.FlingTracker
import eu.hn1f.holoui.widgets.SignalClusterView
import kotlin.concurrent.thread


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

    private var leashes = DisplayWindowInsetsController()

    var isImmersed = false

    private class DisplayWindowInsetsController: IDisplayWindowInsetsController.Stub() {
        var statusBarControl: InsetsSourceControl? = null
        var navBarControl: InsetsSourceControl? = null

        override fun insetsChanged(p0: InsetsState?) {}

        //
        override fun insetsControlChanged(p0: InsetsState?, controls: Array<out InsetsSourceControl?>?) {
            controls?.forEach { control ->
                if (control?.type == WindowInsets.Type.statusBars()) {
                    this.statusBarControl = control
                }
            }
        }
        override fun hideInsets(p0: Int, p1: Boolean, p2: ImeTracker.Token?) {}
        override fun showInsets(p0: Int, p1: Boolean, p2: ImeTracker.Token?) {}

        override fun setImeInputTargetRequestedVisibility(
            p0: Boolean,
            p1: ImeTracker.Token?
        ) {
            // TODO("Not yet implemented")
        }

        override fun topFocusedWindowChanged(p0: ComponentName?, p1: Int) {
            // TODO("Not yet implemented")
        }

    }

    private class InputListener: InputEventReceiver {
        val statusBar: StatusBar
        val flingTracker = FlingTracker()
        var stalkingEvents = false

        constructor(sb: StatusBar, channel: InputChannel, looper: Looper)
                : super(channel, looper) {
            statusBar = sb
        }

        fun onSwipe() {
            Log.v("HoloUI", "swiping down event")
            statusBar.run {
                showStatusBar()
                (context.applicationContext as SystemUIApplication).navigationBar!!
                    .show()
            }
            /* Handler(Looper.getMainLooper()).postDelayed({
                if(!statusBar.isImmersed) return@postDelayed

                statusBar.hideStatusBar()
                (statusBar.context.applicationContext as SystemUIApplication).navigationBar!!
                    .hide()
            }, 3000) */
        }

        override fun onInputEvent(inputEvent: InputEvent) {
            super.onInputEvent(inputEvent)
            if(inputEvent !is MotionEvent || !statusBar.isImmersed) return
            // this makes the code less confusing imo
            @Suppress("USELESS_CAST")
            val event = inputEvent as MotionEvent

            if(event.action == MotionEvent.ACTION_DOWN && event.y < statusBar.barHeight) {
                Log.v("HoloUI", "i AM A STALKER!!")
                stalkingEvents = true
                flingTracker.trackMovement(event)
            } else if(event.action == MotionEvent.ACTION_MOVE && stalkingEvents) {
                flingTracker.trackMovement(event)
            } else if(event.action == MotionEvent.ACTION_UP && stalkingEvents) {
                flingTracker.computeCurrentVelocity(1000)
                val yV = flingTracker.yVelocity
                flingTracker.recycle()
                stalkingEvents = false

                Log.v("HoloUI", "velocity y $yV")

                if(yV > 100)
                    runInUIThread { onSwipe() }
            } else if(event.action == MotionEvent.ACTION_CANCEL && stalkingEvents) flingTracker.recycle()
        }
    }

    fun hideStatusBar() {
        val animator = statusBar!!.animate()
        animator.translationY(-barHeight.toFloat())
        animator.withEndAction {
            statusBar!!.visibility = View.GONE
        }
        animator.start()
        isImmersed = true
    }
    fun showStatusBar(immersed: Boolean? = null) {
        statusBar!!.visibility = View.VISIBLE
        val animator = statusBar!!.animate()
        animator.translationY(0f)
        animator.start()

        if(immersed != null) isImmersed = immersed
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
        val wms = IWindowManager.Stub.asInterface(ServiceManager.getService(Context.WINDOW_SERVICE))
        wms.setDisplayWindowInsetsController(context.display.displayId, leashes)

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

        thread(isDaemon = true) {
            val monitor = InputManagerGlobal.getInstance().monitorGestureInput(
                "statusBarTracker",
                context.display.displayId
            )

            Looper.prepare()
            InputListener(this, monitor.inputChannel, Looper.myLooper()!!)
            Looper.loop()
        }
    }
}
