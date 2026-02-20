package eu.hn1f.holoui

import android.app.ITransientNotificationCallback
import android.content.Context
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.TextView

class Toaster(mContext: Context) {
    val windowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    var currentToast: LinearLayout? = null
    var currentDuration = 0
    var currentCallback: ITransientNotificationCallback? = null
    val inflater = LayoutInflater.from(mContext)

    fun showToast(token: IBinder, message: String, duration: Int, callback: ITransientNotificationCallback?) {
        if(currentToast != null) {
            hideToastASAP()
        }

        currentDuration = duration
        currentCallback = callback
        currentToast = inflater.inflate(R.layout.toast, null) as LinearLayout?;
        currentToast!!.findViewById<TextView>(R.id.toast_text).text = message;
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_TOAST,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        params.title = "Toast"
        params.isFitInsetsIgnoringVisibility = true
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.token = token

        windowManager.addView(currentToast, params)
        currentCallback?.onToastShown()
    }

    fun hideToast() {
        if(currentToast != null) {
            currentToast!!.animate()
                .alpha(0f)
                .setDuration(currentDuration.toLong())
                .withEndAction {
                    hideToastASAP()
                }
                .start()
        }
    }

    fun hideToastASAP() {
        windowManager.removeViewImmediate(currentToast);
        currentToast = null
        currentCallback?.onToastHidden()
    }
}