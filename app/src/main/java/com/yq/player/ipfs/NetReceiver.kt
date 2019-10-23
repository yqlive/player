package com.yq.player.ipfs

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import com.yq.player.Settings
import com.yq.player.rely.d
import com.yq.player.rely.i

class NetReceiver(private var needRestart: (Boolean) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ConnectivityManager.CONNECTIVITY_ACTION, ACTION_WIFI_SETTING_CHANGED -> {
                val manager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = manager.activeNetworkInfo
                if (activeNetwork != null) { // connected to the internet
                    if (activeNetwork.isConnected) {
                        if (activeNetwork.type == ConnectivityManager.TYPE_WIFI) {
                            Settings["wifi"] = true
                            d("当前WiFi连接可用 ", TAG)
                        } else {
                            d("当前移动网络连接可用 ", TAG)
                            Settings["wifi"] = false
                        }
                        Settings["conn"] = true
                    } else {
                        d("当前没有网络连接，请确保你已经打开网络 ", TAG)
                        Settings["conn"] = false
                    }
                    d("type:${activeNetwork.typeName},state:${activeNetwork.state}")
                } else {   // not connected to the internet
                    d("当前没有网络连接，请确保你已经打开网络 ", TAG)
                    Settings["conn"] = false
                }
//                direct {
//                    if (Settings["conn", false] && !Settings["wifi", false])
//                        ipfs.shareOff()
//                    else
//                        ipfs.shareOn()
//                }.work()
            }
        }
    }

    private fun offlineName(offline: Boolean) = if (offline) "离线" else "在线"

    private fun offline(): Boolean {
        if (!Settings["conn", false])//如果没有联网，则使用离线模式启动ipfs
            return true
        //已联网
        i("是否允许使用流量:${!Settings.wifiOnly}", TAG)
        // wifiOnly改为是否在移动网络提示
//        if (Settings.wifiOnly)//如果是只能在wifi模式下使用
//            return !Settings["wifi", false]//则判断是否当前是wifi模式，如果是，则返回false不以离线模式启动ipfs，如果不是则返回true以离线模式启动ipfs
        return false//以上条件全不满足则说明已联网并且可以在wifi模式以外的网络环境使用，则返回false不以离线模式启动ipfs
    }

    companion object {
        const val ACTION_WIFI_SETTING_CHANGED =
            "com.yq.invitationcat.ipfs.local.action.ACTION_WIFI_SETTING_CHANGED"
        private const val TAG = "WIFI"
    }

}
