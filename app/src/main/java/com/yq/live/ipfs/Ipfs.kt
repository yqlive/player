package com.yq.live.ipfs

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import com.yq.live.Settings
import com.yq.ipfs.Daemon
import com.yq.rely.bindReceiver
import com.yq.rely.d
import com.yq.rely.file
import com.yq.rely.i
import com.yq.rely.tasks.Task
import com.yq.rely.tasks.task
import kotlinx.coroutines.Dispatchers

class IpfsManager(private val _context: Context, val events: ((IpfsEvents) -> Unit)? = null) {

    private var _centerInited = false
    private var _isOffline = false
    private var _isStarting = false
    private var _isSubSucceed = false

    private val daemon by lazy {
        Daemon(_context)
            .root(_context.filesDir)//如果使用外部路径会因为没有权限报错
            .repo(_context.file(".ipfs_repo"))//设置ipfs的仓库路径，建议设置为外部存储器，以减少内部空间的占用
//            .swarmkey(swarmkeyWord)
//            .bootstraps(BOOTSTRAPS)
//            .privateKey(privateKey)
//            .publicKey(publicKey)
//            .apiPort(5019)
            .gatewayPort(ipfsLivePort)
            .swarmPort(4019)
//            .newestVersion(IPFS_VERSION)
            .started {
                _isStarting = false
                d("init is _centerInited:$_centerInited", "IPFS")
                if (!_centerInited) {
                    //派发初始化成功事件
                    d("init dispatch IpfsEvents.INITED", "IPFS")
                    events?.invoke(IpfsEvents.INITED)//该事件只会派发一次
                    _centerInited = true
                }
                events?.invoke(IpfsEvents.STARTED)
                //启动子线程侦听订阅内容
                if (!Settings["offline", false]) {
                }
            }.failed {
                events?.invoke(IpfsEvents.FAILED)
            }
    }

    private var daemonTask: Task<Unit>? = null


    fun start(): Task<Unit> {
        stop()
        _isStarting = true
        _isOffline = offline
        daemonTask = daemon.init().work(Dispatchers.Main)
        return daemonTask!!
    }

    fun stop() {
        if (ipfsWorking || _isStarting) {
            d("Reday stop", "IPFS")
        }
        ipfsWorking = false
        daemonTask?.cancel()
        _isStarting = false
    }

    val offline: Boolean
        get() = _isOffline

    val starting: Boolean
        get() = _isStarting

    fun destory() =
        task {
            _isStarting = false
            _centerInited = false
            _isSubSucceed = false
            daemonTask?.cancel()
            daemonTask = null
        }.finally { destory() }.work()

}

var ipfsWorking = false
val ipfsStarting
    get() = ipfsService?.starting ?: false

var ipfsLivePort = 8080

val LIVE
    get() = "http://127.0.0.1:$ipfsLivePort/live/"


var ipfsService: IpfsManager? = null
private var bindedWifiReceiver = false


fun Context.bindIpfs(listener: IpfsListener? = null): IpfsManager {
    if (ipfsService == null)
        ipfsService =
            IpfsManager(this) { events: IpfsEvents ->
                when (events) {
                    IpfsEvents.INITED -> {
                        i("on IpfsEvents.INITED", "IPFS")
                        if (!bindedWifiReceiver) {//首次绑定IPFSService后才开始监听wifi状态
                            bindReceiver(
                                netReceiver, ConnectivityManager.CONNECTIVITY_ACTION,
                                NetReceiver.ACTION_WIFI_SETTING_CHANGED
                            )
                            bindedWifiReceiver = true
                        }
                        ipfsWorking = true
                        listener?.onInited()
                    }
                    IpfsEvents.STARTED -> {
                        ipfsWorking = true
//                        direct {
//                            if (Settings["conn", false] && !Settings["wifi", false])
//                                ipfs.shareOff()
//                            else
//                                ipfs.shareOn()
//                        }.work()
                        d(
                            "IPFS STARTED starting:${ipfsService?.starting},offline:${ipfsService?.offline}",
                            "IPFS"
                        )
                        listener?.onStarted()
                    }
                    IpfsEvents.FAILED -> {
                        ipfsWorking = false
                        d(
                            "IPFS FAILED starting:${ipfsService?.starting},offline:${ipfsService?.offline}",
                            "IPFS"
                        )
                        listener?.onFailed()
                    }
                    IpfsEvents.NEW_VERSION -> {
                        listener?.onNewVersion()
                    }
                }
            }
    return ipfsService!!
}

private val netReceiver by lazy {
    NetReceiver {
        d("ipfsService is  start  $it", "WIFI")
        ipfsService?.start()
        if (Settings["wifi", false]) {
//            GSYVideoManager.onPause()
        }
    }
}

/**
 * 判断网络是否链接
 *
 * @return`
 */
fun Context.isNetworkAvailable(): Boolean {
    // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
    val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    if (connectivityManager == null) {
        Settings["conn"] = false
        return false
    } else {
        // 获取NetworkInfo对象
        val networkInfo = connectivityManager.allNetworkInfo

        if (networkInfo != null && networkInfo.isNotEmpty()) {
            for (i in networkInfo.indices) {
                // 判断当前网络状态是否为连接状态
                if (networkInfo[i].state == NetworkInfo.State.CONNECTED) {
                    Settings["conn"] = true
                    return true
                }
            }
        }
    }
    Settings["conn"] = false
    return false
}

/**
 * 是否是无线
 *
 * @return
 */
private fun Context.isWifiActive(): Boolean {
    val connectivity = getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    val info: Array<NetworkInfo>?
    if (connectivity != null) {
        info = connectivity.allNetworkInfo
        if (info != null) {
            for (i in info.indices) {
                if (info[i].typeName == "WIFI" && info[i].isConnected) {
                    Settings["wifi"] = true
                    return true
                }
            }
        }
    }
    Settings["wifi"] = false
    return false
}

enum class IpfsEvents {
    INITED, STARTED, FAILED, NEW_VERSION,
}

