package com.yq.player.player.cover

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.SeekBar
import com.yq.player.R
import com.yq.player.player.DataInter
import com.yq.player.event.OnPlayerEventListener
import com.yq.player.receiver.BaseCover
import com.yq.player.receiver.IReceiverGroup
import com.yq.player.touch.OnTouchGestureListener
import com.yq.player.view.*
import com.yq.player.view.enko.drawable
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.dip
import org.jetbrains.anko.imageView
import org.jetbrains.anko.seekBar
import org.jetbrains.anko.view
import kotlin.math.abs

class GestureCover(context: Context) : BaseCover(context), OnTouchGestureListener {


    private var backgroundView by view<View>()
    private var iconView by view<ImageView>()
    private var seekBar by view<SeekBar>()

    private var firstTouch: Boolean = false

//    private var mWidth: Int = 0
//    private var mHeight: Int = 0

    private val mWidth: Int
        get() = view.width
    private val mHeight: Int
        get() = view.height
    private var newPosition: Long = 0

    private var mHorizontalSlide: Boolean = false
    private var brightness = -1f
    private var volume: Int = 0
    private var audioManager: AudioManager? = null
    private var maxVolume: Int = 0

    private var mGestureEnable = true
    private var horizontalSlide: Boolean = false
    private var rightVerticalSlide: Boolean = false


    private val mOnGroupValueUpdateListener = object : IReceiverGroup.OnGroupValueUpdateListener {
        override fun filterKeys(): Array<String> {
            return arrayOf(DataInter.Key.KEY_COMPLETE_SHOW, DataInter.Key.KEY_IS_LANDSCAPE)
        }

        override fun onValueUpdate(key: String, value: Any) {
            if (DataInter.Key.KEY_COMPLETE_SHOW == key) {
                setGestureEnable(!(value as Boolean))
            }
        }
    }

    private val activity: Activity?
        get() {
            val context = context
            return if (context is Activity) {
                context
            } else null
        }

    override fun onReceiverBind() {
        super.onReceiverBind()
        initAudioManager(context)
    }

    private fun initAudioManager(context: Context) {
        audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager?.getStreamMaxVolume(AudioManager.STREAM_MUSIC) ?: 0
    }


    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        groupValue.registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener)
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        groupValue.unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener)
    }


    fun setGestureEnable(gestureEnable: Boolean) {
        this.mGestureEnable = gestureEnable
    }

    public override fun onCreateCoverView(context: Context): View {
        return context.constraintLayout {
            backgroundView = view {
                visibility = View.INVISIBLE
                background = drawable {
                    cornerRadius = dip(12).toFloat()
                    setColor(0x99000000.toInt())
                }
            }.lparams(dip(168), dip(24)) {
                topOf = parentId
                topMargin = dip(53)
            }

            iconView = imageView {
                visibility = View.INVISIBLE
            }.lparams(dip(16), dip(16)) {
                startOf = backgroundView.id
                leftMargin = dip(22)
            }

            seekBar = seekBar {
                max = 100
                background = null
                progressDrawable = drawable(R.drawable.db_seekbar_process)
                thumb = null
                visibility = View.INVISIBLE
            }.lparams(matchConstraint, dip(3)) {
                startOf = iconView.id
                endToEnd = backgroundView.id
                leftMargin = dip(8)
//                rightMargin = dip(22)
            }

        }
    }

    override fun getCoverLevel(): Int {
        return levelLow(0)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> setGestureEnable(true)
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onSingleTapUp(event: MotionEvent?) {

    }

    override fun onDoubleTap(event: MotionEvent?) {

    }

    override fun onDown(event: MotionEvent?) {
        mHorizontalSlide = false
        firstTouch = true
        volume = getVolume()
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float) {
        if (!mGestureEnable)
            return
        val mOldX = e1.x
        val mOldY = e1.y
        val deltaY = mOldY - e2.y
        val deltaX = mOldX - e2.x
        if (firstTouch) {
            horizontalSlide = abs(distanceX) >= abs(distanceY)
            rightVerticalSlide = mOldX > mWidth * 0.5f
            firstTouch = false
        }

        if (!horizontalSlide) {
            if (abs(deltaY) > mHeight)
                return
            if (rightVerticalSlide) {
                onRightVerticalSlide(deltaY / mHeight)
            } else {
                onLeftVerticalSlide(deltaY / mHeight)
            }
        }
    }

    private fun onRightVerticalSlide(percent: Float) {
        mHorizontalSlide = false
        var index = (percent * maxVolume).toInt() + volume
        if (index > maxVolume)
            index = maxVolume
        else if (index < 0)
            index = 0
        // 变更声音
        audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0)
        // 变更进度条
        seekBar.progress = (index * 1.0 / maxVolume * 100).toInt()
        viewState(true, R.drawable.ic_play_icon_volume)
    }

    private fun onLeftVerticalSlide(percent: Float) {
        mHorizontalSlide = false
        val activity = activity ?: return
        if (brightness < 0) {
            brightness = activity.window.attributes.screenBrightness
            if (brightness <= 0.00f) {
                brightness = 0.50f
            } else if (brightness < 0.01f) {
                brightness = 0.01f
            }
        }
        viewState(true, R.drawable.ic_icon_play_brightness)
        val lpa = activity.window.attributes
        lpa.screenBrightness = brightness + percent
        if (lpa.screenBrightness > 1.0f) {
            lpa.screenBrightness = 1.0f
        } else if (lpa.screenBrightness < 0.01f) {
            lpa.screenBrightness = 0.01f
        }
        seekBar.progress = (lpa.screenBrightness * 100).toInt()
        activity.window.attributes = lpa
    }

    private fun getVolume(): Int {
        volume = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
        if (volume < 0)
            volume = 0
        return volume
    }

    override fun onEndGesture() {
        volume = -1
        brightness = -1f
        if (newPosition >= 0 && mHorizontalSlide) {
            newPosition = 0
        } else {
            groupValue.putBoolean(DataInter.Key.KEY_TIMER_UPDATE_ENABLE, true)
        }
        mHorizontalSlide = false
        viewState(false)
    }

    private fun viewState(show: Boolean, icon: Int = 0) {
        if (icon > 0)
            iconView.setImageResource(icon)
        backgroundView.show = show
        iconView.show = show
        seekBar.show = show
    }
}
