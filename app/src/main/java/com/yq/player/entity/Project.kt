package com.yq.player.entity

import android.os.Parcel
import android.os.Parcelable

data class Project(val img: String, val name: String, val ranking: Int, val _id: String) :
    Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(img)
        writeString(name)
        writeInt(ranking)
        writeString(_id)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Project> = object : Parcelable.Creator<Project> {
            override fun createFromParcel(source: Parcel): Project = Project(source)
            override fun newArray(size: Int): Array<Project?> = arrayOfNulls(size)
        }
    }
}