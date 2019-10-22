package com.yq.live.rely

import android.util.Log

//import com.tencent.bugly.crashreport.BuglyLog
//import com.tencent.bugly.crashreport.CrashReport

fun <T : Any> T.v(msg: Any, tag: String? = this::class.java.simpleName) {
    log("v", tag, msg)
}

fun v(msg: Any, tag: String? = null) {
    log("v", tag, msg)
}

fun <T : Any> T.i(msg: Any, tag: String? = this::class.java.simpleName) {
    log("i", tag, msg)
}

fun i(msg: Any, tag: String? = null) {
    log("i", tag, msg)
}

fun <T : Any> T.d(msg: Any, tag: String? = this::class.java.simpleName) {
    log("d", tag, msg)
}

fun d(msg: Any, tag: String? = null) {
    log("d", tag, msg)
}

fun <T : Any> T.w(msg: Any, tag: String? = this::class.java.simpleName) {
    log("w", tag, msg)
}

fun w(msg: Any, tag: String? = null) {
    log("w", tag, msg)
}

fun <T : Any> T.e(msg: Any, tag: String? = this::class.java.simpleName) {
    log("e", tag, msg)
}

fun e(msg: Any, tag: String? = null) {
    log("e", tag, msg)
}

inline fun <T : Any, R> T.t(tag: String? = this::class.java.simpleName, block: (time: (String) -> Long) -> R): R {
    val ccu = System.currentTimeMillis()
    var cu = ccu
    val time: (String) -> Long = {
        (System.currentTimeMillis() - cu).apply {
            d("$it , use time: $this", tag)
            cu = System.currentTimeMillis()
        }
    }
    d("with time start ----------", tag)
    val re = block(time)
    d("all use time: ${System.currentTimeMillis() - ccu}", tag)
    d("with time end ------------", tag)
    return re
}

fun log(type: String, tag: String?, msg: Any?) {
    if (BuildConfig.DEBUG || msg is Throwable) {
        println()
        val any = when (msg) {
            null -> "null"
            is Array<*> -> if (msg.isEmpty()) "size-0" else StringBuffer(":\n").apply {
                msg.forEach {
                    append("\t$it\n")
                }
            }
            is Iterable<*> -> if (msg.iterator().hasNext()) StringBuffer(":\n").apply {
                msg.forEach {
                    append("\t$it\n")
                }
            } else "size-0"
            is Throwable -> StringBuffer(":\n$msg").apply {
                msg.stackTrace.forEach {
                    append("\tat $it\n")
                }
//                if (type == "e" || type == "w")
//                    CrashReport.postCatchedException(msg)
            }
            else -> msg
        }
        when (type) {
            "v" -> Log.v(tag, any.toString())
            "i" -> Log.i(tag, any.toString())
            "d" -> Log.d(tag, any.toString())
            "w" -> Log.w(tag, any.toString())
            "e" -> Log.e(tag, any.toString())
            else -> Log.i(tag, any.toString())
        }
//        when (type) {
//            "v" -> BuglyLog.v(tag, any.toString())
//            "i" -> BuglyLog.i(tag, any.toString())
//            "d" -> BuglyLog.d(tag, any.toString())
//            "w" -> BuglyLog.w(tag, any.toString())
//            "e" -> BuglyLog.e(tag, any.toString())
//            else -> BuglyLog.i(tag, any.toString())
//        }
    }
}

fun Throwable.d() {
    d(this, null)
}

fun Throwable.w() {
    w(this, null)
}

fun Throwable.e() {
    e(this, null)
}