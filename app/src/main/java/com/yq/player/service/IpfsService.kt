package com.yq.player.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.yq.player.ipfs.IpfsListener
import com.yq.player.ipfs.bindIpfs

class IpfsService : Service() {

    private val ipfsCenter by lazy {
        bindIpfs(object : IpfsListener {
            override fun onStarted() {
                sendBroadcast(Intent(IPFS_STARTED))
            }
        })
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onTaskRemoved(rootIntent: Intent) {
        ipfsCenter.destory()
    }

    override fun onCreate() {
        ipfsCenter.start()
    }

    override fun onDestroy() {
        ipfsCenter.destory()
    }

    companion object{
        const val IPFS_STARTED="com.yq.live.IPFS_STARTED"
    }
}