package com.yq.player.service

import com.yq.player.entity.Live
import com.yq.player.entity.Tribute
import com.yq.player.http.api
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    /**
     * 获取关注数据
     * @param type:  0-用户 1-节目 2-播单
     * @param count: 指定要返回的列表的数据 默认为10
     */
    @GET("races")
    fun lives(
        @Query("project") project: String? = null,
        @Query("league") league: String? = null,
        @Query("channel") channel: String? = null,
        @Query("status") status: String? = null,
        @Query("origin") origin: String? = null
    ): Call<Tribute<List<Live>>>

    @GET("race")
    fun live(@Query("code") code: String): Call<Tribute<Live>>
}

val apiService by lazy { ApiService::class.api }
val <T> Call<T>.body
    get() = execute().body()