package eu.hn1f.holoui.volumedialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.Gravity
import android.view.LayoutInflater
import android.view.Window
import android.view.WindowManager.LayoutParams
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import eu.hn1f.holoui.R

private const val MSG_TIMEOUT = 1

class VolumeDialog: Handler {
    private val mContext: Context
    private val mDialogView: FrameLayout
    private val mDialog: Dialog
    private val mMainSlider: LinearLayout
    private val mSeekbar: SeekBar
    private val mSliderIcon: ImageView
    private val mTone: ToneGenerator
    private val mAudioManager: AudioManager

    private val volumeBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            mSeekbar.progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        }
    }

    enum class VolumeType {
        VOLUME_UP,
        VOLUME_DOWN
    }

    @SuppressLint("InflateParams")
    constructor(context: Context): super(Looper.getMainLooper()) {
        mContext = context
        mAudioManager = context.getSystemService(AudioManager::class.java)

        val inflater = LayoutInflater.from(context)
        mDialogView = inflater.inflate(
            R.layout.volume_adjust, null
        ) as FrameLayout

        mDialog = Dialog(mContext, R.style.Theme_VolumePanel)
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog.setTitle("Volume control")
        mDialog.setContentView(mDialogView)
        mTone = ToneGenerator(AudioManager.STREAM_SYSTEM, 100)

        mMainSlider = inflater.inflate(
            R.layout.volume_adjust_item, mDialogView.findViewById<LinearLayout>(R.id.slider_group)
        ) as LinearLayout
        mSeekbar = mMainSlider.findViewById(R.id.seekbar)
        mSliderIcon = mMainSlider.findViewById(R.id.stream_icon)

        mSliderIcon.setImageResource(R.drawable.ic_volume_small)
        mSeekbar.max = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        mSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(ignored: SeekBar, level: Int, fromUser: Boolean) {
                if(fromUser)
                    mAudioManager.setStreamVolume(
                        AudioManager.STREAM_MUSIC, level, 0
                    )

                if(level == 0)
                    mSliderIcon.setImageResource(R.drawable.ic_volume_off_small)
                else
                    mSliderIcon.setImageResource(R.drawable.ic_volume_small)
            }

            override fun onStartTrackingTouch(ignored: SeekBar) {
                removeMessages(MSG_TIMEOUT)
            }

            override fun onStopTrackingTouch(ignored: SeekBar) {
                sendEmptyMessageDelayed(MSG_TIMEOUT, 3000)
            }
        })

        mDialog.window!!.setGravity(Gravity.TOP)
        mDialog.window!!.setDimAmount(0f)
        val lp = mDialog.window!!.attributes
        lp.y = mContext.resources.getDimensionPixelOffset(
            R.dimen.volume_panel_top
        )
        lp.type = LayoutParams.TYPE_VOLUME_OVERLAY
        lp.width = LayoutParams.WRAP_CONTENT
        lp.height = LayoutParams.WRAP_CONTENT
        mDialog.window!!.attributes = lp
        mDialog.window!!.addFlags(
            (LayoutParams.FLAG_NOT_FOCUSABLE or LayoutParams.FLAG_NOT_TOUCH_MODAL
                    or LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        )

        mContext.registerReceiver(
            volumeBroadcastReceiver,
            IntentFilter(AudioManager.VOLUME_CHANGED_ACTION),
            Context.RECEIVER_NOT_EXPORTED
        )
    }

    @Suppress("unused")
    fun onTrigger(type: VolumeType) {
        removeMessages(MSG_TIMEOUT)
        mDialog.show()
        mSeekbar.progress = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        mTone.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
        sendEmptyMessageDelayed(MSG_TIMEOUT, 3000)
    }

    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        when(msg.what) {
            MSG_TIMEOUT -> mDialog.dismiss()
        }
    }
}