/*
 * Copyright 2017 jiajunhui<junhui_jia@163.com>
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.yq.player.player.cover

import android.content.Context
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.yq.player.R
import com.yq.player.apiHost
import com.yq.player.base.entity.DataSource
import com.yq.player.base.event.EventKey
import com.yq.player.base.event.OnErrorEventListener
import com.yq.player.base.event.OnPlayerEventListener
import com.yq.player.base.receiver.BaseCover
import com.yq.player.player.DataInter
import com.yq.player.rely.*
import com.yq.player.rely.chain.chain
import com.yq.player.rely.chain.end
import com.yq.player.rely.chain.then
import com.yq.player.service.apiService
import com.yq.player.service.body
import com.yq.player.view.*
import org.jetbrains.anko.*
import org.jetbrains.anko.constraint.layout.constraintLayout
import org.jetbrains.anko.constraint.layout.matchConstraint

/**
 * Created by Taurus on 2018/4/20.
 */

class CompleteCover(context: Context) : BaseCover(context) {


    override fun onReceiverBind() {
        super.onReceiverBind()
    }

    override fun onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow()
        if (groupValue.getBoolean(DataInter.Key.KEY_COMPLETE_SHOW)) {
            setPlayCompleteState(true)
        }
    }

    override fun onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow()
        setCoverVisibility(View.GONE)
    }

    override fun onReceiverUnBind() {
        super.onReceiverUnBind()
    }

    private fun setPlayCompleteState(state: Boolean) {
        setCoverVisibility(if (state) View.VISIBLE else View.GONE)
        groupValue.putBoolean(DataInter.Key.KEY_COMPLETE_SHOW, state)
    }


    private var code = ""
    private var icon = ""
    private var status = ""
    override fun onPlayerEvent(eventCode: Int, bundle: Bundle?) {
        when (eventCode) {
            OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET -> {

                (bundle?.getSerializable(EventKey.SERIALIZABLE_DATA) as? DataSource)?.apply {
                    code = extra?.get("code") ?: ""
                    icon = extra?.get("icon") ?: ""
                    status = extra?.get("status") ?: ""
                }
                whenStatusRender(status, false)
            }
            OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START -> setPlayCompleteState(false)
            OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE -> {
                status = "end"
                whenStatusRender(status, false)
            }
        }
    }

    override fun onErrorEvent(eventCode: Int, bundle: Bundle?) {
        if (eventCode == OnErrorEventListener.ERROR_EVENT_IO) {
            chain {
                apiService.live(code).body
            }.then {
                it?.takeIf {
                    it.success
                }?.let {
                    it.data.value
                }
            }.end {
                async(mainCoroutine) {
                    whenStatusRender(it?.status)
                }
            }.onFailure { chain, throwable ->
                e(throwable)
                async(mainCoroutine) {
                    setPlayCompleteState(false)
                }
            }.onFinally {
                it.destory()
            }.call(childCoroutine)
        } else
            setPlayCompleteState(false)
    }

    private fun whenStatusRender(status: String?, replay: Boolean = true) {
        when (status) {
//                            "unstart" -> {
//                                if (icon.isNotEmpty())
//                                    waiting.setImageURI(icon)
//                                setErrorState(true)
//                            }
            "unstart", "start", "paused" -> {
                if (icon.isNotEmpty()) {
                    if (icon.indexOf("http") < 0) {
                        icon = "$apiHost$icon"
                    }
                    waiting.setImageURI(Uri.parse(icon))
                }
                statusText.show = true
                setPlayCompleteState(true)
            }
            "end" -> {
                waiting.setImageURI(context.id2Uri(R.drawable.ic_end_cover))
                statusText.show = false
                setPlayCompleteState(true)
            }
            "broadcast" -> {
                if (replay)
                    requestReplay(null)
                setPlayCompleteState(false)
            }
            else -> {
                setPlayCompleteState(false)
            }
        }
    }

    override fun onReceiverEvent(eventCode: Int, bundle: Bundle?) {
    }

    private var waiting by view<ImageView>()
    private var statusText by view<View>()
    public override fun onCreateCoverView(context: Context): View {
        return context.constraintLayout {
            //            waiting = frescoImage(builder = {
//                placeholderImage = drawable(R.drawable.ic_player_default)
//                placeholderImageScaleType = ScalingUtils.ScaleType.CENTER_CROP
//            }, init = {
//            }).lparams(matchConstraint, matchConstraint) {
//                centerOf = parentId
//            }
            waiting = imageView {

            }.lparams(matchConstraint, matchConstraint) {
                centerOf = parentId
            }
            statusText = textView {
                textSize = 13f
                text = "未开始"
                horizontalPadding = dip(6)
                textColor = color(0xFF999999)
                drawableLeft = context.drawable(R.drawable.db_solid_circle)?.apply {
                    setColorFilter(color(0xFF999999), PorterDuff.Mode.SRC_ATOP)
                }
                background = context.drawable(R.drawable.db_wireframe)?.apply {
                    setColorFilter(color(0xFF999999), PorterDuff.Mode.SRC_ATOP)
                }
                drawablePadding = dip(4)
                show = false
            }.lparams(wrapContent, dip(19)) {
                topEndOf = parentId
                topMargin = dip(16)
                rightMargin = dip(16)
            }

            lparams(matchParent, matchParent)
        }
    }

    override fun getCoverLevel(): Int {
        return levelMedium(20)
    }
}
