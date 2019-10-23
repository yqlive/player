package com.yq.player.player.cover

import android.content.Context
import android.os.Bundle
import android.view.View
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.yq.player.event.OnPlayerEventListener
import com.yq.player.player.IPlayer
import com.yq.player.receiver.BaseCover
import com.yq.player.receiver.PlayerStateGetter
import com.yq.player.view.centerOf
import com.yq.player.view.parentId
import com.yq.player.view.view
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint
import org.jetbrains.anko.custom.customView

/**
 * Created by Taurus on 2018/4/15.
 */

class LoadingCover(context: Context) : BaseCover(context) {
    private var animationView: LottieAnimationView by view()

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        val playerStateGetter = playerStateGetter
        if (playerStateGetter != null && isInPlaybackState(playerStateGetter)) {
            setLoadingState(playerStateGetter.isBuffering)
        }
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        animationView.pauseAnimation()
    }


    private fun isInPlaybackState(playerStateGetter: PlayerStateGetter): Boolean {
        val state = playerStateGetter.state
        return (state != IPlayer.STATE_END
                && state != IPlayer.STATE_ERROR
                && state != IPlayer.STATE_IDLE
                && state != IPlayer.STATE_INITIALIZED
                && state != IPlayer.STATE_STOPPED)
    }

    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START, OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET, OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START, OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO -> setLoadingState(
                true
            )

            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START, OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END, OnPlayerEventListener.PLAYER_EVENT_ON_STOP, OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_ERROR, OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_COMPLETE -> setLoadingState(
                false
            )
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
        setLoadingState(false)
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {

    }

    private fun setLoadingState(show: Boolean) {
        setCoverVisibility(if (show) View.VISIBLE else View.GONE)
        if (show)
            animationView.resumeAnimation()
        else
            animationView.pauseAnimation()
    }

    public override fun onCreateCoverView(context: Context): View {
        return context.constraintLayout {
            animationView = customView<LottieAnimationView> {
                setAnimation("player_loading.json")
                imageAssetsFolder = "images/"
                repeatCount = LottieDrawable.INFINITE
                repeatMode = LottieDrawable.RESTART
            }.lparams(matchConstraint, matchConstraint) {
                centerOf = parentId
                matchConstraintPercentWidth = 0.22f
                matchConstraintPercentHeight = 0.3f
            }
        }
    }

    override fun getCoverLevel(): Int {
        return levelMedium(1)
    }
}
