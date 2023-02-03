package com.blackace.data.entity.http

/**
 *
 * @author: magicHeimdall
 * @create: 4/1/2023 9:28 PM
 */
class MoreBean<T> : BaseResult<T>() {

    var more: Int = 0

    var start: String = ""

    companion object {
        fun <T> fail(msg: String): MoreBean<T> {
            val result = MoreBean<T>()
            result.status = 0
            result.msg = msg
            return result
        }

        fun <T> fail(t: Throwable): MoreBean<T> {
            val result = MoreBean<T>()
            result.status = 0
            result.msg = t.message.toString()
            return result
        }
    }
}

