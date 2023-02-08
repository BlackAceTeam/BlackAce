package com.blackace.data.state

/**
 *
 * @author: magicHeimdall
 * @create: 7/2/2023 9:27 PM
 */
sealed interface SendEmailState {

    object Loading : SendEmailState

    object Ready : SendEmailState

    data class Success(val email: String) : SendEmailState

    data class Fail(val msg: String) : SendEmailState

    data class Wait(val second: Int) : SendEmailState

}
