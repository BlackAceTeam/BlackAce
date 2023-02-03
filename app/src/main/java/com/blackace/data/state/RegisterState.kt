package com.blackace.data.state

import com.blackace.data.entity.UserBean

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 11:19 PM
 */
sealed interface RegisterState {

    object Loading : RegisterState

    data class Fail(val msg: String) : RegisterState

    object Success : RegisterState
}
