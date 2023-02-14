package com.blackace.data.state

/**
 *
 * @author: magicHeimdall
 * @create: 13/2/2023 10:22 PM
 */
sealed interface InstallState {

    object Loading : InstallState

    data class NeedUnInstall(val pkg: String, val path: String,val appName:String) : InstallState

    data class InstallFail(val error: String, val path: String) : InstallState

    data class UninstallSuccess(val pkg: String, val path: String,val appName:String) : InstallState
}
