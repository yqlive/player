package com.yq.player.player.cover

import android.content.Context
import android.graphics.PorterDuff
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.yq.player.R
import com.yq.player.base.config.PConst
import com.yq.player.base.entity.DataSource
import com.yq.player.base.event.BundlePool
import com.yq.player.base.event.EventKey
import com.yq.player.base.event.OnErrorEventListener
import com.yq.player.base.event.OnPlayerEventListener
import com.yq.player.base.receiver.BaseCover
import com.yq.player.base.utils.NetworkUtils
import com.yq.player.player.DataInter
import com.yq.player.player.LivePlayer
import com.yq.player.rely.asto
import com.yq.player.view.*
import com.yq.player.view.enko.drawableSelector
import com.yq.player.view.enko.selected
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.guideline

/**
 * Created by Taurus on 2018/4/20.
 */

class ErrorCover(context: Context) : BaseCover(context) {

    private val STATUS_ERROR = -1
    private val STATUS_UNDEFINE = 0
    private val STATUS_MOBILE = 1
    private val STATUS_NETWORK_ERROR = 2

    private var mStatus = STATUS_UNDEFINE

    private var mErrorShow: Boolean = false

    private var mCurrPosition: Int = 0

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        handleStatusUI(NetworkUtils.getNetworkState(context))
    }

    private fun handleStatus() {
        val bundle = BundlePool.obtain()
        bundle.putInt(EventKey.INT_DATA, mCurrPosition)
        when (mStatus) {
            STATUS_ERROR -> {
                setErrorState(false)
                requestRetry(bundle)
            }
            STATUS_MOBILE -> {
                LivePlayer.ignoreMobile = true
                setErrorState(false)
                requestResume(bundle)
            }
            STATUS_NETWORK_ERROR -> {
                setErrorState(false)
                requestRetry(bundle)
            }
        }
    }

    override fun onProducerData(key: String, data: Any) {
        super.onProducerData(key, data)
        if (DataInter.Key.KEY_NETWORK_STATE == key) {
            val networkState = data as Int
            if (networkState == PConst.NETWORK_STATE_WIFI && mErrorShow) {
                val bundle = BundlePool.obtain()
                bundle.putInt(EventKey.INT_DATA, mCurrPosition)
                requestRetry(bundle)
            }
            handleStatusUI(networkState)
        }
    }

    private fun handleStatusUI(networkState: Int) {
        if (!groupValue.getBoolean(DataInter.Key.KEY_NETWORK_RESOURCE, true))
            return
        if (networkState < 0) {
            mStatus = STATUS_NETWORK_ERROR
            setErrorInfo("无网络！")
            setHandleInfo("重试", false)
            setErrorState(true)
        } else {
            if (networkState == PConst.NETWORK_STATE_WIFI) {
                if (mErrorShow) {
                    setErrorState(false)
                }
            } else {
                if (LivePlayer.ignoreMobile)
                    return
                mStatus = STATUS_MOBILE
                setErrorInfo("当前正在使用流量播放，是否继续")
                setHandleInfo("继续", true)
                setErrorState(true)
            }
        }
    }

    private fun setErrorInfo(text: String) {
        mInfo.text = text
    }

    private fun setHandleInfo(text: String, selected: Boolean) {
        mRetry.text = text
        mRetry.isSelected = selected
    }

    private fun setErrorState(state: Boolean) {
        mErrorShow = state
        setCoverVisibility(if (state) View.VISIBLE else View.GONE)
        if (!state) {
            mStatus = STATUS_UNDEFINE
        } else {
            notifyReceiverEvent(DataInter.Event.EVENT_CODE_ERROR_SHOW, null)
        }
        groupValue.putBoolean(DataInter.Key.KEY_ERROR_SHOW, state)
    }

    var code = ""
    var icon = ""
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> {
                mCurrPosition = 0
                handleStatusUI(NetworkUtils.getNetworkState(context))
                (bundle?.getSerializable(EventKey.SERIALIZABLE_DATA) as? DataSource)?.apply {
                    code = extra?.get("code") ?: ""
                    icon = extra?.get("icon") ?: ""
                }
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_TIMER_UPDATE -> mCurrPosition = bundle?.getInt(EventKey.INT_ARG1) ?: 0
        }
    }


    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
        mStatus = STATUS_ERROR
        if (eventCode != OnErrorEventListener.ERROR_EVENT_IO)
            onError()
    }

    private fun onError() {
        if (!mErrorShow) {
            setErrorInfo("播放出错啦，刷新试试吧")
            setHandleInfo("重试", false)
            setErrorState(true)
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }


    private var centerLine by view<Guideline>()
    private var mInfo by view<TextView>()
    private var mRetry by view<TextView>()
    //    private var waiting by view<SimpleDraweeView>()
    public override fun onCreateCoverView(context: Context): View {
        return context.constraintLayout {
            show = false
            backgroundColor = 0xCC000000.toInt()
            centerLine = guideline { }.lparams {
                orientation = ConstraintLayout.LayoutParams.VERTICAL
                guidePercent = 0.5f
            }
            mInfo = textView("播放出错啦，刷新试试吧") {
                textSize = 13f
                bottomPadding = dip(15)
                textColor = 0xFFFFFFFF.toInt()
            }.lparams(wrapContent, wrapContent) {
                centerOf = parentId
            }
            mRetry = textView("重试") {
                textSize = 12f
                gravity = Gravity.CENTER
                textColor = 0xFFFFFFFF.toInt()
                background = drawableSelector {
                    selected(com.yq.player.view.enko.drawable {
                        setColorFilter(0xFFF27700.toInt(), PorterDuff.Mode.SRC_IN)
                        cornerRadius = 5f
                    }, false)
                    selected(drawable(R.drawable.db_wireframe)?.asto<GradientDrawable> {
                        setColorFilter(0xFFFFFFFF.toInt(), PorterDuff.Mode.SRC_IN)
                        cornerRadius = 5f
                    }, true)
                }
                onClick {
                    handleStatus()
                }
            }.lparams(dip(56), dip(21)) {
                horizontalCenterOf = mInfo.id
                topToBottom = mInfo.id
            }
            lparams(matchParent, matchParent)
        }
    }

    override fun getCoverLevel(): Int {
        return levelHigh(0)
    }
}
