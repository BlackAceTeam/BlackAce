package com.blackace.data.state

import com.blackace.data.entity.db.SignBean

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 2:45 PM
 */
sealed interface SignListState {

    object Loading : SignListState

    data class Success(val list: List<SignBean>) : SignListState
}
