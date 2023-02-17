package com.blackace.data.entity.http

/**
 *
 * @author: magicHeimdall
 * @create: 19/12/2022 10:36 AM
 */
data class LoginBean(
    val account: String,
    val token: String,
    val email:String,
    val registerTime:Long
)
