package com.yq.player.ipfs

import android.content.Context
import android.os.Build
import com.yq.player.http.direct
import com.yq.player.http.gson
import com.yq.player.rely.d
import com.yq.player.rely.t
import com.yq.player.rely.tasks.Task
import com.yq.player.rely.tasks.task
import com.yq.player.rely.w
import kotlinx.coroutines.*
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.InputStream
import java.io.InterruptedIOException
import java.net.ConnectException
import java.nio.charset.Charset
import java.util.*

class Daemon(private val _context: Context) {
    companion object {
        protected const val TAG = "IPFS-Core"
    }

    private val daemon = IpfsDaemon()
    private var lastTask: Task<Unit>? = null
    private var onDone: (() -> Unit)? = null
    private var onFailed: ((Throwable) -> Unit)? = null

    fun ipfsWriter(ipfsWriter: IpfsWriter): Daemon {
        daemon.ipfsWriter = ipfsWriter
        return this
    }

    fun root(rootDir: File): Daemon {
        if (!rootDir.exists() || !rootDir.isDirectory)
            throw DaemonException("IPFS rootDir must be a directory!")
        daemon.rootDir = rootDir
        return this
    }

    fun repo(repoDir: File): Daemon {
        if (repoDir.exists() && repoDir.isDirectory) {
            daemon.repoDir = repoDir
        }
        return this
    }

    fun apiPort(port: Int): Daemon {
        apiPort = port
        return this
    }

    fun configFiles(vararg config: Pair<String, String>): Daemon {
        configFile.addAll(config)
        return this
    }

    fun swarmPort(port: Int): Daemon {
        swarmPort = port
        return this
    }

    fun gatewayPort(port: Int): Daemon {
        gatewayPort = port
        return this
    }

    fun newestVersion(version: String): Daemon {
        daemon.newstVersion = version
        return this
    }

    fun swarmkey(key: String): Daemon {
        daemon.swarmKey = key
        return this
    }

    fun privateKey(key: String): Daemon {
        daemon.privateKeyStr = key
        return this
    }

    fun publicKey(key: String): Daemon {
        daemon.publicKeyStr = key
        return this
    }

    fun bootstraps(bootstraps: ArrayList<String>): Daemon {
        daemon.bootstraps = bootstraps
        return this
    }

    fun started(block: () -> Unit): Daemon {
        onDone = block
        return this
    }

    fun failed(block: (err: Throwable) -> Unit): Daemon {
        onFailed = block
        return this
    }

    fun init(frontMode: String? = null, behindMode: String? = null): Task<Unit> {
        lastTask?.cancel()
        var process: Process? = null
        lastTask = daemon.task {
            daemon.context = _context
            if (!daemon.initialized) {
                t("$TAG initialized") {
                    daemon.init()
                }
            }
            process = start(frontMode, behindMode) {
                d("daemon exit! for cmd:[$it]", TAG)
            }
            var cmds: String? = null
            while (cmds.isNullOrEmpty()) {
                cmds = try {
                    direct(ipfs.diagCmds())?.string()
                } catch (e: Throwable) {
                    if (e !is ConnectException)
                        w(e, TAG)
                    null
                }
                delay(200)
            }
            if (!cmds.isNullOrEmpty()) {
                d("Daemon is Ready ", TAG)
            }
        }.done {
            d("Daemon will to use ", TAG)
            onDone?.invoke()
        }.failed {
            onFailed?.invoke(it)
        }.cancelled {
            d("daemon task on destroy", TAG)
            d("daemon process ${if (process == null) "is" else "is not"} null", TAG)
            process?.destroy()
            destory()
        }
        return lastTask!!
    }

    private class IpfsDaemon {
        lateinit var rootDir: File
        var repoDir: File? = null
        lateinit var context: Context
        var swarmKey: String? = null
        var bootstraps: ArrayList<String>? = null
        var privateKeyStr: String? = null
        var publicKeyStr: String? = null
        var newstVersion: String? = null
        var ipfsWriter: IpfsWriter? = null

