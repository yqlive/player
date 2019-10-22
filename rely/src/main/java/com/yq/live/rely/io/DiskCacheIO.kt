package com.yq.live.rely.io

import android.content.Context
import com.yq.live.rely.millisBetween
import com.yq.live.rely.w
import com.yq.live.rely.cache
import com.yq.live.rely.sizeOf
import java.io.*


open class DiskCacheIO<V : Serializable>(var savePath: String = "cache") : ICacheIO<String, V> {

    /**
     * 判断缓存是否存在
     *
     * @param cachefile
     * @return
     */
    fun exists(context: Context, cachefile: String): Boolean {
        if (cachefile.isNullOrEmpty())
            return false
        val file = File(saveDir(context), cachefile)
        return file.exists()
    }


    override fun get(context: Context, key: String): CacheObejct<V>? {
        val result = readCache(context, key) ?: return null
        if (result.isExpired) {
            remove(context, key)
            return null
        }
        return result
    }

    override fun get(context: Context, key: String, shelfLife: Long): CacheObejct<V>? {
        val cacheObejct = readCache(context, key) ?: return null
        val millis = cacheObejct.cacheTime.millisBetween()
        return if (shelfLife - millis <= 0) {
            null
        } else cacheObejct
    }

    override fun put(context: Context, key: String, value: V): Boolean {
        return saveCache(context, key, CacheObejct(value))
    }

    override fun put(context: Context, key: String, value: V, shelfLife: Long): Boolean {
        return saveCache(context, key, CacheObejct(value, shelfLife))
    }

    override fun remove(context: Context, key: String): Boolean {
        val cacheFile = File(saveDir(context), key)
        return if (cacheFile.exists()) cacheFile.delete() else false
    }

    override fun clear(context: Context): Boolean {
        val files = saveDir(context)?.listFiles()
        if (files != null) {
            val bf = files.size
            var af = 0
            for (file in files) {
                if (file != null && file.exists() && file.delete()) {
                    af++
                }
            }
            return bf == af
        }
        return false
    }

    override fun sortOut(context: Context): Boolean {
        val be = byteSize(context)
        val files = saveDir(context)?.listFiles()
        if (files != null) {
            for (file in files) {
                if (file != null && file.exists()) {
                    val cacheObejct = readCache(file)
                    if (cacheObejct == null || cacheObejct.isExpired || cacheObejct.shelfLife < 0) {
                        file.delete()
                    }
                }
            }
        }
        val af = byteSize(context)
        return be > af
    }

    override fun sortOut(context: Context, shelfLife: Long): Boolean {
        val be = byteSize(context)
        val files = saveDir(context)?.listFiles()
        if (files != null) {
            for (file in files) {
                if (file != null && file.exists()) {
                    val cacheObejct = readCache(file)
                    if (cacheObejct == null || shelfLife - cacheObejct.cacheTime.millisBetween() <= 0) {
                        file.delete()
                    }
                }
            }
        }
        val af = byteSize(context)
        return be > af
    }

    override fun size(context: Context): Int {
        return saveDir(context)?.list()?.size ?: 0
    }

    override fun byteSize(context: Context): Long {
        return saveDir(context).sizeOf
    }

    private fun readCache(context: Context, key: String): CacheObejct<V>? {
        val cacheFile = File(saveDir(context), key)
        return readCache(cacheFile)
    }

    @Suppress("UNCHECKED_CAST")
    private fun readCache(cacheFile: File): CacheObejct<V>? {
        if (!cacheFile.exists())
            return null
        var fis: FileInputStream? = null
        var ois: ObjectInputStream? = null
        try {
            fis = FileInputStream(cacheFile)
            ois = ObjectInputStream(fis)
            val readObject = ois.readObject()
            return readObject as? CacheObejct<V>
        } catch (e: Exception) {
            // 反序列化失败 - 删除缓存文件
            if (e is InvalidClassException) {
                cacheFile.delete()
            }
        } finally {
            try {
                ois!!.close()
                fis!!.close()
            } catch (e: Exception) {
            }
        }
        return null
    }

    private fun saveCache(context: Context, key: String, cache: CacheObejct<V>): Boolean {
        val cacheFile = File(saveDir(context), key)
        var fos: FileOutputStream? = null
        var oos: ObjectOutputStream? = null
        try {
            fos = FileOutputStream(cacheFile)
            oos = ObjectOutputStream(fos)
            oos.writeObject(cache)
            oos.flush()
            return true
        } catch (e: Exception) {
            w(e)
        } finally {
            try {
                oos!!.close()
                fos!!.close()
            } catch (e: Exception) {
            }
        }
        return false
    }

    protected open fun saveDir(context: Context) = context.cache(savePath)

    companion object {

        val defaultCache by lazy { DiskCacheIO<Serializable>() }


        @Suppress("UNCHECKED_CAST")
        fun <V : Serializable> getCache(context: Context, key: String): V? {
            return defaultCache.get(context, key)?.value as? V?
        }

        @Suppress("UNCHECKED_CAST")
        fun <V : Serializable> getCache(context: Context, key: String, deadline: Long): V? {
            return defaultCache.get(context, key, deadline)?.value as? V?
        }

        fun <V : Serializable> putCache(context: Context, key: String, value: V): Boolean {
            return defaultCache.put(context, key, value)
        }

        fun <V : Serializable> putCache(context: Context, key: String, value: V, shelfLife: Long): Boolean {
            return defaultCache.put(context, key, value, shelfLife)
        }

        fun removeCache(context: Context, key: String): Boolean {
            return defaultCache.remove(context, key)
        }
    }

}