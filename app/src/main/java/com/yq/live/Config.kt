package com.yq.live

import android.app.Application
import com.airbnb.lottie.L.debug
import com.facebook.cache.disk.DiskCacheConfig
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.imagepipeline.core.ImagePipelineConfig
import com.google.gson.JsonDeserializer
import com.yq.http.Http
import com.yq.http.addGsonConverter
import com.yq.http.interceptor
import com.yq.http.interceptor.FilterLoggingInterceptor
import com.yq.ipfs.ipfsHttp
import com.yq.rely.MB
import com.yq.rely.cache
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit


/***************************** OTHER ***************************/
private const val MAX_DISK_CACHE_VERYLOW_SIZE = 20 * MB
private const val MAX_DISK_CACHE_LOW_SIZE = 60 * MB
private const val MAX_DISK_CACHE_SIZE = 100 * MB

private const val IMAGE_PIPELINE_CACHE_DIR = "ImagePipeLine"

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

/**
 * fresco 图片管理库初始化
 * 配置默认最大尺寸和最大缓存尺寸等
 */
fun Application.frescoInit() {
    val builder = ImagePipelineConfig.newBuilder(this).setDownsampleEnabled(true)
    val cache = cache("header")?.absolutePath
    if (cache.isNullOrEmpty()) {
        throw IllegalStateException("the cache dir is null.")
    } else {
        val diskCacheConfig = DiskCacheConfig.newBuilder(this).setBaseDirectoryPath(File(cache))
            .setBaseDirectoryName(IMAGE_PIPELINE_CACHE_DIR).setMaxCacheSize(
                MAX_DISK_CACHE_SIZE
            ).setMaxCacheSizeOnLowDiskSpace(MAX_DISK_CACHE_LOW_SIZE)
            .setMaxCacheSizeOnVeryLowDiskSpace(
                MAX_DISK_CACHE_VERYLOW_SIZE
            ).build()
        builder.setMainDiskCacheConfig(diskCacheConfig)
    }
    val config = builder.build()
    Fresco.initialize(this, config)
}