        private val binaryFile by lazy { File(rootDir, "ipfsbin") }
        private val repoPath by lazy { File(rootDir, "ipfs_repo") }
        private val swarmKeyFile by lazy { File(repoPath, "swarm.key") }

        private val keysDir by lazy { File("$repoPath/keys") }
        private val privateKey by lazy { File(keysDir, "private_key.pem") }
        private val publicKey by lazy { File(keysDir, "public_key.pem") }
        private val log by lazy { File(rootDir, "ipfs.log") }


        var initialized = false
        private var daemonProcess: Process? = null

        fun init() {
            initialized = false
            var toUpdate = false
            if (!binaryFile.exists() || checkUpdate().apply {
                    toUpdate = this
                }) {//如果该文件不存在，则表示没有安装 或者已存在，但是需要更新
                try {
                    t("$TAG install files") {
                        install()//开始安装
                    }
                    if (!toUpdate)
                        t("$TAG run init cmd") {
                            run("init")//完成安装后执行初始化命令
                        }

                    if (apiPort > 0 && apiPort != 5001)
                        run("config Addresses.API /ip4/127.0.0.1/tcp/$apiPort")
                    if (gatewayPort > 0 && gatewayPort != 8080)
                        run("config Addresses.Gateway /ip4/127.0.0.1/tcp/$gatewayPort")
                    if (swarmPort > 0 && swarmPort != 4001)
                        run("config --json Addresses.Swarm [\"/ip4/0.0.0.0/tcp/$swarmPort\",\"/ip6/::/tcp/$swarmPort\"]")

                    if (!bootstraps.isNullOrEmpty()) {//如果配置了bootstrap，则移除原有所有节点，逐条加入
                        run("bootstrap rm all")
                        for (bootstrap in bootstraps!!) {
                            run("bootstrap add $bootstrap")
                        }
                    }
                    initialized = true
                } catch (e: Throwable) {
                    w(e)
                    rootDir.delete()//期间如果出现任何异常，则删除所有安装或初始化生成的文件
//                    throw InitializationException(e.message)//同时抛出一个ipfs初始化的异常
                }
            }

            if (!swarmKey.isNullOrEmpty()) {//如果配置了swarmkey
                if (swarmKeyFile.exists()) {//则判断是否有老旧的swarmkey
                    //如果存在老旧的swarkey则读取key文件并判断key值是否一致，如果不一致，则重新写入新key覆盖
                    if (readString(swarmKeyFile) != swarmKey)
                        t("$TAG update swarmkey") {
                            swarmKeyFile.writeText(swarmKey!!)
                        }
                } else //如果不存在则直接写入
                    t("$TAG update swarmkey") {
                        swarmKeyFile.writeText(swarmKey!!)
                    }
            }

            val newestVersion = logStream(run("version --enc json", false).inputStream)
            d("NEWEST IPFS VERSION:$newestVersion", TAG)
        }


        fun start(
            frontMode: String? = null,
            behindMode: String? = null,
            exit: ((String) -> Unit)? = null
        ): Process? {
            if (!initialized)
                init()
            if (daemonProcess != null) {
                daemonProcess?.destroy()
                daemonProcess = null
            }
            val behind = if (!behindMode.isNullOrEmpty()) " $behindMode" else ""
            val front = if (!frontMode.isNullOrEmpty()) " $frontMode " else ""
            val cmd = "${front}daemon$behind"
            daemonProcess = sync(cmd)
            GlobalScope.launch(Dispatchers.Default) {
                if (daemonProcess != null) {
                    daemonProcess?.waitFor()
                    exit?.invoke(cmd)
                }
            }
            return daemonProcess
        }


