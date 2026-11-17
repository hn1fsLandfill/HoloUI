package eu.hn1f.holoui.transitions

import android.app.ActivityTaskManager
import android.content.Context
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.SurfaceControl
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.window.ITransitionPlayer
import android.window.IWindowContainerToken
import android.window.StartingWindowRemovalInfo
import android.window.TransitionInfo
import android.window.TransitionRequestInfo
import android.window.WindowContainerToken
import android.window.WindowContainerTransaction
import java.util.concurrent.Executor
import kotlin.concurrent.thread

private class EngineInfo(val iBinder: IBinder, val transitionInfo: TransitionInfo,
                 val t: SurfaceControl.Transaction, val finishT: SurfaceControl.Transaction)

class TransitionEngine: Handler {
    val controller = ActivityTaskManager.getService().windowOrganizerController!!
    val defaultAnimation: Animation
    val pool = TransactionPool()

    constructor(looper: Looper, context: Context): super(looper) {
        defaultAnimation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)

        val info = msg.obj as EngineInfo

        if(info.transitionInfo.flags and TransitionInfo.FLAG_NO_ANIMATION != 0) {
            info.finishT.close()
            info.t.close()
            info.transitionInfo.releaseAllSurfaces()
            controller.finishTransition(info.iBinder, null)
            return
        }

        val iWct = IWindowContainerToken.Stub.asInterface(info.iBinder)
        val wct = WindowContainerTransaction()

        val change = info.transitionInfo.getChange(WindowContainerToken(iWct))

        if(change == null) {

            Log.v("HoloUI", "change is null");
            info.finishT.close()
            info.t.close()
            info.transitionInfo.releaseAllSurfaces()
            controller.finishTransition(info.iBinder, null)
            return
        }

        pool.release(info.t)

        val animation = DefaultSurfaceAnimator.buildWindowAnimation(
            defaultAnimation, change, change.leash, {}, pool, null /* probably needs this*/,
            null, 1f, null, null
        );

        animation.addFinishCallback({ wa ->
            info.t.show(change.leash)
            info.finishT.show(change.leash)
            info.finishT.close()
            info.t.close()
            info.transitionInfo.releaseAllSurfaces()
            controller.finishTransition(info.iBinder, null)
        }, null)

        animation.start()

        /* if(info.transitionInfo.flags and TransitionInfo.FLAG_NO_ANIMATION != 0) {
            info.t.apply()
            info.t.close()
            info.transitionInfo.releaseAllSurfaces()
            controller.finishTransition(info.iBinder, null)
            return
        }

        // do shit with T later
        info.finishT.setAlpha(info.transitionInfo.getRoot(0).leash, 0.5f)
        info.t.apply()
        info.t.close()
        info.transitionInfo.releaseAllSurfaces()

        controller.finishTransition(info.iBinder, null) */
    }
}

class Transitions {
    val controller = ActivityTaskManager.getService().windowOrganizerController!!

    var engine: TransitionEngine? = null

    val player = object: ITransitionPlayer.Stub() {
        override fun onTransitionReady(
            iBinder: IBinder,
            transitionInfo: TransitionInfo,
            t: SurfaceControl.Transaction,
            finishT: SurfaceControl.Transaction
        ) {
            val msg = Message()
            msg.obj = EngineInfo(iBinder, transitionInfo, t, finishT)
            engine!!.sendMessage(msg)
        }

        override fun removeStartingWindow(p0: StartingWindowRemovalInfo?) {
        }

        override fun requestStartTransition(
            p0: IBinder?,
            p1: TransitionRequestInfo?
        ) {
            if(p0 != null)
                controller.startTransition(p0, null)
        }
    }

    // disable for now
    val ENABLE_SHELLTRANSITIONS = false

    fun register(context: Context) {
        if(ENABLE_SHELLTRANSITIONS) {
            thread(isDaemon = true) {
                Looper.prepare()
                engine = TransitionEngine(Looper.myLooper()!!, context)
                Looper.loop()
            }

            controller.registerTransitionPlayer(player)
        }
    }
}