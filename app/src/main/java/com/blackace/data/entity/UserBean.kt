package com.blackace.data.entity

/**
 *
 * @author: magicHeimdall
 * @create: 2022/12/16-下午2:56
 */
data class UserBean(
    val name:String,
    val email:String,
    val token:String = "",
    val registerTime:Long
)
