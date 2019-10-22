package com.yq.live.rely

import android.annotation.SuppressLint
import android.arch.lifecycle.GenericLifecycleObserver
import android.arch.lifecycle.Lifecycle
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.util.DisplayMetrics
import android.view.Display
import android.view.KeyCharacterMap
import android.view.KeyEvent
import android.view.WindowManager


/**
 * 注册一个receiver 并会在ondestory时注销receiver
 * 如果该action没有被注册则调用blo创建对应的receiver进行注册
 * 否则不再注册
 * @param action
 * @param blo
 */
fun Context.bindReceiver(action: String, blo: (Intent?) -> Unit) {
    val filter = IntentFilter()
    val resolveInfos = packageManager.queryBroadcastReceivers(Intent(action), 0)
    if (resolveInfos.isNullOrEmpty()) {
        filter.addAction(action)
        bindReceiver(NormalBroadcastReceiver { _, i ->
            blo(i)
        }, filter)
    }
}

class NormalBroadcastReceiver(private val blo: (context: Context?, intent: Intent?) -> Unit) : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        blo(context, intent)
    }
}


/**
 * 注册一个receiver 并会在ondestory时注销receiver
 */
fun Context.bindReceiver(receiver: BroadcastReceiver, vararg actions: String) {
    val filter = IntentFilter()
    actions.forEach {
        //如果该action已经注册过则不再注册
//        val resolveInfos = packageManager.queryBroadcastReceivers(Intent(it), 0)
//        if (resolveInfos.isNullOrEmpty()) {
        filter.addAction(it)
//        }
    }
    bindReceiver(receiver, filter)
}


fun Context.bindReceiver(receiver: BroadcastReceiver, filter: IntentFilter) {

    when (this) {
        is FragmentActivity -> {
            registerReceiver(receiver, filter)
            lifecycle.addObserver(GenericLifecycleObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        unregisterReceiver(receiver)
                    }
                    else -> {
                    }
                }
            })
        }
        is Fragment -> {
            context!!.registerReceiver(receiver, filter)
            lifecycle.addObserver(GenericLifecycleObserver { _, event ->
                when (event) {
                    Lifecycle.Event.ON_DESTROY -> {
                        unregisterReceiver(receiver)
                    }
                    else -> {
                    }
                }
            })
        }
        else -> {
            registerReceiver(receiver, filter)
        }
    }

}

val Context.screenWidth get() = ScreenSize[0]
val Context.screenHeight get() = ScreenSize[1]
val Context.stateBarHeight
    get() = resources.getIdentifier("status_bar_height", "dimen", "android").let {
        if (it > 0) resources.getDimensionPixelSize(it) else Math.ceil((20 * this.resources.displayMetrics.density).toDouble()).toInt()
    }
val Context.navigationBarSize: Int
    get() {
        val hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK)
        val hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME)
        val hasMenuKey = !(hasBackKey && hasHomeKey)
        //判断是否有虚拟按钮
        return if (hasMenuKey) {
            val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
            //获取NavigationBar的高度
            val height = resources.getDimensionPixelSize(resourceId);
            height
        } else {
            0
        }
    }

val Context.ScreenSize: IntArray
    @SuppressLint("ObsoleteSdkInt")
    get() {
        val size = IntArray(2)

        val w = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val d = w.defaultDisplay
        val metrics = DisplayMetrics()
        d.getMetrics(metrics)
        // since SDK_INT = 1;
        var widthPixels = metrics.widthPixels
        var heightPixels = metrics.heightPixels

        // includes window decorations (statusbar bar/menu bar)
        if (Build.VERSION.SDK_INT in 14..16)
            try {
                widthPixels = Display::class.java.getMethod("getRawWidth").invoke(d) as Int
                heightPixels = Display::class.java.getMethod("getRawHeight").invoke(d) as Int
            } catch (ignored: Exception) {
            }

        if (Build.VERSION.SDK_INT >= 17)
            try {
                val realSize = Point()
                Display::class.java.getMethod("getRealSize", Point::class.java).invoke(d, realSize)
                widthPixels = realSize.x
                heightPixels = realSize.y
            } catch (ignored: Exception) {
            }

        size[0] = widthPixels
        size[1] = heightPixels
        return size
    }


fun Context.id2Uri(resourceId: Int): Uri {
    return Uri.parse("android.resource://$packageName/$resourceId")
}

val Context.networkType: NetworkType
    get() {
        val activeNetwork = (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).activeNetworkInfo
            ?: return NetworkType.NONE
        return if (activeNetwork.isConnected) {
            if (activeNetwork.type == ConnectivityManager.TYPE_WIFI)
                NetworkType.WIFI
            else
                NetworkType.MOBILE
        } else NetworkType.NONE
    }

enum class NetworkType {
    MOBILE, WIFI, NONE
}