package com.yq.live.ipfs

import com.yq.live.http.addGsonConverter
import com.yq.live.rely.Sub
import okhttp3.OkHttpClient
import retrofit2.Retrofit

var apiPort: Int = 5110
var gatewayPort: Int = 8080
var swarmPort: Int = 4001

val ROOT
    get() = "http://127.0.0.1:$gatewayPort/ipfs/"

val String.ofIpfs
    get() = "$ROOT$this"

val String.ofIpfsVideo
    get() = "$ROOT$this?type=video"

private var _ipfsHttpBuilder: Sub<OkHttpClient.Builder>? = null

fun ipfsHttp(blo: Sub<OkHttpClient.Builder>) {
    _ipfsHttpBuilder = blo
}

val ipfs: Ipfs by lazy {
    Retrofit.Builder().apply {
        client(OkHttpClient.Builder().apply {
            _ipfsHttpBuilder?.let { it() }
        }.build())
        baseUrl("http://127.0.0.1:$apiPort/api/v0/")
        addGsonConverter()
    }.build().create(Ipfs::class.java)
}

