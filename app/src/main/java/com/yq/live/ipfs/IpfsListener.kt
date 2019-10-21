package com.yq.live.ipfs

public interface IpfsListener {
    fun onInited() {}
    fun onStarted() {}
    fun onFailed() {}
    fun onNewVersion() {}
}