package com.yq.player.entity

/**
 * 接口响应的统一对象
 * 所有接口无论是否合法调用都会返回该对象
 */
class Tribute<D>(success: Boolean = true, code: Int = 0, message: String = "", data: SingleData<D>) :

    Conclusion<SingleData<D>>(success, code, message, data) {
    override fun toString(): String {
        return "Tribute{success=$success,data=$data,code=$code,message=$message}"
    }
}