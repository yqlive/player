package com.yq.player.rely

import android.content.Context
import android.os.Parcelable
import com.yq.player.rely.io.CacheObejct
import com.yq.player.rely.io.DiskCacheIO
import com.yq.player.rely.io.DiskDataIO
import com.yq.player.rely.io.LruCacheIO
import com.yq.player.rely.keeps.io.BasicType
import java.io.Serializable
import java.util.*

operator fun <V> Context.set(key: String, value: V): Boolean {
    val typeValue = basicType(value!!) ?: value
    var result = false
    if (typeValue is Parcelable)
        result = ram.put(this, key, typeValue)
    if (typeValue is Serializable)
        result = disk.put(this, key, typeValue)
    return result
}

operator fun <V> Context.set(key: String, shelfLife: Long, value: V): Boolean {
    val typeValue = basicType(value!!) ?: value
    var result = false
    if (typeValue is Parcelable)
        result = ram.put(this, key, typeValue, shelfLife)
    if (typeValue is Serializable)
        result = disk.put(this, key, typeValue, shelfLife)
    return result
}

@Suppress("UNCHECKED_CAST")
operator fun <V> Context.get(key: String): V? {
    var cache: CacheObejct<*>? = ram[this, key]
    if (cache?.value == null || cache.isExpired) {
        cache = disk[this, key]
        if (cache != null && !cache.isExpired && cache.value is Parcelable)
            ram.put(this, key, cache.value as Parcelable)
    }
    val cacheObejct = cache
    if (cacheObejct != null && cacheObejct.value is BasicType)
        return (cacheObejct.value as BasicType).valueOf<V>()
    return cacheObejct?.value as? V?
}

fun Context.clear(): Boolean {
    ram.clear(this)
    return disk.clear(this)
}

fun Context.ram(key: String, value: Parcelable, shelfLife: Long = -1L) = ram.put(this, key, value, shelfLife)

fun Context.ram2(key: String, value: Serializable, shelfLife: Long = -1L) = ram2.put(this, key, value, shelfLife)

fun Context.ram(key: String): CacheObejct<Parcelable>? = ram[this, key]

fun Context.ram2(key: String): CacheObejct<Serializable>? = ram2.get(this, key)

fun Context.ramRemove(key: String) = ram.remove(this, key)

fun Context.disk(key: String, value: Serializable, shelfLife: Long = -1L) =
    DiskCacheIO.putCache(this, key, value, shelfLife)

fun <V : Serializable> Context.disk(key: String) = DiskCacheIO.getCache<V>(this, key)

fun <V : Serializable> Context.data(key: String, value: V) = DiskDataIO.put(this, key, value)

fun <V : Serializable> Context.data(key: String) = DiskDataIO.get<V>(this, key)

fun <V : Serializable> Context.pull(key: String): ArrayList<V> = DiskDataIO.pull(this, key)

fun <V : Serializable> Context.push(key: String, value: V): Boolean = DiskDataIO.push(this, key, value)

//覆盖对应键值arraylist中符合条件的数据，如果找到则覆盖，没有找到则新增
inline fun <V : Serializable> Context.cover(
    key: String,
    value: V,
    predicate: ((V, V) -> Boolean) = { n, o -> n == o }
): Boolean = DiskDataIO.cover(this, key, value, predicate)

fun <V : Serializable> Context.pump(key: String, value: V): Boolean = DiskDataIO.pump(this, key, value)

inline fun <V : Serializable> Context.pump(key: String, predicate: ((V) -> Boolean)) =
    DiskDataIO.pump(this, key, predicate)

//删除所有符合条件的数据
inline fun <V : Serializable> Context.pumps(key: String, predicate: ((V) -> Boolean)) =
    DiskDataIO.pumps(this, key, predicate)

fun Context.dataRemove(key: String) = DiskDataIO.remove(this, key)

val ram by lazy { LruCacheIO.defaultCache }
val ram2 by lazy { LruCacheIO.serializableCache }
val disk by lazy { DiskCacheIO.defaultCache }


private fun basicType(value: Any): BasicType? {
    val type = when (value) {
        is String -> "String"
        is Int -> "Int"
        is Long -> "Long"
        is Boolean -> "Boolean"
        is Double -> "Double"
        is Float -> "Float"
        is Short -> "Short"
        else -> null
    }
    return type?.let { BasicType(value.toString(), it) }
}
