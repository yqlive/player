package com.yq.player.player.cover

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.text.TextUtils
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.*
import com.yq.player.R
import com.yq.player.base.entity.DataSource
import com.yq.player.base.event.EventKey
import com.yq.player.base.event.OnPlayerEventListener
import com.yq.player.base.log.PLog
import com.yq.player.base.player.IPlayer
import com.yq.player.base.receiver.BaseCover
import com.yq.player.base.receiver.IReceiverGroup
import com.yq.player.base.touch.OnTouchGestureListener
import com.yq.player.player.DataInter
import com.yq.player.view.*
import com.yq.player.view.anko.flexboxLayout
import com.yq.player.view.component.EdgeTransparentView
import com.yq.player.view.enko.colorSelector
import com.yq.player.view.enko.drawableSelector
import com.yq.player.view.enko.selected
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.custom.customView

/**
 * Created by Taurus on 2018/4/15.
 */

class ControllerCover(context: Context) : BaseCover(context), OnTouchGestureListener,
    View.OnClickListener {

    private val MSG_CODE_DELAY_HIDDEN_CONTROLLER = 101
    private var topContainer: View  by view()
    private var whiteTop: View  by view()
    private var blackTop: View  by view()
    private var bottomContainer: View  by view()
    private var backIcon: ImageView by view(this)

    private var topTitle: TextView by view()
    private var viewNum: TextView by view()
    private var statusText: TextView by view()

    private var whiteTopTitle: TextView by view()
    private var whiteViewNum: TextView by view()
    private var whiteStatusText: TextView by view()

    private var stateIcon: ImageView by view(this)
    private var screenSwitch: ImageView by view(this)

    private var shareButton by view<View>()
    private var relationButton by view<TextView>(this)
    private var relationList by view<FlexboxLayout>()

    private val checkedColor by lazy { color(0xFFF27700) }
    private val uncheckColor by lazy { color(0xFF999999) }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MSG_CODE_DELAY_HIDDEN_CONTROLLER -> {
                    PLog.d(tag.toString(), "msg_delay_hidden...")
                    setControllerState(false)
                }
            }
        }
    }

    private var mGestureEnable = true

    private var mControllerTopEnable: Boolean = false
    private var mBottomAnimator: ObjectAnimator? = null
    private var mTopAnimator: ObjectAnimator? = null

    private val mOnGroupValueUpdateListener by lazy {
        object : IReceiverGroup.OnGroupValueUpdateListener {
            override fun filterKeys(): Array<String> {
                return arrayOf(
                    DataInter.Key.KEY_COMPLETE_SHOW,
                    DataInter.Key.KEY_TIMER_UPDATE_ENABLE,
                    DataInter.Key.KEY_DATA_SOURCE,
                    DataInter.Key.KEY_IS_LANDSCAPE,
                    DataInter.Key.KEY_CONTROLLER_TOP_ENABLE
                )
            }

            override fun onValueUpdate(key: String, value: Any) {
                when (key) {
                    DataInter.Key.KEY_COMPLETE_SHOW -> {
                        val show = value as Boolean
                        if (show) {
                            setControllerState(false)
                        }
                        setGestureEnable(!show)
                    }
                    DataInter.Key.KEY_CONTROLLER_TOP_ENABLE -> {
                        mControllerTopEnable = value as Boolean
                        if (!mControllerTopEnable) {
                            setTopContainerState(false)
                        }
                    }
                    DataInter.Key.KEY_IS_LANDSCAPE -> setSwitchScreenIcon(value as Boolean)
                    DataInter.Key.KEY_DATA_SOURCE -> dataSourceChange(value as DataSource)
//                    DataInter.Key.KEY_TIMER_UPDATE_ENABLE -> {  }
                }
            }
        }
    }

    private val isControllerShow: Boolean
        get() = bottomContainer.visibility == View.VISIBLE

    override fun onReceiverBind() {
        super.onReceiverBind()
        groupValue.registerOnGroupValueUpdateListener(mOnGroupValueUpdateListener)

    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        dataSourceChange(groupValue.get<DataSource>(DataInter.Key.KEY_DATA_SOURCE))

        mControllerTopEnable = true
//        setTopContainerState(false)
        val screenSwitchEnable = groupValue.getBoolean(DataInter.Key.KEY_CONTROLLER_SCREEN_SWITCH_ENABLE, true)
        setScreenSwitchEnable(screenSwitchEnable)
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        topContainer.visibility = View.GONE
        bottomContainer.visibility = View.GONE
        removeDelayHiddenMessage()
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()

        cancelTopAnimation()
        cancelBottomAnimation()

        groupValue.unregisterOnGroupValueUpdateListener(mOnGroupValueUpdateListener)
        removeDelayHiddenMessage()
    }

    private fun dataSourceChange(dataSource: DataSource?) {
        if (dataSource != null) {
            statusText.isSelected = dataSource.extra["status"] == "broadcast"
            statusText.text = if (statusText.isSelected) "直播中" else "未开始"
            whiteStatusText.isSelected = statusText.isSelected
            whiteStatusText.text = statusText.text

            viewNum.text = dataSource.extra["viewNum"]
            whiteViewNum.text = viewNum.text

            relationList.removeAllViews()
            val resolution = dataSource.extra["resolution"] ?: "1080p"
            relationButton.text = resolution
            dataSource.extra["resolutions"]?.split(",")?.forEach { itemResolution ->
                relationList.apply {
                    if (itemResolution.isNotEmpty())
                        textView(itemResolution) {
                            textSize = 13f
                            text = itemResolution
                            gravity = Gravity.CENTER
                            setTextColor(colorSelector {
                                selected(0xFFF27700.toInt(), true)
                                selected(0xFFFFFFFF.toInt(), false)
                            })
                            isSelected = itemResolution == resolution
                            verticalPadding = dip(7)
                            layoutParams = ViewGroup.LayoutParams(matchParent, wrapContent)
                            onClick {
                                relationList.forEach<TextView> {
                                    it.isSelected = false
                                }
                                notifyReceiverEvent(DataInter.Event.EVENT_CODE_CHANGE_RELATION, Bundle().apply {
                                    putString("resolution", itemResolution)
                                })
                                isSelected = true
                            }
                        }
                }
            }

            val title = dataSource.title?.takeIf { !it.isNullOrEmpty() } ?: dataSource.data
            topTitle.text = title
            whiteTopTitle.text = title

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            backIcon.id -> notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_BACK, null)
            stateIcon.id -> {
                val selected = stateIcon.isSelected
                if (selected) {
                    requestResume(null)
                } else {
                    requestPause(null)
                }
                stateIcon.isSelected = !selected
            }
            screenSwitch.id -> notifyReceiverEvent(DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN, null)
            relationButton.id -> relationList.show = !relationList.show
        }
    }


    private fun setSwitchScreenIcon(isFullScreen: Boolean) {
        blackTop.show = isFullScreen
        whiteTop.show = !isFullScreen
        screenSwitch.setImageResource(if (isFullScreen) R.drawable.ic_play_icon_unfullscreen else R.drawable.ic_play_icon_fullscreen)
    }

    private fun setScreenSwitchEnable(screenSwitchEnable: Boolean) {
        screenSwitch.visibility = if (screenSwitchEnable) View.VISIBLE else View.GONE
    }

    private fun setGestureEnable(gestureEnable: Boolean) {
        this.mGestureEnable = gestureEnable
    }

    private fun cancelTopAnimation() {
        mTopAnimator?.apply {
            cancel()
            removeAllListeners()
            removeAllUpdateListeners()
        }
    }

    private fun setTopContainerState(state: Boolean) {
        if (mControllerTopEnable) {
            topContainer.clearAnimation()
            cancelTopAnimation()
            ObjectAnimator.ofFloat(topContainer, "alpha", if (state) 0f else 1f, if (state) 1f else 0f)
                .apply {
                    duration = 300
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator) {
                            super.onAnimationStart(animation)
                            if (state) {
                                topContainer.visibility = View.VISIBLE
                            }
                        }

                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            if (!state) {
                                topContainer.visibility = View.GONE
                            }
                        }
                    })
                    mTopAnimator = this
                    start()
                }
        } else {
            topContainer.visibility = View.GONE
        }
    }

    private fun cancelBottomAnimation() {
        mBottomAnimator?.apply {
            cancel()
            removeAllListeners()
            removeAllUpdateListeners()
        }
    }

    private fun setBottomContainerState(state: Boolean) {
        bottomContainer.clearAnimation()
        if (!state)
            relationList.show = false
        cancelBottomAnimation()
        ObjectAnimator.ofFloat(bottomContainer, "alpha", if (state) 0f else 1f, if (state) 1f else 0f)
            .apply {
                duration = 300
                addListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator) {
                        super.onAnimationStart(animation)
                        if (state) {
                            bottomContainer.visibility = View.VISIBLE
                        }
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        if (!state) {
                            bottomContainer.visibility = View.GONE
                        }
                    }
                })
                mBottomAnimator = this
                start()
            }
    }

    private fun setControllerState(state: Boolean) {
        if (state) {
            sendDelayHiddenMessage()
        } else {
            removeDelayHiddenMessage()
        }
        setTopContainerState(state)
        setBottomContainerState(state)
    }

    private fun toggleController() {
        if (isControllerShow) {
            setControllerState(false)
        } else {
            setControllerState(true)
        }
    }

    private fun sendDelayHiddenMessage() {
        removeDelayHiddenMessage()
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_CONTROLLER, 5000)
    }

    private fun removeDelayHiddenMessage() {
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_CONTROLLER)
    }


    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> {
                val data = bundle?.getSerializable(EventKey.SERIALIZABLE_DATA) as DataSource
                groupValue.putObject(DataInter.Key.KEY_DATA_SOURCE, data)
                dataSourceChange(data)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_STATUS_CHANGE -> {
                val status = bundle?.getInt(EventKey.INT_DATA)
                if (status == IPlayer.STATE_PAUSED) {
                    stateIcon.isSelected = true
                } else if (status == IPlayer.STATE_STARTED) {
                    stateIcon.isSelected = false
                }
            }
//            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START, OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE ->
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }

    override fun onPrivateEvent(eventCode: Int, bundle: Bundle?): Bundle? {
        when (eventCode) {
            DataInter.PrivateEvent.EVENT_CODE_UPDATE_SEEK -> if (bundle != null) {
            }
        }
        return null
    }

    private val solidCircle by lazy {
        drawableSelector {
            selected(context.drawable(R.drawable.db_solid_circle)?.apply {
                setColorFilter(checkedColor, PorterDuff.Mode.SRC_ATOP)
            }, true)
            selected(context.drawable(R.drawable.db_solid_circle)?.apply {
                setColorFilter(uncheckColor, PorterDuff.Mode.SRC_ATOP)
            }, false)
        }
    }

    private val wireframe by lazy {
        drawableSelector {
            selected(context.drawable(R.drawable.db_wireframe)?.apply {
                setColorFilter(checkedColor, PorterDuff.Mode.SRC_ATOP)
            }, true)
            selected(context.drawable(R.drawable.db_wireframe)?.apply {
                setColorFilter(uncheckColor, PorterDuff.Mode.SRC_ATOP)
            }, false)
        }
    }

    public override fun onCreateCoverView(context: Context): View {
        return context.constraintLayout {
            topContainer = verticalLayout {
                blackTop = linearLayout {
                    show = false
                    backgroundColor = 0xAA000000.toInt()
                    gravity = Gravity.CENTER_VERTICAL
                    backIcon = imageView(R.drawable.ic_player_back) {
                        horizontalPadding = dip(8)
                        scaleType = centerInside
                    }.lparams(wrapContent, matchParent)
                    topTitle = textView {
                        textColor = 0xFFFFFFFF.toInt()
                        textSize = 18f
                        lines = 1
                        leftPadding = dip(15)
                        ellipsize = TextUtils.TruncateAt.END
                    }.lparams(0, wrapContent) {
                        weight = 1f
                    }
                    viewNum = textView {
                        textColor = 0xFFFFFFFF.toInt()
                        textSize = 13f
                        horizontalPadding = dip(10)
                        drawableBounds(R.drawable.ic_icon_people_home, DrawableMode.LEFT) {
                            setBounds(0, 0, dip(15), dip(15))
                        }
                        gravity = Gravity.CENTER_VERTICAL
                        drawablePadding = dip(4)
                    }.lparams(wrapContent, wrapContent)
                    statusText = textView {
                        textSize = 13f
                        text = "未开始"
                        horizontalPadding = dip(6)
                        setTextColor(colorSelector {
                            selected(checkedColor, true)
                            selected(uncheckColor, false)
                        })
                        drawableLeft = solidCircle
                        background = wireframe
                        drawablePadding = dip(4)
                    }.lparams(wrapContent, dip(19)) {
                        rightMargin = dip(16)
                    }
                }.lparams(matchParent, dip(43))
                whiteTop = linearLayout {
                    backgroundColor = 0xCCFFFFFF.toInt()
                    gravity = Gravity.CENTER_VERTICAL
                    horizontalPadding = dip(15)
                    whiteTopTitle = textView {
                        textColor = 0xFF333333.toInt()
                        textSize = 15f
                        lines = 1
                        ellipsize = TextUtils.TruncateAt.END
                    }.lparams(0, wrapContent) {
                        weight = 1f
                    }
                    whiteViewNum = textView {
                        textColor = 0xFF666666.toInt()
                        textSize = 13f
                        horizontalPadding = dip(10)
                        drawableBounds(R.drawable.ic_icon_people, DrawableMode.LEFT) {
                            setBounds(0, 0, dip(13), dip(13))
                        }
                        gravity = Gravity.CENTER_VERTICAL
                        drawablePadding = dip(4)
                    }.lparams(wrapContent, wrapContent)
                    whiteStatusText = textView {
                        textSize = 13f
                        text = "未开始"
                        horizontalPadding = dip(6)
                        setTextColor(colorSelector {
                            selected(checkedColor, true)
                            selected(uncheckColor, false)
                        })
                        isClickable = false
                        isFocusable = false
                        drawableLeft = solidCircle
                        background = wireframe
                        drawablePadding = dip(4)
                    }.lparams(wrapContent, dip(19))
                }.lparams(matchParent, dip(33))
            }.lparams(matchConstraint, wrapContent) {
                topOf = parentId
            }
            bottomContainer = constraintLayout {
                customView<EdgeTransparentView> {
                    position = EdgeTransparentView.TOP
                    drawSize = dip(30).toFloat()
                    view {
                        backgroundColor = 0xAA000000.toInt()
                        layoutParams = FrameLayout.LayoutParams(matchParent, matchParent)
                    }
                }.lparams(matchConstraint, matchConstraint) {
                    centerOf = parentId
                }
                stateIcon = imageView {
                    padding = dip(6)
                    scaleType = centerInside
                    setImageDrawable(drawableSelector {
                        selected(drawable(R.drawable.ic_play_icon_play), true)
                        selected(drawable(R.drawable.ic_play_icon_pause), false)
                    })
                }.lparams(dip(31), matchConstraint) {
                    startOf = parentId
                    leftMargin = dip(7)
                }

                screenSwitch = imageView(R.drawable.ic_play_icon_fullscreen) {
                    scaleType = centerInside
                    padding = dip(6)
                }.lparams(dip(31), matchConstraint) {
                    rightMargin = dip(4)
                    endOf = parentId
                }

                shareButton = imageView(R.drawable.ic_icon_share) {
                    padding = dip(6)
                }.lparams(dip(31), dip(31)) {
                    verticalCenterOf = parentId
                    endToStart = screenSwitch.id
                    rightMargin = dip(4)
                }

                relationButton = textView {
                    horizontalPadding = dip(6)
                    textSize = 12f
                    gravity = Gravity.CENTER
                    textColor = 0xFFFFFFFF.toInt()
                }.lparams(wrapContent, matchConstraint) {
                    verticalCenterOf = parentId
                    endToStart = shareButton.id
                    rightMargin = dip(4)
                }
            }.lparams(matchConstraint, dip(31)) {
                bottomOf = parentId
            }

            relationList = flexboxLayout {
                justifyContent = JustifyContent.CENTER
                alignItems = AlignItems.CENTER
                flexWrap = FlexWrap.WRAP
                show = false
                flexDirection = FlexDirection.COLUMN
                backgroundColor = 0xAA000000.toInt()
            }.lparams(matchConstraint, matchConstraint) {
                endToEnd = parentId
                topToBottom = topContainer.id
                bottomToBottom = parentId
                dimensionRatio = "253:400"
            }

        }
    }

    override fun getCoverLevel(): Int {
        return levelLow(1)
    }

    override fun onSingleTapUp(event: MotionEvent) {
        if (!mGestureEnable)
            return
        toggleController()
    }

    override fun onDoubleTap(event: MotionEvent) {}

    override fun onDown(event: MotionEvent) {}

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float) {
        if (!mGestureEnable)
            return
    }

    override fun onEndGesture() {}
}
