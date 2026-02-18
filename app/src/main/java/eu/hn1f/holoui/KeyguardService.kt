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
    private class BinderService: IKeyguardService.Stub() {
        override fun setOccluded(isOccluded: Boolean, animate: Boolean) {}
        override fun addStateMonitorCallback(callback: IKeyguardStateCallback?) {}
        override fun verifyUnlock(callback: IKeyguardExitCallback?) {}
        override fun dismiss(
            callback: IKeyguardDismissCallback?,
            message: CharSequence?
        ) {}
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
            callback: IKeyguardDrawnCallback?
        ) {}
        override fun onScreenTurnedOn() {
            Log.v("HoloUI", "who woke me up")
        }
        override fun onScreenTurningOff() {}
        override fun onScreenTurnedOff() {}
        override fun setKeyguardEnabled(enabled: Boolean) {}
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

    private var mBinder = BinderService();

    override fun onCreate() {
        super.onCreate()
        // TODO?
        Log.v("HoloUI", "STUB: KeyguardService created")
        // throw RuntimeException("mrow meow mrrp")
    }
    override fun onBind(p0: Intent?): IBinder? {
        return mBinder;
    }
}