package com.blackace.data.entity.http

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 11:10 PM
 */
open class BaseResult<T> {
    var code: Int = 0
    var result: T? = null
    var msg: String = ""
    var status: Int = 0

    fun isSuccess(): Boolean {
        return status == 1
    }

    companion object {
        fun <T> fail(msg: String): BaseResult<T> {
            val result = BaseResult<T>()
            result.status = 0
            result.msg = msg
            return result
        }

        fun <T> fail(t: Throwable): BaseResult<T> {
            t.printStackTrace()
            val result = BaseResult<T>()
            result.status = 0
            result.msg = t.message.toString()
            return result
        }
    }
}
