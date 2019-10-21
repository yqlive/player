package com.yq.rely.io

import android.content.Context

interface ICacheIO<T, V> {
    operator fun get(context: Context, key: T): CacheObejct<V>?

    operator fun get(context: Context, key: T, shelfLife: Long): CacheObejct<V>?

    fun put(context: Context, key: T, value: V): Boolean

    fun put(context: Context, key: T, value: V, shelfLife: Long): Boolean

    fun remove(context: Context, key: T): Boolean

    fun clear(context: Context): Boolean

    fun sortOut(context: Context): Boolean

    fun sortOut(context: Context, shelfLife: Long): Boolean

    fun size(context: Context): Int

    fun byteSize(context: Context): Long
}
