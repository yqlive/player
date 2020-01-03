package com.yq.player.ipfs

object Ipfs {

    var ipfsWriter: IpfsWriter? = null

    fun init(ipfsWriter: IpfsWriter?) {
        this.ipfsWriter = ipfsWriter
    }
}
