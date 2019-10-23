package com.yq.player.entity

open class Argument(val success: Boolean = true, val code: Int = 0, val message: String = "") {
    override fun toString(): String {
        return "Argument{success=$success,code=$code,message=$message}"
    }
}