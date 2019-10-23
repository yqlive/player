package com.yq.player.ipfs

public interface IpfsListener {
    fun onInited() {}
    fun onStarted() {}
    fun onFailed() {}
    fun onNewVersion() {}
}