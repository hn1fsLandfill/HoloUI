package eu.hn1f.holoui

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.android.internal.policy.IKeyguardDismissCallback
import com.android.internal.policy.IKeyguardDrawnCallback
import com.android.internal.policy.IKeyguardExitCallback
import com.android.internal.policy.IKeyguardService
import com.android.internal.policy.IKeyguardStateCallback

class KeyguardService: Service() {
    private class BinderService(val service: KeyguardService): IKeyguardService.Stub() {
        var mApplication: SystemUIApplication? = null;

        override fun setOccluded(isOccluded: Boolean, animate: Boolean) {}
        override fun addStateMonitorCallback(callback: IKeyguardStateCallback?) {
            Log.v("HoloUI", "TODO: addStateMonitorCallback?")
            callback!!.onShowingStateChanged(true, 0)
        }
        override fun verifyUnlock(callback: IKeyguardExitCallback) {
            Log.v("HoloUI", "TODO: Authentication handling")
            callback.onKeyguardExitResult(true)
        }
        override fun dismiss(
            callback: IKeyguardDismissCallback?,
            message: CharSequence?
        ) {
            Log.v("HoloUI", "dismiss message $message")
            callback?.onDismissSucceeded()
        }
        override fun onDreamingStarted() {}
        override fun onDreamingStopped() {}
        override fun onStartedGoingToSleep(pmSleepReason: Int) {}
        override fun onFinishedGoingToSleep(
            pmSleepReason: Int,
            powerButtonLaunchGestureTriggered: Boolean
        ) {}
        override fun onStartedWakingUp(
            pmWakeReason: Int,
            powerButtonLaunchGestureTriggered: Boolean
        ) {}
        override fun onFinishedWakingUp() {}
        override fun onScreenTurningOn(
            reason: Int,
            callback: IKeyguardDrawnCallback
        ) {
            callback.onDrawn()
        }
        override fun onScreenTurnedOn() {
            Log.v("HoloUI", "who woke me up")
        }
        override fun onScreenTurningOff() {}
        override fun onScreenTurnedOff() {
            mApplication!!.runInUIThread {
                mApplication!!.statusBar!!.lockscreen!!.showLockscreen(true)
            }
        }
        override fun setKeyguardEnabled(enabled: Boolean) {
            mApplication!!.runInUIThread {
                if(enabled)
                    mApplication!!.statusBar!!.lockscreen!!.showLockscreen()
                else
                    mApplication!!.statusBar!!.lockscreen!!.hideLockscreen()
            }
        }
        override fun onSystemReady() {}
        override fun doKeyguardTimeout(options: Bundle?) {}
        override fun setSwitchingUser(switching: Boolean) {}
        override fun setCurrentUser(userId: Int) {}
        override fun onBootCompleted() {}
        override fun startKeyguardExitAnimation(
            startTime: Long,
            fadeoutDuration: Long
        ) {}
        override fun onShortPowerPressedGoHome() {}
        override fun dismissKeyguardToLaunch(intentToLaunch: Intent?) {}
        override fun onSystemKeyPressed(keycode: Int) {}
        override fun showDismissibleKeyguard() {}
    }

    private var mBinder = BinderService(this);

    override fun onCreate() {
        super.onCreate()
        // TODO?
        Log.v("HoloUI", "STUB: KeyguardService created")
        // throw RuntimeException("mrow meow mrrp")
    }
    override fun onBind(p0: Intent?): IBinder? {
        mBinder.mApplication = (application as SystemUIApplication)
        return mBinder;
    }
}