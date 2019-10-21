package com.yq.rely.io

import android.content.Context
import com.yq.rely.file
import java.io.File
import java.io.Serializable

object DiskDataIO {

    @Suppress("UNCHECKED_CAST")
    fun <V : Serializable> get(context: Context, key: String): V? {
        return io[context, key]?.value as? V?
    }

    fun <V : Serializable> put(context: Context, key: String, value: V): Boolean {
        return io.put(context, key, value)
    }

    //获取对应键值arraylist
    fun <V : Serializable> pull(context: Context, key: String): ArrayList<V> {
        return get(context, key) ?: arrayListOf()
    }

    //向对应键值的arraylist增加一条数据
    fun <V : Serializable> push(context: Context, key: String, value: V): Boolean {
        pull<V>(context, key).let {
            it.add(value)
            return io.put(context, key, it)
        }
    }

//    fun <V : Serializable> cover(activity: Context, key: String, value: V): Boolean {
//        return if (pull<V>(activity, key).indexOf(value) < 0) push(activity, key, value) else true
//    }

    //覆盖对应键值arraylist中符合条件的数据，如果找到则覆盖，没有找到则新增
    inline fun <V : Serializable> cover(
        context: Context,
        key: String,
        value: V,
        predicate: ((V, V) -> Boolean) = { n, o ->
            n == o
        }
    ): Boolean {
        val list = pull<V>(context, key)
        val find = list.find { predicate(value, it) }
        return if (find != null) {
            list[list.indexOf(find)] = value
            put(context, key, list)
        } else {
            push(context, key, value)
        }
    }


    fun <V : Serializable> pump(context: Context, key: String, value: V): Boolean {
        return pump<V>(context, key) { o ->
            o == value
        }
    }

    inline fun <V : Serializable> pump(context: Context, key: String, predicate: ((V) -> Boolean)): Boolean {
        val list = pull<V>(context, key)
        list.find { predicate(it) }?.let {
            if (list.remove(it))
                return@pump io.put(context, key, list)
        }
        return false
    }

    //删除所有符合条件的数据
    inline fun <V : Serializable> pumps(context: Context, key: String, predicate: ((V) -> Boolean)): Boolean {
        val list = pull<V>(context, key)
        list.filter { predicate(it) }.let {
            if (list.removeAll(it))
                return io.put(context, key, list)
        }
        return false
    }


    fun remove(context: Context, key: String): Boolean {
        return io.remove(context, key)
    }

    fun clear(context: Context): Boolean {
        return io.clear(context)
    }

    val io
        get() = dataIO

    private val dataIO by lazy {
        object : DiskCacheIO<Serializable>("filedata") {
            override fun saveDir(context: Context): File? {
                return context.file(savePath)
            }
        }
    }

    const val ARTICLE_PUB_TASK = "articlePubTask"
    const val DOWNLOAD_TASK = "downloadTasks"
    const val ARTICLE_HISTORY = "articleHistory"
    const val FIRST_PLAY_RECORD = "firstPlayRecord"
    const val UPDATE_VERSION = "updateVersion"
}