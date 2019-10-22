package com.yq.live.rely.keeps.io

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

@Suppress("IMPLICIT_CAST_TO_ANY", "UNCHECKED_CAST")
class BasicType(private val value: String?, val type: String?) : Parcelable, Serializable {
    fun <T> valueOf(): T {
        return when (type) {
            "Double" -> value?.toDouble()
            "Float" -> value?.toFloat()
            "Boolean" -> value?.toBoolean()
            "Int" -> value?.toInt()
            "Long" -> value?.toLong()
            "Short" -> value?.toShort()
            else -> value
        } as T
    }

    constructor(source: Parcel) : this(
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
        writeString(type)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<BasicType> = object : Parcelable.Creator<BasicType> {
            override fun createFromParcel(source: Parcel): BasicType =
                BasicType(source)

            override fun newArray(size: Int): Array<BasicType?> = arrayOfNulls(size)
        }
    }
}