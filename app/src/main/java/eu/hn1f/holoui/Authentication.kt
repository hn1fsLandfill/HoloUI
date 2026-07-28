package eu.hn1f.holoui

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.graphics.drawable.BitmapDrawable
import android.hardware.biometrics.BiometricManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.IBiometricSysuiReceiver
import android.hardware.biometrics.PromptInfo
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.android.internal.widget.LockPatternUtils
import com.android.internal.widget.LockscreenCredential
import kotlin.concurrent.thread

class Authentication(val context: Context) {
    val userId = ActivityManager.getCurrentUser()
    private var mDialog: AlertDialog? = null;
    private var mCallback: IBiometricSysuiReceiver? = null;

    class CheckResult(val success: Boolean = false,
                      val result: com.android.internal.widget.VerifyCredentialResponse?);

    fun check(dialogView: View): CheckResult? {
        val form = dialogView.findViewById<EditText>(R.id.password_form)

        val lockPattern = LockPatternUtils(context)
        val cred = when(lockPattern.getCredentialTypeForUser(userId)) {
            LockPatternUtils.CREDENTIAL_TYPE_PASSWORD -> {
                val text = form.text.toString().replace("\n", "")
                LockscreenCredential.createPassword(text)
            }
            LockPatternUtils.CREDENTIAL_TYPE_PIN -> {
                val text = form.text.toString().replace("\n", "")
                LockscreenCredential.createPin(text)
            }
            LockPatternUtils.CREDENTIAL_TYPE_NONE -> {
                return CheckResult(true, null)
            }
            else -> {
                showDialog("Unimplemented authentication type: ${lockPattern.getCredentialTypeForUser(userId)}")
                return null
            }
        }

        val resp = lockPattern.verifyCredential(cred, userId, 0);
        cred.zeroize()

        if(resp.isMatched) {
            return CheckResult(true, resp)
        } else {
            (context.applicationContext as SystemUIApplication).runInUIThread {
                showDialog("Authentication failure (invalid password/pin?)")
            }
            return null
        }
    }

    fun showDialog(msg: String) {
        val warning = AlertDialog.Builder(context)
            .setTitle("HoloUI")
            .setMessage(msg)
            .setPositiveButton("OK") { _, _ -> }
            .create()

        warning.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
        warning.show()
    }

    fun onBiometricAuthenticated() {
        mDialog!!.dismiss()
        mCallback!!.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_CREDENTIAL_CONFIRMED,
            null // TODO? for "STRONG" biometric apps like Bitwarden
            // Bitwarden seems to be happy tho?
        )
        mCallback = null
    }

    fun showAuthenticationDialog(promptInfo: PromptInfo, callback: IBiometricSysuiReceiver,
                                 uid: Int, opID: Long) {
        val dialog_view = LayoutInflater.from(context)
            .inflate(R.layout.authentication_dialog, null)

        val screenCredentialsNotAllowed =
            promptInfo.authenticators and BiometricManager.Authenticators.BIOMETRIC_STRONG != 0 ||
            promptInfo.authenticators and BiometricManager.Authenticators.BIOMETRIC_WEAK != 0 ||
            promptInfo.authenticators and BiometricManager.Authenticators.DEVICE_CREDENTIAL == 0

        val dialog = AlertDialog.Builder(context).let { builder ->
            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
                callback.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_USER_CANCEL, null)
            }
            if(!screenCredentialsNotAllowed)
                builder.setPositiveButton(android.R.string.ok, null)
            builder.setView(dialog_view)
            builder.setTitle(promptInfo.title)
            builder.setIcon(android.R.drawable.ic_dialog_alert)
            builder.setOnCancelListener {
                callback.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_USER_CANCEL, null)
            }

        }.create()

        if(promptInfo.logo != null)
            dialog.setIcon(
                BitmapDrawable(context.resources, promptInfo.logo)
            )

        if(screenCredentialsNotAllowed) {
            dialog_view.findViewById<View>(R.id.nonbiometric).visibility = View.GONE
            dialog_view.findViewById<View>(R.id.biometric).visibility = View.VISIBLE
        }

        mDialog = dialog
        mCallback = callback

        val form = dialog_view.findViewById<EditText>(R.id.password_form)
        val lockPattern = LockPatternUtils(context)

        when(lockPattern.getCredentialTypeForUser(uid)) {
            LockPatternUtils.CREDENTIAL_TYPE_PASSWORD -> {
                form.visibility = View.VISIBLE
            }
            LockPatternUtils.CREDENTIAL_TYPE_PIN -> {
                form.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
                form.visibility = View.VISIBLE
            }
            // Challenge Complete!
            // How Did We Get Here?
            LockPatternUtils.CREDENTIAL_TYPE_NONE -> {
                callback.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_CREDENTIAL_CONFIRMED,
                    null)
                mCallback = null
                return
            }
        }

        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
        dialog.show()
        callback.onDialogAnimatedIn(true)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            var result: CheckResult? = null

            thread { result = check(dialog_view) }.join()

            if(result != null && result!!.success) {
                dialog.dismiss()
                callback.onDeviceCredentialPressed()
                // jig is up you can stop horsing around android
                val hatResponse = lockPattern.verifyGatekeeperPasswordHandle(
                    result!!.result!!.gatekeeperPasswordHandle, opID, uid
                )
                Log.v("DroidCSS", "matched ${hatResponse.isMatched}")
                callback.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_CREDENTIAL_CONFIRMED,
                    hatResponse.gatekeeperHAT
                )
                mCallback = null
            }
        }
    }

    fun hideAuthenticationDialog() {
        if(mCallback == null) return
        mCallback!!.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_SERVER_REQUESTED, null)
        mDialog!!.dismiss()
    }
}