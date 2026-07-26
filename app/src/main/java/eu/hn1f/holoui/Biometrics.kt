package eu.hn1f.holoui

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal

// TODO: Underdisplay fingerprints (via getSensorPropertiesInternal?)
class Biometrics {
    val fm: FingerprintManager?;
    var cancelSignal: CancellationSignal? = null;
    private var userCallback: BiometricCallback? = null;

    interface BiometricCallback {
        fun onAuthSuccess();
        fun onAuthFail();
    }

    constructor(context: Context, callback: BiometricCallback) {
        fm = context.getSystemService(FingerprintManager::class.java)
        userCallback = callback
    }

    fun startListening(userId: Int) {
        if(fm == null || !fm.isHardwareDetected) return;

        cancelSignal?.cancel()
        cancelSignal = CancellationSignal()

        fm.authenticate(
            null,
            cancelSignal,
            callback,
            null,
            userId
        )
    }
    fun stopListening() {
        cancelSignal?.cancel()
    }

    val callback = object : FingerprintManager.AuthenticationCallback() {
        override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
            super.onAuthenticationSucceeded(result)
            userCallback!!.onAuthSuccess()
        }

        override fun onAuthenticationFailed() {
            userCallback!!.onAuthFail()
        }
    }
}