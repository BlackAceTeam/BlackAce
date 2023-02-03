package com.blackace.util.holder

import com.google.gson.Gson

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16-下午2:59
 */
object GsonHolder {
    val gson by lazy { Gson() }

    fun toJson(any: Any): String {
        return gson.toJson(any)
    }

    inline fun <reified T : Any> fromJson(json:String): T {
        return gson.fromJson(json,T::class.java)
    }
}
