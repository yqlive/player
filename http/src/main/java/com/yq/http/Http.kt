@file:Suppress("ObjectPropertyName")

package com.yq.http

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.yq.http.interceptor.TimeoutInterceptor
import com.yq.rely.Sub
import com.yq.rely.TIME_OF_FULL_DEFAULT
import com.yq.rely.tasks.Task
import com.yq.rely.tasks.call
import com.yq.rely.tasks.task
import com.yq.rely.w
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.reflect.KClass


object Http {
    internal lateinit var _client: OkHttpClient
    internal lateinit var _api: Retrofit
    internal lateinit var _gson: Gson
    private var inited = false
    fun init(
        gsonBuilder: Sub<GsonBuilder>? = null,
        httpBuilder: Sub<OkHttpClient.Builder>? = null,
        apiBuilder: Sub<Retrofit.Builder>? = null
    ) {
        if (inited)
            throw RuntimeException("Http can not be init again!")
        _gson = GsonBuilder().apply {
            setDateFormat(TIME_OF_FULL_DEFAULT)
            gsonBuilder?.let { it() }
        }.create()

        _client = OkHttpClient.Builder().apply {
            addInterceptor(TimeoutInterceptor())
            httpBuilder?.let {
                it()
            }
        }.build()

        _api = Retrofit.Builder().apply {
            client(_client)
            apiBuilder?.let { it() }
        }.build()
        inited = true
    }
}

inline fun OkHttpClient.Builder.interceptor(crossinline block: (chain: Interceptor.Chain) -> okhttp3.Response) =
    addInterceptor(object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response = block(chain)
    })

fun Retrofit.Builder.addGsonConverter() {
    this.addConverterFactory(GsonConverterFactory.create(Http._gson))
}

@Suppress("UPPER_BOUND_VIOLATED")
val <T> KClass<T>.api: T
    @JvmName("getJavaClass")
    get() = com.yq.http.api.create(this.java)


/**
 * 同步请求
 */
fun <R> direct(block: Call<R>): R? = run {
    val url = block.request().url.toString()
    try {
        block.execute().body()
    } catch (e: Throwable) {
        if (!url.contains("127.0.0.1") && e !is ConnectException)
            w(e, "Direct Error")
        null
    }
}


/**
 * 同步请求
 */
inline fun <T, R> T.direct(crossinline block: T.() -> Call<R>) =
    task {
        direct(block()) ?: throw  EmptyResponseException()
    }.finally { destory() }

/**
 * 异步请求
 */
inline fun <T, R> T.tactful(
    dispatcher: CoroutineDispatcher = Dispatchers.Default,
    crossinline block: T.() -> Call<R>
): Task<R> =
    call<R>(dispatcher) {
        block().enqueue(object : retrofit2.Callback<R> {
            override fun onResponse(caller: Call<R>, response: Response<R>) {
                val result = response.body()
                if (result != null)
                    it.resume(result)
                else
                    it.resumeWithException(EmptyResponseException())
                if (!caller.isCanceled)
                    caller.cancel()
            }

            override fun onFailure(caller: Call<R>, t: Throwable) {
                if (!caller.isCanceled)
                    caller.cancel()
                it.resumeWithException(t)
            }
        })
    }.finally { destory() }

val client by lazy { Http._client }

val api by lazy { Http._api }

val gson by lazy { Http._gson }

class EmptyResponseException : RuntimeException("is empty")