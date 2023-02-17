package com.blackace.data.state

import com.blackace.data.entity.http.VersionBean


/**
 *
 * @author: magicHeimdall
 * @create: 17/2/2023 10:39 AM
 */
sealed interface UpdateAppState {

    data class NoUpdate(val msg: String) : UpdateAppState

    data class Update(val versionInfo: VersionBean) : UpdateAppState

    data class Downloading(val progress: Int) : UpdateAppState

    data class DownloadFail(val msg: String) : UpdateAppState

    data class Install(val path: String) : UpdateAppState
}
