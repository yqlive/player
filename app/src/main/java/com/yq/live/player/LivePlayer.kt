package com.yq.live.player

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.yq.player.config.PlayerConfig
import com.yq.player.config.PlayerLibrary
import com.yq.player.entity.DecoderPlan
import com.yq.player.record.PlayRecordManager
import com.yq.rely.Blo
import com.yq.live.*
import com.yq.live.ipfs.ipfsLivePort
import com.yq.live.player.core.exo.ExoMediaPlayer
import com.yq.live.player.core.ijk.IjkPlayer
import com.yq.live.service.IpfsService
import com.yq.live.service.IpfsService.Companion.IPFS_STARTED

object LivePlayer {
    fun init(app: Application, livePort: Int = 8080, onInited: Blo? = null) {
        ipfsLivePort = livePort
        Settings.init(app)
        //-------------------------------------------
        //如果添加了'cn.jiajunhui:exoplayer:xxxx'该依赖
        ExoMediaPlayer.init(app)

        PlayerConfig.addDecoderPlan(DecoderPlan(App.PLAN_ID_EXO, ExoMediaPlayer::class.java.name, "ExoPlayer"))
        PlayerConfig.setDefaultPlanId(App.PLAN_ID_EXO)

        //use default NetworkEventProducer.
        PlayerConfig.setUseDefaultNetworkEventProducer(true)

        PlayRecordManager.setRecordConfig(PlayRecordManager.RecordConfig.Builder().setMaxRecordCount(100).build())

        PlayerLibrary.init(app)
        httpInit()
        ipfsInit()
        frescoInit()
        app.startService(Intent(app, IpfsService::class.java))
        onInited?.let {
            app.registerReceiver(object : BroadcastReceiver() {
                override fun onReceive(context: Context?, intent: Intent?) {
                    if (intent?.action == IPFS_STARTED) {
                        it()
                    }
                }
            }, IntentFilter(IPFS_STARTED))
        }

    }


}