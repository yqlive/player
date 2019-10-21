package com.yq.live.entity

import java.util.*

data class Live(
    val status: String = "",
    val title: String = "",
    val startAt: Date = Date(0),
    val code: String = "",
    val viewNum: Long = 0,
    val icon: String = "",
    val resolutions: ArrayList<Resolution> = arrayListOf()
)