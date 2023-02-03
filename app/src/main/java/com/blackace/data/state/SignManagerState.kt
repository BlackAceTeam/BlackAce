package com.blackace.data.state

import com.blackace.data.entity.db.SignBean

/**
 *
 * @author: magicHeimdall
 * @create: 5/1/2023 8:31 PM
 */
sealed interface SignManagerState {

    object Loading : SignManagerState

    data class Delete(val position: Int) : SignManagerState

    data class CreateSuccess(val bean: SignBean) : SignManagerState

    data class CreateFail(val msg: String) : SignManagerState
}
