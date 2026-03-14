package eu.hn1f.holoui

import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.IBiometricSysuiReceiver
import android.hardware.biometrics.PromptInfo
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

    fun check(dialogView: View): Boolean {
        val form = dialogView.findViewById<EditText>(R.id.password_form)

        val lockPattern = LockPatternUtils(context)
        val cred = when(lockPattern.getCredentialTypeForUser(userId)) {
            LockPatternUtils.CREDENTIAL_TYPE_PASSWORD -> {
                val text = form.text
                LockscreenCredential.createPassword(text)
            }
            LockPatternUtils.CREDENTIAL_TYPE_PIN -> {
                val text = form.text
                LockscreenCredential.createPin(text)
            }
            LockPatternUtils.CREDENTIAL_TYPE_NONE -> {
                return true
            }
            else -> {
                showDialog("Unimplemented authentication type: ${lockPattern.getCredentialTypeForUser(userId)}")
                return false
            }
        }

        val resp = lockPattern.verifyCredential(cred, userId, 0);
        cred.zeroize()

        if(resp.isMatched) {
            return true
        } else {
            (context.applicationContext as SystemUIApplication).runInUIThread {
                showDialog("Authentication failure (invalid password/pin?)")
            }
            return false
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

    fun showAuthenticationDialog(promptInfo: PromptInfo, callback: IBiometricSysuiReceiver) {
        val dialog_view = LayoutInflater.from(context)
            .inflate(R.layout.authentication_dialog, null)

        val dialog = AlertDialog.Builder(context)
            .setNegativeButton(android.R.string.cancel) { _, _ ->
                callback.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_USER_CANCEL, null)
            }
            .setPositiveButton(android.R.string.ok, null)
            .setView(dialog_view)
            .setTitle(promptInfo.title)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setOnCancelListener {
                callback.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_USER_CANCEL, null)
            }
            .create()

        val form = dialog_view.findViewById<EditText>(R.id.password_form)
        val lockPattern = LockPatternUtils(context)

        when(lockPattern.getCredentialTypeForUser(userId)) {
            LockPatternUtils.CREDENTIAL_TYPE_PASSWORD -> {
                form.visibility = View.VISIBLE
            }
            LockPatternUtils.CREDENTIAL_TYPE_PIN -> {
                form.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
                form.visibility = View.VISIBLE
            }
        }

        dialog.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
        dialog.show()

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            var result = false

            thread { result = check(dialog_view) }.join()

            if(result) {
                dialog.dismiss()
                callback.onDialogDismissed(BiometricPrompt.DISMISSED_REASON_CREDENTIAL_CONFIRMED, null)
            }
        }
    }
}