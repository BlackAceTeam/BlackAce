package com.blackace.data.entity.http

/**
 *
 * @author: magicHeimdall
 * @create: 19/12/2022 10:33 AM
 */
data class RegisterBean(
    val account:String,
    val id:String,
    val token:String,
    val email:String,
    val registerTime:Long
)
