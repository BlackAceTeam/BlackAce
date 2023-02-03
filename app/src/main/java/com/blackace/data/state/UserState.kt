package com.blackace.data.state

import com.blackace.data.entity.UserBean

/**
 *
 * @author: magicHeimdall
 * @create: 18/12/2022 9:06 下午
 */
sealed interface UserState {

    object NoLogin : UserState

    data class Login(val bean: UserBean):UserState

}
