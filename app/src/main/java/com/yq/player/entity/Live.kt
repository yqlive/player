package com.yq.player.entity

import android.os.Parcel
import android.os.Parcelable
import java.util.*

data class Live(
    val status: String = "",
    val title: String = "",
    val startAt: Date = Date(0),
    val code: String = "",
    val viewNum: Long = 0,
    val icon: String = "",
    val resolutions: ArrayList<Resolution> = arrayListOf()
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readSerializable() as Date,
        source.readString(),
        source.readLong(),
        source.readString(),
        ArrayList<Resolution>().apply { source.readList(this, Resolution::class.java.classLoader) }
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(status)
        writeString(title)
        writeSerializable(startAt)
        writeString(code)
        writeLong(viewNum)
        writeString(icon)
        writeList(resolutions)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Live> = object : Parcelable.Creator<Live> {
            override fun createFromParcel(source: Parcel): Live = Live(source)
            override fun newArray(size: Int): Array<Live?> = arrayOfNulls(size)
        }
    }
}