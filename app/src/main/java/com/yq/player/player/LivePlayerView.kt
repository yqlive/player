package com.yq.player.player

import android.annotation.SuppressLint
import android.app.Activity
import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.SupportActivity
import android.util.AttributeSet
import android.view.ViewGroup
import com.yq.player.base.assist.InterEvent
import com.yq.player.base.assist.OnVideoViewEventHandler
import com.yq.player.base.entity.DataSource
import com.yq.player.base.player.IPlayer
import com.yq.player.base.receiver.ReceiverGroup
import com.yq.player.base.widget.BaseVideoView
import com.yq.player.entity.Live
import com.yq.player.ipfs.LIVE
import com.yq.player.ipfs.ipfs
import com.yq.player.isTopActivity
import com.yq.player.player.cover.*
import com.yq.player.rely.chain.chain
import com.yq.player.rely.chain.end
import com.yq.player.rely.chain.then
import com.yq.player.rely.childCoroutine
import com.yq.player.rely.d
import com.yq.player.rely.screenWidth
import com.yq.player.service.apiService
import kotlin.collections.set

@SuppressLint("RestrictedApi")
class LivePlayerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : BaseVideoView(context, attrs, defStyleAttr) {

    private val mReceiverGroup: ReceiverGroup by lazy {
        ReceiverGroup(null).apply {
            //        addReceiver(KEY_CLOSE_COVER, CloseCover(this@receiverGroup))
            addReceiver(DataInter.ReceiverKey.KEY_LOADING_COVER, LoadingCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_CONTROLLER_COVER, ControllerCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, GestureCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_COMPLETE_COVER, CompleteCover(context))
            addReceiver(DataInter.ReceiverKey.KEY_ERROR_COVER, ErrorCover(context))
        }
    }

    private var userPause: Boolean = false
    private var isLandscape: Boolean = false

    private val onVideoViewEventHandler = object : OnVideoViewEventHandler() {
        override fun onAssistHandle(assist: BaseVideoView, eventCode: Int, bundle: Bundle?) {
            super.onAssistHandle(assist, eventCode, bundle)
            when (eventCode) {
                InterEvent.CODE_REQUEST_PAUSE -> userPause = true
                DataInter.Event.EVENT_CODE_REQUEST_BACK ->
                    if (context is Activity) {
                        if (isLandscape) {
                            context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        } else {
                            context.finish()
                        }
                    }
                DataInter.Event.EVENT_CODE_REQUEST_TOGGLE_SCREEN -> {
                    if (context is Activity) {
                        context.requestedOrientation =
                            if (isLandscape) ActivityInfo.SCREEN_ORIENTATION_PORTRAIT else ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    }
                }
                DataInter.Event.EVENT_CODE_ERROR_SHOW -> stop()
                DataInter.Event.EVENT_CODE_CHANGE_RELATION -> {
                    d("$bundle", "EVENT_CODE_CHANGE_RELATION")
                    bundle?.getString("resolution")?.let {
                        if (it != resolution) {
                            resolution = it
                            start()
                        }
                    }
                }
            }
        }

        override fun requestRetry(videoView: BaseVideoView, bundle: Bundle?) {
            if (context.isTopActivity) {
                super.requestRetry(videoView, bundle)
            }
        }
    }

    var onScreenChangeListener: ((Boolean) -> Unit)? = null

    private val sw by lazy { context.screenWidth }
    private var _liveCode = ""
    private var _resolution = ""
    private val Live?.live
        get() = "$LIVE${this?.code}/$_resolution.m3u8"
    var live: Live? = null
        set(value) {
            field?.let {
                if (it.code != value?.code)
                    closeIO()
            }
            field = value
            value?.let {
                _liveCode = it.code
            }
        }
    var resolution
        set(value) {
            if (value != _resolution)
                closeIO(_resolution)
            _resolution = value
            live?.let {
                setDataSource(DataSource(it.live).apply {
                    title = it.title
                    extra = hashMapOf()
                    extra["code"] = it.code
                    extra["status"] = it.status
                    extra["icon"] = it.icon
                    extra["viewNum"] = it.viewNum.toString()
                    var resolutions = ""
                    it.resolutions.forEach { re ->
                        resolutions += re.value + ","
                    }
                    extra["resolutions"] = resolutions
                    extra["resolution"] = value
                })
            }
        }
        get() = _resolution

