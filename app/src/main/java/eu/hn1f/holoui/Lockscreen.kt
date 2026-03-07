package eu.hn1f.holoui

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.PixelFormat
import android.os.Binder
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import com.android.internal.widget.LockPatternUtils
import com.android.internal.widget.LockscreenCredential
import kotlin.concurrent.thread

class Lockscreen(val context: Context) {
    val root = LayoutInflater.from(context).inflate(R.layout.lock_screen, null)
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val lp = WindowManager.LayoutParams(
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.MATCH_PARENT,
        WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG,
        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        PixelFormat.TRANSLUCENT)
    val token = Binder("Lockscreen")
    var shown = false

    init {
        lp.token = token
        lp.gravity = Gravity.TOP
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        lp.title = "Keyguard"
        lp.packageName = context.packageName
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        root.findViewById<Button>(R.id.unlock).setOnClickListener {
            unlock()
        }
    }

    fun showDialog(msg: String) {
        val warning = AlertDialog.Builder(context)
            .setTitle("HoloUI")
            .setMessage(msg)
            .setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->})
            .create()

        warning.window!!.setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG)
        warning.show()
    }

    fun onSuccess() {
        (context.applicationContext as SystemUIApplication).runInUIThread {
            Sounds(context).playUnlock()
            hideLockscreen()
        }
    }

    fun unlock() {
        val userId = 0
        thread {
            val lockPattern = LockPatternUtils(context)
            val cred = when(lockPattern.getCredentialTypeForUser(userId)) {
                LockPatternUtils.CREDENTIAL_TYPE_PASSWORD -> {
                    LockscreenCredential.createPassword("test")
                }
                LockPatternUtils.CREDENTIAL_TYPE_NONE -> {
                    onSuccess()
                    return@thread
                }
                else -> {
                    showDialog("Unimplemented authentication type: ${lockPattern.getCredentialTypeForUser(userId)}")
                    return@thread
                }
            }

            val resp = lockPattern.verifyCredential(cred, userId, 0);
            if(resp.isMatched) {
                (context.applicationContext as SystemUIApplication).runInUIThread {
                    Sounds(context).playUnlock()
                    hideLockscreen()
                }
                lockPattern.reportSuccessfulPasswordAttempt(userId)
            } else {
                (context.applicationContext as SystemUIApplication).runInUIThread {
                    showDialog("Authentication failure")
                }
            }

            cred.zeroize()
        }
    }

    fun showLockscreen(sound: Boolean = false) {
        if(!shown) {
            windowManager.addView(root, lp)
            if (sound) Sounds(context).playLock()
            shown = true

            if((context.applicationContext as SystemUIApplication).stateCallback == null) {
                Log.v("HoloUI","no statecallback, strange");
            } else {
                (context.applicationContext as SystemUIApplication).stateCallback?.onShowingStateChanged(
                    true,
                    0
                )
            }
        }
    }
    // when unlocked
    fun hideLockscreen() {
        if(shown) {
            windowManager.removeView(root)
            shown = false
            (context.applicationContext as SystemUIApplication).stateCallback?.onShowingStateChanged(false, 0)
        }
    }
}