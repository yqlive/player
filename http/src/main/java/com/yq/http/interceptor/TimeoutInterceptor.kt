package com.yq.http.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class TimeoutInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        var connectTimeout = chain.connectTimeoutMillis()
        var readTimeout = chain.readTimeoutMillis()
        var writeTimeout = chain.writeTimeoutMillis()

        val connectNew = request.header(CONNECT_TIMEOUT)
        val readNew = request.header(READ_TIMEOUT)
        val writeNew = request.header(WRITE_TIMEOUT)

        if (!connectNew.isNullOrEmpty()) {
            connectTimeout = Integer.valueOf(connectNew)
        }
        if (!readNew.isNullOrEmpty()) {
            readTimeout = Integer.valueOf(readNew)
        }
        if (!writeNew.isNullOrEmpty()) {
            writeTimeout = Integer.valueOf(writeNew)
        }

        val builder = request.newBuilder()
        builder.removeHeader(CONNECT_TIMEOUT)
        builder.removeHeader(READ_TIMEOUT)
        builder.removeHeader(WRITE_TIMEOUT)

        return chain
            .withConnectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
            .withReadTimeout(readTimeout, TimeUnit.MILLISECONDS)
            .withWriteTimeout(writeTimeout, TimeUnit.MILLISECONDS)
            .proceed(builder.build())
    }

    companion object {
        const val CONNECT_TIMEOUT = "CONNECT_TIMEOUT"
        const val READ_TIMEOUT = "READ_TIMEOUT"
        const val WRITE_TIMEOUT = "WRITE_TIMEOUT"
    }
}