package com.yq.live.ipfs

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import retrofit2.http.Path

interface Ipfs {

    @GET("swarm/peers")
    fun peers(): Call<SwarmPeers>

    @GET("config")
    fun config(@Query("arg") key: String, @Query("arg") value: String): Call<String>

    @GET("cat/{hash}")
    fun cat(@Path("hash") hash: String): Call<String>

    @GET("version")
    fun version(): Call<Version>

    @GET("id")
    fun id(): Call<PeerID>

    @GET("ls/{hash}")
    fun ls(@Path("hash") hash: String): Call<LinkObjects>

    @GET("pin/ls")
    fun pinLs(@Query("type") type: String = "recursive"): Call<Map<String, Map<String, Any>>>

    @GET("pin/add/{hash}")
    fun addPin(@Path("hash") hash: String): Call<String>

    @GET("pin/rm/{hash}")
    fun removePin(@Path("hash") hash: String): Call<String>

    @GET("pubsub/pub")
    fun pub(@Query("arg") theme: String, @Query("arg") content: String): Call<String>

    @GET("pubsub/sub/{theme}")
    fun sub(@Path("theme") theme: String): Call<String>

    @GET("stats/bw")
    fun statsBw(): Call<BandWidthInfo>

    @GET("repo/stat")
    fun statStat(): Call<Stat>

    @GET("repo/gc")
    fun statGc(): Call<ResponseBody>

    @GET("diag/cmds")
    fun diagCmds(): Call<ResponseBody>

    @GET("name/publish/{hash}")
    fun namePublish(@Path("hash") hash: String): Call<NameValue>

    @GET("name/resolve/{hash}")
    fun nameResolve(@Path("hash") hash: String): Call<com.yq.live.ipfs.Path>

    @GET("object/stat/{hash}")
    fun objectStat(@Path("hash") hash: String): Call<ObjectStat>

    @Streaming
    @GET("{hash}")
    fun resource(@Header("Range") range: String, @Path("hash") hash: String): Call<ResponseBody>

    @Streaming
    @GET("progress/{hash}")
    fun progress(@Path("hash") hash: String): Call<ResponseBody>

    @GET("iorate/{hash}")
    fun ioRate(@Path("hash") hash: String): Call<IORate>

    @GET("live/close/{code}")
    fun closeLive(@Path("code") code: String, @Query("arg") vp: String? = ""): Call<ResponseBody>

//    @POST("share/on")
//    fun shareOn(): Call<String>
//
//    @POST("share/on")
//    fun shareOff(): Call<String>

}