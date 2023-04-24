package com.blackace.data.state

/**
 *
 * @author: magicHeimdall
 * @create: 24/4/2023 2:57 PM
 */
sealed interface SimpleActionState<T> {

    class Loading<T> : SimpleActionState<T>

    data class Fail<T>(val msg: String) : SimpleActionState<T>

    data class Success<T>(val bean: T) : SimpleActionState<T>
}