    @JvmOverloads
    fun loadLive(liveCode: String, autoPlay: Boolean = false, loaded: ((Boolean) -> Unit)? = null) {
        if (_liveCode != liveCode) {
            _liveCode = liveCode
            chain {
                apiService.live(_liveCode).execute().body()
            }.then {
                it?.takeIf { it.success }?.data?.value
            }.end {
                live = it
                it?.let {
                    resolution = it.resolutions.takeIf { it.isNotEmpty() }?.first()?.value
                        ?: "1080p"
                }
                if (autoPlay)
                    start()
                "success" put it != null
            }.onFinally {
                it.destory()
                loaded?.invoke("success" take false)
            }.call(childCoroutine)
        } else {
            if (autoPlay)
                start()
            loaded?.invoke(false)
        }
    }

    private val closeIO by lazy {
        chain {
            ipfs.closeLive("liveCode" take _liveCode, "resolution" take _resolution)
        }.then {
            it.execute().body()
        }.end {
            it?.string()
        }.alias {
            "CHAIN-CloseIO"
        }
    }

    init {
        mReceiverGroup.groupValue.putBoolean(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true)
        setReceiverGroup(mReceiverGroup)
        setEventHandler(onVideoViewEventHandler)
        if (context is SupportActivity) {
            context.lifecycle.addObserver(object : GenericLifecycleObserver {
                override fun onStateChanged(source: LifecycleOwner?, event: Lifecycle.Event?) {
                    when (event) {
                        Lifecycle.Event.ON_PAUSE -> {
                            if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
                                return
                            if (isInPlaybackState) {
                                pause()
                            } else {
                                stop()
                            }
                        }
                        Lifecycle.Event.ON_RESUME -> {
                            if (state == IPlayer.STATE_PLAYBACK_COMPLETE)
                                return
                            if (isInPlaybackState) {
                                if (!userPause)
                                    resume()
                            } else {
                                rePlay(0)
                            }
                        }
                        Lifecycle.Event.ON_DESTROY -> {
                            stopPlayback()
                        }
                    }
                }
            })
        }
    }


    override fun onConfigurationChanged(newConfig: Configuration?) {
        super.onConfigurationChanged(newConfig)
        isLandscape = newConfig?.orientation == Configuration.ORIENTATION_LANDSCAPE
        updateVideo(isLandscape)
        mReceiverGroup.groupValue.putBoolean(DataInter.Key.KEY_IS_LANDSCAPE, isLandscape)
    }


    private fun updateVideo(landscape: Boolean) {
        val lp = this.layoutParams
        if (landscape) {
            lp.width = ViewGroup.LayoutParams.MATCH_PARENT
            lp.height = ViewGroup.LayoutParams.MATCH_PARENT
        } else {
            lp.width = sw
            lp.height = lp.width * 9 / 16
        }
        this.layoutParams = lp
        onScreenChangeListener?.invoke(isLandscape)
    }

    override fun stop() {
        super.stop()
        closeIO()
    }

    override fun stopPlayback() {
        closeIO()
        super.stopPlayback()
    }

    private fun closeIO(resolution: String? = null) {
        if (_liveCode.isNotEmpty())
            closeIO.call(childCoroutine, "liveCode" to _liveCode, "resolution" to resolution)
        d(closeIO, "CHAIN")
    }


//    override fun onBackPressed() {
//        if (isLandscape) {
//            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
//            return
//        }
//        super.onBackPressed()
//    }


}