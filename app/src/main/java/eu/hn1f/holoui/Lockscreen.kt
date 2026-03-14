package eu.hn1f.holoui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AlertDialog
import android.content.Context
import android.graphics.PixelFormat
import android.os.Binder
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import com.android.internal.widget.LockPatternUtils
import com.android.internal.widget.LockscreenCredential
import eu.hn1f.holoui.widgets.Clock
import kotlin.concurrent.thread

class Lockscreen(val context: Context) {
    @SuppressLint("InflateParams")
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
    val userId = ActivityManager.getCurrentUser()

    init {
        lp.token = token
        lp.gravity = Gravity.TOP
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        lp.title = "Keyguard"
        lp.packageName = context.packageName
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS

        root.findViewById<Clock>(R.id.status_bar_clock).visibility = View.GONE

        val form = root.findViewById<EditText>(R.id.password_form)

        form.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                unlock()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        form.setOnKeyListener { _, keyCode, _ ->
            if(keyCode == KeyEvent.KEYCODE_ENTER) {
                unlock()
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }

        reload()
    }

    fun reload() {
        val form = root.findViewById<EditText>(R.id.password_form)
        val lockPattern = LockPatternUtils(context)

        when(lockPattern.getCredentialTypeForUser(userId)) {
            LockPatternUtils.CREDENTIAL_TYPE_PASSWORD -> {
                form.visibility = View.VISIBLE
            }
            LockPatternUtils.CREDENTIAL_TYPE_PIN -> {
                form.inputType = EditorInfo.TYPE_CLASS_NUMBER or EditorInfo.TYPE_NUMBER_VARIATION_PASSWORD
                form.visibility = View.VISIBLE
            }
            else -> {
                val form = root.findViewById<Button>(R.id.unlock_noauth)

                form.setOnClickListener {
                    unlock()
                }
                form.visibility = View.VISIBLE
            }
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

    fun onSuccess() {
        (context.applicationContext as SystemUIApplication).runInUIThread {
            Sounds(context).playUnlock()
            hideLockscreen()
        }
    }

    fun unlock() {
        if(!shown) return;
        val form = root.findViewById<EditText>(R.id.password_form)

        thread {
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
                lockPattern.userPresent(userId)
                lockPattern.reportSuccessfulPasswordAttempt(userId)
            } else {
                (context.applicationContext as SystemUIApplication).runInUIThread {
                    showDialog("Authentication failure (invalid password/pin?)")
                }
            }

            cred.zeroize()
        }
        form.text.clear()
    }

    fun showLockscreen(sound: Boolean = false) {
        if(!shown) {
            reload()
            windowManager.addView(root, lp)
            if (sound) Sounds(context).playLock()
            shown = true
            (context.applicationContext as SystemUIApplication).runInUIThread {
                val form = root.findViewById<EditText>(R.id.password_form)
                form.text.clear()
            }

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