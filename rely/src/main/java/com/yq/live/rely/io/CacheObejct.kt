package com.yq.live.rely.io

import com.yq.live.rely.currentTime
import com.yq.live.rely.millisBetween
import java.io.Serializable
import java.util.*

class CacheObejct<T> : Serializable {
    val value: T
    val shelfLife: Long
    val cacheTime: Date

    val isExpired: Boolean
        get() {
            val millis = cacheTime.millisBetween()
            return this.shelfLife != -1L && this.shelfLife - millis <= 0L
        }

    constructor(value: T, deadline: Date) : this(value, currentTime.millisBetween(deadline))

    @JvmOverloads
    constructor(value: T, shelfLife: Long = -1L) {
        this.value = value
        this.shelfLife = shelfLife
        this.cacheTime = Date()
    }
}