        private fun install() {
            if (!Build.CPU_ABI.toLowerCase().startsWith("arm"))
                throw  CompatibilityException("Unsupported CPU")
            ipfsWriter?.write(context, binaryFile) ?: run {
                val source = context.assets.open("snipfs").source().buffer()
                val sink = binaryFile.sink().buffer()
                while (!source.exhausted()) {
                    source.read(sink.buffer(), 1024)
                }
                source.close()
                sink.close()
            }
            binaryFile.setExecutable(true)

            if (!repoPath.exists() && !repoPath.mkdirs())
                throw DaemonException("init keys dir failed")
            if (!keysDir.exists() && !keysDir.mkdirs())
                throw DaemonException("init keys dir failed")
            privateKeyStr?.let {
                privateKey.writeText(it)
            }
            publicKeyStr?.let {
                publicKey.writeText(it)
            }

            configFile.forEach {
                if (it.first.isNotEmpty() && it.second.isNotEmpty())
                    File(repoPath, it.first).writeText(it.second)
            }
        }

        private fun checkUpdate(): Boolean {
            d("IPFS NEW VERSION:$newstVersion", TAG)
            if (newstVersion.isNullOrEmpty())
                return false
            val installedVersion =
                logStream(
                    run(
                        "version --enc json",
                        false
                    ).inputStream
                ).takeIf { !it.isNullOrEmpty() }?.let {
                    runCatching {
                        d("IPFS INSTALLED VERSION:$it", TAG)
                        gson.fromJson(it, Version::class.java)
                    }.getOrNull()
                }?.Sn ?: return true
            return SinoVersion.of(newstVersion) > installedVersion
//            return false
        }

        private fun run(cmd: String, output: Boolean = true): Process {
            if (!binaryFile.exists())
                throw DaemonException("IPFS Uninstall")
            val env =
                arrayOf("IPFS_PATH=${repoPath.absoluteFile}", "GOLOG_FILE=${log.absoluteFile}")
            val command = binaryFile.absolutePath + " " + cmd
            val exec = Runtime.getRuntime().exec(command, env)
            val error = logStream(exec.errorStream, cmd, "error", true)
            if (output)
                logStream(exec.inputStream)
            val time = System.currentTimeMillis()
            d("cmd: [$cmd] executing", TAG)
            val code = exec.waitFor()
            d("cmd: [$cmd] finished for time: " + (System.currentTimeMillis() - time), TAG)
            if (code != 0) {
                if (error.indexOf("ipfs configuration file already exists!") < 0)
                    throw DaemonException("IPFS Cmd [$cmd] Exception:\n$error")
            }
            return exec
        }

        private fun sync(cmd: String): Process {
            if (!binaryFile.exists())
                throw DaemonException("IPFS Uninstall")
            val env =
                arrayOf("IPFS_PATH=${repoPath.absoluteFile}", "GOLOG_FILE=${log.absoluteFile}")
            val command = binaryFile.absolutePath + " " + cmd
            val exec = Runtime.getRuntime().exec(command, env)
            d("cmd: [$cmd] executing", TAG)
            GlobalScope.async {
                val error = logStream(exec.errorStream, cmd, "error", true)
                logStream(exec.inputStream)
                if (exec.waitFor() != 0) {
                    throw DaemonException("IPFS Cmd [$cmd] Exception:\n$error")
                }
            }
            return exec
        }

        private fun logStream(
            stream: InputStream,
            cmd: String = "run",
            tag: String = "LOG",
            print: Boolean = false
        ): String {
            d("logStream", TAG)
            val log = try {
                stream.bufferedReader().readText()
            } catch (e: InterruptedIOException) {
                w(e)
                "Process read interrupted"
            } catch (e: Exception) {
                w(e)
                e.message ?: ""
            }

            if (log.isNotEmpty() && print)
                d("cmd: [$cmd] $tag: $log", TAG)
            return log
        }

        private fun readString(file: File): String? {
            val source = file.source().buffer()
            val string = source.readString(Charset.defaultCharset())
            source.close()
            d("read file [${file.absolutePath}]", TAG)
            d(" to String [$string]", TAG)
            return string
        }
    }
}

open class DaemonException(msg: String = "Unknown IPFS Daemon Exception") : RuntimeException(msg)

class CompatibilityException(msg: String = "Unknown Compatibility Exception") : DaemonException(msg)

class InitializationException(msg: String?) :
    DaemonException("Initialization of IPFS failed, maybe it is running or has been initialized\nError message:\n$msg")

