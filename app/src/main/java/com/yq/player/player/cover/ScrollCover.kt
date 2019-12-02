package com.yq.player.player.cover

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import com.yq.player.base.event.OnPlayerEventListener
import com.yq.player.base.receiver.BaseCover
import com.yq.player.base.receiver.IReceiverGroup
import com.yq.player.base.touch.OnTouchGestureListener
import com.yq.player.player.DataInter
import org.jetbrains.anko.constraint.layout.constraintLayout

class ScrollCover(context: Context, private val onScrollBlo: (e1: MotionEvent, event: MotionEvent, distanceX: Float, distanceY: Float) -> Unit) : BaseCover(context), OnTouchGestureListener {


    private var mGestureEnable = true


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
        return context.constraintLayout { }
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
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float) {
        if (!mGestureEnable)
            return
        onScrollBlo(e1, e2, distanceX, distanceY)
    }


    override fun onEndGesture() {

    }


}
