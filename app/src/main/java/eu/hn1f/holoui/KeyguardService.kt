package eu.hn1f.holoui

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.os.Binder
import android.os.Bundle
import android.os.IBinder
import android.os.Process
import android.util.Log
import com.android.internal.policy.IKeyguardDismissCallback
import com.android.internal.policy.IKeyguardDrawnCallback
import com.android.internal.policy.IKeyguardExitCallback
import com.android.internal.policy.IKeyguardService
import com.android.internal.policy.IKeyguardStateCallback


class KeyguardService: Service() {
    val PERMISSION: String = Manifest.permission.CONTROL_KEYGUARD
    var bootCompleted = false;

    fun checkPermission() {
        // Avoid deadlock by avoiding calling back into the system process.
        if (Binder.getCallingUid() == Process.SYSTEM_UID) return

        // Otherwise,explicitly check for caller permission ...
        if (getBaseContext().checkCallingOrSelfPermission(PERMISSION) != PERMISSION_GRANTED) {
            Log.w("HoloUI", "Caller needs permission '" + PERMISSION)
            throw SecurityException(
                ("Access denied to process: " + Binder.getCallingPid()
                        + ", must have permission " + PERMISSION)
            )
        }
    }

    private var mBinder = object: IKeyguardService.Stub() {
        var mApplication: SystemUIApplication? = null;

        override fun setOccluded(isOccluded: Boolean, animate: Boolean) {}
        override fun addStateMonitorCallback(callback: IKeyguardStateCallback) {
            Log.v("HoloUI", "TODO: addStateMonitorCallback?")
            checkPermission()
            mApplication!!.stateCallback = callback
        }
        override fun verifyUnlock(callback: IKeyguardExitCallback) {
            Log.v("HoloUI", "TODO: Authentication handling (verifyUnlock)")
            checkPermission()
            callback.onKeyguardExitResult(true)
        }
        override fun dismiss(
            callback: IKeyguardDismissCallback?,
            message: CharSequence?
        ) {
            checkPermission()
            Log.v("HoloUI", "dismiss message $message")
            callback?.onDismissSucceeded()
        }
        // dreams = cute name for screensavers
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
            checkPermission()
            mApplication!!.runInUIThread {
                mApplication!!.statusBar!!.lockscreen!!.showLockscreen(true)
            }
        }
        override fun setKeyguardEnabled(enabled: Boolean) {
            Log.v("HoloUI", "setKeyguardEnabled")
            checkPermission()
            mApplication!!.runInUIThread {
                if(enabled)
                    mApplication!!.statusBar!!.lockscreen!!.showLockscreen()
                else
                    mApplication!!.statusBar!!.lockscreen!!.hideLockscreen()
            }
        }
        override fun onSystemReady() {
            bootCompleted = true
        }
        override fun doKeyguardTimeout(options: Bundle?) {
            Log.v("HoloUI", "TODO: doKeyguardTimeout")
        }
        override fun setSwitchingUser(switching: Boolean) {}
        override fun setCurrentUser(userId: Int) {}
        override fun onBootCompleted() {}
        override fun startKeyguardExitAnimation(
            startTime: Long,
            fadeoutDuration: Long
        ) {}
        override fun onShortPowerPressedGoHome() {
            Log.v("HoloUI", "TODO: onShortPowerPressedGoHome")
        }
        override fun dismissKeyguardToLaunch(intentToLaunch: Intent?) {}
        override fun onSystemKeyPressed(keycode: Int) {
            Log.v("HoloUI", "TODO: onSystemKeyPressed $keycode")
        }
        override fun showDismissibleKeyguard() {}
    }

    override fun onCreate() {
        super.onCreate()
        // TODO?
        Log.v("HoloUI", "STUB: KeyguardService created")
        // throw RuntimeException("mrow meow mrrp")
    }
    override fun onBind(intent: Intent?): IBinder {
        mBinder.mApplication = (application as SystemUIApplication)
        return mBinder;
    }
}