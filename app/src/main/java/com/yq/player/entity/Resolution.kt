package com.yq.player.entity

import android.os.Parcel
import android.os.Parcelable

data class Resolution(val value: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(value)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Resolution> = object : Parcelable.Creator<Resolution> {
            override fun createFromParcel(source: Parcel): Resolution = Resolution(source)
            override fun newArray(size: Int): Array<Resolution?> = arrayOfNulls(size)
        }
    }
}