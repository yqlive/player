package com.yq.live.entity

/**
 * 接口响应的统一对象
 * 所有接口无论是否合法调用都会返回该对象
 */
open class Conclusion<D>(success: Boolean = true, code: Int = 0, message: String = "", val data: D) :
    Argument(success, code, message) {
    override fun toString(): String {
        return "Conclusion{success=$success,data=$data,code=$code,message=$message}"
    }
}