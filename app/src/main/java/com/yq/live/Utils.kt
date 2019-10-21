package com.yq.live

import android.app.ActivityManager
import android.content.Context
import com.yq.rely.Blo

val Context.isTopActivity: Boolean
    get() = this.isTopActivity(this.javaClass.name)

fun Context.isTopActivity(activityName: String): Boolean {
    return isForeground(activityName)
}

fun Context.isForeground(className: String): Boolean {
    if (className.isNullOrEmpty()) {
        return false
    }
    val am = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    val list = am.getRunningTasks(1)
    if (list != null && list.size > 0) {
        val cpn = list[0].topActivity
        if (className == cpn.className) {
            return true
        }
    }
    return false
}

fun debug(blo: Blo) {
    if (BuildConfig.DEBUG) {
        blo()
    }
}