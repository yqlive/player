package com.yq.player

import com.google.gson.JsonDeserializer
import com.yq.player.http.Http
import com.yq.player.http.addGsonConverter
import com.yq.player.http.interceptor
import com.yq.player.http.interceptor.FilterLoggingInterceptor
import com.yq.player.ipfs.ipfsHttp
import okhttp3.ResponseBody.Companion.toResponseBody
import java.util.*
import java.util.concurrent.TimeUnit


val apiHost = "https://wsrv.tvc6.cn/"
//val apiHost = "http://111.6.79.35:7001/"

fun ipfsInit() {
    ipfsHttp {
        //        callTimeout(6000, TimeUnit.SECONDS)
//        connectTimeout(6000, TimeUnit.SECONDS)
        readTimeout(10, TimeUnit.MINUTES)
        debug {
            //日志拦截器
            val logging = FilterLoggingInterceptor {
                val url = it.request().url.toString()
                !url.contains("/swarm/peers") && !url.contains("/diag/cmds")
            }
            logging.level = FilterLoggingInterceptor.Level.BASIC
            addInterceptor(logging)
        }
    }
}

/**
 * Http配置
 * 拦截配置、序列化配置等
 */
fun httpInit() {
    Http.init(
        gsonBuilder = {
            registerTypeAdapter(
                Date::class.java,
                JsonDeserializer { json, _, _ -> Date(json.asJsonPrimitive.asLong) })
        },
        httpBuilder = {
            callTimeout(15, TimeUnit.MINUTES)
            connectTimeout(10, TimeUnit.MINUTES)
            readTimeout(10, TimeUnit.MINUTES)
            //网络状态拦截器
            //错误代码拦截器
            interceptor { chain ->
                chain.proceed(chain.request()).let { response ->
                    if (response.code == 413) {//目前仅处理了413错误
                        val mediaType = response.body?.contentType()
                        val responseBody =
                            "{\"success\":false, \"message\"=\"文件太大\",\"code\":413}".toResponseBody(
                                mediaType
                            )
                        response.newBuilder().body(responseBody).build()
                    } else {
                        response
                    }
                }
            }
            // 返回结果code=401 拦截，即登录信息过期或者进行需要已登录的才能进行的操作
        },
        apiBuilder = {
            baseUrl("${apiHost}client/api/v1/")
            addGsonConverter()
        }
    )
}



