package com.yq.player.entity

import android.os.Parcel
import android.os.Parcelable

data class League(val _id: String, val name: String, val shortName: String) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(_id)
        writeString(name)
        writeString(shortName)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<League> = object : Parcelable.Creator<League> {
            override fun createFromParcel(source: Parcel): League = League(source)
            override fun newArray(size: Int): Array<League?> = arrayOfNulls(size)
        }
    }
}