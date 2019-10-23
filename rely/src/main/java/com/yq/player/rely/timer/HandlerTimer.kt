package com.yq.player.rely.timer

import android.content.Context
import android.os.Handler
import android.os.Message

//val handler = object : Handler(){
//    override fun handleMessage(msg: Message){
//        msg.what==1
//    }
//}
//handler.sendEmptyMessage(1)

class HandlerTimer(
    private val what: Int,
    private val period: Long = 0,
    private val delay: Long = 0,
    private val todo: () -> Unit
) : Handler() {
    private var work = false

    override fun handleMessage(msg: Message) {
        if (msg.what == what && work) {
            todo()
            sendEmptyMessageDelayed(what, period)
        }
    }

    fun start() {
        if (!work) {
            work = true
            sendEmptyMessageDelayed(what, delay)
        }
    }

    fun stop() {
        work = false
    }
}

fun Context.handlerTimer(period: Long = 0, delay: Long = 0, todo: () -> Unit) =
    HandlerTimer(hashCode(), period, delay, todo)