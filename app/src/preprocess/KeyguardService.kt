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
    val DEBUG_KG = true;

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

    fun trace(msg: String) {
        if(DEBUG_KG)
            Log.v("KeyguardService", msg)
    }

    private var mBinder = object: IKeyguardService.Stub() {
        var mApplication: SystemUIApplication? = null;

        override fun setOccluded(isOccluded: Boolean, animate: Boolean) {
            trace("setOccluded")
        }
        override fun addStateMonitorCallback(callback: IKeyguardStateCallback) {
            trace("addStateMonitorCallback")
            checkPermission()
            mApplication!!.stateCallback = callback
        }
        override fun verifyUnlock(callback: IKeyguardExitCallback) {
            trace("verifyUnlock")
            Log.v("HoloUI", "TODO: Authentication handling (verifyUnlock)")
            checkPermission()
            callback.onKeyguardExitResult(true)
        }
        override fun dismiss(
            callback: IKeyguardDismissCallback?,
            message: CharSequence?
        ) {
            trace("dismiss")
            checkPermission()
            Log.v("HoloUI", "dismiss message $message")
            callback?.onDismissSucceeded()
        }
        // dreams = cute name for screensavers
        override fun onDreamingStarted() { trace("onDreamingStarted") }
        override fun onDreamingStopped() { trace("onDreamingStopped") }

        override fun onStartedGoingToSleep(pmSleepReason: Int) {
            trace("onStartedGoingToSleep")
        }
        override fun onFinishedGoingToSleep(
            pmSleepReason: Int,
            powerButtonLaunchGestureTriggered: Boolean
        ) {
            trace("onFinishedGoingToSleep")
        }
        override fun onStartedWakingUp(
            pmWakeReason: Int,
            powerButtonLaunchGestureTriggered: Boolean
        ) {
            trace("onStartedWakingUp")
        }
        override fun onFinishedWakingUp() {
            trace("onFinishedWakingUp")
        }
        override fun onScreenTurningOn(
            #ifdef BAKLAVA_QPR2_LATER
            reason: Int,
            #endif
            callback: IKeyguardDrawnCallback) {
            trace("onScreenTurningOn")
            mApplication!!.statusBar!!.lockscreen!!.showLockscreen()
            callback.onDrawn()
        }
        override fun onScreenTurnedOn() {
            trace("onScreenTurnedOn")
        }
        override fun onScreenTurningOff() {
            trace("onScreenTurningOff")
        }

        override fun onScreenTurnedOff() {
            trace("onScreenTurnedOff")
            checkPermission()
            mApplication!!.runInUIThread {
                mApplication!!.statusBar!!.lockscreen!!.showLockscreen(true)
            }
        }
        override fun setKeyguardEnabled(enabled: Boolean) {
            trace("setKeyguardEnabled")
            checkPermission()
            mApplication!!.runInUIThread {
                if(enabled)
                    mApplication!!.statusBar!!.lockscreen!!.showLockscreen()
                else
                    mApplication!!.statusBar!!.lockscreen!!.hideLockscreen()
            }
        }
        override fun onSystemReady() {
            trace("onSystemReady")
            bootCompleted = true
        }
        override fun doKeyguardTimeout(options: Bundle?) {
            trace("doKeyguardTimeout")
            Log.v("HoloUI", "TODO: doKeyguardTimeout")
        }
        override fun setSwitchingUser(switching: Boolean) {
            trace("setSwitchingUser")
        }
        override fun setCurrentUser(userId: Int) {
            trace("setCurrentUser")
        }
        override fun onBootCompleted() {
            trace("onBootCompleted")
        }
        override fun startKeyguardExitAnimation(
            startTime: Long,
            fadeoutDuration: Long
        ) {
            trace("startKeyguardExitAnimation")
        }
        override fun onShortPowerPressedGoHome() {
            Log.v("HoloUI", "TODO: onShortPowerPressedGoHome")
        }
        override fun dismissKeyguardToLaunch(intentToLaunch: Intent?) {
            trace("dismissKeyguardToLaunch")
        }
        override fun onSystemKeyPressed(keycode: Int) {
            trace("onSystemKeyPressed")
            Log.v("HoloUI", "TODO: onSystemKeyPressed $keycode")
        }
        override fun showDismissibleKeyguard() {
            trace("showDismissibleKeyguard")
        }
    }

    override fun onCreate() {
        super.onCreate()
        // TODO?
        Log.v("HoloUI", "KeyguardService created")
        // throw RuntimeException("mrow meow mrrp")
    }
    override fun onBind(intent: Intent?): IBinder {
        mBinder.mApplication = (application as SystemUIApplication)
        return mBinder;
    }
}