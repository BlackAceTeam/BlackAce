package com.blackace.data.entity.http

/**
 *
 * @author: magicHeimdall
 * @create: 17/2/2023 10:42 AM
 */
data class VersionBean(
    var versionName: String?,
    var version: Int,
    var url: String?,
    var content: String?,
    var type: Int
) {
    fun isForceUpdate(): Boolean {
        return type == 2
    }
}
