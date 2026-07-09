package eu.hn1f.holoui

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.ActivityTaskManager
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
import android.view.WindowManagerGlobal
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
    val mApplication = context.applicationContext as SystemUIApplication

    init {
        lp.token = token
        lp.gravity = Gravity.TOP
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        lp.title = "Keyguard"
        lp.packageName = context.packageName
        lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_ALWAYS
        lp.windowAnimations = android.R.style.Animation_Activity

        root.findViewById<Clock>(R.id.clock).visibility = View.GONE

        val form = root.findViewById<EditText>(R.id.password_form)

        form.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                unlock()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
        form.setOnKeyListener { _, keyCode, event ->
            if(keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_UP) {
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

        form.visibility = View.GONE

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

        val unlockButton = root.findViewById<Button>(R.id.unlock_noauth)

        unlockButton.setOnClickListener {
            unlock()
        }
        unlockButton.visibility = View.VISIBLE
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
        mApplication.runInUIThread {
            Sounds(context).playUnlock()
            hideLockscreen(true)
        }
    }

    fun unlock() {
        if(!shown) return;
        val form = root.findViewById<EditText>(R.id.password_form)
        val text = form.text.toString()

        thread {
            val lockPattern = LockPatternUtils(context)
            val cred = when(lockPattern.getCredentialTypeForUser(userId)) {
                LockPatternUtils.CREDENTIAL_TYPE_PASSWORD -> {
                    LockscreenCredential.createPassword(text)
                }
                LockPatternUtils.CREDENTIAL_TYPE_PIN -> {
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
                lockPattern.userPresent(userId)
                lockPattern.reportSuccessfulPasswordAttempt(userId)

                onSuccess()
                if(mApplication.stateCallback == null) {
                    Log.v("HoloUI","no statecallback, strange");
                } else {
                    mApplication.stateCallback!!.onTrustedChanged(true)
                }
            } else {
                mApplication.runInUIThread {
                    lockPattern.reportFailedPasswordAttempt(userId)
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
            root.alpha = 1.0f
            try {
                windowManager.addView(root, lp)
            } catch(ignored: IllegalStateException) {
                return;
            }
            if (sound) Sounds(context).playLock()
            shown = true
            mApplication.runInUIThread {
                val form = root.findViewById<EditText>(R.id.password_form)
                form.text.clear()
            }

            if(mApplication.stateCallback == null) {
                Log.v("HoloUI","no statecallback, strange");
            } else {
                mApplication.stateCallback!!.onShowingStateChanged(
                    true,
                    userId
                )
            }

            val taskManager = ActivityTaskManager.getService()
            taskManager.setLockScreenShown(true, false)
        }
    }
    // when unlocked
    fun hideLockscreen(animate: Boolean = false) {
        if(shown) {
            if(animate)
                root.animate()
                    .alpha(0f)
                    .setDuration(200)
                    .withEndAction {
                        windowManager.removeView(root)
                    }
                    .start()
            else
                windowManager.removeView(root)

            shown = false
            if(mApplication.stateCallback == null) {
                Log.v("HoloUI","no statecallback, strange");
            } else {
                mApplication.stateCallback!!.onShowingStateChanged(false, userId)
            }

            val taskManager = ActivityTaskManager.getService()
            taskManager.setLockScreenShown(false, false)
            taskManager.keyguardGoingAway(0)
        }
    }
}