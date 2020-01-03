package com.yq.player.ipfs

object SnIpfs {

    var ipfsWriter: IpfsWriter? = null

    fun init(ipfsWriter: IpfsWriter?) {
        this.ipfsWriter = ipfsWriter
    }
}
