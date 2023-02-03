package com.blackace.data.state


/**
 *
 * @author: magicHeimdall
 * @create: 19/12/2022 3:49 PM
 */
sealed interface CreateTaskState {
    object Loading : CreateTaskState

    object Success : CreateTaskState
//    object Success(val taskNo: String, val downUrl: String) : CreateTaskState

    data class Fail(val msg: String) : CreateTaskState
}
