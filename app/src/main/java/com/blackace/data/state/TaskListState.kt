package com.blackace.data.state

import com.blackace.data.entity.http.TaskBean

/**
 *
 * @author: magicHeimdall
 * @create: 4/1/2023 9:19 PM
 */
sealed interface TaskListState {

    data class FirstSuccess(val list: List<TaskBean>) : TaskListState

    data class FirstFail(val msg: String):TaskListState

    data class MoreSuccess(val list: List<TaskBean>) : TaskListState

    data class MoreFail(val msg: String):TaskListState

    data class StateUpdate(val list: List<TaskBean>):TaskListState

    data class SimpleFail(val msg: String):TaskListState

    data class Delete(val position:Int):TaskListState
}
