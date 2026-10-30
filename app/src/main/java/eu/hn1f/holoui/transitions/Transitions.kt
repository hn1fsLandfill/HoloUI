package eu.hn1f.holoui.transitions

import android.app.ActivityTaskManager
import android.os.IBinder
import android.view.SurfaceControl
import android.window.ITransitionPlayer
import android.window.StartingWindowRemovalInfo
import android.window.TransitionInfo
import android.window.TransitionRequestInfo
import kotlin.concurrent.thread

class Transitions {
    val controller = ActivityTaskManager.getService().windowOrganizerController!!

    val player = object: ITransitionPlayer.Stub() {
        override fun onTransitionReady(
            iBinder: IBinder,
            transitionInfo: TransitionInfo,
            t: SurfaceControl.Transaction,
            finishT: SurfaceControl.Transaction
        ) {
            thread {
                // do shit with T later
                t.apply()
                t.close()
                transitionInfo.releaseAllSurfaces()

                controller.finishTransition(iBinder, null)
            }
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

    fun register() {
        controller.registerTransitionPlayer(player)
    }
}