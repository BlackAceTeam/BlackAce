package com.blackace.data.state

import com.blackace.data.entity.db.SignBean
import com.blackace.data.entity.http.TaskBean

/**
 *
 * @author: magicHeimdall
 * @create: 6/1/2023 11:12 AM
 */
sealed interface PackageState {

    object Loading : PackageState

    data class Fail(val msg: String) : PackageState

    data class LoadSignSuccess(val model: TaskBean, val list: List<SignBean>) : PackageState

    data class Success(val pkg: String, val path: String, val appName: String) : PackageState
}
