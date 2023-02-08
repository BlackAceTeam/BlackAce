package com.blackace.data.state

/**
 *
 * @author: magicHeimdall
 * @create: 7/2/2023 5:08 PM
 */
sealed interface ChangePassState {

    object Loading : ChangePassState

    data class Success(val password: String) : ChangePassState

    data class Fail(val msg: String) : ChangePassState
}
