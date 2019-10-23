@file:Suppress("UNCHECKED_CAST")

package com.yq.player

import android.app.Application
import com.yq.player.rely.data
import com.yq.player.rely.dataRemove
import java.io.Serializable

/**
 * 系统配置中心
 * 配置中心所保存的对象必须实现序列化<Serializable>接口
 * 且保存后不会被自动清除
 */
object Settings {
    lateinit var app: Application

    fun init(application: Application) {
        this.app = application
    }

    /**
     * 参数不能使用double 或 date
     */
    operator fun <T> get(key: String): T? {
        return app.data<Serializable>("setting-$key") as T?
    }

    operator fun <T> get(key: String, defValue: T): T {
        return get(key) ?: defValue.apply { set(key, this) }
    }

    operator fun <T> set(key: String, value: T) {
        app.data("setting-$key", value as Serializable)
    }

    fun remove(key: String) = app.dataRemove("setting-$key")

    var wifiOnly: Boolean
        get() = Settings["wifiOnly", true]
        set(value) {
            // true移动网络下需提示，false则直接播放
            // 不在设置为离线模式
//            app.sendBroadcast(Intent(NetReceiver.ACTION_WIFI_SETTING_CHANGED))
            Settings["wifiOnly"] = value
        }

    var isFirst: Boolean
        get() = Settings["isFirst", true]
        set(value) {
            Settings["isFirst"] = value
        }

    // 唯一设备好
    val serialNumber: String = android.os.Build.SERIAL ?: "unknow"
    //{客户端系统}/{版本}
    val userAgent: String = "Android/${BuildConfig.VERSION_CODE}"

}

infix fun <V : Serializable> String.set(that: V) {
    Settings[this] = that
}

infix fun <V : Serializable?> String.get(def: V?): V? {
    return if (def == null) {
        Settings.get<Serializable>(this) as? V
    } else {
        Settings.get<Serializable>(this, def) as? V
    }
}

fun <V : Serializable?> String.get(): V? {
    return Settings.get<Serializable>(this) as? V
}


