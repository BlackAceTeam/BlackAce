package com.blackace.data.state

/**
 *
 * @author: magicHeimdall
 * @create: 7/2/2023 5:08 PM
 */
sealed interface ChangePassState {

    object Loading : ChangePassState

    object Success : ChangePassState

    data class Fail(val msg: String) : ChangePassState
}
