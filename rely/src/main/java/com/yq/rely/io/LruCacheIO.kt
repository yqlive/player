package com.yq.rely.io

import android.content.Context
import android.graphics.Bitmap
import android.os.Parcelable
import android.util.LruCache
import com.yq.rely.millisBetween
import com.yq.rely.toByteArray
import java.io.Serializable

class LruCacheIO<T, V> : ICacheIO<T, V> {

    interface ISizeOf {
        val sizeOf: Int
    }

    private val lruCache by lazy {
        object : LruCache<T, CacheObejct<V>?>(Runtime.getRuntime().maxMemory().toInt() / 8) {
            override fun sizeOf(key: T, value: CacheObejct<V>?): Int {
                if (value?.value == null)
                    return 0
                if (value.value is ISizeOf)
                    return (value.value as ISizeOf).sizeOf
                if (value.value is Bitmap)
                    return (value.value as Bitmap).byteCount
                val byteArray = value.value.toByteArray() ?: return 0
                return byteArray.size
            }
        }
    }

    override fun get(context: Context, key: T): CacheObejct<V>? {
        val result = this.lruCache.get(key)
        return when {
            result == null -> null
            result.isExpired -> {
                this.lruCache.remove(key)
                null
            }
            else -> result
        }
    }

    override fun get(context: Context, key: T, shelfLife: Long): CacheObejct<V>? {
        val result = this.lruCache.get(key)
        return if (result == null) {
            null
        } else {
            val millis = result.cacheTime.millisBetween()
            if (shelfLife - millis <= 0L) null else result
        }
    }

    override fun put(context: Context, key: T, value: V): Boolean {
        this.lruCache.put(key, CacheObejct(value))
        return true
    }

    override fun put(context: Context, key: T, value: V, shelfLife: Long): Boolean {
        this.lruCache.put(key, CacheObejct(value, shelfLife))
        return true
    }

    override fun remove(context: Context, key: T): Boolean {
        this.lruCache.remove(key)
        return true
    }

    override fun clear(context: Context): Boolean {
        lruCache.evictAll()
        return true
    }

    override fun sortOut(context: Context): Boolean {
        val be = this.byteSize(context)
        val snapshot = lruCache.snapshot()
        val objects = snapshot.keys
        objects.forEach { key ->
            lruCache.get(key)?.takeIf { it.isExpired }?.let { lruCache.remove(key) }
        }
        snapshot.clear()
        val af = this.byteSize(context)
        return be > af
    }

    override fun sortOut(context: Context, shelfLife: Long): Boolean {
        val be = this.byteSize(context)
        val snapshot = lruCache.snapshot()
        val objects = snapshot.keys
        objects.forEach {
            val value = lruCache.get(it)
            if (value != null) {
                val millis = value.cacheTime.millisBetween()
                if (shelfLife - millis <= 0L) {
                    this.lruCache.remove(it)
                }
            }
        }
        snapshot.clear()
        val af = this.byteSize(context)
        return be > af
    }

    override fun size(context: Context): Int {
        val snapshot = lruCache.snapshot()
        val size = snapshot.size
        snapshot.clear()
        return size
    }

    override fun byteSize(context: Context): Long {
        return lruCache.size().toLong()
    }

    companion object {
        val defaultCache by lazy { LruCacheIO<String, Parcelable>() }
        val serializableCache by lazy { LruCacheIO<String, Serializable>() }
    }
}