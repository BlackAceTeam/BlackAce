package com.blackace.util.holder

import android.annotation.SuppressLint
import android.content.Context

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/14-下午7:13
 */
@SuppressLint("StaticFieldLeak")
object ContextHolder {

    private lateinit var context: Context

    fun init(context: Context){
        ContextHolder.context = context
    }

    fun get():Context{
        return context
    }
}
